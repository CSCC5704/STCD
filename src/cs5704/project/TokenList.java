package cs5704.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TokenList{
	
	// ArrayList of TokenVector
	public List<TokenVector> tokenList = new ArrayList<TokenVector>();
	
	// get tokenVector
	public TokenVector getTokenVector(int index) {
		return tokenList.get(index);
	}
	// add function
	public void addTokenVector(TokenVector tv) {
		tokenList.add(tv);
	}
	
	// size
	public int size() {
		return tokenList.size();
	}
	
	// look up with TokenName
	public int getIndexByName(String name) {
		for(int i = 0; i < tokenList.size(); i++) {
			if(tokenList.get(i).TokenName.equals(name))
				return i;
		}
		return -1;
	}
	
	// increase TokenCount
	public void setValueByIndex(int index) {
		tokenList.get(index).TokenCount++;
	}
	
	// get tokenList by tokenType
	public TokenList getListByType(String type) {
		TokenList tv = new TokenList();
		for(int i = 0; i < tokenList.size(); i++) {
			if(tokenList.get(i).TokenType.equals(type))
				tv.addTokenVector(tokenList.get(i));
		}
		return tv;
	}
	
	// sort ArrayList by TokenName
	public void sortListByName() {
		Collections.sort(tokenList, new Comparator<TokenVector>() {
	        public int compare(TokenVector arg0, TokenVector arg1) {
	            return arg0.TokenName.compareTo(arg1.TokenName);
	        }
	    });
	}
	
	// sort ArrayList by TokenType
	public void sortListByType() {
		Collections.sort(tokenList, new Comparator<TokenVector>() {
	        public int compare(TokenVector arg0, TokenVector arg1) {
	            return arg0.TokenType.compareTo(arg1.TokenType);
	        }
	    });
	}
	
	// sort ArrayList by TokenCount
	public void sortListByCount() {
		Collections.sort(tokenList, new Comparator<TokenVector>() {
	        public int compare(TokenVector arg0, TokenVector arg1) {
	            return arg1.TokenCount - arg0.TokenCount;
	        }
	    });
	}
	
	// print
	public void print() {
		for(int i = 0; i < tokenList.size(); i++) {
			tokenList.get(i).print();
		}
	}
}
