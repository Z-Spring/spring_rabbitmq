package com.murphy.service;

import org.springframework.stereotype.Service;


/**
 * @author Murphy
 */
@Service
public interface OrderService {
    /**
     * 获取订单
     * @param id 商品id
     * @return
     */
    public String getOrder(int id);
}
