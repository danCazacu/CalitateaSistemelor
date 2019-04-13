package main.exception;

public class InvalidCommand extends Exception{

    public InvalidCommand(){
        super("Invalid command line query");
    }
    public InvalidCommand(String message){
        super(message);
    }
}
