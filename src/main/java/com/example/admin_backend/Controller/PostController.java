package com.example.admin_backend.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;  // <-- Add this import
import org.springframework.http.ResponseEntity;  // Ensure this is also imported



import com.example.admin_backend.Entity.CommentEntity;
import com.example.admin_backend.Entity.PostEntity;
import com.example.admin_backend.Service.PostService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // Get all posts
    @GetMapping
    public List<PostEntity> getAllPosts() {
        return postService.getAllPosts();
    }

    // Get post by ID
    @GetMapping("/{postId}")
    public ResponseEntity<PostEntity> getPostById(@PathVariable int postId) {
        Optional<PostEntity> post = postService.getPostById(postId);
        return post.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a new post (either for superusers or admins)
    @PostMapping("/add")
    public ResponseEntity<PostEntity> addPost(@RequestBody PostEntity post) {
        System.out.println("Received post: " + post);
        System.out.println("Received image: " + (post.getImage() != null ? "image present" : "no image"));
        PostEntity newPost = postService.createPost(post);
        System.out.println("Created post: " + newPost);
        return ResponseEntity.ok(newPost);
    }

    // Update post visibility
 @PutMapping("/{post_id}/visibility")
public ResponseEntity<PostEntity> updatePostVisibility(@PathVariable("post_id") int postId, @RequestBody Map<String, Boolean> requestBody) {
    boolean newVisibility = requestBody.get("visible");  // Ensure this matches the request body field
    PostEntity updatedPost = postService.updateVisibility(postId, newVisibility);
    return ResponseEntity.ok(updatedPost);
}


    // Update a post
    @PutMapping("/{postId}")
    public ResponseEntity<PostEntity> updatePost(@PathVariable int postId, @RequestBody PostEntity postDetails) {
        try {
            PostEntity updatedPost = postService.updatePost(postId, postDetails);
            return ResponseEntity.ok(updatedPost);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete (soft delete) a post
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable int postId) {
        try {
            postService.softDeletePost(postId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

@PostMapping("/{postId}/like")  // Remove /posts
public ResponseEntity<PostEntity> likePost(@PathVariable int postId, @RequestParam int userId, @RequestParam boolean isAdmin) {
    PostEntity updatedPost = postService.toggleLike(postId, userId, isAdmin);
    return new ResponseEntity<>(updatedPost, HttpStatus.OK);
}

@PostMapping("/{postId}/dislike")  // Remove /posts
public ResponseEntity<PostEntity> dislikePost(@PathVariable int postId, @RequestParam int userId, @RequestParam boolean isAdmin) {
    PostEntity updatedPost = postService.toggleDislike(postId, userId, isAdmin);
    return new ResponseEntity<>(updatedPost, HttpStatus.OK);
}


    // Get comments for a post
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentEntity>> getCommentsByPostId(@PathVariable int postId) {
        List<CommentEntity> comments = postService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // Add a comment to a post
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentEntity> addComment(@PathVariable int postId, @RequestBody CommentEntity comment) {
        try {
            CommentEntity newComment = postService.addComment(comment, postId);
            return ResponseEntity.ok(newComment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
