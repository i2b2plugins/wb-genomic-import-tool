/*
* Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 */

package edu.harvard.i2b2.eclipse.plugins.importBigData.ws;

import java.io.StringReader;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;


import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.crc.loader.datavo.loader.query.BulkLoadRequestType;
import edu.harvard.i2b2.crc.loader.datavo.loader.query.GetUploadInfoRequestType;
import edu.harvard.i2b2.crc.loader.datavo.loader.query.PublishDataRequestType;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.importBigData.util.MessageUtil;


public class CrcServiceDriver {

	public static final String THIS_CLASS_NAME = CrcServiceDriver.class.getName();
    private static Log log = LogFactory.getLog(THIS_CLASS_NAME);
    private static String serviceURL = 
    	//"http://infra3.mgh.harvard.edu:9090/i2b2/rest/CRCLoaderService/";
    	UserInfoBean.getInstance().getCellDataUrl("CRC");
	
	private static EndpointReference soapEPR = new EndpointReference(serviceURL);
	
	private static EndpointReference bulkLoadRequestEPR = new EndpointReference(
			serviceURL + "bulkLoadRequest");

	private static EndpointReference publishDataRequestEPR = new EndpointReference(
			serviceURL + "publishDataRequest");

	
	private static EndpointReference loadDataStatusRequestEPR = new EndpointReference(
		serviceURL + "getLoadDataStatusRequest");
	
	
	
    public static OMElement getVersion() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://axisversion.sample/xsd", "tns");

        OMElement method = fac.createOMElement("getVersion", omNs);

