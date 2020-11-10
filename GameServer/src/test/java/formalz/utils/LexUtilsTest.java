/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import formalz.utils.Token.TokenType;

public class LexUtilsTest {

    /**
     * Test whether a condition for sorting is lexed correctly.
     */
    @Test
    public void lexTestSort() {
        String input = "forall(a,i->a[i]>=a[i+1])";
        Token[] output = new Token[] { new Token(TokenType.Quantifier, "forall"), new Token(TokenType.Syntax, "("),
                new Token(TokenType.Variable, "a"), new Token(TokenType.Syntax, ","),
                new Token(TokenType.Variable, "i"), new Token(TokenType.Syntax, "->"),
                new Token(TokenType.Variable, "a"), new Token(TokenType.ArrayIndex, "["),
                new Token(TokenType.Variable, "i"), new Token(TokenType.ArrayIndex, "]"),
                new Token(TokenType.RelationalComparer, ">="), new Token(TokenType.Variable, "a"),
                new Token(TokenType.ArrayIndex, "["), new Token(TokenType.Variable, "i"),
                new Token(TokenType.Arithmetic, "+"), new Token(TokenType.Number, "1"),
                new Token(TokenType.ArrayIndex, "]"), new Token(TokenType.Syntax, ")") };
        assertArrayEquals(output, LexUtils.lex(input));
    }

    /**
     * Test whether a condition containing array related tokens is lexed correctly.
     */
    @Test
    public void lexTestArrayNull() {
        String input = "a!=null && a.length > 0";
        Token[] output = new Token[] { new Token(TokenType.Variable, "a"), new Token(TokenType.EqualityComparer, "!="),
                new Token(TokenType.Null, "null"), new Token(TokenType.LogicOperator, "&&"),
                new Token(TokenType.Variable, "a"), new Token(TokenType.Length, ".length"),
                new Token(TokenType.RelationalComparer, ">"), new Token(TokenType.Number, "0") };
        assertArrayEquals(output, LexUtils.lex(input));
    }

    /**
     * Test whether a condition containing logic operators and booleans is lexed
     * correctly.
     */
    @Test
    public void lexTestBoolLogic() {
        String input = "!a!=true&&||false";
        Token[] output = new Token[] { new Token(TokenType.Not, "!"), new Token(TokenType.Variable, "a"),
                new Token(TokenType.EqualityComparer, "!="), new Token(TokenType.Boolean, "true"),
                new Token(TokenType.LogicOperator, "&&"), new Token(TokenType.LogicOperator, "||"),
                new Token(TokenType.Boolean, "false") };
        assertArrayEquals(output, LexUtils.lex(input));
    }

    /**
     * Test whether a condition containing arithmetic is lexed correctly.
     */
    @Test
    public void lexTestArithmetic() {
        String input = "\\ *+/-%;=";
        Token[] output = new Token[] { new Token(TokenType.Arithmetic, "*"), new Token(TokenType.Arithmetic, "+"),
                new Token(TokenType.Arithmetic, "/"), new Token(TokenType.Arithmetic, "-"),
                new Token(TokenType.Arithmetic, "%") };
        assertArrayEquals(output, LexUtils.lex(input));
    }

    /**
     * Test whether a condition containing comparator operators is lexed correctly.
     */
    @Test
    public void lexTestComparer() {
        String input = "<<>===>!=";
        Token[] output = new Token[] { new Token(TokenType.RelationalComparer, "<"),
                new Token(TokenType.RelationalComparer, "<"), new Token(TokenType.RelationalComparer, ">="),
                new Token(TokenType.EqualityComparer, "=="), new Token(TokenType.RelationalComparer, ">"),
                new Token(TokenType.EqualityComparer, "!=") };
        assertArrayEquals(output, LexUtils.lex(input));
    }

    /**
     * Test whether a condition containing variables and numbers is lexed correctly.
     */
    @Test
    public void lexTestVarsNums() {
        String input = "_ASDFddds  exists1.23%5 abe54;()0.5f forallr";
        Token[] output = new Token[] { new Token(TokenType.Variable, "_ASDFddds"),
                new Token(TokenType.Quantifier, "exists"), new Token(TokenType.Real, "1.23"),
                new Token(TokenType.Arithmetic, "%"), new Token(TokenType.Number, "5"),
                new Token(TokenType.Variable, "abe54"), new Token(TokenType.Syntax, "("),
                new Token(TokenType.Syntax, ")"), new Token(TokenType.Real, "0.5f"),
                new Token(TokenType.QuantifierR, "forallr") };
        assertArrayEquals(output, LexUtils.lex(input));
    }

