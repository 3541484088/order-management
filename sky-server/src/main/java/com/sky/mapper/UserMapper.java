package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

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

    /**
     * 根据id查询用户
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{id}")
    User selectById(Long userId);

    /**
     * 根据条件统计用户数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
