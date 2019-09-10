package com.fluffytiger.earlygamewebapp;

import com.fluffytiger.earlygamewebapp.model.Role;
import com.fluffytiger.earlygamewebapp.model.User;
import com.fluffytiger.earlygamewebapp.properties.FileStorageProperties;
import com.fluffytiger.earlygamewebapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
@EnableCaching
public class EarlyGameWebAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(EarlyGameWebAppApplication.class, args);
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Component
    public static class CacheCustomizer implements CacheManagerCustomizer<ConcurrentMapCacheManager> {
        @Override
        public void customize(ConcurrentMapCacheManager cacheManager) {
            cacheManager.setCacheNames(Arrays.asList("jars", "version"));
        }
    }

    @Component
    @Profile("dev")
    class InsertTestData implements CommandLineRunner {
        final UserRepository userRepository;
        final PasswordEncoder encoder;

        public InsertTestData(UserRepository userRepository, PasswordEncoder encoder) {
            this.userRepository = userRepository;
            this.encoder = encoder;
        }

        @Override
        public void run(String... args) throws Exception {
            userRepository.save(new User("Admin007", encoder.encode("Admin007"), new HashSet<>(Arrays.asList(Role.ROLE_CLIENT, Role.ROLE_ADMIN))));
        }
    }
}
