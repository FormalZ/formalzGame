package main;

import java.util.*;

/**
 * This is a script to generate random paths for the game.
 * 
 * @author Ludiscite
 * 
 * @version 1.0
 */
public class Main
{
    public static int width = 22, height = 11, xOffset = 4, yOffset = 4, distance = 3;
    public static Random random = new Random();
    private static Point[][] graph;
    
    /**
     * The main method. When called, a random path will be generated and it will be printed on the console.
     * @param args This needs to be of the form {width, height, xOffset, yOffset, distance}.
     */
    public static void main(String[] args)
    {
        width = Integer.parseInt(args[0]);
        height = Integer.parseInt(args[1]);
        xOffset = Integer.parseInt(args[2]);
        yOffset = Integer.parseInt(args[3]);
        distance = Integer.parseInt(args[4]);
        System.out.println(generateRandomPath());
    }
    
    /**
     * Generate a random path and return it as a string.
     * @return The generated path as a string.
     */
    public static String generateRandomPath()
    {
        graph = generateCompleteGraph();
        Edge function = generateFunction();
        Edge endPoints = generateTree(function);
        return printPath(endPoints);
    }
    
    /**
     * Initialize the graph over which to generate a random path.
     * @return The points in the graph as a 2D-array.
     */
    private static Point[][] generateCompleteGraph()
    {
        // Generate the points in the graph.
        Point[][] graph = new Point[width][height];
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                graph[x][y] = new Point(x, y);
            }
        }
        // Generate the edges in the graph.
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                for (int dx = -1; dx <= 1; dx++)
                {
                    for (int dy = -1; dy <= 1; dy++)
                    {
                        if ((dx != 0 || dy != 0) && x + dx >= 0 && x + dx < width && y + dy >= 0 && y + dy < height)
                        {
                            graph[x][y].connections.add(graph[x + dx][y + dy]);
                        }
                    }
                }
            }
        }
        return graph;
    }
    
    /**
     * Generate the function block on the path and change the graph accordingly.
     * @return The edge of the function block.
     */
    private static Edge generateFunction()
    {
        // Generate the function block.
        Point functionLeft = graph[width / 2][random.nextInt(height / 2) + height / 4];
        Point functionRight = graph[functionLeft.x + 1][functionLeft.y];
        
        //Remove edges in the graph that intersect with the function block.
        for (int x = functionLeft.x; x <= functionRight.x; x++)
        {
            for (int y = functionLeft.y - 1; y <= functionLeft.y + 1; y++)
            {
                Point point = graph[x][y];
                functionLeft.connections.remove(point);
                functionRight.connections.remove(point);
                point.connections.remove(functionLeft);
                point.connections.remove(functionRight);
            }
        }
        
        return new Edge(functionLeft, functionRight);
    }
    
    /**
     * Generate a random spanning tree containing the function as an edge.
     * The intended start and end of the eventual path will be the
     * from and to properties of the returned edge.
     * @param function The location of the function block.
     * @return An edge with the intended start and end of the eventual path.
     */
    private static Edge generateTree(Edge function)
    {
        // Initializing datastructures.
        Set<Point> reached = new HashSet<Point>();
        reached.add(function.from);
        reached.add(function.to);

        List<Edge> queueLeft = new ArrayList<Edge>();
        List<Edge> queueRight = new ArrayList<Edge>();

        for (Point connection : function.from.connections)
        {
            queueLeft.add(new Edge(function.from, connection));
        }
        for (Point connection : function.to.connections)
        {
            queueRight.add(new Edge(function.to, connection));
        }
        
        Point[] treeEnds = new Point[]{function.from, function.to};
        
        // The main loop which adds edges to the tree.
        outerloop:
        while (!(queueLeft.isEmpty() && queueRight.isEmpty()))
        {
            // Choosing the side to which to add edges.
            int treeIndex = 0;
            List<Edge> queue = queueLeft;
            if (queueLeft.isEmpty() || ((treeEnds[0].depth > treeEnds[1].depth || (treeEnds[0].depth == treeEnds[1].depth && random.nextBoolean())) && !queueRight.isEmpty()))
            {
                treeIndex = 1;
                queue = queueRight;
            }
            
            // Choosing the edge to add.
            int edgeIndex = random.nextInt(queue.size());
            Edge edge = queue.get(edgeIndex);
            queue.set(edgeIndex, queue.get(queue.size() - 1));
            queue.remove(queue.size() - 1);
            
            // Determining to forget the edge if necessary.
            if (reached.contains(edge.to))
                continue;
            
            for (Edge crossingEdge : edge.getCrossingEdges())
                if (crossingEdge.to.previous == crossingEdge.from)
                    continue outerloop;
            
            // Actually adding the edge.
            edge.to.previous = edge.from;
            edge.to.depth = edge.from.depth + 1;
            if (edge.to.depth > treeEnds[treeIndex].depth)
                treeEnds[treeIndex] = edge.to;
            
            reached.add(edge.to);
            
            for (Point connection : edge.to.connections)
            {
                int oldAngle = calcAngle(edge);
                int newAngle = calcAngle(new Edge(edge.to, connection));
                if (Math.abs(relativeAngle(oldAngle, newAngle)) <= 45)
                    queue.add(new Edge(edge.to, connection));
            }
        }
        
        return new Edge(treeEnds[0], treeEnds[1]);
    }
    
    /**
     * Find the path and format it as a string.
     * @param endPoints The start and end of the path.
     * @return The path formatted as a string.
     */
    private static String printPath(Edge endPoints)
    {
        List<Point> prePath = getPath(endPoints.from);
        List<Point> postPath = getPath(endPoints.to);
        Collections.reverse(postPath);
        centralize(prePath, postPath);
        StringBuilder sb = new StringBuilder();
        formatPath(prePath, sb);
        sb.append(';');
        formatPath(postPath, sb);
        return sb.toString();
    }
    
    /**
     * Find the path from an end point of the generated tree to the function block.
     * @param point The end point of the path.
     * @return The list of points on the path.
     */
    private static List<Point> getPath(Point point)
    {
        List<Point> path = new ArrayList<Point>();
        path.add(point);
        while (point.previous != null)
        {
            point = point.previous;
            path.add(point);
        }
        return path;
    }
    
    /**
     * Centralize the pre and post path.
     * @param prePath The pre path.
     * @param postPath The post path.
     */
    private static void centralize(List<Point> prePath, List<Point> postPath)
    {
        int leftMost = width;
        int rightMost = 0;
        int topMost = height;
        int bottomMost = 0;
        for (Point p : prePath)
        {
            leftMost = Math.min(leftMost, p.x);
            rightMost = Math.max(rightMost, p.x);
            topMost = Math.min(topMost, p.y);
            bottomMost = Math.max(bottomMost, p.y);
        }
        for (Point p : postPath)
        {
            leftMost = Math.min(leftMost, p.x);
            rightMost = Math.max(rightMost, p.x);
            topMost = Math.min(topMost, p.y);
            bottomMost = Math.max(bottomMost, p.y);
        }
        int translateX = (width - leftMost - rightMost) / 2;
        int translateY = (height - topMost - bottomMost) / 2;
        for (Point p : prePath)
        {
            p.x += translateX;
            p.y += translateY;
        }
        for (Point p : postPath)
        {
            p.x += translateX;
            p.y += translateY;
        }
    }
    
    /**
     * Format a path and append it to a stringbuilder.
     * @param path The path to be formatted.
     * @param sb The stringbuilder to which to append the path.
     */
    private static void formatPath(List<Point> path, StringBuilder sb)
    {
        sb.append('(');
        sb.append(path.get(0).x * distance + xOffset);
        sb.append('.');
        sb.append(path.get(0).y * distance + yOffset);
        sb.append(')');
        sb.append(';');
        sb.append('[');
        int lastAngle = 0;
        for (int i = 1; i < path.size(); i++)
        {
            if (i > 1)
                sb.append('.');
            sb.append('[');
            sb.append(distance);
            sb.append(',');
            int angle = calcAngle(new Edge(path.get(i - 1), path.get(i)));
            sb.append(relativeAngle(lastAngle, angle));
            lastAngle = angle;
            sb.append(']');
        }
        sb.append(']');
    }
    
    /**
     * Calculate the absolute angle of an edge.
     * @param edge The edge of which to calculate the angle.
     * @return The angle of the edge.
     */
    private static int calcAngle(Edge edge)
    {
        switch ((edge.to.x - edge.from.x) + 3 * (edge.to.y - edge.from.y))
        {
        case -4:
            return 135;
        case -3:
            return 90;
        case -2:
            return 45;
        case -1:
            return 180;
        case 1:
            return 0;
        case 2:
            return -135;
        case 3:
            return -90;
        default:
            return -45;
        }
    }
    
    /**
     * Calculate the difference of two angles.
     * @param oldAngle The old angle
     * @param newAngle The new angle.
     * @return The angle to add to the old angle to get the new angle.
     */
    private static int relativeAngle(int oldAngle, int newAngle)
    {
        return (newAngle - oldAngle + 495) % 360 - 135;
    }
    
    /**
     * A Point in the graph.
     * @author Ludiscite
     */
    private static class Point
    {
        public int x;
        public int y;
        public Set<Point> connections = new HashSet<Point>();
        
        public Point previous = null;
        public int depth = 0;
        
        public Point(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }
    
    /**
     * An tuple of points in the graph.
     * @author M.P
     */
    private static class Edge
    {
        public Point from;
        public Point to;
        
        public Edge(Point from, Point to)
        {
            this.from = from;
            this.to = to;
        }
        
        public Set<Edge> getCrossingEdges()
        {
            Set<Edge> crossingEdges = new HashSet<Edge>();
            if (from.x != to.x && from.y != to.y){
                crossingEdges.add(new Edge(graph[from.x][to.y], graph[to.x][from.y]));
                crossingEdges.add(new Edge(graph[to.x][from.y], graph[from.x][to.y]));
            }
            return crossingEdges;
        }
    }
}
