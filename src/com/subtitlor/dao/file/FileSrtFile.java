package com.subtitlor.dao.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.subtitlor.dao.SrtFileDAO;
import com.subtitlor.beans.Subtitle;
import com.subtitlor.beans.SrtFile;
import com.subtitlor.utilities.FileManager;

public class FileSrtFile implements SrtFileDAO {
	
	public Optional<SrtFile> find(String filename) {
		// Try to open file with filename
		List<String> lines;
		try {
			lines = FileManager.readFile(filename);
		}
		catch (IOException e) {
			System.out.println("findBy error: " + e.getMessage());
			return Optional.empty();
		}

		// Prepare sequences list
		List<Subtitle> sequences = new ArrayList<Subtitle>();
		
		// Prepare list of lines
		List<String> buf = new ArrayList<String>(); 
		// Read and parse data into sequence
		for (String line : lines) {
			// Read and store lines until a blank line
			if (!line.trim().equals("")) {
				buf.add(line);
			}
			else {
				if (buf.size() > 2) {
					int index = Integer.parseInt(buf.get(0));
					// Compare first line and number of sequence 
					if (sequences.size() + 1 != index) {
						System.out.println("Wrong index order: " + index + " != " + sequences.size() + 1 );
					}
					// Store start and stop time separated by " -- > "
					String[] times = buf.get(1).split("-->");
					if (times.length == 2) {
						String startTime  = times[0].trim();
						String stopTime = times[1].trim();
						// Store other lines in text
						List<String> text = buf.subList(2, buf.size());
						// Create sequence and add it to list
						Subtitle seq = new Subtitle(index, startTime, stopTime, text);
						sequences.add(seq);
					}
					else {
						System.out.println("Wrong sequence: " + buf.toArray());
					}
				}
				buf.clear();
			}
		}
		
		SrtFile srtFile = new SrtFile(filename, sequences);
		return Optional.of(srtFile);
	}
	
	public void add(SrtFile srtFile) {
		String filename = srtFile.getName();
		String text = "";
		for (Subtitle seq : srtFile.getSequences()) {
			text += seq.getRawText() + "\n\n";
		}
		
		try {
			FileManager.writeFile(text, filename);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void update(SrtFile srtFile) {
		// Try to remove file with name id - optional
		// Call add method with this srtFile
		add(srtFile);
	}

	public void delete(String filename) {
		try {
			FileManager.deleteFile(filename);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> list() {
		return FileManager.listOfFiles();
	}
	
	public SrtFile createEmptyFileFrom(SrtFile template, String filename) {
		
		List<Subtitle> sequences = template.getSequences();
		List<Subtitle> empty = new ArrayList<Subtitle>();
		for (Subtitle seq : sequences) {
			String[] lines = new String[seq.getLines().length];
			empty.add(new Subtitle(
					seq.getIndex(), 
					seq.getStartTime(), 
					seq.getStopTime(), 
					lines));
		}
		return new SrtFile(filename, empty);
	}
}
