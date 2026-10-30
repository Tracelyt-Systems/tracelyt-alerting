package io.tracelyt.alerting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstrap entry point for the Tracelyt Alerting subsystem.
 *
 * <p>In this architectural foundation phase, this class verifies application
 * context loading, configuration binding, and Actuator health endpoints.
 * Alert evaluation scheduling, threshold logic, and incident processing
 * are intentionally deferred to future implementation stages.
 */
@SpringBootApplication
public class AlertingApplication {

    /**
     * Main entry point for the application bootstrap.
     *
     * @param args command-line arguments passed at startup
     */
    public static void main(String[] args) {
        SpringApplication.run(AlertingApplication.class, args);
    }
}
