package com.subtitlor.dao;

import java.util.List;
import java.util.Optional;

import com.subtitlor.beans.Subtitle;
import com.subtitlor.beans.SrtFile;

/**
 * This interface should:
 * <li> be related to a SRT file </li>
 * <li> find and return a sequence by its index </li>
 * <li> update a sequence text </li>
 * <li> return a sublist of sequences </li>
 * <li> return its parent SRT file </li>
 * <li> return the number of sequences in its parent SRT file </li>
 */
public interface SequenceDAO {
	
	public Optional<Subtitle> getSequence(String id);
	public void updateSubtitle(String id, String[] lines);
	public List<Subtitle> getList(String startId, String stopId);
	
	public Optional<SrtFile> getParentSrtFile();
	public int getListCount();

}