    /**
     * Test whether a condition containing implications is lexed correctly.
     */
    @Test
    public void lexTestImp() {
        String input = "imp(true, true && b)";
        Token[] output = new Token[] { new Token(TokenType.Implication, "imp"), new Token(TokenType.Syntax, "("),
                new Token(TokenType.Boolean, "true"), new Token(TokenType.Syntax, ","),
                new Token(TokenType.Boolean, "true"), new Token(TokenType.LogicOperator, "&&"),
                new Token(TokenType.Variable, "b"), new Token(TokenType.Syntax, ")") };
        assertArrayEquals(output, LexUtils.lex(input));
    }

    /**
     * Test whether a condition containing with is lexed correctly.
     */
    @Test
    public void lexTestWith() {
        String input = "with(a[i], ai -> ai)";
        Token[] output = new Token[] { new Token(TokenType.With, "with"), new Token(TokenType.Syntax, "("),
                new Token(TokenType.Variable, "a"), new Token(TokenType.ArrayIndex, "["),
                new Token(TokenType.Variable, "i"), new Token(TokenType.ArrayIndex, "]"),
                new Token(TokenType.Syntax, ","), new Token(TokenType.Variable, "ai"),
                new Token(TokenType.Syntax, "->"), new Token(TokenType.Variable, "ai"),
                new Token(TokenType.Syntax, ")") };
        assertArrayEquals(output, LexUtils.lex(input));
    }

    /**
     * Test wheter the unique tokens are extracted correctly.
     */
    @Test
    public void uniqueTokens() {
        Token[] input = new Token[] { new Token(TokenType.Quantifier, "exists"),
                new Token(TokenType.Quantifier, "exists"), new Token(TokenType.Syntax, "("),
                new Token(TokenType.RelationalComparer, "<"), new Token(TokenType.RelationalComparer, "<"),
                new Token(TokenType.RelationalComparer, ">="), new Token(TokenType.EqualityComparer, "=="),
                new Token(TokenType.Number, "12"), new Token(TokenType.Syntax, "("), new Token(TokenType.Syntax, ")"),
                new Token(TokenType.EqualityComparer, "!=") };
        String[] output = new String[] { new Token(TokenType.Quantifier, "exists").toString(),
                new Token(TokenType.RelationalComparer, "<").toString(),
                new Token(TokenType.RelationalComparer, ">=").toString(),
                new Token(TokenType.EqualityComparer, "==").toString(), new Token(TokenType.Number, "12").toString(),
                new Token(TokenType.Syntax, "(").toString(), new Token(TokenType.Syntax, ")").toString(),
                new Token(TokenType.EqualityComparer, "!=").toString() };
        String[] ar = LexUtils.uniqueTokens(input, true);
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(ar));
        // Check uniqueness
        for (String u : output) {
            assertTrue(list.contains(u));
        }
        Set<String> set = new HashSet<String>(list);
        assertTrue(set.size() == list.size());
    }

    /**
     * Test if extending an array of tokens is extended correctly.
     */
    @Test
    public void textExtendTokens() {
        Token[] input = new Token[] { new Token(TokenType.Quantifier, "exists"), new Token(TokenType.Syntax, "("),
                new Token(TokenType.RelationalComparer, "<"), new Token(TokenType.RelationalComparer, ">="),
                new Token(TokenType.EqualityComparer, "=="), new Token(TokenType.Number, "12"),
                new Token(TokenType.Syntax, "("), new Token(TokenType.Syntax, ")"),
                new Token(TokenType.EqualityComparer, "!="), new Token(TokenType.Boolean, "true"),
                new Token(TokenType.Arithmetic, "+") };
        Token[] output = new Token[] { new Token(TokenType.Quantifier, "exists"),
                new Token(TokenType.RelationalComparer, "<"), new Token(TokenType.RelationalComparer, "<="),
                new Token(TokenType.RelationalComparer, ">"), new Token(TokenType.RelationalComparer, ">="),
                new Token(TokenType.EqualityComparer, "!="), new Token(TokenType.EqualityComparer, "=="),
                new Token(TokenType.Number, "12"), new Token(TokenType.Boolean, "true"),
                new Token(TokenType.Boolean, "false"), new Token(TokenType.Arithmetic, "+"),
                new Token(TokenType.Arithmetic, "-") };
        Token[] ar = LexUtils.extendTokens(input);
        ArrayList<Token> list = new ArrayList<Token>(Arrays.asList(ar));
        // Check containment
        for (Token u : output) {
            assertTrue(list.contains(u));
        }
    }
}
