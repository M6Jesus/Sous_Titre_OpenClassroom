package com.subtitlor.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Iterables;
import com.subtitlor.utilities.SupportedLanguage;

public enum MySqlRequest {

	/**
	 * Strings[] = {filename, language}
	 * <p>
	 * INSERT INTO srt_file (filename, language) VALUES (?, ?)
	 */
	INSERT_SRT_FILE {
		public ResultSet execute(String... strings) throws SQLException {
			return MySqlRequest.update("INSERT INTO srt_file (filename, language) VALUES (?, ?)", strings);
		}
	},

	/**
	 * Strings[] = {filename}
	 * <p>
	 * SELECT id, language FROM srt_file WHERE filename = ?
	 */
	SELECT_SRT_FILE {
		public ResultSet execute(String... strings) throws SQLException {
			return MySqlRequest.query("SELECT id, language FROM srt_file WHERE filename = ?", strings);
		}
	},

	/**
	 * Strings[] = {filename}
	 * <p>
	 * DELETE FROM srt_file WHERE filename = ?
	 */
	REMOVE_SRT_FILE {
		public ResultSet execute(String... strings) throws SQLException {
			return MySqlRequest.update("DELETE FROM srt_file WHERE filename = ?", strings);
		}
	},

	/**
	 * Strings[] = {id}
	 * <p>
	 * SELECT filename, language FROM srt_file WHERE id = ?
	 */
	SELECT_SRT_FILE_BY_ID {
		public ResultSet execute(String... strings) throws SQLException {
			return MySqlRequest.query("SELECT filename, language FROM srt_file WHERE id = ?", strings);
		}
	},

	/**
	 * Strings[] = {}
	 * <p>
	 * SELECT id, filename, language FROM srt_file ORDER BY filename
	 */
	LIST_ALL_SRT_FILES {
		public ResultSet execute(String... strings) throws SQLException {
			String[] empty = {};
			return MySqlRequest.query("SELECT id, filename, language FROM srt_file ORDER BY filename", empty);
		}
	},

	/**
	 * Strings[] = {}
	 * <p>
	 * SELECT id, filename, language FROM srt_file ORDER BY filename
	 */
	LIST_ORIG_SRT_FILES {
		public ResultSet execute(String... strings) throws SQLException {
			String[] language = { SupportedLanguage.Original.getID() };
			return MySqlRequest.query("SELECT id, filename FROM srt_file WHERE language = ? ORDER BY filename",
					language);
		}
	},

	/**
	 * Strings[] = {id, srt_file_id, start_time, stop_time, subtitle}
	 * <p>
	 * INSERT INTO sequence (id, srt_file_id, start_time, stop_time, subtitle)
	 * VALUES (?, ?, ?, ?, ?)
	 */
	INSERT_SEQUENCE {
		public ResultSet execute(String... strings) throws SQLException {
			return MySqlRequest.update(
					"INSERT INTO sequence (id, srt_file_id, start_time, stop_time, subtitle) VALUES (?, ?, ?, ?, ?)",
					strings);
		}
	},

	/**
	 * Strings[] = {srt_file_id, id}
	 * <p>
	 * SELECT start_time, stop_time, subtitle FROM sequence WHERE srt_file_id = ?
	 * AND id = ?
	 */
	SELECT_SEQUENCE {
		public ResultSet execute(String... strings) throws SQLException {
			return MySqlRequest.query(
					"SELECT start_time, stop_time, subtitle FROM sequence WHERE srt_file_id = ? AND id = ?", strings);
		}
	},

	/**
	 * Strings[] = {subtitle, srt_file_id, id}
	 * <p>
	 * UPDATE sequence SET subtitle = ? WHERE srt_file_id = ? AND id = ?
	 */
	UPDATE_SEQUENCE {
		public ResultSet execute(String... strings) throws SQLException {
			return MySqlRequest.update("UPDATE sequence SET subtitle = ? WHERE srt_file_id = ? AND id = ?", strings);
		}
	},

	/**
	 * Strings[] = {srt_file_id}
	 * <p>
	 * SELECT id, start_time, stop_time, subtitle FROM sequence WHERE srt_file_id =
	 * ?
	 */
	LIST_SEQUENCES {
		public ResultSet execute(String... strings) throws SQLException {
			return MySqlRequest.query("SELECT id, start_time, stop_time, subtitle FROM sequence WHERE srt_file_id = ?",
					strings);
		}
	},

	/**
	 * Strings[] = {srt_file_id, start_id, stop_id}
	 * <p>
	 * SELECT id, start_time, stop_time, subtitle FROM sequence 
	 * WHERE srt_file_id = ? AND id >= ? AND id <= ? ORDER BY id
	 */
	SUBLIST_SEQUENCES {
		public ResultSet execute(String... strings) throws SQLException {
			return MySqlRequest.query(
					"SELECT id, start_time, stop_time, subtitle FROM sequence WHERE srt_file_id = ? AND id >= ? AND id <= ? ORDER BY id",
					strings);
		}
	},

	/**
	 * Strings[] = {srt_file_id}
	 * <p>
	 * SELECT COUNT(*) FROM sequence WHERE srt_file_id = ?
	 */
	COUNT_SEQUENCES {
		public ResultSet execute(String... strings) throws SQLException {
			return MySqlRequest.query("SELECT COUNT(*) FROM sequence WHERE srt_file_id = ?", strings);
		}
	};

	public abstract ResultSet execute(String... strings) throws SQLException;

	private static ResultSet query(String sql, String... strings) throws SQLException {
		Connection connection = MySqlDAOFactory.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		preparedStatement = sqlPreparedStatement(connection, sql, strings);
		resultSet = preparedStatement.executeQuery();

		add(resultSet, preparedStatement, connection);

		return resultSet;
	}

	private static ResultSet update(String sql, String... strings) throws SQLException {
		Connection connection = MySqlDAOFactory.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		preparedStatement = sqlPreparedStatement(connection, sql, strings);
		int statut = preparedStatement.executeUpdate();
		if (statut == 0) {
			System.out.println("Error in executeUpdate: '" + sql + "', " + Arrays.toString(strings));
		}

		resultSet = preparedStatement.getGeneratedKeys();

		add(resultSet, preparedStatement, connection);

		return resultSet;
	}

	private static PreparedStatement sqlPreparedStatement(Connection connexion, String sql, String... strings)
			throws SQLException {
		PreparedStatement preparedStatement = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		for (int i = 0; i < strings.length; i++) {
			preparedStatement.setObject(i + 1, strings[i]);
		}
		return preparedStatement;
	}

	private static List<ResultSet> rsList = new ArrayList<ResultSet>();
	private static List<PreparedStatement> psList = new ArrayList<PreparedStatement>();
	private static List<Connection> cList = new ArrayList<Connection>();

	private static void add(ResultSet rs, PreparedStatement ps, Connection c) {
		rsList.add(rs);
		psList.add(ps);
		cList.add(c);
	}

	public static void closeLast() throws SQLException {
		if (!rsList.isEmpty()) {
			Iterables.getLast(rsList).close();
		}
		if (!psList.isEmpty()) {
			Iterables.getLast(psList).close();
		}
		if (!cList.isEmpty()) {
			Iterables.getLast(cList).close();
		}
	}
}
