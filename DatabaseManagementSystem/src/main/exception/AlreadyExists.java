package main.exception;

public class AlreadyExists extends Exception{

    public AlreadyExists(String name){
        super("Entity already exists "+name);
    }
}
