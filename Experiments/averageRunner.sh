IFS=$'\n'
EXP_DIR=`pwd`
for config in `cat test_cases_average`;
do
	IFS=$' '
	cd "../Official Material/Novratis/src"
	java -cp . GraphAnalysis.TAPLoadGenerator $config
	echo ".................................."
	echo ".................................."
	echo ".................................."
	OtherPart=`echo $config| sed 's/ /_/ig'`
	DataFileName="data_$OtherPart"
	echo "$DataFileName"
	StdRuntime1=`java -cp . GraphAnalysis.TimeVariantBipartiteGraph ./ $DataFileName`
	StdRuntime2=`java -cp . GraphAnalysis.TimeVariantBipartiteGraph ./ $DataFileName`
	StdRuntime3=`java -cp . GraphAnalysis.TimeVariantBipartiteGraph ./ $DataFileName`
	StdRuntime4=`java -cp . GraphAnalysis.TimeVariantBipartiteGraph ./ $DataFileName`
	StdRuntime5=`java -cp . GraphAnalysis.TimeVariantBipartiteGraph ./ $DataFileName`
	StdRuntime=`echo "($StdRuntime1+$StdRuntime2+$StdRuntime3+$StdRuntime4+$StdRuntime5)/5.0" | bc -l`
	echo "Average java runtime is $StdRuntime"
	StandardOutputFileName="output_LongPatterns_"$DataFileName"_4_3.txt"
	mv "$DataFileName" "$EXP_DIR"
	mv "$StandardOutputFileName" "$EXP_DIR"
	echo ".................................."
	echo ".................................."
	echo ".................................."
	cd "$EXP_DIR"
	pwd
	MyRunTime1=`./seqMiner ./ "$DataFileName" | grep Time | cut -d':' -f2`
	MyRunTime2=`./seqMiner ./ "$DataFileName" | grep Time | cut -d':' -f2`
	MyRunTime3=`./seqMiner ./ "$DataFileName" | grep Time | cut -d':' -f2`
	MyRunTime4=`./seqMiner ./ "$DataFileName" | grep Time | cut -d':' -f2`
	MyRunTime5=`./seqMiner ./ "$DataFileName" | grep Time | cut -d':' -f2`
	MyRunTime=`echo "($MyRunTime1+$MyRunTime2+$MyRunTime3+$MyRunTime4+$MyRunTime5)/5.0" | bc -l`
	echo "Average cpp runtime is $MyRunTime"
	rm -rf "$OtherPart"
	mkdir "$OtherPart"
	mv "$DataFileName" ./"$OtherPart"/
	mv "$StandardOutputFileName" ./"$OtherPart"/
	mv TAPs.txt ./"$OtherPart"/
	echo "$OtherPart","$StdRuntime","$MyRunTime" >> experiment_results_average
	IFS=$'\n'
done