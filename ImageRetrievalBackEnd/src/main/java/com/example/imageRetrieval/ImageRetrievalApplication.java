package com.example.imageRetrieval;

import com.example.imageRetrieval.image.ImgScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ImageRetrievalApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImageRetrievalApplication.class, args);
    }

}
