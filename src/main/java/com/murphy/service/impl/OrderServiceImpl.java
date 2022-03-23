package com.murphy.service.impl;

import com.murphy.service.OrderService;
import org.springframework.stereotype.Component;

/**
 * @author Murphy
 */
@Component
public class OrderServiceImpl implements OrderService {
    int a,b;

    public void getOrder2(int a, int b) {
        this.a = a;
        this.b = b;
    }
    public void setOrder(String order){
        System.out.println("order:"+order);
    }

    /**
     * 获取订单
     *
     * @param id 商品id
     * @return
     */
    @Override
    public String getOrder(int id) {
        return null;
    }
}
