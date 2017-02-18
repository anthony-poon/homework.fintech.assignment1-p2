/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.fintech.assignment.one.part2;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author ypoon
 */
public class StockPriceEntry {
    private Date date;
    private Double price;
    private Double rValue;
    public StockPriceEntry(Date date, Double price) {
        this.date = date;
        this.price = price;
    }

    public StockPriceEntry(Date date, Double price, double rValue) {
        this.rValue = rValue;
        this.date = date;
        this.price = price;
    }
    
    public Date getDate() {
        return date;
    }

    public Double getPrice() {
        return price;
    }
    
    public Double getR() {
        return rValue;
    }
    
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("d/M/y");
        if (rValue == null) {
            return formatter.format(date) + "\t" + String.valueOf(price);
        } else {
            return formatter.format(date) + "\t" + String.valueOf(price) + "\t" + String.valueOf(rValue);
        }
        
    }
}
