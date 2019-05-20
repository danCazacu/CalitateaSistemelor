package main.model;

import main.exception.FieldValueNotSet;
import main.exception.InvalidValue;
import main.exception.TypeMismatchException;

import static main.service.FilteringService.isValid;
import static main.service.FilteringService.validate;

import java.io.IOException;
import java.io.OutputStream;

public class Field {
    private String stringValue;
    private Integer intValue;
    private Column.Type type;

    public Field() {
        stringValue = null;
        intValue = null;

    }

    public Field(String val) throws InvalidValue {

        assert val != null : "Precondition failed: input value null or empty";

        if(!val.trim().isEmpty()) {

            assert isValid(val) : "Precondition failed: " + new InvalidValue(val);
        }
        setValue(val);

    }

    public Field(int val) {

        setValue(val);
    }

    /**
     * @return value as String
     * @throws FieldValueNotSet
     */
    public String getStringValue() throws FieldValueNotSet {
        if (!isStringValueSet())
            throw new FieldValueNotSet(this);

        assert this.type.equals(Column.Type.STRING) : "Post-condition failed: string value was not set correctly to field";
        return stringValue;
    }

    public void setValue(String stringValue) throws InvalidValue {

        assert stringValue != null : "Precondition failed: input value null";

        if(!stringValue.trim().isEmpty()) {
            assert isValid(stringValue) : "Precondition failed: " + new InvalidValue(stringValue);
        }

        this.stringValue = stringValue;
        this.intValue = null;
        this.type = Column.Type.STRING;

        assert this.stringValue.equals(stringValue) && this.intValue == null && this.type.equals(Column.Type.STRING) : "Post-condition failed: STRING value was not set correctly to the field";
    }

    /**
     * @return value as Integer
     * @throws FieldValueNotSet
     */
    public Integer getIntValue() throws FieldValueNotSet {
        if (!isIntValueSet())
            throw new FieldValueNotSet(this);

        assert this.intValue != null && this.type.equals(Column.Type.INT) : "Post-condition failed: INT value was not set correctly to the field";
        return intValue;
    }

    public void setValue(Integer intValue) {
        this.intValue = intValue;
        this.stringValue = null;
        this.type = Column.Type.INT;
        assert this.intValue != null && this.stringValue == null && this.type.equals(Column.Type.INT) : "Post-condition failed: INT value was not set correctly to the field";
    }

    public boolean isStringValueSet() {
        return stringValue != null;
    }

    public boolean isIntValueSet() {
        return intValue != null;
    }

    public Column.Type getType() {

        assert type != null: "Precondition failed: Type is null!";
        return type;
    }

    public boolean equals(Field field) {
        if (!field.getType().equals(this.getType()))
            return false;
        if (field.isIntValueSet() != this.isIntValueSet())
            return false;
        if (field.isStringValueSet() != this.isStringValueSet())
            return false;
        if (field.isStringValueSet()) {
            try {
                if (!field.getStringValue().equals(this.getStringValue()))
                    return false;
            } catch (FieldValueNotSet fieldValueNotSet) {
                fieldValueNotSet.printStackTrace();
            }
        }
        if (field.isIntValueSet()) {
            try {
                if (!field.getIntValue().equals(this.getIntValue()))
                    return false;
            } catch (FieldValueNotSet fieldValueNotSet) {
                fieldValueNotSet.printStackTrace();
            }
        }

        return true;
    }

    /**
     * Uses equalsIgnoreCase for string value match
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if (!(obj instanceof Field)) {
            return false;
        }

        Field field = (Field) obj;
        if (field.hashCode() != this.hashCode())
            return false;
        if (!field.getType().equals(this.getType()))
            return false;
        if (field.isIntValueSet() != this.isIntValueSet())
            return false;
        if (field.isStringValueSet() != this.isStringValueSet())
            return false;
        if (field.isStringValueSet()) {
            try {
                if (!field.getStringValue().equals(this.getStringValue()))
                    return false;
            } catch (FieldValueNotSet fieldValueNotSet) {
                fieldValueNotSet.printStackTrace();
            }
        }
        if (field.isIntValueSet()) {
            try {
                if (!field.getIntValue().equals(this.getIntValue()))
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
        if (isIntValueSet()) {
            try {
                superHash += getIntValue().hashCode();
            } catch (FieldValueNotSet fieldValueNotSet) {
                fieldValueNotSet.printStackTrace();
            }
        }
        if (isStringValueSet()) {
            try {
                superHash -= getStringValue().hashCode();
            } catch (FieldValueNotSet fieldValueNotSet) {
                fieldValueNotSet.printStackTrace();
            }
        }
        return superHash;
    }

    @Override
    public String toString() {
        if (isStringValueSet()) {
            try {
                return getStringValue();
            } catch (FieldValueNotSet fieldValueNotSet) {
                fieldValueNotSet.printStackTrace();
            }
        }
        if (isIntValueSet()) {
            try {
                return getIntValue().toString();
            } catch (FieldValueNotSet fieldValueNotSet) {
                fieldValueNotSet.printStackTrace();
            }
        }
        return "Field.toString not working";
    }

    public void persist(OutputStream outputstream) throws IOException, FieldValueNotSet {
        if (isStringValueSet()) {
            outputstream.write((this.getStringValue() + "\n").getBytes());

        }
        if (isIntValueSet()) {
            outputstream.write((this.getIntValue().toString() + "\n").getBytes());
        }
    }

    public void copyFrom(Field field) throws TypeMismatchException {
        if (!field.getType().equals(this.getType()))
            throw new TypeMismatchException(field, this);

        try {
            if (field.getType().equals(Column.Type.INT)) {
                this.setValue(field.getIntValue());
            }
            if (field.getType().equals(Column.Type.STRING)) {
                this.setValue(field.getStringValue());
            }
        } catch (InvalidValue | FieldValueNotSet ignored) {

        }

    }
}
