package model;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

public class SHA {
	
	private final static Logger logger = Logger
			.getLogger(SHA.class);
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	private static final String SHA1_ALGORITHM = "SHA-1";

	public static byte[] sha1Hmac(byte[] data, byte[] key) {
		try {
			SecretKeySpec signingKey = new SecretKeySpec(key,
					HMAC_SHA1_ALGORITHM);
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);
			return mac.doFinal(data);
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
			logger.fatal(e.getMessage());
		} 
		return null;

	}
	
	public static byte[] sha1(byte[] text) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(SHA1_ALGORITHM);
		md.update(text, 0, text.length);
		return md.digest();
	}
}