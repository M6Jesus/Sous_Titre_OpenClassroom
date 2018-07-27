package com.subtitlor.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.subtitlor.utilities.Key;
import com.subtitlor.beans.Subtitle;
import com.subtitlor.beans.SrtFile;
import com.subtitlor.dao.*;

/**
 * @author nicolas
 *
 */
@WebServlet("/EditSubtitle")
public class EditSubtitle extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String VUE = "/WEB-INF/edit_subtitle.jsp";

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();

		String filename = getFilename(request);
		System.out.println("Edit subtitle named: " + filename);

		int sequencesPerPage = getSPP(request);
		request.setAttribute(Key.SPP, sequencesPerPage);

		// Files DAO
		SrtFileDAO dao = AbstractDAOFactory.getFactory(FactoryType.MySql_DAO_Factory).getSrtFileDAO();
		if (dao.list().contains(filename)) {

			// Save valid filename in session
			session.setAttribute(Key.FILENAME, filename);

			// Translation file creation if needed
			if (!dao.list().contains(filename + Key.TRANSLATION_SUFFIX)) {
				System.out.println("Translation not found, create new translation file");
				// Create a new translation file with same size as original file and empty lines
				SrtFile originalFile = dao.find(filename).get();
				SrtFile translationFile = dao.createEmptyFileFrom(originalFile, filename + Key.TRANSLATION_SUFFIX);
				System.out.println("Translation file created as " + translationFile.getName());
			}

			SequenceDAO original = AbstractDAOFactory
					.getFactory(FactoryType.MySql_DAO_Factory)
					.getSequenceDAO(filename);
			SequenceDAO translation = AbstractDAOFactory
					.getFactory(FactoryType.MySql_DAO_Factory)
					.getSequenceDAO(filename + Key.TRANSLATION_SUFFIX);
			
			/* Computations for displaying a small part of all sequences*/
			int nbSequences = original.getListCount();
			int nbPages = nbSequences / sequencesPerPage + ((nbSequences % sequencesPerPage == 0) ? 0 : 1);
			request.setAttribute(Key.NBPAGES, nbPages);
			int currentPage = getCurrentPage(request, nbPages);
			request.setAttribute(Key.PAGE, currentPage);
			
			setPaginationLimit(request, currentPage, nbPages);

			int firstDisplayedSequence = ((currentPage - 1) * sequencesPerPage) + 1;
			int lastDisplayedSequence = firstDisplayedSequence + sequencesPerPage - 1;

			if (lastDisplayedSequence > nbSequences) {
				lastDisplayedSequence = nbSequences;
			}
			String startID = Integer.toString(firstDisplayedSequence);
			String stopID = Integer.toString(lastDisplayedSequence);
			
			/* Retrieve only wanted sequences */
			List<Subtitle> originalSequences = original.getList(startID, stopID);
			List<Subtitle> translatedSequences = translation.getList(startID, stopID);
			
			/* Add them to an attribute named sequences */
			List<List<Subtitle>> sequences = Arrays.asList(originalSequences, translatedSequences);
			request.setAttribute(Key.SEQUENCES, sequences);

			setUrls(request, filename);
		}
		else {
			request.setAttribute(Key.FILENAME, filename);
			request.setAttribute(Key.MESSAGE, "File not found in server");
			request.setAttribute(Key.DISABLED, "disabled");
		}

		this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String filename = request.getParameter(Key.FILENAME);
		String page = request.getParameter(Key.PAGE);
		String fpp = request.getParameter(Key.SPP);
		String url = "/Subtitlor/edit?" + Key.FILENAME + "=" + filename + "&" + Key.PAGE + "=" + page + "&" + Key.SPP
				+ "=" + fpp;

		// Get translation file
		AbstractDAOFactory factory = AbstractDAOFactory.getFactory(FactoryType.MySql_DAO_Factory);
		SequenceDAO seqDao = factory.getSequenceDAO(filename + Key.TRANSLATION_SUFFIX);
		Optional<SrtFile> parentSrtFile = seqDao.getParentSrtFile();

		if (parentSrtFile.isPresent()) {
			// Retrieve the list of input subtitles
			Map<String, String[]> param = request.getParameterMap();
			List<String> inputParamList = param.keySet().stream()
					.filter(k -> k.startsWith(Key.SEQUENCE_PREFIX))
					.collect(Collectors.toList());

			// Update subtitles
			for (String inputParam : inputParamList) {
				String index = inputParam.split(Key.SEQUENCE_PREFIX)[1];
				seqDao.updateSubtitle(index, param.get(inputParam));
			}
		}

		response.sendRedirect(url);
	}
	
	
	/**
	 * Find valid filename in request parameters
	 * or use the filename saved in session
	 * or use the default filename : "password_presentation".
	 * @param request
	 * @return filename string
	 */
	private String getFilename(HttpServletRequest request) {
		HttpSession session = request.getSession();
		
		String filename = Key.DEFAULT_FILENAME;
		if (session.getAttribute(Key.FILENAME) != null) {
			filename = (String) session.getAttribute(Key.FILENAME);
		}
		if (request.getParameterMap().containsKey(Key.FILENAME)) {
			String tmp = request.getParameter(Key.FILENAME);
			if (!tmp.equals(Key.LAST_EDITED_FILE)) {
				filename = tmp;
			}
		}
		return filename;
	}
	
	/**
	 * Retrieve and correct the current page number.
	 * @param request
	 * @param nbPages number of pages which could be displayed
	 * @return corrected current page number
	 */
	private int getCurrentPage(HttpServletRequest request, int nbPages) {
		int currentPage = 1;
		if (request.getParameterMap().containsKey(Key.PAGE)) {
			currentPage = Integer.parseInt(request.getParameter(Key.PAGE));
		}

		if (currentPage < 1) {
			currentPage = 1;
		}
		if (currentPage > nbPages) {
			currentPage = nbPages;
		}
		
		return currentPage;
	}
	
	/**
	 * Retrieve frames per page number in request parameters or in session
	 * @param request
	 * @return
	 */
	private int getSPP(HttpServletRequest request) {
		HttpSession session = request.getSession();
		
		int sequencesPerPage = 5;
		if (request.getParameterMap().containsKey(Key.SPP)) {
			sequencesPerPage = Integer.parseInt(request.getParameter(Key.SPP));
			session.setAttribute(Key.SPP, sequencesPerPage);
		}
		else if (session.getAttribute(Key.SPP) != null) {
			sequencesPerPage = (int) session.getAttribute(Key.SPP);
		}
		if (sequencesPerPage < 1) {
			sequencesPerPage = 1;
		}
		return sequencesPerPage;
	}
	
	/**
	 * Set attributes in request to obtain a working pagination navbar in edit_subtitle.jsp.
	 * @param request
	 * @param currentPage
	 * @param nbPages
	 */
	private void setPaginationLimit(HttpServletRequest request, int currentPage, int nbPages) {
		if (currentPage <= 3) {
			request.setAttribute(Key.PAGE_BEGIN, 1);
			request.setAttribute(Key.PAGE_END, 5);
		}
		else if (currentPage >= nbPages - 2) {
			request.setAttribute(Key.PAGE_BEGIN, nbPages - 4);
			request.setAttribute(Key.PAGE_END, nbPages);
		}
		else {
			request.setAttribute(Key.PAGE_BEGIN, currentPage - 2);
			request.setAttribute(Key.PAGE_END, currentPage + 2);
		}
	}
	
	/**
	 * Set urls in request attributes to obtain functional link in edit_subtitle.jsp.
	 * @param request
	 * @param filename
	 */
	private void setUrls(HttpServletRequest request, String filename) {
		// Base Url for pagination: URL without pagination parameter
		if (request.getQueryString() != null) {
			String queryStr = String.join("", request.getQueryString().split("[?&]page=-?\\d+"));
			String url = request.getRequestURI() + "?" + queryStr;
			request.setAttribute(Key.BASE_PAGINATION_URL, url);
		}
		else {
			request.setAttribute(Key.BASE_PAGINATION_URL, request.getRequestURI());
		}

		request.setAttribute(Key.ORIGINAL_FILE_URL, "download?" + Key.FILENAME + "=" + filename);
		request.setAttribute(Key.TRANSLATION_FILE_URL,
				"download?" + Key.FILENAME + "=" + filename + Key.TRANSLATION_SUFFIX);
	}

}
