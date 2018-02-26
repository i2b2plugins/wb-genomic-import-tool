/*
 * Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 	     Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.importBigData.status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;

import edu.harvard.i2b2.crc.loader.datavo.loader.query.GetUploadInfoRequestType;
import edu.harvard.i2b2.crc.loader.datavo.loader.query.LoadDataResponseType;
import edu.harvard.i2b2.crcclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.eclipse.plugins.importBigData.ws.CrcResponseData;
import edu.harvard.i2b2.eclipse.plugins.importBigData.ws.CrcServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.status.model.StatusContentProvider;
import edu.harvard.i2b2.eclipse.plugins.status.model.StatusLabelProvider;

import java.util.ArrayList;
import java.util.List;

public class TableComposite 
{
  private Log log = LogFactory.getLog(TableComposite.class.getName());	
  private Table table;
  private TableViewer viewer;
  private List statusList = new ArrayList();
  private static TableComposite instance;
  private List<String> uploadIds;
  
  private TableComposite(Composite parent)
  {
	  viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		createColumns(viewer);
		viewer.setContentProvider(new StatusContentProvider());
		viewer.setLabelProvider(new StatusLabelProvider(viewer, Display.getCurrent()));
//		if(uploadIds != null)
//			process(uploadIds, user_id);
	 
  }
  
	private void createColumns(TableViewer viewer) {

		String[] titles = {"Upload Id", "Status","Rows Processed", "Rows Loaded", "Date Loaded", "File Name"};
		int[] bounds = {75, 175, 100, 100, 100, 300};

		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			column.getColumn().setText(titles[i]);
			column.getColumn().setWidth(bounds[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);
		
		}
		table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		
		GridData gridData1 = new GridData(GridData.FILL_BOTH);
		gridData1.grabExcessVerticalSpace = true;
		gridData1.heightHint = 400;
		table.setLayoutData(gridData1);
		
	}

	public static void setInstance(Composite composite) {
		instance = new TableComposite(composite);
	}

	/**
	 * Function to return the TableComposite instance
	 * 
	 * @return  TableComposite object
	 */
	public static TableComposite getInstance() {
		return instance;
	}
  
	/*
	public void process(String uploadIds, String user_id){
		int numberOfRequests = uploadIds.size();
		
		for (int i = 0; i < numberOfRequests; i++) {
			getBulkLoadStatus(uploadIds.get(i), user_id).start();
		}
			
		
	}
  */
	
  public void refresh()
  {
	  this.viewer.getTable().clearAll();
	  this.viewer.refresh();
	  this.viewer.setInput(statusList);
	  this.viewer.refresh();
  }
  
  
  public void printWorkingMessage(){
		statusList.clear();
		LoadDataResponseType status = new LoadDataResponseType();
		status.setLoadStatus("working ... ");
		statusList.add(status);
		refresh();
  }
  
  
  public void printEmptyMessage(String upload_id){
		statusList.clear();
		LoadDataResponseType status = new LoadDataResponseType();
		if(upload_id != null)
			status.setUploadId(upload_id);
		status.setLoadStatus("No records were returned ... ");
		statusList.add(status);
		refresh();
}
    
  public Thread getBulkLoadStatus(String uploadId, String user_id){

	  GetUploadInfoRequestType bulkLoadRequest = new GetUploadInfoRequestType();
	  if((uploadId.contains(",")))  // used to look up a list of ids; change to get by UserId if there is a list.
		  bulkLoadRequest.setLoadId(null);
	  else
	  	  bulkLoadRequest.setLoadId(uploadId);
	  bulkLoadRequest.setUserId(user_id);

	  final Display theDisplay = Display.getCurrent();
	  final GetUploadInfoRequestType theStatusRequest = bulkLoadRequest;
	  return new Thread() {
		  @Override
		  public void run(){
			  try {
				  CrcResponseData msg = new CrcResponseData();
				  StatusType procStatus = null;	
				  while(procStatus == null || !procStatus.getType().equals("DONE")){
					  String response = CrcServiceDriver.getUploadDataStatusRequest(theStatusRequest);
					  
					  log.info(response);
					  procStatus = msg.processResult(response);

					  if (procStatus.getType().equals("ERROR")){		
						  System.setProperty("errorMessage",  procStatus.getValue());
						  statusList.clear();
						  theDisplay.syncExec(new Runnable() {
								
								public void run() {
									MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
									mBox.setText("Please Note ...");
									mBox.setMessage("Error in attempt to get load status. \n" +
											"Please try again.");
									int result = mBox.open();
								}
							});
						  return;
					  }		
					  // msg may be a list or a single entry
					  LoadDataResponseType answer = msg.doReadLoad();
					  if(answer == null){
						  // see if a a list was returned
						  List<LoadDataResponseType> answerList = msg.doReadLoadList().getLoadDataResponse();
						  if(answerList.isEmpty())
							  printEmptyMessage(theStatusRequest.getLoadId());
						  else{
							  statusList.clear();
							  statusList.addAll(answerList);
						  }
					  }
					  
					  if(answer.getStartDate() == null){
						  printEmptyMessage(theStatusRequest.getLoadId());
						  
					  }
					  else{
						  statusList.clear();
						  statusList.add(answer);
					  }
				  }
				 

			  } catch (Exception e) {
				  log.error("Get Load Status error");					
			  }

			  theDisplay.syncExec(new Runnable() {
				  public void run() {
					  TableComposite.getInstance().refresh();
				  }
			  });
		  }
	  };


  }


  

 public List<LoadDataResponseType> getList(){
	 return statusList;
 }
  
 
/* 
 private void parseMaps(String allMaps){
	 
	 
	 list.clear();
	 BufferedReader in = new BufferedReader(new StringReader(allMaps));
	 try {
		String line = in.readLine();
		while (line != null){
			String[] cols = line.split(";");
			MapType map = new MapType();
			if((!(cols[0] == null)) && (cols[0].trim().equals("null")))
				cols[0] = "";
			map.setSourceBasecode(cols[0]);
			if((!(cols[1] == null)) && (cols[1].trim().equals("null")))
				cols[1] = "";
			map.setSourceName(cols[1]);
			if((!(cols[2] == null)) && (cols[2].trim().equals("null")))
				cols[2] = "";
			map.setDestinationBasecode(cols[2]);
			if((!(cols[3] == null)) && (cols[3].trim().equals("null")))
				cols[3] = "";
			map.setDestinationName(cols[3]);

			list.add(map);
			
			line = in.readLine();
		}
		
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
 }
 */
 
} 







