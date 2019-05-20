package main.model;

import main.exception.FieldValueNotSet;
import main.exception.TypeMismatchException;

import java.util.Arrays;

import static main.model.Column.Type.INT;
import static main.model.Column.Type.STRING;
import static main.model.FieldComparator.Sign.*;

public class FieldComparator {
    public enum Sign {
        EQUAL("="),
        LESS_THAN("<"),
        GREATER_THAN(">"),
        EQUAL_LESS_THAN("<="),
        EQUAL_GREATER_THAN(">="),
        DIFFERENT("<>");

        private String value;

        Sign(String s) {

            assert s!= null && !s.trim().isEmpty() : "Precondition failed: input is null or empty";
            this.value = s;

            assert this.value.equals(s) : "Post-condition failed ... " + this.value + " != " + s;
        }

        public String getValue() {

            assert value != null;
            return value;
        }

        public static FieldComparator.Sign getEnum(String value) {

            assert value != null && !value.trim().isEmpty() : "Precondition failed: input value is null";

            assert Sign.values().length > 0:"Invariant failed: does not exist any sign value";
            for (FieldComparator.Sign sign : FieldComparator.Sign.values()) {
                if (sign.getValue().equalsIgnoreCase(value)) {

                    assert value.equalsIgnoreCase(sign.getValue()) : "Post-condition failed: value are not equals but will be returned";
                    return sign;
                }
            }

            assert Arrays.asList(FieldComparator.Sign.values()).contains(value) : "Post-condition failed: value exist but was not returned";
            throw new IllegalArgumentException();
        }
    }


    public int compare(Field o1, Field o2) throws TypeMismatchException {

        checkFieldInput(o1);
        checkFieldInput(o2);

        if (o1.equals(o2))
            return 0;
        if (o1.isStringValueSet() && o2.isStringValueSet()) {
            try {

                assert o1.getType().equals(o2.getType()) && o1.getType().equals(STRING): "isStringValueSet were set for both input fields, but are not STRING types";
                return o1.getStringValue().compareTo(o2.getStringValue());
            } catch (FieldValueNotSet fieldValueNotSet) {
                fieldValueNotSet.printStackTrace();
            }
        }
        if (o1.isIntValueSet() && o2.isIntValueSet()) {
            try {

                assert o1.getType().equals(o2.getType()) && o1.getType().equals(INT): "isIntValueSet were set for both input fields, but are not INT types";
                return o1.getIntValue().compareTo(o2.getIntValue());
            } catch (FieldValueNotSet fieldValueNotSet) {
                fieldValueNotSet.printStackTrace();
            }
        }

        assert !o1.getType().equals(o2.getType()): "The types of the input fields are equals but a TypeMismatchException will be thrown";
        throw new TypeMismatchException(o1, o2);
    }

    public boolean isEqual(Field o1, Field o2) throws TypeMismatchException {
        checkFieldInput(o1);
        checkFieldInput(o2);

        if (o1.equals(o2)) {

            assert o1.getType().equals(o2.getType()): "Post-condition failed: fields are considered true, but the types are not equals...";
            try {
                if (o1.getType().equals(INT)) {

                    assert o1.getIntValue().equals(o2.getIntValue()) : "Post-condition failed: fields are of type INT but are not equals even it is considered to be equals";
                } else {

                    assert o1.getStringValue().equals(o2.getStringValue()) : "Post-condition failed: fields are of type STRING but are not equals even it is considered to be equals";
                }
            } catch (FieldValueNotSet fieldValueNotSet) {
                //fieldValueNotSet.printStackTrace();

                assert true: "Field not set was thrown and shouldn't be here... ";
            }
            return true;
        }

        try {
            if (o1.getType().equals(INT)) {

                assert !o1.getIntValue().equals(o2.getIntValue()) : "Post-condition failed: fields are of type INT and are equals but weren't found as equals";
            } else {

                assert !o1.getStringValue().equals(o2.getStringValue()) : "Post-condition failed: fields are of type STRING and are equals but weren't found as equals";
            }
        } catch (FieldValueNotSet fieldValueNotSet) {
            //fieldValueNotSet.printStackTrace();

            assert true: "Field not set was thrown and shouldn't be here... ";
        }
        return false;
    }

