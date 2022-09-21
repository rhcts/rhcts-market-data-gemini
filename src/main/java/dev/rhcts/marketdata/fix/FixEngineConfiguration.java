package dev.rhcts.marketdata.fix;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import quickfix.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class FixEngineConfiguration {

    @ConfigProperty(name = "quickfixj.session-settings-file")
    String sessionSettingsFile;

    @Produces
    public SessionSettings sessionSettings() throws ConfigError {
        return new SessionSettings(sessionSettingsFile);
    }

    @Produces
    public MessageStoreFactory messageStoreFactory() {
        return new MemoryStoreFactory();
    }

    @Produces
    public LogFactory logFactory(SessionSettings sessionSettings) {
        return new SLF4JLogFactory(sessionSettings);
    }

    @Produces
    public MessageFactory messageFactory() {
        return new quickfix.fix44.MessageFactory();
    }

}
