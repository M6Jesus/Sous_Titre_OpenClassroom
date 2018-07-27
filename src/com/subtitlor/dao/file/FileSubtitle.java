package com.subtitlor.dao.file;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.subtitlor.beans.Subtitle;
import com.subtitlor.beans.SrtFile;
import com.subtitlor.dao.SequenceDAO;

public class FileSubtitle implements SequenceDAO {
	private Optional<SrtFile> srtFile;

	FileSubtitle(String id) {
		FileSrtFile tmpDAO = new FileSrtFile();
		this.srtFile = tmpDAO.find(id);
	}

	public Optional<Subtitle> getSequence(String id) {
		if (!srtFile.isPresent()) {
			return Optional.empty();
		}

		if (id.isEmpty()) {
			return Optional.empty();
		}
		int index = Integer.parseInt(id) - 1;
		List<Subtitle> sequences = srtFile.get().getSequences();

		if ((index >= 0) && (index < sequences.size())) {
			return Optional.of(sequences.get(index));
		}
		return Optional.empty();
	}

	public void updateSubtitle(String id, String[] lines) {
		if (!srtFile.isPresent()) {
			return;
		}

		if (id.isEmpty()) {
			return;
		}
		Optional<Subtitle> seq = this.getSequence(id);
		if (seq.isPresent()) {
			seq.get().setLines(lines);
		}
	}

	public Optional<SrtFile> getParentSrtFile() {
		return srtFile;
	}

	public List<Subtitle> getList(String startId, String stopId) {
		if (!srtFile.isPresent()) {
			return new ArrayList<Subtitle>();
		}

		if (startId.isEmpty() || stopId.isEmpty()) {
			return new ArrayList<Subtitle>();
		}

		// Start Stop check
		int max = getListCount() - 1;
		int start = 0;
		int stop = max;
		
		try {
			start = Integer.parseInt(startId) - 1;
			stop = Integer.parseInt(stopId) - 1;
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			start = 0;
			stop = max;
		}
		
		if (start < 0) {
			start = 0;
		}
		if (stop > max) {
			stop = max;
		}
		if (start > stop) {
			int tmp = start;
			start = stop;
			stop = tmp;
		}

		// Sublist
		List<Subtitle> sequences = srtFile.get().getSequences();
		return sequences.subList(start, stop);
	}

	public int getListCount() {
		if (!srtFile.isPresent()) {
			return 0;
		}
		return srtFile.get().getSequences().size();
	}

}