    public boolean isGreaterThan(Field o1, Field o2) throws TypeMismatchException {

        checkFieldInput(o1);
        checkFieldInput(o2);

        if (compare(o1, o2) > 0) {

            try {
                if (o1.getType().equals(INT)) {

                    assert o1.getIntValue() > (o2.getIntValue()) : "Precondition failed: fields are of type INT and o1 <= o2 but the comparision wasn't made correctly";
                } else {

                    assert o1.getIntValue().compareTo(o2.getIntValue()) > 0 : "Precondition failed: fields are of type STRING and o1 <= o2 but the comparision wasn't made correctly";
                }
            } catch (FieldValueNotSet fieldValueNotSet) {
                //fieldValueNotSet.printStackTrace();

                assert true: "Field not set was thrown and shouldn't be here... ";
            }
            return true;
        }

        try {
            if (o1.getType().equals(INT)) {

                assert o1.getIntValue() <= (o2.getIntValue()) : "Post-condition failed: fields are of type INT and o1 > o2 but the comparision wasn't made correctly";
            } else {

                assert o1.getIntValue().compareTo(o2.getIntValue()) <= 0 : "Post-condition failed: fields are of type STRING and o1 > o2 but the comparision wasn't made correctly";
            }
        } catch (FieldValueNotSet fieldValueNotSet) {
            //fieldValueNotSet.printStackTrace();

            assert true: "Field not set was thrown and shouldn't be here... ";
        }
        return false;
    }

    public boolean isLesserThan(Field o1, Field o2) throws TypeMismatchException {

        checkFieldInput(o1);
        checkFieldInput(o2);

        if (compare(o1, o2) < 0) {
            try {
                if (o1.getType().equals(INT)) {

                    assert o1.getIntValue() < (o2.getIntValue()) : "Precondition failed: fields are of type INT and o1 >= o2 but the comparision wasn't made correctly";
                } else {

                    assert o1.getIntValue().compareTo(o2.getIntValue()) < 0 : "Precondition failed: fields are of type STRING and o1 >= o2 but the comparision wasn't made correctly";
                }
            } catch (FieldValueNotSet fieldValueNotSet) {
                //fieldValueNotSet.printStackTrace();

                assert true: "Field not set was thrown and shouldn't be here... ";
            }
            return true;
        }

        try {
            if (o1.getType().equals(INT)) {

                assert o1.getIntValue() >= (o2.getIntValue()) : "Post-condition failed: fields are of type INT and o1 < o2 but the comparision wasn't made correctly";
            } else {

                assert o1.getIntValue().compareTo(o2.getIntValue()) >= 0 : "Post-condition failed: fields are of type STRING and o1 < o2 but the comparision wasn't made correctly";
            }
        } catch (FieldValueNotSet fieldValueNotSet) {
            //fieldValueNotSet.printStackTrace();

            assert true: "Field not set was thrown and shouldn't be here... ";
        }
        return false;
    }

    public boolean isGreaterThanOrEquals(Field o1, Field o2) throws TypeMismatchException {

        checkFieldInput(o1);
        checkFieldInput(o2);

        if (compare(o1, o2) >= 0) {

            try {
                if (o1.getType().equals(INT)) {

                    assert o1.getIntValue() >= (o2.getIntValue()) : "Precondition failed: fields are of type INT and o1 < o2 but the comparision wasn't made correctly";
                } else {

                    assert o1.getIntValue().compareTo(o2.getIntValue()) >= 0 : "Precondition failed: fields are of type STRING and o1 < o2 but the comparision wasn't made correctly";
                }
            } catch (FieldValueNotSet fieldValueNotSet) {
                //fieldValueNotSet.printStackTrace();

                assert true: "Field not set was thrown and shouldn't be here... ";
            }
            return true;
        }

        try {
            if (o1.getType().equals(INT)) {

                assert o1.getIntValue() < (o2.getIntValue()) : "Post-condition failed: fields are of type INT and o1 >= o2 but the comparision wasn't made correctly";
            } else {

                assert o1.getIntValue().compareTo(o2.getIntValue()) < 0 : "Post-condition failed: fields are of type STRING and o1 >= o2 but the comparision wasn't made correctly";
            }
        } catch (FieldValueNotSet fieldValueNotSet) {
            //fieldValueNotSet.printStackTrace();

            assert true: "Field not set was thrown and shouldn't be here... ";
        }
        return false;
    }

