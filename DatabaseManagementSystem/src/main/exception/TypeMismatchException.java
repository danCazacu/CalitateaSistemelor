package main.exception;

import main.model.Column;
import main.model.Field;

public class TypeMismatchException extends Exception {

    public TypeMismatchException(Column.Type type1, Column.Type type2) {
        super("Type missmatch: " + type1 + " with " + type2);
    }
    public TypeMismatchException(Field f1, Field f2){
        super("Trying to compare "+f1.getType()+" with "+f2.getType());
    }
    public TypeMismatchException(Column.Type expected, Column.Type actual, String columnName){
        super("Trying to match "+actual+" into "+expected+" column type. Column name \""+columnName+"\"");
    }
}
