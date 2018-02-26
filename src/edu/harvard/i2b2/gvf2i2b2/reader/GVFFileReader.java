/*
* Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */

package edu.harvard.i2b2.gvf2i2b2.reader;

import java.util.*;
import java.io.*;

public class GVFFileReader extends GVFFile{
	/**
	 * The buffered reader linked to the GVF file to be read.
	 */
  protected BufferedReader in;

	/**
	 * GVFileReader constructor just need the name of the existing GVF file that will be read.
	 *
	 * @param  inputFileName         The name of the GVF file to be opened for reading
	 * @throws FileNotFoundException If the file to be read does not exist
	 */
  public GVFFileReader(String inputFileName) throws FileNotFoundException {
    super();
    in = new BufferedReader(new FileReader(inputFileName));
  }


	/**
	 * Split the next line of the input GVF file into fields.
	 * <p>
	 * This is currently the most important function of the package.
	 *
	 * @return             List of strings containing each field from the next line of the file
	 * @throws IOException If an error occurs while reading the new line from the file
	 */
  public List<String> readFields() throws IOException {
    String[] fields = new String[]{};
    StringBuffer sb = new StringBuffer();
    String line = in.readLine();
    if(line==null) return null;

    if(line.length()==0) {
     
      return Arrays.asList(fields);
    }
    
    // skip header lines
    if(line.charAt(0) == pragmaIndicator){
    	return Arrays.asList(fields);
    	// if we process pragma add code here..
    //	if(line.charAt(1) == pragmaIndicator){	
    //	}
    }

    // otherwise we have a line of variant info
    
    	fields = line.split(getFieldSeparator());
    	List<String> fieldList = new ArrayList(Arrays.asList(fields));
    	fieldList.add(line);
    	
    	
  
    return fieldList;
  }

	/**
	 * Close the input GVF file.
	 *
	 * @throws IOException If an error occurs while closing the file
	 */
  public void close() throws IOException {
    in.close();
  }


  
	/**
	 * Handles an unquoted field.
	 *
	 * @return index of next separator
	 */
  protected int handlePlainField(String s, StringBuffer sb, int i) {
    int j = s.indexOf(fieldSeparator, i);  // look for separator
    if(j == -1) {  // none found
      sb.append(s.substring(i));
      return s.length();
    } else {
      sb.append(s.substring(i, j));
      return j;
    }
  }
}

