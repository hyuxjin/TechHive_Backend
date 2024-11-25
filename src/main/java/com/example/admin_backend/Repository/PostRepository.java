package com.example.admin_backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.admin_backend.Entity.PostEntity;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Integer> {
    List<PostEntity> findByIsDeletedFalse();
    
    @Query("SELECT p FROM PostEntity p WHERE p.isDeleted = false AND p.visible = true")
    List<PostEntity> findByIsDeletedFalseAndVisibleTrue();
    
    List<PostEntity> findByIsDeletedFalseOrderByTimestampDesc();
    Optional<PostEntity> findByPostIdAndIsDeletedFalse(int postId);
    List<PostEntity> findByUserIdAndIsDeletedFalse(int userId);
    List<PostEntity> findByAdminIdAndIsDeletedFalse(int adminId);
    List<PostEntity> findByContentContainingAndIsDeletedFalse(String content);
    long countByIsDeletedFalse();
    List<PostEntity> findTop10ByIsDeletedFalseOrderByTimestampDesc();
    List<PostEntity> findByLikesGreaterThanAndIsDeletedFalse(int likeCount);
    List<PostEntity> findByPostIdInAndIsDeletedFalse(List<Integer> postIds);
}