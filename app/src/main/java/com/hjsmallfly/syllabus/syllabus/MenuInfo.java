package com.hjsmallfly.syllabus.syllabus;

import java.io.Serializable;

/**
 * Created by daidaijie on 2016/3/26.
 */
public class MenuInfo implements Serializable {
    private String dish;
    private String price;

    public String getDish() {
        return dish;
    }

    public void setDish(String dish) {
        this.dish = dish;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
