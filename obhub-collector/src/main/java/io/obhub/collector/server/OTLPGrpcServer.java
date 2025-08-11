package io.obhub.collector.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.obhub.collector.service.LogsService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class OTLPGrpcServer {

    private Server server;


    @PostConstruct
    public void start() throws Exception {
        server = ServerBuilder
                .forPort(4317)
                .permitKeepAliveTime(30, TimeUnit.SECONDS)
                .addService(new LogsService())
//                .intercept(remoteAddrInterceptor)
//                .maxInboundMessageSize(DEFAULT_MAX_INBOUND_MESSAGE_SIZE)
//                .maxInboundMetadataSize(DEFAULT_MAX_INBOUND_METADATA_SIZE)
                .build()
                .start();

        new Thread(() -> {
            try {
                server.awaitTermination();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }
}
