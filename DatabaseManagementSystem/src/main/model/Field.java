package main.model;

import main.exception.FieldValueNotSet;
import main.persistance.PersistenceContants;

import java.io.IOException;
import java.io.OutputStream;

public class Field{
    private String stringValue;
    private Integer intValue;
    private Column.Type type;

    public Field(){
        stringValue = null;
        intValue = null;

    }

    public Field(String val){
        setValue(val);
    }

    public Field(int val){
        setValue(val);
    }

    /**
     * @return value as String
     * @throws FieldValueNotSet
     */
    public String getStringValue() throws FieldValueNotSet {
        if(!isStringValueSet())
            throw new FieldValueNotSet(this);
        return stringValue;
    }

    public void setValue(String stringValue) {
        this.stringValue = stringValue;
        this.intValue = null;
        this.type = Column.Type.STRING;
    }

    /**
     * @return value as Integer
     * @throws FieldValueNotSet
     */
    public Integer getIntValue() throws FieldValueNotSet {
        if(!isIntValueSet())
            throw new FieldValueNotSet(this);
        return intValue;
    }

    public void setValue(Integer intValue) {
        this.intValue = intValue;
        this.stringValue = null;
        this.type = Column.Type.INT;
    }

    public boolean isStringValueSet(){
        return stringValue != null;
    }

    public boolean isIntValueSet(){
        return intValue != null;
    }

    public Column.Type getType() {
        return type;
    }
    public boolean equals(Field field){
        if(!field.getType().equals(this.getType()))
            return false;
        if(field.isIntValueSet()!=this.isIntValueSet())
            return false;
        if(field.isStringValueSet()!=this.isStringValueSet())
            return false;
        if(field.isStringValueSet()){
            try {
                if(!field.getStringValue().equals(this.getStringValue()))
                    return false;
            } catch (FieldValueNotSet fieldValueNotSet) {
                fieldValueNotSet.printStackTrace();
            }
        }
        if(field.isIntValueSet()){
            try {
                if(!field.getIntValue().equals(this.getIntValue()))
                    return false;
            } catch (FieldValueNotSet fieldValueNotSet) {
                fieldValueNotSet.printStackTrace();
            }
        }

        return true;
    }
    /**
     * Uses equalsIgnoreCase for string value match
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Field)) {
            return false;
        }

        Field field = (Field) obj;
        if(field.hashCode()!=this.hashCode())
            return false;
        if(!field.getType().equals(this.getType()))
            return false;
        if(field.isIntValueSet()!=this.isIntValueSet())
            return false;
        if(field.isStringValueSet()!=this.isStringValueSet())
            return false;
        if(field.isStringValueSet()){
            try {
                if(!field.getStringValue().equals(this.getStringValue()))
                    return false;
            } catch (FieldValueNotSet fieldValueNotSet) {
                fieldValueNotSet.printStackTrace();
            }
        }
        if(field.isIntValueSet()){
            try {
                if(!field.getIntValue().equals(this.getIntValue()))
                    return false;
            } catch (FieldValueNotSet fieldValueNotSet) {
                fieldValueNotSet.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int superHash = super.hashCode();
        if(isIntValueSet()) {
            try {
                superHash+=getIntValue().hashCode();
            } catch (FieldValueNotSet fieldValueNotSet) {
                fieldValueNotSet.printStackTrace();
            }
        }
        if(isStringValueSet()) {
            try {
                superHash-=getStringValue().hashCode();
            } catch (FieldValueNotSet fieldValueNotSet) {
                fieldValueNotSet.printStackTrace();
            }
        }
        return superHash;
    }

    @Override
    public String toString() {
        if(isStringValueSet()) {
            try {
                return getStringValue();
            } catch (FieldValueNotSet fieldValueNotSet) {
                fieldValueNotSet.printStackTrace();
            }
        }
        if(isIntValueSet()) {
            try {
                return getIntValue().toString();
            } catch (FieldValueNotSet fieldValueNotSet) {
                fieldValueNotSet.printStackTrace();
            }
        }
        return "Field.toString not working";
    }

    public void persist(OutputStream outputstream) throws IOException, FieldValueNotSet {
        if(isStringValueSet()) {
            outputstream.write((this.getStringValue()+"\n").getBytes());

        }
        if(isIntValueSet()) {
            outputstream.write((this.getIntValue().toString() + "\n").getBytes());
        }
    }
}
