package com.toutiao.controller;

import com.toutiao.model.*;
import com.toutiao.service.CommentService;
import com.toutiao.service.NewsService;
import com.toutiao.service.QiniuService;
import com.toutiao.service.UserService;
import com.toutiao.utils.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    NewsService newsService;

    @Autowired
    UserService userService;

    @Autowired
    QiniuService qiniuService;

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = "/news/{newsId}",method = {RequestMethod.GET,RequestMethod.POST})
    public String newsDetail(@PathVariable("newsId") int newsId, Model model){

        News news = newsService.getNewsById(newsId);
        if(news != null){
            //评论
            List<Comment> comments = commentService.selectByEntity(news.getId(), EntityType.ENTITY_NEWS);
            List<ViewObject> commentVOS = new ArrayList<>();
            for (Comment comment : comments) {
                ViewObject vo = new ViewObject();
                vo.set("comment",comment);
                vo.set("user",userService.getUserById(comment.getUserId()));
                commentVOS.add(vo);
            }
            model.addAttribute("comments",commentVOS);
        }


        model.addAttribute("news",news);
        model.addAttribute("owner",userService.getUserById(news.getUserId()));
        return "detail";
    }

    @RequestMapping(path = "/addComment",method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content) {
        try {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setUserId(hostHolder.getUser().getId());
            comment.setEntityId(newsId);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);
            commentService.addComment(comment);

            int commentCount = commentService.getCommentCount(newsId, EntityType.ENTITY_NEWS);
            newsService.updateCommentCount(newsId,commentCount);

        }catch (Exception e){
            logger.error("添加评论异常："+e.getMessage());
        }
        return "redirect:/news/"+String.valueOf(newsId);
    }

        @RequestMapping(path = "/uploadImage",method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file")MultipartFile file){

        try {

            //String fileUrl = qiniuService.saveImage(file);
            String fileUrl = newsService.saveImage(file);
            if (fileUrl == null){
                return ToutiaoUtil.getJSONString(1,"上传图片失败");
            }
            return ToutiaoUtil.getJSONString(0,fileUrl);

        }catch (Exception e){
            logger.error("上传图片失败"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"上传失败");
        }

    }


    @RequestMapping(path = "/user/addNews/",method = {RequestMethod.POST})
    @ResponseBody
    public String addNews(@RequestParam("image")String image,
                          @RequestParam("title")String title,
                          @RequestParam("link")String link){
        try {
            News news = new News();
            if (hostHolder.getUser() != null){
                news.setUserId(hostHolder.getUser().getId());
            }else {
                news.setUserId(3);
            }
            news.setTitle(title);
            news.setLink(link);
            news.setImage(image);
            news.setLikeCount(0);
            news.setCommentCount(0);
            news.setCreatedDate(new Date());
            if(newsService.addNews(news) > 0){
                return ToutiaoUtil.getJSONString(0);
            }else {
                return ToutiaoUtil.getJSONString(1,"添加资讯出错");
            }

        }catch (Exception e){
            logger.error("添加资讯异常："+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"添加资讯异常");
        }

    }





}
