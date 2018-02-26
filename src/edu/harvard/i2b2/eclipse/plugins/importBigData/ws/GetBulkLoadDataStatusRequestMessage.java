/*
* Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 	    Raj Kuttan
 * 		Lori Phillips
 */

package edu.harvard.i2b2.eclipse.plugins.importBigData.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.crcclient.datavo.i2b2message.*;
import edu.harvard.i2b2.crc.loader.datavo.loader.query.GetBulkLoadInfoRequestType;


/**
 * @author Lori Phillips
 *
 */
public class GetBulkLoadDataStatusRequestMessage extends CrcRequestData {
	
	public static final String THIS_CLASS_NAME = GetBulkLoadDataStatusRequestMessage.class.getName();
    private Log log = LogFactory.getLog(THIS_CLASS_NAME);	

	public GetBulkLoadDataStatusRequestMessage() {}
	
	/**
	 * Function to build GetUploadInfoRequestType
	 * 
	 * @return GetUploadInfoRequestType object
	 */
	public GetBulkLoadInfoRequestType getBulkLoadInfoRequestType() { 
		GetBulkLoadInfoRequestType requestType = new GetBulkLoadInfoRequestType();		
		return requestType;
	}
	
	
	/**
	 * Function to build body type for GetUploadInfoRequest
	 * 
	 * @param 
	 * @return BodyType object
	 */
	
	public BodyType getBodyType() {
		GetBulkLoadInfoRequestType requestType = getBulkLoadInfoRequestType();
		edu.harvard.i2b2.crc.loader.datavo.loader.query.ObjectFactory of = new edu.harvard.i2b2.crc.loader.datavo.loader.query.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createGetBulkLoadInfoRequest(requestType));
		return bodyType;
	}
	
	/**
	 * Function to build body type for GetUploadInfoRequest
	 * 
	 * @param 
	 * @return BodyType object
	 */
	
	public BodyType getBodyType(GetBulkLoadInfoRequestType requestType) {
		edu.harvard.i2b2.crc.loader.datavo.loader.query.ObjectFactory of = new edu.harvard.i2b2.crc.loader.datavo.loader.query.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createGetBulkLoadInfoRequest(requestType));
		return bodyType;
	}

	/**
	 * Function to build Request message type and return it as an XML string
	 * 
	 * @param GetUploadInfoRequestType request
	 * @return A String data type containing the RequestMessage in XML format
	 */
	public String doBuildXML(GetBulkLoadInfoRequestType request){ 
		String requestString = null;
			try {
				MessageHeaderType messageHeader = getMessageHeader(); 
				RequestHeaderType reqHeader  = getRequestHeader();
				BodyType bodyType = getBodyType(request) ;
				RequestMessageType reqMessageType = getRequestMessageType(messageHeader,
						reqHeader, bodyType);
				requestString = getXMLString(reqMessageType);
			} catch (JAXBUtilException e) {
				log.error(e.getMessage());
			} 
		return requestString;
	}
}


	
	