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

/**
 * GVFFile is a class used to handle Genomic Variant Format files
 * <p>
 * It is abstract because it is the base class used for {GVFFileReader} 
 * so you should use one of these (or both) according on what you need to do.

 */
public abstract class GVFFile {

	/**
	 * The default char used as field separator.
	 */
	protected static final char DEFAULT_FIELD_SEPARATOR = '\t';


	/**
	 * The current char used as field separator.
	 */
	protected char fieldSeparator;

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
	 * GVFFile constructor with the default field separator.
	 */
	public GVFFile() {
		this(DEFAULT_FIELD_SEPARATOR, DEFAULT_PRAGMA_INDICATOR, DEFAULT_ANNOTATION_SEPARATOR);
	}



	/**
	 * GVFFile constructor with given field separator.
	 *
	 * @param sep  The field separator to be used; overwrites the default one
	 */
	public GVFFile(char sep, char pragma, char annotation_sep) {
		setFieldSeparator(sep);
		setPragmaIndicator(pragma);
		setAnnotationSeparator(annotation_sep);
	}

	/**
	 * Set the current field separator.
	 *
	 * @param sep The new field separator to be used; overwrites the old one
	 */
	public void setFieldSeparator(char sep) {
		fieldSeparator = sep;
	}

	/**
	 * Get the current field separator.
	 *
	 * @return The char containing the current field separator
	 */
	public String getFieldSeparator() {
		return Character.toString(fieldSeparator);
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

