package dev.rhcts.marketdata.fix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.fix44.MarketDataIncrementalRefresh;
import quickfix.fix44.MarketDataSnapshotFullRefresh;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MarketDataMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(MarketDataMessageHandler.class);

    public void handle(MarketDataSnapshotFullRefresh message) {
        log.debug("{}", message);
    }

    public void handle(MarketDataIncrementalRefresh message) {
        log.debug("{}", message);
    }

}
