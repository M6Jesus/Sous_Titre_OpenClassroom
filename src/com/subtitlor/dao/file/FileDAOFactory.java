package com.subtitlor.dao.file;

import com.subtitlor.dao.AbstractDAOFactory;
import com.subtitlor.dao.SrtFileDAO;
import com.subtitlor.dao.SequenceDAO;

/**
 * File system implementation of Abstract DAO Factory.
 */
public class FileDAOFactory extends AbstractDAOFactory {
	public SrtFileDAO getSrtFileDAO(){
		return new FileSrtFile();
	}
	
	public SequenceDAO getSequenceDAO(String id){
		return new FileSubtitle(id);
	}
}
