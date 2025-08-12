package io.obhub.collector;

import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.logs.GlobalLoggerProvider;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporterBuilder;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporterBuilder;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporterBuilder;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static io.opentelemetry.semconv.ServiceAttributes.SERVICE_NAME;

@SpringBootApplication
public class Collector {

    private static final String KEY_OTEL_EXPORTER_OTLP_ENDPOINT = "OTEL_EXPORTER_OTLP_ENDPOINT";
    private static final String VALUE_SERVICE_NAME = "obhub-collector";

    public static void main(String[] args) {
        Resource resource = Resource.getDefault().toBuilder()
                .put(SERVICE_NAME, VALUE_SERVICE_NAME)
                .build();
        try (OpenTelemetrySdk openTelemetrySdk = initializeOpenTelemetry(resource)) {
            GlobalLoggerProvider.set(openTelemetrySdk.getSdkLoggerProvider());
        }

        SpringApplication app = new SpringApplication(Collector.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    private static SdkTracerProvider buildTracerProvider(Resource resource) {

        OtlpGrpcSpanExporterBuilder exporterBuilder = OtlpGrpcSpanExporter.builder();
        String endpoint = System.getenv(KEY_OTEL_EXPORTER_OTLP_ENDPOINT);
        if (endpoint != null && !endpoint.isBlank()) {
            exporterBuilder.setEndpoint(System.getenv(KEY_OTEL_EXPORTER_OTLP_ENDPOINT));
        }

        return SdkTracerProvider.builder()
                .setResource(resource)
                .setSampler(Sampler.alwaysOn())
                .addSpanProcessor(BatchSpanProcessor.builder(exporterBuilder.build())
                        .build())
                .build();
    }

    private static SdkLoggerProvider buildLoggerProvider(Resource resource) {
        OtlpGrpcLogRecordExporterBuilder exporterBuilder = OtlpGrpcLogRecordExporter.builder();
        String endpoint = System.getenv(KEY_OTEL_EXPORTER_OTLP_ENDPOINT);
        if (endpoint != null && !endpoint.isBlank()) {
            exporterBuilder.setEndpoint(endpoint);
        }

        return SdkLoggerProvider.builder()
                .setResource(resource)
                .addLogRecordProcessor(BatchLogRecordProcessor.builder(exporterBuilder.build())
                        .build())
                .build();
    }

    private static SdkMeterProvider buildMetricProvider(Resource resource) {

        OtlpGrpcMetricExporterBuilder exporterBuilder = OtlpGrpcMetricExporter.builder();
        String endpoint = System.getenv(KEY_OTEL_EXPORTER_OTLP_ENDPOINT);
        if (endpoint != null && !endpoint.isBlank()) {
            exporterBuilder.setEndpoint(endpoint);
        }


        return SdkMeterProvider.builder()
                .setResource(resource)
                .registerMetricReader(PeriodicMetricReader.builder(exporterBuilder.build())
                        .build())
                .build();
    }

    private static OpenTelemetrySdk initializeOpenTelemetry(Resource resource) {
        return OpenTelemetrySdk.builder()
                .setMeterProvider(buildMetricProvider(resource))
                .setTracerProvider(buildTracerProvider(resource))
                .setLoggerProvider(buildLoggerProvider(resource))
                .setPropagators(ContextPropagators.create(W3CBaggagePropagator.getInstance()))
                .buildAndRegisterGlobal();
    }

}
