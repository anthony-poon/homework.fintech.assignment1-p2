/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.fintech.assignment.one.part2;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ypoon
 */
public class Index {
    private List<Double> indexList = new ArrayList();
    
    public void addEntry(Double index) {
        indexList.add(index);
    }
    
    public List<Double> getRList() {
        List<Double> rList = new ArrayList();
        for (int i = 1; i < indexList.size(); i++) {
            rList.add((indexList.get(i) / indexList.get(i - 1)) - 1);
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
}
