package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import org.apache.log4j.Logger;

import delegate.ProgressDisplay;

public class PasswordCracker {

	private final String key3Path;

	public static final int MAX_WORDLENGTH = 5;
	private static long wordsTried;

	private final static Logger logger = Logger
			.getLogger(PasswordCracker.class);

	private final ProgressDisplay progressDisplay;
	private boolean running;

	public PasswordCracker(String key3Path, ProgressDisplay progressDisplay) {
		this.key3Path = key3Path;
		this.progressDisplay = progressDisplay;
	}

	public String recoverByWordList(String wordlistPath) {
		this.running = true;
		wordsTried = 0;

		try (BufferedReader reader = Files.newBufferedReader(new File(
				wordlistPath).toPath(), StandardCharsets.ISO_8859_1)) {

			Key3DBParser parser = new Key3DBParser(key3Path);
			parser.parse();

			String word = null;
			while ((word = reader.readLine()) != null) {
				handleWordCount();
				if (!running) {
					break;
				}
				if (parser.isPassword(word)) {
					logger.info("found password: " + word);
					return word;
				}
			}

		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public String recoverByBruteforcing(int wordlength) {
		this.running = true;
		wordsTried = 0;
		try {
			Key3DBParser parser = new Key3DBParser(key3Path);
			parser.parse();
			String result = bruteforce(getAlphabet(), wordlength, parser);
			if (result != null) {
				return result;
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public String recoverByBruteforcing() {
		this.running = true;
		wordsTried = 0;
		try {
			Key3DBParser parser = new Key3DBParser(key3Path);
			parser.parse();
			for (int i = 1; i <= MAX_WORDLENGTH; i++) {
				if (!running) {
					break;
				}
				String result = bruteforce(getAlphabet(), i, parser);
				if (result != null) {
					return result;
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public String bruteforce(char[] alphabet, int wordlength,
			Key3DBParser parser) {
		final long MAX_WORDS = (long) Math.pow(alphabet.length, wordlength);
		final int RADIX = alphabet.length;

		for (long i = 0; i < MAX_WORDS; i++) {
			if (!running) {
				break;
			}
			int[] indices = convertToRadix(RADIX, i, wordlength);
			char[] word = new char[wordlength];
			for (int k = 0; k < wordlength; k++) {
				word[k] = alphabet[indices[k]];
			}

			handleWordCount();

			if (parser.isPassword(new String(word))) {
				logger.info("found password: " + new String(word));
				return new String(word);
			}
		}
		return null;
	}

	private void handleWordCount() {
		wordsTried++;
		if (wordsTried % 10000 == 0) {
			progressDisplay.setNewProgress(wordsTried);
			logger.debug("number of passwords tried: " + wordsTried);
		}
	}

	private int[] convertToRadix(int radix, long number, int wordlength) {
		int[] indices = new int[wordlength];
		for (int i = wordlength - 1; i >= 0; i--) {
			if (number > 0) {
				int rest = (int) (number % radix);
				number /= radix;
				indices[i] = rest;
			} else {
				indices[i] = 0;
			}

		}
		return indices;
	}

	private char[] getAlphabet() { // TODO make editable alphabets
		char[] alphabet = new char[26 * 2];
		for (int i = 0; i < 26; i++) {
			alphabet[i] = (char) (i + 97);
			alphabet[i + 26] = (char) (i + 65);
		}
		logger.debug("alphabet used for bruteforcing: "
				+ Arrays.toString(alphabet));
		return alphabet;
	}

	public void setRunning(boolean b) {
		this.running = b;
	}

	public static long getWordsTried() {
		return wordsTried;
	}

}
