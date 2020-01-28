/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from './states/level';
import WaveData from './utils/waveData';
import ErrorState from './states/errorState';
import MainGameTask from './gameTasks/mainGameTask';

export default class Connection {
    public static connection: Connection;

    private socket: WebSocket;
    private level: Level;
    private game: Phaser.Game;


    // Variable indicating whether the hash of the local storage was correctly checked with the back end
    private hashCheck: boolean = false;

    // variable that indicates the session id for local storage
    private sessionId: string = '';
    private canSendCheck: boolean = false;

    /**
     * Initialize a connection and a Connection object.
     */
    public static initializeConnection(game: Phaser.Game): void {
        const settings: JSON = game.cache.getJSON('settings');
        const ip: string = settings['ipAddress'];
        const ipLocal: string = settings['ipAddressLocal'];
        const server: number = settings['server'];

        const ipAddress: string = FORMALZ_GAMESERVER_HOST || [ip, ipLocal][server];
        const port: number = FORMALZ_GAMESERVER_PORT || settings['port'];
        const protocol: string = FORMALZ_GAMESERVER_PROTOCOL || settings['protocol'];
        const path: string = FORMALZ_GAMESERVER_PATH || settings['path'];
        const url: string = protocol + '://' + ipAddress + ':' + port + path;
        const socket: WebSocket = new WebSocket(url);

        this.connection = new Connection(socket, game);
        socket.onopen = () => {
            if (DEBUG) {
                console.log('Not via Laravel, so you cannot get the token from there. We will use the debug one.');
                const debugToken: string = settings['debugToken'];
                this.connection.sendStartup(debugToken);
            } else {
                const token: HTMLInputElement = <HTMLInputElement>document.getElementById('token');
                if (token) {
                    this.connection.sendStartup(token.value);
                } else {
                    ErrorState.throw(game, 'No token was found in the HTML', false);
                }
            }
        };

        socket.onerror = (error: Event) => {
            ErrorState.throw(game, 'Connection failed', false);
        };

        socket.onmessage = (event: MessageEvent) => {
            const message: string = event.data;
            if (DEBUG) {
                console.log('Message from the server: ' + message);
            }

            const argList: string[] = message.split(' ');
            if (argList.length < 1) {
                if (DEBUG) {
                    console.log('Empty message');
                }

                return;
            }

            const command: string = argList[0];

            let args: string = '';
            const index: number = message.indexOf(' ');
            if (index !== -1) {
                args = message.substring(index + 1);
            }

            if (command === 'startup') {
                switch (args) {
                    case 'done':
                        if (DEBUG) {
                            console.log('Connection authorized: Token has been verified');
                        }
                        break;
                    case 'wrong':
                        ErrorState.throw(game, 'Connection failed', false);
                        break;
                    case 'timeout':
                        ErrorState.throw(game, 'Connection timeout', false);
                        break;
                }
            } else if (!MainGameTask.mainTask.tryCommand(command, args)) {
                console.warn('Invalid message: ' + message);
            } else if (command === 'path') {
                game.state.start('level');
            }
        };

        // Error code information from this URL:
        // tslint:disable-next-line
        // https://stackoverflow.com/questions/18803971/websocket-onerror-how-to-read-error-description?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
        socket.onclose = (event: CloseEvent) => {
            switch (event.code) {
                case 1000:
                    if (DEBUG) {
                        console.log('Normal closure.');
                    }

                    break;
                case 1001:
                    ErrorState.throw(game, `Error code ${event.code}: Endpoint is gone (browser navigated away).`, false);
                    break;
                case 1002:
                    ErrorState.throw(game, `Error code ${event.code}: Terminating connection due to a protocol error.`, false);
                    break;
                case 1003:
                    ErrorState.throw(game, `Error code ' + event.code}: Type of data received cannot be accepted.`, false);
                    break;
                case 1004:
                    ErrorState.throw(game, `Error code ${event.code}: Code is reserved.`, false);
                    break;
                case 1005:
                    ErrorState.throw(game, `Error code ${event.code}: No status code was actually present.`, false);
                    break;
                case 1006:
                    ErrorState.throw(game, `Error code ${event.code}: The connection was closed abnormally.`, false);
                    break;
                case 1007:
                    ErrorState.throw(game, `Error code ${event.code}: Received a message with inconsistent data.`, false);
                    break;
                case 1008:
                    ErrorState.throw(game, `Error code ${event.code}: Message received by server violates policy.`, false);
                    break;
                case 1009:
                    ErrorState.throw(game, `Error code ${event.code}: Message received is too big`, false);
                    break;
                case 1010:
                    ErrorState.throw(game, `Error code ${event.code}: Client terminated the connection`, false);
                    break;
                case 1011:
                    ErrorState.throw(game, `Error code ${event.code
                        }: Server terminated the connection because of an unexpected condition`, false);
                    break;
                case 1015:
                    ErrorState.throw(game, `Error code ${event.code}: Failed to perform TLS handshake`, false);
                    break;
                default:
                    ErrorState.throw(game, `Error code unknown: Unknown reason`, false);
                    break;
            }
        };
    }

