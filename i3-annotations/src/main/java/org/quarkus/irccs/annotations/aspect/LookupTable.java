package org.quarkus.irccs.annotations.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r5.model.Extension;
import org.hl7.fhir.r5.model.Identifier;
import org.hl7.fhir.r5.model.Practitioner;
import org.hl7.fhir.r5.model.StringType;
import org.quarkus.irccs.annotations.models.AuthMicroserviceClient;
import org.quarkus.irccs.annotations.models.Group;
import org.quarkus.irccs.annotations.models.User;
import org.quarkus.irccs.client.controllers.GenericController;
import org.quarkus.irccs.client.restclient.FhirClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiFunction;

import static io.quarkus.arc.ComponentsProvider.LOG;

@ApplicationScoped
@SuppressWarnings("unchecked")
public class LookupTable {

    private final static Logger LOG = LoggerFactory.getLogger(LookupTable.class);

    private final AuthMicroserviceClient authClient;
    private final JsonWebToken jwt;
    private final Map<String, BiFunction<FhirClient<?>, InvocationContext, Object>> lookupTable = new HashMap<>();

    private String psw = null;
    private String orgReq = null;
    private String structure = null;
    private String unitName = null;
    private String role = null;

    private String identifier = null;

    @Inject
    public LookupTable(@RestClient AuthMicroserviceClient authClient, JsonWebToken jwt) {
        this.authClient = authClient;
        this.jwt = jwt;
    }

    protected String intercept(FhirClient<?> fhirClient, InvocationContext context) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
            getOrSetGroupIds(fhirClient, context);

        if(context.getMethod().getName().equals("delete") && (fhirClient.getResourceType().equals(org.hl7.fhir.r5.model.Group.class) || fhirClient.getResourceType().equals(Practitioner.class)) ){
            LOG.debug(Arrays.toString(context.getParameters()));
            List<Identifier> identifiers = (List<Identifier>) fhirClient.getResourceType().getMethod("getIdentifier").invoke(fhirClient.read((String) context.getParameters()[0]));
            if(identifiers.size() > 0){
                identifier = identifiers.get(0).getValue();
            }
        }

