package com.jy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;


import static com.jy.OrderBook.SIDE.BUY;
import static com.jy.OrderBook.SIDE.SELL;
import static java.lang.Math.min;

public class OrderBookSortedMapImpl implements OrderBook{

    private ConcurrentSkipListMap<Double, AtomicInteger> buyOrders = new ConcurrentSkipListMap<>(Comparator.reverseOrder());
    private ConcurrentSkipListMap<Double, AtomicInteger> sellOrders = new ConcurrentSkipListMap<>();
    private EnumMap<SIDE, ConcurrentSkipListMap<Double, AtomicInteger>> bidAskSides = new EnumMap<>(SIDE.class);


    private ConcurrentHashMap<Double, ReentrantLock> locks = new ConcurrentHashMap<>();

    public OrderBookSortedMapImpl(){
        bidAskSides.put(BUY, buyOrders);
        bidAskSides.put(SELL, sellOrders);
    }

    @Override
    public boolean placeOrder(SIDE side, double price, int quantity) {
        if (quantity == 0)
            return false;


        ConcurrentSkipListMap<Double, AtomicInteger> bidAskSide = bidAskSides.get(side);
        boolean updated = false;

        while (!updated) {
            AtomicInteger v = bidAskSide.get(price);

            if (v != null) {
                do {
                    int oldSize = v.get();

                    if (oldSize == 0) {
                        break;
                    }
                    ReentrantLock lock = locks.computeIfAbsent(price, k -> new ReentrantLock());
                    try {
                        lock.lock();
                        updated = v.compareAndSet(oldSize, oldSize + quantity);
                    }finally{
                        lock.unlock();
                    }
                } while (!updated);

            }else{

                AtomicInteger exist = bidAskSide.putIfAbsent(price, new AtomicInteger(quantity));
                if (exist == null)
                    updated = true;
            }

        }

        tryMatch();

        return true;
    }

    private void tryMatch(){
        while(true) {
            Map.Entry<Double, AtomicInteger> bid = buyOrders.firstEntry();
            Map.Entry<Double, AtomicInteger> ask = sellOrders.firstEntry();

            if (bid == null || ask == null)
                return;

            Double bidPrice = bid.getKey();
            Double askPrice = ask.getKey();
            AtomicInteger bidSize = bid.getValue();
            AtomicInteger askSize = ask.getValue();
            int matchedQty;
            int oldBid;
            int oldAsk;


            if (bidPrice >= askPrice) {
                ReentrantLock bidLock = locks.computeIfAbsent(bidPrice, k -> new ReentrantLock());
                ReentrantLock askLock = locks.computeIfAbsent(askPrice, k -> new ReentrantLock());

                try {
                    bidLock.lock();
                    askLock.lock();

                    oldBid = bidSize.get();
                    oldAsk = askSize.get();
                    matchedQty = min(oldBid, oldAsk);

                    if (oldBid > 0 && oldAsk > 0 && oldBid - matchedQty >= 0 && oldAsk - matchedQty >= 0) {
                        bidSize.compareAndSet(oldBid, oldBid - matchedQty);
                        askSize.compareAndSet(oldAsk, oldAsk - matchedQty);

                    }
                } finally {
                    bidLock.unlock();
                    askLock.unlock();
                }

                if (bidSize.get() == 0) {
                    buyOrders.remove(bidPrice, bidSize);

                }


                if (askSize.get() == 0) {
                    sellOrders.remove(askPrice, askSize);
                }


            }else{
                break;
            }
        }

    }

    @Override
    public OrderEntry getOrderBySideAndLevel(SIDE side, int level) {

        List<OrderEntry> entries = getAllBySideAndLevel(side);
        if (level > 0 && level <= entries.size()){
            return entries.get(level-1);
        }else {
            return null;
        }
    }

    @Override
    public List<OrderEntry> getAllBySideAndLevel(SIDE side) {
        List<OrderEntry>  entries=new ArrayList<>();

        int level=1;
        for (Map.Entry<Double, AtomicInteger> e : bidAskSides.get(side).entrySet()){
            OrderEntryView c = new OrderEntryView(side, e.getKey(), e.getValue().get(), level++);
            entries.add(c);
        }

        return entries;
    }

    @Override
    public List<OrderEntry> getAll() {
        List<OrderEntry> entries = new ArrayList<>();

        entries.addAll(getAllBySideAndLevel(BUY));
        entries.addAll(getAllBySideAndLevel(SELL));

        return entries;
    }

}
