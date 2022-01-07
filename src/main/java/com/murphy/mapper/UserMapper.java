package com.murphy.mapper;

import com.murphy.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Mapper
public interface UserMapper {
    //登录
    @Select("select count(*) from user2 where name=#{user.name} and password=#{user.password}")
    int login(@Param("user") User user);

    //注册
    @Insert("insert into user2 (email,name,password,uid2) values (#{user.email},#{user.name},#{user.password},#{user.uid2})")
    int register(@Param("user") User user);

    //从user2中取出userID
    @Select("SELECT uid FROM user2 WHERE name=#{name}")
    int getUid(@Param("name") String name);

}
