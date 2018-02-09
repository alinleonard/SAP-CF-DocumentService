package com.cerner.maven_document_service;



public class DocumentServiceAdapter {
	/*private static final Boolean CREATE_REPOSITORY_IF_NOT_EXIST = false;

	private static Session cmisSession = null;

	public static void createFolder(HttpServletResponse response, String folderName) throws IOException {

		Session session = getCmisSession(response);

		if (session == null) {
			response.getWriter().println("ECM not found, the session is null");
			return;
		}
		
		response.getWriter().printf("<h3>Create of %s folder</h3>",  folderName);
		
		// access the root folder of the repository
		Folder root = session.getRootFolder();
		
//		int beginIndex = folderPath.lastIndexOf("/");
//		String folderName = folderPath.substring(beginIndex, folderPath.length());

		// create a new folder
		Map<String, String> newFolderProps = new HashMap<String, String>();
		newFolderProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
		newFolderProps.put(PropertyIds.NAME, folderName);
		try {
			root.createFolder(newFolderProps);
			response.getWriter().printf("<p>Folder: %s created with succcess!</p>", folderName);
		} catch (CmisNameConstraintViolationException e) {
			// Folder exists already, nothing to do
			response.getWriter().printf(
					"<p>Folder: %s exists or something else happened!</p><p style='color:red'>%s</p>", folderName,
					e.getMessage());
		}
	}

	public static void createDocument(HttpServletResponse response) throws IOException {

		Session session = getCmisSession(response);

		if (session == null) {
			response.getWriter().println("ECM not found, the session is null");
			return;
		}
		
		response.getWriter().println("<h3 style='color:blue'>createDocument</h3>");

		// access the root folder of the repository
		Folder root = session.getRootFolder();

		// create a new file in the root folder
	      Map<String, Object> properties = new HashMap<String, Object>();
	      properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
	      properties.put(PropertyIds.NAME, "HelloWorld.txt");
	      byte[] helloContent = "Hello World!".getBytes("UTF-8");
	      InputStream stream = new ByteArrayInputStream(helloContent);
	      ContentStream contentStream = session.getObjectFactory()
	                                    .createContentStream("HelloWorld.txt",
	                                    helloContent.length, "text/plain; charset=UTF-8", stream);
	      try {
	        root.createDocument(properties, contentStream, VersioningState.NONE);
	      } catch (CmisNameConstraintViolationException e) {
	        // Document exists already, nothing to do
	      }
		
	}
	
	public static void displayFolderStructureOfRoot(HttpServletResponse res) throws IOException {
		
		res.getWriter().println("<h3 style='color:blue'>displayFolderStructureOfRoot</h3>");

		Session session = getCmisSession(res);

		if (session == null) {
			res.getWriter().println("ECM not found, the session is null");
			return;
		}
		
		res.getWriter().println("<h3>Display of root folder structure</h3>");

		// access the root folder of the repository
		Folder root = session.getRootFolder();
		

		// Display the root folder's children objects
		ItemIterable<CmisObject> children = root.getChildren();
		res.getWriter().println(
				"The root folder of the repository with id " + root.getId() + " contains the following objects:<ul>");
		for (CmisObject o : children) {
			res.getWriter().print("<li>" + o.getName());
			if (o instanceof Folder) {
				res.getWriter().println(" createdBy: " + o.getCreatedBy() + "</li>");
			} else {
				Document doc = (Document) o;
				res.getWriter().println(" createdBy: " + o.getCreatedBy() + " filesize: "
						+ doc.getContentStreamLength() + " bytes" + "</li>");
			}
		}
		res.getWriter().println("</ul>");
		
	}
	
	public static void displayFolderStructure(HttpServletResponse res, String folderPath) throws IOException {

		Session session = getCmisSession(res);

		if (session == null) {
			res.getWriter().println("ECM not found, the session is null");
			return;
		}
		
		res.getWriter().printf("<h3>Display of %s folder path structure</h3>",  folderPath);

		// access the root folder of the repository
		Folder folder = getFolderByName(res, folderPath);
		
		if(folder == null) {
			return;
		}
		// Display the root folder's children objects
		ItemIterable<CmisObject> children = folder.getChildren();
		res.getWriter().println(
				"The root folder of the repository with id " + folder.getId() + " contains the following objects:<ul>");
		for (CmisObject o : children) {
			res.getWriter().print("<li>" + o.getName());
			if (o instanceof Folder) {
				res.getWriter().println(" createdBy: " + o.getCreatedBy() + "</li>");
			} else {
				Document doc = (Document) o;
				res.getWriter().println(" createdBy: " + o.getCreatedBy() + " filesize: "
						+ doc.getContentStreamLength() + " bytes" + "</li>");
			}
		}
		res.getWriter().println("</ul>");
		
	}
	
	*//**
	 * 
	 * @param res
	 * The HttpServletResponse so we can interact with the frontend
	 * @param folderPath
	 * The folder path + the folder name eg: /folderName
	 * @return
	 * returns null of there was no folder found
	 * @throws IOException
	 *//*
	private static Folder getFolderByName(HttpServletResponse res, String folderPath) throws IOException {
		
		Session session = getCmisSession(res);
		
		Folder folder = null;

		if (session == null) {
			res.getWriter().println("ECM not found, the session is null");
			return folder;
		}
		
		try {
			session.getObjectByPath(folderPath);
			folder = (Folder) session.getObjectByPath(folderPath);
		} catch (CmisObjectNotFoundException e) {
			// TODO: handle exception
			res.getWriter().printf("Folder path %s was not found", folderPath);
		}
		
		return folder;
	}

	public static Session getCmisSession(HttpServletResponse response) throws IOException {
		if (response == null)
			return cmisSession = null;
		if (cmisSession == null) {
			
			response.getWriter().println("<h3 style='color:blue'>getCmisSession</h3>");
			
			try {
				// default factory implementation
				SessionFactory factory = SessionFactoryFinder.find();
				Map<String, String> parameters = new HashMap<String, String>();

				// user credentials
				parameters.put(SessionParameter.USER, Config.SESSION_USER);
				parameters.put(SessionParameter.PASSWORD, Config.SESSION_PASSWORD);

				// connection settings
				parameters.put(SessionParameter.BROWSER_URL, Config.BROWSER_URL);
				parameters.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());
				parameters.put(SessionParameter.REPOSITORY_ID, Config.REPOSITORY_ID);

				// create session
				Session session = factory.createSession(parameters);
				
				cmisSession = session;
			} catch (ClassNotFoundException | InstantiationException e) {
				response.getWriter().println("<div style='color:red'>There was an error in retrieving the CMIS Session</div>");
				// TODO: handle exception
				response.getWriter().println(e.getMessage());
				e.printStackTrace(response.getWriter());
			}
		}

		return cmisSession;
	}
	

	private static void createRepository(EcmService ecmSvc) {
		RepositoryOptions options = new RepositoryOptions();
		options.setUniqueName(Config.UNIQUE_NAME);
		options.setRepositoryKey(Config.UNIQUE_KEY);
		options.setVisibility(Visibility.PROTECTED);
		ecmSvc.createRepository(options);
	}*/
}
