package org.hapi.devservice.deployment;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerPort;
import io.quarkus.runtime.LaunchMode;
import org.testcontainers.DockerClientFactory;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiPredicate;

public class ContainerLocator {

    private static final BiPredicate<ContainerPort, Integer> hasMatchingPort = (containerPort,
                                                                                port) -> containerPort.getPrivatePort() != null &&
            containerPort.getPublicPort() != null &&
            containerPort.getPrivatePort().equals(port);

    private final String devServiceLabel;
    private final int port;

    public ContainerLocator(String devServiceLabel, int port) {
        this.devServiceLabel = devServiceLabel;
        this.port = port;
    }

    private Optional<Container> lookup(String expectedLabelValue) {
        return DockerClientFactory.lazyClient().listContainersCmd().exec().stream()
                .filter(container -> expectedLabelValue.equals(container.getLabels().get(devServiceLabel)))
                .findAny();
    }

    private Optional<ContainerPort> getMappedPort(Container container, int port) {
        return Arrays.stream(container.getPorts())
                .filter(containerPort -> hasMatchingPort.test(containerPort, port))
                .findAny();
    }

    public Optional<ContainerAddress> locateContainer(String serviceName, boolean shared, LaunchMode launchMode) {
        if (shared && launchMode == LaunchMode.DEVELOPMENT) {
            return lookup(serviceName)
                    .flatMap(container -> getMappedPort(container, port)
                            .flatMap(containerPort -> Optional.ofNullable(containerPort.getPublicPort())
                                    .map(port -> new ContainerAddress(
                                            container.getId(),
                                            DockerClientFactory.instance().dockerHostIpAddress(),
                                            containerPort.getPublicPort()))));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Integer> locatePublicPort(String serviceName, boolean shared, LaunchMode launchMode, int privatePort) {
        if (shared && launchMode == LaunchMode.DEVELOPMENT) {
            return lookup(serviceName)
                    .flatMap(container -> getMappedPort(container, privatePort))
                    .map(ContainerPort::getPublicPort);
        } else {
            return Optional.empty();
        }
    }
}