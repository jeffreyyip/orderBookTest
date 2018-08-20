package com.jy;


public interface OrderEntry {
    OrderBook.SIDE getSide();
    double getPrice();
    int getSize();
    int getLevel();

}
