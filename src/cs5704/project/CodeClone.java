package cs5704.project;

public class CodeClone {
	
	public static String filename = "testfiles/testfile4.java";
	
	public static ASTParserTool parserTool = new ASTParserTool();

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub	
		MethodList methodVectorList = parserTool.parseMethod(parserTool.getCompilationUnit(filename));
		
		MethodSimilarity methodSim = new MethodSimilarity();
		methodSim.simDetector(methodVectorList);
		/*
		double similarity[][] = {
				{0.071,0.000,0.414,0.333,0.055,0.414,0.087,0.143},
				{0.077,0.000,0.414,0.333,0.062,0.191,0.127,0.137},
				{0.091,0.000,0.414,0.333,0.062,0.191,0.127,0.137},
				{0.480,0.000,0.274,0.366,0.093,0.191,0.095,0.195},
				{0.846,0.000,1.000,1.000,0.069,0.261,0.176,0.333},
				{0.545,1.000,1.000,1.000,0.071,0.261,0.176,0.333},
				{0.080,0.000,0.274,0.500,0.059,0.261,0.195,0.309},
				{0.600,0.000,1.000,1.000,0.079,1.000,1.000,1.000},
				{0.087,0.000,0.274,0.500,0.066,1.000,0.195,0.309},
				{0.105,0.000,0.274,0.500,0.066,1.000,0.195,0.309}};
        int clone[][] = {{0}, {0}, {0}, {0}, {1}, {1}, {0}, {1}, {0}, {0}};
        
		MultiplePerceptionTool MPL = new MultiplePerceptionTool(8, 5, 1);
		
		for(int i = 0; i < 500; i++) {
            for(int j = 0; j < similarity.length; j++)
                MPL.trainMLP(1, similarity[j], clone[j]);
        }
		
		
		double test[][] = {
				{0.071,0.000,0.414,0.333,0.055,0.414,0.087,0.143},
				{0.077,0.000,0.414,0.333,0.062,0.191,0.127,0.137},
				{0.091,0.000,0.414,0.333,0.062,0.191,0.127,0.137},
				{0.480,0.000,0.274,0.366,0.093,0.191,0.095,0.195},
				{0.846,0.000,1.000,1.000,0.069,0.261,0.176,0.333},
				{0.545,1.000,1.000,1.000,0.071,0.261,0.176,0.333},
				{0.080,0.000,0.274,0.500,0.059,0.261,0.195,0.309},
				{0.600,0.000,1.000,1.000,0.079,1.000,1.000,1.000},
				{0.087,0.000,0.274,0.500,0.066,1.000,0.195,0.309},
				{0.105,0.000,0.274,0.500,0.066,1.000,0.195,0.309}};
		int out[] = {0};
		
		for(int i = 0; i < test.length; i++) {
			MPL.cloneDetector(test[i], out);
			System.out.println(out[0]);
		}
		*/
	}
}