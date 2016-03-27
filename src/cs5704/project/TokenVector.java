package cs5704.project;

public class TokenVector {
	
	// variables
	String TokenName;
	String TokenType;
	int TokenCount;
	
	// constructor
	TokenVector(String name, String type) {
		this.TokenName = name;
		this.TokenType = type;
		this.TokenCount = 1;
	}
	
	// print
	public void print() {
		System.out.printf("%15s%15s%15d\n", this.TokenName, this.TokenType, this.TokenCount);
	}
}
