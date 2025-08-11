package io.obhub.collector.service;

import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest;
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse;
import io.opentelemetry.proto.collector.logs.v1.LogsServiceGrpc;
import io.opentelemetry.proto.logs.v1.ResourceLogs;


public class LogsService extends LogsServiceGrpc.LogsServiceImplBase {


    @Override
    public void export(ExportLogsServiceRequest request, StreamObserver<ExportLogsServiceResponse> responseObserver) {
        for (ResourceLogs resourceLogs : request.getResourceLogsList()) {

        }
    }
}
