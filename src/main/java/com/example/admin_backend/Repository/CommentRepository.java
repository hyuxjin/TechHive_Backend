package com.example.admin_backend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.admin_backend.Entity.CommentEntity;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {

    // Find comments by postId where isDeleted is false (for fetching all comments under a specific post)
    List<CommentEntity> findByPostIdAndIsDeletedFalse(int postId);
    
    // Find a specific comment by commentId (for fetching a single comment by its ID)
    Optional<CommentEntity> findByCommentId(int commentId);
}
