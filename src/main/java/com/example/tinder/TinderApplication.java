package com.example.tinder;

import com.example.tinder.infrastucture.CommentRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TinderApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TinderApplication.class, args);
        CommentRepository interactiveRepository = context.getBean(CommentRepository.class);
    }

}
