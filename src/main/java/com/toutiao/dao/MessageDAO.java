package com.toutiao.dao;

import com.toutiao.model.Message;
import com.toutiao.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;


public interface MessageDAO {

    String TABLE_NAME = "message";
    String INSERT_FIELDS = " from_id,to_id,content,created_date,has_read,conversation_id ";
    String SELECT_FIELDS = " id,"+INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(",INSERT_FIELDS,
            ") values (#{fromId},#{toId},#{content},#{createdDate},#{hasRead},#{conversationId})"})
    public int addMessage(Message message);

    @Select({"select ",SELECT_FIELDS ," from ",TABLE_NAME," where conversation_id=#{conversationId} order by id desc limit #{offset},#{limit}"})
    public List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                              @Param("offset") int offset,
                                              @Param("limit") int limit);

    //select * from message t1 where (select count(id) from message t2 where t2.created_date>=t1.created_date and t2.conversation_id=t1.conversation_id)<=1 having from_id=11 or to_id=11 order by id desc limit 0,10;

    @Select({"select ",SELECT_FIELDS ," from ",TABLE_NAME," t1 where (select count(id) from ",TABLE_NAME," t2 where t2.created_date>=t1.created_date and t2.conversation_id=t1.conversation_id)<=1 having from_id=#{userId} or to_id=#{userId} order by id desc limit #{offset},#{limit}"})
    public List<Message> getConversationList(@Param("userId") int userId,
                                             @Param("offset") int offset,
                                             @Param("limit") int limit);

    //select count from (select count(id) as count,conversation_id from message where from_id=11 or to_id=11 group by conversation_id) tt where conversation_id='2_11';

    @Select({"select count from (select count(id) as count,conversation_id from ",TABLE_NAME," where from_id=#{userId} or to_id=#{userId} group by conversation_id) tt where conversation_id=#{conversationId};"})
    public int getConversationCount(@Param("userId") int userId,@Param("conversationId") String conversationId);


    //select count(id) as count from message where has_read=0 and to_id=11 and conversation_id='2_11';
    @Select({"select count(id) as count from ",TABLE_NAME," where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    public int getConversationUnReadCount(@Param("userId") int userId,@Param("conversationId") String conversationId);

    @Update({"update ",TABLE_NAME," set has_read=1 where id = #{id}"})
    public int updateHasRead(@Param("id") int id);

    @Update({"update ",TABLE_NAME," set has_read=1 where conversation_id=#{conversationId} and to_id=#{userId}"})
    public int updateAllHasRead(@Param("conversationId") String conversationId,@Param("userId") int userId);

}







