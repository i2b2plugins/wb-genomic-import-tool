/*
* Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */

package edu.harvard.i2b2.gvf2i2b2.writer;

import java.util.*;
import java.io.*;

import edu.harvard.i2b2.gvf.model.GVF;

public class I2B2FileWriter extends I2B2File{
	/**
	 * The buffered writer linked to the I2B2 file to be written.
	 */
  protected BufferedWriter out;

	/**
	 * GVFileReader constructor just need the name of the existing GVF file that will be read.
	 *
	 * @param  inputFileName         The name of the GVF file to be opened for reading
	 * @throws FileNotFoundException If the file to be read does not exist
	 */
  public I2B2FileWriter(String outputFileName){
    super();
    try {
		out = new BufferedWriter(new FileWriter(outputFileName, false));
		writeI2B2Header();
	} catch (IOException e) {
		// TODO Auto-generated catch block
        //Close the BufferedWriter
        try {
            if (out != null) {
                out.flush();
                out.close();
            }		e.printStackTrace();
	
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
  }

	/**
	 * Format the output file header 
	 * @throws IOException If an error occurs while writing to the the file
	 */
  	public void writeI2B2Header() throws IOException {
  		// original header
  		//		out.write("ENCOUNTER_NUM|PATIENT_NUM|CONCEPT_CD|PROVIDER_ID|START_DATE|MODIFIER_CD|INSTANCE_NUM|VALTYPE_CD|TVAL_CHAR|NVAL_NUM|SOURCESYSTEM_CD|OBSERVATION_BLOB");
  		// new header accounts for all obs_fact columns.
  		out.write("ENCOUNTER_NUM|PATIENT_NUM|CONCEPT_CD|PROVIDER_ID|START_DATE|MODIFIER_CD|INSTANCE_NUM|VALTYPE_CD|TVAL_CHAR|NVAL_NUM|VALUEFLAG_CD|QUANTITY_NUM|UNITS_CD|END_DATE|LOCATION_CD|CONFIDENCE_NUM|UPDATE_DATE|DOWNLOAD_DATE|IMPORT_DATE|UPLOAD_ID|SOURCESYSTEM_CD|OBSERVATION_BLOB");

  	}

	/**
	 * Format the file output for the reference genome read in
	 * @throws IOException If an error occurs while writing to the the file
	 */
  	
  public void writeReference(String refGenome, String patientNum, String encNum, String startDate) throws IOException {
	  out.write(getConceptString(refGenome, patientNum, encNum, startDate));
  	
  }
	/**
	 * Format the file output from the fields read in
	 * @throws IOException If an error occurs while writing to the the file
	 */
  	
  //	chr1	CGI	SNV	41981	41981	127	+	.	ID=1;Reference_seq=A;Variant_seq=G;Genotype=homozygous;
  public void writeFields(List<String> fields, int instance_num, String patientNum, String encNum, String startDate, String refGenome) throws IOException {
	  GVF gvfRecord = new GVF(fields, refGenome);
	  try {
		String output = gvf2String(gvfRecord, instance_num, patientNum, encNum, startDate);
		if(output!=null)	
			out.write(output);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		try {
            if (out != null) {
                out.flush();
                out.close();
            }		e.printStackTrace();
	
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
	  			  
	  
  }

	/**
	 * Close the I2B2 file writer.
	 *
	 * @throws IOException If an error occurs while closing the file
	 */
  public void close() throws IOException {
	 
	  try {
		  if (out != null) {
			  out.flush();
			  out.close();
		  }
	  } catch (Exception e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  }     

  }


  
	
}

