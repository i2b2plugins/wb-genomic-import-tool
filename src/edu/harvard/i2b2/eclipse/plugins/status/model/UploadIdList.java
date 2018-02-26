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


public class UploadIdList {

	private String uploadIds = null;

	
	private static UploadIdList thisInstance;
	static {
		thisInstance = new UploadIdList();
	}

	public static UploadIdList getInstance() {
		return thisInstance;
	}

	public void add(String id){
		if(uploadIds == null)
			uploadIds = id;
		else
			uploadIds += "," + id;
	}
	
	public String getList(){
		return uploadIds;
	}
	
	public void clear(){
		uploadIds = null;
	}
	
}
