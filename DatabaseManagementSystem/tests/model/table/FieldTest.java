package model.table;

import main.exception.FieldValueNotSet;
import main.exception.InvalidValue;
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

        } catch (InvalidValue invalidValue) {
            invalidValue.printStackTrace();
        }
    }

}
