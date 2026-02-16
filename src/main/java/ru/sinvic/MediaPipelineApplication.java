package ru.sinvic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class MediaPipelineApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediaPipelineApplication.class, args);
    }
}
