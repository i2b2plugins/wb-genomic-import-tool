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

/**
 * MapFile is a class used to handle mapping files associated with vcf 2 gvf output
 * <p>
 * It is abstract because it is the base class used for {MapFileReader} 
 * so you should use one of these (or both) according on what you need to do.

 */
public abstract class MapFile {

	/**
	 * The default char used as field separator.
	 */
	protected static final String DEFAULT_FIELD_SEPARATOR = "|";


	/**
	 * The current char used as field separator.
	 */
	protected String fieldSeparator;

	/**
	 * The default char used in header.
	 */
	protected static final char DEFAULT_PRAGMA_INDICATOR = '#';

	/**
	 * The current char used as header indicator.
	 */
	protected char pragmaIndicator;


	/**
	 * The default char used as annotation field separator.
	 */
	protected static final char DEFAULT_ANNOTATION_SEPARATOR = ';';

	/**
	 * The current char used as annotation field separator.
	 */
	protected char annotationSeparator;

	/**
	 * MapFile constructor with the default field separator.
	 */
	public MapFile() {
		this(DEFAULT_FIELD_SEPARATOR, DEFAULT_PRAGMA_INDICATOR, DEFAULT_ANNOTATION_SEPARATOR);
	}



	/**
	 * MapFile constructor with given field separator.
	 *
	 * @param sep  The field separator to be used; overwrites the default one
	 */
	public MapFile(String sep, char pragma, char annotation_sep) {
		setFieldSeparator(sep);
		setPragmaIndicator(pragma);
		setAnnotationSeparator(annotation_sep);
	}

	/**
	 * Set the current field separator.
	 *
	 * @param sep The new field separator to be used; overwrites the old one
	 */
	public void setFieldSeparator(String sep) {
		fieldSeparator = sep;
	}

	/**
	 * Get the current field separator.
	 *
	 * @return The char containing the current field separator
	 */
	public String getFieldSeparator() {
		return fieldSeparator;
	}


	/**
	 * Set the current pragma indicator.
	 *
	 * @param sep The new pragma indicator to be used; overwrites the old one
	 */
	public void setPragmaIndicator(char pragma) {
		pragmaIndicator = pragma;
	}

	/**
	 * Get the current pragma indicator
	 *
	 * @return The char containing the current pragma indicator
	 */
	public char getPragmaIndicator() {
		return pragmaIndicator;
	}

	/**
	 * Set the current annotation field separator.
	 *
	 * @param sep The new annotation field separator to be used; overwrites the old one
	 */
	public void setAnnotationSeparator(char sep) {
		annotationSeparator = sep;
	}

	/**
	 * Get the current annotation separator.
	 *
	 * @return The char containing the current annotation field separator
	 */
	public String getAnnotationSeparator() {
		return Character.toString(annotationSeparator);
	}





}

