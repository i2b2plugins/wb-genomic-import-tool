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

import edu.harvard.i2b2.gvf.model.GVF;
import edu.harvard.i2b2.gvf2i2b2.Messages;

/**
 * GVFFile is a class used to handle Genomic Variant Format files
 * <p>
 * It is abstract because it is the base class used for {GVFFileReader} 
 * so you should use one of these (or both) according to what you need to do.
 */
public abstract class I2B2File {

	/**
	 * The default char used as field separator.
	 */
	protected static final String DEFAULT_FIELD_SEPARATOR = "|";

	protected static final String DEFAULT_PLACEHOLDER = "\"@\"";

	protected static final String DEFAULT_PATIENT_NUM = "1000000001";
	protected static final String DEFAULT_ENCOUNTER_NUM = "1900000001";
	protected static final String DEFAULT_START_DATE = "2011-02-04:11:08:38";

	
	protected static final String DEFAULT_NULL = "(null)";
	protected static final String DEFAULT_SOURCE = "\"GVF2I2B2\"";

	protected static final String DEFAULT_FILLER = "||||||||||";
	
	
	/**
	 * The default char used as annotation field separator.
	 */
	protected static final String DEFAULT_ANNOTATION_SEPARATOR = ";";

	
	/**
	 * The current char used as field separator.
	 */
	protected String fieldSeparator;

	
	
	
	/**
	 * GVFFile constructor with the default field separator.
	 */
	public I2B2File() {
		this(DEFAULT_FIELD_SEPARATOR);
	}



