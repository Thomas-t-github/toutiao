package com.toutiao.controller;

import com.toutiao.model.HostHolder;
import com.toutiao.model.Message;
import com.toutiao.model.User;
import com.toutiao.model.ViewObject;
import com.toutiao.service.MessageService;
import com.toutiao.service.UserService;
import com.toutiao.utils.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);


    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content){
        Message message = new Message();
        message.setContent(content);
        message.setCreatedDate(new Date());
        message.setFromId(fromId);
        message.setToId(toId);
        message.setHasRead(0);
        message.setConversationId(fromId > toId ? String.format("%d_%d",toId,fromId) : String.format("%d_%d",fromId,toId));
        int i = messageService.addMessage(message);
        if ( i > 0){
            return ToutiaoUtil.getJSONString(message.getId());
        }else {
            logger.error("添加消息异常");
            return null;
        }
    }

    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model, @RequestParam("conversationId") String conversationId) {

        try {
            List<Message> messages = messageService.getConversationDetail(conversationId, 0, 10);
            ArrayList<ViewObject> vos = new ArrayList<>();
            for (Message message : messages) {
                ViewObject vo = new ViewObject();
                vo.set("message",message);
                User user = userService.getUserById(message.getFromId());
                if(user == null){
                    continue;
                }
                vo.set("headUrl",user.getHeadUrl());
                vo.set("userName",user.getName());
                vos.add(vo);
            }
            messageService.updateAllHasRead(conversationId,hostHolder.getUser().getId());
            model.addAttribute("messages",vos);

        }catch (Exception e){
            logger.error("获取站内信详情异常："+e.getMessage());
        }

        return "letterDetail";
    }

    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String conversationList(Model model) {

        try {
            ArrayList<ViewObject> vos = new ArrayList<>();
            int localUserId = hostHolder.getUser().getId();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            for (Message message : conversationList) {
                ViewObject vo = new ViewObject();
                int targetId = message.getFromId() == localUserId ? message.getToId() : message.getFromId();
                User user = userService.getUserById(targetId);
                vo.set("headUrl",user.getHeadUrl());
                vo.set("userName",user.getName());
                vo.set("targetId",targetId);
                vo.set("totalCount",messageService.getConversationCount(localUserId,message.getConversationId()));
                message.setId(messageService.getConversationCount(localUserId,message.getConversationId()));
                vo.set("conversation",message);
                vo.set("unreadCount",messageService.getConversationUnReadCount(localUserId,message.getConversationId()));
                vos.add(vo);
            }
            model.addAttribute("conversations",vos);

        }catch (Exception e){
            logger.error("获取站内信列表异常："+e.getMessage());
        }
        return "letter";
    }



}
