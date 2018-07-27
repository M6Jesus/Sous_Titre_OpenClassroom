package com.subtitlor.servlets;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.subtitlor.beans.SrtFile;
import com.subtitlor.dao.AbstractDAOFactory;
import com.subtitlor.dao.FactoryType;
import com.subtitlor.dao.SrtFileDAO;
import com.subtitlor.utilities.FileManager;
import com.subtitlor.utilities.Key;

@WebServlet("/Download")
public class Download extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Download() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String filename = null;

		try {
		    filename = request.getParameter(Key.FILENAME);        
		    if(filename == null || filename.isEmpty()) {
		        throw new ServletException("File Name can't be null or empty");
		    }
		    
		    String filepath = FileManager.getPathFor(filename);

		    // Create file from DB
			SrtFileDAO srtDao = AbstractDAOFactory.getFactory(FactoryType.MySql_DAO_Factory).getSrtFileDAO();
			Optional<SrtFile> srtFile = srtDao.find(filename);
			
			if (srtFile.isPresent()) {
				// Add file in DB
				srtDao = AbstractDAOFactory.getFactory(FactoryType.File_DAO_Factory).getSrtFileDAO();
				srtDao.update(srtFile.get());
			}
		    
		    File file = new File(filepath);
		    if(!file.exists()) {
		        throw new ServletException("File doesn't exists on server.");
		    }

		    response.setContentType("text/plain");
		    response.setHeader("Content-Disposition","attachment; filename=\"" + filename + FileManager.EXTENSION + "\""); 

		    java.io.FileInputStream fileInputStream = new java.io.FileInputStream(filepath);

		    int i; 
		    while ((i=fileInputStream.read()) != -1)  {
		         response.getWriter().write(i); 
		    } 
		    fileInputStream.close();
		}
		catch(Exception e) {
		    System.err.println("Error while downloading file["+filename+"] " + e);
		}
	} 

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