        try {
            return syncAuth(checkAccess((String) context.proceed(), fhirClient, context), fhirClient, context);
        } catch (ForbiddenException e) {
            LOG.debug(e.getMessage());
            throw new ForbiddenException();
        } catch (Exception e){
            LOG.debug(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String syncAuth(String payload, FhirClient<?> fhirClient, InvocationContext context) {
        if(!(context.getMethod().getName().equals("create") || context.getMethod().getName().equals("update") || context.getMethod().getName().equals("delete"))) return payload;
        if(fhirClient.getResourceType().equals(org.hl7.fhir.r5.model.Group.class)){
            if(context.getMethod().getName().equals("delete")){
                LOG.info("Delete Request for a Group... ");
                if(identifier != null){
                    LOG.info("Asking to delete Keycloak Group...");
                    authClient.deleteGroup("Bearer " + jwt.getRawToken(), identifier);
                }
                return payload;
            }
            org.hl7.fhir.r5.model.Group fhirGroup = (org.hl7.fhir.r5.model.Group) fhirClient.parseResource(fhirClient.getResourceType(), payload);
            if(!fhirGroup.getType().equals(org.hl7.fhir.r5.model.Group.GroupType.PRACTITIONER)) return payload;
            Group group = Group.groupFromFhirGroup(fhirGroup, fhirClient);
            if(null == group.getId()){
                Group authGroup = authClient.createGroup("Bearer " + jwt.getRawToken(), group).readEntity(Group.class);
                return fhirClient.encodeResourceToString(addIdentifierIdGroup(authGroup, fhirGroup, (FhirClient<org.hl7.fhir.r5.model.Group>) fhirClient));
            } else {
                Group authGroup = authClient.updateGroup("Bearer " + jwt.getRawToken(), group).readEntity(Group.class);
                return fhirClient.encodeResourceToString(addIdentifierIdGroup(authGroup, fhirGroup, (FhirClient<org.hl7.fhir.r5.model.Group>) fhirClient));
            }
        }

        if(fhirClient.getResourceType().equals(Practitioner.class)){

            if(context.getMethod().getName().equals("delete")){
                LOG.info("Delete Request for a Practitioner... ");
                if(identifier != null){
                    LOG.info("Asking to delete Keycloak Practitioner...");
                    authClient.deleteUser("Bearer " + jwt.getRawToken(), identifier);
                }
                return payload;
            }

            Practitioner practitioner = (Practitioner) fhirClient.parseResource(fhirClient.getResourceType(), payload);
                User user = User.fromPractitioner(practitioner, this.psw, this.orgReq, this.unitName, this.role, this.structure);
                if (null == user.getId()) {
                    User authUser = authClient.createUser("Bearer " + jwt.getRawToken(), user).readEntity(User.class);
                    return fhirClient.encodeResourceToString(addIdentifierIdUser(authUser, practitioner, (FhirClient<Practitioner>) fhirClient));
                } else {
                    User authUser = authClient.updateUser("Bearer " + jwt.getRawToken(), user).readEntity(User.class);
                    return fhirClient.encodeResourceToString(addIdentifierIdUser(authUser, practitioner, (FhirClient<Practitioner>) fhirClient));
                }
        }
        return payload;
    }

    private org.hl7.fhir.r5.model.Group addIdentifierIdGroup(Group authGroup, org.hl7.fhir.r5.model.Group fhirGroup, FhirClient<org.hl7.fhir.r5.model.Group> fhirClient) {
        fhirGroup.setIdentifier(List.of(new Identifier()
                .setUse(Identifier.IdentifierUse.SECONDARY)
                .setValue(authGroup.getId())));
        return fhirClient.update(fhirGroup.getId(), fhirGroup);
    }

    private Practitioner addIdentifierIdUser(User user, Practitioner practitioner, FhirClient<org.hl7.fhir.r5.model.Practitioner> fhirClient) {
        practitioner.setIdentifier(List.of(new Identifier()
                .setUse(Identifier.IdentifierUse.SECONDARY)
                .setValue(user.getId())));
        return fhirClient.update(practitioner.getId(), practitioner);
    }

    /*   private void initializeLookupTable() {
           lookupTable.put("Practitioner.create", (fhirClient, context) -> {
               Class<? extends IBaseResource> resourceType = fhirClient.getResourceType();
               if (resourceType.equals(Practitioner.class)) {
                   String payload = (String) context.getParameters()[0];
                   Practitioner resource = (Practitioner) fhirClient.parseResource(resourceType, payload);
                   User user = createUserFromPractitioner(resource);
                   resource.setIdentifier(List.of(new Identifier()
                           .setUse(Identifier.IdentifierUse.SECONDARY)
                           .setValue(user.getId())));
                   List<Extension> extensions = new ArrayList<>();
                   HashMap<String, String> map = getJwtGroupsId();
                   map.put("admin", "ac1041bb-731f-452f-92c5-e549752af05b");
                   for (String key : map.keySet()) {
                       Extension extension = new Extension();
                       extension.setUrl(key);
                       extension.setValue(new StringType().setValue(map.get(key)));
                       extensions.add(extension);
                   }

                   resource.setExtension(extensions);
                   Object[] newParams = new Object[1];
                   newParams[0] = fhirClient.encodeResourceToString(resource);
                   context.setParameters(newParams);
                   String practitionerRes;
                   try {
                       practitionerRes = (String) context.proceed();
                   } catch (Exception e) {
                       throw new RuntimeException(e);
                   }
                   return practitionerRes;
               }
               throw new IllegalArgumentException("Unsupported resource type: " + resourceType.getSimpleName());
           });
           lookupTable.put("Practitioner.search_Internal", (fhirClient, context) -> {
               Class<? extends IBaseResource> resourceType = fhirClient.getResourceType();
               if (resourceType.equals(Practitioner.class)) {
                   try {
                       Map<String, List<String>> params = deepCopy((Map<String, List<String>>) context.getParameters()[0]);
                       params.put("group-id:exact", Collections.singletonList(String.join(",", getJwtGroupsId().values())));
                       Object[] obj = new Object[1];
                       obj[0] = params;
                       context.setParameters(obj);
                     return context.proceed();
                   } catch (Exception e) {
                       throw new RuntimeException(e);
                   }
               }
               throw new IllegalArgumentException("Unsupported resource type: " + resourceType.getSimpleName());
           });
           lookupTable.put("Practitioner.update", (fhirClient, context) -> {
               Class<? extends IBaseResource> resourceType = fhirClient.getResourceType();
               if (resourceType.equals(Practitioner.class)) {
                   String payload = (String) context.getParameters()[1];
                   Practitioner resource = (Practitioner) fhirClient.parseResource(resourceType, payload);
                   User user = updateUserFromPractitioner(resource);
                   resource.setIdentifier(List.of(new Identifier()
                           .setUse(Identifier.IdentifierUse.SECONDARY)
                           .setValue(user.getId())));
                   List<Extension> extensions = new ArrayList<>();
                   HashMap<String, String> map = getJwtGroupsId();
                   map.put("admin", "ac1041bb-731f-452f-92c5-e549752af05b");
                   for (String key : map.keySet()) {
                       Extension extension = new Extension();
                       extension.setUrl(key);
                       extension.setValue(new StringType().setValue(map.get(key)));
                       extensions.add(extension);
                   }

                   resource.setExtension(extensions);
                   Object[] newParams = new Object[1];
                   newParams[0] = fhirClient.encodeResourceToString(resource);
                   context.setParameters(newParams);
                   String practitionerRes;
                   try {
                       practitionerRes = (String) context.proceed();
                   } catch (Exception e) {
                       throw new RuntimeException(e);
                   }
                   return practitionerRes;
               }
               throw new IllegalArgumentException("Unsupported resource type: " + resourceType.getSimpleName());
           });
           lookupTable.put("Group.create", (fhirClient, context) -> {
               Class<? extends IBaseResource> resourceType = fhirClient.getResourceType();
               if (resourceType.equals(org.hl7.fhir.r5.model.Group.class)) {
                   String payload = (String) context.getParameters()[0];
                   org.hl7.fhir.r5.model.Group resource = (org.hl7.fhir.r5.model.Group) fhirClient.parseResource(resourceType, payload);
                   Group group = createGroupFromFhirGroup(resource, fhirClient);
                   resource.setIdentifier(List.of(new Identifier()
                           .setUse(Identifier.IdentifierUse.SECONDARY)
                           .setValue(group.getId())));
                   List<Extension> extensions = new ArrayList<>();
                   HashMap<String, String> map = getJwtGroupsId();
                   map.put("admin", "ac1041bb-731f-452f-92c5-e549752af05b");
                   for (String key : map.keySet()) {
                       Extension extension = new Extension();
                       extension.setUrl(key);
                       extension.setValue(new StringType().setValue(map.get(key)));
                       extensions.add(extension);
                   }
                   resource.setExtension(extensions);
                   Object[] newParams = new Object[1];
                   newParams[0] = fhirClient.encodeResourceToString(resource);
                   context.setParameters(newParams);
                   String groupRes;
                   try {
                       groupRes = (String) context.proceed();
                   } catch (Exception e) {
                       throw new RuntimeException(e);
                   }
                   return groupRes;
               }
               throw new IllegalArgumentException("Unsupported resource type: " + resourceType.getSimpleName());
           });
           lookupTable.put("Group.update", (fhirClient, context) -> {
               Class<? extends IBaseResource> resourceType = fhirClient.getResourceType();
               if (resourceType.equals(org.hl7.fhir.r5.model.Group.class)) {
                   String payload = (String) context.getParameters()[1];
                   org.hl7.fhir.r5.model.Group resource = (org.hl7.fhir.r5.model.Group) fhirClient.parseResource(resourceType, payload);
                   resource.setId((String) context.getParameters()[0]);
                   Group group = updateGroupfromFhirGroup(resource, fhirClient, (String) context.getParameters()[0]);
                   resource.setIdentifier(List.of(new Identifier()
                           .setUse(Identifier.IdentifierUse.SECONDARY)
                           .setValue(group.getId())));
                   List<Extension> extensions = new ArrayList<>();
                   HashMap<String, String> map = getJwtGroupsId();
                   map.put("admin", "ac1041bb-731f-452f-92c5-e549752af05b");
                   for (String key : map.keySet()) {
                       Extension extension = new Extension();
                       extension.setUrl(key);
                       extension.setValue(new StringType().setValue(map.get(key)));
                       extensions.add(extension);
                   }
                   resource.setExtension(extensions);
                   Object[] newParams = new Object[1];
                   newParams[0] = fhirClient.encodeResourceToString(resource);
                   context.setParameters(newParams);
                   String groupRes;
                   try {
                       groupRes = (String) context.proceed();
                   } catch (Exception e) {
                       throw new RuntimeException(e);
                   }
                   return groupRes;
               }
               throw new IllegalArgumentException("Unsupported resource type: " + resourceType.getSimpleName());
           });
           lookupTable.put("Practitioner.searchPath_internal", (fhirClient, context) -> {
               Class<? extends IBaseResource> resourceType = fhirClient.getResourceType();
               if (resourceType.equals(Practitioner.class)) {
                   try {
                       Map<String, List<String>> params = deepCopy((Map<String, List<String>>) context.getParameters()[0]);
                       params.put("group-id:exact", Collections.singletonList(String.join(",", getJwtGroupsId().values())));
                       Object[] obj = new Object[1];
                       obj[0] = params;
                       context.setParameters(obj);
                       return context.proceed();
                   } catch (Exception e) {
                       throw new RuntimeException(e);
                   }
               }
               throw new IllegalArgumentException("Unsupported resource type: " + resourceType.getSimpleName());
           });
           lookupTable.put("Practitioner.delete", (fhirClient, context) -> {
               Class<? extends IBaseResource> resourceType = fhirClient.getResourceType();
               if (resourceType.equals(Practitioner.class)) {
                   try {
                       String payload = (String) context.getParameters()[1];
                       Practitioner resource = (Practitioner) fhirClient.parseResource(resourceType, payload);
                       deleteUserFromPractitioner(resource);
                       return context.proceed();
                   } catch (Exception e) {
                       throw new RuntimeException(e);
                   }
               }
               throw new IllegalArgumentException("Unsupported resource type: " + resourceType.getSimpleName());
           });
           lookupTable.put("*.create", (fhirClient, context) -> {
               Class<? extends IBaseResource> resourceType = fhirClient.getResourceType();
               String payload = (String) context.getParameters()[0];
               IBaseResource resource = fhirClient.parseResource(resourceType, payload);
               if (resourceType.isInstance(resource)) {
                   @SuppressWarnings("unchecked")
                   Class<? extends IBaseResource> actualClass = (Class<? extends IBaseResource>) resourceType;
                   try {
                       List<Extension> extensions = new ArrayList<>();
                       HashMap<String, String> map = getJwtGroupsId();
                       map.put("admin", "ac1041bb-731f-452f-92c5-e549752af05b");
                       for (String key : map.keySet()) {
                           Extension extension = new Extension();
                           extension.setUrl(key);
                           extension.setValue(new StringType().setValue(map.get(key)));
                           extensions.add(extension);
                       }
                       Method setExtensionMethod = actualClass.getMethod("setExtension", List.class);
                       setExtensionMethod.invoke(resource, extensions);
                       Object[] newParams = new Object[1];
                       newParams[0] = fhirClient.encodeResourceToString(resource);
                       context.setParameters(newParams);
                       String res;
                       try {
                           res = (String) context.proceed();
                       } catch (Exception e) {
                           throw new RuntimeException(e);
                       }
                       return res;
                   } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                       throw new RuntimeException(e);
                   }
               }
               throw new IllegalArgumentException("Unsupported resource type: " + resourceType.getSimpleName());
           });
           lookupTable.put("*.update", (fhirClient, context) -> {
               Class<? extends IBaseResource> resourceType = fhirClient.getResourceType();
               String payload = (String) context.getParameters()[1];
               IBaseResource resource = fhirClient.parseResource(resourceType, payload);
               if (resourceType.isInstance(resource)) {
                   @SuppressWarnings("unchecked")
                   Class<? extends IBaseResource> actualClass = (Class<? extends IBaseResource>) resourceType;
                   try {
                       List<Extension> extensions = new ArrayList<>();
                       HashMap<String, String> map = getJwtGroupsId();
                       map.put("admin", "ac1041bb-731f-452f-92c5-e549752af05b");
                       for (String key : map.keySet()) {
                           Extension extension = new Extension();
                           extension.setUrl(key);
                           extension.setValue(new StringType().setValue(map.get(key)));
                           extensions.add(extension);
                       }
                       Method setExtensionMethod = actualClass.getMethod("setExtension", List.class);
                       setExtensionMethod.invoke(resource, extensions);
                       Object[] newParams = new Object[1];
                       newParams[0] = fhirClient.encodeResourceToString(resource);
                       context.setParameters(newParams);
                       String res;
                       try {
                           res = (String) context.proceed();
                       } catch (Exception e) {
                           throw new RuntimeException(e);
                       }
                       return res;
                   } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                       throw new RuntimeException(e);
                   }
               }
               throw new IllegalArgumentException("Unsupported resource type: " + resourceType.getSimpleName());
           });
           lookupTable.put("*.search_Internal", (fhirClient, context) -> {
               Map<String, List<String>> params = deepCopy((Map<String, List<String>>) context.getParameters()[0]);
               params.put("group-id:exact", Collections.singletonList(String.join(",", getJwtGroupsId().values())));
               Object[] obj = new Object[1];
               obj[0] = params;
               context.setParameters(obj);
               String res;
               try {
                   res = (String) context.proceed();
               } catch (Exception e) {
                   throw new RuntimeException(e);
               }
               return res;
           });
           lookupTable.put("*.searchPath_internal", (fhirClient, context) -> {
                       Map<String, List<String>> params = deepCopy((Map<String, List<String>>) context.getParameters()[0]);
                       params.put("group-id:exact", Collections.singletonList(String.join(",", getJwtGroupsId().values())));
                       Object[] obj = new Object[1];
                       obj[0] = params;
                       context.setParameters(obj);
               String res;
               try {
                   res = (String) context.proceed();
               } catch (Exception e) {
                   throw new RuntimeException(e);
               }
               return res;
           });
       }
   */
    public Object processInvocation(InvocationContext context) throws Exception {
        String key = generateKey(context);
        BiFunction<FhirClient<?>, InvocationContext, Object> methodToCall = lookupTable.get(key);
        if (methodToCall == null) {
            return context.proceed();
        }
        FhirClient<?> fhirClient = ((GenericController<?>) context.getTarget()).fhirClient;
        return methodToCall.apply(fhirClient, context);
    }

    private String generateKey(InvocationContext context) {
        String resourceTypeName = ((GenericController<?>) context.getTarget()).fhirClient.getResourceType().getSimpleName();
        String methodName = context.getMethod().getName();
        return (resourceTypeName.equals("Practitioner") ? "Practitioner" : resourceTypeName.equals("Group") ? "Group" : "*") + "." + methodName;
    }

    private void getOrSetGroupIds(FhirClient<?> fhirClient, InvocationContext context){
        try {
            Class<? extends IBaseResource> resourceType = fhirClient.getResourceType();
            String resourceName = resourceType.getSimpleName().toLowerCase();
            String method = context.getMethod().getName();
            Object[] newParams = null;
            boolean isAdmin = false;
            try{
                isAdmin = ((ArrayList<String>)((HashMap<?,?>) new ObjectMapper().readValue(jwt.getClaim("resource_access").toString(), HashMap.class).get("irccs")).get("roles")).contains("admin");
            } catch (NullPointerException e) {
                LOG.info("Not ADMIN request.");
            }

            List<String> groupsIds = new ArrayList<>();
            if(jwt.containsClaim("groupsId")){
                HashMap<String, HashMap<String, List<String>>> groupsId = new ObjectMapper().readValue(jwt.getClaim("groupsId").toString(), HashMap.class);
                 groupsIds = new ArrayList<>(groupsId.entrySet().stream()
                        .filter(entry -> entry.getValue().containsKey(resourceName))
                        .filter(entry -> entry.getValue().get(resourceName).contains(method.contains("search") ? "search" : method))
                        .map(Map.Entry::getKey).toList());
            }

            if(method.equals("create") || method.equals("update")){
                String payload = method.equals("update") ? (String) context.getParameters()[1] : (String) context.getParameters()[0] ;
                IBaseResource resource = fhirClient.parseResource(resourceType, payload);
                groupsIds.add("ac1041bb-731f-452f-92c5-e549752af05b");
                List<Extension> extensionList = (List<Extension>) resourceType.getMethod("getExtensionsByUrl", String.class).invoke(resource, "password");
                List<Extension> extensions = new ArrayList<>();
                if(extensionList.size() > 0){
                    this.psw = extensionList.get(0).getValueStringType().asStringValue();
                    extensions.add(extensionList.get(0));
                }
                List<Extension> orgReqExt = (List<Extension>) resourceType.getMethod("getExtensionsByUrl", String.class).invoke(resource, "organizationRequest");
                if(orgReqExt.size() > 0){
                    this.orgReq = orgReqExt.get(0).getValueStringType().asStringValue();
                    extensions.add(orgReqExt.get(0));
                }
                List<Extension> roleExt = (List<Extension>) resourceType.getMethod("getExtensionsByUrl", String.class).invoke(resource, "role");
                LOG.info("role from Extensions: " + roleExt);
                if(roleExt.size() > 0){
                    this.role = roleExt.get(0).getValueStringType().asStringValue();
                    extensions.add(roleExt.get(0));
                }
                List<Extension> structureExt = (List<Extension>) resourceType.getMethod("getExtensionsByUrl", String.class).invoke(resource, "structure");
                LOG.info("structure from Extensions: " + structureExt);
                if(structureExt.size() > 0){
                    this.structure = structureExt.get(0).getValueStringType().asStringValue();
                    extensions.add(structureExt.get(0));
                }
                List<Extension> unitNameExt = (List<Extension>) resourceType.getMethod("getExtensionsByUrl", String.class).invoke(resource, "unitName");
                LOG.info("unitName from Extensions: " + unitNameExt);
                if(unitNameExt.size() > 0){
                    this.unitName = unitNameExt.get(0).getValueStringType().asStringValue();
                    extensions.add(unitNameExt.get(0));
                }
                for (int i = 0; i < groupsIds.size(); i++) {
                    Extension extension = new Extension();
                    extension.setUrl("group_ids");
                    extension.setValue(new StringType(groupsIds.get(i)));
                    extensions.add(extension);
                }
                newParams = null;
                if(method.equals("create")){
                    resourceType.getMethod("setExtension", List.class).invoke(resource, extensions);
                    newParams = new Object[1];
                    newParams[0] = fhirClient.encodeResourceToString(resource);
                } else {
                    if(!isAdmin){
                        resourceType.getMethod("setExtension", List.class).invoke(resource, extensions);
                    }
                    newParams = new Object[2];
                    newParams[0] = context.getParameters()[0];
                    newParams[1] = fhirClient.encodeResourceToString(resource);
                }
            } else if((method.equals("search_Internal") || method.equals("searchPath_Internal") || method.contains("history")) && !isAdmin) {
                String practitionerId = jwt.getClaim("sub");
                Map<String, List<String>> params = deepCopy((Map<String, List<String>>) context.getParameters()[0]);
                List<String> param = new ArrayList<>();
                param.add(String.join(" or group-id eq ", groupsIds.stream().map(x -> '"' + x + '"').toList()));
                param.set(0, "group-id eq " + param.get(0));
                if(!practitionerId.isEmpty()){
                    param.set(0, param.get(0) + " or identifier eq " + practitionerId);
                }
                params.put("_filter", param);
                System.out.println(params);
                newParams = new Object[1];
                newParams[0] = params;
            }

            if(null != newParams){
                context.setParameters(newParams);
            }
        } catch (
                JsonProcessingException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private String checkAccess(String response, FhirClient<?> fhirClient, InvocationContext context) throws JsonProcessingException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(!context.getMethod().getName().equals("read")) return response;
        Class<? extends IBaseResource> resourceType = fhirClient.getResourceType();
        String resourceName = resourceType.getSimpleName().toLowerCase();
        String method = context.getMethod().getName();
        HashMap<String, HashMap<String, List<String>>> groupsId = new ObjectMapper().readValue(jwt.getClaim("groupsId").toString(), HashMap.class);
        String practitionerId = jwt.getClaim("sub");
        List<String> groupsIds = new ArrayList<>(groupsId.entrySet().stream()
                .filter(entry -> entry.getValue().containsKey(resourceName))
                .filter(entry -> entry.getValue().get(resourceName).contains(method.contains("search") ? "search" : method))
                .map(Map.Entry::getKey).toList());
        boolean isAdmin = false;
        try{
            isAdmin = ((ArrayList<String>)((HashMap<?,?>) new ObjectMapper().readValue(jwt.getClaim("resource_access").toString(), HashMap.class).get("irccs")).get("roles")).contains("admin");
        } catch (NullPointerException e) {
            LOG.info("Not ADMIN request.");
        }

        IBaseResource resource = fhirClient.parseResource(resourceType, response);
        List<Extension> extensions = (List<Extension>) resourceType.getMethod("getExtensionsByUrl", String.class).invoke(resource, "group_ids");
        String identifier = null;
        try {
            identifier = ((Identifier)((List) resourceType.getMethod("getIdentifier").invoke(resource)).get(0)).getValue();
        } catch (Exception e){
            LOG.info("No identifier on the resource.");
        }

        boolean isGranted = extensions.stream().map(x -> x.getValueStringType().toString()).anyMatch(value -> groupsIds.stream().anyMatch(value::contains)) || (identifier != null && identifier.equals(practitionerId)) || isAdmin;

        if(!isGranted){
            throw new ForbiddenException();
        }

        return response;
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