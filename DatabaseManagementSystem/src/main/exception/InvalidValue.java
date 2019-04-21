package main.exception;

public class InvalidValue extends Exception {

    public InvalidValue(String invalidation){
        super("Input value cannot contain: "+invalidation);
    }
}
