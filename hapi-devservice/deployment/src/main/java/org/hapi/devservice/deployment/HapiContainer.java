package org.hapi.devservice.deployment;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.time.Duration;

public class HapiContainer extends GenericContainer<HapiContainer> {

    private static final String HAPI_VERSION = ConfigProvider.getConfig().getOptionalValue("quarkus.hapi.devservices.version", String.class).orElse("6.10.1");
    private static final String HAPI_CONFIG_PATH = ConfigProvider.getConfig().getOptionalValue("quarkus.hapi.devservices.config-path", String.class).orElse("app-config.yaml");

    private Integer fixedPort;

    private static String getHapiImageName() {
        return "hapiproject/hapi:v" + HAPI_VERSION;
    }

    public HapiContainer() {
        this(DockerImageName.parse(getHapiImageName()));
    }

    /*Non utilizzato.Per i test viene creato un network proprio*/
    public static Network networkHapi = new Network() {
        @Override
        public String getId() {
            return "Qui va l'id del network irccs-docker-network";
        }

        @Override
        public void close() {

        }

        @Override
        public Statement apply(Statement statement, Description description) {
            return null;
        }
    };
    public HapiContainer(DockerImageName hapiImageName) {
        super(hapiImageName);
        this.withCopyFileToContainer(MountableFile.forClasspathResource(HAPI_CONFIG_PATH, 484), "/data/hapi/app-config.yaml");
        this.withEnv("SPRING_CONFIG_LOCATION", "file:///data/hapi/app-config.yaml");
        this.withNetwork(Network.SHARED); //non è necessario, è possibile creare un nuovo network e passargli l'id del network esistente nello stack se vogliamo
        this.withExposedPorts(8080);
        this.withNetworkAliases("hapi");
    }

    public HapiContainer withFixedPort(int fixedPort) {
        this.fixedPort = fixedPort;
        return this;
    }

    @Override
    protected void doStart() {
        if (fixedPort != null) {
            this.addFixedExposedPort(fixedPort, 8080);
        }
        this.waitingFor(Wait.forLogMessage(".*expired searches.*", 1))
                .withStartupTimeout(Duration.ofMinutes(6));
        super.doStart();
    }

    public Integer getPort() {
        return fixedPort != null ? fixedPort : this.getMappedPort(8080);
    }

    public String getServerUrl() {
        return String.format("http://%s:%d/fhir", this.getHost(), this.getPort());
    }
}
