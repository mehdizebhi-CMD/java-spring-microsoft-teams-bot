package dev.mehdizebhi.tbot.core;

import com.corundumstudio.socketio.SocketIOServer;

import dev.mehdizebhi.tbot.event.StartupEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * A CommandLineRunner that Spring will run on startup to
 * start the SocketIO server
 */
@Component
public class SocketIOServerRunner implements CommandLineRunner {

    private final SocketIOServer socketIOServer;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public SocketIOServerRunner(SocketIOServer server) {
        socketIOServer = server;
    }

    @Override
    public void run(String... args) throws Exception {
        socketIOServer.start();
        eventPublisher.publishEvent(new StartupEvent(this));
    }
}
