package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

public class FileIO {

	/**
	 * @param file
	 *            file to get the bytes from
	 * @return byte array that represents the given file
	 * @throws IOException
	 */
	public static byte[] getBytesFromFile(File file) throws IOException {
		byte[] data = null;
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			data = new byte[(int) file.length()];
			fileInputStream.read(data);
		}
		return data;
	}

	/**
	 * reads a given textfile and prints it out
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public static List<String> readFile(String filename) throws IOException {
		List<String> lines = new LinkedList<>();
		try (BufferedReader reader = Files.newBufferedReader(
				new File(filename).toPath(), StandardCharsets.UTF_8)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			return lines;
		}
	}

}
