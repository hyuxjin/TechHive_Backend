package com.example.admin_backend.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.admin_backend.Entity.*;
import com.example.admin_backend.Repository.*;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SuperUserRepository superUserRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    @SuppressWarnings("unused")
    private LeaderboardService leaderboardService;

    // Get all posts
    public List<PostEntity> getAllPosts() {
        return postRepository.findByIsDeletedFalseOrderByTimestampDesc();
    }

    // Get only visible posts
    public List<PostEntity> getAllVisiblePosts() {
<<<<<<< Updated upstream
        return postRepository.findByIsDeletedFalseAndVisibleTrue();
=======
        try {
            System.out.println("PostService: Starting to fetch visible posts");
            List<PostEntity> posts = postRepository.findByIsDeletedFalseAndIsVisibleTrue();
            System.out.println("PostService: SQL query executed successfully");
            System.out.println("PostService: Number of posts found: " + posts.size());
            posts.forEach(post -> System.out.println("Post ID: " + post.getPostId() + ", isVisible: " + post.isVisible()));
            return posts;
        } catch (Exception e) {
            System.err.println("PostService Error in getAllVisiblePosts: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error fetching visible posts", e);
        }
>>>>>>> Stashed changes
    }

    // Get post by ID
    public Optional<PostEntity> getPostById(int postId) {
        return postRepository.findByPostIdAndIsDeletedFalse(postId);
    }

    // Create new post
    @Transactional
    public PostEntity createPost(PostEntity post) {
        if (post.getContent() == null && post.getImage() == null) {
            throw new IllegalArgumentException("Post must have either content or an image");
        }

        post.setTimestamp(LocalDateTime.now());
        post.setDeleted(false);
        post.setLikedBy(new HashSet<>());
        post.setDislikedBy(new HashSet<>());
        
        switch (post.getUserRole().toUpperCase()) {
            case "USER":
                return createUserPost(post);
            case "ADMIN":
                return createAdminPost(post);
            case "SUPERUSER":
                return createSuperUserPost(post);
            default:
                throw new IllegalArgumentException("Invalid user role");
        }
    }

    private PostEntity createUserPost(PostEntity post) {
        if (post.getUserId() == 0) {
            throw new IllegalArgumentException("User ID must be provided");
        }

        UserEntity user = userRepository.findById(post.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));

       // Ensure user points are not null and handle it
       user.setPoints(Math.max(user.getPoints(), 0)); // Reset points to default if less than 0
    
       post.setFullName(user.getFullName());
       post.setProfile(profileRepository.findByUser(user)); // Fetch profile
       post.setTimestamp(LocalDateTime.now());
       post.setIsSubmittedReport(post.getIsSubmittedReport()); 
       post.setDeleted(false);
        
        return postRepository.save(post);
    }

    private PostEntity createAdminPost(PostEntity post) {
        AdminEntity admin = adminRepository.findById(post.getAdminId())
            .orElseThrow(() -> new RuntimeException("Admin not found"));

        post.setFullName(admin.getFullName());
        post.setProfile(profileRepository.findByAdmin(admin));
        post.setVerified(true);
        
        return postRepository.save(post);
    }

    private PostEntity createSuperUserPost(PostEntity post) {
        SuperUserEntity superuser = superUserRepository.findById(post.getSuperUserId())
            .orElseThrow(() -> new RuntimeException("Superuser not found"));

        post.setFullName(superuser.getFullName());
        post.setVerified(true);
        
        return postRepository.save(post);
    }

    // Update post
    @Transactional
    public PostEntity updatePost(int postId, PostEntity postDetails) {
        PostEntity existingPost = postRepository.findByPostIdAndIsDeletedFalse(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        // Update basic fields
        if (postDetails.getContent() != null) {
            existingPost.setContent(postDetails.getContent());
        }
        if (postDetails.getImage() != null) {
            existingPost.setImage(postDetails.getImage());
        }

        existingPost.setLastModifiedAt(LocalDateTime.now());

        // Update role-specific fields
        switch (existingPost.getUserRole().toUpperCase()) {
            case "USER":
                updateUserPost(existingPost, postDetails);
                break;
            case "ADMIN":
                updateAdminPost(existingPost, postDetails);
                break;
            case "SUPERUSER":
                updateSuperUserPost(existingPost, postDetails);
                break;
        }

        return postRepository.save(existingPost);
    }

    private void updateUserPost(PostEntity existingPost, PostEntity details) {
        existingPost.setVisible(details.isVisible());
    }

    private void updateAdminPost(PostEntity existingPost, PostEntity details) {
        existingPost.setVerified(true);
        existingPost.setVisible(details.isVisible());
        if (details.getAdminNotes() != null) {
            existingPost.setAdminNotes(details.getAdminNotes());
        }
    }

    private void updateSuperUserPost(PostEntity existingPost, PostEntity details) {
        existingPost.setVerified(true);
        existingPost.setVisible(details.isVisible());
        if (details.getAdminNotes() != null) {
            existingPost.setAdminNotes(details.getAdminNotes());
        }
    }

<<<<<<< Updated upstream
    // Like/Dislike functionality
    @Transactional
    public PostEntity toggleLike(int postId, int userId, String userRole) {
        PostEntity post = postRepository.findByPostIdAndIsDeletedFalse(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean isAdminOrSuperuser = "ADMIN".equalsIgnoreCase(userRole) || 
                                   "SUPERUSER".equalsIgnoreCase(userRole);

        if (post.getLikedBy() == null) {
            post.setLikedBy(new HashSet<>());
        }
        if (post.getDislikedBy() == null) {
            post.setDislikedBy(new HashSet<>());
        }

        boolean isOwnPost = userId == post.getUserId();
        boolean hasLiked = post.getLikedBy().contains(userId);
        boolean hasDisliked = post.getDislikedBy().contains(userId);

        if (hasLiked) {
            // Remove like
            post.getLikedBy().remove(userId);
            post.setLikes(post.getLikes() - 1);
            if (!isOwnPost && !isAdminOrSuperuser) {
                updateUserPoints(post.getUserId(), -1);
            }
        } else {
            // Handle dislike if exists
            if (hasDisliked) {
                post.getDislikedBy().remove(userId);
                post.setDislikes(post.getDislikes() - 1);
                if (!isOwnPost && !isAdminOrSuperuser) {
                    updateUserPoints(post.getUserId(), 1);
                }
            }
            // Add like
            post.getLikedBy().add(userId);
            post.setLikes(post.getLikes() + 1);
            if (!isOwnPost && !isAdminOrSuperuser) {
                updateUserPoints(post.getUserId(), 1);
            }
        }

        return postRepository.save(post);
    }

    @Transactional
    public PostEntity toggleDislike(int postId, int userId, String userRole) {
        PostEntity post = postRepository.findByPostIdAndIsDeletedFalse(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean isAdminOrSuperuser = "ADMIN".equalsIgnoreCase(userRole) || 
                                   "SUPERUSER".equalsIgnoreCase(userRole);

        if (post.getLikedBy() == null) {
            post.setLikedBy(new HashSet<>());
        }
        if (post.getDislikedBy() == null) {
            post.setDislikedBy(new HashSet<>());
        }

        boolean isOwnPost = userId == post.getUserId();
        boolean hasLiked = post.getLikedBy().contains(userId);
        boolean hasDisliked = post.getDislikedBy().contains(userId);

        if (hasDisliked) {
            // Remove dislike
            post.getDislikedBy().remove(userId);
            post.setDislikes(post.getDislikes() - 1);
            if (!isOwnPost && !isAdminOrSuperuser) {
                updateUserPoints(post.getUserId(), 1);
            }
        } else {
            // Handle like if exists
            if (hasLiked) {
                post.getLikedBy().remove(userId);
                post.setLikes(post.getLikes() - 1);
                if (!isOwnPost && !isAdminOrSuperuser) {
                    updateUserPoints(post.getUserId(), -1);
                }
            }
            // Add dislike
            post.getDislikedBy().add(userId);
            post.setDislikes(post.getDislikes() + 1);
            if (!isOwnPost && !isAdminOrSuperuser) {
                updateUserPoints(post.getUserId(), -1);
            }

            // Check for auto-deletion threshold for user posts
            if ("USER".equalsIgnoreCase(post.getUserRole()) && post.getDislikes() >= 50) {
                softDeletePost(postId);
                return post;
=======
    // Post visibility
    public PostEntity updateVisibility(int postId, boolean newVisibility) {
        try {
            System.out.println("Updating visibility for post ID: " + postId);
            PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
            post.setVisible(newVisibility);
            post.setLastModifiedAt(LocalDateTime.now());
            PostEntity updatedPost = postRepository.save(post);
            System.out.println("Visibility updated successfully");
            return updatedPost;
        } catch (Exception e) {
            System.err.println("Error updating visibility: " + e.getMessage());
            throw e;
        }
    }

    // Soft delete
    @Transactional
    public void softDeletePost(int postId) {
        try {
            System.out.println("Soft deleting post ID: " + postId);
            PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
            post.setDeleted(true);
            post.setLastModifiedAt(LocalDateTime.now());
            postRepository.save(post);
            System.out.println("Post soft deleted successfully");
        } catch (Exception e) {
            System.err.println("Error soft deleting post: " + e.getMessage());
            throw e;
        }
    }

    // Comments
    public List<CommentEntity> getCommentsByPostId(int postId) {
        try {
            System.out.println("Fetching comments for post ID: " + postId);
            List<CommentEntity> comments = commentRepository.findByPostIdAndIsDeletedFalse(postId);
            System.out.println("Found " + comments.size() + " comments");
            return comments;
        } catch (Exception e) {
            System.err.println("Error fetching comments: " + e.getMessage());
            throw e;
        }
    }

    public CommentEntity addComment(CommentEntity comment, int postId) {
        try {
            System.out.println("Adding comment to post ID: " + postId);
            comment.setPostId(postId);
            CommentEntity savedComment = commentRepository.save(comment);
            System.out.println("Comment added successfully");
            return savedComment;
        } catch (Exception e) {
            System.err.println("Error adding comment: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public PostEntity handleLike(Integer postId, Integer userId, String userRole) {
        PostEntity post = postRepository.findById(postId)
            .orElseThrow(() -> new NoSuchElementException("Post not found"));

        Set<Integer> likedBy = post.getLikedBy();
        if (likedBy == null) {
            likedBy = new HashSet<>();
            post.setLikedBy(likedBy);
        }

        // Check if user has already liked the post - if so, remove the like
        if (likedBy.contains(userId)) {
            // Remove like
            likedBy.remove(userId);
            post.setLikes(post.getLikes() - 1);

            // Remove points if it's a user's post
            if (post.getUserId() != null) {
                if ("ADMIN".equals(userRole)) {
                    leaderboardService.subtractPoints(post.getUserId(), 3);
                    UserEntity user = userRepository.findById(post.getUserId())
                        .orElseThrow(() -> new NoSuchElementException("User not found"));
                    user.subtractPoints(3);
                    userRepository.save(user);
                } else if ("SUPERUSER".equals(userRole)) {
                    leaderboardService.subtractPoints(post.getUserId(), 5);
                    UserEntity user = userRepository.findById(post.getUserId())
                        .orElseThrow(() -> new NoSuchElementException("User not found"));
                    user.subtractPoints(5);
                    userRepository.save(user);
                }
            }
        } else {
            // Add like
            likedBy.add(userId);
            post.setLikes(post.getLikes() + 1);

            // Remove dislike if exists
            Set<Integer> dislikedBy = post.getDislikedBy();
            if (dislikedBy != null && dislikedBy.contains(userId)) {
                dislikedBy.remove(userId);
                post.setDislikes(post.getDislikes() - 1);

                // Add back points that were removed by dislike
                if (post.getUserId() != null) {
                    if ("ADMIN".equals(userRole)) {
                        leaderboardService.addPoints(post.getUserId(), 3);
                        UserEntity user = userRepository.findById(post.getUserId())
                            .orElseThrow(() -> new NoSuchElementException("User not found"));
                        user.addPoints(3);
                        userRepository.save(user);
                    } else if ("SUPERUSER".equals(userRole)) {
                        leaderboardService.addPoints(post.getUserId(), 5);
                        UserEntity user = userRepository.findById(post.getUserId())
                            .orElseThrow(() -> new NoSuchElementException("User not found"));
                        user.addPoints(5);
                        userRepository.save(user);
                    }
                }
            }

            // Add points for new like if it's a user's post
            if (post.getUserId() != null) {
                if ("ADMIN".equals(userRole)) {
                    leaderboardService.addPoints(post.getUserId(), 3);
                    UserEntity user = userRepository.findById(post.getUserId())
                        .orElseThrow(() -> new NoSuchElementException("User not found"));
                    user.addPoints(3);
                    userRepository.save(user);
                } else if ("SUPERUSER".equals(userRole)) {
                    leaderboardService.addPoints(post.getUserId(), 5);
                    UserEntity user = userRepository.findById(post.getUserId())
                        .orElseThrow(() -> new NoSuchElementException("User not found"));
                    user.addPoints(5);
                    userRepository.save(user);
                }
            }
        }

        return postRepository.save(post);
    }

    @Transactional
    public PostEntity handleDislike(Integer postId, Integer userId, String userRole) {
        PostEntity post = postRepository.findById(postId)
            .orElseThrow(() -> new NoSuchElementException("Post not found"));

        Set<Integer> dislikedBy = post.getDislikedBy();
        if (dislikedBy == null) {
            dislikedBy = new HashSet<>();
            post.setDislikedBy(dislikedBy);
        }

        // Check if user has already disliked the post - if so, remove the dislike
        if (dislikedBy.contains(userId)) {
            // Remove dislike
            dislikedBy.remove(userId);
            post.setDislikes(post.getDislikes() - 1);

            // Add back points if it's a user's post
            if (post.getUserId() != null) {
                if ("ADMIN".equals(userRole)) {
                    leaderboardService.addPoints(post.getUserId(), 3);
                    UserEntity user = userRepository.findById(post.getUserId())
                        .orElseThrow(() -> new NoSuchElementException("User not found"));
                    user.addPoints(3);
                    userRepository.save(user);
                } else if ("SUPERUSER".equals(userRole)) {
                    leaderboardService.addPoints(post.getUserId(), 5);
                    UserEntity user = userRepository.findById(post.getUserId())
                        .orElseThrow(() -> new NoSuchElementException("User not found"));
                    user.addPoints(5);
                    userRepository.save(user);
                }
            }
        } else {
            // Add dislike
            dislikedBy.add(userId);
            post.setDislikes(post.getDislikes() + 1);

            // Remove like if exists
            Set<Integer> likedBy = post.getLikedBy();
            if (likedBy != null && likedBy.contains(userId)) {
                likedBy.remove(userId);
                post.setLikes(post.getLikes() - 1);

                // Remove points from previous like
                if (post.getUserId() != null) {
                    if ("ADMIN".equals(userRole)) {
                        leaderboardService.subtractPoints(post.getUserId(), 3);
                        UserEntity user = userRepository.findById(post.getUserId())
                            .orElseThrow(() -> new NoSuchElementException("User not found"));
                        user.subtractPoints(3);
                        userRepository.save(user);
                    } else if ("SUPERUSER".equals(userRole)) {
                        leaderboardService.subtractPoints(post.getUserId(), 5);
                        UserEntity user = userRepository.findById(post.getUserId())
                            .orElseThrow(() -> new NoSuchElementException("User not found"));
                        user.subtractPoints(5);
                        userRepository.save(user);
                    }
                }
            }

            // Subtract points for new dislike if it's a user's post
            if (post.getUserId() != null) {
                if ("ADMIN".equals(userRole)) {
                    leaderboardService.subtractPoints(post.getUserId(), 3);
                    UserEntity user = userRepository.findById(post.getUserId())
                        .orElseThrow(() -> new NoSuchElementException("User not found"));
                    user.subtractPoints(3);
                    userRepository.save(user);
                } else if ("SUPERUSER".equals(userRole)) {
                    leaderboardService.subtractPoints(post.getUserId(), 5);
                    UserEntity user = userRepository.findById(post.getUserId())
                        .orElseThrow(() -> new NoSuchElementException("User not found"));
                    user.subtractPoints(5);
                    userRepository.save(user);
                }
>>>>>>> Stashed changes
            }
        }

        return postRepository.save(post);
    }

    // Post visibility
    public PostEntity updateVisibility(int postId, boolean newVisibility) {
        PostEntity post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setVisible(newVisibility);
        post.setLastModifiedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    // Soft delete
    @Transactional
    public void softDeletePost(int postId) {
        PostEntity post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setDeleted(true);
        post.setLastModifiedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    // Comments
    public List<CommentEntity> getCommentsByPostId(int postId) {
        return commentRepository.findByPostIdAndIsDeletedFalse(postId);
    }

    public CommentEntity addComment(CommentEntity comment, int postId) {
        comment.setPostId(postId);
        return commentRepository.save(comment);
    }
    
    // Helper methods
    private void updateUserPoints(int userId, int pointChange) {
        if ("USER".equalsIgnoreCase(userRepository.findById(userId)
                .map(UserEntity::getRole)  // Fixed: using getRole() instead of getUserRole()
                .orElse(null))) {
            UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            user.setPoints(Math.max(0, user.getPoints() + pointChange));
            userRepository.save(user);
        }
    }

    private boolean isValidUserRole(String userRole) {
        return userRole != null && 
               (userRole.equalsIgnoreCase("USER") || 
                userRole.equalsIgnoreCase("ADMIN") || 
                userRole.equalsIgnoreCase("SUPERUSER"));
    }
}