/*
 * Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */

package edu.harvard.i2b2.eclipse.plugins.importBigData.mapReader;

import java.util.*;
import java.io.*;

public class MapFileReader extends MapFile{
	/**
	 * The buffered reader linked to the Map file to be read.
	 */
  protected BufferedReader in;

	/**
	 * MapFileReader constructor just need the name of the existing Map file that will be read.
	 *
	 * @param  mapFileName         The name of the Map file to be opened for reading
	 * @throws FileNotFoundException If the file to be read does not exist
	 */
  public MapFileReader(String mapFileName) throws FileNotFoundException {
    super();
    in = new BufferedReader(new FileReader(mapFileName));
  }


	/**
	 * Split the next line of the map file into fields.
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
    
    line = line.trim();
    // skip header lines
    if(line.charAt(0) == pragmaIndicator){
    	// if we process pragma add code here..
    	if(line.charAt(1) == pragmaIndicator){	
    		List<String> fieldList = new ArrayList(Arrays.asList(line));
    		return fieldList;
    	}
    	return Arrays.asList(fields);
    }
//
    // otherwise we have a line of mapping
    
    	StringTokenizer token = new StringTokenizer(line, fieldSeparator);
    	List<String> fieldList = new ArrayList<String>();
    	while(token.hasMoreElements()){   		
    		fieldList.add(token.nextToken());
    	}
    	
    //	fields = line.split(getFieldSeparator());
    	
    //	List<String> fieldList = new ArrayList(Arrays.asList(fields));
    //	fieldList.add(line);
    	
    	
  
    return fieldList;
  }

	/**
	 * Close the input Map file.
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

