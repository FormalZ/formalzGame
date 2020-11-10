/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.utils;

/**
 * The class with utility methods related to string.
 * @author Ludiscite
 * @version 1.0
 */
public class StringUtils
{
    /**
     * Creates an equivalent string with unsafe characters in JSON escaped.
     * @param s The string to escape.
     * @return The escaped string.
     */
    public static String escapeJSON(String s)
    {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray())
        {
            sb.append(escapeJSONCharacter(c));
        }
        return sb.toString();
    }

    /**
     * Creates a string from a character where unsafe characters in JSON are escaped.
     * @param c The character to escape.
     * @return The escaped string.
     */
    private static String escapeJSONCharacter(char c)
    {
        switch (c)
        {
            case '\b':
                return "\\b";
            case '\f':
                return "\\f";
            case '\n':
                return "\\n";
            case '\r':
                return "\\r";
            case '\t':
                return "\\t";
            case '\"':
                return "\\\"";
            case '\\':
                return "\\\\";
            default:
                return Character.toString(c);
        }
    }
}
