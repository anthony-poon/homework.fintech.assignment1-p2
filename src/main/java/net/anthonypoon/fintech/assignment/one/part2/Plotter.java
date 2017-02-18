/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.fintech.assignment.one.part2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.List;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author ypoon
 */
public class Plotter extends ApplicationFrame {
    private double minX = Double.MAX_VALUE;
    private double maxX = Double.MIN_VALUE;
    private XYSeriesCollection dataObj = new XYSeriesCollection();
    private XYSeries dataSeries = new XYSeries("Data");
    private XYSeries lineSeries = new XYSeries("Line");
    private String title;
    private JFreeChart chart = null;
    public Plotter(String title) {
        super(title);       
        this.title = title;
        dataObj.addSeries(dataSeries);
        dataObj.addSeries(lineSeries);
    }
    
    public void addXYPoint(Double x, Double y) {
        if (x < minX) {
            minX = x;
        }
        if (x > maxX) {
            maxX = x;
        }
        dataSeries.add(x, y);
    }
    
    public void setRegression(Double intercept, Double s) {
        lineSeries.add(minX, s * minX);
        lineSeries.add(0, intercept);
        lineSeries.add(maxX, s * maxX);
    }
    
    public void render() {
        this.render(true);
    }
    
    private void render(boolean showGraph) {
        chart = ChartFactory.createXYLineChart(
                this.title,
                "Index Return",
                "Stock Return",
                dataObj,
                PlotOrientation.VERTICAL ,
                true,
                true,
                false
            );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560 , 367));
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesLinesVisible(1, true);
        //renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);        
        setContentPane(chartPanel);
        if (showGraph) {
            this.pack();
            RefineryUtilities.centerFrameOnScreen(this);
            this.setVisible(true);
        }
    }

    void addList(String seriesName, List<Point2D.Double> ptList) {
        XYSeries dataSeries = new XYSeries(seriesName, false);
        for (Point2D.Double pt : ptList) {
            dataSeries.add(pt.getX(), pt.getY());
        }
        dataObj.addSeries(dataSeries);
    }
    
    void saveAsPNG(String path, int width, int height ) throws Exception {
        this.render(false);
        ChartUtilities.saveChartAsPNG(new File(path), chart, width, height);
    }
}
