package dev.rhcts.marketdata.fix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.Message;
import quickfix.field.*;
import quickfix.fix44.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GeminiMarketDataApplication extends quickfix.fix44.MessageCracker implements Application {

    private static final Logger log = LoggerFactory.getLogger(GeminiMarketDataApplication.class);

    private final MarketDataMessageHandler marketDataMessageHandler;

    @Inject
    public GeminiMarketDataApplication(MarketDataMessageHandler marketDataMessageHandler) {
        this.marketDataMessageHandler = marketDataMessageHandler;
    }

    @Override
    public void onCreate(SessionID sessionID) {
        log.debug("{}", sessionID);
    }

    @Override
    public void onLogon(SessionID sessionID) {
        log.debug("{}", sessionID);
        this.requestSecurityList(sessionID);
    }

    private void requestSecurityList(SessionID sessionID) {
        SecurityListRequest securityListRequest = new SecurityListRequest(new SecurityReqID("1"), new SecurityListRequestType(0));
        try {
            Session.sendToTarget(securityListRequest, sessionID);
        } catch (SessionNotFound e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onLogout(SessionID sessionID) {
        log.debug("{}", sessionID);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {
        log.debug("{}", sessionID);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        log.debug("{}", sessionID);
    }

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {
        log.debug("{}", sessionID);
    }

    @Override
    public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
       log.debug("{}", message);
       crack(message, sessionId);
    }

    @Override
    public void onMessage(SecurityList message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        log.debug("{}", message);
        List<String> symbolList = this.parseSymbolList(message);
        try {
            this.sendMarketDataRequest(symbolList, sessionID);
        } catch (SessionNotFound e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> parseSymbolList(SecurityList securityList) throws FieldNotFound {
        int value = securityList.getNoRelatedSym().getValue();
        NoRelatedSym noRelatedSymField = new NoRelatedSym();
        securityList.get(noRelatedSymField);
        SecurityList.NoRelatedSym group = new SecurityList.NoRelatedSym();
        Symbol symbolField = new Symbol();
        List<String> symbolList = new ArrayList<>();
        for (int i = 0; i < value; i++) {
            securityList.getGroup(i, group);
            String symbol = group.get(symbolField).getValue();
            symbolList.add(symbol);
        }
        return symbolList;
    }

    private void sendMarketDataRequest(List<String> symbolList, SessionID sessionID) throws SessionNotFound {
        MarketDataRequest marketDataRequest = new MarketDataRequest(new MDReqID("1"), new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_UPDATES), new MarketDepth(0));
        MarketDataRequest.NoMDEntryTypes noMDEntryTypesGroup = new MarketDataRequest.NoMDEntryTypes();
        noMDEntryTypesGroup.set(new MDEntryType(MDEntryType.BID));
        marketDataRequest.addGroup(noMDEntryTypesGroup);
        noMDEntryTypesGroup.set(new MDEntryType(MDEntryType.OFFER));
        marketDataRequest.addGroup(noMDEntryTypesGroup);
        noMDEntryTypesGroup.set(new MDEntryType(MDEntryType.TRADE));
        marketDataRequest.addGroup(noMDEntryTypesGroup);

        MarketDataRequest.NoRelatedSym noRelatedSymGroup = new MarketDataRequest.NoRelatedSym();
        symbolList.forEach(s -> {
            noRelatedSymGroup.set(new Symbol(s));
            noRelatedSymGroup.addGroup(noRelatedSymGroup);
        });
        Session.sendToTarget(marketDataRequest, sessionID);
    }


    @Override
    public void onMessage(MarketDataSnapshotFullRefresh message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        log.debug("{}", message);
    }

    @Override
    public void onMessage(MarketDataIncrementalRefresh message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        log.debug("{}", message);
    }

}
