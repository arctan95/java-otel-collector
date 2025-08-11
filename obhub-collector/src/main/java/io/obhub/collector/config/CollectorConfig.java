package io.obhub.collector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CollectorConfig {

    @Value("${otel.exporter.otlp.endpoint}")
    private String otelExporterOtlpEndpoint;

    public String getOtelExporterOtlpEndpoint() {
        return otelExporterOtlpEndpoint;
    }
}
