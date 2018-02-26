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

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.harvard.i2b2.crc.loader.datavo.loader.query.LoadDataResponseType;

public class StatusContentProvider implements IStructuredContentProvider {

	//@Override
	public Object[] getElements(Object inputElement) {
		@SuppressWarnings("unchecked")
		List<LoadDataResponseType> terms = (List<LoadDataResponseType>) inputElement;
		return terms.toArray();
	}

//	@Override
	public void dispose() {
	}

//	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}



}
