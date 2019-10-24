# Dinigma
Dinigma is an OpenPGP api for Java. It mirrors Minigmand, which is an Android OpenPGP api.

Both of them use the BouncyCastle OpenPGP implementation - but there are some variations:

Minigmand: uses Android logging, "spongycastle" instead of bouncycastle, and the Android Base64 encoders

Dinigma: uses Log4J logging, original bouncycastle, and Java Base64 encoders.

Otherwise they should be exactly the same (but probably aren't, because - fork maintenance is difficult)

Use Dinigma on servers and Minigma on Android devices. 

