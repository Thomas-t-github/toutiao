package com.toutiao.service;

import com.toutiao.dao.MessageDAO;
import com.toutiao.model.Message;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    MessageDAO messageDAO;

    public int addMessage(Message message){
        return messageDAO.addMessage(message);
    }


    public List<Message> getConversationDetail(String conversationId,int offset,int limit){
        return messageDAO.getConversationDetail(conversationId,offset,limit);
    }

    public List<Message> getConversationList(int userId,int offset,int limit){
        return messageDAO.getConversationList(userId,offset,limit);
    }

    public int getConversationCount(int userId,String conversationId){
        return messageDAO.getConversationCount(userId,conversationId);
    }

    public int getConversationUnReadCount(int userId, String conversationId){
        return messageDAO.getConversationUnReadCount(userId,conversationId);
    }

    public int updateHasRead(int id){
        return messageDAO.updateHasRead(id);
    }

    public int updateAllHasRead(String conversationId,int userId){
        return messageDAO.updateAllHasRead(conversationId,userId);
    }

}
