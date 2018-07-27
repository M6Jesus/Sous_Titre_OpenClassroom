package com.subtitlor.beans;

import java.util.Arrays;
import java.util.List;

public class Subtitle {
	private int index;
	private String startTime;
	private String stopTime;
	private String[] lines;
	
	public Subtitle(int index, String startTime, String stopTime, List<String> textLines) {
		this.index = index;
		this.startTime = startTime;
		this.stopTime = stopTime;
		lines = new String[textLines.size()];
		lines = textLines.toArray(lines);
	}
	
	public Subtitle(int index, String startTime, String stopTime, String[] lines) {
		this.index = index;
		this.startTime = startTime;
		this.stopTime = stopTime;
		this.lines = lines;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public String getStopTime() {
		return stopTime;
	}
	
	public String[] getLines() {
		return lines;
	}
	
	public void setLines(String[] lines) {
		this.lines = lines;
	}
	
	public String getTimes() {
		return startTime + " --> " + stopTime;
	}
	
	public String getRawText() {
		// Empty text lines are PROHIBITED in srt files -> Blank lines are considered as separators.
		for (int index = 0; index < lines.length; index++){
		    lines[index] = lines[index].isEmpty() ? "." : lines[index];
		}
		
		return index + "\n" + getTimes() + "\n" + String.join("\n", lines);
	}
	
	public Subtitle emptyCopy() {
		String[] empty = new String[lines.length];
		// Empty text lines are PROHIBITED in srt files -> Blank lines are considered as separators.
	    Arrays.fill(empty, "."); 
		return new Subtitle(index,startTime,stopTime, Arrays.asList(empty));
	}
}
