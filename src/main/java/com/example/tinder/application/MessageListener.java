package com.example.tinder.application;

import com.example.tinder.domain.Activity;
import com.example.tinder.domain.ActivityType;
import com.example.tinder.domain.Interactive;
import com.example.tinder.domain.Notification;
import com.example.tinder.infrastucture.InteractiveRepository;
import com.example.tinder.infrastucture.NotificationRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public void listen(Activity activity) {
        Notification notification = new Notification();

        if (activity.getType() == ActivityType.FOLLOW) {
            if (interactiveRepository.findAllByInteractiveID_ToUserID(activity.getFrom_userid()) != null) {
                notification.setType(ActivityType.MATCH);
                notification.setUserid(activity.getTo_userid());
                notification.setFrom_userid(activity.getFrom_userid());
                notification.setNotification_id(0);
                notificationRespository.save(notification);
            }
        } else if (activity.getType() == ActivityType.UP_STATUS) {
            List<Interactive> listid = interactiveRepository.findAllByInteractiveID_ToUserID(activity.getFrom_userid());

            for (Interactive interactive : listid) {
                notification.setType(ActivityType.UP_STATUS);
                notification.setFrom_userid(activity.getFrom_userid());
                notification.setUserid(interactive.getInteractiveID().getFromUserID());
                notification.setNotification_id(0);
                notificationRespository.save(notification);

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
        String type = activity.getType().toString().toLowerCase(Locale.ROOT);
        String fileName = type + LocalDate.now() +".txt";
        String directory = type+ "/" + fileName;
        HDFSfile.writeFile(directory, notification.toString());

    }
}
