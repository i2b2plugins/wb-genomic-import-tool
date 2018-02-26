/*
* Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 *     Lori Phillips - initial API and implementation
 */
package edu.harvard.i2b2.eclipse.plugins.status.model;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import edu.harvard.i2b2.crc.loader.datavo.loader.query.LoadDataResponseType;

public class StatusLabelProvider  extends LabelProvider implements	ITableLabelProvider {
	
	public static final Class THIS_CLASS = StatusLabelProvider.class;	

	private TableViewer viewer;
	private Display display;
	
	public StatusLabelProvider(TableViewer viewer, Display display) {
		this.viewer = viewer;
		this.display = display;
	}
	
	
//	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// In case you don't like image just return null here

		return null;
	}

//	@Override
	public String getColumnText(Object element, int columnIndex) {
		LoadDataResponseType status = (LoadDataResponseType) element;
		switch (columnIndex) {

		case 0:
			return status.getUploadId();
		case 1:
			return status.getLoadStatus();
		case 2:
			if(status.getObservationSet() == null)
//			if(status.getInputFileName() == null)
				return null;
			else
				return String.valueOf(status.getObservationSet().getTotalRecord());
		//		return String.valueOf(status.getNumberOfRecords());
		case 3:
			if(status.getObservationSet() == null)
//			if(status.getInputFileName() == null)
				return null;
			else
				return String.valueOf(status.getObservationSet().getInsertedRecord());
			//	return String.valueOf(status.getLoadedRecords());
		case 4:
			if(status.getStartDate() != null){
			return 
				status.getStartDate().getMonth() + "/" 
				+ status.getStartDate().getDay() + "/" 
				+ status.getStartDate().getYear();
			}
			
		case 5:
			if(status.getDataFileLocationUri() == null)
				return null;
			else
				return status.getDataFileLocationUri().getValue();
//			return status.getInputFileName();	

		default:
			throw new RuntimeException("Should not happen");
		}

	}
}
