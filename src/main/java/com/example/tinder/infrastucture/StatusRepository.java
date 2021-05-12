package com.example.tinder.infrastucture;

import com.example.tinder.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusRepository extends JpaRepository<Status, Integer> {
    List<Status> findAllByUserID(int userid);
    Status findAllByStatusID(int id);
}
