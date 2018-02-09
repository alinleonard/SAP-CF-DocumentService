package com.cerner.document_service_proxybridge;

import com.sap.ecm.api.AbstractCmisProxyServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CMISProxyServlet
 */

public class CMISProxyServlet extends AbstractCmisProxyServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see AbstractCmisProxyServlet#AbstractCmisProxyServlet()
     */
    public CMISProxyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
	protected boolean supportAtomPubBinding() {
		return true;
	}
    
    @Override
	protected boolean supportBrowserBinding() {
		return true;
	}

	@Override
	protected String getRepositoryKey() {
		// TODO Auto-generated method stub
		return Config.UNIQUE_KEY;
	}

	@Override
	protected String getRepositoryUniqueName() {
		// TODO Auto-generated method stub
		return Config.UNIQUE_NAME;
	}

}
