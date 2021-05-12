package com.example.tinder.infrastucture;

import com.example.tinder.domain.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByStatusIDOrderByTime(int statusid);

    List<Comment> findAllByStatusID(int statusId, Pageable pageable);
    Comment findByCommentID(long commentId);

}
