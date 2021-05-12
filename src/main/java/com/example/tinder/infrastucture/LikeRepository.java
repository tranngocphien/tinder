package com.example.tinder.infrastucture;

import com.example.tinder.domain.Like1;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like1, Integer> {
    List<Like1> findAllByStatusID(int id);


}
