package main.graphicalInterface.tableRecord;

public class InvalidEmptyName extends Exception {

    public InvalidEmptyName(){
        super("You can't add empty value");
    }

    public InvalidEmptyName(String message){
        super(message);
    }
}
