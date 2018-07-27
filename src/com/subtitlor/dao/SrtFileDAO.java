package com.subtitlor.dao;

import java.util.List;
import java.util.Optional;

import com.subtitlor.beans.SrtFile;

/**
 * This interface should:
 * <li> find a SRT file by name </li>
 * <li> add a new SRT file </li>
 * <li> update a prevously added file </li>
 * <li> delete a file with its name </li>
 * <li> list all referenced files </li>
 * <li> create an empty SRT file from a previously added file </li>
 */
public interface SrtFileDAO {
	
	public Optional<SrtFile> find(String filename);
	public void add(SrtFile entry);
	public void update(SrtFile entry);
	public void delete(String filename);
	public List<String> list();
	public SrtFile createEmptyFileFrom(SrtFile template, String filename);
}
