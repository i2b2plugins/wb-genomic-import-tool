/*
 * Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.importBigData.status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import edu.harvard.i2b2.eclipse.UserInfoBean;



/**
 * This class demonstrates how to create your own dialog classes. It allows users
 * to input a String
 */
public class SearchByUploadIdDialog extends Dialog {

	private static final Log log = LogFactory.getLog(SearchByUploadIdDialog.class);

	private String uploadIdText = null;


	/**
	 * InputDialog constructor
	 * 
	 * @param parent the parent
	 */
	public SearchByUploadIdDialog(Shell parent) {
		super(parent);
		
	}

	/**
	 * Creates the dialog's contents
	 * 
	 */
	@Override
	protected Control createDialogArea(Composite parent){
    	Composite comp = (Composite)super.createDialogArea(parent);
    	comp.getShell().setText("Search by Upload ID");
    	
       	GridLayout layout = (GridLayout)comp.getLayout();
    	layout.numColumns = 2;

    	Label label = new Label(comp, SWT.RIGHT);
    	label.setText("Upload ID");
    	GridData findComboData = new GridData (GridData.FILL_HORIZONTAL);
    	findComboData.widthHint = 150;
    	findComboData.horizontalSpan = 1;
   
    	
	    
		   // Then set up the Find text combo box    
	    final Text findText = new Text(comp, SWT.DROP_DOWN);

		findText.setLayoutData(findComboData);
	    findText.addModifyListener(new ModifyListener() {
	    	public void modifyText(ModifyEvent e) {	    
	    		// Text Item has been entered
	    		// Does not require 'return' to be entered
	    		uploadIdText = findText.getText();
	    	}
	    });
	    
	    findText.addSelectionListener(new SelectionListener(){
	    	public void widgetSelected(SelectionEvent e) {
	    	}
	    	public void widgetDefaultSelected(SelectionEvent e) {
	    		uploadIdText = findText.getText();
	    		
	    	}
	    });
	    
  
	    return parent;
	}
	
	@Override
	protected void buttonPressed(int buttonId){

		String message = "";
	//	int maxTerms = -1;
		// OK
		if(buttonId == 0){

			// call TableComposite and refresh
			TableComposite.getInstance().printWorkingMessage();
			TableComposite.getInstance().getBulkLoadStatus(uploadIdText, UserInfoBean.getInstance().getUserName()).start();
			close();
		}
		//Cancel
		else if(buttonId ==1) {
			close();
		}
	}
	
}

