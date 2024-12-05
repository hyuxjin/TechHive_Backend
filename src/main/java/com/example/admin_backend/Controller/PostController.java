package com.example.admin_backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.admin_backend.Entity.PostEntity;
import com.example.admin_backend.Entity.CommentEntity;
import com.example.admin_backend.Service.PostService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {

    @Autowired
    private PostService postService;

    // Get all posts
    @GetMapping
    public ResponseEntity<List<PostEntity>> getAllPosts() {
        try {
            List<PostEntity> posts = postService.getAllPosts();
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get visible posts only
    @GetMapping("/visible")
    public ResponseEntity<List<PostEntity>> getVisiblePosts() {
        try {
            List<PostEntity> posts = postService.getAllVisiblePosts();
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get post by ID
    @GetMapping("/{postId}")
    public ResponseEntity<PostEntity> getPostById(@PathVariable int postId) {
        try {
            return postService.getPostById(postId)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create new post
    @PostMapping("/add")
    public ResponseEntity<PostEntity> addPost(@RequestBody PostEntity post) {
        try {
            // Validation checks
            if (post.getUserRole() == null || post.getUserRole().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (post.getContent() == null && post.getImage() == null) {
                return ResponseEntity.badRequest().build();
            }

            PostEntity newPost = postService.createPost(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(newPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
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
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
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
            return ResponseEntity.notFound().build();
        }
    }

    // Toggle like on post
    @PostMapping("/{postId}/like")
    public ResponseEntity<PostEntity> toggleLike(
            @PathVariable int postId,
            @RequestParam int userId,
            @RequestParam String userRole) {
        try {
            // Validation
            if (userRole == null || userRole.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            PostEntity updatedPost = postService.toggleLike(postId, userId, userRole.toUpperCase());
            return ResponseEntity.ok(updatedPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Toggle dislike on post
    @PostMapping("/{postId}/dislike")
    public ResponseEntity<PostEntity> toggleDislike(
            @PathVariable int postId,
            @RequestParam int userId,
            @RequestParam String userRole) {
        try {
            // Validation
            if (userRole == null || userRole.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            PostEntity updatedPost = postService.toggleDislike(postId, userId, userRole.toUpperCase());
            return ResponseEntity.ok(updatedPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get comments for a post
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentEntity>> getCommentsByPostId(@PathVariable int postId) {
        try {
            List<CommentEntity> comments = postService.getCommentsByPostId(postId);
            return ResponseEntity.ok(comments);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Add comment to a post
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentEntity> addComment(
            @PathVariable int postId,
            @RequestBody CommentEntity comment) {
        try {
            // Validation
            if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            CommentEntity newComment = postService.addComment(comment, postId);
            return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Error handling for unsupported operations
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<?> handleUnsupportedOperation(UnsupportedOperationException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    // Generic error handling
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}