/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package utils;

/**
 * Class that contains methods for parsing without exceptions.
 * @author Ludiscite
 * @version 1.0
 */
public class TryParser
{
    /**
     * Try to parse a string to an int, if it fails it returns 0.
     * @param str String to parse to an int.
     * @return String parsed to an int.
     */
    public static int parseInt(String str)
    {
        try
        {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Parse a pretty string of an int array of the form [1, 2, 4, 6].
     * @param str String to parse to an int array.
     * @return Array of ints.
     */
    public static int[] parseStringToIntArray(String str)
    {
        String substring = str.substring(1, str.length() - 1);
        String[] split = substring.split(", ");
        int[] array = new int[split.length];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = parseInt(split[i]);
        }
        return array;
    }
}
