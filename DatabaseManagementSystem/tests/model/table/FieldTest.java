package model.table;

import main.exception.FieldValueNotSet;
import main.exception.InvalidValue;
import main.exception.TypeMismatchException;
import main.model.Column;
import main.model.Field;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FieldTest {

    private static final String STRING_FIELD_VALUE = "string";
    private static final Integer INTEGER_FIELD_VALUE = 10;
    @Test
    public void valueIntegrity(){
        try {
            Field field = new Field(STRING_FIELD_VALUE);
            assertTrue(field.getStringValue().equals(STRING_FIELD_VALUE));
            assertThrows(FieldValueNotSet.class,()->{
               field.getIntValue();
            });
            assertTrue(field.isStringValueSet());
            assertFalse(field.isIntValueSet());

            field.setValue(INTEGER_FIELD_VALUE);

            assertTrue(field.getIntValue().equals(INTEGER_FIELD_VALUE));
            assertThrows(FieldValueNotSet.class,()->{
                field.getStringValue();
            });
            assertFalse(field.isStringValueSet());
            assertTrue(field.isIntValueSet());
        } catch (InvalidValue invalidValue) {
            fail(invalidValue);
        } catch (FieldValueNotSet fieldValueNotSet) {
            fail(fieldValueNotSet);
        }

        try {
            Field field = new Field(INTEGER_FIELD_VALUE);
            assertTrue(field.getIntValue().equals(INTEGER_FIELD_VALUE));
            assertThrows(FieldValueNotSet.class,()->{
                field.getStringValue();
            });
            assertFalse(field.isStringValueSet());
            assertTrue(field.isIntValueSet());

            field.setValue(STRING_FIELD_VALUE);
            assertTrue(field.getStringValue().equals(STRING_FIELD_VALUE));
            assertThrows(FieldValueNotSet.class,()->{
                field.getIntValue();
            });
            assertTrue(field.isStringValueSet());
            assertFalse(field.isIntValueSet());
        } catch (FieldValueNotSet fieldValueNotSet) {
            fail(fieldValueNotSet);
        } catch (InvalidValue invalidValue) {
            fail(invalidValue);
        }

    }


    @Test
    public void equals(){
        try {
            Field zero = new Field("EQUAL FAIL STRING VALUE");
            Field first = new Field(STRING_FIELD_VALUE);
            Field second = new Field(STRING_FIELD_VALUE);
            Field third = new Field(INTEGER_FIELD_VALUE);
            Field forth = new Field(12);
            assertFalse(zero.equals(first));
            assertTrue(first.equals(second));
            assertFalse(first.equals(third));
            assertFalse(third.equals(forth));
            assertTrue(zero.equals(zero));
            assertTrue(forth.equals(forth));

        } catch (InvalidValue invalidValue) {
            invalidValue.printStackTrace();
        }
    }

    @Test
    public void hashCodeTest(){
        try {
            Field first = new Field(STRING_FIELD_VALUE);
            Field second = new Field(STRING_FIELD_VALUE);
            Field third = new Field(INTEGER_FIELD_VALUE);

            assertTrue(first.hashCode() == first.hashCode());
            assertTrue(second.hashCode() !=first.hashCode());
        } catch (InvalidValue invalidValue) {
            fail(invalidValue);
        }
    }

    @Test
    public void copyFrom(){
        try {
            Field second = new Field(STRING_FIELD_VALUE);
            Field third = new Field(INTEGER_FIELD_VALUE);
            Field first = new Field(STRING_FIELD_VALUE+"TEST");
            assertFalse(second.equals(first));
            first.copyFrom(second);
            assertTrue(first.equals(second));
            assertThrows(TypeMismatchException.class,()->{
               first.copyFrom(third);
            });
            Field forth = new Field(13);
            third.copyFrom(forth);
            assertTrue(third.equals(forth));

        } catch (InvalidValue invalidValue) {
            fail(invalidValue);
        } catch (TypeMismatchException e) {
            fail(e);
        }
    }

    @Test
    public void toStringTestEquals(){
        try{
            Field first = new Field(STRING_FIELD_VALUE);
            Field second = new Field(STRING_FIELD_VALUE);
            assertEquals(second.toString(),first.toString());

        } catch (InvalidValue invalidValue) {
            fail(invalidValue);
        }
    }

}
