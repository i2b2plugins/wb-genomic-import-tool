#!/usr/bin/perl

$varID = 0;

open input, "<$ARGV[0]"; #input VCF file
#open output, ">$ARGV[1]"; #output GVF file




$source = 'VCF';

$strand = '+';
$phase = '.';

$fileindex = 0;
@filehandle = ();

$lineNumber = 0;
#read each variant from var file
while ($line = readline input)
{
	#get each var 
	chomp($line);
	if ($line =~ m/^#comment\t##/)
	{
	}
	if ($line =~ m/^#comment\t#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\t(.+)/)
	{
		@sample_all = split(/\t/, $1);
		foreach (@sample_all)
		{
			$fileindex = $fileindex + 1;
			$filehandle[$fileindex] = 'output'.$fileindex;
			$filename = $_.'.gvf';
			open $filehandle[$fileindex], ">$filename";
			
			#print Pragma
			print {$filehandle[$fileindex]} "##gvf-version 1.05\n";
			print {$filehandle[$fileindex]} "##file-version 1.0\n";
			print {$filehandle[$fileindex]} "##feature-ontology http://sourceforge.net/projects/song/files/Sequence%20Ontology/so_2_4_4/so_2_4_4.obo/download\n";
			print {$filehandle[$fileindex]} "##genome-build NCBI B37.3\n";
			print {$filehandle[$fileindex]} "##sequence-region\n";
		}
	
	}
	elsif ($line =~ m/^([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t(.+)/)
	{
		$lineNumber = $lineNumber + 1;
		print "$lineNumber\n";	
		$varFeature = $1;
		$gene = $2;
		$chromosome = 'chr'.$8;
		$POS = $9;
		$ID = $10;
		$REF = $11;
		@ALT_all = split(',', $12);
		$QUAL = $13;
		$FILTER = $14;
		@genotype_all = split(/\t/, $17);
		
		$score = $QUAL;
		
		$ALT_index = 0;
		@variant_output = ();
		
		#determine each variant
		foreach (@ALT_all)
		{
			$ALT = $_;
			$ALT_index = $ALT_index + 1;
			
			#check varType and store variant output
			if (length($REF) == length($ALT) && length($REF) == 1)
			{
				$varID = $varID + 1;
				$varType = 'SNV';
				$begin_gvf = $POS;
				$end_gvf = $POS;
				$reference_gvf = $REF;
				$variant_gvf = $ALT;
				#print output "$chromosome\t$source\t$varType\t$begin_gvf\t$end_gvf\t$score\t$strand\t$phase\tID=$varID;Reference_seq=$reference_gvf;Variant_seq=$variant_gvf;\n";
				$variant_output[$ALT_index] = "$chromosome\t$source\t$varType\t$begin_gvf\t$end_gvf\t$score\t$strand\t$phase\tID=$varID;Reference_seq=$reference_gvf;Variant_seq=$variant_gvf;Variant_feature=$varFeature;Gene=$gene;";
			}
			elsif (length($REF) < length($ALT) && length($REF) == 1)
			{
				$varType = 'insertion';
				$match = 0;
				
				if ($REF =~ m/^(\w)(\w*)/)
				{
					$REF_base1 = $1;
					$REF_base2 = $2;
					$match = $match + 1;
				}
				
				if ($ALT =~ m/^(\w)(\w*)/)
				{
					$ALT_base1 = $1;
					$ALT_base2 = $2;
					$match = $match + 1;
				}
				
				if ($match == 2)
				{
					$varID = $varID + 1;
					$begin_gvf = $POS;
					$end_gvf = $POS;
					$reference_gvf = '-';
					$variant_gvf = $ALT_base2;
					#print output "$chromosome\t$source\t$varType\t$begin_gvf\t$end_gvf\t$score\t$strand\t$phase\tID=$varID;Reference_seq=$reference_gvf;Variant_seq=$variant_gvf;\n";
					$variant_output[$ALT_index] = "$chromosome\t$source\t$varType\t$begin_gvf\t$end_gvf\t$score\t$strand\t$phase\tID=$varID;Reference_seq=$reference_gvf;Variant_seq=$variant_gvf;Variant_feature=$varFeature;Gene=$gene;";
				}
				
			}
			elsif (length($REF) > length($ALT) && length($ALT) == 1)
			{
				$varType = 'deletion';
				$match = 0;
				
				if ($REF =~ m/^(\w)(\w*)/)
				{
					$REF_base1 = $1;
					$REF_base2 = $2;
					$match = $match + 1;
				}
				
				if ($ALT =~ m/^(\w)(\w*)/)
				{
					$ALT_base1 = $1;
					$ALT_base2 = $2;
					$match = $match + 1;
				}
				
				if ($match == 2)
				{
					$varID = $varID + 1;
					$begin_gvf = $POS + 1;
					$end_gvf = $POS + 1 + length($REF_base2) - 1;
					$reference_gvf = $REF_base2;
					$variant_gvf = '-';
					#print output "$chromosome\t$source\t$varType\t$begin_gvf\t$end_gvf\t$score\t$strand\t$phase\tID=$varID;Reference_seq=$reference_gvf;Variant_seq=$variant_gvf;\n";
					$variant_output[$ALT_index] = "$chromosome\t$source\t$varType\t$begin_gvf\t$end_gvf\t$score\t$strand\t$phase\tID=$varID;Reference_seq=$reference_gvf;Variant_seq=$variant_gvf;Variant_feature=$varFeature;Gene=$gene;";
				}
			}
			elsif (length($REF) > 1 && length($ALT) > 1)
			{
				$varType = 'complex_substitution';
				$match = 0;
				
				if ($REF =~ m/^(\w)(\w*)/)
				{
					$REF_base1 = $1;
					$REF_base2 = $2;
					$match = $match + 1;
				}
				
				if ($ALT =~ m/^(\w)(\w*)/)
				{
					$ALT_base1 = $1;
					$ALT_base2 = $2;
					$match = $match + 1;
				}
				
				if ($match == 2)
				{
					$varID = $varID + 1;
					$begin_gvf = $POS + 1;
					$end_gvf = $POS + 1 + length($REF_base2) - 1;
					$reference_gvf = $REF_base2;
					$variant_gvf = $ALT_base2;
					#print output "$chromosome\t$source\t$varType\t$begin_gvf\t$end_gvf\t$score\t$strand\t$phase\tID=$varID;Reference_seq=$reference_gvf;Variant_seq=$variant_gvf;\n";
					$variant_output[$ALT_index] = "$chromosome\t$source\t$varType\t$begin_gvf\t$end_gvf\t$score\t$strand\t$phase\tID=$varID;Reference_seq=$reference_gvf;Variant_seq=$variant_gvf;Variant_feature=$varFeature;Gene=$gene;";
				}
			}
			
		}
		
		$sample_index = 0;
		#report variant and zygosity for each genotype
		foreach (@genotype_all)
		{
			$sample_index = $sample_index + 1;
			$genotype = $_;
			if ($genotype =~ m/^([\d\.])[\|\/]([\d\.])/)
			{
				$allele_1 = $1;
				$allele_2 = $2;
			
				if ($allele_1 ne '.' && $allele_1 >= 1 && $allele_1 != $allele_2)
				{
					print {$filehandle[$sample_index]} "$variant_output[$allele_1]Genotype=heterozygous;\n";
				}
				
				if ($allele_2 ne '.' && $allele_2 >= 1 && $allele_1 != $allele_2)
				{
					print {$filehandle[$sample_index]} "$variant_output[$allele_2]Genotype=heterozygous;\n";
				}
				
				if ($allele_1 ne '.' && $allele_2 ne '.' && $allele_1 >= 1 && $allele_2 >= 1 && $allele_1 == $allele_2)
				{
					print {$filehandle[$sample_index]} "$variant_output[$allele_2]Genotype=homozygous;\n";
				}
			
			}
			
		}
			
	}

}

#close filehandle
$i = 1;
while ($i <= $fileindex)
{
	close $filehandle[$i];
	$i = $i + 1;
}

	
	

	
		


