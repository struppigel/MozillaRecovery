package model;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class TripleDES {
	private KeySpec keySpec;
	private SecretKey key;
	private IvParameterSpec iv;

	public TripleDES(byte[] keyBytes, byte[] ivString) {
		try {
			keySpec = new DESedeKeySpec(keyBytes);
			key = SecretKeyFactory.getInstance("DESede")
					.generateSecret(keySpec);
			iv = new IvParameterSpec(ivString);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException
				| InvalidKeyException e) {
			e.printStackTrace();
		}

	}

	public byte[] encrypt(byte[] text) {
		if (text != null) {
			try {
				Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding",
						"SunJCE");
				cipher.init(Cipher.ENCRYPT_MODE, key, iv);
				return cipher.doFinal(text);
			} catch (IllegalBlockSizeException | InvalidKeyException
					| InvalidAlgorithmParameterException
					| NoSuchAlgorithmException | NoSuchProviderException
					| NoSuchPaddingException | BadPaddingException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public String decrypt(byte[] text) throws BadPaddingException {
		if (text != null) {
			try {
				Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding",
						"SunJCE");
				cipher.init(Cipher.DECRYPT_MODE, key, iv);
				byte[] result = cipher.doFinal(text);
				return new String(result, "UTF8");
			} catch (NoSuchAlgorithmException | NoSuchProviderException
					| NoSuchPaddingException | IllegalBlockSizeException
					| InvalidKeyException | InvalidAlgorithmParameterException
					| UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
