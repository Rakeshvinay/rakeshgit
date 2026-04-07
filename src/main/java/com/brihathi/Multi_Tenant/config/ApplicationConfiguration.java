package com.brihathi.Multi_Tenant.config;


import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.entity.Educator;
import com.brihathi.Multi_Tenant.repository.UserRepository;
import com.brihathi.Multi_Tenant.repository.EducatorRepository;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class ApplicationConfiguration {
    private final UserRepository userRepository;
    private final EducatorRepository educatorRepository;

    public ApplicationConfiguration(UserRepository userRepository,EducatorRepository educatorRepository) {
        this.userRepository = userRepository;
        this.educatorRepository = educatorRepository;
    }

    // @Bean
    // UserDetailsService userDetailsService() {
    //     return username -> userRepository.findByEnrollmentId(username)
    //             .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    // }
    @Bean
    UserDetailsService userDetailsService() {

        return username -> {

            // 1️⃣ Try USER (by enrollmentId OR email if needed)
            User user = userRepository.findByEnrollmentId(username).orElse(null);
            if (user != null) {
                return user;
            }

            // 2️⃣ Try EDUCATOR (by email)
            Educator educator = educatorRepository.findByEmail(username).orElse(null);
            if (educator != null) {
                return educator;
            }

            // ❌ Nothing found
            throw new UsernameNotFoundException(
                    "User or Educator not found with identifier: " + username
            );
        };
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
     @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}