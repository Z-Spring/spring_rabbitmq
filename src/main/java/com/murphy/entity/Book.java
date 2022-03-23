package com.murphy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * Book entity.
 * @author Murphy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookinfo")
public class Book implements Serializable {
    private int pid;
    private String name;
    private double price;
    private String imgpath;

}
