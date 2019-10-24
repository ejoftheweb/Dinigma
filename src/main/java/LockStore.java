/*
 * Created on Jan 30, 2006
 * (c) copyright 2006 YouSayWho.com Ltd
 * Released under the YouSayWho Charter Licence.
 * You may use or modify this code for most purposes, i
 * including commercial purposes, provided that you
 * do not (unless compelled to do so by law or torture)
 * disclose any personal information obtained  with this software 
 * or any software derived from it without the consent of the 
 * data subject.
 * 
 * 
 *  
 * 
 * LockStore is a convenient class for handling Locks. 
 * 
 * 
 */


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.operator.KeyFingerPrintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import uk.co.platosys.dinigma.exceptions.MinigmaException;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * @author edward
 * The LockStore class wraps PGP public keyrrings
 *
 *
 */
public class LockStore {
	private static Logger log = LogManager.getRootLogger();
private static Marker mark = MarkerManager.getMarker("CryptoEngine");
	private PGPPublicKeyRingCollection keyRings;
	private PGPPublicKeyRing pgpPublicKeyRing;
	private File file;
	private long storeId;
	
	public LockStore (File file){
		this.file=file;
		if (file.exists()&&file.canRead()){
			if (!load()){
				log.debug(mark, "LockStore-init failed at loading");
			}

	   	
		}else{
			log.debug(mark,  "LockStore-init: file doesn't exist");
		}
  }
	
	public boolean load(){
		try {
		   InputStream keyIn = new ArmoredInputStream(new FileInputStream(file));
		   KeyFingerPrintCalculator calculator = new JcaKeyFingerprintCalculator();
		   keyRings=new PGPPublicKeyRingCollection(keyIn, calculator);
		   PGPPublicKey publicKey = null;
		   Iterator<PGPPublicKeyRing> ringIterator = keyRings.getKeyRings();
		   while (ringIterator.hasNext() && publicKey==null){
		   	PGPPublicKeyRing thisKeyRing=(PGPPublicKeyRing)ringIterator.next();
		   	Iterator<PGPPublicKey> keyIterator = thisKeyRing.getPublicKeys();
		   	while(keyIterator.hasNext() && publicKey==null){
		   		PGPPublicKey testKey = (PGPPublicKey)keyIterator.next();
		   		if (testKey.isEncryptionKey()){
		   			publicKey=testKey;
		   			pgpPublicKeyRing=thisKeyRing;
		   			
		   		}
		   	}
		   	this.storeId=publicKey.getKeyID();
		   }
			
		   //encryptionLock=new Lock(publicKey);
		   return true;
    	}catch(Exception e){
    		log.debug(mark, "LockStore-load failed",e);
    		return false;
     	}
	}
	public boolean save(){
		  	try {
	    		OutputStream outStream = new ArmoredOutputStream(new FileOutputStream(file));
	      	keyRings.encode(outStream);
	      	outStream.close();
	    		return true;
	    	}catch(Exception e){
	    		log.debug(mark, "LockStore-save: failed", e);
	    		return false;
	    	}
    }
	 
	public boolean saveAs(File file){
	 	this.file=file;
	 	return save();
	}
	 
	public boolean addLock(Lock lock){
	 	 try {
	 	 	if (keyRings==null){
	 	 		load();
	 	 	}
	 	 	Iterator<PGPPublicKeyRing> it = lock.getKeys();

	 	 	while (it.hasNext()){
	 	 		PGPPublicKeyRing publicKey =  it.next();
	 	 		keyRings = PGPPublicKeyRingCollection.addPublicKeyRing(keyRings, publicKey);
	 	 	}
	 	 	return save();
	 	 }catch(Exception e){
	 	 	log.debug(mark, "LockStore-addLock: failed", e);
	 	 	return false;
	 	 }
	 }
	
	/** @param keyID
	 * @return a lock with this keyID */
	 public Lock getLock(long keyID){
	 	try{
	 		PGPPublicKeyRing keyRing = keyRings.getPublicKeyRing(keyID);
	 		Collection<PGPPublicKeyRing> collection = new ArrayList<>();
	 		collection.add(keyRing);
	 		PGPPublicKeyRingCollection keyRingCollection = new PGPPublicKeyRingCollection(collection);
	 		return new Lock(keyRingCollection);
	 	}catch(Exception e){
	 		log.debug(mark, "LockStore-getLock(ID) error ", e);
	 		return null;
	 	}
	 }
	 public Iterator<Lock> iterator() throws MinigmaException{
		 List<Lock> list = new ArrayList<>();
		 try{
			 Iterator<PGPPublicKeyRing> kringit = keyRings.getKeyRings();
			 while(kringit.hasNext()){
				Collection<PGPPublicKeyRing> collection = new ArrayList<>();
		 		collection.add(kringit.next());
		 		PGPPublicKeyRingCollection keyRingCollection = new PGPPublicKeyRingCollection(collection);
		 		list.add(new Lock(keyRingCollection));
			 }
		 }catch(Exception e){
			 throw new MinigmaException("problem creating lockstore iterator");
		 }
		 return list.iterator();
	 }
	 /** returns */
	 public Lock getLock(String userID)throws MinigmaException{
	 	try{
	 		PGPPublicKeyRingCollection keyRingCollection=null;
	 		Iterator<PGPPublicKeyRing> itr = keyRings.getKeyRings(userID, true);
	 		while(itr.hasNext() ){
	 		 	PGPPublicKeyRing publicKeyRing=itr.next();
	 		 	if (keyRingCollection==null){
	 		 		Collection<PGPPublicKeyRing> collection = new ArrayList<>();
	 		 		collection.add(publicKeyRing);
	 		 		keyRingCollection=new PGPPublicKeyRingCollection(collection);
	 		 	}else{
	 		 		keyRingCollection=PGPPublicKeyRingCollection.addPublicKeyRing(keyRingCollection,publicKeyRing);
	 		 	}
	 		}
	 		return new Lock(keyRingCollection);
	 	}catch(Exception e){
	 		throw new MinigmaException("error getting lock for userID "+userID, e);
	 	}
	 }
	/**
	  * 
	  */
	  public long getStoreId(){
		  return storeId;
	  }

}

