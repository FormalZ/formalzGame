/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Path from '../../gameObjects/path/path';
import PathData from '../../gameObjects/path/pathData';
import Spark from '../../gameObjects/sparks/spark';
import Level from '../../states/level';
import GameTask from '../gameTask';

/**
 * PathGameTask is a GameTask that manages the pre and post Paths.
 * It initializes both Paths based on the PathData that was received from the back end.
 */
export default class PathGameTask extends GameTask {
    private pathData: PathData;

    /**
     * Handle a command from the server.
     * In the case of a 'path' command, make a new path from the path data passed in the arguments
     * @param keyWord the key word describing what command is triggered
     * @param args the possible arguments for a command
     */
    public tryCommand(command: string, args: string): boolean {
        switch (command) {
            case 'path':
                this.pathData = PathData.makePath(args);
                return true;
            default:
                return false;
        }
    }

    /**
     * Initialization of pre and post paths.
     */
    public initialize(level: Level): void {
        super.initialize(level);

        this.level.setPathData(this.pathData);

        const prePath: Path = new Path(this.level, this.pathData.getPreStartPoint());
        const postPath: Path = new Path(this.level, this.pathData.getPostStartPoint());

        this.level.setPrePath(prePath);
        this.level.setPostPath(postPath);

        prePath.draw(this.pathData.getPrePathTurns(), this.pathData.getStartAngle());
        prePath.setOnReachedEnd((spark: Spark) => {
            // When a Spark reaches the end of the pre condition Path, it should hop over to the post condition Path.

            spark.endOfPath(false);

            this.level.getPreScanner().updateSparkPassed(spark, spark.getSparkHealthLoss());

            // Place the spark on the post condition path
            const [correct, satisfiesCondition] = postPath.getConditionScanner().getRandomSparkType();
            spark.placeOnPath(postPath, correct, satisfiesCondition);
        });

        postPath.draw(this.pathData.getPostPathTurns());
        postPath.setOnReachedEnd((spark: Spark) => {
            // When a Spark reaches the end of the pre condition Path, it should disappear.
            spark.endOfPath(true);

            this.level.getPostScanner().updateSparkPassed(spark, spark.getSparkHealthLoss());
        });
    }
}