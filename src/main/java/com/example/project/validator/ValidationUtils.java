package com.example.project.validator;

import java.util.HashMap;
import java.util.Map;

public class ValidationUtils {

    private Map<String, String> errorMessages = new HashMap<>();

    public void isTrue(boolean condition, String errorMessage) {
        if (!condition) {
            errorMessages.put("error", errorMessage);
        }
    }

    public void isFalse(boolean condition, String errorMessage) {
        if (condition) {
            errorMessages.put("error", errorMessage);
        }
    }

    public void throwErrorIfHasMessages() {
        if (!errorMessages.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            errorMessages.values().forEach(sb::append);
            errorMessages.clear(); // Clear messages after throwing
            throw new RuntimeException(sb.toString());
        }
    }
}