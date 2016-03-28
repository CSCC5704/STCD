package cs5704.project;

import java.io.BufferedInputStream;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ASTParserTool {
	
	// AST Parser returns the structure of the source code
	public CompilationUnit getCompilationUnit(String javaFilePath){  
		byte[] input = null;
		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(javaFilePath));
			input = new byte[bufferedInputStream.available()];
			bufferedInputStream.read(input);
			bufferedInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();  
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ASTParser astParser = ASTParser.newParser(AST.JLS3);
		
		Map options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_5, options);
		astParser.setCompilerOptions(options);
		
		astParser.setSource(new String(input).toCharArray());
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		CompilationUnit result = (CompilationUnit) (astParser.createAST(null));
		
		return result;
	}
	
	// get methods in source code
	public MethodList parseMethod(CompilationUnit result) {
		
		MethodList methodVectorList = new MethodList();
		
		List types = result.types();
		TypeDeclaration typeDec = (TypeDeclaration) types.get(0);
		
		MethodDeclaration methodDec[] = typeDec.getMethods();
		
		for (MethodDeclaration method : methodDec) {
			// look into each method
			visitMethod(result, method, methodVectorList);
		}
		
		return methodVectorList;
	}
	
	public void visitMethod(CompilationUnit result, MethodDeclaration method, MethodList methodVectorList) {
		
		// get method start line #
		int startLineNumber = result.getLineNumber(method.getStartPosition());
		
		// get method end line #
		int endLineNumber = result.getLineNumber(method.getStartPosition() + method.getLength()) - 1;
				
		// get method name
		String methodName = method.getName().toString();
		
		// get method parameters
		String methodPara = method.parameters().toString();
		
		// get method return type
		String methodType;
		if(method.getReturnType2() != null)
			methodType = method.getReturnType2().toString();
		else
			methodType = "";
		
		// get method body
		String methodBody = method.getBody().toString();

		// tokenize method body
		MethodTokenizerTool tokenizerTool = new MethodTokenizerTool();
		TokenList methodTokenList = tokenizerTool.visit(methodBody);
		
		// construct methodVector
		MethodVector methodVector = new MethodVector(startLineNumber, endLineNumber, methodName,
												methodPara, methodType, methodTokenList);
		// add to methodVectorList
		methodVectorList.addMethodVector(methodVector);
    }
}
