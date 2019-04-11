package main.exception;

public class InvalidCommandException extends Exception{

    public InvalidCommandException(){
        super("Invalid command line query");
    }
    public InvalidCommandException(String message){
        super(message);
    }
}
