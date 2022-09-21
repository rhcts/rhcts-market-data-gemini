package dev.rhcts.marketdata.cache;

import java.util.Objects;

public class MarketDataKey {

    private final Side side;
    private final double price;

    public MarketDataKey(Side side, double price) {
        this.side = Objects.requireNonNull(side);
        this.price = Objects.requireNonNull(price);
    }

    public Side getSide() {
        return side;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketDataKey that = (MarketDataKey) o;
        return Double.compare(that.price, price) == 0 && side == that.side;
    }

    @Override
    public int hashCode() {
        return Objects.hash(side, price);
    }

    @Override
    public String toString() {
        return "MarketDataKey{" +
                "side=" + side +
                ", price=" + price +
                '}';
    }

}
