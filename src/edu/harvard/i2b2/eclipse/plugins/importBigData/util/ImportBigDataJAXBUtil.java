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
package edu.harvard.i2b2.eclipse.plugins.importBigData.util;



public class ImportBigDataJAXBUtil {

	private static edu.harvard.i2b2.common.util.jaxb.JAXBUtil jaxbUtil = null;
	
	private ImportBigDataJAXBUtil() { 
	}
	
	
	public static edu.harvard.i2b2.common.util.jaxb.JAXBUtil getJAXBUtil() {
		if (jaxbUtil == null) {
			jaxbUtil = new edu.harvard.i2b2.common.util.jaxb.JAXBUtil(edu.harvard.i2b2.eclipse.plugins.importBigData.util.JAXBConstant.DEFAULT_PACKAGE_NAME);
		}
		return jaxbUtil;
	}
	
	
}
