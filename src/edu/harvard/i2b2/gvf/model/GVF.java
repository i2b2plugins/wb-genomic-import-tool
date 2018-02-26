/*
* Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */

package edu.harvard.i2b2.gvf.model;

import java.util.List;

import edu.harvard.i2b2.gvf2i2b2.reader.GVFFile;

public class GVF extends GVFFile{
	
	  String conceptCode;
	  String chromosome; 
	  String start; 
	  String end; 
	  String strand;
	  String zygosity;
	  String obs_blob;
	  String variantIdentifier;
	  String variantFeature;
	  String gene;

	String referenceAllele;
	  String alternateAllele;
	  
	  String exonicFunction;
	  String dbSNP;
	  String pp2Score;
	  String pp2Pred;
	  
	public String getConceptCode() {
		return conceptCode;
	}
	public void setConceptCode(String conceptCode) {
		this.conceptCode = conceptCode;
	}
	public String getChromosome() {
		return chromosome;
	}
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public String getStrand() {
		return strand;
	}
	public void setStrand(String strand) {
		if(strand != null){
			if(strand.equals("+"))
				this.strand = strand;
			else if(strand.equals("-"))
				this.strand = strand;
		}
	}
	public String getObs_blob() {
		return obs_blob;
	}
	public void setObs_blob(String obsBlob) {
		obs_blob = obsBlob;
	}
	public String getVariantIdentifier() {
		return variantIdentifier;
	}
	public void setVariantIdentifier(String variantIdentifier) {
		this.variantIdentifier = variantIdentifier;
	}
	
	public String getVariantFeature() {
		return variantFeature;
	}
	public void setVariantFeature(String variantFeature) {
		if(variantFeature.startsWith("ncRNA"))
				variantFeature = "ncRNA";
		// set gene to null in case of intergenic variants
		// ANNOVAR lists adjacent genes.
		if(variantFeature.startsWith("intergenic"))
			setGene("");
	
		this.variantFeature = variantFeature;
	}
	
	public String getGene() {
		return gene;
	}
	
	public void setGene(String gene) {
		if(gene.startsWith("NONE"))
			gene = "";
		if(gene.contains("(")){
			String[] geneParts = gene.split("\\(");
			gene = geneParts[0];
		}
		this.gene = gene;
	}
	
	public String getZygosity() {
		return zygosity;
	}
	public void setZygosity(String zygosity) {
		this.zygosity = zygosity;
	}
	
	  public String getReferenceAllele() {
			return referenceAllele;
		}
		public void setReferenceAllele(String referenceAllele) {
			this.referenceAllele = referenceAllele;
		}
		public String getAlternateAllele() {
			return alternateAllele;
		}
		public void setAlternateAllele(String alternateAllele) {
			this.alternateAllele = alternateAllele;
		}

		public String getExonicFunction() {
			return exonicFunction;
		}
		public void setExonicFunction(String exonicFunction) {
			this.exonicFunction  = exonicFunction;
		}
		
		public String getDbSNP() {
			return dbSNP;
		}
		public void setDbSNP(String dbSNP) {
			this.dbSNP = dbSNP;
		}

		public String getPP2Score() {
			return pp2Score;
		}
		public void setPP2Score(String score) {
			this.pp2Score = score;
		}

		public String getPP2Pred() {
			return pp2Pred;
		}
		public void setPP2Pred(String pred) {
			this.pp2Pred = pred;
		}
		
	
	public GVF(){
		initializeStrings();
	}
	
	private void initializeStrings(){
		setChromosome("(null)");
		setEnd("(null)");
		setStart("(null)");
		setConceptCode("(null)");
		setStrand("(null)");
		setObs_blob("(null)");
		setReferenceAllele("(null)");
		setAlternateAllele("(null)");
		setExonicFunction("(null)");
		setDbSNP("(null)");
	}
	
	public GVF(List<String> fields, String refGenome){
		initializeStrings();
		
		setChromosome(fields.get(0));
		setEnd(fields.get(4));
		setStart(fields.get(3));
		setConceptCode(fields.get(2));
		setStrand(fields.get(6));
		
		 String variantIdentifier = "";
		//  if(conceptCode.equals("SNV"))
		variantIdentifier = refGenome + ":" + chromosome + ":" + start + ":";
		  
		  String zygosity = "";
		  String gene = "";
		  String variantFeature = "";
		  String exonicFunction = "";
		  String dbSNP = "";
		  String pp2Pred = "";
		  String pp2Score = "";
		  
		  String[] annotations = fields.get(8).split(getAnnotationSeparator());
		  for(int i = 0; i<annotations.length; i++){

			  if(annotations[i].startsWith("Reference_seq")){
				  variantIdentifier += annotations[i].substring(annotations[i].indexOf("=")+1);
				  variantIdentifier += ">";
				  setReferenceAllele(annotations[i].substring(annotations[i].indexOf("=")+1));
			  }
			  else if(annotations[i].startsWith("Variant_seq")){
				  variantIdentifier += annotations[i].substring(annotations[i].indexOf("=")+1);
				  setAlternateAllele(annotations[i].substring(annotations[i].indexOf("=")+1));

			  }
			  else if(annotations[i].startsWith("Genotype")){
				  zygosity = annotations[i].substring(annotations[i].indexOf("=")+1);
			  }
			  else if(annotations[i].startsWith("Zygosity")){
				  zygosity = annotations[i].substring(annotations[i].indexOf("=")+1);
			  }
			  else if(annotations[i].startsWith("Gene")){
				  gene = annotations[i].substring(annotations[i].indexOf("=")+1);
			  }
			  else if(annotations[i].startsWith("Variant_feature")){
				  variantFeature = annotations[i].substring(annotations[i].indexOf("=")+1);
			  }
			  
			  else if(annotations[i].startsWith("Exonic_function")){
				  exonicFunction = annotations[i].substring(annotations[i].indexOf("=")+1);
			  }
			  else if(annotations[i].startsWith("dbSNP")){
				  dbSNP = annotations[i].substring(annotations[i].indexOf("=")+1);
			  }
			  
			  else if(annotations[i].startsWith("PP2_pred")){
				  pp2Pred = annotations[i].substring(annotations[i].indexOf("=")+1);
			  }
			  
			  else if(annotations[i].startsWith("PP2_score")){
				  pp2Score = annotations[i].substring(annotations[i].indexOf("=")+1);
			  }
			  
		  }
		  if(zygosity.length()>0)
			  setZygosity(zygosity);
	
		  if(gene.length()>0)
			  setGene(gene);
		  
		  if(variantFeature.length()>0)
			  setVariantFeature(variantFeature);
		  
		  if(exonicFunction.length()>0)
			  setExonicFunction(exonicFunction);
		  
		  if(dbSNP.length()>0)
			  setDbSNP(dbSNP);
		  
		  if(pp2Pred.length()>0)
			  setPP2Pred(pp2Pred);
		  
		  if(pp2Score.length()>0)
			  setPP2Score(pp2Score);
		  
		  setObs_blob("<GVFxml><reference_genome>"+ refGenome
		  		+"</reference_genome><variant_identifier>" + variantIdentifier 
				  + "</variant_identifier><GVF>"+fields.get(9) 
				  		+ "</GVF></GVFxml>");
	}
	
}
