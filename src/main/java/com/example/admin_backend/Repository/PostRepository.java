package com.example.admin_backend.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.admin_backend.Entity.PostEntity;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Integer> {
    // Find all posts that are not deleted
    List<PostEntity> findByIsDeletedFalse();
    
    @Query(value = "SELECT * FROM tblpost WHERE is_deleted = false AND is_visible = true ORDER BY timestamp DESC", nativeQuery = true)
    List<PostEntity> findByIsDeletedFalseAndIsVisibleTrue();
    
    // Fetch all posts that are not deleted and order by timestamp in descending order
    List<PostEntity> findByIsDeletedFalseOrderByTimestampDesc();
    
    // Find a specific post by ID that is not deleted
    Optional<PostEntity> findByPostIdAndIsDeletedFalse(int postId);
    
    // Find posts by user ID (and not deleted)
    List<PostEntity> findByUserIdAndIsDeletedFalse(int userId);
    
    List<PostEntity> findByAdminIdAndIsDeletedFalse(int adminId);
    
    // Find posts by content (and not deleted)
    List<PostEntity> findByContentContainingAndIsDeletedFalse(String content);
    
    // Count posts (excluding deleted ones)
    long countByIsDeletedFalse();
    
    // Find the latest posts (and not deleted)
    List<PostEntity> findTop10ByIsDeletedFalseOrderByTimestampDesc();
    
    // Find posts with more than a certain number of likes (and not deleted)
    List<PostEntity> findByLikesGreaterThanAndIsDeletedFalse(int likeCount);
    
    // Find posts by a list of IDs (and not deleted)
    List<PostEntity> findByPostIdInAndIsDeletedFalse(List<Integer> postIds);

    // Find all report posts that are not deleted
    @Query("SELECT p FROM PostEntity p WHERE p.isSubmittedReport = true AND p.isDeleted = false ORDER BY p.timestamp DESC")
    List<PostEntity> findByIsSubmittedReportTrueAndIsDeletedFalseOrderByTimestampDesc();
    
    // Find report posts by status
    @Query("SELECT p FROM PostEntity p WHERE p.isSubmittedReport = true AND p.status = :status AND p.isDeleted = false")
    List<PostEntity> findByIsSubmittedReportTrueAndStatusAndIsDeletedFalse(@Param("status") String status);
    
    @Query("SELECT p FROM PostEntity p WHERE p.isSubmittedReport = true AND p.status = :status AND p.isDeleted = false AND p.userId = :userId")
    List<PostEntity> findReportPostsByStatusAndUserId(@Param("status") String status, @Param("userId") Integer userId);
    
    @Query("SELECT COUNT(p) FROM PostEntity p WHERE p.isSubmittedReport = true AND p.status = :status AND p.isDeleted = false")
    long countReportPostsByStatus(@Param("status") String status);
}