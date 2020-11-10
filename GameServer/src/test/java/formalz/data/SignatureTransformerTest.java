package formalz.data;

import org.junit.Test;
import static org.junit.Assert.*;
import static formalz.data.SignatureTransformer.*;

public class SignatureTransformerTest {
     
	@Test
	public void test_emptyparam() {
		assertEquals(null,insertRetval_in_methodSignature("void foo()")) ;
		assertEquals("int foo(int retval)",insertRetval_in_methodSignature("int foo()")) ;
	}
     
	
	@Test
	public void test_non_emptyparam() {
		assertEquals(null,insertRetval_in_methodSignature("void foo(double x)")) ;
		assertEquals("int foo(int retval,double x)",insertRetval_in_methodSignature("int foo(double x)")) ;
		assertEquals("int foo(int retval,double x, int[] y)",insertRetval_in_methodSignature("int foo(double x, int[] y)")) ;
	}
	
	@Test
	public void test_whitespaces() {
		assertEquals("boolean foo(boolean retval,double x)",insertRetval_in_methodSignature("  boolean  foo(double x)")) ;
		assertEquals("boolean foo  (boolean retval,double x)",insertRetval_in_methodSignature("  boolean  foo  (double x)")) ;
		assertEquals("boolean foo  (boolean retval,double   x)",insertRetval_in_methodSignature("  boolean  foo  (double   x)")) ;
	}
	
	@Test
	public void test_basetype() {
		assertEquals("boolean foo(boolean retval,double x)",insertRetval_in_methodSignature("boolean foo(double x)")) ;
		assertEquals("int foo(int retval,double x)",insertRetval_in_methodSignature("int foo(double x)")) ;
		assertEquals("short foo(short retval,double x)",insertRetval_in_methodSignature("short foo(double x)")) ;
		assertEquals("long foo(long retval,double x)",insertRetval_in_methodSignature("long foo(double x)")) ;
		assertEquals("float foo(float retval,double x)",insertRetval_in_methodSignature("float foo(double x)")) ;
		assertEquals("double foo(double retval,double x)",insertRetval_in_methodSignature("double foo(double x)")) ;
		assertEquals(null,insertRetval_in_methodSignature("void foo(double x)")) ;
		assertEquals(null,insertRetval_in_methodSignature("Object foo(double x)")) ;
		assertEquals(null,insertRetval_in_methodSignature("String foo(double x)")) ;
	}
     
	@Test
	public void test_arraytype() {
		assertEquals("long[] foo(long[] retval,double x)",insertRetval_in_methodSignature("long[] foo(double x)")) ;
		assertEquals("long[] foo(long[] retval,double x)",insertRetval_in_methodSignature("long[  ] foo(double x)")) ;
		assertEquals("long[] foo(long[] retval,double x)",insertRetval_in_methodSignature("long [ ] foo(double x)")) ;
		assertEquals("long[][] foo(long[][] retval,double x)",insertRetval_in_methodSignature("long[][] foo(double x)")) ;
		assertEquals("long[][] foo(long[][] retval,double x)",insertRetval_in_methodSignature("long [ ] [] foo(double x)")) ;
		assertEquals("long[][][][] foo(long[][][][] retval,double x)",insertRetval_in_methodSignature("long[][] [] []foo(double x)")) ;
	}
	
	@Test
	public void test_removePreceedingComments() {
		assertEquals("boolean foo(boolean retval,double x)",insertRetval_in_methodSignature("/*bla*/boolean foo(double x)")) ;
		assertEquals("boolean foo(boolean retval,double x)",insertRetval_in_methodSignature("  /*bla*/  boolean foo(double x)")) ;
		assertEquals("boolean foo(boolean retval,double x)",insertRetval_in_methodSignature("  /* bla */ boolean foo(double x)")) ;
		assertEquals("boolean foo(boolean retval,double x)",insertRetval_in_methodSignature(" /*bla*//*bla*/boolean foo(double x)")) ;
		assertEquals("boolean foo(boolean retval,double x)",insertRetval_in_methodSignature("/*bla*/ /* bla */ boolean foo(double x)")) ;
		assertEquals("boolean foo(boolean retval,double x)",insertRetval_in_methodSignature(" /*bla /*bla*/ /*bla*/ */ /*bla*/ boolean foo(double x)")) ;
		assertEquals("boolean foo(boolean retval,double x)",insertRetval_in_methodSignature(" /*bla /*/*bla*/ /*bla*/*/ */ /*bla*/ boolean foo(double x)")) ;	
	}
	
	@Test
	public void test_modifiers() {
		assertEquals("public float[] foo(float[] retval,boolean x)",insertRetval_in_methodSignature("public float[] foo(boolean x)")) ;
		assertEquals("public static float[] foo(float[] retval,boolean x)",insertRetval_in_methodSignature("public static  float[] foo(boolean x)")) ;
		assertEquals("static public float[] foo(float[] retval,boolean x)",insertRetval_in_methodSignature("  static public   float[] foo(boolean x)")) ;
		assertEquals("static private synchronized float[] foo(float[] retval,boolean x)",insertRetval_in_methodSignature("  static private synchronized   float[] foo(boolean x)")) ;
		assertEquals("synchronized static protected float[] foo(float[] retval,boolean x)",insertRetval_in_methodSignature("synchronized  static protected    float[] foo(boolean x)")) ;
		
	}
	
     
}
