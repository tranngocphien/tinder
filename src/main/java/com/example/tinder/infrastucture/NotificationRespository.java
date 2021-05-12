package com.example.tinder.infrastucture;

import com.example.tinder.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRespository extends JpaRepository<Notification, Integer> {
}
