package main.exception;

public class InexistentColumn extends Exception {

    public InexistentColumn(String columnName){
        super("Column \""+columnName+"\" does not exist");
    }
}