        return method;
    }
	
	/**
	 * Function to send bulkLoad requestVdo to CRC web service
	 * 
	 * @param BulkLoadRequestType  info about the data we are loading
	 * @return A String containing the CRC web service response 
	 */
	
	public static String bulkLoad(BulkLoadRequestType request) throws Exception{
		String response = null;
		
			 try {
				 BulkLoadRequestMessage reqMsg = new BulkLoadRequestMessage();

				 String bulkLoadRequestString = reqMsg.doBuildXML(request);
				 log.debug(bulkLoadRequestString);
				 				
					 response = sendREST(bulkLoadRequestEPR, bulkLoadRequestString);
				 
			 } catch (Exception e) {
				log.error(e.getMessage());
				throw new Exception(e);
			}
		return response;
	}
	
	/**
	 * Function to send getPublishDataRequest requestVdo to CRC web service
	 * 
	 * @param GetPublishDataRequest  parentNode we wish to get data for
	 * @return A String containing the CRC web service response 
	 */
	
	public static String getUploadDataStatusRequest(GetUploadInfoRequestType request) throws Exception{
		String response = null;
		
			 try {
				 GetLoadDataStatusRequestMessage reqMsg = new GetLoadDataStatusRequestMessage();

				 String getStatusRequestString = reqMsg.doBuildXML(request);
				 log.info(getStatusRequestString);
				 				 
				
					 response = sendREST(loadDataStatusRequestEPR, getStatusRequestString);
				 
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new Exception(e);
			}
		return response;
	}
    
	/**
	 * Function to send getPublishDataRequest requestVdo to CRC web service
	 * 
	 * @param GetPublishDataRequest  parentNode we wish to get data for
	 * @return A String containing the CRC web service response 
	 */
	
	public static String getPublishDataRequest(PublishDataRequestType publishRequest) throws Exception{
		String response = null;
		
			 try {
				 GetPublishDataRequestMessage reqMsg = new GetPublishDataRequestMessage();

				 String publishRequestString = reqMsg.doBuildXML(publishRequest);
				 log.debug(publishRequestString);
				 				 
			/*	 if(System.getProperty("webServiceMethod").equals("SOAP")) {
					 response = sendSOAP(publishRequestString, "http://rpdr.partners.org/GetChildren", "GetChildren", type );
				 }
			*/
				
				response = sendREST(publishDataRequestEPR, publishRequestString);
				 
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new Exception(e);
			}
		return response;
	}
	

	/**
	 * Function to convert CRC request to OMElement
	 * 
	 * @param requestVdo   String requestVdo to send to CRC web service
	 * @return An OMElement containing the CRC web service requestVdo
	 */
	public static OMElement getCrcPayLoad(String requestVdo) throws Exception {
		OMElement lineItem  = null;
		try {
			StringReader strReader = new StringReader(requestVdo);
			XMLInputFactory xif = XMLInputFactory.newInstance();
			XMLStreamReader reader = xif.createXMLStreamReader(strReader);

			StAXOMBuilder builder = new StAXOMBuilder(reader);
			lineItem = builder.getDocumentElement();
		} catch (FactoryConfigurationError e) {
			log.error(e.getMessage());
			throw new Exception(e);
		}
		return lineItem;
	}
	

	
	public static String sendREST(EndpointReference restEPR, String requestString) throws Exception{	
		OMElement getCrc = getCrcPayLoad(requestString);
		
		Options options = new Options();
		log.debug(restEPR.toString());
		options.setTo(restEPR);
		options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

		options.setProperty(Constants.Configuration.ENABLE_REST, Constants.VALUE_TRUE);
		options.setProperty(HTTPConstants.SO_TIMEOUT,new Integer(125000));
		options.setProperty(HTTPConstants.CONNECTION_TIMEOUT,new Integer(125000));

		ServiceClient sender = ImportServiceClient.getServiceClient();
		sender.setOptions(options);

		MessageUtil.getInstance().setRequest("URL: " + restEPR + "\n" + requestString);
		OMElement result = sender.sendReceive(getCrc);
		String response = result.toString();
		
		
//		MessageUtil.getInstance().setRequest("URL: " + restEPR + "\n" + requestString);
        MessageUtil.getInstance().setResponse("URL: " + restEPR + "\n" + response);
		return response;

	}
	
	public static String sendSOAP(String requestString, String action, String operation, String type) throws Exception{	

		ServiceClient sender = ImportServiceClient.getServiceClient();
		OperationClient operationClient = sender
				.createClient(ServiceClient.ANON_OUT_IN_OP);

		// creating message context
		MessageContext outMsgCtx = new MessageContext();
		// assigning message context's option object into instance variable
		Options opts = outMsgCtx.getOptions();
		// setting properties into option
		log.debug(soapEPR);
		opts.setTo(soapEPR);
		opts.setAction(action);
		opts.setTimeOutInMilliSeconds(180000);
		
		log.debug(requestString);

		SOAPEnvelope envelope = null;
		
		try {
			SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
			envelope = fac.getDefaultEnvelope();
			OMNamespace omNs = fac.createOMNamespace(
					"http://rpdr.partners.org/",                                   
					"rpdr");

			
			// creating the SOAP payload
			OMElement method = fac.createOMElement(operation, omNs);
			OMElement value = fac.createOMElement("RequestXmlString", omNs);
			value.setText(requestString);
			method.addChild(value);
			envelope.getBody().addChild(method);
		}
		catch (FactoryConfigurationError e) {
			log.error(e.getMessage());
			throw new Exception(e);
		}
 
		outMsgCtx.setEnvelope(envelope);
		
	
		operationClient.addMessageContext(outMsgCtx);
		operationClient.execute(true);
		
		
		MessageContext inMsgtCtx = operationClient.getMessageContext("In");
		SOAPEnvelope responseEnv = inMsgtCtx.getEnvelope();
		
		OMElement soapResponse = responseEnv.getBody().getFirstElement();
		
	
			//	String formattedResponse = XMLUtil.StrFindAndReplace("&lt;", "<", responseEnv.toString());
			//	String indentedResponse = XMLUtil.convertDOMToString(XMLUtil.convertStringToDOM(formattedResponse) );
			
		
		OMElement soapResult = soapResponse.getFirstElement();

		String i2b2Response = soapResult.getText();
		log.debug(i2b2Response);
		MessageUtil.getInstance().setRequest("URL: " + soapEPR + "\n" + requestString);
        MessageUtil.getInstance().setResponse("URL: " + soapEPR + "\n" + i2b2Response);

		return i2b2Response;		
	}
	public static String publishDataRequest(PublishDataRequestType request) throws Exception{
		String response = null;
		
			 try {
				 PublishDataRequestMessage reqMsg = new PublishDataRequestMessage();

				 String bulkLoadRequestString = reqMsg.doBuildXML(request);
				 log.debug(bulkLoadRequestString);
				 				
					 response = sendREST(publishDataRequestEPR, bulkLoadRequestString);
				 
			 } catch (Exception e) {
				log.error(e.getMessage());
				throw new Exception(e);
			}
		return response;
	}
	
}
