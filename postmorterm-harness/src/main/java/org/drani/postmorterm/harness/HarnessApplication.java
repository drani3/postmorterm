package org.drani.postmorterm.harness;

import org.drani.postmorterm.bundle.BundleStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import tools.jackson.databind.json.JsonMapper;

@SpringBootApplication
public class HarnessApplication {

    public static void main(String[] args) {
        SpringApplication.run(HarnessApplication.class, args);
    }

    @Bean
    BundleStore bundleStore(JsonMapper json) {
        return new BundleStore(json);
    }
}
