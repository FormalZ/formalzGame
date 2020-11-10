package formalz.data;

/**
 * Provide a function to transform a method signature (in string) by inserting retval
 * as an additional parameter. For example "int foo(int x)" will be transformed to
 * "int foo(int retval, int x)". 
 * 
 * A method signature/header has the form of "type method-name(parameter-list)".
 * It may be preceeded with a comment. A parameter-list is zero or or parameters
 * separated by commas. A single parameter is a pair "type parameter-name".
 * 
 * The transformation is necessary to allow the type
 * of retval to be explicitly passed to the Haskell backend. This backend assumes
 * that all variables that occur free in the pre and post-conditions to appear in the
 * method signature, or else it will just default a variable to the type int.
 * 
 * The transformation for now assumes the return type of the method to be either
 * int,short,long,double,float, or boolean, or arrays of these primitive types.
 * Other types are not supported; this is because the Haskell backend currently only
 * support these types.
 * 
 * @author WP
 *
 */
public class SignatureTransformer {
	
	/**
	 * Representing a succesful parse result, consisting of a pair (v,r) where
	 * v is some value which represents the result of parsing
	 * (e.g. it could be an int), and r is the remaining string to parse.
	 */
	public static class SimpleParseResult<T> {
		T value ;
		String rest ;
		SimpleParseResult(T value, String rest) {
			this.value = value ; this.rest = rest ;
		}
		
		/**
		 * @return The value-part of the result. It represents the parsing result.
		 */
		public T getValue() { return value ; }
		/**
		 * @return The rest-part of the result. It represents the remaining string to parse.
		 */
		public String getRest() { return rest ; }
		public String toString() {
			return "" + value + ";" + rest ;
		}
	}
	
	/**
	 * Parse leading white space. Returns the remaining string. Never fails.
	 * @return The remaining string.
	 * @param s The input string to parse.
	 */
	static String parseWhiteSpace(String s) {
		if (s.length() == 0) return s ;
		char c = s.charAt(0) ;
		if (c == ' ' || c == '\n' || c== '\t') return parseWhiteSpace(s.substring(1)) ;
		return s ;
	}
	
	/**
	 * Parse a white-space followed by d. Return the rest of the string.
	 * If d cannot be parsed, return null.
	 * @return  The remaining string if d is recognized. Else null.
	 * @param d The character to recognize.
	 * @param s The input string.
	 */
	static String parseChar(char d, String s) {
		String z = parseWhiteSpace(s) ;
		if (z.length() == 0) return null ;
		char c = z.charAt(0) ;
		if (c == d) return z.substring(1) ;
		return null ;
	}

	/**
	 * Parse a white-space followed by name. Return the rest of the string.
	 * If the name cannot be parsed, return null.
	 *  @return  The remaining string if name is recognized. Else null.
	 *  @param name The string to recognize.
	 *  @param s The input string.
	 */
	static String parseName(String name, String s) {
		String z = parseWhiteSpace(s) ;
		if (z.startsWith(name)) return z.substring(name.length()) ;
		return null ;
	}
	
	/**
	 * Parse a single block of nested comment. It returns the remaining string.
	 * Null is returned if it cannot parse such a block.
	 * @return The remaining string if the parse is successful. Else null.
	 * @param s The input string.
	 */
	static String parseNestedComment(String s) {
		String z = s ;
		z= parseName("/*",z) ;
		if(z == null) return null ;
		//System.err.println(">> " + s + " >> " + z) ;
		while (z.length() >= 2) {
			String head2 = z.substring(0,2) ;
			//System.err.println(">> head2 = " + head2) ;
			if (head2.equals("*/")) return z.substring(2) ;
			if (head2.equals("/*")) {
				z = parseNestedComment(z) ;
				if (z == null) return null ;
			}
			else z = z.substring(1) ;
		}
		// fail to see a matching close-comment
		return null ;
	}
	
