package main.model;

import main.exception.NotSameFieldType;

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
            this.value = s;
        }

        public String getValue() {
            return value;
        }

        public static FieldComparator.Sign getEnum(String value) {
            for (FieldComparator.Sign sign : FieldComparator.Sign.values())
                if (sign.getValue().equalsIgnoreCase(value)) {
                    return sign;
                }

            throw new IllegalArgumentException();
        }
    }


    public int compare(Field o1, Field o2) throws NotSameFieldType {
        if (o1.equals(o2))
            return 0;
        if (o1.isStringValueSet() && o2.isStringValueSet())
            return o1.getStringValue().compareTo(o2.getStringValue());
        if (o1.isIntValueSet() && o2.isIntValueSet()) {
            return o1.getIntValue().compareTo(o2.getIntValue());
        }
        throw new NotSameFieldType(o1, o2);
    }

    public boolean isEqual(Field o1, Field o2) throws NotSameFieldType {
        if (o1.equals(o2))
            return true;
        return false;
    }

    public boolean isGreaterThan(Field o1, Field o2) throws NotSameFieldType {
        if (compare(o1, o2) > 0)
            return true;
        return false;
    }

    public boolean isLesserThan(Field o1, Field o2) throws NotSameFieldType {
        if (compare(o1, o2) < 0)
            return true;
        return false;
    }

    public boolean isGreaterThanOrEquals(Field o1, Field o2) throws NotSameFieldType {
        if (compare(o1, o2) >= 0)
            return true;
        return false;
    }

    public boolean isLesserThanOrEquals(Field o1, Field o2) throws NotSameFieldType {
        if (compare(o1, o2) <= 0)
            return true;
        return false;
    }

    public boolean isDifferent(Field o1, Field o2) throws NotSameFieldType {
        if (compare(o1, o2) != 0)
            return true;
        return false;
    }

    public boolean compareWithSign(Field o1, Field o2, Sign sign) throws NotSameFieldType {
        switch (sign) {
            case EQUAL:
                return isEqual(o1, o2);
            case LESS_THAN:
                return isLesserThan(o1, o2);
            case GREATER_THAN:
                return isGreaterThan(o1, o2);
            case EQUAL_LESS_THAN:
                return isLesserThanOrEquals(o1, o2);
            case EQUAL_GREATER_THAN:
                return isGreaterThanOrEquals(o1, o2);
            case DIFFERENT:
                return isDifferent(o1, o2);
            default:
                return false;
        }
    }

}
