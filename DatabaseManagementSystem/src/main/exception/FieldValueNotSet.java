package main.exception;

import main.model.Field;

public class FieldValueNotSet extends Exception{
    public FieldValueNotSet(Field field){
        super("Field type is: "+field.getType());
    }
}
