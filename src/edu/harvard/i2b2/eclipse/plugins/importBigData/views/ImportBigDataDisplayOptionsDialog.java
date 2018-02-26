/*
 * Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 	     Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.importBigData.views;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


/****
 * CURRENTLY NOT USED
 * EXAMPLE ONLY IF NEEDED IN FUTURE
 */



/**
 *
 * @author  lcp5
 */
public class ImportBigDataDisplayOptionsDialog extends Dialog {
    
	private Text genVersion;
	private Text genInterval;
	
    /**
     * Creates new  DisplayOptionsDialog
     */
    public ImportBigDataDisplayOptionsDialog(Shell parentShell) {
    	super(parentShell);
    	
    }
      	
    @Override
	protected Control createDialogArea(Composite parent){
    	Composite comp = (Composite)super.createDialogArea(parent);
    	comp.getShell().setText("Genomics Import Options");

       	GridLayout layout = (GridLayout)comp.getLayout();
    	layout.numColumns = 2;
    	
    	Label genVersionLabel = new Label(comp, SWT.RIGHT);
    	genVersionLabel.setText("Genome version: ");
    	genVersion = new Text(comp, SWT.SINGLE);
    	GridData data = new GridData(GridData.FILL_HORIZONTAL);
    	genVersion.setLayoutData(data);
    	genVersion.setText(System.getProperty("GenVersion"));
    	
		Label intervalLabel = new Label(comp, SWT.RIGHT);
    	intervalLabel.setText("Genomic interval (bps): ");
    	genInterval = new Text(comp, SWT.SINGLE);
    	genInterval.setLayoutData(data);
		genInterval.setText(System.getProperty("GenInterval"));
    	
    	return parent;
    }  
    
    @Override
	protected void createButtonsForButtonBar(Composite parent){
    	super.createButtonsForButtonBar(parent);
    	createButton(parent, 2, "Reset to Defaults", false);
    }

    @Override
	protected void buttonPressed(int buttonId){	
    	// OK
    	if(buttonId == 0){
    		
    		String message = "";
    		try{
    			Integer.parseInt(genInterval.getText());
    		}catch(java.lang.NumberFormatException e){
    			message = message + "Genomic interval should be an integer \n";
    		}
    		
    		if(!message.equals("")){
    			MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), 
						SWT.ICON_ERROR);
				mBox.setText("Please Note ...");
				mBox.setMessage(message);
				mBox.open();
    			return;
    		}
    		
    		System.setProperty("GenVersion", genVersion.getText());
    		System.setProperty("GenInterval", genInterval.getText());

        	close();
    	}
    	//Cancel
    	else if(buttonId ==1) {
    		close();
    	}
    	
    	// reset
    	else if(buttonId == 2){
    		System.setProperty("GenVersion", "hg19");
    		System.setProperty("GenInterval", "500");

    		genVersion.setText(System.getProperty("GenVersion"));
    		genInterval.setText(System.getProperty("GenInterval"));
  
    		
    	}
    }
    
 }
