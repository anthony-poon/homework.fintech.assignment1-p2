
package net.anthonypoon.fintech.assignment.one.part2;

import com.sun.xml.internal.messaging.saaj.util.TeeInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.ui.RefineryUtilities;

public class Main {
    private static final int[] PRESET_STOCK_CODE = {
        6,
        17,
        489,
        669,
        883,
        1211,
        1299,
        2318,
        2388,
        6837
    };
    private static final String[] PRESET_STOCK_NAME = {
        "Power Assets Holdings Ltd",
        "New World Development Co Ltd",
        "Dongfeng Motors",
        "Techtronic Industries",
        "CNOOC Ltd",
        "BYD",
        "AIA",
        "Ping An Insurance",
        "BOC Hong Kong",
        "Haitong Securities"
    };
    
    private static Index hsiIndex = new Index();
    private static Index hsceiIndex = new Index();
    private static String inputPath;
    private static DecimalFormat ddf = new DecimalFormat("0.000");
    public static void main(String[] args) throws Exception {
        TeeOutputStream teeStream = new TeeOutputStream(System.out, new FileOutputStream("output.txt"));
        PrintStream ps = new PrintStream(teeStream, true);
        System.setOut(ps); 
        processArgs(args);
        Map<Integer, Stock> stockMap = parseFile(inputPath);
        List<Stock> stockList = new ArrayList(stockMap.values());
        for (Stock stock : stockMap.values()) {
            System.out.printf("Stock #%-7s", stock.getCode());
            System.out.println("STD = " + ddf.format(stock.getStd()));
        }
        
        System.out.println("HSI Index STD = " + ddf.format(hsiIndex.getStd()));
        System.out.println("HSCEI Index STD = " + ddf.format(hsceiIndex.getStd()));
        
        List<Double> hsiBeta = regress(stockList, hsiIndex, "hsi");
        List<Double> hsceiBeta = regress(stockList, hsceiIndex, "hscei");
        
        List<Double> hsiEK = getEK(stockList, hsiIndex, hsiBeta);
        List<Double> hsceiEK = getEK(stockList, hsceiIndex, hsceiBeta);
        
        for (int i = 0; i < stockList.size(); i ++) {
            System.out.println("HSI EK of stock #" + stockList.get(i).getCode() + "\t" + ddf.format(hsiEK.get(i)));
        }
        
        for (int i = 0; i < stockList.size(); i ++) {
            System.out.println("HSCEI EK of stock #" + stockList.get(i).getCode() + "\t" + ddf.format(hsceiEK.get(i)));
        }
    }
    
    private static void processArgs(String[] args) throws ParseException {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        CommandLine cli = parser.parse(options, args);
        inputPath = cli.getArgList().get(0);
    }
    
    private static Map<Integer, Stock> parseFile(String path) throws Exception {
        Map<Integer, Stock> stockMap = new TreeMap();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] strArray = line.split(",");
            SimpleDateFormat dateFormatter = new SimpleDateFormat("d/M/y");
            Date date = dateFormatter.parse(strArray[0]);
            for (int col = 1; col <= PRESET_STOCK_CODE.length; col++ ) {
                if (!stockMap.containsKey(PRESET_STOCK_CODE[col - 1])) {
                    stockMap.put(PRESET_STOCK_CODE[col - 1], new Stock(PRESET_STOCK_CODE[col - 1], PRESET_STOCK_NAME[col - 1]));
                }
                Stock currentStock = stockMap.get(PRESET_STOCK_CODE[col - 1]);
                currentStock.addEntry(Double.valueOf(strArray[col]));
            }
            hsiIndex.addEntry(Double.valueOf(strArray[strArray.length - 2]));
            hsceiIndex.addEntry(Double.valueOf(strArray[strArray.length - 1]));
        }
        return stockMap;
    }
    
    private static List<Double> regress(List<Stock> stockList, Index index, String filePrefix) throws Exception{
        List<Double> returnList = new ArrayList();
        SimpleRegression regress = new SimpleRegression();
        for (Stock stock : stockList) {
            List<Double> stockRList = stock.getRList();
            List<Double> hsiRList = index.getRList();
            Plotter plotter = new Plotter(stock.getName() + " Regression");
            for (int i = 0; i < stockRList.size(); i++) {
                plotter.addXYPoint(hsiRList.get(i), stockRList.get(i));
                regress.addData(hsiRList.get(i), stockRList.get(i));
            }
            Double slope = regress.getSlope();
            Double intercept = regress.getIntercept();
            returnList.add(slope);
            System.out.println("Stock #" + stock.getCode() + " alpha = " + ddf.format(intercept) + "; beta = " + ddf.format(slope));
            plotter.setRegression(intercept, slope);
            //plotter.render();
            plotter.saveAsPNG(filePrefix + "_" +  stock.getCode() + "_regress.png", 500, 400);
        }
        return returnList;
    }
    
    private static List<Double> getEK(List<Stock> stockList, Index index, List<Double> betaList) {
        List<Double> returnList = new ArrayList();
        for (int i = 0; i < stockList.size(); i ++) {
            returnList.add(Math.sqrt(Math.pow(stockList.get(i).getStd(), 2) - Math.pow(betaList.get(i), 2) * Math.pow(index.getStd(), 2)));
        }
        return returnList;
    }
}
