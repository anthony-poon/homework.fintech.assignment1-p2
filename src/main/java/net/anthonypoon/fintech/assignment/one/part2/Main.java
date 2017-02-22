
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.UIManager;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.ui.RefineryUtilities;

public class Main {
    private static final double riskFreeR = 0.0009;
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
    private static final Double[] PRESET_EXPECTED_RETURN = {
        0.089,
        0.163,
        0.251,
        0.181,
        0.185,
        0.241,
        0.154,
        0.217,
        0.126,
        0.232,
    };
    
    private static final Double[] PRESET_EXPECTED_ALT_RETURN = {
        0.089,
        0.163,
        0.190,
        0.181,
        0.185,
        0.241,
        0.154,
        0.217,
        0.126,
        0.232,
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
        
        Portfolio hsiPortf = new Portfolio("HSI rate 1", stockList, hsiIndex, riskFreeR);
        Portfolio hsceiPortf = new Portfolio("HSCEI rate 1", stockList, hsceiIndex, riskFreeR);
        
        for (Stock stock : stockList) {
            stock.setBeta(0.999);
        }
        
        System.out.println();
        for (Stock stock : hsiPortf.getStock()) {
            System.out.println("HSI of stock#" + stock.getCode() + ": Alpha = " + ddf.format(stock.getAlpha()) + "; Beta = " + ddf.format(stock.getBeta()));
        }
        
        System.out.println();
        for (Stock stock : hsceiPortf.getStock()) {
            System.out.println("HSCEI of stock#" + stock.getCode() + ": Alpha = " + ddf.format(stock.getAlpha()) + "; Beta = " + ddf.format(stock.getBeta()));
        }
        
        System.out.println();
        for (Stock stock : hsiPortf.getStock()) {
            System.out.println("HSI EK of stock #" + stock.getCode() + "\t" + ddf.format(stock.getEK()));
        }
        
        System.out.println();
        for (Stock stock : hsceiPortf.getStock()) {
            System.out.println("HSCEI EK of stock #" + stock.getCode() + "\t" + ddf.format(stock.getEK()));
        }
        
        hsiPortf.regressIndexAsPNG("HSI");
        hsceiPortf.regressIndexAsPNG("HSCEI");
        System.out.println();
        getOptimalPortf(hsiPortf);
        System.out.println();
        getOptimalPortf(hsceiPortf);
        
        List<Stock> altRStockList = new ArrayList(stockList);
        for (int i = 0; i < altRStockList.size(); i++) {
            altRStockList.get(i).setRate(PRESET_EXPECTED_ALT_RETURN[i]);
        }
        System.out.println();
        Portfolio altHSIPort = new Portfolio("HSI Rate 2", altRStockList, hsiIndex, riskFreeR);
        Portfolio altHSCEIPort = new Portfolio("HSCEI Rate 2", altRStockList, hsceiIndex, riskFreeR);
        System.out.println();
        getOptimalPortf(altHSIPort);
        System.out.println();
        getOptimalPortf(altHSCEIPort);
        /**
        Double[] TEST_STOCK_RETURN = {
            15.0,
            17.0,
            12.0,
            17.0,
            11.0,
            11.0,
            11.0,
            7.0,
            7.0,
            5.6,
        };
        Double[] TEST_STOCK_BETA = {
            1.0,
            1.5,
            1.0,
            2.0,
            1.0,
            1.5,
            2.0,
            0.8,
            1.0,
            0.6,
        };
        Double[] TEST_STOCK_EK = {
            Math.sqrt(50.0),
            Math.sqrt(40.0),
            Math.sqrt(20.0),
            Math.sqrt(10.0),
            Math.sqrt(40.0),
            Math.sqrt(30.0),
            Math.sqrt(40.0),
            Math.sqrt(16.0),
            Math.sqrt(20.0),
            Math.sqrt(6.0),
        };
        List<Stock> testStock = new ArrayList();
        for (int i = 0; i < 10; i ++) {
            Stock stock = new Stock(i, "Test stock #" + i);
            stock.setRate(TEST_STOCK_RETURN[i]);
            stock.setEK(TEST_STOCK_EK[i]);
            stock.setBeta(TEST_STOCK_BETA[i]);
            testStock.add(stock);
        }
        Portfolio testP = new Portfolio(testStock, 5.0);
        testP.sortByExReturnOverBeta();
        testP.setMarketVar(10.0);
        System.out.println();
        System.out.println("Test Table 1");
        for (Stock stock : testP.getStock()) {
            System.out.print(stock.getCode() + "\t");
            System.out.print(stock.getRate()+ "\t");
            System.out.print(ddf.format(stock.getExReturn())+ "\t");
            System.out.print(ddf.format(stock.getBeta())+ "\t");
            System.out.print(ddf.format(Math.pow(stock.getEK(), 2))+ "\t");
            System.out.println(ddf.format(stock.getExReturnToBeta())+ "\t");
        }
        
        System.out.println();
        System.out.println("test Table 2");
        Double sumT1 = 0.0;
        Double sumT2 = 0.0;
        for (int i = 0; i < testP.getStock().size(); i ++) {
            Stock stock = testP.getStock().get(i);
            System.out.print(stock.getCode() + "\t");
            System.out.print(ddf.format(stock.getExReturn() / stock.getBeta()) + "\t");
            System.out.print(ddf.format(stock.getExReturn() * stock.getBeta() / Math.pow(stock.getEK(), 2))+ "\t");
            sumT1 = sumT1 + stock.getExReturn() * stock.getBeta() / Math.pow(stock.getEK(), 2);
            System.out.print(ddf.format(Math.pow(stock.getBeta(), 2) / Math.pow(stock.getEK(), 2))+ "\t");
            sumT2 = sumT2 + Math.pow(stock.getBeta(), 2) / Math.pow(stock.getEK(), 2);
            System.out.print(ddf.format(sumT1)+ "\t");            
            System.out.print(ddf.format(sumT2)+ "\t");
            System.out.println(ddf.format(testP.getCutOff(i)));
 
        }
        
        System.out.println();
        System.out.println("Optimal Weight (Test Rate 1)");
        for (Map.Entry<Integer, Double> pair : testP.getWeight().entrySet()) {
            System.out.println(pair.getKey() + "\t"  + pair.getValue());
        }
        * **/
    }
    
