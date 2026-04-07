package com.brihathi.Multi_Tenant.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.Duration;

@Configuration
public class JacksonConfig {
    
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Create and configure JavaTimeModule
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        // Custom serializer for Duration
        javaTimeModule.addSerializer(Duration.class, new StdSerializer<Duration>(Duration.class) {
            @Override
            public void serialize(Duration duration, JsonGenerator gen, SerializerProvider provider) throws IOException {
                long totalSeconds = duration.getSeconds();
                long hours = totalSeconds / 3600;
                long minutes = (totalSeconds % 3600) / 60;
                long seconds = totalSeconds % 60;
                gen.writeString(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            }
        });
        
        // Register the module
        mapper.registerModule(javaTimeModule);
        
        // Configure serialization features
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
        
        Hibernate6Module hibernate6Module = new Hibernate6Module();
        // Configure Hibernate6Module to handle lazy loading
        hibernate6Module.configure(Hibernate6Module.Feature.FORCE_LAZY_LOADING, false);
        mapper.registerModule(hibernate6Module);
        
        return mapper;
    }
} 