package com.brihathi.Multi_Tenant.context;

import org.springframework.stereotype.Component;

@Component
public class TenantContext {

    // private final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    // public void setTenant(String tenant) {
    //     CURRENT_TENANT.set(tenant);
    // }

    // public String getTenant() {
    //     return CURRENT_TENANT.get();
    // }

    // public void clear() {
    //     CURRENT_TENANT.remove();
    // }

    // ✅ static ThreadLocal
    private static final ThreadLocal<String> TENANT = new ThreadLocal<>();

    // private constructor to prevent instantiation
    private TenantContext() {}

    // ✅ set tenant
    public static void setTenant(String tenant) {
        TENANT.set(tenant);
    }

    // ✅ get tenant
    public static String getTenant() {
        return TENANT.get();
    }

    // ✅ clear tenant (important after request)
    public static void clear() {
        TENANT.remove();
    }
}
