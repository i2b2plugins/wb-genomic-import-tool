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
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.status.model.UploadIdList;



/**
 * This class demonstrates how to create your own dialog classes. It allows users
 * to input a String
 */
public class OpenStatusWindowDialog extends Dialog {

	private static final Log log = LogFactory.getLog(OpenStatusWindowDialog.class);
	private Composite parentComp = null;

	/**
	 * InputDialog constructor
	 * 
	 * @param parent the parent
	 */
	public OpenStatusWindowDialog(Shell parent) {
		super(parent);
		
	}

	/**
	 * Creates the dialog's contents
	 * 
	 */
	@Override
	protected Control createDialogArea(Composite parent){
    	Composite comp = (Composite)super.createDialogArea(parent);
    	comp.getShell().setText("Open Bulk Load Status Option");
    	
       	GridLayout layout = (GridLayout)comp.getLayout();
    	layout.numColumns = 2;

    	Label label = new Label(comp, SWT.RIGHT);
    	label.setText("All data has been converted and sent to bulk loader for processing \n"
    					+ "Would you like to view the load status table?");
    
  
	    return parent;
	}
	
	@Override
	protected void buttonPressed(int buttonId){

		String message = "";

		if(buttonId == 0){
			
				IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
				IWorkbenchPage[] pages = windows[0].getPages();
		
		//		IViewReference[] list =  pages[0].getViewReferences();
				
				final ViewPart bulkLoadStatusView = (ViewPart) pages[0].findView("edu.harvard.i2b2.eclipse.plugins.importBigData.status.bulkLoadStatusView");			
				if (bulkLoadStatusView != null){
					pages[0].activate(bulkLoadStatusView);
					TableComposite.getInstance().printWorkingMessage();
					TableComposite.getInstance().getBulkLoadStatus(UploadIdList.getInstance().getList(), UserInfoBean.getInstance().getUserName()).start();
				}
				else{
					
					BulkLoadStatusView view = new BulkLoadStatusView();
					try {
						pages[0].activate(view);
						pages[0].showView("edu.harvard.i2b2.eclipse.plugins.importBigData.status.bulkLoadStatusView");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						log.error("Problem creating Bulk Load Status View " + e.getMessage());
						close();
						return;
					}
					TableComposite.getInstance().printWorkingMessage();
					TableComposite.getInstance().getBulkLoadStatus(UploadIdList.getInstance().getList(), UserInfoBean.getInstance().getUserName()).start();
				}
			
			close();
		}
		//Cancel
		else if(buttonId ==1) {
			close();
		}
	}
	
}