    /**
     * Create a Connection that handles all the sending of messages. Level should have this class.
     * @param connection the socket that sends the messages to the server
     */
    constructor(socket: WebSocket, game: Phaser.Game) {
        this.socket = socket;
        this.game = game;
    }

    /**
     * Determine whether the connection has already successfully been set up.
     */
    public hasSucceeded(): boolean {
        return this.socket.readyState === 1;
    }

    /**
     * Set the level in order to be able to call processMessage().
     * @param level The level of the game.
     */
    public setLevel(level: Level): void {
        this.level = level;
    }

    /**
     * Send a message to the server. The methods that call this already prepended the keyword for that functionality.
     * @param message the message that will be send
     */
    private sendMessage(message: string): void {
        if (this.socket.readyState !== 1) {
            if (!DEBUG && this.level && !this.level.getIsTutorial()) {
                ErrorState.throw(this.game, 'Connection is not open', false);
            }

            return;
        }

        this.socket.send(message);
        if (DEBUG) {
            console.log('Sent message: ' + message);
        }
    }

    /**
     * Send a message to the server to make a connection
     * @param token The token that gets send to the server for authentication.
    */
    public sendStartup(token: string): void {
        this.sendMessage('startup ' + token);
    }

    /**
     * Send the message startGame to the server to start the game.
     */
    public sendStartGame(hash: number): void {
        this.sendMessage('startGame ' + hash);
        this.canSendCheck = true;
    }

    /**
     * Send the message startProblem to the server to start the problem.
     */
    public sendStartProblem(challengeCounter: number): void {
        this.sendMessage('startProblem ' + challengeCounter);
    }

    /**
     * Send pre of post condition to the server
     * @param type Either Pre or Post
     * @param condition The written condition
     */
    public sendCondition(type: string, condition: string): void {
        if(this.canSendCheck){
            this.sendMessage('submit' + type + ' ' + condition);
        }
    }

    /**
     * Send the time spent that the pre or post scanner was open
     * @param type Either Pre or Post
     * @param time The time spent on writing the condition
     */
    public sendTimeSpent(type: string, time: number): void {
        this.sendMessage('timeSpent' + type + ' ' + time);
    }

    public sendError(error: string, stackTrace: string): void {
        this.sendMessage('error ' + error + ';' + stackTrace);
    }

    /**
     * Send 'waveDone' to the server signalling that you completed the wave.
     * @param data the data that needs to be sent.
     */
    public sendWaveDone(data: WaveData): void {
        const score: number = data.score;
        const deltaScore: number = data.deltaScore;

        const money: number = data.money;
        const health: number = data.health;
        const towerCount: number = data.towerCount;

        const preS: string = this.prettifyArray(data.preSpawned);
        const preP: string = this.prettifyArray(data.prePassed);
        const postS: string = this.prettifyArray(data.postSpawned);
        const postP: string = this.prettifyArray(data.postPassed);

        const preDeltaH: string = this.prettifyArray(data.preDeltaHealth);
        const postDeltaH: string = this.prettifyArray(data.postDeltaHealth);

        const moneySpent: string = this.prettifyArray(data.moneySpent);
        const timeSpent: string = this.prettifyArray(data.timeSpent);

        const output: string = `${score};${deltaScore};${money};${health};${towerCount};${preS};${preP};${postS};${postP};` +
            `${preDeltaH};${postDeltaH};${moneySpent};${timeSpent}`;

        this.sendMessage('waveDone ' + output);
    }

    /**
     * Close the connection with the GameServer.
     */
    public closeConnection(throwError: boolean = true): void {
        this.socket.close(1000, 'Closed connection with server');

        if (throwError) {
            ErrorState.throw(this.level.game, 'Closed server', false);
        }
    }

    public sendFinalScore(): void {
        this.sendMessage('finalScore ' + this.level.getScore());
    }

    /**
    * Send an updated Hash value to the game server
    */
    public sendHash(hash: number): void {
      this.sendMessage('hash ' + hash);
    }

    /**
     * Change an array to a string in the style that we want.
     * @param input the array that needs to be converted
     */
    private prettifyArray(input: any[]): string {
        return '[' + input.join(', ') + ']';
    }


    public setHashCheck(hashCheck: boolean): void { this.hashCheck = hashCheck; }
    public getHashCheck(): boolean { return this.hashCheck; }

    public getSessionId(): string { return this.sessionId; }
    public setSessionId(sessionId: string): void { this.sessionId = sessionId; }
}
