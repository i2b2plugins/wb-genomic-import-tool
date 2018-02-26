/*
* Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Raj Kuttan
 * 		Lori Phillips
 */

package edu.harvard.i2b2.eclipse.plugins.importBigData.ws;

import java.io.StringWriter;
import javax.xml.bind.JAXBElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.crc.loader.datavo.loader.query.LoadDataListResponseType;
import edu.harvard.i2b2.crc.loader.datavo.loader.query.LoadDataResponseType;
import edu.harvard.i2b2.crc.loader.datavo.loader.query.LoadType;
import edu.harvard.i2b2.crcclient.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcclient.datavo.i2b2message.ResponseHeaderType;
import edu.harvard.i2b2.crcclient.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.crcclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.eclipse.plugins.importBigData.util.ImportBigDataJAXBUtil;

public class CrcResponseData {

	public static final String THIS_CLASS_NAME = CrcResponseData.class.getName();
    private Log log = LogFactory.getLog(THIS_CLASS_NAME);	
    private ResponseMessageType respMessageType = null;
    
	public CrcResponseData() {}
	
	public StatusType processResult(String response){	
		StatusType status = null;
		try {
			JAXBElement jaxbElement = ImportBigDataJAXBUtil.getJAXBUtil().unMashallFromString(response);
			respMessageType  = (ResponseMessageType) jaxbElement.getValue();
			
			// Get response message status 
			ResponseHeaderType responseHeader = respMessageType.getResponseHeader();
			status = responseHeader.getResultStatus().getStatus();
			String procStatus = status.getType();
			String procMessage = status.getValue();
			
			if(procStatus.equals("ERROR")){
				log.error("Error reported by CRC web Service " + procMessage);				
			}
			else if(procStatus.equals("WARNING")){
				log.error("Warning reported by CRC web Service" + procMessage);
			}	
			
		} catch (JAXBUtilException e) {
			log.error(e.getMessage());
		}
		return status;
	}
/*
	public BulkLoadDataListResponseType doReadBulkLoadList(){
		BulkLoadDataListResponseType status = null;
		try {
			BodyType bodyType = respMessageType.getMessageBody();
			JAXBUnWrapHelper helper = new JAXBUnWrapHelper(); 
			if(bodyType != null)
		        status = (BulkLoadDataListResponseType) helper.getObjectByClass(bodyType.getAny(),
                        	BulkLoadDataListResponseType.class);				
				//loads = (LoadDataResponseType)helper.getObjectByClass(bodyType.getAny(), LoadType.class);
		} catch (JAXBUtilException e) {
			log.error(e.getMessage());;
		}
		return status;		
	}	
	*/
	
	public LoadDataResponseType doReadLoad(){
		LoadDataResponseType status = null;
		try {
			BodyType bodyType = respMessageType.getMessageBody();
			JAXBUnWrapHelper helper = new JAXBUnWrapHelper(); 
			if(bodyType != null)
		        status = (LoadDataResponseType) helper.getObjectByClass(bodyType.getAny(),
                        	LoadDataResponseType.class);				
				//loads = (LoadDataResponseType)helper.getObjectByClass(bodyType.getAny(), LoadType.class);
		} catch (JAXBUtilException e) {
			log.error(e.getMessage());;
		}
		return status;		
	}	
	
	public LoadDataListResponseType doReadLoadList(){
		LoadDataListResponseType loads = null;
		try {
			BodyType bodyType = respMessageType.getMessageBody();
			JAXBUnWrapHelper helper = new JAXBUnWrapHelper(); 
			if(bodyType != null)
		        loads = (LoadDataListResponseType) helper.getObjectByClass(bodyType.getAny(),
                        	LoadDataListResponseType.class);				
				//loads = (LoadDataResponseType)helper.getObjectByClass(bodyType.getAny(), LoadType.class);
		} catch (JAXBUtilException e) {
			log.error(e.getMessage());;
		}
		return loads;		
	}	
	
	
	public String getXMLString(LoadType load)throws Exception{ 
		StringWriter strWriter =  new StringWriter();
		try {
			ImportBigDataJAXBUtil.getJAXBUtil().marshaller(load, strWriter);
		} catch (JAXBUtilException e) {
			log.error("Error marshalling load info");
			throw new JAXBUtilException(e.getMessage(), e);
		} 
		return strWriter.toString();
	}
}

	
