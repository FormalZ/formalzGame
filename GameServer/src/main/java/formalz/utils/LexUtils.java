/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import formalz.utils.Token.TokenType;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * The class with utility methods related to lexing.
 * @author Ludiscite
 * @version 1.0
 */
public class LexUtils
{
    /**
     * Do all the lexing stuff given pre and/or post conditions.
     * @param input pre and/or post condition.
     * @param withSyntax output with or without Syntax Tokens.
     * @param extended whether to extend the content with similar tokens.
     * @return string array containing all used tokens.
     */
    public static String[] getContent(String input, boolean withSyntax, boolean extended)
    {
        Token[] lexed = lex(input);
        if (extended)
        {
            lexed = extendTokens(lexed);
        }
        ArrayList<Token> list = new ArrayList<Token>(Arrays.asList(lexed));
        Collections.sort(list, (t1, t2) -> t1.getType().order.compareTo(t2.getType().order));
        return uniqueTokens(list.toArray(new Token[0]), withSyntax);
    }

    private static Token[] allTokens = new Token[] { new Token(TokenType.Implication, "imp"), new Token(TokenType.With, "with"),
            new Token(TokenType.QuantifierR, "forallr"), new Token(TokenType.QuantifierR, "existsr"),
            new Token(TokenType.Quantifier, "forall"), new Token(TokenType.Quantifier, "exists"), new Token(TokenType.Null, "null"),
            new Token(TokenType.Length, ".length"), new Token(TokenType.Boolean, "true"), new Token(TokenType.Boolean, "false"),
            new Token(TokenType.Arithmetic, "*"), new Token(TokenType.Arithmetic, "/"), new Token(TokenType.Arithmetic, "+"),
            new Token(TokenType.Arithmetic, "-"), new Token(TokenType.Arithmetic, "%"), new Token(TokenType.RelationalComparer, "<="),
            new Token(TokenType.RelationalComparer, "<"), new Token(TokenType.RelationalComparer, ">"),
            new Token(TokenType.RelationalComparer, ">="), new Token(TokenType.EqualityComparer, "=="),
            new Token(TokenType.EqualityComparer, "!="), new Token(TokenType.Not, "!"), new Token(TokenType.LogicOperator, "&&"),
            new Token(TokenType.LogicOperator, "||") };

    public static String[] getContentAndAllTokens(String input)
    {
        Token[] lexed = lex(input);
        ArrayList<Token> combined = new ArrayList<Token>(Arrays.asList(lexed));
        combined.addAll(Arrays.asList(allTokens));
        Collections.sort(combined, (t1, t2) -> t1.getType().order.compareTo(t2.getType().order));
        return uniqueTokens(combined.toArray(new Token[0]), false);
    }

    /**
     * Lex the given input.
     * @param input The String to lex, format should be in the JavaEDSL.
     * @return Array of tokens, each describing an item contained in the input.
     */
    public static Token[] lex(String input)
    {
        // Taken From http://www.giocc.com/writing-a-lexer-in-java-1-7-using-regex-named- capturing-groups.html

        // The tokens to return
        ArrayList<Token> tokens = new ArrayList<Token>();

        // Lexer logic begins here
        StringBuffer tokenPatternsBuffer = new StringBuffer();
        for (TokenType tokenType : TokenType.values())
        {
            tokenPatternsBuffer.append(String.format("|(?<%s>%s)", tokenType.name(), tokenType.pattern));
        }
        Pattern tokenPatterns = Pattern.compile(tokenPatternsBuffer.substring(1));

        // Begin matching tokens
        Matcher matcher = tokenPatterns.matcher(input);
        while (matcher.find())
        {
            for (TokenType tk : TokenType.values())
            {
                if (matcher.group(TokenType.WhiteSpace.name()) != null)
                {
                    continue;
                }
                else if (matcher.group(tk.name()) != null)
                {
                    tokens.add(new Token(tk, matcher.group(tk.name())));
                    break;
                }
            }
        }

        return tokens.toArray(new Token[tokens.size()]);
    }

