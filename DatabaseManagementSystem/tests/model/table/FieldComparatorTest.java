package model.table;

import com.sun.javaws.exceptions.InvalidArgumentException;
import main.exception.InvalidValue;
import main.exception.TypeMismatchException;
import main.model.Field;
import main.model.FieldComparator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FieldComparatorTest {

    private static FieldComparator comparator = new FieldComparator();
    private static Field int12Field = new Field(12);
    private static Field int13Field = new Field(13);
    private static Field int13Field2 = new Field(13);
    private static Field stringField;
    private static Field anotherStringField;
    static {
        try {
            anotherStringField = new Field("another string field");
            stringField = new Field("string field");
        } catch (InvalidValue invalidValue) {
            invalidValue.printStackTrace();
        }
    }

    @Test
    public void enumTest(){
        assertTrue(FieldComparator.Sign.getEnum("<").equals(FieldComparator.Sign.LESS_THAN));
        assertTrue(FieldComparator.Sign.getEnum("<=").equals(FieldComparator.Sign.EQUAL_LESS_THAN));
        assertTrue(FieldComparator.Sign.getEnum("=").equals(FieldComparator.Sign.EQUAL));
        assertTrue(FieldComparator.Sign.getEnum(">").equals(FieldComparator.Sign.GREATER_THAN));
        assertTrue(FieldComparator.Sign.getEnum(">=").equals(FieldComparator.Sign.EQUAL_GREATER_THAN));
        assertTrue(FieldComparator.Sign.getEnum("<>").equals(FieldComparator.Sign.DIFFERENT));
        assertThrows(IllegalArgumentException.class,()->{
            FieldComparator.Sign.getEnum("invalid");
        });
    }

    @Test
    public void equals(){
        try {
            assertTrue(comparator.compareWithSign(int13Field2,int13Field,FieldComparator.Sign.EQUAL));
            assertFalse(comparator.compareWithSign(int13Field2,int12Field,FieldComparator.Sign.EQUAL));
            assertFalse(comparator.compareWithSign(stringField,anotherStringField,FieldComparator.Sign.EQUAL));
            assertTrue(comparator.compareWithSign(stringField,stringField,FieldComparator.Sign.EQUAL));
        } catch (TypeMismatchException e) {
            fail(e);
        }
    }
    @Test
    public void lessThan(){
        try {
            assertTrue(comparator.compareWithSign(int12Field,int13Field,FieldComparator.Sign.LESS_THAN));
            assertFalse(comparator.compareWithSign(int13Field,int13Field2,FieldComparator.Sign.LESS_THAN));
            assertFalse(comparator.compareWithSign(stringField,stringField,FieldComparator.Sign.LESS_THAN));
        } catch (TypeMismatchException e) {
            fail(e);
        }
    }
    @Test
    public void lessThanEqual(){
        try {
            assertTrue(comparator.compareWithSign(int13Field2,int13Field,FieldComparator.Sign.EQUAL_LESS_THAN));
            assertTrue(comparator.compareWithSign(int12Field,int13Field,FieldComparator.Sign.EQUAL_LESS_THAN));
            assertFalse(comparator.compareWithSign(int13Field,int12Field,FieldComparator.Sign.EQUAL_LESS_THAN));
            assertTrue(comparator.compareWithSign(stringField,stringField,FieldComparator.Sign.EQUAL_LESS_THAN));
        } catch (TypeMismatchException e) {
            fail(e);
        }
    }

    @Test
    public void greaterThan(){
        try {
            assertTrue(comparator.compareWithSign(int13Field,int12Field,FieldComparator.Sign.GREATER_THAN));
            assertFalse(comparator.compareWithSign(int12Field,int12Field,FieldComparator.Sign.GREATER_THAN));
            assertFalse(comparator.compareWithSign(stringField,stringField,FieldComparator.Sign.GREATER_THAN));
        } catch (TypeMismatchException e) {
            fail(e);
        }
    }

    @Test
    public void greaterThanEquals(){
        try {
            assertTrue(comparator.compareWithSign(int13Field,int12Field,FieldComparator.Sign.EQUAL_GREATER_THAN));
            assertTrue(comparator.compareWithSign(int13Field,int13Field2,FieldComparator.Sign.EQUAL_GREATER_THAN));
            assertFalse(comparator.compareWithSign(int12Field,int13Field2,FieldComparator.Sign.EQUAL_GREATER_THAN));
            assertTrue(comparator.compareWithSign(stringField,stringField,FieldComparator.Sign.EQUAL_GREATER_THAN));
        } catch (TypeMismatchException e) {
            fail(e);
        }
    }

    @Test
    public void different(){
        try {
            assertTrue(comparator.compareWithSign(int12Field,int13Field,FieldComparator.Sign.DIFFERENT));
            assertFalse(comparator.compareWithSign(int13Field,int13Field2,FieldComparator.Sign.DIFFERENT));
            assertTrue(comparator.compareWithSign(stringField,anotherStringField,FieldComparator.Sign.DIFFERENT));
        } catch (TypeMismatchException e) {
            fail(e);
        }
    }

    @Test
    public void expectTypeMismatch(){
        assertThrows(TypeMismatchException.class,()->{
            comparator.compareWithSign(int12Field,stringField,FieldComparator.Sign.DIFFERENT);
        });
    }

}
