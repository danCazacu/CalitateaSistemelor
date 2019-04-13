package main.exception;

import main.model.Column;

public class TypeMismatchException extends Exception {

    public TypeMismatchException(Column.Type type1, Column.Type type2) {
        super("Type missmatch: " + type1 + " with " + type2);
    }
}
