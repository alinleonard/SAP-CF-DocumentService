package com.cerner.maven_document_service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.chemistry.opencmis.client.SessionFactoryFinder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;


/**
 * Servlet implementation class Servlet
 */
@WebServlet("/")
public class Servlet extends HttpServlet  {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Servlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		//Session session = DocumentServiceAdapter.getCmisSession(response);
		Session session = null;
		try {
			// default factory implementation
			SessionFactory factory = SessionFactoryImpl.newInstance();
			Map<String, String> parameters = new HashMap<String, String>();

			// user credentials
			parameters.put(SessionParameter.USER, Config.SESSION_USER);
			parameters.put(SessionParameter.PASSWORD, Config.SESSION_PASSWORD);

			// connection settings
			parameters.put(SessionParameter.BROWSER_URL, Config.BROWSER_URL);
			parameters.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());
			parameters.put(SessionParameter.REPOSITORY_ID, Config.REPOSITORY_ID);

			// create session
			session = factory.createSession(parameters);
			
		} catch (Exception e) {
			response.getWriter().println("<div style='color:red'>There was an error in retrieving the CMIS Session</div>");
			// TODO: handle exception
			response.getWriter().println(e.getMessage());
			e.printStackTrace(response.getWriter());
		}
		if(session != null)
			response.getWriter().append("Root folder: ").append(session.getRootFolder().getPath());
		else
			response.getWriter().println("There was no session found");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
