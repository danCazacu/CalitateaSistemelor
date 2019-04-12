package main.exception;

import main.model.Field;

public class NotSameFieldType extends Exception {

    public NotSameFieldType(Field f1, Field f2){
        super("Trying to compare "+f1.getType()+" with "+f2.getType());
    }
}
