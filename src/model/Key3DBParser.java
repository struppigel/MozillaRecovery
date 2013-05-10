package model;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;

import org.apache.log4j.Logger;

public class Key3DBParser {

	private static final byte[] VERSION_STR = "Version".getBytes();
	private static final byte[] GLOBAL_SALT_STR = "global-salt".getBytes();
	private static final byte[] PASSWORD_CHECK_STR = "password-check"
			.getBytes();
	private final static Logger logger = Logger.getLogger(Key3DBParser.class);
	public static String NEWLINE = System.getProperty("line.separator");

	@SuppressWarnings("unused")
	// TODO use for decrypting signons.sqlite
	private static final byte[] DEFAULT_SIGNONS_PASSWORD = Key3DBParser
			.convertHextoByte("f8000000000000000000000000000001");

	private static final int ENC_PASSWORD_CHECK_LENGTH = 16;
	private static final int GLOBAL_KEY_LENGTH = 20;

	private final byte[] key3Bytes;
	private byte[] globalSalt;
	private Integer globalSaltIndex;
	private byte[] encPasswordCheck;
	private byte[] entrySalt;

	public Key3DBParser(String key3Path) throws IOException {
		key3Bytes = FileIO.getBytesFromFile(new File(key3Path));
	}

	public String parse() {
		StringBuilder b = new StringBuilder();
		b.append("Version: " + parseVersion() + NEWLINE);
		b.append("Global salt: " + parseGlobalSalt() + NEWLINE);
		b.append("Entry salt: " + parseEntrySalt() + NEWLINE);
		b.append("Encrypted PasswordCheck: " + parseEncryptedPasswordCheck()
				+ NEWLINE);
		return b.toString();
	}

	public boolean isPassword(String masterPassword) {
		String result = performPasswordCheck(masterPassword);
		if (result == null) {
			return false;
		}
		return result.equals(new String(PASSWORD_CHECK_STR));
	}

