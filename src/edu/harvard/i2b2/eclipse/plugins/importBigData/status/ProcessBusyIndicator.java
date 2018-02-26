/*
* Copyright (c) 2006-2013 Partners Healthcare 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * This source code was developed as part of i2b2 for the 
 * Medical Imaging Informatics Bench to Beside project (mi2b2).
 * 
 * Contributors: Taowei David Wang 
 */


package edu.harvard.i2b2.eclipse.plugins.importBigData.status;




import org.eclipse.swt.SWT;

import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.swt.widgets.Label;

import edu.harvard.i2b2.eclipse.plugins.importBigData.util.Colors;
import edu.harvard.i2b2.eclipse.plugins.importBigData.util.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.status.model.StatusMessage;

public class ProcessBusyIndicator extends Composite{
	
    public static final int    WIDTH  = 190;
    public static final int    HEIGHT = 100;
    
    private static final String MSG   = "";
    private static final String DOTS = ".....";
    
    private Label        myLabel;
    private int          myCounter = 0;
    
    private boolean canceled = false;
    
    public ProcessBusyIndicator( Composite parent, int style )
    { 
           super( parent, style );
           setup();
    }
    
    public void updateText() 
    {
           myLabel.setText( StatusMessage.getInstance().getMessage() + "\n" + DOTS.substring(0, myCounter+1 ));
           myCounter = (myCounter+1) % DOTS.length();                    
    }

    
    private void setup()
    {                   

           this.setBackground(Colors.DARK_DARK_GRAY );
           this.setLayout( new FormLayout() );
           myLabel = new Label( this, SWT.CENTER | SWT.WRAP );
           myLabel.setBackground( Colors.DARK_DARK_GRAY );
           myLabel.setForeground( Colors.LIGHT_LIGHT_GRAY );
    //       GridData data = new GridData();
          
           this.myLabel.setLayoutData( FormDataMaker.makeFormData( 0, 4, 100, -2, 0, 4, 100, -4) );
           myLabel.setText("Start me up \n \n \n");            
    }             

    public boolean isCanceled(){
    	return canceled;
    }
    
    public void cancel(){
    	canceled = true;
   // 	this.setVisible(false);
    }
    
}
