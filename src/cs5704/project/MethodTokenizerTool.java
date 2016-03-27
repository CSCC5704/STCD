package cs5704.project;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.nio.charset.StandardCharsets;

public class MethodTokenizerTool{
	
	// types in java
	private String[] types = {"int", "byte", "short", "long", "char", "float", "double", "boolean",
			"null", "true", "false"};
	
	// keywords in java
	private String[] keywords = {"private", "protected", "public", "abstract", "class", "extends",
			"final", "implements", "interface", "native", "new", "static", "strictfp", "synchronized",
			"transient", "volatile", "break", "continue", "return", "do", "while", "if", "else", "for",
			"instanceof", "switch", "case", "default", "try", "cathc", "throw", "throws", "super",
			"this", "void", "goto", "const"};
	
	private char[] ignores = {' ', ';'};
	
	// markers in java
	private char[] markers = {'{', '}', '[', ']', '(', ')'};
	
	// operators in java
	private char[] operators = {'+', '-', '*', '/', '%', '^', '<', '>', '!', '&', '|', '=', '~'};
	
	public TokenList tokenList = new TokenList();
	
	public TokenList visit(String fragmentBody) {
		try {
			// create the tokenizer to read from fragmentBody
			InputStream iStream = new ByteArrayInputStream(fragmentBody.getBytes(StandardCharsets.UTF_8));
			StreamTokenizer st = new StreamTokenizer(iStream);
			
			// prepare the tokenizer for Java-style tokenizing rules
			st.parseNumbers();
			st.wordChars('_', '_');
			st.eolIsSignificant(true);
			
			// if whitespace is not to be discarded, make this call
			//st.ordinaryChars(0, ' ');

			// if dot is not to be discarded, make this call
			st.ordinaryChars(0, '.');
			
			// discard comments
			st.slashSlashComments(true);
			st.slashStarComments(true);
			
			// core process of tokenizer
			TokenVector tokenVector;
			int index;
			int token = st.nextToken();
			while (token != StreamTokenizer.TT_EOF) {
				token = st.nextToken();
				switch (token) {
				case StreamTokenizer.TT_NUMBER:
					// a number was found; the value is in nval
					double num = st.nval;
					
					String numstr = Double.toString(num);
					
					// if it has already in the tokenList, tokenCount++
					// else add it up to the tokenList and tokenCount is 1
					index = tokenList.getIndexByName(numstr);
					if(index != -1)
						tokenList.setValueByIndex(index);
					else {
						tokenVector = new TokenVector(numstr, "Num");
						tokenList.addTokenVector(tokenVector);
					}
					break;
				case StreamTokenizer.TT_WORD:
					// a word was found; the value is in sval
					String word = st.sval;
					
					// if it has already in the tokenList, tokenCount++
					// else add it up to the tokenList and tokenCount is 1
					index = tokenList.getIndexByName(word);
					if(index != -1)
						tokenList.setValueByIndex(index);
					else {
						if(isStringElement(word, types))
							tokenVector = new TokenVector(word, "Type");
						else if(isStringElement(word, keywords))
							tokenVector = new TokenVector(word, "Keyword");
						else
							tokenVector = new TokenVector(word, "OtherStr");
						tokenList.addTokenVector(tokenVector);
					}
					break;
				case '"':
					// a double-quoted string was found; sval contains the contents
					String dquoteVal = st.sval;
					break;
				case '\'':
					// a single-quoted string was found; sval contains the contents
					String squoteVal = st.sval;
					break;
				case StreamTokenizer.TT_EOL:
					// end of line character found
					break;
				case StreamTokenizer.TT_EOF:
					// end of body has been reached
					break;
				default:
					// a regular character was found; the value is the token itself
					char ch = (char)st.ttype;
					
					// if it has already in the tokenList, tokenCount++
					// else add it up to the tokenList and tokenCount is 1
					index = tokenList.getIndexByName(Character.toString(ch));
					if(index != -1)
						tokenList.setValueByIndex(index);
					else {
						if (isCharElement(ch, ignores))
							break;
						else if(isCharElement(ch, markers))
							tokenVector = new TokenVector(Character.toString(ch), "Marker");
						else if(isCharElement(ch, operators))
							tokenVector = new TokenVector(Character.toString(ch), "Operator");
						else
							tokenVector = new TokenVector(Character.toString(ch), "OtherChar");
						tokenList.addTokenVector(tokenVector);
					}
					break;
				}
			}
		} catch (IOException e) {}

		return tokenList;
	}
	
	// judge whether a char is in the charList
	public boolean isCharElement(char ch, char[] charList) {
		for(int i = 0; i < charList.length ; i++) {
			if (ch == charList[i])
				return true;
		}
		return false;
	}
	
	// judge whether a string is in the strList
	public boolean isStringElement(String str, String[] strList) {
		for(int i = 0; i < strList.length ; i++) {
			if (str.equals(strList[i]))
				return true;
		}
		return false;
	}
}
