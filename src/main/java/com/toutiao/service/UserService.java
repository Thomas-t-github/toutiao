package com.toutiao.service;

import com.toutiao.controller.LoginController;
import com.toutiao.dao.LoginTicketDAO;
import com.toutiao.dao.UserDAO;
import com.toutiao.model.LoginTicket;
import com.toutiao.model.User;
import com.toutiao.utils.ToutiaoUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService .class);


    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User getUserById(int id){
        return userDAO.selectById(id);
    }

    public int addUser(User user){
        return userDAO.addUser(user);
    }

    public Map<String,Object> register(String name,String password){

        Map<String, Object> map = new HashMap<>();

        if(StringUtils.isBlank(name)){
            map.put("msgname","用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("msgpwd","密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(name);
        if (user != null){
            map.put("msgname","用户名已被注册");
            return map;
        }

        //密码强度

        user = new User();
        user.setName(name);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setPassword(ToutiaoUtil.MD5(password+user.getSalt()));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));

        userDAO.addUser(user);

        //注册成功
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);

        return map;
    }


    public Map<String,Object> login(String name,String password){

        Map<String, Object> map = new HashMap<>();

        if(StringUtils.isBlank(name)){
            map.put("msgname","用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("msgpwd","密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(name);
        if (user == null){
            map.put("msgname","用户不存在");
            return map;
        }

        if(user.getPassword().equals(ToutiaoUtil.MD5(password+user.getSalt()))){
            //登录成功
            String ticket = addLoginTicket(user.getId());
            map.put("ticket",ticket);
        }else{
            map.put("msgpwd","登录密码错误");
        }

        map.put("userId",user.getId());

        return map;
    }

    public String addLoginTicket(int userId){
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime()+1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replace("-",""));
        loginTicketDAO.addTicket(ticket);

        return ticket.getTicket();
    }

    public int logout(String ticket){
        int i = loginTicketDAO.updateStatus(ticket, 1);
        return i;
    }



}
