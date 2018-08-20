package com.jy;

import java.util.List;

public interface OrderBook {
    enum SIDE { BUY, SELL}

    boolean placeOrder(SIDE side, double price, int quantity);

    OrderEntry getOrderBySideAndLevel(SIDE side, int level);

    List<OrderEntry> getAllBySideAndLevel(SIDE side);

    List<OrderEntry> getAll();
}
