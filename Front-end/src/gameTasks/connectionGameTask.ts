/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Connection from '../connection';
import GameTask from './gameTask';

/**
 * ConnectionGameTask is a GameTask that takes care of the stop command from the server.
 */
export default class ConnectionGameTask extends GameTask {
    public tryCommand(command: string, args: string): boolean {
        switch (command) {
            case 'stop':
                Connection.connection.closeConnection();
                return true;
            // if the game server has checked the local storage has, it will send
            // true or false for if the hash matched the hash in the database
            // set the hash check value in connection to this boolean value
            case 'hashCheck':
                if (args === 'true')
                  Connection.connection.setHashCheck(true);
                else
                  Connection.connection.setHashCheck(false);
                return true;
            // if the game server has send the sessionId (userid and problemid)
            // set the session id variable in connection to the combined string
            // of this userid and problemid
            case 'sessionId':
                let argumentsplit: string[] = args.split(' ');
                Connection.connection.setSessionId(argumentsplit[0] + '-' + argumentsplit[1]);
                return true;
            default:
                return false;
        }
     }
}
