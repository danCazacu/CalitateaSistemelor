package main.exception;

public class DoesNotExist extends Exception {
    public DoesNotExist(String name){
        super("Entity "+name+" does not exist");
    }
}
