package com.example.admin_backend.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return commentRepository.findAll();
    }

    // Get comments by postId for users
    public List<CommentEntity> getCommentsByPostId(int postId) {
        return commentRepository.findByPostIdAndIsDeletedFalse(postId);
    }

    // Add a comment (for users, admins, or superusers)
    public CommentEntity addComment(CommentEntity comment) {
        comment.setTimestamp(LocalDateTime.now());

        // Validation for admin/superuser roles
        if (comment.getAdminId() == null && comment.getSuperUserId() == null && comment.getUserId() == null) {
            throw new IllegalArgumentException("Either User, Admin, or SuperUser must be set");
        }

        return commentRepository.save(comment);
    }

    // Soft delete a comment (for users, admins, or superusers)
    public boolean softDeleteComment(int commentId, Integer userId, Integer adminId, Integer superUserId) {
        Optional<CommentEntity> commentOpt = commentRepository.findById(commentId);

        if (commentOpt.isPresent()) {
            CommentEntity comment = commentOpt.get();

            Optional<PostEntity> postOpt = postRepository.findById(comment.getPostId());
            if (postOpt.isPresent()) {
                PostEntity post = postOpt.get();

                // Check permissions for user, admin, or superuser
                if ((userId != null && (comment.getUserId() == userId || post.getUserId() == userId)) ||
                    (adminId != null && (comment.getAdminId() != null && comment.getAdminId().equals(adminId) || post.getAdminId() == adminId)) ||
                    (superUserId != null && comment.getSuperUserId() != null && comment.getSuperUserId().equals(superUserId))) {
                    comment.setDeleted(true);
                    commentRepository.save(comment);
                    return true;
                }
            }
        }

        return false;
    }

    // Update visibility of a comment (admin/superuser only)
    public boolean updateCommentVisibility(int commentId, boolean visible) {
        Optional<CommentEntity> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isPresent()) {
            CommentEntity comment = commentOpt.get();
            comment.setVisible(visible); // Set the visible field
            commentRepository.save(comment); // Save the updated entity
            return true;
        }
        return false;
    }
}
