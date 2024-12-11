package com.example.admin_backend.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.admin_backend.Entity.AdminEntity;
import com.example.admin_backend.Entity.CommentEntity;
import com.example.admin_backend.Repository.AdminRepository;
import com.example.admin_backend.Service.CommentService;

@RestController
@RequestMapping("/comments")
@CrossOrigin(origins = "http://localhost:3000")
public class CommentController {

    @Autowired
    private CommentService commentService;
    
    @Autowired
    private AdminRepository adminRepository;

    // Get all comments
    @GetMapping
    public List<CommentEntity> getAllComments() {
        try {
            return commentService.getAllComments();
        } catch (Exception e) {
            System.err.println("Error fetching all comments: " + e.getMessage());
            throw e;
        }
    }

    // Get comments for a specific post
    @GetMapping("/{postId}")
    public List<CommentEntity> getCommentsByPost(@PathVariable int postId) {
        try {
            System.out.println("Fetching comments for post ID: " + postId);
            List<CommentEntity> comments = commentService.getCommentsByPostId(postId);
            System.out.println("Found " + comments.size() + " comments");
            return comments;
        } catch (Exception e) {
            System.err.println("Error fetching comments for post " + postId + ": " + e.getMessage());
            throw e;
        }
    }

    // Add a new comment
    @PostMapping("/add")
    public CommentEntity addComment(@RequestBody CommentEntity comment) {
        try {
            System.out.println("Adding new comment: " + comment);
            
            // Set default values
            comment.setTimestamp(LocalDateTime.now());
            comment.setDeleted(false);
            comment.setVisible(true);

            // Handle different user types and set their details
            if (comment.getAdminId() != null) {
                AdminEntity admin = adminRepository.findById(comment.getAdminId())
                    .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + comment.getAdminId()));
                comment.setFullName(admin.getFullName());  // Changed to match your entity
                comment.setIdNumber(admin.getIdNumber());  // Changed to match your entity
                System.out.println("Set admin details - Name: " + comment.getFullName() + ", ID: " + comment.getIdNumber());
            }

            CommentEntity savedComment = commentService.addComment(comment);
            System.out.println("Comment saved successfully with ID: " + savedComment.getCommentId());
            return savedComment;
        } catch (Exception e) {
            System.err.println("Error adding comment: " + e.getMessage());
            throw e;
        }
    }

    // Delete a comment (soft delete)
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable int commentId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer adminId,
            @RequestParam(required = false) Integer superUserId) {
        try {
            System.out.println("Attempting to delete comment ID: " + commentId);
            boolean deleted = commentService.softDeleteComment(commentId, userId, adminId, superUserId);
            
            if (deleted) {
                System.out.println("Comment " + commentId + " deleted successfully");
                return ResponseEntity.ok().build();
            } else {
                System.out.println("Failed to delete comment " + commentId + " - Permission denied");
                return ResponseEntity.badRequest()
                    .body("Unable to delete comment. You may not have permission.");
            }
        } catch (Exception e) {
            System.err.println("Error deleting comment " + commentId + ": " + e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{commentId}/superuser/{superuserId}")
public ResponseEntity<?> deleteCommentBySuperuser(
    @PathVariable int commentId,
    @PathVariable Integer superuserId
) {
    boolean deleted = commentService.softDeleteComment(commentId, null, null, superuserId);
    if (deleted) {
        return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().body("Failed to delete comment");
}

    // Update comment visibility
    @PutMapping("/{commentId}/visibility")
    public ResponseEntity<?> updateVisibility(
            @PathVariable int commentId,
            @RequestBody Map<String, Boolean> visibilityMap) {
        try {
            System.out.println("Updating visibility for comment ID: " + commentId);
            Boolean visible = visibilityMap.get("visible");
            
            if (visible == null) {
                return ResponseEntity.badRequest().body("Visibility value not provided");
            }

            boolean updated = commentService.updateCommentVisibility(commentId, visible);
            
            if (updated) {
                System.out.println("Visibility updated successfully for comment " + commentId);
                return ResponseEntity.ok().build();
            } else {
                System.out.println("Failed to update visibility for comment " + commentId);
                return ResponseEntity.badRequest().body("Failed to update visibility.");
            }
        } catch (Exception e) {
            System.err.println("Error updating comment visibility: " + e.getMessage());
            throw e;
        }
    }

    // Removed getCommentById endpoint since it's not implemented in the service
}