    public boolean isLesserThanOrEquals(Field o1, Field o2) throws TypeMismatchException {
        if (compare(o1, o2) <= 0) {

            try {
                if (o1.getType().equals(INT)) {

                    assert o1.getIntValue() <= (o2.getIntValue()) : "Precondition failed: fields are of type INT and o1 > o2 but the comparision wasn't made correctly";
                } else {

                    assert o1.getIntValue().compareTo(o2.getIntValue()) <= 0 : "Precondition failed: fields are of type STRING and o1 > o2 but the comparision wasn't made correctly";
                }
            } catch (FieldValueNotSet fieldValueNotSet) {
                //fieldValueNotSet.printStackTrace();

                assert true: "Field not set was thrown and shouldn't be here... ";
            }
            return true;
        }

        try {
            if (o1.getType().equals(INT)) {

                assert o1.getIntValue() > (o2.getIntValue()) : "Post-condition failed: fields are of type INT and o1 <= o2 but the comparision wasn't made correctly";
            } else {

                assert o1.getIntValue().compareTo(o2.getIntValue()) > 0 : "Post-condition failed: fields are of type STRING and o1 <= o2 but the comparision wasn't made correctly";
            }
        } catch (FieldValueNotSet fieldValueNotSet) {
            //fieldValueNotSet.printStackTrace();

            assert true: "Field not set was thrown and shouldn't be here... ";
        }
        return false;
    }

    public boolean isDifferent(Field o1, Field o2) throws TypeMismatchException {

        checkFieldInput(o1);
        checkFieldInput(o2);

        if (compare(o1, o2) != 0) {

            try {
                if (o1.getType().equals(INT)) {

                    assert o1.getIntValue() != (o2.getIntValue()) : "Precondition failed: fields are of type INT and o1 == o2 but the comparision wasn't made correctly";
                } else {

                    assert o1.getIntValue().compareTo(o2.getIntValue()) != 0 : "Precondition failed: fields are of type STRING and o1 == o2 but the comparision wasn't made correctly";
                }
            } catch (FieldValueNotSet fieldValueNotSet) {
                //fieldValueNotSet.printStackTrace();

                assert true: "Field not set was thrown and shouldn't be here... ";
            }
            return true;
        }

        try {
            if (o1.getType().equals(INT)) {

                assert o1.getIntValue() == (o2.getIntValue()) : "Post-condition failed: fields are of type INT and o1 != o2 but the comparision wasn't made correctly";
            } else {

                assert o1.getIntValue().compareTo(o2.getIntValue()) == 0 : "Post-condition failed: fields are of type STRING and o1 != o2 but the comparision wasn't made correctly";
            }
        } catch (FieldValueNotSet fieldValueNotSet) {
            //fieldValueNotSet.printStackTrace();

            assert true: "Field not set was thrown and shouldn't be here... ";
        }
        return false;
    }

    public boolean compareWithSign(Field o1, Field o2, Sign sign) throws TypeMismatchException {

        checkFieldInput(o1);
        checkFieldInput(o2);
        assert sign != null && sign.getValue() != null && !sign.getValue().trim().isEmpty(): "Precondition failed: input sign is null or the value is null or empty";
        assert Arrays.asList(Sign.values()).contains(sign): "Precondition failed: Input sign value isn't part of the Sign values list";

        switch (sign) {
            case EQUAL:
                assert sign.getValue().equals(EQUAL.getValue());
                return isEqual(o1, o2);
            case LESS_THAN:
                assert sign.getValue().equals(LESS_THAN.getValue());
                return isLesserThan(o1, o2);
            case GREATER_THAN:
                assert sign.getValue().equals(GREATER_THAN.getValue());
                return isGreaterThan(o1, o2);
            case EQUAL_LESS_THAN:
                assert sign.getValue().equals(EQUAL_LESS_THAN.getValue());
                return isLesserThanOrEquals(o1, o2);
            case EQUAL_GREATER_THAN:
                assert sign.getValue().equals(EQUAL_GREATER_THAN.getValue());
                return isGreaterThanOrEquals(o1, o2);
            case DIFFERENT:
                assert sign.getValue().equals(DIFFERENT.getValue());
                return isDifferent(o1, o2);
            default:
                assert !Arrays.asList(Sign.values()).contains(sign): "Post-condition failed: even if the sign is part of the sign values list, the comparation wasn't made";
                return false;
        }
    }

    private void checkFieldInput(Field field) {

        assert field != null: "Precondition failed ... input field is null";
        assert field.getType() != null: "Precondition failed... input field has no type set";

        try {
            if (field.getType().equals(STRING)) {

                assert field.isStringValueSet() && field.getStringValue() != null && !field.getStringValue().trim().isEmpty() : "Field of type STRING wasn't correctly created";
            } else {

                assert field.isIntValueSet() && field.getIntValue() != null : "Field of type INT wasn't correctly created";
            }
        } catch (FieldValueNotSet fieldValueNotSet) {

            //fieldValueNotSet.printStackTrace();
            assert true: "Field not set was thrown and shouldn't be here... ";
        }
    }

}
