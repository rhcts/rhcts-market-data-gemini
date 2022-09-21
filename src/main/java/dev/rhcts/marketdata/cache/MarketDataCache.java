package dev.rhcts.marketdata.cache;

import org.infinispan.client.hotrod.DefaultTemplate;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class MarketDataCache {

    private static final Logger log = LoggerFactory.getLogger(MarketDataCache.class);

    private final RemoteCacheManager remoteCacheManager;
    private final Map<String, RemoteCache<MarketDataKey, Double>> remoteCacheMap;

    public MarketDataCache(RemoteCacheManager remoteCacheManager) {
        this.remoteCacheManager = remoteCacheManager;
        this.remoteCacheMap = new ConcurrentHashMap<>();
    }

    private RemoteCache<MarketDataKey, Double> getRemoteCache(String symbol) {
        if (this.remoteCacheMap.containsKey(symbol)) {
            return this.remoteCacheMap.get(symbol);
        }
        RemoteCache<MarketDataKey, Double> remoteCache = this.remoteCacheManager.administration().getOrCreateCache(symbol, DefaultTemplate.DIST_SYNC);
        this.remoteCacheMap.put(symbol, remoteCache);
        return remoteCache;
    }

    public void update(String symbol, Side side, double price, double quantity) {
        this.getRemoteCache(symbol).put(new MarketDataKey(side, price), quantity);
    }

    public void remove(String symbol, Side side, double price) {
        this.getRemoteCache(symbol).remove(new MarketDataKey(side, price));
    }

}
