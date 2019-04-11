package main.exception;

import main.model.Column;

public class WrongTypeInColumnException extends Exception{

    public WrongTypeInColumnException(Column.Type expected, Column.Type actual, String columnName){
        super("Trying to match "+actual+" into "+expected+" column type. Column name \""+columnName+"\"");
    }
}
