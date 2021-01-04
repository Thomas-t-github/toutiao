package com.toutiao.dao;


import com.toutiao.model.LoginTicket;
import com.toutiao.model.News;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface LoginTicketDAO {

    String TABLE_NAME = "login_ticket";
    String INSERT_FIELDS = " user_id,ticket,expired,status ";
    String SELECT_FIELDS = " id,"+INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(",INSERT_FIELDS,
            ") values (#{userId},#{ticket},#{expired},#{status})"})
    public int addTicket(LoginTicket loginTicket);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where ticket=#{ticket}"})
    public LoginTicket selectByTicket(String ticket);

    @Update({"update ",TABLE_NAME," set status=#{status} where ticket=#{ticket}"})
    public int updateStatus(@Param("ticket") String ticket,@Param("status") int status );

}
