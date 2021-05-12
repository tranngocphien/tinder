package com.example.tinder.infrastucture;

import com.example.tinder.domain.LikeComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeCommentRepository extends JpaRepository<LikeComment, Integer> {
}
