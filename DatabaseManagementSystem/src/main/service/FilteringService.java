package main.service;

import main.exception.InvalidValue;

public class FilteringService {

    public static void validate(String toValidate) throws InvalidValue {

        assert toValidate != null && !toValidate.trim().isEmpty(): "Precondition failed: input is null";

        if (toValidate.contains("\"")) {

            //assert !toValidate.contains("\""): "Precondition failed ... " + new InvalidValue("\"");
            throw new InvalidValue("\"");
        }
        if (toValidate.contains(",")) {

            //assert !toValidate.contains(","): "Precondition failed ... " + new InvalidValue(",");
            throw new InvalidValue(",");
        }
    }

    public static boolean isValid(String name) {

        try {
            validate(name);
            return true;
        } catch (InvalidValue invalidValue) {
            return false;
        }
    }
}
