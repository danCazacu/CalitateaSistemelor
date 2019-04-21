package main.graphicalInterface.tableRecord;

public class InvalidEmptyName extends Exception {

    public InvalidEmptyName(){
        super("You can't add empty value");
    }
}
