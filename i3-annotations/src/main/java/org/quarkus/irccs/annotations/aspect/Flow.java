package org.quarkus.irccs.annotations.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.HttpHeaders;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r5.model.Extension;
import org.hl7.fhir.r5.model.StringType;
import org.jboss.logging.Logger;
import org.quarkus.irccs.annotations.models.clients.AuthMicroserviceClient;
import org.quarkus.irccs.client.restclient.FhirClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Flow {

    HttpHeaders httpHeaders;
    FhirClient<?> fhirClient;
    InvocationContext context;
    AuthMicroserviceClient authClient;
    JsonWebToken jwt;

    @Inject
    Logger logger;
    public Flow(FhirClient<?> fhirClient, InvocationContext context, AuthMicroserviceClient authClient, JsonWebToken jwt, HttpHeaders httpHeaders) {
        this.fhirClient = fhirClient;
        this.context = context;
        this.authClient = authClient;
        this.jwt = jwt;
        this.httpHeaders = httpHeaders;
    }

    public String apply() throws Exception {
    // The flow is applied as follows
    // We retrieve the method used, and an implementation of it based off of the ResourceType will be called
        String methodName = context.getMethod().getName().toLowerCase();

    // Here we follow different flows based on the fhir request's method
        return switch (methodName) {
            case "create" -> create();
            case "read" -> read();
            case "update" -> update();
            case "search" -> search();
            case "search_internal" -> search_internal();
            case "delete" -> delete();
            default -> (String) context.proceed();
        };

    }

    public String create() throws Exception {
        Class<? extends IBaseResource> resourceType = fhirClient.getResourceType();
        IBaseResource resource = fhirClient.parseResource(resourceType, context.getParameters()[0].toString());
        String organizationId;
        if(null != this.jwt.getClaim("organizations-id")){
            organizationId = new ObjectMapper().readValue((new ObjectMapper()).readValue(this.jwt.getClaim("organizations-id").toString(), List.class).get(0).toString().replace("=", ":"), Map.class).get("keycloakId").toString();
        } else {
            organizationId = httpHeaders.getHeaderString("organizationId");
        }

        List<String> groupsIds = getGroupsIds();
        List<Extension> extensions = new ArrayList<>();
        extensions.add(new Extension("group_ids", new StringType("ac1041bb-731f-452f-92c5-e549752af05b")));
        extensions.add(new Extension("group_ids", new StringType(organizationId)));
        extensions.add(new Extension("organization_id", new StringType(organizationId)));
        List<String> roles = (List<String>) new ObjectMapper().readValue(this.jwt.getClaim("realm_access").toString(), Map.class).get("roles");
        if(roles != null && roles.contains("datamanager")){
            extensions.add(new Extension("group_ids", new StringType(organizationId + "_DM")));
        }

        groupsIds.forEach(groupId ->
                extensions.add(new Extension("group_ids", new StringType(groupId)))
        );
        resourceType.getMethod("setExtension", List.class).invoke(resource, extensions);
        context.getParameters()[0] = fhirClient.encodeResourceToString(resource);
        return context.getParameters()[0].toString();
    };
    public String read() throws Exception {
        return (String) context.proceed();
    };
    public String update() throws Exception {
        return (String) context.proceed();
    };
    public String search() throws Exception {
        return (String) context.proceed();
    };
    public String search_internal() throws Exception {
        List<String> roles = null;
        try {
            Map<String, Object> realmAccess = new ObjectMapper().readValue(this.jwt.getClaim("realm_access").toString(),Map.class);
            if (realmAccess != null) {
                roles = (List<String>) realmAccess.get("roles");
            }
        } catch (Exception e) {
            throw new Exception("Error parsing realm_access roles", e);
        }

        boolean hasAdminRole = false;
        try {
            Map<String, Object> resourceAccess = new ObjectMapper().readValue(this.jwt.getClaim("resource_access").toString(),Map.class);
            if (resourceAccess != null) {
                Map<String, Object> irccs = (Map<String, Object>) resourceAccess.get("irccs");
                if (irccs != null) {
                    List<String> resourceRoles = (List<String>) irccs.get("roles");
                    if (resourceRoles != null && resourceRoles.contains("admin") && !roles.contains("orgadmin")) {
                        hasAdminRole = true;
                    }
                }
            }
        } catch (Exception e) {}

        if (hasAdminRole) {
            return (String) this.context.proceed();
        }

        List<String> groupIds = new ArrayList<>();
        String organizationId = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> organizationIds = objectMapper.readValue(this.jwt.getClaim("organizations-id").toString(), List.class);
            if (organizationIds != null && !organizationIds.isEmpty()) {
                organizationId = organizationIds.get(0).replace("=", ":");
                Map<String, Object> orgInfo = objectMapper.readValue(organizationId, Map.class);
                organizationId = (String) orgInfo.get("keycloakId");
            }
        } catch (Exception e) {
            throw new Exception("Error parsing organizations-id claim", e);
        }

        Map<String, List<String>> params = deepCopy((Map<String, List<String>>) this.context.getParameters()[0]);

        if (roles != null && roles.contains("orgadmin")) {
            if (organizationId != null) {
                groupIds.add(organizationId);
            }
        } else if (organizationId != null) {
            groupIds.addAll(this.getGroupsIds());
            groupIds.add(organizationId + "_DM");
        }

        if (!groupIds.isEmpty()) {
            String groupIdFilter = groupIds.stream()
                    .map(groupId -> "\"" + groupId + "\"")
                    .collect(Collectors.joining(" or group-id eq ", "group-id eq ", ""));

            groupIds.add(organizationId);

            String identifierFilter = groupIds.stream()
                    .map(groupId -> "\"" + groupId + "\"")
                    .collect(Collectors.joining(" or identifier eq ", "identifier eq ", ""));

            String filter = String.join(" or ", groupIdFilter, identifierFilter);

            params.put("_filter", List.of(filter));
        }

        this.context.setParameters(new Object[]{params});
        return (String) this.context.proceed();
    }
    public String delete() throws Exception {
        return (String) context.proceed();
    };


    public final void execute() {
        try {
            apply();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }

    }

    private List<String> getGroupsIds() throws JsonProcessingException {
        Class<? extends IBaseResource> resourceType = fhirClient.getResourceType();
        String resourceName = resourceType.getSimpleName().toLowerCase();
        String method = context.getMethod().getName();
        List<String> groupsIds = new ArrayList<>();
        if(null != this.jwt.getClaim("groupsId")){
            HashMap<String, HashMap<String, List<String>>> groupsId = new ObjectMapper().readValue(jwt.getClaim("groupsId").toString(), HashMap.class);
            groupsIds = new ArrayList<>(groupsId.entrySet().stream()
                    .filter(entry -> entry.getValue().containsKey(resourceName))
                    .filter(entry -> entry.getValue().get(resourceName).contains(method.contains("search") ? "search" : method))
                    .map(Map.Entry::getKey).toList());
        }
        return groupsIds;
    }

    public static Map<String, List<String>> deepCopy(Map<String, List<String>> original) {
        Map<String, List<String>> copy = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : original.entrySet()) {
            List<String> originalList = entry.getValue();
            List<String> copiedList = new ArrayList<>(originalList);
            copy.put(entry.getKey(), copiedList);
        }
        return copy;
    }

}