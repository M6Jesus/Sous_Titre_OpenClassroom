package com.subtitlor.dao;

import com.subtitlor.dao.file.FileDAOFactory;
import com.subtitlor.dao.mysql.MySqlDAOFactory;

/**
 * Abstract DAO factory
 */
public abstract class AbstractDAOFactory {
	
	/**
	 * Abstract DAO of SRT File.  
	 * This interface should:
	 * <li> find an SRT file by name </li>
	 * <li> add a new SRT file </li>
	 * <li> update a prevously added file </li>
	 * <li> delete a file with its name </li>
	 * <li> list all referenced files </li>
	 * <li> create an empty SRT file from a previously added file </li>
	 * 
	 * @return A SRT file DAO implementation
	 */
	public abstract SrtFileDAO getSrtFileDAO();
	
	/**
	 * Abstract DAO of an SRT file sequences.
	 * This interface should:
	 * <li> be related to an SRT file </li>
	 * <li> find and return a sequence by its index </li>
	 * <li> update a sequence text </li>
	 * <li> return a sublist of sequences </li>
	 * <li> return its parent SRT file </li>
	 * <li> return the number of sequences in its parent SRT file </li>
	 */
	public abstract SequenceDAO getSequenceDAO(String id);

	public static AbstractDAOFactory getFactory(FactoryType type){
		
		if(type.equals(FactoryType.File_DAO_Factory)) 
			return new FileDAOFactory();
		
		if(type.equals(FactoryType.MySql_DAO_Factory))
			return new MySqlDAOFactory();
		
		return null;
	}
}
