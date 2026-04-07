package com.brihathi.Multi_Tenant.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UrlResolverService {

    @Value("${app.domain}")
    private String domain;

    @Value("${app.port:}")
    private String port;

    public String getTenantWebUrl(String tenant) {

        // localhost case
        if (domain.equals("localhost")) {
            return "http://" + tenant + ".localhost:" + port;
        }

        // production case (no port)
        return "https://" + tenant + "." + domain;
    }
}

