package com.toutiao;

import com.toutiao.dao.CommentDAO;
import com.toutiao.dao.LoginTicketDAO;
import com.toutiao.dao.NewsDAO;
import com.toutiao.dao.UserDAO;
import com.toutiao.model.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
@Sql("/init-schema.sql")
public class InitDatabaseTests {

	@Autowired
	UserDAO userDAO;

	@Autowired
	NewsDAO newsDAO;

	@Autowired
	LoginTicketDAO loginTicketDAO;

	@Autowired
	CommentDAO commentDAO;

	@Test
	public void initdata() {

		Random random = new Random();
		for(int i = 0; i < 10; i++){
			User user = new User();
			user.setName(String.format("USER%d",i));
			user.setPassword("");
			user.setSalt("");
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",random.nextInt(1000)));

			userDAO.addUser(user);

			News news = new News();
			news.setCommentCount(i+1);
			Date date = new Date();
			date.setTime(date.getTime() + 1000*3600*5*i);
			news.setCreatedDate(date);
			news.setImage(String.format("http://images.nowcoder.com/head/%dt.png",random.nextInt(1000)));
			news.setLikeCount(i+1);
			news.setLink(String.format("http://www.nowcoder.com/%dt.html",random.nextInt(1000)));
			news.setTitle(String.format("TITLE{%d}",i));
			news.setUserId(i+1);

			newsDAO.addNews(news);
			user.setPassword("newpassword");
			userDAO.updateUser(user);

			for (int j = 0; j < 3; j++){
				Comment comment = new Comment();
				Date date1 = new Date();
				date1.setTime(date.getTime() + 1000*9*j);
				comment.setCreatedDate(date1);
				comment.setContent("comment "+j);
				comment.setEntityId(news.getUserId());
				comment.setEntityType(EntityType.ENTITY_NEWS);
				comment.setUserId(j+2);
				comment.setStatus(0);
				commentDAO.addComment(comment);

			}


			LoginTicket ticket = new LoginTicket();
			ticket.setStatus(0);
			ticket.setExpired(date);
			ticket.setUserId(i+1);
			ticket.setTicket(String.format("TICKET%d",i));
			loginTicketDAO.addTicket(ticket);
			loginTicketDAO.updateStatus(ticket.getTicket(),1);

		}

		Assert.assertEquals("newpassword",userDAO.selectById(1).getPassword());

		userDAO.deleteUser(1);
		Assert.assertNull(userDAO.selectById(1));
		Assert.assertEquals(2,loginTicketDAO.selectByTicket("TICKET1").getUserId());
		Assert.assertNotNull(commentDAO.selectByEntity(1,EntityType.ENTITY_NEWS));


	}

}
