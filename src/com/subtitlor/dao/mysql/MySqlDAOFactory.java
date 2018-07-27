package com.subtitlor.dao.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.subtitlor.dao.AbstractDAOFactory;
import com.subtitlor.dao.SequenceDAO;
import com.subtitlor.dao.SrtFileDAO;

public class MySqlDAOFactory extends AbstractDAOFactory {
	
	private static String url = "jdbc:mysql://localhost:3306/subtitlor?autoReconnect=true&useSSL=false";
	private static String user = "opcr";
	private static String passwd = "opcr";

	/**
	 * My SQL implementation of Abstract DAO Factory
	 */
	public MySqlDAOFactory() {
    	try {
    		// Load driver for MySql
            Class.forName("com.mysql.cj.jdbc.Driver");
        } 
        catch (ClassNotFoundException e) {
			e.printStackTrace();
        }
	}
	
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, passwd);
    }
	
	public SrtFileDAO getSrtFileDAO(){
		return new MySqlSrtFile(this);
	}
	
	public SequenceDAO getSequenceDAO(String id){
		return new MySqlSequence(id);
	}
}
