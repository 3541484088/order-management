package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User selectByOpenid(String openid);

    /**
     * 插入用户数据
     * @param user
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into user(openid, name, create_time) values (#{openid},#{name},#{createTime})")
    void insert(User user);
}
