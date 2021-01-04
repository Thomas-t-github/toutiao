package com.toutiao.dao;

import com.toutiao.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;


public interface UserDAO {

    String TABLE_NAME = "user";
    String INSERT_FIELDS = " name,password,salt,head_url ";
    String SELECT_FIELDS = " id,"+INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(",INSERT_FIELDS,
            ") values (#{name},#{password},#{salt},#{headUrl})"})
    public int addUser(User user);

    @Select({"select ",SELECT_FIELDS ," from ",TABLE_NAME," where id=#{id}"})
    public User selectById(int id);

    @Select({"select ",SELECT_FIELDS ," from ",TABLE_NAME," where name=#{name}"})
    public User selectByName(String name);

    @Update({"update ",TABLE_NAME," set name=#{name},password=#{password},salt=#{salt},head_url=#{headUrl} where id = #{id}"})
    public int updateUser(User user);

    @Delete({"delete from ",TABLE_NAME," where id=#{id}"})
    public int deleteUser(int id);

}







