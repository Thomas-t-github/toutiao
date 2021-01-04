package com.toutiao.controller;


import com.toutiao.model.*;
import com.toutiao.service.LikeService;
import com.toutiao.service.NewsService;
import com.toutiao.service.UserService;
import com.toutiao.utils.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    NewsService newsService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;


    @RequestMapping(path = {"/","/index"},method = {RequestMethod.GET,RequestMethod.POST})
    public String index(Model model){

        model.addAttribute("vos",getNews(0,0,10));

        return "home";
    }

    @RequestMapping(path = {"/user/{userId}"},method = {RequestMethod.GET,RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId){

        model.addAttribute("vos",getNews(userId,0,10));

        return "home";
    }

    public List<ViewObject> getNews(int userId, int offset, int limit){
        List<ViewObject> vos = new ArrayList<>();

        List<News> listNews = newsService.getLatestNews(userId, offset, limit);

        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;

        for (News news : listNews) {

            ViewObject vo = new ViewObject();
            vo.set("news",news);
            vo.set("user",userService.getUserById(news.getUserId()));
            if(localUserId != 0){
                vo.set("like",likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS,news.getId()));
            }else {
                vo.set("like",0);
            }

            vos.add(vo);
            //User user = (User) vo.get("user");

        }
        return vos;
    }

    @RequestMapping(path = {"/test"},method = {RequestMethod.GET,RequestMethod.POST})
    public String test(){
        return "test";
    }

}
