package cs5704.project;

public class MethodVector {
	
	// variables
	int startLineNumber;
	int endLineNumber;
	String methodName;
	String methodPara;
	String methodType;
	TokenList methodTokenList;
	
	// constructor
	MethodVector(int start, int end, String name, String para, 
					String type, TokenList tokenlist) {
		this.startLineNumber = start;
		this.endLineNumber = end;
		this.methodName = name;
		this.methodPara = para;
		this.methodType = type;
		this.methodTokenList = tokenlist;
	}
	
	// print
	public void print() {
		System.out.println("Fragment Name: " + methodName);
		System.out.println("Start #: " + startLineNumber);
		System.out.println("End #: " + endLineNumber);
		System.out.println("Fragment Para: " + methodPara);
		System.out.println("Fragment Type: " + methodType);
		
		System.out.println("Token Frequency:");
		methodTokenList.sortListByCount();
		methodTokenList.print();
		System.out.println();
	}
}
