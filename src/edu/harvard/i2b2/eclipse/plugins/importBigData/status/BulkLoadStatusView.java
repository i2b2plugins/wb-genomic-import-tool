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

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;




/**
 * The Bulk Load Status View class provides the status View to the
 *  Eclipse framework  --- 
 * @author Lori Phillips   
 */

public class BulkLoadStatusView extends ViewPart {


	public static final String ID = "edu.harvard.i2b2.eclipse.plugins.importBigData.status.bulkLoadStatusView";
	
	
	public static final String THIS_CLASS_NAME = BulkLoadStatusView.class.getName();
	
	//setup context help
//	public static final String PREFIX = "edu.harvard.i2b2.eclipse.plugins.mappingViewer";
//	public static final String MAPPING_VIEW_CONTEXT_ID = PREFIX + ".mapping_viewer_view_help_context";
	
	private Composite	statusComposite;	
	private Log log = LogFactory.getLog(THIS_CLASS_NAME);


	/**
	 * The constructor.
	 */
	public BulkLoadStatusView() {
		super();
	}
	
	/**
	 * The constructor.
	
	public BulkLoadStatusView(String uploadIds, Composite comp) {
		
	//	this.uploadIds = uploadIds;
		createPartControl(comp);
		if(TableComposite.getInstance() == null)
			TableComposite.setInstance(comp);
		TableComposite.getInstance().getBulkLoadStatus(uploadIds, "deid_user").start();
	}
	 */
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */

	@Override
	public void createPartControl(Composite parent) {
		log.info("Bulk Load Status View version 1.7.0");
	//	MappingViewerJAXBUtil.getJAXBUtil().printMessage();
				
		SashForm sf = new SashForm(parent, SWT.HORIZONTAL);
		
		// Mapping tree
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.horizontalSpacing = 1;
		gridLayout.verticalSpacing = 1;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;

		GridData gridData = new GridData (GridData.FILL_BOTH);
		gridData.widthHint = 300;

		
		// Status table
		statusComposite = new Composite(sf, SWT.NULL);
		statusComposite.setLayout(gridLayout);
		statusComposite.setLayoutData(gridData);		
		
		SearchComposite search = new SearchComposite(statusComposite);
				
		TableComposite.setInstance(statusComposite);
		
		
//		getUploadStatus("119");
		
		

	}

/*	//add help button
	private void addHelpButtonToToolBar() {
		final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
		Action helpAction = new Action(){
			@Override
			public void run() {
				helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.ontology/html/i2b2_navigate_terms_index.htm");
		}
		};
		helpAction.setImageDescriptor(ImageDescriptor.createFromFile(MappingToolView.class, "/icons/help.png"));
		getViewSite().getActionBars().getToolBarManager().add(helpAction);
	}
*/
	
	/**
	 * Passing the focus request 
	 */
	@Override
	public void setFocus() {
		statusComposite.setFocus();
	}
	/*
	public void getUploadStatus(String uploadId){
		try {
			GetBulkLoadInfoRequestType bulkLoadRequest = new GetBulkLoadInfoRequestType();
			bulkLoadRequest.setLoadId(uploadId);
			bulkLoadRequest.setUserId("demo_deid");
		
			
			CrcResponseData msg = new CrcResponseData();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				String response = CrcServiceDriver.getBulkLoadDataStatusRequest(bulkLoadRequest);
				log.info(response);
					procStatus = msg.processResult(response);

					//				else if  other error codes
					//				TABLE_ACCESS_DENIED and USER_INVALID and DATABASE ERRORS
					if (procStatus.getType().equals("ERROR")){		
						System.setProperty("errorMessage",  procStatus.getValue());				
						
						return;
					}		
					
				}

			} catch (AxisFault e) {
				log.error(e.getMessage());
				
			} catch (Exception e) {
				log.error(e.getMessage());
				
			}
			
		}
*/
	
	
	
}