	private String performPasswordCheck(String masterPassword) {
		if (entrySalt != null && encPasswordCheck != null) {
			try {
				return decryptPasswordCheck(masterPassword.getBytes("utf-8"),
						entrySalt, globalSalt, encPasswordCheck);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		return "entry-salt or password-check not found";
	}

	private String parseVersion() {
		Integer index = indexOf(VERSION_STR, key3Bytes);
		if (index != null) {
			return String.valueOf(key3Bytes[index - 1]);
		}
		return "No Version found";
	}

	/**
	 * This is tested and works well.
	 * 
	 * @param password
	 * @param es
	 * @param gs
	 * @param text
	 * @return result of decryption
	 */
	private static String decryptPasswordCheck(byte[] password, byte[] es,
			byte[] gs, byte[] text) {
		try {
			// HP = SHA1(global-salt||password)
			byte[] hp = SHA.sha1(appendArray(gs, password));
			logger.debug("HP: " + convertByteToHex(hp));
			byte[] pes = Arrays.copyOf(es, 20);
			logger.debug("PES: " + convertByteToHex(pes));
			// CHP = SHA1(HP||ES)
			byte[] chp = SHA.sha1(appendArray(hp, es));
			logger.debug("CHP: " + convertByteToHex(chp));
			// k1 = CHMAC(PES||ES)
			byte[] k1 = SHA.sha1Hmac(appendArray(pes, es), chp);
			logger.debug("k1: " + convertByteToHex(k1));
			// tk = CHMAC(PES)
			byte[] tk = SHA.sha1Hmac(pes, chp);
			logger.debug("tk: " + convertByteToHex(tk));
			// k2 = CHMAC(tk||ES)
			byte[] k2 = SHA.sha1Hmac(appendArray(tk, es), chp);
			logger.debug("k2: " + convertByteToHex(k2));
			// k = k1||k2
			byte[] k = appendArray(k1, k2);
			byte[] desKey = Arrays.copyOf(k, 24);
			logger.debug("key: " + convertByteToHex(desKey));
			byte[] desIV = Arrays.copyOfRange(k, k.length - 8, k.length);
			logger.debug("iv: " + convertByteToHex(desIV) + "\n");

			return new TripleDES(desKey, desIV).decrypt(text);

		} catch (NoSuchAlgorithmException e) {
			logger.fatal(e.getMessage());
			e.printStackTrace();
		} catch (BadPaddingException e) {
			logger.debug(e.getMessage() + ". Probably wrong key.");
		}
		return null;
	}

	private String parseEncryptedPasswordCheck() {
		Integer index = indexOf(PASSWORD_CHECK_STR, key3Bytes);
		if (index != null) {
			int from = index - ENC_PASSWORD_CHECK_LENGTH;
			int to = index;
			encPasswordCheck = Arrays.copyOfRange(key3Bytes, from, to);
			return convertByteToHex(encPasswordCheck);
		}
		return null;
	}

	private String parseEntrySalt() {
		if (globalSaltIndex != null) {
			int saltLength = key3Bytes[globalSaltIndex + GLOBAL_SALT_STR.length
					+ 1];
			logger.debug("Salt length: " + saltLength);
			int from = globalSaltIndex + GLOBAL_SALT_STR.length + 3;
			int to = from + saltLength;
			entrySalt = Arrays.copyOfRange(key3Bytes, from, to);
			return convertByteToHex(entrySalt);
		}
		return null;
	}

	private String parseGlobalSalt() {
		globalSaltIndex = indexOf(GLOBAL_SALT_STR, key3Bytes);
		if (globalSaltIndex != null) {
			int from = globalSaltIndex - GLOBAL_KEY_LENGTH;
			int to = globalSaltIndex;
			globalSalt = Arrays.copyOfRange(key3Bytes, from, to);
			return convertByteToHex(globalSalt);
		}
		return "No global-salt found";
	}

	private static Integer indexOf(byte[] subarray, byte[] array) {

		if (subarray.length > array.length) {
			return null;
		}

		for (int i = 0; i < array.length; i++) {
			// possible starting index of subarray?
			if (array[i] == subarray[0]) {
				boolean found = true;
				// test all other indices of subarray
				for (int j = 1; j < subarray.length; j++) {
					if ((i + j) >= array.length || array[i + j] != subarray[j]) {
						found = false;
						break;
					}
				}
				// all other values where equal, so return starting index
				if (found) {
					return i;
				}
			}
		}
		return null;
	}

	/**
	 * Helping method to convert a byte array to a hex String
	 * 
	 * @param array
	 * @return
	 */
	private static String convertByteToHex(byte array[]) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if ((array[i] & 0xff) < 0x10) {
				buffer.append("0");
			}
			buffer.append(Integer.toString(array[i] & 0xff, 16) + " ");
		}
		return buffer.toString();
	}

	private static byte[] appendArray(byte[] arr1, byte[] arr2) {
		byte[] ret = new byte[arr1.length + arr2.length];
		for (int i = 0; i < arr1.length; i++) {
			ret[i] = arr1[i];
		}
		for (int i = 0; i < arr2.length; i++) {
			ret[i + arr1.length] = arr2[i];
		}
		return ret;
	}

	/**
	 * Helping method to convert a hex String to a byte array
	 * 
	 * @param hexString
	 * @return
	 */
	private static byte[] convertHextoByte(String hexString) {
		char[] hex = hexString.toCharArray();
		byte[] result = new byte[hex.length / 2];
		for (int i = 0; i < result.length; i++) {
			result[i] = (byte) ((Character.digit(hex[i * 2], 16) << 4) + Character
					.digit(hex[i * 2 + 1], 16));
		}
		return result;
	}

	@SuppressWarnings("unused")
	private static boolean testDecryption() {
		byte[] password = Key3DBParser.convertHextoByte("70617373776f7264");
		byte[] entrySalt = Key3DBParser
				.convertHextoByte("1596bb8112652a43e7bdfb2fdc8799e5");
		byte[] globalSalt = Key3DBParser
				.convertHextoByte("5aac8e0439e8d69ea0fe1bc013cd5af8");
		byte[] data = Key3DBParser
				.convertHextoByte("c0846848fe6e3524fdd4a6e3e783cf38");
		String result = decryptPasswordCheck(password, entrySalt, globalSalt,
				data);
		System.out.println("result string: " + result);
		return result.equals(new String(PASSWORD_CHECK_STR));
	}

}
