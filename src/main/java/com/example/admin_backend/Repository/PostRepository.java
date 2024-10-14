package com.example.admin_backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.admin_backend.Entity.PostEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Integer> {

    // Find all posts that are not deleted
     List<PostEntity> findByIsDeletedFalseAndIsVisibleTrue();

    // Find a specific post by ID
Optional<PostEntity> findByPostIdAndIsDeletedFalse(int postId);




    // If you need to find posts by user ID (and not deleted)
    List<PostEntity> findByAdminIdAndIsDeletedFalse(int adminId);

    // If you need to find posts by content (and not deleted)
    List<PostEntity> findByContentContainingAndIsDeletedFalse(String content);

    // If you need to count posts (excluding deleted ones)
    long countByIsDeletedFalse();

    // If you need to find the latest posts (and not deleted)
    List<PostEntity> findTop10ByIsDeletedFalseOrderByTimestampDesc();

    // If you need to find posts with more than a certain number of likes (and not deleted)
    List<PostEntity> findByLikesGreaterThanAndIsDeletedFalse(int likeCount);

    // If you need to find posts by a list of IDs (and not deleted)
    List<PostEntity> findByPostIdInAndIsDeletedFalse(List<Integer> postIds);
}