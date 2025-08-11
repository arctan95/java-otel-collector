package io.obhub.collector;

import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.logs.GlobalLoggerProvider;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static io.opentelemetry.semconv.ServiceAttributes.SERVICE_NAME;

@SpringBootApplication
public class Collector {

    private static final String KEY_OTEL_EXPORTER_OTLP_ENDPOINT = "OTEL_EXPORTER_OTLP_ENDPOINT";
    private static final String VALUE_SERVICE_NAME = "obhub-collector";
    private static final Logger logger = LoggerFactory.getLogger(Collector.class);

    public static void main(String[] args) {
        Resource resource = Resource.getDefault().toBuilder()
                .put(SERVICE_NAME, VALUE_SERVICE_NAME)
                .build();
        OpenTelemetrySdk openTelemetrySdk = initializeOpenTelemetry(resource);
        GlobalLoggerProvider.set(openTelemetrySdk.getSdkLoggerProvider());

        logger.info("Starting obhub collector...");

        SpringApplication app = new SpringApplication(Collector.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    private static SdkTracerProvider buildTracerProvider(Resource resource) {
        return SdkTracerProvider.builder()
                .setResource(resource)
                .setSampler(Sampler.alwaysOn())
                .addSpanProcessor(BatchSpanProcessor.builder(OtlpGrpcSpanExporter.builder()
                        .setEndpoint(System.getenv(KEY_OTEL_EXPORTER_OTLP_ENDPOINT)).build())
                        .build())
                .build();
    }

    private static SdkLoggerProvider buildLoggerProvider(Resource resource) {
        return SdkLoggerProvider.builder()
                .setResource(resource)
                .addLogRecordProcessor(BatchLogRecordProcessor.builder(
                                OtlpGrpcLogRecordExporter.builder()
                                        .setEndpoint(System.getenv(KEY_OTEL_EXPORTER_OTLP_ENDPOINT))
                                        .build())
                        .build())
                .build();
    }

    private static SdkMeterProvider buildMetricProvider(Resource resource) {

        return SdkMeterProvider.builder()
                .setResource(resource)
                .registerMetricReader(PeriodicMetricReader.builder(OtlpGrpcMetricExporter.builder()
                                .setEndpoint(VALUE_SERVICE_NAME).build())
                        .build())
                .build();
    }

    private static OpenTelemetrySdk initializeOpenTelemetry(Resource resource) {
        return OpenTelemetrySdk.builder()
//                .setMeterProvider(buildMetricProvider(resource))
                .setTracerProvider(buildTracerProvider(resource))
                .setLoggerProvider(buildLoggerProvider(resource))
                .setPropagators(ContextPropagators.create(W3CBaggagePropagator.getInstance()))
                .buildAndRegisterGlobal();
    }

}
