package main.exception;

public class ColumnAlreadyExists extends Exception{
    public ColumnAlreadyExists(String columnName){
        super("Column already exists: "+columnName);
    }
}
