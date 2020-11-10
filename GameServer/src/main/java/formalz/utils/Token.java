/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.utils;

/**
 * Class that contains methods for parsing without exceptions. Source for basic layout lexer:
 * http://giocc.com/writing-a-lexer-in-java-1-7-using-regex-named-capturing-groups.html
 * @author Ludiscite
 * @version 1.0
 */
public class Token
{
    private TokenType ttype = null;
    private String tdata = null;

    /**
     * Constructor for a Token object.
     * @param t Type of the token.
     * @param d Data of the token.
     */
    public Token(TokenType t, String d)
    {
        ttype = t;
        tdata = d;
    }

    /**
     * Types of Tokens.
     */
    public enum TokenType
    {
        WhiteSpace("([ \t\f\r\n]+)", 999),
        Syntax("(->)|[\\(\\),]", 999),
        ArrayIndex("[\\[\\]]", 10),
        Implication("(imp)", 15),
        With("(with)", 16),
        QuantifierR("(forallr|existsr)", 14),
        Quantifier("(forall|exists)", 13),
        Null("(null)", 11),
        Length(".(length)", 12),
        Boolean("(true|false)", 4),
        Variable("[a-zA-Z_][a-zA-Z0-9_]*", 1),
        Arithmetic("[*\\/+\\-%]", 5),
        Real("-?[0-9]*.[0-9]+f?", 2),
        Number("-?[0-9]+", 3),
        RelationalComparer("(<=|>=|<|>)", 9),
        EqualityComparer("(==|!=)", 8),
        Not("(!)", 7),
        LogicOperator("(&&|\\|\\|)", 6);

        public final String pattern;
        public final Integer order;

        private TokenType(String pattern, Integer order)
        {
            this.pattern = pattern;
            this.order = order;
        }
    }

    /**
     * Return the type of the token.
     * @return Token type.
     */
    public TokenType getType()
    {
        return ttype;
    }

    /**
     * Return the data of the token.
     * @return Token data.
     */
    public String getData()
    {
        return tdata;
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public boolean equals(Object other)
    {
        if (other == null)
            return false;
        if (other == this)
            return true;
        if (!(other instanceof Token))
            return false;
        Token otherToken = (Token) other;
        return this.ttype == otherToken.getType() && this.tdata.equals(otherToken.getData());
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public int hashCode()
    {
        assert false : "hashCode not designed";
        return 42; // any arbitrary constant will do
    }

    @Override
    public String toString()
    {
        return String.format("(%s %s)", ttype.name(), tdata);
    }
}
