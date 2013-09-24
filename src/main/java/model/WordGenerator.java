package model;

public class WordGenerator {

	private int wordNumber;
	private final int wordlength;
	private final char[] alphabet;
	private final long maxWords;
	private final int radix;

	public WordGenerator(char[] alphabet, int wordlength) {
		this.wordlength = wordlength;
		this.alphabet = alphabet;
		this.maxWords = (long) Math.pow(alphabet.length, wordlength);
		this.radix = alphabet.length;
	}

	public String generateNextWord() {
		if (hasNext()) {
			int[] indices = convertToRadix(wordNumber);
			char[] word = new char[wordlength];
			for (int k = 0; k < wordlength; k++) {
				word[k] = alphabet[indices[k]];
			}
			wordNumber++;
			return new String(word);
		}
		return null;
	}

	public boolean hasNext() {
		return (wordNumber < maxWords);
	}

	private int[] convertToRadix(long number) {
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

	public static char[] initAllowedCharacters(int start, int end) {
		char[] allowedCharacters = new char[end - start + 1];
		for (int i = start; i <= end; i++) {
			allowedCharacters[i - start] = (char) i;
		}
		return allowedCharacters;
	}

}
