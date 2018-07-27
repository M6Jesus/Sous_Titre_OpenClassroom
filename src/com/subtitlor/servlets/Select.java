package com.subtitlor.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.subtitlor.beans.SrtFile;
import com.subtitlor.dao.AbstractDAOFactory;
import com.subtitlor.dao.FactoryType;
import com.subtitlor.dao.SrtFileDAO;
import com.subtitlor.utilities.*;

/**
 * Servlet implementation class Select
 */
@WebServlet("/Select")
public class Select extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String VUE = "/WEB-INF/select.jsp";

	public Select() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	System.out.println("ContextPath: " + ContextHandler.getContext().getRealPath(""));
		
    	SrtFileDAO srtDao = AbstractDAOFactory.getFactory(FactoryType.MySql_DAO_Factory).getSrtFileDAO();
    	List<String> list = srtDao.list();
		
		// If list in database is empty, check if SRT files are present on server
		if (list.isEmpty()) {
			srtDao = AbstractDAOFactory.getFactory(FactoryType.File_DAO_Factory).getSrtFileDAO();
			list = srtDao.list();
			
			// Add files in DB
			for (String filename: list) {
				copyFromFileSystemToDB(filename);
			}
		}
		
		// Remove all translation files (ended by Key.TRANSLATION_SUFFIX) from list
		List<String> filtered = list.stream()
				.filter(f -> !f.endsWith(Key.TRANSLATION_SUFFIX))
				.collect(Collectors.toList());

		
		request.setAttribute(Key.FILENAME_LIST, filtered);
		
		this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String filename = "";

		if (request.getParameterMap().containsKey(Key.UPLOAD)) {
			Part part = request.getPart(Key.FILE);
			filename = FileManager.downloadFrom(part);
			copyFromFileSystemToDB(filename);
		}
		if (request.getParameterMap().containsKey(Key.SELECT) && request.getParameterMap().containsKey(Key.SELECTED)) {
			filename = request.getParameter(Key.SELECTED);
		}

		response.sendRedirect("/Subtitlor/edit?" + Key.FILENAME + "=" + filename);
	}

	private void copyFromFileSystemToDB(String filename) {
		// Read file
		SrtFileDAO srtDao = AbstractDAOFactory.getFactory(FactoryType.File_DAO_Factory).getSrtFileDAO();
		Optional<SrtFile> srtFile = srtDao.find(filename);

		if (srtFile.isPresent()) {
			// Add file in DB
			srtDao = AbstractDAOFactory.getFactory(FactoryType.MySql_DAO_Factory).getSrtFileDAO();
			List<String> list = srtDao.list();
			
			// If file already present in DB, delete it first to avoid confusion
			if (list.contains(filename)) {
				srtDao.delete(filename);
			}
			
			srtDao.add(srtFile.get());
		}
	}

}
