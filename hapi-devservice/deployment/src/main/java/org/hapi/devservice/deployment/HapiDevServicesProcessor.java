package org.hapi.devservice.deployment;

import io.quarkus.deployment.IsNormal;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.DevServicesResultBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.dev.devservices.GlobalDevServicesConfig;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;

/** @noinspection ALL*/
public class HapiDevServicesProcessor {

    private static final Logger LOG = Logger.getLogger(HapiDevServicesProcessor.class);

    private static final String FEATURE = "hapi-devservice";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep(onlyIfNot = IsNormal.class, onlyIf = GlobalDevServicesConfig.Enabled.class)
    DevServicesResultBuildItem createContainer(LaunchModeBuildItem launchMode) {
        HapiContainer hapi = new HapiContainer();
        hapi.start();

        Map<String, String> props = new HashMap<>();
        props.put("quarkus.hapi.devservices.url", hapi.getServerUrl());
        props.put("quarkus.hapi.devservices.port", hapi.getPort().toString());

        return new DevServicesResultBuildItem.RunningDevService(FEATURE, hapi.getContainerId(),
                hapi::close, props)
                .toBuildItem();
    }
}