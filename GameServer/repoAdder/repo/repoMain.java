/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package repo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.io.IOException;

import data.Database;
import haskellapi.Prover;
import haskellapi.Response;

public class repoMain
{
    private static final String repoProblemsFilePath = "repoProblems.tsv";

    private static int counter = 0;

    public static void main(String[] args)
    {
        String line = null;

        try
        {
            FileReader fileReader = new FileReader(repoProblemsFilePath);
            BufferedReader reader = new BufferedReader(fileReader);

            // Throw away first line, which only contains the column headers
            line = reader.readLine();

            int num = 1;
            while ((line = reader.readLine()) != null)
            {
                processProblem(line.split("\t"), num);
                num++;
            }

            System.out.println("Succesfully updated the repo");
            System.out.println("Added " + counter + " problems");
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a problem is correct and not already in the database and if so add it to the problem repo.
     * @param data Problem to try to add to the repo.
     */
    private static void processProblem(String[] data, int num)
    {
        if (!isCorrect(data))
        {
            System.out.println("Problem " + num + " incorrect with description: " + data[1]);
            return;
        }

        if (!alreadyAdded(data))
        {
            addProblem(data);
            counter++;
        }
    }

    /**
     * Check whether a problem is correct by sending it to the equivalence checker.
     * @param data Problem to check.
     * @return Whether the problem is correct.
     */
    private static boolean isCorrect(String[] data)
    {
        String method = data[0] + "{pre(" + data[2] + ");post(" + data[3] + ");}";
        Response resp = Prover.getInstance().compare(method, method);
        if (resp.getErr() != null && !resp.getErr().equals(""))
        {
            System.out.println("Error: " + resp.getErr());
        }
        return resp.isEquivalent();
    }

    private static String queryCheck = "SELECT Count(*) as \"c\" FROM problemrepo WHERE header = ? AND description = ?";

    /**
     * Check whether a problem is correct by sending it to the equivalence checker.
     * @param data Problem to check.
     * @return Whether the problem is correct.
     */
    private static boolean alreadyAdded(String[] data)
    {
        return Database.getInstance().queryInt((con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement(queryCheck);
                statement.setString(1, data[0]);
                statement.setString(2, data[1]);
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        } , "c") >= 1;
    }

    private static String queryAdd = "INSERT INTO problemrepo (header, description, pre_conditions, post_conditions, difficulty,"
            + " hasForAll, hasExists, hasArrays, hasEquality, hasLogicOperator, hasRelationalComparer, hasArithmetic, hasImplication)"
            + " VALUES ( ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? )";

    private static void addProblem(String[] data)
    {
        Database.getInstance().queryVoid((con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement(queryAdd);
                statement.setString(1, data[0]);
                statement.setString(2, data[1]);
                statement.setString(3, data[2]);
                statement.setString(4, data[3]);
                statement.setInt(5, Integer.parseInt(data[4]));
                statement.setBoolean(6, data[5].equals("1"));
                statement.setBoolean(7, data[6].equals("1"));
                statement.setBoolean(8, data[7].equals("1"));
                statement.setBoolean(9, data[8].equals("1"));
                statement.setBoolean(10, data[9].equals("1"));
                statement.setBoolean(11, data[10].equals("1"));
                statement.setBoolean(12, data[11].equals("1"));
                statement.setBoolean(13, data[12].equals("1"));
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        });
    }
}
