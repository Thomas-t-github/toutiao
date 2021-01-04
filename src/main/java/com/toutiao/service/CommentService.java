package com.toutiao.service;

import com.toutiao.dao.CommentDAO;
import com.toutiao.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentDAO commentDAO;

    public int addComment(Comment comment){
        return commentDAO.addComment(comment);
    }

    public List<Comment> selectByEntity(int entityId, int entityType){
        return commentDAO.selectByEntity(entityId,entityType);
    }

    public int getCommentCount(int entityId, int entityType){
        return commentDAO.getCommentCount(entityId,entityType);
    }

}