	/**
	 * Parse 0 or more nested-comment block. It returns the remaining string.
	 * This parser never fails.
	 * @return The remaining string.
	 * @param s The input string.
	 */
	static String parseMany_NestedComment(String s) {
		String z = s ;
		z = parseNestedComment(s) ;
		//System.err.println(">> " + s + " >> " + z) ;
		if (z==null) return s ;
		return parseMany_NestedComment(z) ;
	}
	
	/**
	 * Remove leading white space and nested comments. Never fails.
	 * @return The remaining string.
	 * @param s The input string.
	 */
	static String remove_leading_white_andComment(String s) {
		s = parseMany_NestedComment(s) ;
		return parseWhiteSpace(s) ;
	}

		
	/**
	 * Parse "white [ white ]". Return the rest of the string.
	 * If the pattern cannot be parsed, return null.
	 * @return The remaining string if the parse is successful. Else null.
	 * @param s The input string.
	 */
	static String parseSQbracketPair(String s) {
		String z = parseChar('[',s) ;
		if (z==null) return null ;
		z = parseChar(']',z) ;
		return z ;
	}
	
	/**
	 * Parse one or more " [ ] ". Return a pair (k,r) where k is the number
	 * of bracket-pairs that can be parsed, and r is the rest of the string.
	 * If the pattern cannot be recognized, return null.
	 * @return The remaining string if the parse is successful. Else null.
	 * @param s The input string.
	 */
	static SimpleParseResult<Integer> parse_many1_SQbracketPair(String s) {
		String z = parseSQbracketPair(s) ;
		if (z==null) return null ;
		SimpleParseResult<Integer> recursionResult = parse_many1_SQbracketPair(z) ;
		if (recursionResult == null) {
			return new SimpleParseResult(1,z) ;
		}
		recursionResult.value++ ;
		return recursionResult ;
	}

	
	/**
	 * Construct a sequence of "[]", k-times.
	 * @param k Specifying how many "[]" to repeat.
	 * @return The resulting sequence of "[]".
	 */
	static String mkSqBracketPairs(int k) {
		if (k<=0) return "" ;
		return "[]" + mkSqBracketPairs(k-1) ;
	}
	
	/**
	 * Parse the return type of a method signature. The signature is assumed to start by declaring
	 * its return type. Only int,short,long,double,float, or boolean, or arrays of these primitive types
	 * will be recognized.
	 * 
	 * @param  methodSignature A string containing a method signature/header.
	 * @return If the return type can be recognized, return.value will contain this type (in String), 
	 * and return.rest is the remaining string to parse. Else it is null.
	 */
	static SimpleParseResult<String> parseReturnType(String methodSignature) {
		methodSignature = remove_leading_white_andComment(methodSignature) ;
		// int or int[][]...
		String basetype = null ;
		String z = null ;
		while(true) { // a way to encode a case-statement....
			z = parseName("void",methodSignature) ;
			if (z != null)	/* force fail ... retval is not relevant for this case */ return null ;
			z = parseName("int",methodSignature) ;
			if (z != null) { basetype = "int" ; break ; }
			z = parseName("short",methodSignature) ;
			if (z != null) { basetype = "short" ; break ; }
			z = parseName("long",methodSignature) ;
			if (z != null) { basetype = "long" ; break ; }
			z = parseName("float",methodSignature) ;
			if (z != null) { basetype = "float" ; break ; }
			z = parseName("double",methodSignature) ;
			if (z != null) { basetype = "double" ; break ; }
			z = parseName("boolean",methodSignature) ;
			if (z != null) { basetype = "boolean" ; break ; }
			// the base type is not one of the above: then fail
			return null ;
		}
		// check if it is an array-type
		SimpleParseResult<Integer> dim = parse_many1_SQbracketPair(z) ;
		if (dim==null) 
			return new SimpleParseResult(basetype,z) ;
		else
		    return new SimpleParseResult(basetype + mkSqBracketPairs(dim.value), dim.rest) ;
	}
	
