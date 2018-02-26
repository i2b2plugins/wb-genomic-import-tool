/*
 * Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.status.model;


public class StatusMessage {

	private String message = null;

	
	private static StatusMessage thisInstance;
	static {
		thisInstance = new StatusMessage();
	}

	public static StatusMessage getInstance() {
		return thisInstance;
	}

	public void setMessage(String msg){
		message = msg;
	}
	
	public String getMessage(){
		return message;
	}
	
	public void clear(){
		message = "";
	}
	
}
