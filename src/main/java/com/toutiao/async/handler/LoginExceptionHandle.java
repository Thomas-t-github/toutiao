package com.toutiao.async.handler;

import cn.hutool.extra.mail.MailUtil;
import com.toutiao.async.EventHandle;
import com.toutiao.async.EventModel;
import com.toutiao.async.EventType;
import com.toutiao.model.Message;
import com.toutiao.model.User;
import com.toutiao.service.MessageService;
import com.toutiao.service.UserService;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class LoginExceptionHandle implements EventHandle {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    private VelocityEngine velocityEngine;

    @Override
    public void doHandle(EventModel model) {
        //判断是否登录异常，这里简写
        if(model.getActorId() == 11){
            Message message = new Message();
            //假设10为系统id
            message.setFromId(10);
            message.setToId(model.getActorId());
            message.setCreatedDate(new Date());
            message.setConversationId("10_"+model.getActorId());
            message.setHasRead(0);
            User user = userService.getUserById(model.getActorId());
            message.setContent("尊敬的用户："+user.getName()+" 你的登录IP异常,如果不是你的操作，请修改密码！");

            messageService.addMessage(message);

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("username","张三");
            String result = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "mails/exception.html", "UTF-8", hashMap);


            MailUtil.send("747059344@qq.com", "登录异常", result, true);

        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