	/**
	 * To parse a method name. It looks for the pattern "name(..".
	 * @param s The input string.
	 * @return If the pattern can be recognized, return.value contains the type (in String) and
	 * return.rest the rest of the string to parse. Else it is null.
	 */
	static SimpleParseResult<String> parseMethodName(String s) {
		String z = parseWhiteSpace(s) ;
		int k = z.indexOf('(') ;
		String name = z.substring(0,k) ;
		return new SimpleParseResult(name,z.substring(k)) ;
	}
	
	/**
	 * Parse a single method modifier keyword (private,protected,public,static, or synchronized).
	 * @param  The input string to parse.
	 * @return return.val is the parsed keyword, and return.rest the the remaining string to parse.
	 * Null if the parse fails.
	 */
	static SimpleParseResult<String> parseModifier1(String s) {
		String z = s ;
		while(true) {
			z = parseName("private",s) ;
			if (z != null) return new SimpleParseResult("private",z) ;
			z = parseName("protected",s) ;
			if (z != null) return new SimpleParseResult("protected",z) ;
			z = parseName("public",s) ;
			if (z != null) return new SimpleParseResult("public",z) ;
			z = parseName("static",s) ;
			if (z != null) return new SimpleParseResult("static",z) ;
			z = parseName("synchronized",s) ;
			if (z != null) return new SimpleParseResult("synchronized",z) ;
			return null ;		
		}
	}
	
	/**
	 * Parse a sequence of method modifier keywords (private,protected,public,static, or synchronized).
	 * There can be at most r=three modifiers.
	 * 
	 * @param  The input string to parse.
	 * @return return.val is a string containing the sequence of parsed keywords, and return.rest the the remaining string to parse.
	 * Null if the parse fails.
	 */
	static SimpleParseResult<String> parseModifiers(String s) {
		SimpleParseResult<String> m1 = parseModifier1(s) ;
		if (m1 == null) return null ;
		SimpleParseResult<String> m2 = parseModifier1(m1.rest) ;
		if (m2 == null) return m1 ;
		SimpleParseResult<String> m3 = parseModifier1(m2.rest) ;
		if (m3 == null) {
			m2.value = m1.value + " " + m2.value ;
			return m2 ;
		}
		else {
			m3.value = m1.value + " " + m2.value + " " + m3.value ;
			return m3 ;
		}
	}
	
	/**
	 * To transform a given method signature by inserting "retval" as an explicit parameter
	 * for the method, with the correct type (the same as the method return type). The
	 * signature may start with some comment blocks, as long as they are well formed
	 * (symetrically closed). Only int,short,long,double,float, or boolean, or arrays 
	 * of these primitive types as the method'd return type will be recognized.
	 * 
	 * If this method cannot parse the signature, it will return null.
	 * 
	 * @param s The input method signature.
	 * @return If the pattern can be recognized, return the new signature. Else null.
	 */
	public static String insertRetval_in_methodSignature(String msignature) {
		/* remove leading white-space and comment */
		msignature = remove_leading_white_andComment(msignature) ;
		/* parse modifiers like static, public etc. There can be at most three */
		SimpleParseResult<String> modifiers = parseModifiers(msignature) ;
		if (modifiers != null) msignature = modifiers.rest ;
		/* parse the return type */
		SimpleParseResult<String> returnType = parseReturnType(msignature) ;
		if (returnType == null) return null ;
		/* parse the method name */
		SimpleParseResult<String> mname = parseMethodName(returnType.rest) ;
		/* find the beginning of the parameter list, and insert retval there :) */
		String z = parseChar('(',mname.rest) ;
		if (z==null) return null ;
		// check first if the method has parameter
		boolean hasParameter = true ;
		if(parseChar(')',z)!=null) hasParameter = false ;
		
		String newSignature = "" ;
		if (modifiers != null) newSignature += modifiers.value + " ";
		
		newSignature += returnType.value + " " + mname.value
				        + "("
				        + returnType.value + " retval"  ;
	    if (hasParameter) newSignature += "," ;
	    newSignature += z ;
	    return newSignature ;
	}
	
	
		
   /*
	public static void main(String[] args) {
		// use JShell to do quick interactive testing of this class
	}
   */

}
