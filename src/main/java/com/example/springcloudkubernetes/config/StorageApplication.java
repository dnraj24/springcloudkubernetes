package com.example.springcloudkubernetes.config;

import com.example.springcloudkubernetes.SpringcloudkubernetesApplication;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.spring.storage.GoogleStorageProtocolResolver;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.InputStream;

@Configuration
@Import(GoogleStorageProtocolResolver.class)
public class StorageApplication {
    @Bean
    public static Storage storage() throws Exception {
        String googleKey = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        InputStream keyStream = SpringcloudkubernetesApplication.class.getClassLoader().getResourceAsStream("skipcart_app.json");
        // Define the Google cloud storage
        Storage storage = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(keyStream))
                .build()
                .getService();

        return storage;
    }
}