	/**
	 * GVFFile constructor with given field separator.
	 *
	 * @param sep  The field separator to be used; overwrites the default one
	 */
	public I2B2File(String sep) {
		setFieldSeparator(sep);
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


	public String gvf2String(GVF gvfRecord, int instance_num, String patientNum, String encNum, String startDate){
		
		String out = null;
		if((gvfRecord.getConceptCode() != null) && (gvfRecord.getConceptCode().length() > 0))
			out = getConceptString(gvfRecord, instance_num, patientNum, encNum, startDate);
		
		if(out != null){
			if(gvfRecord.getChromosome() != null)
				out+= getValCharModifier(gvfRecord, Messages.getString("ModifierCode.Chromosome"), gvfRecord.getChromosome(), instance_num, patientNum, encNum, startDate);
			if(gvfRecord.getStart() != null)
				out+= getNvalNumModifier(gvfRecord,  Messages.getString("ModifierCode.Start"), gvfRecord.getStart(), instance_num, patientNum, encNum, startDate);
			if(gvfRecord.getEnd() != null)
				out+= getNvalNumModifier(gvfRecord,  Messages.getString("ModifierCode.End"), gvfRecord.getEnd(), instance_num, patientNum, encNum, startDate);
			
	//		if(gvfRecord.getStrand() != null)
	//			out+= getValCharModifier(gvfRecord,  Messages.getString("ModifierCode.Strand"), gvfRecord.getStrand(), instance_num, patientNum, encNum, startDate);

			if(gvfRecord.getZygosity() != null)
				out+= getValCharModifier(gvfRecord,  Messages.getString("ModifierCode.Zygosity"), gvfRecord.getZygosity(), instance_num, patientNum, encNum, startDate);	
			
			if((gvfRecord.getGene() != null) && (gvfRecord.getGene().length() != 0)){
				if(gvfRecord.getGene().contains(",")){
					String[] genes = gvfRecord.getGene().split(",");
					for(int i=0; i<genes.length; i++){
						out+= getValCharModifier(gvfRecord,  Messages.getString("ModifierCode.Gene"), genes[i], instance_num, patientNum, encNum, startDate);		
					}
				}
				else
					out+= getValCharModifier(gvfRecord,  Messages.getString("ModifierCode.Gene"), gvfRecord.getGene(), instance_num, patientNum, encNum, startDate);	
			}
			if(gvfRecord.getReferenceAllele() != null) 
				out+= getValCharModifier(gvfRecord,  Messages.getString("ModifierCode.ReferenceAllele"), gvfRecord.getReferenceAllele(), instance_num, patientNum, encNum, startDate);	

			if(gvfRecord.getAlternateAllele() != null) 
				out+= getValCharModifier(gvfRecord,  Messages.getString("ModifierCode.AlternateAllele"), gvfRecord.getAlternateAllele(), instance_num, patientNum, encNum, startDate);	

			if(gvfRecord.getPP2Pred() != null)
				out+= getValCharModifier(gvfRecord,  Messages.getString("ModifierCode.PP2Pred"), gvfRecord.getPP2Pred(), instance_num, patientNum, encNum, startDate);	
			
			if(gvfRecord.getPP2Score() != null)
				out+= getNvalNumModifier(gvfRecord,  Messages.getString("ModifierCode.PP2Score"), gvfRecord.getPP2Score(), instance_num, patientNum, encNum, startDate);	
			
			if(gvfRecord.getDbSNP() != null)
				out+= getValCharModifier(gvfRecord,  Messages.getString("ModifierCode.dbSNP"), gvfRecord.getDbSNP(), instance_num, patientNum, encNum, startDate);	
			
			
			if(gvfRecord.getVariantFeature() != null)
				out+= getModifierString(gvfRecord, instance_num, patientNum, encNum, startDate );
			
			if((gvfRecord.getExonicFunction() != null) && (!( gvfRecord.getExonicFunction().equals("(null)"))) )
				out+= getExonicModifierString(gvfRecord, instance_num, patientNum, encNum, startDate );
		}

		return out;
	}	
	
	private String getConceptString(GVF gvfRecord, int instance_num, String patientNum, String encNum, String startDate){
		
		 String code;
		try {
			code = Messages.getString("ConceptCode." + gvfRecord.getConceptCode());
		} catch (Exception e) {
			// concept code not found, skip
			return null;
		}
		 
			
		// Strings have "s  numbers dont; no nulls
		String out = "\r\n" + 
			  encNum  + DEFAULT_FIELD_SEPARATOR
			  + patientNum  + DEFAULT_FIELD_SEPARATOR
			 //concept code
			 + "\"" + code + "\""+ DEFAULT_FIELD_SEPARATOR
			 //provider
			 + DEFAULT_PLACEHOLDER+ DEFAULT_FIELD_SEPARATOR
			 //startdate
			 + "\"" + startDate + "\"" + DEFAULT_FIELD_SEPARATOR
			//modifier
			 + DEFAULT_PLACEHOLDER+ DEFAULT_FIELD_SEPARATOR
			
			  + instance_num  + DEFAULT_FIELD_SEPARATOR
			
			 //valtype_code
			 + DEFAULT_FIELD_SEPARATOR
			 
			 
			 //tval_char
			  + DEFAULT_FIELD_SEPARATOR
				
			 //nval_num
			 + DEFAULT_FIELD_SEPARATOR
			 
			 // account for unused obs_fact columns
			  + DEFAULT_FILLER
			
		 //sourcesystem_cd
		 + DEFAULT_SOURCE + DEFAULT_FIELD_SEPARATOR
			
		 //blob
		 + "\"" + gvfRecord.getObs_blob() + "\"" ;
		 
		 
		 return out;
	
		
		/*  full obs fact
		String out = "\r\n" + 
		 "\"" + encNum + "\"" + DEFAULT_FIELD_SEPARATOR
		 + "\"" + patientNum + "\"" + DEFAULT_FIELD_SEPARATOR
		 //concept code
		 + "\"" + Messages.getString("ConceptCode." + gvfRecord.getConceptCode())+ "\""+ DEFAULT_FIELD_SEPARATOR
		 //provider
		 + DEFAULT_PLACEHOLDER+ DEFAULT_FIELD_SEPARATOR
		 //startdate
		 + "\"" + startDate + "\"" + DEFAULT_FIELD_SEPARATOR
		//modifier
		 + DEFAULT_PLACEHOLDER+ DEFAULT_FIELD_SEPARATOR
		
		 + "\"" + instance_num + "\"" + DEFAULT_FIELD_SEPARATOR
		
		 //valtype_code
		 + DEFAULT_NULL + DEFAULT_FIELD_SEPARATOR
		 
		 //tval_char
		 + DEFAULT_NULL + DEFAULT_FIELD_SEPARATOR
			
		 //nval_num
		 + DEFAULT_NULL + DEFAULT_FIELD_SEPARATOR
			
		 //valueflag_cd
		 + DEFAULT_NULL + DEFAULT_FIELD_SEPARATOR
		 
		 //quantity num
		 + DEFAULT_NULL + DEFAULT_FIELD_SEPARATOR
			
		 //units cd
		 + DEFAULT_NULL + DEFAULT_FIELD_SEPARATOR
		 
		 //end_date
		 + DEFAULT_NULL + DEFAULT_FIELD_SEPARATOR
		 
		 //location_cd
		 + DEFAULT_NULL + DEFAULT_FIELD_SEPARATOR
			
		 //blob
		 + "\"" + gvfRecord.getObs_blob() + "\"" + DEFAULT_FIELD_SEPARATOR
		 
		 //confidence num
		 + DEFAULT_NULL + DEFAULT_FIELD_SEPARATOR
		 
		 //update date
		 + DEFAULT_NULL + DEFAULT_FIELD_SEPARATOR
			
		 //download date
		 + DEFAULT_NULL + DEFAULT_FIELD_SEPARATOR
			
		 //import date
		 + DEFAULT_NULL + DEFAULT_FIELD_SEPARATOR
		
		//sourcesystem_cd
		 + DEFAULT_SOURCE + DEFAULT_FIELD_SEPARATOR
		 
		 //upload id
		 + DEFAULT_NULL 
			
		 //text search index ignored
		 ;
		 		 return out;
		 */
		

	}
			
	private String getValCharModifier(GVF gvfRecord, String modifierCd, String valChar, int instance_num, String patientNum, String encNum, String startDate){
	String out = "\r\n" + 
		 encNum +  DEFAULT_FIELD_SEPARATOR
		  + patientNum  + DEFAULT_FIELD_SEPARATOR
		 
		 //concept code
		 + "\"" + Messages.getString("ConceptCode." + gvfRecord.getConceptCode()) + "\""+ DEFAULT_FIELD_SEPARATOR
		 
		 //provider
		 + DEFAULT_PLACEHOLDER+ DEFAULT_FIELD_SEPARATOR
		//startdate
		 + "\"" + startDate + "\"" + DEFAULT_FIELD_SEPARATOR
		//modifier
		 + "\"" + modifierCd + "\"" + DEFAULT_FIELD_SEPARATOR
		
		  + instance_num  + DEFAULT_FIELD_SEPARATOR
		
		 //valtype_code
		 + "\"T\"" + DEFAULT_FIELD_SEPARATOR
		 
		 //tval_char
		 + "\"" + valChar + "\"" + DEFAULT_FIELD_SEPARATOR
			
		 //nval_num
		  + DEFAULT_FIELD_SEPARATOR
		
		  // account for unused obs_fact columns
		  + DEFAULT_FILLER
		  
			//sourcesystem_cd
			 + DEFAULT_SOURCE + DEFAULT_FIELD_SEPARATOR
			 
		 //blob
		 + "\"" + gvfRecord.getObs_blob() + "\"" ;
		 

	return out;
	}
	

	private String getNvalNumModifier(GVF gvfRecord, String modifierCd, String nval_num, int instance_num, String patientNum, String encNum, String startDate){
		String out = "\r\n" 
		+ encNum +  DEFAULT_FIELD_SEPARATOR
		 + patientNum  + DEFAULT_FIELD_SEPARATOR
		//concept code
		 + "\"" + Messages.getString("ConceptCode." + gvfRecord.getConceptCode()) + "\"" + DEFAULT_FIELD_SEPARATOR
		
		 //provider
		 + DEFAULT_PLACEHOLDER+ DEFAULT_FIELD_SEPARATOR
		 //startdate
		 + "\"" + startDate + "\"" + DEFAULT_FIELD_SEPARATOR
		//modifier
		 + "\"" +  modifierCd + "\"" + DEFAULT_FIELD_SEPARATOR
		
		  + instance_num + DEFAULT_FIELD_SEPARATOR
		
		 //valtype_code
		 + "\"N\"" + DEFAULT_FIELD_SEPARATOR
		 
		 //tval_char
		 + "\"E\""+ DEFAULT_FIELD_SEPARATOR
			
		 //nval_num
		 +  nval_num  + DEFAULT_FIELD_SEPARATOR
			
		 // account for unused obs_fact columns
		  + DEFAULT_FILLER
		 
		 ///sourcesystem_cd
		 
		 + DEFAULT_SOURCE + DEFAULT_FIELD_SEPARATOR
		 
		 //blob
		 + "\"" + gvfRecord.getObs_blob() + "\"";
		 
		 
	
	return out;
		/*String out = "\n" + DEFAULT_PATIENT_NUM + DEFAULT_FIELD_SEPARATOR
			 + DEFAULT_ENCOUNTER_NUM + DEFAULT_FIELD_SEPARATOR
			 //provider
			 + DEFAULT_PLACEHOLDER+ DEFAULT_FIELD_SEPARATOR
			//concept code
			 + Messages.getString("ConceptCode." + gvfRecord.getConceptCode())+ DEFAULT_FIELD_SEPARATOR
			 //startdate
			 + DEFAULT_START_DATE + DEFAULT_FIELD_SEPARATOR
			//modifier
			 + modifierCd + DEFAULT_FIELD_SEPARATOR
			
			 + instance_num + DEFAULT_FIELD_SEPARATOR
			
			 //valtype_code
			 + "N" + DEFAULT_FIELD_SEPARATOR
			 
			 //tval_char
			 + "E"+ DEFAULT_FIELD_SEPARATOR
				
			 //nval_num
			 + nval_num + DEFAULT_FIELD_SEPARATOR
				
			 //sourcesystem_cd
			 + DEFAULT_SOURCE + DEFAULT_FIELD_SEPARATOR
				
			 //blob
			 + gvfRecord.getObs_blob();
			 
		
		return out;
		*/
	}
	
	public String getConceptString(String refGenome, String patientNum, String encNum, String startDate){
		String out = "\r\n" + 
			 encNum + DEFAULT_FIELD_SEPARATOR
		  + patientNum  + DEFAULT_FIELD_SEPARATOR
		
		 //concept code
		 + "\"" + Messages.getString("ConceptCode.ReferenceGenome") + "\""+ DEFAULT_FIELD_SEPARATOR
		 
		 //provider
		 + DEFAULT_PLACEHOLDER+ DEFAULT_FIELD_SEPARATOR
		 //startdate
		 + "\"" + startDate + "\"" + DEFAULT_FIELD_SEPARATOR
		//modifier
		 + DEFAULT_PLACEHOLDER+ DEFAULT_FIELD_SEPARATOR
		
		 //instance_num
		 + "1" + DEFAULT_FIELD_SEPARATOR
		 
		 //valtype_code
		 + "\"T\"" + DEFAULT_FIELD_SEPARATOR
		 
		 //tval_char
		 + "\"" + refGenome + "\"" + DEFAULT_FIELD_SEPARATOR

			
		 //nval_num
		 + DEFAULT_FIELD_SEPARATOR
		 
		 // account for unused obs_fact columns
		  + DEFAULT_FILLER
		
		//sourcesystem_cd
		 + DEFAULT_SOURCE + DEFAULT_FIELD_SEPARATOR
		 
			
		 //blob
		;
		 
		 
		 return out;
	
	}

	public String getModifierString(GVF gvfRecord, int instance_num, String patientNum, String encNum, String startDate){
		String out = "\r\n" + 
			 encNum + DEFAULT_FIELD_SEPARATOR
		  + patientNum  + DEFAULT_FIELD_SEPARATOR
		
		 //concept code
		 + "\"" + Messages.getString("ConceptCode." + gvfRecord.getConceptCode()) + "\""+ DEFAULT_FIELD_SEPARATOR
		 
		 //provider
		 + DEFAULT_PLACEHOLDER+ DEFAULT_FIELD_SEPARATOR
		 //startdate
		 + "\"" + startDate + "\"" + DEFAULT_FIELD_SEPARATOR
		//modifier
		 + "\"" + Messages.getString("ModifierCode."+gvfRecord.getVariantFeature()) + "\""+ DEFAULT_FIELD_SEPARATOR
		
		 //instance_num
		 + instance_num + DEFAULT_FIELD_SEPARATOR
		 
		 //valtype_code
		 + DEFAULT_FIELD_SEPARATOR
		 
		 //tval_char
		 + DEFAULT_FIELD_SEPARATOR

			
		 //nval_num
		 + DEFAULT_FIELD_SEPARATOR
		 
		 // account for unused obs_fact columns
		  + DEFAULT_FILLER
		
		//sourcesystem_cd
		 + DEFAULT_SOURCE + DEFAULT_FIELD_SEPARATOR
		 
			
		 //blob
		 + "\"" + gvfRecord.getObs_blob() + "\"";
			
		
		 
		 
		 return out;
	
	}

	public String getExonicModifierString(GVF gvfRecord, int instance_num, String patientNum, String encNum, String startDate){
		String out = "\r\n" + 
			 encNum + DEFAULT_FIELD_SEPARATOR
		  + patientNum  + DEFAULT_FIELD_SEPARATOR
		
		 //concept code
		 + "\"" + Messages.getString("ConceptCode." + gvfRecord.getConceptCode()) + "\""+ DEFAULT_FIELD_SEPARATOR
		 
		 //provider
		 + DEFAULT_PLACEHOLDER+ DEFAULT_FIELD_SEPARATOR
		 //startdate
		 + "\"" + startDate + "\"" + DEFAULT_FIELD_SEPARATOR
		//modifier
		 + "\"" + Messages.getString("ModifierCode."+gvfRecord.getExonicFunction()) + "\""+ DEFAULT_FIELD_SEPARATOR
		
		 //instance_num
		 + instance_num + DEFAULT_FIELD_SEPARATOR
		 
		 //valtype_code
		 + DEFAULT_FIELD_SEPARATOR
		 
		 //tval_char
		 + DEFAULT_FIELD_SEPARATOR

			
		 //nval_num
		 + DEFAULT_FIELD_SEPARATOR
		 
		 // account for unused obs_fact columns
		  + DEFAULT_FILLER
		
		//sourcesystem_cd
		 + DEFAULT_SOURCE + DEFAULT_FIELD_SEPARATOR
		 
			
		 //blob
		 + "\"" + gvfRecord.getObs_blob() + "\"";
			
		
		 
		 
		 return out;
	
	}
	
}

