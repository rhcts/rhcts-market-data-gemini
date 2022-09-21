package dev.rhcts.marketdata.fix;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class FixEngineManager {

    private static final Logger log = LoggerFactory.getLogger(FixEngineManager.class);

    private final boolean enabled;
    private final Application application;
    private final MessageStoreFactory messageStoreFactory;
    private final SessionSettings sessionSettings;
    private final LogFactory logFactory;
    private final MessageFactory messageFactory;

    private Connector connector;

    @Inject
    public FixEngineManager(@ConfigProperty(name = "quickfixj.enabled", defaultValue = "true") boolean enabled,
                            Application application, MessageStoreFactory messageStoreFactory,
                            SessionSettings sessionSettings, LogFactory logFactory, MessageFactory messageFactory) {
        this.enabled = enabled;
        this.application = application;
        this.messageStoreFactory = messageStoreFactory;
        this.sessionSettings = sessionSettings;
        this.logFactory = logFactory;
        this.messageFactory = messageFactory;
    }

    void onStart(@Observes StartupEvent ev) throws ConfigError {
        log.info("The application is starting...");
        if (!enabled) {
            return;
        }
        connector = new SocketInitiator(application, messageStoreFactory, sessionSettings, logFactory, messageFactory);
        connector.start();
    }

    void onStop(@Observes ShutdownEvent ev) {
        log.info("The application is stopping...");
        if (!enabled) {
            return;
        }
        connector.stop();
    }

}
