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

    public List<CommentEntity> getCommentsByPostId(int postId) {
        return commentRepository.findByPostIdAndIsDeletedFalse(postId);
    }

    public CommentEntity addComment(CommentEntity comment) {
        comment.setTimestamp(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public boolean softDeleteComment(int commentId, int adminId) {
        Optional<CommentEntity> commentOpt = commentRepository.findById(commentId);
        
        if (commentOpt.isPresent()) {
            CommentEntity comment = commentOpt.get();
            
            Optional<PostEntity> postOpt = postRepository.findById(comment.getPostId());
            if (postOpt.isPresent()) {
                PostEntity post = postOpt.get();
                
                // Check if the admin is the comment owner or the post owner
                if (comment.getAdminId() == adminId || post.getAdminId() == adminId) {
                    comment.setDeleted(true);
                    commentRepository.save(comment);
                    return true;
                }
            }
        }
        
        return false;
    }
}
