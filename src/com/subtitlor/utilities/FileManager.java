package com.subtitlor.utilities;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.Files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.Part;

import org.mozilla.universalchardet.UniversalDetector;

import com.subtitlor.utilities.ContextHandler;

public class FileManager {
	private static final int BUFFER_SIZE = 10240;
	private static final String FILE_FOLDER = "/srt/";
	public static final String EXTENSION = ".srt";

	/**
	 * Upload a file from a Part to the server. Inspired by method "doPost" in Test.java of chapter "Envoyer des fichiers".
	 * @param part Part obtained by a HttpServletRequest.getPart() call.
	 * @return name of uploaded file without its extension
	 * @throws IOException
	 */
	public static String downloadFrom(Part part) throws IOException {
		String filename = getFilenameFrom(part);
		
		if (filename != null && !filename.isEmpty()) {
			filename = filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1);
			try {
				String path = getRealPath();
				System.out.println("Download new srt file : " + filename + " in " + path);
				
				writeFile(part, filename, path);
			}
			catch(IOException error) {
				throw error;
			}
		}
		
		System.out.println("List of files after download: " + FileManager.listOfFiles());
		return Files.getNameWithoutExtension(filename);
	}
	
	/**
	 * @return Real path string of /srt folder in application context
	 */
	private static String getRealPath() {
		return ContextHandler.getContext().getRealPath(FILE_FOLDER);
	}

	/**
	 * Copied from "getNomFichier" in Test.java of chapter "Envoyer des fichiers".
	 * url =
	 * https://openclassrooms.com/courses/developpez-des-sites-web-avec-java-ee/envoyer-des-fichiers
	 */
	private static String getFilenameFrom(Part part) {
		for (String contentDisposition : part.getHeader("content-disposition").split(";")) {
			if (contentDisposition.trim().startsWith("filename")) {
				return contentDisposition.substring(contentDisposition.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}

	/**
	 * Copied from "ecrireFichier" in Test.java of chapter "Envoyer des fichiers"
	 * url =
	 * https://openclassrooms.com/courses/developpez-des-sites-web-avec-java-ee/envoyer-des-fichiers
	 */
	private static void writeFile(Part part, String filename, String path) throws IOException {
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		try {
			input = new BufferedInputStream(part.getInputStream(), BUFFER_SIZE);
			output = new BufferedOutputStream(new FileOutputStream(new File(path + filename)), BUFFER_SIZE);

			byte[] tampon = new byte[BUFFER_SIZE];
			int length;
			while ((length = input.read(tampon)) > 0) {
				output.write(tampon, 0, length);
			}
		}
		catch (IOException error) {
			throw error;
		}
		finally {
			try {
				output.close();
			}
			catch (IOException ignore) {
				throw ignore;
			}
		}
	}
	
	/**
	 * Method to easily write a '.srt' file.
	 * @param text String to write in file
	 * @param filename name of file to write without '.srt' extension
	 * @throws IOException
	 */
	public static void writeFile(String text, String filename) throws IOException {
	    File file = new File(getRealPath() + filename + EXTENSION);
	    CharSink sink = Files.asCharSink(file, Charsets.UTF_8);
	    sink.write(text);
	}
	
	/**
	 * Method to easily read a '.srt' file line by line.
	 * @param filename name of file to read without '.srt' extension
	 * @return a list of strings corresponding to each line in file
	 * @throws IOException
	 */
	public static List<String> readFile(String filename) throws IOException {
		List<String> strings = new ArrayList<String>();
		File file = new File(getRealPath() + filename + EXTENSION);
		Charset guessCharset = guessCharset(file);
		strings = Files.readLines(file, guessCharset);
		// To be sure to read last sequence, add a blank line
		strings.add("\n");
	    return strings;
	}
	
	/**
	 * Method to easily delete a '.srt' file.
	 * @param filename name of file to delete without '.srt' extension
	 * @throws IOException
	 */
	public static void deleteFile(String filename) throws IOException {
		File file = new File(getRealPath() + filename + EXTENSION);
		java.nio.file.Files.deleteIfExists(file.toPath());
	}
	
	/**
	 * Try to guess charset of a text file by analysis its content using an UniversalDetector from org.mozilla.universalchardet.
	 * @param file to analysis
	 * @return guessed charset
	 * @throws IOException
	 */
	private static Charset guessCharset(File file) throws IOException {
		byte[] data = Files.toByteArray(file);
		
		UniversalDetector universalDetector = new UniversalDetector(null);
	    universalDetector.handleData(data, 0, data.length);
	    universalDetector.dataEnd();
	    
	    String encodingName = universalDetector.getDetectedCharset();
	    // If encoding name is null, change it to default value UTF-8
	    encodingName = encodingName != null ? encodingName : "UTF-8";
		
		return Charset.forName(encodingName);
	}
	
	/**
	 * @return list of files in ~/srt folder without their '.srt' extension.
	 */
	public static List<String> listOfFiles() {
		File folder = new File(getRealPath());
		File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(EXTENSION));
		
		if (files.length > 0) {
			return Arrays.asList(files).stream()
				.map(f -> Files.getNameWithoutExtension(f.getName()))
				.collect(Collectors.toList());
		}
		return new ArrayList<String>() ;
	}
	
	/**
	 * @param filename
	 * @return real path of file with name 'filename'.
	 */
	public static String getPathFor(String filename) {
		return getRealPath() + filename + EXTENSION;
	}
}
