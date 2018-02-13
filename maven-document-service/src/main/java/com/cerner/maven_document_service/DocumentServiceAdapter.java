package com.cerner.maven_document_service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.client.util.ContentStreamUtils;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.AclCapabilities;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.PermissionMapping;
import org.apache.chemistry.opencmis.commons.definitions.PermissionDefinition;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNameConstraintViolationException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.MimeTypes;

import com.sap.cloud.sdk.service.prov.api.request.QueryRequest;

public class DocumentServiceAdapter {
	private static final Boolean CREATE_REPOSITORY_IF_NOT_EXIST = false;

	private static Session cmisSession = null;

	public static void createFolder(HttpServletResponse response, String folderName) throws IOException {

		Session session = getCmisSession(response);

		if (session == null) {
			response.getWriter().println("ECM not found, the session is null");
			return;
		}

		response.getWriter().printf("<h3>Create of %s folder</h3>", folderName);

		// access the root folder of the repository
		Folder root = session.getRootFolder();

		// int beginIndex = folderPath.lastIndexOf("/");
		// String folderName = folderPath.substring(beginIndex, folderPath.length());

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

	public static void filter(HttpServletResponse response, String filter) throws IOException {
		Session session = getCmisSession(response);

		if (session == null) {
			response.getWriter().println("ECM not found, the session is null");
			return;
		}

		// access the root folder of the repository
		Folder root = session.getRootFolder();

		try {
			ItemIterable<QueryResult> query = session.query(filter, true);

			response.getWriter().printf("Total results: %d", query.getTotalNumItems());

			Iterator<QueryResult> it = query.iterator();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void createDocument(HttpServletResponse response, String documentNameWithExtension)
			throws IOException {

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
		properties.put(PropertyIds.NAME, documentNameWithExtension);
		byte[] helloContent = "Hello World!".getBytes("UTF-8");
		InputStream stream = new ByteArrayInputStream(helloContent);
		ContentStream contentStream = session.getObjectFactory().createContentStream(documentNameWithExtension,
				helloContent.length, "text/plain; charset=UTF-8", stream);
		try {
			root.createDocument(properties, contentStream, VersioningState.NONE);
		} catch (CmisNameConstraintViolationException e) {
			// Document exists already, nothing to do
		}

	}
	
	public static void createDocument(HttpServletResponse response, String documentNameWithExtension, String createdBy)
			throws IOException {

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
		properties.put(PropertyIds.NAME, documentNameWithExtension);
		properties.put(PropertyIds.CREATED_BY, createdBy);
		properties.put("project:string", "red");
		properties.put("project:number", 1234);

		
		
		try {
			root.createDocument(properties, null, VersioningState.NONE);
		} catch (Exception e) {
			// Document exists already, nothing to do
			e.printStackTrace(response.getWriter());
		} 

	}

	public static void createDocumentAsBytes(HttpServletResponse response, String filename) throws IOException {
		// https://chemistry.apache.org/docs/cmis-samples/samples/content/index.html
		Session session = getCmisSession(response);

		if (session == null) {
			response.getWriter().println("ECM not found, the session is null");
			return;
		}

		response.getWriter().println("<h3 style='color:blue'>createDocumentAsBytes</h3>");

		// access the root folder of the repository
		Folder root = session.getRootFolder();

		// create a new file in the root folder
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		properties.put(PropertyIds.NAME, filename);
		byte[] bytes = "Hello World! octet stream".getBytes();
		//ContentStream cs2 = ContentStreamUtils.createByteArrayContentStream(filename, bytes,
			//	MimeTypes.getMIMEType("txt"));
		ContentStream cs2 = ContentStreamUtils.createByteArrayContentStream(filename, bytes,
				MimeTypes.getMIMEType("application/octet-stream"));
		
		try {
			root.createDocument(properties, cs2, VersioningState.NONE);
		} catch (CmisNameConstraintViolationException e) {
			e.printStackTrace(response.getWriter());
		}
	}

	public static void getObjectByPath(HttpServletResponse response, String path) throws IOException {
		Session session = getCmisSession(response);

		if (session == null) {
			response.getWriter().println("ECM not found, the session is null");
			return;
		}

		CmisObject cmisObject = session.getObjectByPath(path);

		if (cmisObject instanceof Document) {
			Document document = (Document) cmisObject;
			ContentStream contentStream = document.getContentStream();
			InputStream stream = contentStream.getStream();
			
			response.getWriter().println("Properties <br>");
			//Displaying the properties of an Object
			List<org.apache.chemistry.opencmis.client.api.Property<?>> props = document.getProperties();
			for (org.apache.chemistry.opencmis.client.api.Property<?> p : props) {
				response.getWriter().println(p.getDefinition().getDisplayName() + "=" + p.getValuesAsString() + "<br>");
			}
		
			response.getWriter().printf("Content: %s", 
					getStringFromInputStream(response, stream));
			
			response.getWriter().println("<br>ContentURL: " + document.getContentUrl());
		} else if (cmisObject instanceof Folder) {
			// it's a folder
			Folder folder = (Folder) cmisObject;
			displayFolderStructure(response, folder);
		} else {
			response.getWriter().println("Unknown or not exist object");
		}
	}
	
	private static Document getDocumentByPath(HttpServletResponse response, String path) throws IOException {
		Session session = getCmisSession(response);

		if (session == null) {
			response.getWriter().println("ECM not found, the session is null");
			return null;
		}

		CmisObject cmisObject = session.getObjectByPath(path);

		if (cmisObject instanceof Document) {
			Document document = (Document) cmisObject;
			return document;
		} else if (cmisObject instanceof Folder) {
			return null;
		} else {
			return null;
		}
	}

	public static void displayFolderStructure(HttpServletResponse res) throws IOException {
		Session session = getCmisSession(res);

		if (session == null) {
			res.getWriter().println("ECM not found, the session is null");
			return;
		}

		displayFolderStructure(res, session.getRootFolder());
	}

	public static void displayFolderStructure(HttpServletResponse res, Folder folder) throws IOException {
		Session session = getCmisSession(res);

		if (session == null) {
			res.getWriter().println("ECM not found, the session is null");
			return;
		} else if (folder == null) {
			res.getWriter().println("Folder path does not exist");
			return;
		}

		res.getWriter().printf("<h3>Display of %s folder path structure</h3>", folder.getPath());

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
				res.getWriter().println(" createdBy: " + o.getCreatedBy() + " filesize: " + doc.getContentStreamLength()
						+ " bytes" + "</li>");
			}
		}
		res.getWriter().println("</ul>");

	}
	
	public static void setACLdummy(HttpServletResponse res) throws IOException {
		Session session = getCmisSession(res);

		if (session == null) {
			res.getWriter().println("ECM not found, the session is null");
			return;
		}
		
		String userIdOfUser1 = "user_1";
    	String userIdOfUser2 = "user_2";

    	// list of ACEs which should be added
    	List<Ace> addAcl = new ArrayList<Ace>();

    	// build and add ACE for user U1
    	List<String> permissionsUser1 = new ArrayList<String>();
    		permissionsUser1.add("cmis:all");
    	Ace aceUser1 = session.getObjectFactory().createAce(userIdOfUser1, 
    	    	permissionsUser1);
    	addAcl.add(aceUser1);

    	// build and add ACE for user U2
    	List<String> permissionsUser2 = new ArrayList<String>();
    		permissionsUser2.add("cmis:read");
    	Ace aceUser2 = session.getObjectFactory().createAce(userIdOfUser2, 
    		permissionsUser1);
    	addAcl.add(aceUser2);

    	// list of ACEs which should be removed
    	List<Ace> removeAcl = new ArrayList<Ace>();

    	// build and add ACE for user {sap:builtin}everyone
    	List<String> permissionsEveryone = new ArrayList<String>();
    		permissionsEveryone.add("cmis:all");
    	Ace aceEveryone = session.getObjectFactory().createAce(
    	    	"{sap:builtin}everyone", permissionsEveryone);
    	removeAcl.add(aceEveryone);
    
    	// add and remove the ACEs at the folder
    	//folder.applyAcl(addAcl, removeAcl, AclPropagation.OBJECTONLY);
   		Document document = getDocumentByPath(res, "/file1");
   		document.applyAcl(addAcl, removeAcl, AclPropagation.OBJECTONLY);
   		
   		createDocument(res, "acldocument_test_2.txt", "acl_user_1");
   		
   		addAcl = new ArrayList<Ace>();

    	// build and add ACE for user U1
    	permissionsUser1 = new ArrayList<String>();
    	permissionsUser1.add("cmis:all");
    	Ace aceACLUser1 = session.getObjectFactory().createAce("acl_user_1", 
    	    	permissionsUser1);
    	addAcl.add(aceACLUser1);
    	
    	Document document_new = getDocumentByPath(res, "/acldocument_test_2.txt");
    	document_new.applyAcl(addAcl, null, AclPropagation.OBJECTONLY);
	}
	
	public static void getACLcapabilities(HttpServletResponse res) throws IOException {
		Session session = getCmisSession(res);

		if (session == null) {
			res.getWriter().println("ECM not found, the session is null");
			return;
		}
		
		res.getWriter().println("getting ACL capabilities<br>");
		AclCapabilities aclCapabilities = session.getRepositoryInfo().getAclCapabilities();

		res.getWriter().println("Propogation for this repository is " + aclCapabilities.getAclPropagation().toString() + "<br>");

		res.getWriter().println("permissions for this repository are: <br>");
		for (PermissionDefinition definition : aclCapabilities.getPermissions()) {
			res.getWriter().println(definition.toString() + "<br>");                
		}

		res.getWriter().println("\npermission mappings for this repository are: <br>");
		Map<String, PermissionMapping> repoMapping = aclCapabilities.getPermissionMapping();
		for (String key: repoMapping.keySet()) {
			res.getWriter().println(key + " maps to " + repoMapping.get(key).getPermissions() + "<br>");                
		}
	}

	public static Session getCmisSession(HttpServletResponse response) throws IOException {
		if (response == null)
			return cmisSession = null;
		if (cmisSession == null) {

			response.getWriter().println("<h3 style='color:blue'>getCmisSession</h3>");

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
				cmisSession = factory.createSession(parameters);
				
			} catch (Exception e) {
				response.getWriter()
						.println("<div style='color:red'>There was an error in retrieving the CMIS Session</div>");
				// TODO: handle exception
				response.getWriter().println(e.getMessage());
				e.printStackTrace(response.getWriter());
			}
		}

		return cmisSession;
	}

	// convert InputStream to String
	private static String getStringFromInputStream(HttpServletResponse response, InputStream is) throws IOException {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace(response.getWriter());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace(response.getWriter());
				}
			}
		}

		return sb.toString();

	}

}
