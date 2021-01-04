package com.toutiao.dao;


import com.toutiao.model.News;
import com.toutiao.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface NewsDAO {

    String TABLE_NAME = "news";
    String INSERT_FIELDS = " title,link,image,like_count,comment_count,created_date,user_id ";
    String SELECT_FIELDS = " id,"+INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(",INSERT_FIELDS,
            ") values (#{title},#{link},#{image},#{likeCount},#{commentCount},#{createdDate},#{userId})"})
    public int addNews(News news);


    @Select({"select ",SELECT_FIELDS ," from ",TABLE_NAME," where id=#{newsId}"})
    public News selectById(int newsId);

    @Update({"update ",TABLE_NAME," set comment_count=#{count} where id = #{newsId}"})
    public int updateCommentCount(@Param("newsId")int newsId,@Param("count")int count);

    @Update({"update ",TABLE_NAME," set like_count=#{count} where id = #{newsId}"})
    public int updateLikeCount(@Param("newsId")int newsId,@Param("count")int count);


    public List<News> selectByUserIdAndOffset(@Param("userId") int userId,
                                              @Param("offset") int offset,
                                              @Param("limit") int limit);

}
