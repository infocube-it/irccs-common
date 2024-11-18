package org.quarkus.irccs.annotations.models;


import org.hl7.fhir.r5.model.*;
import org.quarkus.irccs.client.restclient.FhirClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class Group {
    private String id;
    private String name;
    private List<String> members;
    private List<String> organizations;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<String> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<String> organizations) {
        this.organizations = organizations;
    }

    public static Group groupFromFhirGroup(org.hl7.fhir.r5.model.Group fhirGroup, FhirClient<?> fhirClient){
        Group group = new Group();
        if(fhirGroup.getIdentifier().size() > 0 && null != fhirGroup.getIdentifier().get(0).getValue()){
            group.setId(fhirGroup.getIdentifier().get(0).getValue());
        }
        group.setName(fhirGroup.getName());
        if(null != fhirGroup.getId()){
            Bundle includedResources = fhirClient.search("_include=*&_id=" + fhirGroup.getId());

            List<String> practitionerIds = fhirGroup.getMember().stream().map(member -> member.getEntity().getReference().replace("Practitioner/", "")).toList();
            List<String> organizationIds = fhirGroup.getCharacteristic().stream().map(characteristic -> characteristic.getValueReference().getReference().replace("Organization/", "")).toList();
            List<String> practitionerIdentifiers = includedResources.getEntry().stream().filter(x -> x.getResource().getResourceType().equals(ResourceType.Practitioner) && practitionerIds.contains(x.getResource().getIdPart())).toList().stream().map(practitioner -> ((Practitioner) practitioner.getResource()).getIdentifier().get(0).getValue()).toList();

            group.setMembers(practitionerIdentifiers);
            group.setOrganizations(organizationIds);
        }


        return group;
    }

    private static Map<String, String> extractExtension(DomainResource resource){
        return resource.getExtension().stream()
                .collect(Collectors.toMap(
                        Extension::getUrl,
                        extension -> extension.getValueStringType().toString()
                ));
    }

    @Override
    public String toString() {
        return "Group{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", members=" + members +
                ", organizations=" + organizations +
                '}';
    }
}
