package cs5704.project;

public class MultiplePerceptionTool {
	
	private static double increVal = 0.35;
	public int trainedEpochs = 0;
	
	// nodes in MLP
	public double inputNodes[];
	public double hiddenNodes[];
	public double outputNodes[];
	
	// weight and delta in hidden nodes
	public double weight1[][];
	public double delta1[];
	
	// weight and delta in output nodes
	public double weight2[][];
	public double delta2[];
	
	// constructor
	public MultiplePerceptionTool(int inputSize, int hiddenSize, int outputSize) {
		
		// initial variables
		inputNodes = new double[inputSize];
		hiddenNodes = new double[hiddenSize];
		outputNodes = new double[outputSize];
		
		delta1 = new double[hiddenSize];
		delta2 = new double[outputSize];
		
		weight1 = new double[inputSize][hiddenSize];
		weight2 = new double[hiddenSize][outputSize];
		
		// set weight of each node link from 0.1 to 0.9
		for(int index1 = 0; index1 < weight1.length; index1++)
			for(int index2 = 0; index2 < weight1[index1].length; index2++)
				weight1[index1][index2] = Math.random() * 0.8 + 0.1;
		for(int index1 = 0; index1 < weight2.length; index1++)
			for(int index2 = 0; index2 < weight2[index1].length; index2++)
				weight2[index1][index2] = Math.random() * 0.8 + 0.1;
	}
	
	// train test data to get proper weights
	public void trainMLP(int trainTimes, double[] similarity, int[] clone) {
		for(int i = 0; i < trainTimes; i++) {
			adjustWeight(similarity, clone);
			trainedEpochs++;
		}
	}
	
	// use a pair of input and output to adjust the weights
	public void adjustWeight(double[] similarity, int[] clone) {
		
		// initial inputNodes' values
		for(int i = 0; i < similarity.length; i++)
			inputNodes[i] = similarity[i];
		
		// from inputNodes to hiddenNodes, calculate hiddenNodes' value
		for(int i = 0; i < hiddenNodes.length; i++) {
			double sum = 0;
			for(int j = 0; j < inputNodes.length; j++)
				sum = sum + weight1[j][i] * inputNodes[j];
			hiddenNodes[i] = 1 / ( 1 + Math.exp(-sum));
		}
		
		// from hiddenNodes to outputNodes, calculate outputNodes' value
		for(int i = 0; i < outputNodes.length; i++) {
			double sum = 0;
			for(int j = 0; j < hiddenNodes.length; j++)
				sum = sum + weight2[j][i] * hiddenNodes[j];
			outputNodes[i] = 1 / (1 + Math.exp(-sum));
		}
		
		// go back to calculate delta and adjust weight 
		for(int i = 0; i < outputNodes.length; i++) {
			double out_back;
			if( clone[i] == 0 )
				out_back = 0.1;
			else if( clone[i] == 1 )
				out_back = 0.9;
			else
				out_back = clone[i];
			delta2[i] = outputNodes[i] * (1 - outputNodes[i]) * (out_back - outputNodes[i]);
		}
		
		for(int i = 0; i < hiddenNodes.length; i++) {
			double sum = 0;
			for(int j = 0; j < outputNodes.length; j++)
				sum = sum + delta2[j] * weight2[i][j];
			delta1[i] = hiddenNodes[i] * (1 - hiddenNodes[i]) * sum;
		}
		
		for(int i = 0; i < hiddenNodes.length; i++)
			for(int j = 0; j < outputNodes.length; j++)
				weight2[i][j] = weight2[i][j] + increVal * delta2[j] * hiddenNodes[i];
		for(int i = 0; i < inputNodes.length; i++)
			for(int j = 0; j < hiddenNodes.length; j++)
				weight1[i][j] = weight1[i][j] + increVal * delta1[j] * inputNodes[i];
	}
	
	// detect code clone with test data
	public double[] cloneDetector(double[] similarity) {
		
		// the process is the same as the forward process in adjustWeight
		for(int i = 0; i < similarity.length; i++)
			inputNodes[i] = similarity[i];
		
		for(int i = 0; i < hiddenNodes.length; i++) {
			double sum = 0;
			for(int j = 0; j < inputNodes.length; j++)
				sum = sum + weight1[j][i] * inputNodes[j];
			hiddenNodes[i] = 1 / ( 1 + Math.exp(-sum));
		}
		
		double output[] = new double[1];
		for(int i = 0; i < outputNodes.length; i++) {
			double sum = 0;
			for(int j = 0; j < hiddenNodes.length; j++)
				sum = sum + weight2[j][i] * hiddenNodes[j];
			//outputNodes[i] = 1 / (1 + Math.exp(-sum));
			output[i] = 1 / (1 + Math.exp(-sum));
		}
				
		return output;
		/*
		for(int i = 0; i < outputNodes.length; i++) {
			if(outputNodes[i] >= 0.5 )
				clone[i] = 1;
			else
				clone[i] = 0;
		}
		*/
	}
}