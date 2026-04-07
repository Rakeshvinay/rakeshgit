package com.brihathi.Multi_Tenant.enums;
 
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
 
public enum ReportValidated {
    REPORTED("REPORTED"),
    UNDER_REVIEW("UNDER-REVIEW"),
    REVIEWED_RESOLVED("REVIEWED-RESOLVED"),
    REVIEWED_NO_CHANGE_NEEDED("REVIEWED-NO CHANGE NEEDED");
 
    private final String value;
 
    ReportValidated(String value) {
        this.value = value;
    }
 
    @JsonValue
    public String getValue() {
        return value;
    }
 
    @JsonCreator
    public static ReportValidated fromValue(String value) {
        for (ReportValidated status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid ReportValidated value: " + value);
    }
}
