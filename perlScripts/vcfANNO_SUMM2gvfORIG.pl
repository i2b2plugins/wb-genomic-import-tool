#!/usr/bin/perl

use Text::CSV;
my $csv = Text::CSV->new({sep_char => ','});

$varID = 0;
$unknown = ".";


open input, "<$ARGV[0]"; #input VCF file (for sample header)
open input2, "<$ARGV[1]"; #input SUMMARY ANNOVAR FILE

$source = 'SUMMARIZE_ANNOVAR';

$fileindex = 0;
@filehandle = ();

#read in sample numbers
$numberOfSamples = 0;
while ($line = readline input)
{
	chomp($line);

	if ($line =~ m/^#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\t(.+)/)
	{
		@sample_all = split(/\t/, $1);
		foreach (@sample_all)
		{
			$numberOfSamples = $numberOfSamples + 1;
			$fileindex = $fileindex + 1;
			$filehandle[$fileindex] = 'output'.$fileindex;
			$filename = $_.'.gvf';
			print $filename;
			open $filehandle[$fileindex], ">$filename";
			
			#print Pragma
			print {$filehandle[$fileindex]} "##gvf-version 1.05\n";
			print {$filehandle[$fileindex]} "##file-version 1.0\n";
			print {$filehandle[$fileindex]} "##feature-ontology http://sourceforge.net/projects/song/files/Sequence%20Ontology/so_2_4_4/so_2_4_4.obo/download\n";
			print {$filehandle[$fileindex]} "##genome-build NCBI B37.3\n";
			print {$filehandle[$fileindex]} "##sequence-region\n";
		
		}
		last;
	}
	
}
	
	#read in first line of SUMMARIZE_ANNOVAR (header)
$header = readline input2;

$lineNumber = 1;
while($line = readline input2)
{
	chomp($line);
	if($csv->parse($line))
	{
		;
		$lineNumber = $lineNumber + 1;
		print "$lineNumber\n";	
		@fields = $csv->fields();
		
		$varFeature = $fields[0];
		$gene = $fields[1];
		my @function = split(" ",$fields[2]);
		$exonicFunction = $function[0];
		$code = $function[1];
		$dbSNP = $fields[8];
		$PP2 = $fields[14];
		$PP2Pred = $fields[15];
		
		$chromosome = 'chr'.$fields[21];
		$POS_start = $fields[22];
		$POS_end = $fields[23];

		$REF = $fields[24];
		@ALT_all = split(',', $fields[25]);
			
		
		#25 = PASS
		#26= INFO
		#27 = GT:DS:PL
		

#		$QUAL = $13;
#		$FILTER = $14;
#		@genotype_all = split(/\t/, $17);
		
		
#		$variant_output[$ALT_index] = "$chromosome\t$source\t$varType\t$begin_gvf\t$end_gvf\t$score\t$strand\t$phase\tID=$varID;Reference_seq=$reference_gvf;Variant_seq=$variant_gvf;Variant_feature=$varFeature;Gene=$gene;";

		$variant_output = "$chromosome\t$source\t$code\t$POS_start\t$POS_end\t$unknown\t$unknown\t$unknown\tID=$varID;Reference_seq=$REF;Variant_seq=$ALT_all[0];Variant_feature=$varFeature;Gene=$gene;dbSNP=$dbSNP;ExonicFunction=$exonicFunction;PP2Score=$PP2;PP2Pred=$PP2Pred;";
#		$variant_output[$ALT_index] = "$chromosome\t$source\t$varType\t$begin_gvf\t$end_gvf\t$score\t$strand\t$phase\tID=$varID;Reference_seq=$reference_gvf;Variant_seq=$variant_gvf;Variant_feature=$varFeature;Gene=$gene;";
#		$variant_output[$ALT_index] = "$chromosome\t$source\t$varType\t$begin_gvf\t$end_gvf\t$score\t$strand\t$phase\tID=$varID;Reference_seq=$reference_gvf;Variant_seq=$variant_gvf;Variant_feature=$varFeature;Gene=$gene;";
		
		my @genotype_all = @fields[29..$numberOfSamples+29];
		
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
					print {$filehandle[$sample_index]} $variant_output."Genotype=heterozygous;\n";
				}
				
				if ($allele_2 ne '.' && $allele_2 >= 1 && $allele_1 != $allele_2)
				{
					print {$filehandle[$sample_index]} $variant_output."Genotype=heterozygous;\n";
				}
				
				if ($allele_1 ne '.' && $allele_2 ne '.' && $allele_1 >= 1 && $allele_2 >= 1 && $allele_1 == $allele_2)
				{
					print {$filehandle[$sample_index]} $variant_output."Genotype=homozygous;\n";
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

	
	

	
		


