package com.subtitlor.dao.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.subtitlor.beans.Subtitle;
import com.subtitlor.beans.SrtFile;
import com.subtitlor.dao.FactoryType;
import com.subtitlor.dao.SequenceDAO;

public class MySqlSequence implements SequenceDAO {
	String srtFileId;
	
	MySqlSequence(String filename) {
		try (ResultSet resultSet = MySqlRequest.SELECT_SRT_FILE.execute(filename)) {
			if (resultSet.next()) {
				 srtFileId = resultSet.getString("id");
			}
			MySqlRequest.closeLast();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Optional<Subtitle> getSequence(String id) {
		try (ResultSet resultSet = MySqlRequest.SELECT_SEQUENCE.execute(srtFileId, id)) {
			if (resultSet.next()) {
				 return Optional.of(map(resultSet));
			}
			MySqlRequest.closeLast();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public void updateSubtitle(String id, String[] lines) {
		String subtitle = String.join("\n",lines);
		try {
			MySqlRequest.UPDATE_SEQUENCE.execute(subtitle, srtFileId, id);
			MySqlRequest.closeLast();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Optional<SrtFile> getParentSrtFile() {
		try (ResultSet resultSet = MySqlRequest.SELECT_SRT_FILE_BY_ID.execute(srtFileId)) {
			if (resultSet.next()) {
				String filename = resultSet.getString("filename");
				return MySqlDAOFactory.getFactory(FactoryType.MySql_DAO_Factory).getSrtFileDAO().find(filename);
			}
			MySqlRequest.closeLast();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public List<Subtitle> getList(String startId, String stopId) {
		List<Subtitle> sequences = new ArrayList<Subtitle>();
		try (ResultSet resultSet = MySqlRequest.SUBLIST_SEQUENCES.execute(srtFileId, startId, stopId)) {
			while (resultSet.next()) {
				 sequences.add(map(resultSet));
			}
			MySqlRequest.closeLast();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return sequences;
	}

	public int getListCount() {
		try (ResultSet resultSet = MySqlRequest.COUNT_SEQUENCES.execute(srtFileId)) {
			if (resultSet.next()) {
				 return resultSet.getInt(1);
			}
			MySqlRequest.closeLast();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private static Subtitle map(ResultSet resultSet) throws SQLException {
		int index = resultSet.getInt("id");
		String startTime = resultSet.getString("start_time");
		String stopTime = resultSet.getString("stop_time"); 
		String subtitle = resultSet.getString("subtitle");
		return new Subtitle(index,startTime,stopTime,subtitle.split("\n"));
	}
}
