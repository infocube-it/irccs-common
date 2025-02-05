package org.hapi.devservice.deployment;

import io.quarkus.deployment.IsNormal;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.DevServicesResultBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.dev.devservices.GlobalDevServicesConfig;
import io.quarkus.runtime.LaunchMode;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** @noinspection ALL*/
public class HapiDevServicesProcessor {
    
    private static final String FEATURE = "hapi-devservice";

    private static final int HAPI_PORT = 8080;
    private static final String DEV_SERVICE_LABEL = "quarkus-dev-service-hapi";
    private static final ContainerLocator hapiDevModeContainerLocator = new ContainerLocator(DEV_SERVICE_LABEL, HAPI_PORT);
    static volatile DevServicesConfig capturedDevServicesConfiguration = new DevServicesConfig();

    @Inject
    Logger logger;

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep(onlyIfNot = IsNormal.class, onlyIf = GlobalDevServicesConfig.Enabled.class)
    DevServicesResultBuildItem createContainer(LaunchModeBuildItem launchMode) {
        // Check if a container with the same configuration already exists
        Optional<ContainerAddress> maybeContainerAddress = hapiDevModeContainerLocator.locateContainer(
                capturedDevServicesConfiguration.serviceName,
                capturedDevServicesConfiguration.shared,
                LaunchMode.current());

        logger.debug(capturedDevServicesConfiguration.serviceName);
        logger.debug(capturedDevServicesConfiguration.shared);

        if (maybeContainerAddress.isPresent()) {
            // Container exists, reuse it
            ContainerAddress containerAddress = maybeContainerAddress.get();
            Map<String, String> props = new HashMap<>();
            props.put("quarkus.hapi.devservices.url", getSharedContainerUrl(containerAddress));
            props.put("quarkus.hapi.devservices.port", String.valueOf(containerAddress.getPort()));

            return new DevServicesResultBuildItem.RunningDevService(FEATURE, containerAddress.getId(), null, props)
                    .toBuildItem();
        } else {
            // Container does not exist, start a new one
            HapiContainer hapi = new HapiContainer();
            // Configure the container with a fixed port if specified
            if (capturedDevServicesConfiguration.port != null && capturedDevServicesConfiguration.port.isPresent()) {
                hapi.withFixedPort(capturedDevServicesConfiguration.port.getAsInt());
            }
            // Start the container
            hapi.start();

            Map<String, String> props = new HashMap<>();
            props.put("quarkus.hapi.devservices.url", hapi.getServerUrl());
            props.put("quarkus.hapi.devservices.port", hapi.getPort().toString());

            return new DevServicesResultBuildItem.RunningDevService(FEATURE, hapi.getContainerId(),
                    hapi::close, props)
                    .toBuildItem();
        }
    }

    private String getSharedContainerUrl(ContainerAddress containerAddress) {
        return "http://" + ("0.0.0.0".equals(containerAddress.getHost()) ? "localhost" : containerAddress.getHost())
                + ":" + containerAddress.getPort();
    }
}
