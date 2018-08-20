package com.jy;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.jy.OrderBook.SIDE.BUY;
import static com.jy.OrderBook.SIDE.SELL;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "ORDER BOOK APP" );
        final Random r = new Random();
        ExecutorService orderPool = Executors.newFixedThreadPool(500);
        final int priceLen = 5;
        final int requests = 1000000;
        final double[] sellPrices = {100.11, 100.22, 100.33, 100.44, 100.55};
        final double[] buyPrices = {100.11, 100.22, 100.33, 100.44, 100.55};
        final int[] sellQty = {100, 200, 300, 400, 500};
        final int[] buyQty = {100, 200, 300, 400, 500};

        List<Callable<Boolean>> orderTasks = new ArrayList<>();

        final OrderBookSortedMapImpl b = new OrderBookSortedMapImpl();

        for (int i=0; i< requests; i++){

            orderTasks.add(() -> b.placeOrder(SELL, sellPrices[r.nextInt(priceLen)], sellQty[r.nextInt(priceLen)]) );

            orderTasks.add(() -> b.placeOrder(BUY, buyPrices[r.nextInt(priceLen)], buyQty[r.nextInt(priceLen)]) );

            orderTasks.add(() -> {b.getAll(); return true; } );

        }

        try {
            long start = System.currentTimeMillis();
            orderPool.invokeAll(orderTasks);
            long end = System.currentTimeMillis();
            BigDecimal totalTime = new BigDecimal((end-start)*1000);
            BigDecimal microSecPerRequest = totalTime.divide(new BigDecimal(orderTasks.size()), new MathContext(3, RoundingMode.HALF_UP));

            System.out.println("Throughput - requests (buy, sell, read): " + orderTasks.size() + ", take millis sec: " +  (end-start) );
            System.out.println("Average time per request (micro sec) : " + microSecPerRequest);
        }catch(InterruptedException e){
            e.printStackTrace();
        }


        orderPool.shutdown();
        try {
            boolean success = orderPool.awaitTermination(60, TimeUnit.SECONDS);
            System.out.println("success: " + success);
        }catch(InterruptedException ie){
            System.out.println("interrupted...");
        }

        System.out.println("order book: ");
        b.getAll().forEach(e -> System.out.println(e));
     }
}
