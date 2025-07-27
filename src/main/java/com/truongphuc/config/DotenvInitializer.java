package com.truongphuc.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ConfigurableApplicationContext;

public class DotenvInitializer {
    public static void initialize() {
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry ->{
                    System.setProperty(entry.getKey(), entry.getValue());
                });
    }
}