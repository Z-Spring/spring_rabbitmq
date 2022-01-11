package com.murphy.service;

import com.murphy.entity.Book;
import com.murphy.entity.Cart;
import com.murphy.mapper.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
public class BookService {
    @Autowired
    private BookMapper bookMapper;

    public List<Book> getBook() {
        List list = bookMapper.display();
        return list;
    }

    public Book getBookById(int pid) {
        return bookMapper.display2(pid);
    }

    public boolean addCart(int pid, int count, double summprice, int uid) {
        if (bookMapper.count(pid, count, summprice, uid) > 0) {
            return true;
        }
        return false;
    }

    public List<Book> getCart(int uid) {
        List list = bookMapper.getCart(uid);
        return list;
    }

    public List<Integer> getId() {
        if (bookMapper.getId().isEmpty()) {
            //设置空集合
            return Collections.emptyList();
        }
        return bookMapper.getId();
    }

    //    根据uid寻找pid
    public List<Integer> getId2(int uid) {
        if (bookMapper.getId2(uid).isEmpty()) {
            //设置空集合
            return Collections.emptyList();
        }
        return bookMapper.getId2(uid);
    }

    public int getCount(int pid, int uid) {
        if (bookMapper.getId2(uid).contains(pid)) {
            return bookMapper.getCount(pid, uid);
        }
        return 0;
    }

    public boolean updateCart(Cart cart) {
        if (bookMapper.updateCart(cart)) {
            return true;
        }
        return false;
    }

    public Double getPrice(int uid) {
        if (bookMapper.getId2(uid).isEmpty()) {
            return 0.00;
        }
        return bookMapper.getPrice(uid);
    }

    public List getPidFromBookInfo(){
        return bookMapper.getPidFromBookInfo();
    }
}
