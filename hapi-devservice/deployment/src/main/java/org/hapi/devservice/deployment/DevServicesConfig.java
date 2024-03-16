package org.hapi.devservice.deployment;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class DevServicesConfig {

    /**
     * Flag to enable (default) or disable Dev Services.
     *
     * When enabled, Dev Services for Keycloak automatically configures and starts Keycloak in Dev or Test mode, and when Docker
     * is running.
     */
    @ConfigItem(defaultValue = "true")
    public boolean enabled = true;

    /**
     * Determines if the Keycloak container is shared.
     *
     * When shared, Quarkus uses label-based service discovery to find and reuse a running Keycloak container, so a second one
     * is not started.
     * Otherwise, if a matching container is not is found, a new container is started.
     *
     * The service discovery uses the {@code quarkus-dev-service-label} label, whose value is set by the {@code service-name}
     * property.
     *
     * Container sharing is available only in dev mode.
     */
    @ConfigItem(defaultValue = "true")
    public boolean shared;

    /**
     * The value of the {@code quarkus-dev-service-keycloak} label attached to the started container.
     * This property is used when {@code shared} is set to {@code true}.
     * In this case, before starting a container, Dev Services for Keycloak looks for a container with the
     * {@code quarkus-dev-service-keycloak} label
     * set to the configured value. If found, it uses this container instead of starting a new one. Otherwise, it
     * starts a new container with the {@code quarkus-dev-service-keycloak} label set to the specified value.
     * <p>
     * Container sharing is only used in dev mode.
     */
    /**
     * The value of the `quarkus-dev-service-keycloak` label for identifying the Keycloak container.
     *
     * Used in shared mode to locate an existing container with this label. If not found, a new container is initialized with
     * this label.
     *
     * Applicable only in dev mode.
     */
    @ConfigItem(defaultValue = "quarkus-dev-service-hapi")
    public String serviceName;

    /**
     * The specific port for the dev service to listen on.
     * <p>
     * If not specified, a random port is selected.
     */
    @ConfigItem
    public OptionalInt port;

    /**
     * Environment variables to be passed to the container.
     */
    @ConfigItem
    public Map<String, String> containerEnv;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DevServicesConfig that = (DevServicesConfig) o;
        return enabled == that.enabled && shared == that.shared && Objects.equals(serviceName, that.serviceName) && Objects.equals(port, that.port) && Objects.equals(containerEnv, that.containerEnv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, shared, serviceName, port, containerEnv);
    }
}