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
import edu.harvard.i2b2.crc.loader.datavo.loader.query.PublishDataRequestType;


/**
 * @author Lori Phillips
 *
 */
public class PublishDataRequestMessage extends CrcRequestData {
	
	public static final String THIS_CLASS_NAME = PublishDataRequestMessage.class.getName();
    private Log log = LogFactory.getLog(THIS_CLASS_NAME);	


	public PublishDataRequestMessage() {}
	
	/**
	 * Function to build bulk load request type for a given request
	 * 
	 * @return BulkLoadRequestType object
	 */
	public PublishDataRequestType publishDataRequestType() { 
		PublishDataRequestType bulkLoadRequestType = new PublishDataRequestType();		
		return bulkLoadRequestType;
	}
	
	
	/**
	 * Function to build vocabData body type
	 * 
	 * @param 
	 * @return BodyType object
	 */
	
	public BodyType getBodyType() {
		PublishDataRequestType bulkLoadRequestType = publishDataRequestType();
		edu.harvard.i2b2.crc.loader.datavo.loader.query.ObjectFactory of = new edu.harvard.i2b2.crc.loader.datavo.loader.query.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createPublishDataRequest(bulkLoadRequestType));
		return bodyType;
	}
	
	/**
	 * Function to build vocabData body type
	 * 
	 * @param 
	 * @return BodyType object
	 */
	
	public BodyType getBodyType(PublishDataRequestType bulkLoadRequestType) {
		edu.harvard.i2b2.crc.loader.datavo.loader.query.ObjectFactory of = new edu.harvard.i2b2.crc.loader.datavo.loader.query.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createPublishDataRequest(bulkLoadRequestType));
		return bodyType;
	}

	/**
	 * Function to build CRC Request message type and return it as an XML string
	 * 
	 * @param BulkLoadRequestType bulkLoadData 
	 * @return A String data type containing the CRC RequestMessage in XML format
	 */
	public String doBuildXML(PublishDataRequestType bulkLoadData){ 
		String requestString = null;
			try {
				MessageHeaderType messageHeader = getMessageHeader(); 
				RequestHeaderType reqHeader  = getRequestHeader();
				BodyType bodyType = getBodyType(bulkLoadData) ;
				RequestMessageType reqMessageType = getRequestMessageType(messageHeader,
						reqHeader, bodyType);
				requestString = getXMLString(reqMessageType);
			} catch (JAXBUtilException e) {
				log.error(e.getMessage());
			} 
		return requestString;
	}
}


	
	