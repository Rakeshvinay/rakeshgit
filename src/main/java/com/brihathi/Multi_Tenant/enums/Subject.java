package com.brihathi.Multi_Tenant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Subject {
    PHYSICS("PHY"),
    CHEMISTRY("CHE"),
    ZOOLOGY("ZOO"),
    BOTANY("BOT"),
    ALL("ALL");

    private final String code;

    Subject(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Subject fromCode(String code) {
        if (code == null) return null;
        String upperCode = code.toUpperCase();
        for (Subject subject : values()) {
            if (subject.code.equals(upperCode)) {
                return subject;
            }
        }
        throw new IllegalArgumentException("Invalid subject code: " + code);
    }

    @JsonCreator
    public static Subject fromString(String key) {
        if (key == null) return null;

        // Try to match enum name (e.g., "BOTANY")
        for (Subject subject : values()) {
            if (subject.name().equalsIgnoreCase(key)) {
                return subject;
            }
        }

        // Try to match by code (e.g., "BOT")
        for (Subject subject : values()) {
            if (subject.code.equalsIgnoreCase(key)) {
                return subject;
            }
        }

        throw new IllegalArgumentException("Invalid subject key: " + key);
    }

    @JsonValue
    public String getValue() {
        return this.name();
    }
}