    /**
     * Returns array with all unique items, with or without Syntax.
     * @param tokens Array of Tokens that may contain duplicates.
     * @param withSyntax Return array contains or not contains any Syntax.
     * @return Array of Tokens without duplicates.
     */
    public static String[] uniqueTokens(Token[] tokens, boolean withSyntax)
    {
        ArrayList<String> uniqueTokens = new ArrayList<String>();

        for (Token token : tokens)
        {
            if (!uniqueTokens.contains(token.toString()) && (withSyntax || (token.getType() != TokenType.Syntax)))
            {
                uniqueTokens.add(token.toString());
            }
        }

        return uniqueTokens.toArray(new String[uniqueTokens.size()]);
    }

    /**
     * Extend a token array to contain similar tokens.
     * @param tokens Array of Tokens to extend.
     * @return Extended array of tokens.
     */
    public static Token[] extendTokens(Token[] tokens)
    {

        List<Token> extendedTokens = new ArrayList<Token>();

        for (Token token : tokens)
        {
            extendedTokens.addAll(closure(token));
        }

        return extendedTokens.toArray(new Token[extendedTokens.size()]);
    }

    /**
     * Returns a list of similar tokens the one provided (ignores syntactic tokens).
     * @param token Token to get similar tokens from.
     * @return List of similar tokens.
     */
    private static List<Token> closure(Token token)
    {
        switch (token.getType())
        {
            case Arithmetic:
                switch (token.getData())
                {
                    case "*":
                    case "/":
                        return multiplicationList;
                    case "+":
                    case "-":
                        return additionList;
                    case "%":
                        return Arrays.asList(token);
                    default:
                        return new ArrayList<Token>();
                }
            case Quantifier:
            case QuantifierR:
                return quantifierList;
            case Boolean:
                return booleanList;
            case RelationalComparer:
            case EqualityComparer:
                return comparerList;
            case LogicOperator:
                return logicOperatorList;
            case Null:
            case Number:
            case Length:
            case Real:
            case Not:
            case Variable:
            case ArrayIndex:
            case Implication:
            case With:
                return Arrays.asList(token);
            default:
                return new ArrayList<Token>();
        }
    }

    private static List<Token> multiplicationList = Arrays.asList(new Token(TokenType.Arithmetic, "*"),
            new Token(TokenType.Arithmetic, "/"));
    private static List<Token> additionList = Arrays.asList(new Token(TokenType.Arithmetic, "+"), new Token(TokenType.Arithmetic, "-"));
    private static List<Token> booleanList = Arrays.asList(new Token(TokenType.Boolean, "true"), new Token(TokenType.Boolean, "false"));
    private static List<Token> comparerList = Arrays.asList(new Token(TokenType.RelationalComparer, "<="),
            new Token(TokenType.RelationalComparer, ">="), new Token(TokenType.EqualityComparer, "=="),
            new Token(TokenType.EqualityComparer, "!="), new Token(TokenType.RelationalComparer, "<"),
            new Token(TokenType.RelationalComparer, ">"));
    private static List<Token> logicOperatorList = Arrays.asList(new Token(TokenType.LogicOperator, "&&"),
            new Token(TokenType.LogicOperator, "||"));
    private static List<Token> quantifierList = Arrays.asList(new Token(TokenType.Quantifier, "forall"),
            new Token(TokenType.Quantifier, "exists"), new Token(TokenType.QuantifierR, "forallr"),
            new Token(TokenType.QuantifierR, "existsr"));

    /**
     * Extract the feature usage of the solution.
     * @param string Solution to extract usage of.
     * @return Feature usage of the solution.
     */
    public static int[] extractFeatureUsage(String string)
    {
        Token[] tokens = lex(string);
        int[] featureUsage = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        for (Token token : tokens)
        {
            int index = -1;
            switch (token.getType())
            {
                case Quantifier:
                    if (token.getData() == "forall")
                    {
                        index = 0;
                    }
                    else if (token.getData() == "exists")
                    {
                        index = 1;
                    }
                    break;
                case ArrayIndex:
                case Null:
                case Length:
                    index = 2;
                    break;
                case EqualityComparer:
                    index = 3;
                    break;
                case Not:
                case LogicOperator:
                    index = 4;
                    break;
                case RelationalComparer:
                    index = 5;
                    break;
                case Arithmetic:
                    index = 6;
                    break;
                default:
                    break;
            }
            if (index == -1)
            {
                continue;
            }
            if (featureUsage[index] == 0)
            {
                featureUsage[index] = 1;
            }
        }
        return featureUsage;
    }
}
