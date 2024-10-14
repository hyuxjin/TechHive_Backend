package com.example.admin_backend.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.admin_backend.Entity.CommentEntity;
import com.example.admin_backend.Entity.PostEntity;
import com.example.admin_backend.Entity.ProfileEntity;
import com.example.admin_backend.Entity.AdminEntity;
import com.example.admin_backend.Repository.CommentRepository;
import com.example.admin_backend.Repository.PostRepository;
import com.example.admin_backend.Repository.ProfileRepository;
import com.example.admin_backend.Repository.AdminRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ProfileRepository profileRepository;

    // Fetch all posts that are not deleted and visible
    public List<PostEntity> getAllPosts() {
        return postRepository.findByIsDeletedFalseAndIsVisibleTrue();
    }

    // Fetch Admin by admin name
    public AdminEntity getAdminByAdminname(String adminname) {
        return adminRepository.findByAdminname(adminname);
    }

    // Fetch post by ID
   public Optional<PostEntity> getPostById(int postId) {
    return postRepository.findByPostIdAndIsDeletedFalse(postId);
}




    // Update visibility of a post (visible/hidden)
    public PostEntity updateVisibility(int postId, boolean newVisibility) {
    PostEntity post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
    post.setVisible(newVisibility);  // Update the visibility
    return postRepository.save(post);
}


    // Create a new post
    public PostEntity createPost(PostEntity post) {
        System.out.println("Creating post: " + post);
        AdminEntity admin = adminRepository.findById(post.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Fetch the latest profile details
        ProfileEntity profile = profileRepository.findByAdmin(admin);
        if (profile != null) {
            post.setFullName(admin.getFullName());
            post.setProfile(profile); // Set the ProfileEntity object
        }

        post.setTimestamp(LocalDateTime.now());
        post.setDeleted(false); // Ensure the post is marked as not deleted
        post.setVisible(true);  // Default is visible when created
        PostEntity savedPost = postRepository.save(post);
        System.out.println("Saved post: " + savedPost);
        return savedPost;
    }

    // Update existing post
    public PostEntity updatePost(int postId, PostEntity postDetails) {
        PostEntity post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Fetch the latest profile details for the admin
        AdminEntity admin = adminRepository.findById(postDetails.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        ProfileEntity profile = profileRepository.findByAdmin(admin);
        if (profile != null) {
            post.setFullName(admin.getFullName());
            post.setProfile(profile); // Set the ProfileEntity object
        }

        post.setContent(postDetails.getContent());
        post.setTimestamp(LocalDateTime.now());
        post.setAdminId(postDetails.getAdminId());
        post.setVisible(postDetails.isVisible());
        post.setLikes(postDetails.getLikes());
        post.setDislikes(postDetails.getDislikes());

        return postRepository.save(post);
    }

    // Soft delete a post (mark as deleted but do not remove from database)
    @Transactional
    public void softDeletePost(int postId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setDeleted(true);  // Mark the post as deleted
        postRepository.save(post);
    }

    // Toggle like for a post
    @Transactional
    public PostEntity toggleLike(int postId, int adminId) {
        PostEntity post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (post.getLikedBy().contains(adminId)) {
            post.getLikedBy().remove(adminId);
            post.setLikes(post.getLikes() - 1);
        } else {
            post.getLikedBy().add(adminId);
            post.setLikes(post.getLikes() + 1);

            // If the admin has disliked the post before, remove the dislike
            if (post.getDislikedBy().contains(adminId)) {
                post.getDislikedBy().remove(adminId);
                post.setDislikes(post.getDislikes() - 1);
            }
        }

        return postRepository.save(post);
    }

    // Toggle dislike for a post
    @Transactional
    public PostEntity toggleDislike(int postId, int adminId) {
        PostEntity post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (post.getDislikedBy().contains(adminId)) {
            post.getDislikedBy().remove(adminId);
            post.setDislikes(post.getDislikes() - 1);
        } else {
            post.getDislikedBy().add(adminId);
            post.setDislikes(post.getDislikes() + 1);

            // If the admin has liked the post before, remove the like
            if (post.getLikedBy().contains(adminId)) {
                post.getLikedBy().remove(adminId);
                post.setLikes(post.getLikes() - 1);
            }
        }

        return postRepository.save(post);
    }

    // Fetch comments for a given post
    public List<CommentEntity> getCommentsByPostId(int postId) {
        return commentRepository.findByPostIdAndIsDeletedFalse(postId);
    }

    // Add a comment to a post
    public CommentEntity addComment(CommentEntity comment, int postId) {
        PostEntity post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        comment.setPostId(postId);
        return commentRepository.save(comment);
    }
}
