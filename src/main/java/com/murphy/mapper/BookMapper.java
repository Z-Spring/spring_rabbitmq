package com.murphy.mapper;

import com.murphy.entity.Book;
import com.murphy.entity.Cart;
import org.apache.ibatis.annotations.*;

import java.lang.reflect.Array;
import java.util.List;

@Mapper
public interface BookMapper {
    @Select("select * from bookinfo")
    List<Book> display();

    @Select("select * from bookinfo WHERE pid=#{pid}")
    Book display2(@Param("pid") int pid);

    @Insert("INSERT  INTO cart (pid,count,summprice,uid) VALUES (#{pid},#{count},#{summprice},#{uid})")
    int count(@Param("pid") int pid, @Param("count") int count, @Param("summprice") double summprice, @Param("uid") int uid);

    //    查询不同用户的购物车，SQL语句用的不熟练
    @Select("SELECT bookinfo.name,bookinfo.price,bookinfo.imgpath FROM bookinfo,cart WHERE cart.uid=#{uid} AND cart.pid=bookinfo.pid")
    List<Book> getCart(@Param("uid") int uid);

    //    从cart中搜索所有的pid
    @Select("SELECT pid FROM cart")
    List<Integer> getId();

    //    根据uid寻找pid  这个没办法了，后面单独添加的uid，全改的话真的改不完了。。。只能单独增加一个方法了
    @Select("SELECT pid FROM cart WHERE uid=#{uid}")
    List<Integer> getId2(@Param("uid") int uid);

    /**
     * 从购物车中查询商品的数量
     *
     * @param pid 商品ID
     * @param uid 用户ID
     * @return 商品的数量
     */
    @Select("SELECT count FROM cart WHERE pid=#{pid} AND uid=#{uid}")
    int getCount(@Param("pid") int pid, @Param("uid") int uid);

    @Update("UPDATE cart SET count=#{cart.count}+1,summprice=#{cart.summprice}*count  WHERE pid=#{cart.pid} AND uid=#{cart.uid}")
    boolean updateCart(@Param("cart") Cart cart);

    //    取出所有的price并求和
    @Select("SELECT SUM(summprice) FROM cart WHERE uid=#{uid}")
    double getPrice(@Param("uid") int uid);
}
