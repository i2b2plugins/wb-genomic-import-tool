/*
 * Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */

package edu.harvard.i2b2.gvf2i2b2;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import edu.harvard.i2b2.gvf2i2b2.reader.GVFFileReader;
import edu.harvard.i2b2.gvf2i2b2.writer.I2B2FileWriter;

public class gvf2i2b2{

	
	public gvf2i2b2(String inputFileName, String patientNum, String encNum, String startDate, String refGenome) throws Exception{
	//	PropertyConfigurator.configure("log4j.properties");
		
	//	System.out.println("gvf2i2b2  Version 1.0  Nov 2012");
	//	System.out.println("gvf2i2b2  Version 1.01  Feb 2013");
		System.out.println("gvf2i2b2  Version 1.02  Apr 2013");  //adds logging/status
		
		GVFFileReader reader = new GVFFileReader(inputFileName);
		
		String inputRoot = inputFileName.substring(0, inputFileName.indexOf(".gvf") + 1);
		I2B2FileWriter writer = new I2B2FileWriter(inputRoot + encNum +".i2b2");
		int instance_num = 0;
	
		BufferedWriter logWriter = new BufferedWriter(new FileWriter(inputFileName + ".i2b2log", false));
		logWriter.write("gvf2i2b2  Version 1.02  Apr 2013 \n");
		logWriter.flush();
		
				
		writer.writeReference(refGenome, patientNum, encNum, startDate);
		
		List<String> fieldList = reader.readFields();
		while(fieldList != null){
			if(fieldList.size() == 0)
				;
			else{
				instance_num++;
				if((instance_num % 1000) == 0)
				{
					logWriter.write("Converting GVF line " + instance_num + "\n");
					logWriter.flush();
					System.out.println("Converting GVF line " + instance_num);
				}
				writer.writeFields(fieldList, instance_num, patientNum, encNum, startDate, refGenome);

				//process fields
			}
			fieldList = reader.readFields();
		}
		logWriter.flush();
		logWriter.close();
		reader.close();
		writer.close();


	//	System.setProperty("gvfStatus","gvf2i2b2  complete");
		
		
	}	
	/**
	 * -outputFileName : Name of the file where the final data will reside
	 * 
	 */
	public static void main(String[] args)throws Exception {
	
		String usage =  "Usage: arguments are \n" + 
		"\t -inputFileName     Name of gvf file you wish to convert \n" +
		"\t -patientNum  	   i2b2 patient number associated with file \n" +
		"\t -encounterNum	   i2b2 encounter number associated with file \n" +
		"\t -startDate		   Date associated with file (in YYYY-MM-DD HH:MM:SS format) \n" +
		"\t -referenceGenome   e.g. hg19 \n";
	
		Map<String, String> options = new HashMap<String, String>();
		options.putAll(simpleCommandLineParser(args));
	
		String inputFileName = options.get("-inputFileName");		
		if(inputFileName == null || inputFileName.length() == 0){
			System.out.println("You must specify a GVF file name to load\n" );
			System.out.println(usage);
			System.exit(1);
		}	

		
		String patientNum = options.get("-patientNum");
		if(patientNum == null || patientNum.length() == 0){
			System.out.println("You must specify a patient number \n" );
			System.out.println(usage);
			System.exit(1);
		}
		
		String encNum = options.get("-encounterNum");
		if(encNum == null || encNum.length() == 0){
			System.out.println("You must specify an encounter number \n" );
			System.out.println(usage);
			System.exit(1);
		}
		
//		String inputRoot = inputFileName.substring(0, inputFileName.indexOf(".") + 1);
//		String test = (inputRoot + encNum +".i2b2");
		
		String startDate = options.get("-startDate");
		if(startDate == null || startDate.length() == 0){
			System.out.println("You must specify a date for this observation file \n" );
			System.out.println(usage);
			System.exit(1);
		}
		
		String refGenome = options.get("-referenceGenome");
		if(refGenome == null || refGenome.length() == 0){
			System.out.println("You must specify a reference genome (hg18/hg19) for this observation file \n" );
			System.out.println(usage);
			System.exit(1);
		}
		
		System.gc();
		new gvf2i2b2(inputFileName, patientNum, encNum, startDate, refGenome);
		
	}

	/**
	   * Simple method which turns an array of command line arguments into a
	   * map, where each token starting with a '-' is a key and the following
	   * non '-' initial token, if there is one, is the value.  For example,
	   * '-size 5 -verbose' will produce a map with entries (-size, 5) and
	   * (-verbose, null).
	   */
	  public static Map<String, String> simpleCommandLineParser(String[] args) {
	    Map<String, String> map = new HashMap<String, String>();
	    for (int i = 0; i <= args.length; i++) {
	      String key = (i > 0 ? args[i-1] : null);
	      String value = (i < args.length ? args[i] : null);
	      if (key == null || key.startsWith("-")) {
	        if (value != null && value.startsWith("-"))
	          value = null;
	        if (key != null || value != null)
	        	map.put(key, value);
	      }
	    }
	    return map;
	  }

}
