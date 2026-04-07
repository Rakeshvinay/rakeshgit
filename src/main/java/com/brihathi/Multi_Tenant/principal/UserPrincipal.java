package com.brihathi.Multi_Tenant.principal;
 
 
import java.security.Principal;
 
public class UserPrincipal implements Principal {
 
    private final String name;
 
    // public UserPrincipal(Long userId) {
    //     this.name = userId.toString();
    // }
    public UserPrincipal(String name) {
        this.name = name;
    }
 
    @Override
    public String getName() {
        return name;
    }
}
 
 