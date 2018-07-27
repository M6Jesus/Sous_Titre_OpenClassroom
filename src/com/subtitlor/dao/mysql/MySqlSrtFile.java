package com.subtitlor.dao.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.subtitlor.dao.SequenceDAO;
import com.subtitlor.dao.SrtFileDAO;
import com.subtitlor.dao.mysql.MySqlRequest;
import com.subtitlor.beans.Subtitle;
import com.subtitlor.beans.SrtFile;

public class MySqlSrtFile implements SrtFileDAO {
	private MySqlDAOFactory mySqlFactory;
	
	public MySqlDAOFactory getMySql() {
		return mySqlFactory;
	}

	MySqlSrtFile(MySqlDAOFactory mySqlFactory) {
		this.mySqlFactory = mySqlFactory;
	}
	
	public Optional<SrtFile> find(String filename) {
		try (ResultSet resultSet = MySqlRequest.SELECT_SRT_FILE.execute(filename)) {
			if (resultSet.next()) {
				SequenceDAO seqDao = mySqlFactory.getSequenceDAO(filename);
				int seqCount = seqDao.getListCount();
				if (seqCount > 0) {
					List<Subtitle> sequences = seqDao.getList("1", seqCount + "");
					SrtFile srtFile = new SrtFile(filename, sequences);
					return Optional.of(srtFile);
				}
			}
			MySqlRequest.closeLast();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}
	
	public void add(SrtFile srtFile) {
		String filename = srtFile.getName();
		String language = srtFile.getLanguage().getID();
		
		try (ResultSet resultSet = MySqlRequest.INSERT_SRT_FILE.execute(filename, language)) {
			if (resultSet.next()) {
				String id = resultSet.getString(1);
				
				List<Subtitle> sequences = srtFile.getSequences();
				for (Subtitle sequence : sequences) {
					String index = sequence.getIndex() + "";
					String startTime = sequence.getStartTime();
					String stopTime = sequence.getStopTime();
					String subtitle = String.join("\n", sequence.getLines());
					
					MySqlRequest.INSERT_SEQUENCE.execute(index, id, startTime, stopTime, subtitle);
					MySqlRequest.closeLast();
				}
			}
			MySqlRequest.closeLast();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void update(SrtFile srtFile) {
		try (ResultSet resultSet = MySqlRequest.SELECT_SRT_FILE.execute(srtFile.getName())) {
			if (resultSet.next()) {
				String filename = resultSet.getString("filename");
				SequenceDAO seqDao = mySqlFactory.getSequenceDAO(filename);
				
				List<Subtitle> sequences = srtFile.getSequences();
				for (Subtitle sequence : sequences) {
					String index = sequence.getIndex() + "";
					seqDao.updateSubtitle(index, sequence.getLines());
				}
			}
			MySqlRequest.closeLast();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void delete(String filename) {
		try {
			MySqlRequest.REMOVE_SRT_FILE.execute(filename);
			MySqlRequest.closeLast();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> list() {
		List<String> list = new ArrayList<String>();
		try (ResultSet resultSet = MySqlRequest.LIST_ALL_SRT_FILES.execute()) {
			while (resultSet.next()) {
				 list.add(resultSet.getString("filename"));
			}
			MySqlRequest.closeLast();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public SrtFile createEmptyFileFrom(SrtFile template, String filename) {
		List<Subtitle> sequences = template.getSequences().stream()
				.map(Subtitle::emptyCopy)
				.collect(Collectors.toList());
		
		SrtFile emptyFile = new SrtFile(filename, sequences);
		add(emptyFile);
		
		return emptyFile;
	}
}
