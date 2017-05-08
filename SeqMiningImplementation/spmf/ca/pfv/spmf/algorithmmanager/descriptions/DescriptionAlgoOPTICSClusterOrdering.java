package ca.pfv.spmf.algorithmmanager.descriptions;

import java.io.IOException;

import ca.pfv.spmf.algorithmmanager.DescriptionOfAlgorithm;
import ca.pfv.spmf.algorithmmanager.DescriptionOfParameter;
import ca.pfv.spmf.algorithms.clustering.distanceFunctions.DistanceFunction;
/* This file is copyright (c) 2008-2016 Philippe Fournier-Viger
* 
* This file is part of the SPMF DATA MINING SOFTWARE
* (http://www.philippe-fournier-viger.com/spmf).
* 
* SPMF is free software: you can redistribute it and/or modify it under the
* terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
* 
* SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
* A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with
* SPMF. If not, see <http://www.gnu.org/licenses/>.
*/
import ca.pfv.spmf.algorithms.clustering.optics.AlgoOPTICS;

/**
 * This class describes the OPTICS algorithm parameters an ordering of points. 
 * It is designed to be used by the graphical and command line interface.
 * 
 * @see AlgoOPTICS
 * @author Philippe Fournier-Viger
 */
public class DescriptionAlgoOPTICSClusterOrdering extends DescriptionOfAlgorithm {

	/**
	 * Default constructor
	 */
	public DescriptionAlgoOPTICSClusterOrdering(){
	}

	@Override
	public String getName() {
		return "OPTICS-cluster-ordering";
	}

	@Override
	public String getAlgorithmCategory() {
		return "CLUSTERING";
	}

	@Override
	public String getURLOfDocumentation() {
		return "http://www.philippe-fournier-viger.com/spmf/index.php?link=documentation.php#optics";
	}

	@Override
	public void runAlgorithm(String[] parameters, String inputFile, String outputFile) throws IOException {
		int minPts = getParamAsInteger(parameters[0]);
		double epsilon = getParamAsDouble(parameters[1]);
		String distanceFunctionName = getParamAsString(parameters[2]);
		DistanceFunction distanceFunction 
			= DistanceFunction.getDistanceFunctionByName(distanceFunctionName);
		
		// We specify that in the input file, double values on each line are separated by spaces
		String separator = " ";
		
		AlgoOPTICS algo = new AlgoOPTICS();  
		algo.computerClusterOrdering(inputFile, minPts, epsilon, separator);
		
		algo.printStatistics();
		algo.saveClusterOrderingToFile(outputFile);
	}

	@Override
	public DescriptionOfParameter[] getParametersDescription() {
        
		DescriptionOfParameter[] parameters = new DescriptionOfParameter[2];
		parameters[0] = new DescriptionOfParameter("minPts", "(e.g. 2)", Integer.class, false);
		parameters[1] = new DescriptionOfParameter("epsilon", "(e.g. 5)", Double.class, false);
		return parameters;
	}

	@Override
	public String getImplementationAuthorNames() {
		return "Philippe Fournier-Viger";
	}

	@Override
	public String[] getInputFileTypes() {
		return new String[]{"Database of instances", "Database of double vectors"};
	}

	@Override
	public String[] getOutputFileTypes() {
		return new String[]{"Clusters", "Density-based cluster ordering of points"};
	}
	
}
