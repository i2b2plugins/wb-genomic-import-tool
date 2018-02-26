/*
 * Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 * 		Mike Mendis
 * 		Janice Donahoe (documentation for on-line help)
 */
package edu.harvard.i2b2.eclipse.plugins.importBigData.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import edu.harvard.i2b2.crc.loader.datavo.loader.query.BulkLoadRequestType;
import edu.harvard.i2b2.crc.loader.datavo.loader.query.DataFormatType;
import edu.harvard.i2b2.crc.loader.datavo.loader.query.DataListType;
import edu.harvard.i2b2.crc.loader.datavo.loader.query.DataListType.LocationUri;
import edu.harvard.i2b2.crc.loader.datavo.loader.query.InputOptionListType;
import edu.harvard.i2b2.crc.loader.datavo.loader.query.LoadDataResponseType;
import edu.harvard.i2b2.crc.loader.datavo.loader.query.PublishDataRequestType;
import edu.harvard.i2b2.crcclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.eclipse.plugins.importBigData.mapReader.MapFileReader;
import edu.harvard.i2b2.eclipse.plugins.importBigData.status.OpenStatusWindowDialog;
import edu.harvard.i2b2.eclipse.plugins.importBigData.status.ProcessBusyIndicator;
import edu.harvard.i2b2.eclipse.plugins.importBigData.util.StringUtil;
import edu.harvard.i2b2.eclipse.plugins.importBigData.ws.CrcResponseData;
import edu.harvard.i2b2.eclipse.plugins.importBigData.ws.CrcServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.status.model.StatusMessage;
import edu.harvard.i2b2.eclipse.plugins.status.model.UploadIdList;
import edu.harvard.i2b2.gvf2i2b2.gvf2i2b2;

@SuppressWarnings("restriction")
public class ImportBigDataView extends ViewPart {

	public static final String ID = "edu.harvard.i2b2.eclipse.plugins.importBigData.views.importBigDataView";
	public static final String THIS_CLASS_NAME = ImportBigDataView.class.getName();

	//setup context help
	public static final String PREFIX = "edu.harvard.i2b2.eclipse.plugins.importBigData";
	public static final String FIND_VIEW_CONTEXT_ID = PREFIX + ".view_help_context";

	private Log log = LogFactory.getLog(THIS_CLASS_NAME);

	private String genVersion= "hg18";
	private String fileFormat = "VCF";
	private String patientNum;
	private String encounterNum;
	private String startDate;
	private String inputFilename;
	private String mapFilename;

	private String sampleID;
	private String sampleType = "TISSUE";
	private String anatomicalSource = "Pericardium";
	private String collectionMethod = "BIOPSY";
	private String additive = "UNKNOWN";

	private String tumor = "TUMOR";
	private String tumorGrade = "UNKNOWN";
	private String tumorStage = "UNKNOWN";

	private ScrolledForm form;

