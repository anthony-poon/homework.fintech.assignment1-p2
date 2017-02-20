/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.fintech.assignment.one.part2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 *
 * @author ypoon
 */
public class Portfolio {
    private Double riskFreeR;
    private List<Stock> stockList;
    private Index refIndex;
    private Double marketVar;
    private String name;
    public Portfolio(String name, List<Stock> stockList, Index index, double riskFreeR) {
        this.name = name;
        this.stockList = stockList;
        this.riskFreeR = riskFreeR;
        this.refIndex = index;
        this.marketVar = Math.pow(refIndex.getStd(), 2);
        for (Stock stock : stockList) {
            stock.setExReturn(stock.getRate() - riskFreeR);
            SimpleRegression regress = new SimpleRegression();
            List<Double> stockRList = stock.getRList();
            List<Double> indexRList = index.getRList();
            for (int i = 0; i < stockRList.size(); i++) {
                regress.addData(indexRList.get(i), stockRList.get(i));
            }
            stock.setAlpha(regress.getIntercept());
            stock.setBeta(regress.getSlope());
            stock.setEK(Math.sqrt(Math.pow(stock.getStd(), 2) - Math.pow(regress.getSlope(), 2) * Math.pow(index.getStd(), 2)));
        }
    }
    
    public Portfolio(List<Stock> stockList, double riskFreeR) {
        this.stockList = stockList;
        this.riskFreeR = riskFreeR;
        for (Stock stock : stockList) {
            stock.setExReturn(stock.getRate() - riskFreeR);
        }
    }
    
    public String getName() {
        return name;
    }
    
    public void sortByExReturnOverBeta() {
        Collections.sort(stockList, new Comparator<Stock>() {
            @Override
            public int compare(Stock o1, Stock o2) {
                return o2.getExReturnToBeta().compareTo(o1.getExReturnToBeta());
            }
        });
    }
    
    public List<Stock> getStock() {
        return stockList;
    }
    
    public void regressIndexAsPNG(String indexName) throws Exception {
        for (int i = 0; i < stockList.size(); i ++) {
            List<Double> stockRList = stockList.get(i).getRList();
            List<Double> indexRList = refIndex.getRList();
            Plotter plotter = new Plotter(stockList.get(i).getName() + " Regression");
            for (int j = 0; j < stockRList.size(); j++) {
                plotter.addXYPoint(indexRList.get(j), stockRList.get(j));
            }
            Double intercept = stockList.get(i).getAlpha();
            Double slope = stockList.get(i).getBeta();
            plotter.setRegression(intercept, slope);
            //plotter.render();
            plotter.saveAsPNG(indexName + "_" +  stockList.get(i).getCode() + "_regress.png", 500, 400);
        }
    }
    
    public int size() {
        return stockList.size();
    }
    
    public double getMarketVar() {
        return marketVar;
    }
    
    public void setMarketVar(Double var) {
        this.marketVar = var;
    }
    
    public double getCutOff(int cutOffIndex) {
        Double cutOff = 0.0;
        for (int i = 0; i <= cutOffIndex; i++) {
            cutOff = cutOff + (stockList.get(i).getExReturn() * stockList.get(i).getBeta()) / Math.pow(stockList.get(i).getEK(), 2);
        }
        cutOff = cutOff * getMarketVar();
        Double divider = 0.0;
        for (int i = 0; i <= cutOffIndex; i++) {
            divider = divider + Math.pow(stockList.get(i).getBeta(), 2) / Math.pow(stockList.get(i).getEK(), 2);
        }
        divider = 1 + getMarketVar() * divider;
        cutOff = cutOff / divider;
        return cutOff;
    }
    
    public Map<Integer, Double> getWeight() {
        Map<Integer, Double> returnMap = new LinkedHashMap();
        boolean condMet = false;
        int cutOffIndex = 0;
        Double cutOffRate = 0.0;
        while (!condMet || cutOffIndex + 1 >= stockList.size()) {
            cutOffRate = getCutOff(cutOffIndex);
            if (stockList.get(cutOffIndex + 1).getExReturnToBeta() <= getCutOff(cutOffIndex + 1)) {
                condMet = true;
            } else {
                cutOffIndex++;
            }
        }
        List<Double> zList = new ArrayList();
        Double zSum = 0.0;
        for (int i = 0; i <= cutOffIndex; i ++) {
            Double zVal = stockList.get(i).getExReturnToBeta() - cutOffRate;
            zVal = stockList.get(i).getBeta() / Math.pow(stockList.get(i).getEK(), 2) * zVal;
            zList.add(zVal);
            zSum = zSum + zVal;
        }
        for (int i = 0; i < stockList.size(); i ++) {
            if (i <= cutOffIndex) {
                returnMap.put(stockList.get(i).getCode(), (zList.get(i) / zSum));
            } else {
                returnMap.put(stockList.get(i).getCode(), 0.0);
            }
        }
        return returnMap;
    }
}
