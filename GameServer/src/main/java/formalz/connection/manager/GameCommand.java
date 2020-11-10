package formalz.connection.manager;

import java.util.Objects;

public class GameCommand {

    public static final String STARTUP = "startup";

    public String command;

    public String arguments;

    public GameCommand(String command, String arguments) {
        Objects.requireNonNull(command, "command must not be null");
        Objects.requireNonNull(arguments, "arguments must not be null");
        this.command = command;
        this.arguments = arguments;
    }


    @Override
    public String toString() {
        return "GameCommand [arguments=" + arguments + ", command=" + command + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((arguments == null) ? 0 : arguments.hashCode());
        result = prime * result + ((command == null) ? 0 : command.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GameCommand other = (GameCommand) obj;
        if (arguments == null) {
            if (other.arguments != null)
                return false;
        } else if (!arguments.equals(other.arguments))
            return false;
        if (command == null) {
            if (other.command != null)
                return false;
        } else if (!command.equals(other.command))
            return false;
        return true;
    }
}