package main;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import main.Main;

public class MainTest
{
    private Random random = new Random(873823);

    @Test
    public void testOutputFormat()
    {
        Main.random = random;
        String path = Main.generateRandomPath();
        String[] args = path.split(";");
        assertEquals(4, args.length);
        assertTrue(checkFirstAndLastCharacter(args[0], '(', ')'));
        assertTrue(checkFirstAndLastCharacter(args[1], '[', ']'));
        assertTrue(checkFirstAndLastCharacter(args[2], '(', ')'));
        assertTrue(checkFirstAndLastCharacter(args[3], '[', ']'));
        String[][] subArgs = new String[4][];
        for (int i = 0; i < 4; i++)
        {
            subArgs[i] = args[i].substring(1, args[i].length() - 1).split("\\.");
        }
        assertEquals(2, subArgs[0].length);
        assertTrue(isInteger(subArgs[0][0]));
        assertTrue(isInteger(subArgs[0][1]));
        assertEquals(2, subArgs[2].length);
        assertTrue(isInteger(subArgs[2][0]));
        assertTrue(isInteger(subArgs[2][1]));
        for (String subArg : subArgs[1])
        {
            assertTrue(checkFirstAndLastCharacter(subArg, '[', ']'));
            String[] subSubArgs = subArg.substring(1, subArg.length() - 1).split(",");
            assertEquals(2, subSubArgs.length);
            assertTrue(isInteger(subSubArgs[0]));
            assertTrue(isInteger(subSubArgs[1]));
        }
        for (String subArg : subArgs[3])
        {
            assertTrue(checkFirstAndLastCharacter(subArg, '[', ']'));
            String[] subSubArgs = subArg.substring(1, subArg.length() - 1).split(",");
            assertEquals(2, subSubArgs.length);
            assertTrue(isInteger(subSubArgs[0]));
            assertTrue(isInteger(subSubArgs[1]));
        }
    }
    
    private boolean checkFirstAndLastCharacter(String s, char expectedFirst, char expectedLast){
        return s.charAt(0) == expectedFirst && s.charAt(s.length() - 1) == expectedLast;
    }
    
    private boolean isInteger(String s){
        char firstChar = s.charAt(0);
        if (firstChar != '-' && (firstChar < '0' || firstChar > '9'))
        {
            return false;
        }
        for (int i = 1; i < s.length(); i++)
        {
            char thisChar = s.charAt(i);
            if (thisChar < '0' || thisChar > '9')
            {
                return false;
            }
        }
        return true;
    }

    @Test
    public void testInBounds()
    {
        Main.random = random;
        String path = Main.generateRandomPath();
        System.out.println(path);
        System.out.println(Main.width + " " + Main.height);
        String[] args = path.split(";");
        String[][] subArgs = new String[4][];
        for (int i = 0; i < 4; i++)
        {
            subArgs[i] = args[i].substring(1, args[i].length() - 1).split("\\.");
        }
        Point point = new Point((Integer.parseInt(subArgs[0][0]) - Main.xOffset) / Main.distance,
                (Integer.parseInt(subArgs[0][1]) - Main.yOffset) / Main.distance);
        assertTrue(inBounds(point));
        int angle = 0;
        for (String subArg : subArgs[1])
        {
            String[] subSubArgs = subArg.substring(1, subArg.length() - 1).split(",");
            assertEquals(Main.distance, Integer.parseInt(subSubArgs[0]));
            int relativeAngle = Integer.parseInt(subSubArgs[1]);
            angle = angleSum(angle, relativeAngle);
            movePoint(point, angle);
            assertTrue(inBounds(point));
        }
        point.x++;
        assertTrue(inBounds(point));
        assertEquals(point.x, (Integer.parseInt(subArgs[2][0]) - Main.xOffset) / Main.distance);
        assertEquals(point.y, (Integer.parseInt(subArgs[2][1]) - Main.yOffset) / Main.distance);
        angle = 0;
        for (String subArg : subArgs[3])
        {
            String[] subSubArgs = subArg.substring(1, subArg.length() - 1).split(",");
            assertEquals(Main.distance, Integer.parseInt(subSubArgs[0]));
            int relativeAngle = Integer.parseInt(subSubArgs[1]);
            angle = angleSum(angle, relativeAngle);
            movePoint(point, angle);
            assertTrue(inBounds(point));
        }
    }
    
    private boolean inBounds(Point point)
    {
        return 0 <= point.x && point.x <= Main.width && 0 <= point.y && point.y <= Main.height;
    }

    private static int angleSum(int angle1, int angle2)
    {
        return (angle1 + angle2 + 495) % 360 - 135;
    }
    
    private static void movePoint(Point point, int angle)
    {
        switch (angle)
        {
        case -180:
            point.x--;
            break;
        case -135:
            point.x--;
            point.y++;
            break;
        case -90:
            point.y++;
            break;
        case -45:
            point.x++;
            point.y++;
            break;
        case 0:
            point.x++;
            break;
        case 45:
            point.x++;
            point.y--;
            break;
        case 90:
            point.y--;
            break;
        default:
            point.x--;
            point.y--;
            break;
        }
    }
    
    private static class Point
    {
        public int x;
        public int y;
        
        public Point(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }
}