    private static void getOptimalPortf(Portfolio p) {
        p.sortByExReturnOverBeta();
        System.out.println(p.getName() + " Table 1");
        for (Stock stock : p.getStock()) {
            System.out.print(stock.getCode() + "\t");
            System.out.print(stock.getRate()+ "\t");
            System.out.print(ddf.format(stock.getExReturn())+ "\t");
            System.out.print(ddf.format(stock.getBeta())+ "\t");
            System.out.print(ddf.format(Math.pow(stock.getEK(), 2))+ "\t");
            System.out.println(ddf.format(stock.getExReturnToBeta())+ "\t");
        }
        System.out.println();
        System.out.println(p.getName() + " Table 2");
        Double sum1 = 0.0;
        Double sum2 = 0.0;
        for (int i = 0; i < p.getStock().size(); i ++) {
            Stock stock = p.getStock().get(i);
            System.out.print(stock.getCode() + "\t");
            System.out.print(ddf.format(stock.getExReturn() / stock.getBeta()) + "\t");
            System.out.print(ddf.format(stock.getExReturn() * stock.getBeta() / Math.pow(stock.getEK(), 2))+ "\t");
            sum1 = sum1 + stock.getExReturn() * stock.getBeta() / Math.pow(stock.getEK(), 2);
            System.out.print(ddf.format(Math.pow(stock.getBeta(), 2) / Math.pow(stock.getEK(), 2))+ "\t");
            sum2 = sum2 + Math.pow(stock.getBeta(), 2) / Math.pow(stock.getEK(), 2);
            System.out.print(ddf.format(sum1)+ "\t");            
            System.out.print(ddf.format(sum2)+ "\t");
            System.out.println(ddf.format(p.getCutOff(i)));
 
        }
        
        System.out.println();
        System.out.println(p.getName() + " Optimal Weight");
        for (Map.Entry<Integer, Double> pair : p.getWeight().entrySet()) {
            System.out.println(pair.getKey() + "\t"  + ddf.format(pair.getValue()));
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
        int i = 0;
        for (Stock stock : stockMap.values()) {
            stock.setRate(PRESET_EXPECTED_RETURN[i]);
            i++;
        }
        return stockMap;
    }
}
