package main.exception;

public class ColumnAlreadyExists extends AlreadyExists{
    public ColumnAlreadyExists(String columnName){
        super("Column already exists: "+columnName);
    }
}
