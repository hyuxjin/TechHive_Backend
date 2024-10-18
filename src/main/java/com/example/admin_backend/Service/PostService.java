package com.example.admin_backend.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.admin_backend.Entity.CommentEntity;
import com.example.admin_backend.Entity.PostEntity;
import com.example.admin_backend.Entity.ProfileEntity;
import com.example.admin_backend.Entity.AdminEntity;
import com.example.admin_backend.Entity.SuperUserEntity;
import com.example.admin_backend.Repository.CommentRepository;
import com.example.admin_backend.Repository.PostRepository;
import com.example.admin_backend.Repository.ProfileRepository;
import com.example.admin_backend.Repository.AdminRepository;
import com.example.admin_backend.Repository.SuperUserRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SuperUserRepository superUserRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ProfileRepository profileRepository;

    // Fetch all posts that are not deleted and visible
    public List<PostEntity> getAllPosts() {
        return postRepository.findByIsDeletedFalse();
    }

    // Fetch post by ID
    public Optional<PostEntity> getPostById(int postId) {
        return postRepository.findByPostIdAndIsDeletedFalse(postId);
    }

    // Fetch Admin by admin name
    public AdminEntity getAdminByAdminname(String adminname) {
        return adminRepository.findByAdminname(adminname);
    }

    // Create a new post
    public PostEntity createPost(PostEntity post) {
        if (post.getAdminId() != null) {
            // Handle admin post
            AdminEntity admin = adminRepository.findById(post.getAdminId())
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

            ProfileEntity profile = profileRepository.findByAdmin(admin);
            if (profile != null) {
                post.setFullName(admin.getFullName());
            }
            post.setSuperUserId(null); // Ensure superUserId is null for admin posts
        } else if (post.getSuperUserId() != null) {
            // Superuser is posting
            SuperUserEntity superuser = superUserRepository.findById(post.getSuperUserId())
                    .orElseThrow(() -> new RuntimeException("Superuser not found"));
            post.setFullName(superuser.getFullName());
            post.setAdminId(null); // Ensure adminId is null for superuser posts
        } else {
            throw new RuntimeException("Either Admin or Superuser must be set");
        }

        post.setTimestamp(LocalDateTime.now());
        post.setDeleted(false); // Ensure the post is marked as not deleted
        post.setVisible(true);  // Default is visible when created
        return postRepository.save(post);
    }

    // Update existing post
    public PostEntity updatePost(int postId, PostEntity postDetails) {
        PostEntity post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (postDetails.getAdminId() != null) {
            AdminEntity admin = adminRepository.findById(postDetails.getAdminId())
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            post.setFullName(admin.getFullName());
            post.setSuperUserId(null); // Clear superUserId if an admin is updating the post
        } else if (postDetails.getSuperUserId() != null) {
            SuperUserEntity superuser = superUserRepository.findById(postDetails.getSuperUserId())
                    .orElseThrow(() -> new RuntimeException("Superuser not found"));
            post.setFullName(superuser.getFullName());
            post.setAdminId(null); // Clear adminId if a superuser is updating the post
        }

        post.setContent(postDetails.getContent());
        post.setTimestamp(LocalDateTime.now());
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
public PostEntity toggleLike(int postId, int userId, boolean isAdmin) {
    PostEntity post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
    
    Set<Integer> likedBy = post.getLikedBy();
    Set<Integer> dislikedBy = post.getDislikedBy();

    // If the user has already liked the post, remove the like
    if (likedBy.contains(userId)) {
        likedBy.remove(userId);
        post.setLikes(post.getLikes() - 1);
    } else {
        // If the user has disliked the post, remove the dislike first
        if (dislikedBy.contains(userId)) {
            dislikedBy.remove(userId);
            post.setDislikes(post.getDislikes() - 1);
        }
        // Add like
        likedBy.add(userId);
        post.setLikes(post.getLikes() + 1);
    }
    
    post.setLikedBy(likedBy);
    post.setDislikedBy(dislikedBy);

    return postRepository.save(post);
}

public PostEntity toggleDislike(int postId, int userId, boolean isAdmin) {
    PostEntity post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
    
    Set<Integer> likedBy = post.getLikedBy();
    Set<Integer> dislikedBy = post.getDislikedBy();

    // If the user has already disliked the post, remove the dislike
    if (dislikedBy.contains(userId)) {
        dislikedBy.remove(userId);
        post.setDislikes(post.getDislikes() - 1);
    } else {
        // If the user has liked the post, remove the like first
        if (likedBy.contains(userId)) {
            likedBy.remove(userId);
            post.setLikes(post.getLikes() - 1);
        }
        // Add dislike
        dislikedBy.add(userId);
        post.setDislikes(post.getDislikes() + 1);
    }

    post.setLikedBy(likedBy);
    post.setDislikedBy(dislikedBy);

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

    public PostEntity updateVisibility(int postId, boolean newVisibility) {
    PostEntity post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));  // Ensure the post exists
    post.setVisible(newVisibility);
    return postRepository.save(post);  // Save the updated post visibility
}


}
