package com.subtitlor.beans;

import java.util.List;

import com.subtitlor.beans.Subtitle;
import com.subtitlor.utilities.SupportedLanguage;

public class SrtFile {
	private String name;
	private List<Subtitle> sequences;
	private SupportedLanguage language;
	
	public SrtFile(String name, List<Subtitle> sequences) {
		this.name = name;
		this.sequences = sequences;
		this.language = SupportedLanguage.getLanguage(name);
	}
	
	public SrtFile(String name, List<Subtitle> sequences, SupportedLanguage language) {
		this.name = name;
		this.sequences = sequences;
		this.language = language;
	}

	public String getName() {
		return name;
	}

	public List<Subtitle> getSequences() {
		return sequences;
	}
	
	public SupportedLanguage getLanguage() {
		return language;
	}

}
