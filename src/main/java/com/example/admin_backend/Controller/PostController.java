package com.example.admin_backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import com.example.admin_backend.Entity.PostEntity;
import com.example.admin_backend.Entity.CommentEntity;
import com.example.admin_backend.Service.PostService;
import org.springframework.http.MediaType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Get all posts
    @GetMapping
    public ResponseEntity<List<PostEntity>> getAllPosts() {
        try {
            List<PostEntity> posts = postService.getAllPosts();
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get visible posts only with report status
    @GetMapping("/visible")
public ResponseEntity<List<PostEntity>> getVisiblePosts() {
    try {
        System.out.println("==== START: getVisiblePosts ====");
        String query = "SELECT p.*, r.status as report_status " +
                "FROM tblpost p " +
                "LEFT JOIN tblreport r ON p.reportid = r.reportId " +
                "WHERE p.is_visible = true AND p.is_deleted = false " + // Added is_deleted check
                "ORDER BY p.timestamp DESC";

            List<PostEntity> posts = jdbcTemplate.query(query, (rs, rowNum) -> {
                PostEntity post = new PostEntity();
                post.setPostId(rs.getInt("post_id"));
                post.setContent(rs.getString("content"));
                post.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                post.setAdminId(rs.getInt("admin_id"));
                post.setSuperUserId(rs.getInt("superuser_id"));
                post.setImage(rs.getString("image"));
                post.setUserRole(rs.getString("user_role"));
                post.setFullname(rs.getString("fullname"));
                post.setIdnumber(rs.getString("idnumber"));
                post.setLikes(rs.getInt("likes"));
                post.setDislikes(rs.getInt("dislikes"));
                post.setIsVisible(rs.getBoolean("is_visible"));
                post.setIsSubmittedReport(rs.getBoolean("is_submitted_report"));
                post.setReportId(rs.getInt("reportid"));

                if (post.getIsSubmittedReport()) {
                    try {
                        String reportStatus = rs.getString("report_status");
                        if (reportStatus != null) {
                            System.out.println("Found report status for post " + post.getPostId() + ": " + reportStatus);
                            post.setStatus(reportStatus);
                        } else {
                            System.out.println("No report status found for post " + post.getPostId() + ", defaulting to PENDING");
                            post.setStatus("PENDING");
                        }
                    } catch (SQLException e) {
                        System.out.println("Error getting report status for post " + post.getPostId() + ", defaulting to PENDING");
                        post.setStatus("PENDING");
                    }
                }
                return post;
            });

            posts.forEach(post -> {
                if (post.getIsSubmittedReport()) {
                    System.out.println("Report post ID: " + post.getPostId() + ", Status: " + post.getStatus());
                }
            });
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            System.err.println("==== ERROR in PostController.getVisiblePosts ====");
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Handle like on post
    @PostMapping("/{postId}/like")
    public ResponseEntity<PostEntity> handleLike(
            @PathVariable Integer postId,
            @RequestParam Integer userId,
            @RequestParam String userRole) {
        try {
            if (userRole == null || userRole.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            PostEntity updatedPost = postService.handleLike(postId, userId, userRole.toUpperCase());
            return ResponseEntity.ok(updatedPost);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Handle dislike on post
    @PostMapping("/{postId}/dislike")
    public ResponseEntity<PostEntity> handleDislike(
            @PathVariable Integer postId,
            @RequestParam Integer userId,
            @RequestParam String userRole) {
        try {
            if (userRole == null || userRole.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            PostEntity updatedPost = postService.handleDislike(postId, userId, userRole.toUpperCase());
            return ResponseEntity.ok(updatedPost);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create new post
    @PostMapping("/add")
    public ResponseEntity<PostEntity> addPost(@RequestBody PostEntity post) {
        try {
            if (post.getUserRole() == null || post.getUserRole().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (post.getContent() == null && post.getImage() == null) {
                return ResponseEntity.badRequest().build();
            }
            PostEntity newPost = postService.createPost(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(newPost);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update existing post
    @PutMapping("/{postId}")
    public ResponseEntity<PostEntity> updatePost(
            @PathVariable int postId,
            @RequestBody PostEntity postDetails) {
        try {
            PostEntity updatedPost = postService.updatePost(postId, postDetails);
            return ResponseEntity.ok(updatedPost);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    // Update post visibility
    @PutMapping("/{postId}/visibility")
    public ResponseEntity<PostEntity> updateVisibility(
            @PathVariable int postId,
            @RequestBody Map<String, Boolean> visibility) {
        try {
            Boolean isVisible = visibility.get("visible");
            if (isVisible == null) {
                return ResponseEntity.badRequest().build();
            }
            PostEntity updatedPost = postService.updateVisibility(postId, isVisible);
            return ResponseEntity.ok(updatedPost);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    // Soft delete post
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable int postId) {
        try {
            postService.softDeletePost(postId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    // Get post status
    @GetMapping("/{postId}/status")
    public ResponseEntity<?> getPostStatus(@PathVariable int postId) {
        try {
            String query = "SELECT p.is_submitted_report, r.status " +
                    "FROM tblpost p " +
                    "LEFT JOIN tblreport r ON p.report_id = r.report_id " +
                    "WHERE p.post_id = ?";
            Map<String, Object> result = jdbcTemplate.queryForMap(query, postId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error fetching status for post " + postId);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch post status"));
        }
    }

    // Get comments for a post
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentEntity>> getCommentsByPostId(@PathVariable int postId) {
        try {
            List<CommentEntity> comments = postService.getCommentsByPostId(postId);
            return ResponseEntity.ok(comments);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    // Add this method to your PostController class
@GetMapping("/{postId}")
public ResponseEntity<PostEntity> getPostById(@PathVariable int postId) {
    try {
        PostEntity post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
    } catch (NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    } catch (Exception e) {
        System.err.println("Error retrieving post: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

    // Add comment to a post
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentEntity> addComment(
            @PathVariable int postId,
            @RequestBody CommentEntity comment) {
        try {
            if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            CommentEntity newComment = postService.addComment(comment, postId);
            return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    // Get all report posts
    @GetMapping("/reports")
    public ResponseEntity<List<PostEntity>> getAllReportPosts() {
        try {
            List<PostEntity> reportPosts = postService.getAllReportPosts();
            return ResponseEntity.ok(reportPosts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get report posts by status
    @GetMapping("/reports/status/{status}")
    public ResponseEntity<List<PostEntity>> getReportPostsByStatus(@PathVariable String status) {
        try {
            List<PostEntity> reportPosts = postService.getReportPostsByStatus(status);
            return ResponseEntity.ok(reportPosts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}