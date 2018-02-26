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
import org.eclipse.swt.SWT;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.status.model.UploadIdList;


public class SearchComposite extends Composite
{
	private static final Log log = LogFactory.getLog(SearchComposite.class);

	private Composite comp = null;

  
  public SearchComposite(Composite parent)
  {
    super(parent, SWT.NONE);  // used to be SWT.NULL
    comp = parent;
    populateControl();
  }

  protected void populateControl()
  {
    GridLayout compositeLayout = new GridLayout(1, false);
    setLayout(compositeLayout);

 //   Label title = new Label(comp, SWT.BOLD);
//    title.setText("Terms to map");
    
 //  new Label(comp, SWT.NONE);
    
    Group group = new Group(comp, SWT.NONE);
    group.setText("Bulk Load Status");
    
    GridLayout groupLayout = new GridLayout(5, false);
  //  groupLayout.makeColumnsEqualWidth = true;
    group.setLayout(groupLayout);
    
    GridData gridData = new GridData (GridData.FILL_HORIZONTAL);
//	gridData.grabExcessHorizontalSpace = true; 
//	gridData.grabExcessVerticalSpace = true; 
	group.setLayoutData(gridData);
    
    
//    Button codingSystem = new Button(group, SWT.PUSH);
//    codingSystem.setText("Search By Coding System");
  
    
 //   Button all = new Button(group, SWT.PUSH);
 //   all.setText("Get All");
    
 //   GridData gridDataAll = new GridData (GridData.FILL_HORIZONTAL);
//	gridDataAll.grabExcessHorizontalSpace = true; 
//	gridDataAll.horizontalSpan = 5;
 //   all.setLayoutData(gridDataAll);

    
    GridData gridDataCode = new GridData (GridData.FILL_HORIZONTAL);
	gridDataCode.widthHint = 180;
   
    GridData gridDataName = new GridData (GridData.FILL_HORIZONTAL);
//	gridDataAll.grabExcessHorizontalSpace = true; 
	gridDataName.widthHint = 200;
   

    
    Button uploadIdSearch = new Button(group, SWT.PUSH);
    uploadIdSearch.setText("Search By Upload ID");
    uploadIdSearch.setLayoutData(gridDataCode);
    uploadIdSearch.setToolTipText("Search by Upload ID");
    
    Button userIdSearch = new Button(group, SWT.PUSH);
    userIdSearch.setText("Search By User ID");
    userIdSearch.setLayoutData(gridDataName);
    userIdSearch.setToolTipText("Search by User ID");
    
    Button refresh = new Button(group, SWT.PUSH);
    refresh.setText("Refresh");
    refresh.setLayoutData(gridDataName);
    refresh.setToolTipText("Refresh");
 
/*
    all.addSelectionListener(new SelectionListener(){
    	public void widgetSelected(SelectionEvent e) {
    		// Item in list has been selected
    		TableComposite.getInstance().printWorkingMessage();
    		TableComposite.getInstance().update(new GetTermsType()).start();
		}
    	public void widgetDefaultSelected(SelectionEvent e) {
    		// this is not an option (text cant be entered)
    	}
    });
*/
    uploadIdSearch.addSelectionListener(new SelectionListener(){
    	public void widgetSelected(SelectionEvent e) {
    		// Item in list has been selected
    		SearchByUploadIdDialog getName = new SearchByUploadIdDialog(Display.getCurrent().getActiveShell());
    		getName.open();	    	
    	}
    	public void widgetDefaultSelected(SelectionEvent e) {
    		// this is not an option (text cant be entered)
    	}
    });

    userIdSearch.addSelectionListener(new SelectionListener(){
    	public void widgetSelected(SelectionEvent e) {
    		// run query.
    		SearchByUserIdDialog getUser = new SearchByUserIdDialog(Display.getCurrent().getActiveShell());
    		getUser.open();
    		
    //		TableComposite.getInstance().printWorkingMessage();
    //		TableComposite.getInstance().getBulkLoadStatus(null, UserInfoBean.getInstance().getUserName()).start();	    	
    	}
    	public void widgetDefaultSelected(SelectionEvent e) {
    		// this is not an option (text cant be entered)
    	}
    });
    
    refresh.addSelectionListener(new SelectionListener(){
    	public void widgetSelected(SelectionEvent e) {
    		// run query.
    		TableComposite.getInstance().printWorkingMessage();
    		TableComposite.getInstance().getBulkLoadStatus(UploadIdList.getInstance().getList(), UserInfoBean.getInstance().getUserName()).start();	    	
    	}
    	public void widgetDefaultSelected(SelectionEvent e) {
    		// this is not an option (text cant be entered)
    	}
    });

  }

 

  
} 

