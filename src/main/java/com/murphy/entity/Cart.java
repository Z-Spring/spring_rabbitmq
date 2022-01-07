package com.murphy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cart")
public class Cart {
    private int pid;
    private int count;
    private double summprice;
    private int uid;

 /*   public Cart(int pid,int count,double price){
        this.pid=pid;
        this.count=count;
    }*/

}
