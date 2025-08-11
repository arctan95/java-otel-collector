    package io.obhub.receiver;

    import io.grpc.Server;
    import io.grpc.ServerBuilder;

    import java.io.IOException;

    public class OTLPGrpcServer {

        private final OTLPOutput<?> tracesOutput;
        private final OTLPOutput<?> metricsOutput;
        private final OTLPOutput<?> logsOutput;
        private final Integer maxRecvMsgSizeMiB;
        private final Server server;

        private OTLPGrpcServer(Builder builder) {
            this.tracesOutput = builder.tracesOutput;
            this.metricsOutput = builder.metricsOutput;
            this.logsOutput = builder.logsOutput;
            this.maxRecvMsgSizeMiB = builder.maxRecvMsgSizeMiB;
            this.server = builder.server;
        }

        public static Builder builder() {
            return new Builder();
        }

        public void serve() throws IOException {
            this.server.start();
        }

        public static class Builder {
            private OTLPOutput<?> tracesOutput = null;
            private OTLPOutput<?> metricsOutput = null;
            private OTLPOutput<?> logsOutput = null;
            private Integer maxRecvMsgSizeMiB = 0;
            private Server server;

            public Builder withMaxRecvMsgSizeMiB(int size) {
                this.maxRecvMsgSizeMiB = size;
                return this;
            }

            public Builder withTracesOutput(OTLPOutput<?> output) {
                this.tracesOutput = output;
                return this;
            }

            public Builder withMetricsOutput(OTLPOutput<?> output) {
                this.metricsOutput = output;
                return this;
            }

            public Builder withLogsOutput(OTLPOutput<?> output) {
                this.logsOutput = output;
                return this;
            }

            public Builder withGrpcServerBuilder(ServerBuilder<?> serverBuilder) {
                return this;
            }

            public OTLPGrpcServer build() {
                return new OTLPGrpcServer(this);
            }
        }
    }
