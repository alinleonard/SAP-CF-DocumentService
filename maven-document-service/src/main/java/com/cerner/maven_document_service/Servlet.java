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
		
		response.getWriter().append("<html><body>");
		
		displayCommandsToHTML(response);
		listenForParameters(request, response);
		
		response.getWriter().append("</body></html>");
		//Session session = DocumentServiceAdapter.getCmisSession(response);
/*		Session session = null;
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
		}*/
		
//		if(session != null)
//			response.getWriter().append("Root folder: ").append(session.getRootFolder().getPath());
//		else
//			response.getWriter().println("There was no session found");
	}
	
	private void displayCommandsToHTML(HttpServletResponse res) throws IOException {
		StringBuilder content = new StringBuilder();
		
		content.append("<h3>Commands</h3>");
		content.append("<div><b>display?=folderPath</b></div>");
		content.append("<div><b>&dContent=fileName</b></div>");
		content.append("<q>Display the content of the current file.</q>");
		content.append("<div><b>&nFile=fileName</b></div>");
		content.append("<q>Creates new file with the filename</q>");
		content.append("<div><b>&nFolder=folderPath</b></div>");
		content.append("<ul><li>Creates new folder with the provided name.</li>");
		content.append("<li>eg: nFolder?=folderName</li></ul>");
		content.append("<div><b>&dObject=path</b></div>");
		content.append("<q>Display the folder structure of document content</q>");
		content.append("<div style='margin-top:10px'></div>");
		
		res.getWriter().println(content.toString());
	}
	
	private void listenForParameters(HttpServletRequest req,HttpServletResponse res) throws IOException {
		String queryPath = req.getQueryString();
		
		res.getWriter().printf("(GET) 15:40 Query string: %s", queryPath);
		
		try {
			if(req.getParameter("root") != null) {
				DocumentServiceAdapter.displayFolderStructure(res);
			}
			if(req.getParameter("nFolder") != null) {
				DocumentServiceAdapter.createFolder(res, req.getParameter("nFolder"));
			}
			if(req.getParameter("nFile") != null) {
				DocumentServiceAdapter.createDocumentAsBytes(res, req.getParameter("nFile"));
			}
			if(req.getParameter("dObject") != null) {
				DocumentServiceAdapter.getObjectByPath(res, req.getParameter("dObject"));
			}
			if(req.getParameter("acl") != null) {
				DocumentServiceAdapter.getACLcapabilities(res);
			}
			if(req.getParameter("acldummy") != null) {
				DocumentServiceAdapter.setACLdummy(res);
			}
		} catch (Exception e) {
			res.getWriter().printf("Error: %s", e.getMessage());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
