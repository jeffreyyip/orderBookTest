package com.jy;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


public class OrderEntryView implements OrderEntry {

    private OrderBook.SIDE side;
    private double price;
    private int size;
    private int level;

    public OrderEntryView(OrderBook.SIDE side, double price, int size, int level){
        this.side = side;
        this.price = price;
        this.size = size;
        this.level = level;
    }

    @Override
    public OrderBook.SIDE getSide() {
        return side;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public int getSize(){
        return size;
    }


    @Override
    public int getLevel() {
        return level;
    }


    @Override
    public boolean equals(Object o){
        if (o == this)
            return true;

        if (o instanceof OrderEntry) {

            OrderEntry e = (OrderEntry) o;
            return (side == e.getSide()  && price == e.getPrice() && size == e.getSize() && level == e.getLevel());
        }
        return false;
    }

    @Override
    public int hashCode(){
        return Objects.hash(side, price, size, level);
    }

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("OrderEntry[");
        sb.append(" Level="+level);
        sb.append(",Side="+side);
        sb.append(",price="+price);
        sb.append(",size="+size);
        sb.append(" ]");
        return sb.toString();
    }

}
