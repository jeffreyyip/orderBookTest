package com.jy;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.jy.OrderBook.SIDE.BUY;
import static com.jy.OrderBook.SIDE.SELL;
import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    OrderBook book;

    @Before
    public void init(){
        book = new OrderBookSortedMapImpl();
    }
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void placeBuyOrderShouldReturnTrue(){
        System.out.println("test: placeBuyOrderShouldReturnTrue *************");
        assertTrue(book.placeOrder(BUY,  100.12, 100));
    }

    @Test
    public void placeSellOrderShouldReturnTrue(){
        System.out.println("test: placeSellOrderShouldReturnTrue ***************");

        assertTrue(book.placeOrder(SELL, 200, 102));
    }

    @Test
    public void placeBuyOrderAtTwoLevel(){
        System.out.println("test: placeBuyOrderAtTwoLevel ****************");

        int expectedSize = 2;
        OrderEntry[] expected = new OrderEntryView[expectedSize];
        expected[0] = new OrderEntryView(BUY, 105.12, 200, 1);
        expected[1] = new OrderEntryView(BUY, 100.12, 100, 2);


        book.placeOrder(BUY, 100.12, 100);
        book.placeOrder(BUY, 105.12, 200);

        List<OrderEntry> orders = book.getAllBySideAndLevel(BUY);

        assertTrue(orders != null && orders.size()==expectedSize);

        for (int i=0; i<expectedSize; i++) {
            assertEquals(expected[i], orders.get(i));
        }

        for (int i=0; i<expectedSize; i++) {
            OrderEntry orderEntry = book.getOrderBySideAndLevel(BUY, i+1);
            assertEquals(expected[i], orderEntry);

        }

    }

    @Test
    public void placeBuyOrderAtSameLevel(){
        System.out.println("test: placeBuyOrderAtSameLevel ****************");

        int expectedSize = 2;
        OrderEntry[] expected = new OrderEntryView[expectedSize];
        expected[0] = new OrderEntryView(BUY, 105.12, 200, 1);
        expected[1] = new OrderEntryView(BUY, 100.12, 300, 2);



        book.placeOrder(BUY, 100.12, 100);
        book.placeOrder(BUY, 105.12, 200);
        book.placeOrder(BUY, 100.12, 200);

        List<OrderEntry> orders = book.getAllBySideAndLevel(BUY);

        System.out.println(orders);

        assertTrue(orders != null && orders.size()==expectedSize);

        for (int i=0; i<expectedSize; i++) {
            assertEquals(expected[i], orders.get(i));
        }

        for (int i=0; i<expectedSize; i++) {
            OrderEntry orderEntry = book.getOrderBySideAndLevel(BUY, i+1);
            assertEquals(expected[i], orderEntry);

        }

    }

    @Test
    public void placeBuySellOrderInTwoLevel(){
        System.out.println("test: placeBuySellOrderInTwoLevel ****************");

        int expectedSizeBuy = 2;
        OrderEntry[] expectedBuy = new OrderEntryView[expectedSizeBuy];
        expectedBuy[0] = new OrderEntryView(BUY, 105.12, 200, 1);
        expectedBuy[1] = new OrderEntryView(BUY, 100.12, 100, 2);

        int expectedSizeSell = 3;
        OrderEntry[] expectedSell = new OrderEntryView[expectedSizeSell];
        expectedSell[0] = new OrderEntryView(SELL, 106.20, 2000, 1);
        expectedSell[1] = new OrderEntryView(SELL, 106.22, 1000, 2);
        expectedSell[2] = new OrderEntryView(SELL, 106.25, 1000, 3);


        book.placeOrder(BUY, 100.12, 100);
        book.placeOrder(BUY, 105.12, 200);

        book.placeOrder(SELL, 106.22, 1000);
        book.placeOrder(SELL, 106.20, 2000);
        book.placeOrder(SELL, 106.25, 1000);

        System.out.println(book.getAll());
        List<OrderEntry> buyOrders = book.getAllBySideAndLevel(BUY);

        assertTrue(buyOrders != null && buyOrders.size()==expectedSizeBuy);

        List<OrderEntry> sellOrders = book.getAllBySideAndLevel(SELL);

        assertTrue(sellOrders != null && sellOrders.size()==expectedSizeSell);

        for (int i=0; i<expectedSizeBuy; i++) {
            assertEquals(expectedBuy[i], buyOrders.get(i));
        }

        for (int i=0; i<expectedSizeBuy; i++) {
            OrderEntry orderEntry = book.getOrderBySideAndLevel(BUY, i+1);
            assertEquals(expectedBuy[i], orderEntry);

        }

        for (int i=0; i<expectedSizeSell; i++) {
            assertEquals(expectedSell[i], sellOrders.get(i));
        }

        List<OrderEntry> allOrders = book.getAll();
        assertTrue(allOrders != null && allOrders.size()==expectedSizeBuy+expectedSizeSell);

    }

    @Test
    public void placeBuySellOrderOneMatch(){
        System.out.println("test: placeBuySellOrderMatch ****************");
        int expectedSizeBuy = 0;
        OrderEntry[] expectedBuy = new OrderEntryView[expectedSizeBuy];

        int expectedSizeSell = 1;
        OrderEntry[] expectedSell = new OrderEntryView[expectedSizeSell];
        expectedSell[0] = new OrderEntryView(SELL, 105.10, 300, 1);


        book.placeOrder(BUY, 105.12, 200);

        book.placeOrder(SELL, 105.10, 500);

        List<OrderEntry> buyOrders = book.getAllBySideAndLevel(BUY);

        assertTrue(buyOrders != null && buyOrders.size()==0);


        List<OrderEntry> sellOrders = book.getAllBySideAndLevel(SELL);

        assertTrue(sellOrders != null && sellOrders.size()==1);

        for (int i=0; i<expectedSizeBuy; i++) {
            assertEquals(expectedBuy[i], buyOrders.get(i));
        }

        for (int i=0; i<expectedSizeBuy; i++) {
            OrderEntry orderEntry = book.getOrderBySideAndLevel(BUY, i+1);
            assertEquals(expectedBuy[i], orderEntry);

        }


        for (int i=0; i<expectedSizeSell; i++) {
            assertEquals(expectedSell[i], sellOrders.get(i));
        }

        List<OrderEntry> allOrders = book.getAll();
        assertTrue(allOrders != null && allOrders.size()==expectedSizeBuy+expectedSizeSell);

    }

    @Test
    public void placeBuySellOrderTwoMatch(){
        System.out.println("test: placeBuySellOrderTwoMatch ****************");
        int expectedSizeBuy = 0;
        OrderEntry[] expectedBuy = new OrderEntryView[expectedSizeBuy];

        int expectedSizeSell = 1;
        OrderEntry[] expectedSell = new OrderEntryView[expectedSizeSell];
        expectedSell[0] = new OrderEntryView(SELL, 105.10, 100, 1);


        book.placeOrder(BUY, 105.12, 200);
        book.placeOrder(BUY, 105.22, 200);

        book.placeOrder(SELL, 105.10, 500);

        System.out.println(book.getAll());

        List<OrderEntry> buyOrders = book.getAllBySideAndLevel(BUY);

        assertTrue(buyOrders != null && buyOrders.size()==0);


        List<OrderEntry> sellOrders = book.getAllBySideAndLevel(SELL);

        assertTrue(sellOrders != null && sellOrders.size()==1);

        for (int i=0; i<expectedSizeBuy; i++) {
            assertEquals(expectedBuy[i], buyOrders.get(i));
        }

        for (int i=0; i<expectedSizeBuy; i++) {
            OrderEntry orderEntry = book.getOrderBySideAndLevel(BUY, i+1);
            assertEquals(expectedBuy[i], orderEntry);

        }


        for (int i=0; i<expectedSizeSell; i++) {
            assertEquals(expectedSell[i], sellOrders.get(i));
        }

        List<OrderEntry> allOrders = book.getAll();
        assertTrue(allOrders != null && allOrders.size()==expectedSizeBuy+expectedSizeSell);

    }

    @Test
    public void placeSellBuyOrderTwoMatch(){
        System.out.println("test: placeSellBuyOrderTwoMatch ****************");
        int expectedSizeBuy = 0;
        OrderEntry[] expectedBuy = new OrderEntryView[expectedSizeBuy];

        int expectedSizeSell = 1;
        OrderEntry[] expectedSell = new OrderEntryView[expectedSizeSell];
        expectedSell[0] = new OrderEntryView(SELL, 105.10, 100, 1);


        book.placeOrder(SELL, 105.10, 500);

        book.placeOrder(BUY, 105.12, 200);
        book.placeOrder(BUY, 105.22, 200);


        System.out.println(book.getAll());

        List<OrderEntry> buyOrders = book.getAllBySideAndLevel(BUY);

        assertTrue(buyOrders != null && buyOrders.size()==0);


        List<OrderEntry> sellOrders = book.getAllBySideAndLevel(SELL);

        assertTrue(sellOrders != null && sellOrders.size()==1);

        for (int i=0; i<expectedSizeBuy; i++) {
            assertEquals(expectedBuy[i], buyOrders.get(i));
        }

        for (int i=0; i<expectedSizeBuy; i++) {
            OrderEntry orderEntry = book.getOrderBySideAndLevel(BUY, i+1);
            assertEquals(expectedBuy[i], orderEntry);

        }


        for (int i=0; i<expectedSizeSell; i++) {
            assertEquals(expectedSell[i], sellOrders.get(i));
        }

        List<OrderEntry> allOrders = book.getAll();
        assertTrue(allOrders != null && allOrders.size()==expectedSizeBuy+expectedSizeSell);

    }


}
