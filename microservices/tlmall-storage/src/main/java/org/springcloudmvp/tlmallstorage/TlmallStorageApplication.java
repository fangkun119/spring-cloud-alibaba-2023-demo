package org.springcloudmvp.tlmallstorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TlmallStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(TlmallStorageApplication.class, args);
    }

}
