package main.service;

import main.exception.InvalidValue;

public class FilteringService {

    public static void validate(String toValidate) throws InvalidValue {
        if (toValidate.contains("\"")) {
            throw new InvalidValue("\"");
        }
        if (toValidate.contains(","))
            throw new InvalidValue(",");
    }
}
