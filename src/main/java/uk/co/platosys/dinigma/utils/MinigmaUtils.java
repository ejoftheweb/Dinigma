package uk.co.platosys.dinigma.utils;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import uk.co.platosys.dinigma.Minigma;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

//import org.bouncycastle.util.encoders.Base64;

public class MinigmaUtils {

	private static PGPCompressedDataGenerator compressor = new PGPCompressedDataGenerator(Minigma.COMPRESS_ALGORITHM);
	private static PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
	private static Base64.Encoder encoder= Base64.getUrlEncoder();
	private static Base64.Decoder decoder=Base64.getUrlDecoder();
	//static final int B64 =(Base64.URL_SAFE+ Base64.NO_WRAP);
	private static Logger log = LogManager.getRootLogger();
	private static Marker mark = MarkerManager.getMarker("MinigmaUtils");
	/** turns a byte array into a Base-64 encoded string **/
	public static String encode(byte[] bytes){
		return encoder.encodeToString(bytes);
	}
	/** turns a base-64 encoded string into a byte array */
	public static byte[] decode(String string){
		return decoder.decode(string);
	}
	 /**converts an org.jdom2.Document into an array of bytes**/
	 public static byte[] toByteArray(String string){
		return string.getBytes(StandardCharsets.UTF_8);
	 }
	 /**converts an array of bytes into a String*/
	 public static String fromByteArray(byte[] asBytes){
		 return new String(asBytes, StandardCharsets.UTF_8);
	 }
	 /**compresses a byte array of clear data
	  * @param clearData a byte-array of clear, uncompressed data
	  * @return a byte-array of clear, compressed data
	  * */
	 public static byte[] compress(byte[] clearData){
		 printBytes(clearData);
		try {	
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			OutputStream compressedOut = compressor.open(byteOut);
			OutputStream  literalOut = literalDataGenerator.open(compressedOut,
	                											 PGPLiteralData.BINARY,
	                											"compressed",
	                											 clearData.length, 
	                											 new Date());
	
			literalOut.write(clearData);
			literalOut.close();
			
			byte[] bytesOut = byteOut.toByteArray();
			printBytes(bytesOut);
			return bytesOut;
		}catch (IOException e){
			// TODO Auto-generated catch block
			log.debug(mark,"error:",e);
			return null;
		}
		 
 }
	 public static void printBytes (byte[] bytes){
		 for (byte byt:bytes){
			 System.out.print(byt);
		 }
		 System.out.print("\n");
	 }
}
