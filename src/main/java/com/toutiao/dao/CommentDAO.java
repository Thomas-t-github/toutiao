package com.toutiao.dao;

import com.toutiao.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CommentDAO {

    String TABLE_NAME = "comment";
    String INSERT_FIELDS = " content,user_id,entity_id,entity_type,created_date,status ";
    String SELECT_FIELDS = " id,"+INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(",INSERT_FIELDS,
            ") values (#{content},#{userId},#{entityId},#{entityType},#{createdDate},#{status})"})
    public int addComment(Comment comment);

    @Select({"select ",SELECT_FIELDS ," from ",TABLE_NAME," where entity_id=#{entityId} and entity_type=#{entityType} order by id desc"})
    public List<Comment> selectByEntity(@Param("entityId") int entityId,@Param("entityType") int entityType);

    @Select({"select count(id) from ",TABLE_NAME," where entity_id=#{entityId} and entity_type=#{entityType}"})
    public int getCommentCount(@Param("entityId") int entityId,@Param("entityType") int entityType);

}







