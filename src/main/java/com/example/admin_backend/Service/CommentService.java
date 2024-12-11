package com.example.admin_backend.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.admin_backend.Entity.CommentEntity;
import com.example.admin_backend.Entity.PostEntity;
import com.example.admin_backend.Repository.CommentRepository;
import com.example.admin_backend.Repository.PostRepository;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    // Get all comments for admin/superuser
    public List<CommentEntity> getAllComments() {
        System.out.println("Fetching all comments");
        return commentRepository.findAll();
    }

    // Get comments by postId for users
    public List<CommentEntity> getCommentsByPostId(int postId) {
        System.out.println("Fetching comments for post ID: " + postId);
        return commentRepository.findByPostIdAndIsDeletedFalse(postId);
    }

    // Add a comment (for users, admins, or superusers)
    @Transactional
    public CommentEntity addComment(CommentEntity comment) {
        System.out.println("Adding new comment with data: " + comment);

        // Set default values
        ZoneId zoneId = ZoneId.of("Asia/Manila");
        LocalDateTime manilaTime = LocalDateTime.now(zoneId);
        comment.setTimestamp(manilaTime);
        comment.setDeleted(false);
        comment.setVisible(true);

        // Validation
        if (comment.getAdminId() == null && comment.getSuperUserId() == null && comment.getUserId() == null) {
            throw new IllegalArgumentException("Either User, Admin, or SuperUser must be set");
        }

        if (comment.getFullName() == null || comment.getIdNumber() == null) {
            throw new IllegalArgumentException("Full name and ID number must be provided");
        }

        try {
            CommentEntity savedComment = commentRepository.save(comment);
            System.out.println("Successfully saved comment: " + savedComment);
            return savedComment;
        } catch (Exception e) {
            System.err.println("Error saving comment: " + e.getMessage());
            throw e;
        }
    }

    // Soft delete a comment (for users, admins, or superusers)
    @Transactional
    public boolean softDeleteComment(int commentId, Integer userId, Integer adminId, Integer superUserId) {
        try {
            Optional<CommentEntity> commentOpt = commentRepository.findById(commentId);
            if (commentOpt.isPresent()) {
                CommentEntity comment = commentOpt.get();

                // Superuser deletion
                if (superUserId != null) {
                    comment.setDeleted(true);
                    commentRepository.save(comment);
                    System.out.println("Successfully soft deleted comment ID: " + commentId + " by superuser");
                    return true;
                }

                // Validate user/admin deletion permissions
                Optional<PostEntity> postOpt = postRepository.findById(comment.getPostId());
                if (postOpt.isPresent()) {
                    PostEntity post = postOpt.get();

                    boolean isUserAuthorized = userId != null &&
                            (comment.getUserId() != null && comment.getUserId().equals(userId) || post.getUserId() != null && post.getUserId().equals(userId));

                    boolean isAdminAuthorized = adminId != null &&
                            (comment.getAdminId() != null && comment.getAdminId().equals(adminId) || post.getAdminId() != null && post.getAdminId().equals(adminId));

                    if (isUserAuthorized || isAdminAuthorized) {
                        comment.setDeleted(true);
                        commentRepository.save(comment);
                        System.out.println("Successfully soft deleted comment ID: " + commentId);
                        return true;
                    }
                }
            }

            System.out.println("Failed to delete comment ID: " + commentId + " - Permission denied or not found");
            return false;
        } catch (Exception e) {
            System.err.println("Error deleting comment: " + e.getMessage());
            throw e;
        }
    }

    // Update visibility of a comment (admin/superuser only)
    @Transactional
    public boolean updateCommentVisibility(int commentId, boolean visible) {
        try {
            Optional<CommentEntity> commentOpt = commentRepository.findById(commentId);
            if (commentOpt.isPresent()) {
                CommentEntity comment = commentOpt.get();
                comment.setVisible(visible);
                commentRepository.save(comment);
                System.out.println("Successfully updated visibility for comment ID: " + commentId);
                return true;
            }

            System.out.println("Failed to update visibility - Comment not found with ID: " + commentId);
            return false;
        } catch (Exception e) {
            System.err.println("Error updating comment visibility: " + e.getMessage());
            throw e;
        }
    }
}
