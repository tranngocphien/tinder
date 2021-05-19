package com.example.tinder.application;

import com.google.gson.Gson;

import com.example.tinder.domain.*;
import com.example.tinder.infrastucture.InteractiveRepository;
import com.example.tinder.infrastucture.NotificationRespository;
import org.apache.commons.net.ntp.TimeStamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

@Service
public class MessageListener {
    @Autowired
    NotificationRespository notificationRespository;
    @Autowired
    InteractiveRepository interactiveRepository;

    @KafkaListener(
            topics = "demo",
            groupId = "group_id"
    )
    public void listen(Activity activity) throws IOException, InterruptedException {
        Notification notification = new Notification();
        SequenceGenerator sequenceGenerator = new SequenceGenerator(1);
        notification.setNotification_id(sequenceGenerator.nextID());
        Gson gson = new Gson();

        if (activity.getType() == ActivityType.FOLLOW) {
            if (interactiveRepository.findAllByInteractiveID_ToUserID(activity.getFrom_userid()) != null) {
                notification.setType(ActivityType.MATCH);
                notification.setUserid(activity.getTo_userid());
                notification.setFrom_userid(activity.getFrom_userid());
                notificationRespository.save(notification);
            }
        } else if (activity.getType() == ActivityType.UP_STATUS) {
            List<Interactive> listid = interactiveRepository.findAllByInteractiveID_ToUserID(activity.getFrom_userid());

            for (Interactive interactive : listid) {
                notification.setType(ActivityType.UP_STATUS);
                notification.setFrom_userid(activity.getFrom_userid());
                notification.setUserid(interactive.getInteractiveID().getFromUserID());
                notificationRespository.save(notification);

                String type = activity.getType().toString().toLowerCase(Locale.ROOT);
                String fileName = type +(new Timestamp(System.currentTimeMillis())).getTime() +".txt";
                String directory = type+ "/" + fileName;
                HDFSfile.writeFile(directory, gson.toJson(notification));

            }


        } else if (activity.getType() == ActivityType.LIKE_STATUS) {
            notification.setType(ActivityType.LIKE_STATUS);
            notification.setFrom_userid(activity.getFrom_userid());
            notification.setUserid(activity.getTo_userid());
            notificationRespository.save(notification);
        } else if (activity.getType() == ActivityType.COMMENT_STATUS) {
            notification.setType(ActivityType.COMMENT_STATUS);
            notification.setFrom_userid(activity.getFrom_userid());
            notification.setUserid(activity.getTo_userid());
            notificationRespository.save(notification);
        } else {
            System.out.println("Nham oi");
        }

        if(activity.getType() != ActivityType.UP_STATUS){
            String type = activity.getType().toString().toLowerCase(Locale.ROOT);
            String fileName = type +(new Timestamp(System.currentTimeMillis())).getTime() +".txt";
            String directory = type+ "/" + fileName;
            HDFSfile.writeFile(directory, gson.toJson(notification));

        }
    }
}
