/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.data;

import java.util.Arrays;
import java.util.List;

import formalz.haskellapi.Prover;
import formalz.haskellapi.Response;
import formalz.utils.LexUtils;
import formalz.utils.StringUtils;

/**
 * The class containing information about the problem.
 * @author Ludiscite
 * @version 1.0
 */
public class Problem
{
    /**
     * Different types of features a problem can have.
     */
    public enum Feature
    {
        forAll,
        exists,
        arrays,
        equality,
        logicOperator,
        relationalComparer,
        arithmetic,
        implication
    }

    private int id;
    private String header;

    /**
     * The same header as above, but extended with "retval" added as an explicit parameter.
     * This is pure to make the Haskell backend understand what the intended type of retval
     * (e.g. int or bool). If this field is null, it means that we cannot construct the
     * extended header.
     */
    private String header_extended_with_retval = null ;

    private String description;
    private String pre_conditions;
    private String post_conditions;
    private int difficulty;
    private Integer lives;
    private Integer money;
    private Integer deadline;

    private boolean isTeacherProblem;

    private List<Feature> features;

    private String[] tokensPre;
    private String[] tokensPost;

    /**
     * Constructor for a problem object.
     * @param id Id of the problem.
     * @param header Function header of the problem.
     * @param description Description of the problem.
     * @param pre_conditions PreCondition solution of the problem.
     * @param post_conditions PostCondition solution of the problem.
     * @param difficulty Difficulty of the problem.
     * @param lives The amount of lives for this problem
     * @param money The amount of money for this problem.
     * @param deadline The deadline for this problem.
     * @param isTeacherProblem Whether the problem is a teacher problem.
     * @param features List of features the problem has.
     */
    public Problem(int id, String header, String description, String pre_conditions, String post_conditions, int difficulty,
            Integer lives, Integer money, Integer deadline, boolean isTeacherProblem, List<Feature> features)
    {
        this.id = id;
        this.header = header;
        // WP: patch to explicitly declare "retval":
        try {
        	this.header_extended_with_retval = SignatureTransformer.insertRetval_in_methodSignature(header) ;
        }
        catch(Exception e) {
        	// well if the transformation fail, then we leave header_extended_with_retval null.
        }
        this.description = description;
        this.pre_conditions = pre_conditions;
        this.post_conditions = post_conditions;
        this.difficulty = difficulty;
        this.lives = lives;
        this.money = money;
        this.deadline = deadline;

        this.isTeacherProblem = isTeacherProblem;
        this.features = features;

        if (this.isTeacherProblem || this.difficulty == 5)
        {
            tokensPre = LexUtils.getContentAndAllTokens(this.pre_conditions);
            tokensPost = LexUtils.getContentAndAllTokens(this.post_conditions);
        }
        else
        {
            tokensPre = LexUtils.getContent(this.pre_conditions, false, true);
            tokensPost = LexUtils.getContent(this.post_conditions, false, true);
        }
    }

    /**
     * Creates the teacher solution of the problem.
     * @return Teacher solution of the problem.
     */
    private String getSolution()
    {
        return generateSolution(pre_conditions, post_conditions);
    }

    /**
     * Creates a solution based on the given pre- and postcondition.
     * @param pre precondition Precondition to add in the function.
     * @param post postcondition Postcondition to add in the function.
     * @return Method of the problem with the pre and post conditions filled in.
     */
    private String generateSolution(String pre, String post)
    {
        //return header + "{pre(" + pre + ");post(" + post + ");}";
    	// WP: fix to explicitly insert retval as an extra parameter in the method header.
    	// Haskell backend needs this to be aware of the intended type of retval.
        String header_ = this.header_extended_with_retval ;
        if (header_ == null) header_ = this.header ;
        return header_ + "{pre(" + pre + ");post(" + post + ");}";

    }

    /**
     * Get the id of the problem.
     * @return Id of the problem.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Get the method header of the problem.
     * @return Header of the method of the problem.
     */
    public String getHeader()
    {
        return header;
    }

    /**
     * Gets the description of the problem.
     * @return Problem description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Gets the preconditions of the teacher solutions.
     * @return Correct preconditions.
     */
    public String getPreConditions()
    {
        return pre_conditions;
    }

    /**
     * Gets the postconditions of the teacher solutions.
     * @return Correct postconditions.
     */
    public String getPostConditions()
    {
        return post_conditions;
    }

    /**
     * Gets the difficulty of the problem.
     * @return Difficulty of the problem.
     */
    public int getDifficulty()
    {
        return difficulty;
    }

    public int getLives() {
        if(lives != null)
            return lives;
        return 0;
    }

    public int getMoney() {
        if(money != null)
            return money;
        return 0; }

    public int getDeadline() {
        if(deadline != null)
            return deadline;
        return 0; }

    /**
     * Returns whether the problem contains a certain feature.
     * @param feature Feature to search for.
     * @return Whether the problem contains a certain feature.
     */
    public boolean hasFeature(Feature feature)
    {
        return features.contains(feature);
    }

    /**
     * Returns a list of the features of the problem.
     * @return A list of features
     */
    public List<Feature> getFeatures()
    {
        return features;
    }

    /**
     * Returns a string containing the variables and types.
     * @return String with variables and types
     */
    public String getVariableTypes()
    {
        int firstBracIndex = header.indexOf('(');
        String headerPars = header.substring(firstBracIndex);
        String types = "[" + (headerPars.substring(1, headerPars.length() - 1)) + "]";
        return types;
    }

    /**
     * Compare given pre- and postconditions to the teacher solution.
     * @param pre Student precondition.
     * @param post Student postcondition.
     * @return Response about the comparison.
     */
    public Response compare(String pre, String post)
    {
        return Prover.getInstance().compare(StringUtils.escapeJSON(generateSolution(pre, post)), StringUtils.escapeJSON(getSolution()));
    }

    /**
     * Compare given preconditions to the teacher solution.
     * @param pre Student precondition.
     * @return Response about the comparison.
     */
    public Response comparePre(String pre)
    {
        return Prover.getInstance().compare(StringUtils.escapeJSON(generateSolution(pre, "true")),
                StringUtils.escapeJSON(generateSolution(getPreConditions(), "true")));
    }

    /**
     * Compare given postconditions to the teacher solution.
     * @param post Student postcondition.
     * @return Response about the comparison.
     */
    public Response comparePost(String post)
    {
        return Prover.getInstance().compare(StringUtils.escapeJSON(generateSolution("true", post)),
                StringUtils.escapeJSON(generateSolution("true", getPostConditions())));
    }

    /**
     * Returns the tokens used in the preconditition.
     * @return Array of tokens.
     */
    public String[] getPreTokens()
    {
        return Arrays.copyOf(tokensPre, tokensPre.length);
    }

    /**
     * Returns the tokens used in the postcondition.
     * @return Array of tokens.
     */
    public String[] getPostTokens()
    {
        return Arrays.copyOf(tokensPost, tokensPost.length);
    }
}
