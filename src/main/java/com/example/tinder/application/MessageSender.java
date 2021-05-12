package com.example.tinder.application;

import com.example.tinder.domain.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageSender {
    @Autowired
    private KafkaTemplate<String, Activity> kafkaTemplate;

    public void sendmessage(Activity activity){
        kafkaTemplate.send("demo",activity);
    }
}
