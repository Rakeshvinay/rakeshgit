package com.brihathi.Multi_Tenant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Difficulty {
    BASIC,
    INTERMEDIATE,
    ADVANCED;

    @JsonCreator
    public static Difficulty fromString(String key) {
        return key == null ? null : Difficulty.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String getValue() {
        return this.name();
    }
} 