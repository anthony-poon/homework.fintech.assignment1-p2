/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.fintech.assignment.one.part2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author ypoon
 */
public class Stock {
    private int stockCode;
    private List<Double> priceArray = new ArrayList();
    private String name;
    private Double rate;
    private Double beta;
    private Double ek;
    private Double exReturn;
    private Double alpha;
    public Stock(int stockCode, String name) {
        this.stockCode = stockCode;
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void addEntry(Double price) {
        priceArray.add(price);        
    }
    
    public Integer getCode () {
        return stockCode;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
    
    public List<Double> getRList() {
        List<Double> rList = new ArrayList();
        for (int i = 1; i < priceArray.size(); i++) {
            rList.add((priceArray.get(i) / priceArray.get(i - 1)) - 1);
        }
        return rList;
    }
    
    public Double getRAverage() {
        List<Double> rList = getRList();
        Double sum = 0.0;
        for (Double r : rList) {
            sum += r;
        }
        
        return sum / rList.size();
    }
    
    public Double getStd() {
        Double rAvg = this.getRAverage();
        double sum = 0;
        List<Double> rList = getRList();
        for (Double r : rList) {
            sum = sum + Math.pow((r - rAvg), 2);
        }
        return Math.sqrt(sum / (rList.size() - 1)) * Math.sqrt(12);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Stock) {
            Stock stock = (Stock) obj;
            return this.getCode().equals(stock.getCode());
        } else {
            return false;
        }
    }

    public Double getBeta() {
        return beta;
    }

    public void setBeta(Double beta) {
        this.beta = beta;
    }

    public Double getEK() {
        return ek;
    }

    public void setEK(Double ek) {
        this.ek = ek;
    }

    public Double getExReturn() {
        return exReturn;
    }

    public void setExReturn(Double exReturn) {
        this.exReturn = exReturn;
    }

    public Double getAlpha() {
        return alpha;
    }

    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }
    
    public Double getExReturnToBeta() {
        return exReturn / beta;
    }
}