	private ProgressBar progressBar = null;
	private Label step1Header = null;
	private Label step1Status = null;
	private Label step2Header = null;
	private Label step2Status = null;
	private Label conversionStatus = null;
	
//    private final ScheduledExecutorService   myDBIndicatorScheduler = Executors.newScheduledThreadPool( 1, new DaemonThreadFactory() );
 //   private ScheduledFuture<?>  myDBIndicator = null;

 	
	/**
	 * The constructor.
	 */
	public ImportBigDataView() {


	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		log.info("Genomics Import plugin version 1.7.0");
	
//		Composite top = new Composite(parent, SWT.NONE);
//			top.setLayout( new FormLayout() );
/*		FormData mainFD 	= new FormData();
		
		mainFD.top		= new FormAttachment(0);
		mainFD.bottom	= new FormAttachment(100);
		mainFD.left		= new FormAttachment(0);
		mainFD.right		= new FormAttachment(100);
//		top.setLayoutData( mainFD );
*/
	    FormToolkit toolkit = new FormToolkit(parent.getDisplay());

		form = toolkit.createScrolledForm(parent);
		form.setText("Import NGS Variant Data");
		// Composite for storing the data
		final Composite body = form.getBody();
	
		TableWrapLayout wrapLayout = new TableWrapLayout();
		wrapLayout.numColumns = 2;
		body.setLayout(wrapLayout);
//		body.setLayout(FormLayoutFactory.createFormTableWrapLayout(true, 2));

		TableWrapLayout wrapLayout2 = new TableWrapLayout();
		wrapLayout2.numColumns = 2;
		
	
	
		Composite left = toolkit.createComposite(body);
//		left.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 2));
		left.setLayout(wrapLayout2);
		left.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 1;

		TableWrapData td2 = new TableWrapData(TableWrapData.FILL_GRAB);
		td2.colspan = 2;

		Section section = toolkit.createSection(left, Section.DESCRIPTION
				| Section.TITLE_BAR | SWT.WRAP);

		section.setText("Analysis details"); //$NON-NLS-1$
		section.setDescription("Information related to the NGS data");
		section.setLayoutData(td2);

		TableWrapLayout wrapLayout3 = new TableWrapLayout();
		wrapLayout3.numColumns = 2;
		Composite right = toolkit.createComposite(body);
		right.setLayout(wrapLayout3);
		//		right.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 2));
		right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));


		GridLayout layout2 = new GridLayout(2, false);
		layout2.verticalSpacing = 15;
		layout2.marginWidth = 10;
		layout2.marginHeight = 10;

		GridData gridData = new GridData (GridData.FILL_HORIZONTAL);
		gridData.widthHint = 200;
		gridData.heightHint = 25;
		gridData.horizontalSpan = 1;

		GridData gridData2 = new GridData (GridData.FILL_HORIZONTAL);
		gridData2.widthHint = 200;
		gridData2.horizontalSpan = 2;


		Composite genomicComp = toolkit.createComposite(section, SWT.WRAP);
		genomicComp.setLayout(layout2);

		section.setClient(genomicComp);

		toolkit.createLabel(genomicComp, "Specify input file:");

		Composite fileComp = toolkit.createComposite(genomicComp, SWT.WRAP);
		fileComp.setLayout(layout2);

		
		final Text fileText = toolkit.createText(fileComp,"", SWT.SINGLE | SWT.BORDER);
		fileText.setLayoutData(gridData);
		fileText.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				inputFilename = fileText.getText();
			}
		});

		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {	    
				// Text Item has been entered
				// Does not require 'return' to be entered
				inputFilename = fileText.getText();
			}
		});
		
		final Button browseButton = toolkit.createButton(fileComp,"Browse", SWT.NONE);
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {

				FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
				String[] filterExt = { "*.*" }; //$NON-NLS-1$ 
				fd.setFilterExtensions(filterExt);
			
				String returnVal = fd.open();

				if(returnVal != null) { //== JFileChooser.APPROVE_OPTION) {
					fileText.setText(returnVal);
					inputFilename = returnVal;
					if(inputFilename.charAt(1) == (':')){
						
						MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), 
								SWT.ICON_INFORMATION | SWT.OK);
						mBox.setText("Please Note ...");
						mBox.setMessage("Input file name contains a mapped drive ("+ inputFilename.charAt(0)						
							+	":)\n The bulk loader may not be able to resolve this location."
						+	"\n  Its better to specify the full path.");
						mBox.open();
					}
				}				
			}
		});


		toolkit.createLabel(genomicComp, "Input file format:", SWT.WRAP);
		final Combo formatCombo = new Combo(genomicComp, SWT.DROP_DOWN);
		formatCombo.add("VCF-ANNOVAR");
		formatCombo.add("VCF");
		formatCombo.add("GVF");
		formatCombo.add("I2B2");
		formatCombo.setText("VCF");
		formatCombo.setLayoutData(gridData);

		toolkit.createLabel(genomicComp, "VCF mapping file:", SWT.WRAP);	   

		Composite fileComp2 = toolkit.createComposite(genomicComp, SWT.WRAP);
		fileComp2.setLayout(layout2);

		
		final Text mapFileText = toolkit.createText(fileComp2,"", SWT.SINGLE | SWT.BORDER);
		mapFileText.setLayoutData(gridData);
		mapFileText.setEnabled(true);

		mapFileText.addSelectionListener(new SelectionListener(){
			
			public void widgetSelected(SelectionEvent e) {
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				mapFilename = mapFileText.getText();
			}
		});

		mapFileText.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {	    
				// Text Item has been entered
				// Does not require 'return' to be entered
				mapFilename = mapFileText.getText();
			}
		});
		
		final Button browseButton2 = toolkit.createButton(fileComp2,"Browse", SWT.NONE);

		browseButton2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {

				FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
				String[] filterExt = { "*.*" }; //$NON-NLS-1$ 
				fd.setFilterExtensions(filterExt);
			
				String returnVal = fd.open();

				if(returnVal != null) { //== JFileChooser.APPROVE_OPTION) {
					mapFileText.setText(returnVal);
					mapFilename = returnVal;

	
				}				
			}
		});
		
		
		toolkit.createLabel(genomicComp, "I2B2 Patient number:", SWT.WRAP);	   
		final Text patNumText = toolkit.createText(genomicComp,"", SWT.SINGLE | SWT.BORDER);
		patNumText.setLayoutData(gridData);
		patNumText.setEnabled(false);

		toolkit.createLabel(genomicComp, "I2B2 Encounter number:", SWT.RIGHT);	   	
		final Text encNumText = toolkit.createText(genomicComp,"", SWT.SINGLE | SWT.BORDER);
		encNumText.setLayoutData(gridData);
		encNumText.setEnabled(false);

		toolkit.createLabel(genomicComp, "Date of encounter:", SWT.RIGHT);	   
		final Text startText = toolkit.createText(genomicComp,"", SWT.SINGLE | SWT.BORDER);
		startText.setMessage("YYYY-MM-DD format");
		startText.setLayoutData(gridData);
		startText.setEnabled(false);


		toolkit.createLabel(genomicComp, "Reference genome version:", SWT.RIGHT);	   
		final Combo genVersionCombo = new Combo(genomicComp, SWT.DROP_DOWN);
		genVersionCombo.add("hg18");
		genVersionCombo.add("hg19");
		genVersionCombo.setText("hg18");
		genVersionCombo.setLayoutData(gridData);
		genVersionCombo.setEnabled(false);


		formatCombo.addSelectionListener(new SelectionListener(){
		
			public void widgetSelected(SelectionEvent e) {
				fileFormat = formatCombo.getText();
				if(fileFormat.equals("I2B2")){
					patNumText.setEnabled(false);
					encNumText.setEnabled(false);
					startText.setEnabled(false);
					genVersionCombo.setEnabled(false);
					mapFileText.setEnabled(false);
				}
				else if(fileFormat.equals("GVF")){
					patNumText.setEnabled(true);
					encNumText.setEnabled(true);
					startText.setEnabled(true);
					genVersionCombo.setEnabled(true);
					mapFileText.setEnabled(false);
				}
				else if(fileFormat.contains("VCF")){
					patNumText.setEnabled(false);
					encNumText.setEnabled(false);
					startText.setEnabled(false);
					genVersionCombo.setEnabled(false);
					mapFileText.setEnabled(true);
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

	


		patNumText.addSelectionListener(new SelectionListener(){
			
			public void widgetSelected(SelectionEvent e) {
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				patientNum = patNumText.getText();
			}
		});
		patNumText.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {	    
				// Text Item has been entered
				// Does not require 'return' to be entered
				patientNum = patNumText.getText();
			}
		});


		encNumText.addSelectionListener(new SelectionListener(){
			
			public void widgetSelected(SelectionEvent e) {
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				encounterNum = encNumText.getText();
			}
		});

		encNumText.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {	    
				// Text Item has been entered
				// Does not require 'return' to be entered
				encounterNum = encNumText.getText();
			}
		});

		startText.addSelectionListener(new SelectionListener(){
			
			public void widgetSelected(SelectionEvent e) {
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				startDate = startText.getText();
			}
		});

		startText.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {	    
				// Text Item has been entered
				// Does not require 'return' to be entered
				startDate = startText.getText();
			}
		});


		genVersionCombo.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {	    
				// Text Item has been entered
				// Does not require 'return' to be entered
				genVersion = genVersionCombo.getText();
			}
		});

		genVersionCombo.addSelectionListener(new SelectionListener(){
			
			public void widgetSelected(SelectionEvent e) {
				genVersion = genVersionCombo.getText();
				if(genVersionCombo.indexOf(genVersion) < 0) {
					genVersionCombo.add(genVersion);
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				genVersion = genVersionCombo.getText();
				if(genVersionCombo.indexOf(genVersion) < 0) {
					genVersionCombo.add(genVersion);
				}
			}
		});



		toolkit.createLabel(genomicComp, "", SWT.RIGHT);	   
		toolkit.createLabel(genomicComp, "", SWT.RIGHT);
		toolkit.createLabel(genomicComp, "", SWT.RIGHT);

		final Button start = toolkit.createButton(genomicComp,"", SWT.PUSH);
		start.setText("Submit");
		start.setLayoutData(gridData);

		toolkit.createLabel(genomicComp, "                 Progress Bar:", SWT.LEFT);
		progressBar = new ProgressBar(genomicComp, SWT.NONE);
		progressBar.setLayoutData(gridData);
		progressBar.setMaximum(100);

		step1Header = new Label(genomicComp, SWT.RIGHT);
		step1Header.setLayoutData(gridData);
		step1Header.setText("");
		
		conversionStatus = new Label(genomicComp, SWT.RIGHT);
		conversionStatus.setLayoutData(gridData);
		conversionStatus.setText("");
		
		step2Header = new Label(genomicComp, SWT.RIGHT);
		step2Header.setLayoutData(gridData);
		step2Header.setText("");
		
		step2Status = new Label(genomicComp, SWT.RIGHT);
		step2Status.setLayoutData(gridData);
		step2Status.setText("");
		
	//	final Composite overlayComposite = new Composite( top, SWT.NONE);
	//	overlayComposite.setLayout( new FormLayout() );
	//	overlayComposite.setVisible( false );
		
	//	FormData overlayFD = new FormData();
	//	overlayFD.top = new FormAttachment( 50, -overlayComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT ).y/2 ); // centered vertically
	//	overlayFD.left = new FormAttachment( 50, -overlayComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT ).x/2 ); // centered horizontally
	//	overlayComposite.setLayoutData( overlayFD );

		
	//	final ProcessBusyIndicator processBusyIndicator = new ProcessBusyIndicator(overlayComposite, SWT.NONE);
	//	processBusyIndicator.setLayoutData(mainFD);
		//	processBusyIndicator.setVisible(false);
		
	//	overlayComposite.setBackground(Colors.DARK_GRAY);
		
		
		start.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				
		/*		if(1==1){
					progressMonitor();
					return;
				}
		*/		
				
				progressBar.setSelection(0);
				step2Header.setText("");
				step2Status.setText("");
				if((inputFilename != null) && (inputFilename.length() > 0)){
					File f = new File(inputFilename);
					if(f.exists()){
						if(fileFormat.equals("I2B2")){
							startBulkLoad(inputFilename).start();
							conversionStatus.setText("I2B2 file complete");
							progressBar.setSelection(100);
							OpenStatusWindowDialog dialog = new OpenStatusWindowDialog(Display.getCurrent().getActiveShell());
							dialog.open();
						}
						else if(fileFormat.equals("GVF")) {  /// conversion type == gvf 2 i2b2
							if((patientNum == null) || (patientNum.length() == 0)){
								// print message that no patient num
								MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), 
										SWT.ICON_INFORMATION | SWT.OK);
								mBox.setText("Please Note ...");
								mBox.setMessage("An I2B2 patient number must be supplied");
								mBox.open();
							}
							else if((encounterNum == null) || (encounterNum.length() == 0)){
								// print message that no enc num
								MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), 
										SWT.ICON_INFORMATION | SWT.OK);
								mBox.setText("Please Note ...");
								mBox.setMessage("An I2B2 encounter number must be supplied");
								mBox.open();
							}
							else if((startDate == null) || (startDate.length() == 0)){
								// print message that no start date
								MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), 
										SWT.ICON_INFORMATION | SWT.OK);
								mBox.setText("Please Note ...");
								mBox.setMessage("A start date must be supplied");
								mBox.open();

							}
							// check for correct format
							//"YYYY-MM-DD format"
							else if(isDateValid(startDate) == false){
								// print message that no start date
								MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), 
										SWT.ICON_INFORMATION | SWT.OK);
								mBox.setText("Please Note ...");
								mBox.setMessage("A start date format such as '2000-01-22' must be supplied");
								mBox.open();

							}
							else if((genVersion == null) || (genVersion.length() == 0)){
								// print message that no genomic version
								MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), 
										SWT.ICON_INFORMATION | SWT.OK);
								mBox.setText("Please Note ...");
								mBox.setMessage("A genomic version must be supplied");
								mBox.open();

							}

							else{
								
								try {

									progressBar.setSelection(50);
									step1Header.setText("GVF to I2B2 step:  ");
									conversionStatus.setText("GVF to i2b2 conversion in progress.");
									//		conversionStatus.redraw();
									log.info("GVF to i2b2 conversion in progress..");	
					/*				overlayComposite.setVisible(true);
								//	processBusyIndicator.setVisible(true);
									myDBIndicator = myDBIndicatorScheduler.scheduleAtFixedRate( new Runnable()
									{
										@Override
										public void run() 
										{
											Display.getDefault().asyncExec( new Runnable()
											{
												@Override
												public void run() 
												{
													if ( processBusyIndicator.isCanceled() )
														return;
													processBusyIndicator.updateText();
												}
											});
										}
									},0, 250, TimeUnit.MILLISECONDS );
						*/		//	myDBIndicator.cancel(true);

									
									startGVFProcessing(inputFilename, conversionStatus, progressBar, null).start();
									

								} catch (Exception e1) {
									conversionStatus.setText("GVF to i2b2 conversion failed.");
									
								//	processBusyIndicator.cancel();
									
								}

							}

						}
						else if(fileFormat.contains("VCF")) {  /// conversion type == vcf 2 gvf 2 i2b2
							// first validate the map file name
							if((mapFilename != null) && (mapFilename.length() > 0)){
								File mf = new File(mapFilename);
								if (!(mf.exists())){
									// print message that no file name present

									MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), 
											SWT.ICON_INFORMATION | SWT.OK);
									mBox.setText("Please Note ...");
									mBox.setMessage("A valid map file name must be supplied");
									mBox.open();
									return;
								}
							}	
							String path = StringUtil.getDirectoryPath(inputFilename);
							if(fileFormat.equals("VCF")){
								step1Header.setText("VCF to GVF step:  ");
								//step1Header.setText("              VCF to GVF step:");
								try {
									startVCFProcessing(inputFilename, mapFilename, conversionStatus, step2Header, step2Status, progressBar).start();
								} catch (Exception e1) {
									conversionStatus.setText("Error in VCF file processing");
									MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), 
											SWT.ICON_INFORMATION | SWT.OK);
									mBox.setText("Please Note ...");
									mBox.setMessage("Error in VCF file processing");
									mBox.open();

								}
							}
							if(fileFormat.equals("VCF-ANNOVAR")){
								 			
								step1Header.setText("VCF ANNOVAR to GVF step:  ");
								try {
									startANNOVARProcessing(inputFilename, mapFilename, conversionStatus, step2Header, step2Status, progressBar).start();
								} catch (Exception e1) {
									conversionStatus.setText("Error in VCF-ANNOVAR file processing");
									MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), 
											SWT.ICON_INFORMATION | SWT.OK);
									mBox.setText("Please Note ...");
									mBox.setMessage("Error in VCF-ANNOVAR file processing");
									mBox.open();

								}
							}

						}
					}else{
						// print message that no file name present

						MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), 
								SWT.ICON_INFORMATION | SWT.OK);
						mBox.setText("Please Note ...");
						mBox.setMessage("A valid input file name must be supplied");
						mBox.open();
					}
				}
				else{
					// print message that no file name present

					MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), 
							SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("An input file name must be supplied");
					mBox.open();
				}


			}

		});

		Section section2 = toolkit.createSection(right, Section.DESCRIPTION
				| Section.TITLE_BAR | SWT.WRAP);

		section2.setText("Sample details"); //$NON-NLS-1$
		section2.setDescription("Information related to the sample");
		section2.setLayoutData(td2);

		Composite sampleComp = toolkit.createComposite(section2, SWT.WRAP);
		sampleComp.setLayout(layout2);

		section2.setClient(sampleComp);

		toolkit.createLabel(sampleComp, "Sample ID:", SWT.RIGHT);	   

		final Text sampleIDText = toolkit.createText(sampleComp,"", SWT.SINGLE | SWT.BORDER);
		sampleIDText.setLayoutData(gridData);
		sampleIDText.addSelectionListener(new SelectionListener(){
			
			public void widgetSelected(SelectionEvent e) {
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				sampleID = sampleIDText.getText();
			}
		});

		sampleIDText.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {	    
				// Text Item has been entered
				// Does not require 'return' to be entered
				sampleID = sampleIDText.getText();
			}
		});
		sampleIDText.setEnabled(false);
		
		toolkit.createLabel(sampleComp, "Sample Type:", SWT.RIGHT);	   

		final Combo sampleTypeCombo = new Combo(sampleComp, SWT.DROP_DOWN);
		sampleTypeCombo.add("TISSUE");
		sampleTypeCombo.setText("TISSUE");
		sampleTypeCombo.setLayoutData(gridData);

		sampleTypeCombo.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {	    
				// Text Item has been entered
				// Does not require 'return' to be entered
				sampleType = sampleTypeCombo.getText();
			}
		});

		sampleTypeCombo.addSelectionListener(new SelectionListener(){
			
			public void widgetSelected(SelectionEvent e) {
				sampleType = sampleTypeCombo.getText();
				if(sampleTypeCombo.indexOf(sampleType) < 0) {
					sampleTypeCombo.add(sampleType);
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				sampleType = sampleTypeCombo.getText();
				if(sampleTypeCombo.indexOf(sampleType) < 0) {
					sampleTypeCombo.add(sampleType);
				}
			}
		});

		sampleTypeCombo.setEnabled(false);
		
		toolkit.createLabel(sampleComp, "Anatomical Source:", SWT.RIGHT);	   

		final Combo anatomicalCombo = new Combo(sampleComp, SWT.DROP_DOWN);
		anatomicalCombo.add("Pericardium");
		anatomicalCombo.setText("Pericardium");
		anatomicalCombo.setLayoutData(gridData);

		anatomicalCombo.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {	    
				// Text Item has been entered
				// Does not require 'return' to be entered
				anatomicalSource = anatomicalCombo.getText();
			}
		});

		anatomicalCombo.addSelectionListener(new SelectionListener(){
			
			public void widgetSelected(SelectionEvent e) {
				anatomicalSource = anatomicalCombo.getText();
				if(anatomicalCombo.indexOf(anatomicalSource) < 0) {
					anatomicalCombo.add(anatomicalSource);
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				anatomicalSource = anatomicalCombo.getText();
				if(anatomicalCombo.indexOf(anatomicalSource) < 0) {
					anatomicalCombo.add(anatomicalSource);
				}
			}
		});
		anatomicalCombo.setEnabled(false);

		toolkit.createLabel(sampleComp, "Collection Method:", SWT.RIGHT);	   

		final Combo collectionCombo = new Combo(sampleComp, SWT.DROP_DOWN);
		collectionCombo.add("BIOPSY");
		collectionCombo.setText("BIOPSY");
		collectionCombo.setLayoutData(gridData);

		collectionCombo.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {	    
				// Text Item has been entered
				// Does not require 'return' to be entered
				collectionMethod = collectionCombo.getText();
			}
		});

		collectionCombo.addSelectionListener(new SelectionListener(){
			
			public void widgetSelected(SelectionEvent e) {
				collectionMethod = collectionCombo.getText();
				if(collectionCombo.indexOf(collectionMethod) < 0) {
					collectionCombo.add(collectionMethod);
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				collectionMethod = collectionCombo.getText();
				if(collectionCombo.indexOf(collectionMethod) < 0) {
					collectionCombo.add(collectionMethod);
				}
			}
		});
		collectionCombo.setEnabled(false);
		
		toolkit.createLabel(sampleComp, "Additive:", SWT.RIGHT);	   

		final Combo additiveCombo = new Combo(sampleComp, SWT.DROP_DOWN);
		additiveCombo.add("UNKNOWN");
		additiveCombo.setText("UNKNOWN");
		additiveCombo.setLayoutData(gridData);

		additiveCombo.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {	    
				// Text Item has been entered
				// Does not require 'return' to be entered
				additive = additiveCombo.getText();
			}
		});

		additiveCombo.addSelectionListener(new SelectionListener(){
			
			public void widgetSelected(SelectionEvent e) {
				additive = additiveCombo.getText();
				if(additiveCombo.indexOf(additive) < 0) {
					additiveCombo.add(additive);
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				additive = additiveCombo.getText();
				if(additiveCombo.indexOf(additive) < 0) {
					additiveCombo.add(additive);
				}
			}
		});
		additiveCombo.setEnabled(false);


		Section section3 = toolkit.createSection(right,  Section.DESCRIPTION | Section.TITLE_BAR | SWT.WRAP);

		section3.setText("Sample Pathology"); //$NON-NLS-1$
		section3.setDescription("Information related to the sample pathology");

		TableWrapData td3 = new TableWrapData(TableWrapData.FILL_GRAB);
		td3.colspan = 2;

		section3.setLayoutData(td3);

		Composite pathologyComp = toolkit.createComposite(section3, SWT.WRAP);
		pathologyComp.setLayout(layout2);

		section3.setClient(pathologyComp);


		toolkit.createLabel(pathologyComp, "Pathology:", SWT.RIGHT);	   

		final Combo pathologyCombo = new Combo(pathologyComp, SWT.DROP_DOWN);
		pathologyCombo.add("TUMOR");
		pathologyCombo.setText("TUMOR");
		pathologyCombo.setLayoutData(gridData);


		pathologyCombo.addSelectionListener(new SelectionListener(){
			
			public void widgetSelected(SelectionEvent e) {
				tumor = pathologyCombo.getText();
				if(pathologyCombo.indexOf(tumor) < 0) {
					pathologyCombo.add(tumor);
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				tumor = pathologyCombo.getText();
				if(pathologyCombo.indexOf(tumor) < 0) {
					pathologyCombo.add(tumor);
				}
			}
		});
		pathologyCombo.setEnabled(false);


		toolkit.createLabel(pathologyComp, "Tumor Grade", SWT.RIGHT);	   

		final Combo tumorGradeCombo = new Combo(pathologyComp, SWT.DROP_DOWN);
		tumorGradeCombo.add("G1");
		tumorGradeCombo.add("G2");
		tumorGradeCombo.add("G3");
		tumorGradeCombo.add("G4");
		tumorGradeCombo.add("GX");
		tumorGradeCombo.add("UNKNOWN");
		tumorGradeCombo.setText("UNKNOWN");
		tumorGradeCombo.setLayoutData(gridData);


		tumorGradeCombo.addSelectionListener(new SelectionListener(){
			
			public void widgetSelected(SelectionEvent e) {
				tumorGrade = tumorGradeCombo.getText();
				if(tumorGradeCombo.indexOf(tumorGrade) < 0) {
					tumorGradeCombo.add(tumorGrade);
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				tumorGrade = tumorGradeCombo.getText();
				if(tumorGradeCombo.indexOf(tumorGrade) < 0) {
					tumorGradeCombo.add(tumorGrade);
				}
			}
		});
		tumorGradeCombo.setEnabled(false);

		toolkit.createLabel(pathologyComp, "Tumor Stage:", SWT.RIGHT);	   


		final Combo tumorStageCombo = new Combo(pathologyComp, SWT.DROP_DOWN);
		tumorStageCombo.add("I");
		tumorStageCombo.add("II");
		tumorStageCombo.add("III");
		tumorStageCombo.add("IV");
		tumorStageCombo.add("UNKNOWN");
		tumorStageCombo.setText("UNKNOWN");
		tumorStageCombo.setLayoutData(gridData);


		tumorStageCombo.addSelectionListener(new SelectionListener(){
			
			public void widgetSelected(SelectionEvent e) {
				tumorStage = tumorStageCombo.getText();
				if(tumorStageCombo.indexOf(tumorStage) < 0) {
					tumorStageCombo.add(tumorStage);
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				tumorStage = tumorStageCombo.getText();
				if(tumorStageCombo.indexOf(tumorStage) < 0) {
					tumorStageCombo.add(tumorStage);
				}
			}
		});
		tumorStageCombo.setEnabled(false);

	}

	//
	// Passing the focus request
	//
	@Override
	public void setFocus() {
		form.setFocus();
	}


	//add help button
	/*	private void addHelpButtonToToolBar() {
		final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
		Action helpAction = new Action(){
			@Override
			public void run() {
				helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.ontology/html/i2b2_find_terms_index.htm");
		}
		};
		helpAction.setImageDescriptor(ImageDescriptor.createFromFile(ImportBigDataView.class, "/icons/help.png"));
		getViewSite().getActionBars().getToolBarManager().add(helpAction);
	}
	 */

	private boolean isDateValid(String date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		System.out.println(date);
		try{
			dateFormat.parse(date);
			return true;
		}catch(ParseException e){

			System.out.println(e.getMessage());
			return false;
		}


	}


	public Thread startGVFProcessing(String fileName, Label status, ProgressBar progress, ProcessBusyIndicator busy){
		final Display theDisplay = Display.getCurrent();
		final String theFileName = fileName;
		final Label theStatus = status;
		final ProgressBar theProgressBar = progress;
//		final ProcessBusyIndicator theBusy = busy;
		return new Thread() {
			@Override
			public void run(){
				try {

				//	new gvf2i2b2(theFileName, patientNum, encounterNum, startDate, genVersion);

					Thread running = gvf2i2b2(theFileName, patientNum, encounterNum, startDate, genVersion);
					running.setName("running");
					running.start();
					sleep(2000);   //allows gvf2i2b2 to start up  before we try to read the log file.
					
					BufferedReader gvfLogReader = null;
					try {
						gvfLogReader = new BufferedReader(new FileReader(theFileName + ".i2b2log"));
						while ((running != null) && (running.isAlive()) && (running.getName().equals("running"))){
							String line = gvfLogReader.readLine();
							while(line != null){
								final String theLine = line;
								theDisplay.syncExec(new Runnable() {
									
									public void run() {
										theStatus.setText(theLine);
									}

								});
								
								StatusMessage.getInstance().setMessage(line);
								line = gvfLogReader.readLine();
							}

						}
						gvfLogReader.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						log.info(theFileName + ".i2b2log not found");
						if(gvfLogReader != null)
							gvfLogReader.close();
						theDisplay.syncExec(new Runnable() {
							public void run() {
								
							theStatus.setText("GVF processing error; quitting");
								theProgressBar.setSelection(100);
							}		
						});
						return;
					}
					
					
					String i2b2Filename =  theFileName.replace("gvf", (encounterNum));
				//	bulkLoad(i2b2Filename+".i2b2", theDisplay);
				//	publishLoad(i2b2Filename+".i2b2", theDisplay);
				//	startBulkLoad(i2b2Filename+".i2b2", theDisplay).start();	
					theDisplay.syncExec(new Runnable() {
						
						public void run() {
						//	theBusy.cancel();
							theProgressBar.setSelection(100);
							theStatus.setText("GVF to I2B2 conversion complete");
							log.info("GVF to I2B2 conversion complete");
							
							OpenStatusWindowDialog dialog = new OpenStatusWindowDialog(theDisplay.getActiveShell());
							dialog.open();
							
						}
					});

				} catch (Exception e) {
					theDisplay.syncExec(new Runnable() {
						
						public void run() {
							MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
							mBox.setText("Please Note ...");
							mBox.setMessage("Error in GVF to I2B2 conversion process");
							int result = mBox.open();
						}
					});
								
				}
				theDisplay.syncExec(new Runnable() {
					
					public void run() {
						theProgressBar.setSelection(100);
						theStatus.setText("GVF to I2B2 conversion complete");
						log.info("GVF to I2B2 conversion complete");
					}
				});
			}
		};
	}

	public Thread startANNOVARProcessing(String fileName, String mapFileName, Label status, Label step2Header, Label step2Status, ProgressBar progress){
		final Display theDisplay = Display.getCurrent();
		final String theFileName = StringUtil.getFileNamePart(fileName);
		final Label theStatus = status;
		final Label theStep2 = step2Status;
		final Label theStep2Header = step2Header;
		final ProgressBar theProgressBar = progress;
		final String theMapFileName = mapFileName;
		final String thePath = StringUtil.getDirectoryPath(fileName);
		return new Thread() {
			@Override
			public void run(){
				try {

					String path = thePath.replace("\\", "/");
					File dir = new File(path);
					
					String filenamePart = theFileName;// StringUtil.getFileNamePart(theFileName);

					String perl = System.getenv("PERL_HOME");
					perl = perl.replace("\\", "/") + "/perl.exe";
					
					String workingDir = System.getProperty("user.dir");
					if(workingDir.contains("\\"))
						workingDir = workingDir.replace("\\","/");
					// standalone config
				//	workingDir = workingDir + "/dropins";	
					
	//		eclipse testing config				
					workingDir = workingDir.substring(0, workingDir.lastIndexOf("/")) + "/edu.harvard.i2b2.eclipse.plugins.genomicsImport";		

						
					
			//		String perlFile =  workingDir + "/perlScripts/vcfANNO2gvf.pl";
					String perlFile =  workingDir + "/perlScripts/vcfANNO_SUMM2gvf.pl";
					File pf = new File(perlFile);
					if (!(pf.exists())){
						theDisplay.syncExec(new Runnable() {
							
							public void run() {
								MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
								mBox.setText("Please Note ...");
								mBox.setMessage("Perl script vcfANNO_SUMM2gvf.pl not found");
								int result = mBox.open();
							}
						});
						return;		
					}
					
					String[] cmd = {perl,  perlFile, filenamePart , filenamePart.replace("vcf", "csv")}; 
			//		log.info(cmd[0]+cmd[1]+cmd[2] + cmd[3] + path);

					theDisplay.syncExec(new Runnable() {
						
						public void run() {
							theProgressBar.setSelection(10);
							theStatus.setText("Converting VCF-ANNOVAR to GVF.");
							log.info("vcfANNOVAR to GVF starting..");	
						}
					});


					Process p2 = Runtime.getRuntime().exec(cmd, null , dir);
					
					BufferedReader errorStream = new BufferedReader(new InputStreamReader
							(p2.getErrorStream()));
					
					BufferedReader inStream = new BufferedReader(new InputStreamReader
							(p2.getInputStream()));

					Thread running = waitForProcess(p2);
			//		log.error(errorStream.readLine());
					running.setName("running");
					running.start();
					log.error(errorStream.readLine());
					while ((running != null) && (running.isAlive()) && !(running.getName().equals("done"))){
						String line = errorStream.readLine();
						log.error(line);
				//		log.info(inStream.readLine());
						
			//			String line = inStream.readLine();
					
						if(line != null){
							
							if ((Integer.parseInt(line) % 1000) == 0){
								final String theLine = "Converting VCF line " + line;
								
								theDisplay.syncExec(new Runnable() {
									
									public void run() {
										theStatus.setText(theLine);
									}
								});
							}
//							line = errorStream.readLine();
						}
					}
					theDisplay.syncExec(new Runnable() {
						
						public void run() {
							theProgressBar.setSelection(50);
							theStatus.setText("VCF-ANNOVAR to GVF complete");
							log.info("vcfANNO2gvf done");
							//	startMapProcessing(thePath, theMapFileName, theProgressBar, theStatus).start(); 

						}
					});
					startMapProcessing(thePath, theMapFileName, theProgressBar, theStep2Header, theStep2, theDisplay).start(); 

				} catch (Exception e) {
					theDisplay.syncExec(new Runnable() {
						
						public void run() {
							MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
							mBox.setText("Please Note ...");
							mBox.setMessage("Error in VCF-ANNO to GVF conversion process");
							int result = mBox.open();
							
						}
					});
					return;		
				}
			

			}
		};
	}

	public Thread waitForProcess(Process perl){
		final Process p = perl;

		return new Thread() {
			@Override
			public void run(){
				try {
					setName("running");
					p.waitFor();
					setName("done");
					return;
				} catch (InterruptedException e) {
					setName("done");
					p.destroy();
					return;
				}
			}
		};
	}


	public Thread startVCFProcessing(String fileName, String mapFileName, Label status, Label step2Header, Label step2Status, ProgressBar progress){
		final Display theDisplay = Display.getCurrent();
		final String theFileName = fileName;
		final Label theStatus = status;
		final Label theStep2 = step2Status;
		final Label theStep2Header = step2Header;
		final ProgressBar theProgressBar = progress;
		final String theMapFileName = mapFileName;
		final String thePath = StringUtil.getDirectoryPath(fileName);
		return new Thread() {
			@Override
			public void run(){
				try {
					String path = StringUtil.getDirectoryPath(inputFilename);
					File dir = new File(path);
					String filenamePart = StringUtil.getFileNamePart(inputFilename);

					String perl = System.getenv("PERL_HOME") + "/perl.exe";
					
					String workingDir = System.getProperty("user.dir");
					if(workingDir.contains("\\"))
						workingDir = workingDir.replace("\\","/");
					
//					eclipse testing config				
		//			workingDir = workingDir.substring(0, workingDir.lastIndexOf("/")) + "/edu.harvard.i2b2.eclipse.plugins.genomicsImport";		

					
					//standalone config		
					workingDir = workingDir + "/dropins";		
					
					
					String perlFile =  workingDir + "/perlScripts/vcf2gvf.pl";
					File pf = new File(perlFile);
					if (!(pf.exists())){
						theDisplay.syncExec(new Runnable() {
							
							public void run() {
								MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
								mBox.setText("Please Note ...");
								mBox.setMessage("Perl script vcf2gvf.pl not found");
								int result = mBox.open();
							}
						});
						return;		
					}
					
					String[] cmd = {perl,  perlFile, filenamePart}; 
					
					theDisplay.syncExec(new Runnable() {
						
						public void run() {
							theProgressBar.setSelection(10);
							theStatus.setText("Converting VCF to GVF.");
							log.info("VCF to GVF starting..");	
						}
					});
					
					Process p2 = Runtime.getRuntime().exec(cmd, null , dir);
					BufferedReader inStream = new BufferedReader(new InputStreamReader
							(p2.getInputStream()));

					Thread running = waitForProcess(p2);
					running.setName("running");
					running.start();
					while ((running != null) && (running.isAlive()) && !(running.getName().equals("done"))){
						String line = inStream.readLine();
						while(line != null){
						//	if ((Integer.parseInt(line) % 1000) == 0){
								final String theLine = "Converting VCF line " + line;

								theDisplay.syncExec(new Runnable() {
									
									public void run() {
										theStatus.setText(theLine);
									}
								});
						//	}
							line = inStream.readLine();
						}
					}
				
				
					theDisplay.syncExec(new Runnable() {
						
						public void run() {
							theProgressBar.setSelection(50);
							theStatus.setText("VCF to GVF complete");
							log.info("vcf2gvf done");
							

						}
					});

					startMapProcessing(thePath, theMapFileName, theProgressBar, theStep2Header, theStep2, theDisplay).start(); 
				} catch (Exception e) {
					theDisplay.syncExec(new Runnable() {
						
						public void run() {
							MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
							mBox.setText("Please Note ...");
							mBox.setMessage("Error in VCF to GVF conversion process");
							int result = mBox.open();
						}
					});
					return;				
				}
				
			}
		};
	}


	public Thread startMapProcessing(final String path, final String mapFileName, final ProgressBar progress, final Label header, final Label status, final Display theDisplay){
		return new Thread() {
			@Override
			public void run(){
				try{
					MapFileReader reader = new MapFileReader(mapFilename);		
					List<String> fieldList = reader.readFields();
					while(fieldList != null){
						if(fieldList.size() == 1){
							String pragma = fieldList.get(0);
							if(pragma.startsWith("##genome-build")){
								genVersion = pragma.substring(15).trim();
								if((genVersion == null) || (genVersion.length() == 0)){
									// print message that no genomic version

									theDisplay.syncExec(new Runnable() {
										
										public void run() {
											MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
											mBox.setText("Please Note ...");
											mBox.setMessage("A genomic version must be supplied in map file header (##genome-build");
											int result = mBox.open();
										}
									});
									return;	
								}

							}
							else if(pragma.startsWith("##file-date")){
								startDate = pragma.substring(12).trim();
								if((startDate == null) || (startDate.length() == 0)){
									// print message that no start date

									theDisplay.syncExec(new Runnable() {
										
										public void run() {
											MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
											mBox.setText("Please Note ...");
											mBox.setMessage("A start date must be supplied in map file header (##file-date)");
											int result = mBox.open();
										}
									});
									return;	

								}
								else if(isDateValid(startDate) == false){
									// print message that invalid start date

									theDisplay.syncExec(new Runnable() {
									
										public void run() {
											MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
											mBox.setText("Please Note ...");
											mBox.setMessage("A start date format such as '2000-01-22' must be supplied in map file header (##file-date)");
											int result = mBox.open();
										}
									});
									return;	

								}
							}

						}else if(fieldList.size() == 3){
							patientNum = fieldList.get(1).trim();
							encounterNum = fieldList.get(2).trim();

							String pathDelimeter = "/";
							if(path.contains("\\"))
								pathDelimeter = "\\";
							final String fullGvfFilename = path + pathDelimeter + fieldList.get(0).trim() + ".gvf";
							final String gvfFilename =  fieldList.get(0).trim() + ".gvf";
							theDisplay.syncExec(new Runnable() {
								
								public void run() {
									progressBar.setSelection(50);
									header.setText(gvfFilename + " to i2b2 step:  ");
									status.setText("");
									log.info("Converting " + fullGvfFilename + " to i2b2");	
								}
							});



							Thread running = gvf2i2b2(fullGvfFilename, patientNum, encounterNum, startDate, genVersion);
							running.setName("running");
							running.start();
							sleep(2000);   //allows gvf2i2b2 to start up  before we try to read the log file.
							
							BufferedReader gvfLogReader = null;
							try {
								gvfLogReader = new BufferedReader(new FileReader(fullGvfFilename + ".i2b2log"));
								while ((running != null) && (running.isAlive()) && (running.getName().equals("running"))){
									String line = gvfLogReader.readLine();
									while(line != null){
										final String theLine = line;
										theDisplay.syncExec(new Runnable() {
											
											public void run() {
												status.setText(theLine);
											}

										});
										line = gvfLogReader.readLine();
									}

								}
								gvfLogReader.close();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								log.info(fullGvfFilename + ".i2b2log not found");
								if(gvfLogReader != null)
									gvfLogReader.close();
								theDisplay.syncExec(new Runnable() {
									public void run() {
										status.setText("GVF processing error; quitting");
										progressBar.setSelection(100);
									}		
								});
								return;
								
							}
							
							String i2b2Filename =  path + pathDelimeter + fieldList.get(0).trim() +"." + encounterNum +".i2b2";
			//				startBulkLoad(i2b2Filename, theDisplay).start();

						}
						else {
							;
						}
						fieldList = reader.readFields();
					}

					reader.close();
					
				} catch (Exception e1) {

					theDisplay.syncExec(new Runnable() {
						
						public void run() {
							MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
							mBox.setText("Please Note ...");
							mBox.setMessage("Error in mapping file or gvf conversion process");
							int result = mBox.open();
						}
					});
					return;	
				}

				theDisplay.syncExec(new Runnable() {
					
					public void run() {
						progress.setSelection(100);
						header.setText("GVF to i2b2 step:  ");
						status.setText("All GVF to i2b2 complete");
						log.info("GVF to i2b2 complete..");	
						
						OpenStatusWindowDialog dialog = new OpenStatusWindowDialog(theDisplay.getActiveShell());
						dialog.open();
						
					}

				});
			}
		};
	}


	public Thread  gvf2i2b2(String fullGvfFilename, String patientNum, String encounterNum, String startDate, String genVersion){
		final String filename = fullGvfFilename;
		final String patient = patientNum;
		final String encounter = encounterNum;
		final String date = startDate;
		final String version = genVersion;
		return new Thread() {
			@Override
			public void run(){
				try {
					new gvf2i2b2(filename, patient, encounter, date, version);
					setName("done");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.info("GVF Error: " +e.getMessage());	
				}
			}
		};
	}

	public Thread startBulkLoad(String fileName, final Display theDisplay) {
		final String theFile = fileName;
		return new Thread() {
			@Override
			public void run(){
				try {
				//	bulkLoad(theFile, theDisplay);
					publishLoad(theFile, theDisplay);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.setProperty("statusMessage", e.getMessage());			
					log.info("Bulk load error: " + e.getMessage());
				}
				theDisplay.syncExec(new Runnable() {
					
					public void run() {
						// maybe do something with status or progress bar here
					}
				});
			}
		};
	}

	public Thread startBulkLoad(String fileName) {
		final String theFile = fileName;
		final Display theDisplay = Display.getCurrent();
		return new Thread() {
			@Override
			public void run(){
				try {
				//	bulkLoad(theFile, theDisplay);
						publishLoad(theFile, theDisplay);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.setProperty("statusMessage", e.getMessage());					
				}
				theDisplay.syncExec(new Runnable() {

					public void run() {
						// maybe do something with status or progress bar here
					}
				});
			}
		};
	}

	/*
	public void publishData(final String theFile, final Display theDisplay){
		try {
			PublishDataRequestType publishType = new PublishDataRequestType();

			InputOptionListType ioType = new InputOptionListType();
			LoadType loadType = new LoadType();
			DataListType dlType = new DataListType();
			loadType.setClearTempLoadTables(Boolean.valueOf(System.getProperty("FRClearTempTables")));
			loadType.setCommitFlag(true);

			String destDir = UserInfoBean.getInstance().getCellDataParam("FRC", "DestDir");
			if (destDir == null)
				destDir = "";
			String pathSeparator = UserInfoBean.getInstance().getCellDataParam("FRC", "PathSeparator");
			if (pathSeparator == null)
				pathSeparator = "/";
			DataListType.LocationUri uri = new DataListType.LocationUri();
			//		uri.setValue( destDir
			//				+ pathSeparator
			//				+ UserInfoBean.getInstance().getProjectId()
			//				+ pathSeparator
			//				+ f.getName()
			//		);
			uri.setValue(theFile);
			uri.setProtocolName("local");
			dlType.setLocationUri(uri);

			dlType.setDataFormatType(DataFormatType.CSV);	
			dlType.setLoadLabel("BULK_LOAD_OBS_FACT");
			dlType.setSourceSystemCd("ImportBigDataClient");
			//		dlType.setLoadLabel(UserInfoBean.getInstance().getProjectId() + "-" + f.getName());
			dlType.setTransformName("GENOMIC_IMPORT");
			ioType.setDataFile(dlType);

			LoadOptionType loType = new LoadOptionType();
			loType.setEncryptBlob(false);
			loType.setIgnoreBadData(true);

			FactLoadOptionType floType = new FactLoadOptionType();
			floType.setAppendFlag(true);
			loadType.setLoadPidSet(floType);
			loadType.setLoadObservationSet(floType);
			loadType.setLoadEidSet(floType);
			loadType.setLoadEventSet(floType);
			//loadType.setLoadEventidSet(loType);

			OutputOptionListType ooType = new OutputOptionListType();
			ooType.setDetail(true);
			OutputOptionType outputType = new OutputOptionType();
			outputType.setOnlykeys(true);
			ooType.setObservationSet(outputType);
			ooType.setPidSet(outputType);			
			ooType.setEventSet(outputType);
			ooType.setEidSet(outputType);	

			publishType.setInputList(ioType);
			publishType.setLoadList(loadType);
			publishType.setOutputList(ooType);

			GetPublishDataResponseMessage msg = new GetPublishDataResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				String response = CrcServiceDriver.getPublishDataRequest(publishType);

				procStatus = msg.processResult(response);
				if (procStatus.getType().equals("ERROR")){		
					if (procStatus.getType().equals("ERROR")){		
						System.setProperty("errorMessage",  procStatus.getValue());				
						theDisplay.syncExec(new Runnable() {
							
							public void run() {
								MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
								mBox.setText("Please Note ...");
								mBox.setMessage("Server reports: " +  System.getProperty("errorMessage"));
								int result = mBox.open();
							}
						});
						return;
					}	
					
				}			
			}
			// might be LoadDataListResponseType .. doReadLoadList() ?;
			
			LoadDataResponseType loadStatus = msg.doReadLoad();
			UploadIdList.getInstance().add(loadStatus.getUploadId());

		} catch (AxisFault e) {
			log.error(e.getMessage());
			theDisplay.syncExec(new Runnable() {
				
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Unable to make a connection to the remote server\n" +  
							"This is often a network error, please try again");
					int result = mBox.open();
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			theDisplay.syncExec(new Runnable() {
				
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Error message delivered from the remote server\n" +  
							"You may wish to retry your last action");
					int result = mBox.open();
				}
			});			
		}

	}
*/
	
	public void bulkLoad(final String theFile, final Display theDisplay){
		try {
			BulkLoadRequestType bulkLoadRequest = new BulkLoadRequestType();
			bulkLoadRequest.setFileName(theFile);
			bulkLoadRequest.setFileType("BULK_LOAD_OBS_FACT");
			bulkLoadRequest.setSourceSystemCd("ImportBigDataClient");

			CrcResponseData msg = new CrcResponseData();

			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				String response = CrcServiceDriver.bulkLoad(bulkLoadRequest);

				procStatus = msg.processResult(response);

					//				else if  other error codes
					//				TABLE_ACCESS_DENIED and USER_INVALID and DATABASE ERRORS
					if (procStatus.getType().equals("ERROR")){		
						System.setProperty("errorMessage",  procStatus.getValue());				
						theDisplay.syncExec(new Runnable() {
							
							public void run() {
								MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
								mBox.setText("Please Note ...");
								mBox.setMessage("Server reports: " +  System.getProperty("errorMessage"));
								int result = mBox.open();
							}
						});
						return;
					}	
					
				}
				LoadDataResponseType loadStatus = msg.doReadLoad();
				UploadIdList.getInstance().add(loadStatus.getUploadId());

			} catch (AxisFault e) {
				log.error(e.getMessage());
				theDisplay.syncExec(new Runnable() {
					
					public void run() {
						// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
						MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
						mBox.setText("Please Note ...");
						mBox.setMessage("Unable to make a connection to the remote server\n" +  
								"This is often a network error, please try again");
						int result = mBox.open();
					}
				});
			} catch (Exception e) {
				log.error(e.getMessage());
				theDisplay.syncExec(new Runnable() {
					
					public void run() {
						// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
						MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
						mBox.setText("Please Note ...");
						mBox.setMessage("Error message delivered from the remote server\n" +  
								"You may wish to retry your last action");
						int result = mBox.open();
					}
				});			
			}
			
			
			
		}
	
/*	private IRunnableWithProgress runnableWithProgress = new IRunnableWithProgress() {
		public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException{
			monitor.beginTask("Number counting", IProgressMonitor.UNKNOWN);
			for (int i =0; i<10; i++){
				if(monitor.isCanceled()){
					monitor.done();
					return;
				}
				monitor.worked(1);
				Thread.sleep(500);
				if(i==4)
					monitor.subTask("GVF to I2b2 conversion");
			}
			monitor.done();
			 
		}
	};
	*/
	
	public void publishLoad(final String theFile, final Display theDisplay){
		try {
			PublishDataRequestType bulkLoadRequest = new PublishDataRequestType();
			
			
			LocationUri uri = new LocationUri();
			
			uri.setValue(theFile);
			DataListType data = new DataListType();
			
			data.setDataFormatType(DataFormatType.CSV);
			data.setLoadLabel("BULK_LOAD_OBS_FACT");
			data.setLocationUri(uri);
			data.setSourceSystemCd("GENOMICS_CLIENT");
			data.setTransformName("GENOMIC_IMPORT");
			InputOptionListType input = new InputOptionListType();
			input.setDataFile(data);
			
			bulkLoadRequest.setInputList(input);
			
			
			CrcResponseData msg = new CrcResponseData();

			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				String response = CrcServiceDriver.publishDataRequest(bulkLoadRequest);

				procStatus = msg.processResult(response);

					//				else if  other error codes
					//				TABLE_ACCESS_DENIED and USER_INVALID and DATABASE ERRORS
					if (procStatus.getType().equals("ERROR")){		
						System.setProperty("errorMessage",  procStatus.getValue());				
						theDisplay.syncExec(new Runnable() {
							
							public void run() {
								MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
								mBox.setText("Please Note ...");
								mBox.setMessage("Server reports: " +  System.getProperty("errorMessage"));
								int result = mBox.open();
							}
						});
						return;
					}	
					
				}
				LoadDataResponseType loadStatus = msg.doReadLoad();
				UploadIdList.getInstance().add(loadStatus.getUploadId());

			} catch (AxisFault e) {
				log.error(e.getMessage());
				theDisplay.syncExec(new Runnable() {
					
					public void run() {
						// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
						MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
						mBox.setText("Please Note ...");
						mBox.setMessage("Unable to make a connection to the remote server\n" +  
								"This is often a network error, please try again");
						int result = mBox.open();
					}
				});
			} catch (Exception e) {
				log.error(e.getMessage());
				theDisplay.syncExec(new Runnable() {
					
					public void run() {
						// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
						MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
						mBox.setText("Please Note ...");
						mBox.setMessage("Error message delivered from the remote server\n" +  
								"You may wish to retry your last action");
						int result = mBox.open();
					}
				});			
			}
			
			
			
		}
	
/*	private IRunnableWithProgress runnableWithProgress = new IRunnableWithProgress() {
		public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException{
			monitor.beginTask("Number counting", IProgressMonitor.UNKNOWN);
			for (int i =0; i<10; i++){
				if(monitor.isCanceled()){
					monitor.done();
					return;
				}
				monitor.worked(1);
				Thread.sleep(500);
				if(i==4)
					monitor.subTask("GVF to I2b2 conversion");
			}
			monitor.done();
			 
		}
	};
	
	
	
	
	private void progressMonitor(){

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
		try {
			dialog.run(true, true, runnableWithProgress);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
*/

	
	
	}
