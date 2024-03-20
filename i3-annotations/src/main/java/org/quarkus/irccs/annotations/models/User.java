package org.quarkus.irccs.annotations.models;


import org.hl7.fhir.r5.model.ContactPoint;
import org.hl7.fhir.r5.model.Practitioner;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.HashMap;
import java.util.List;


public class User {
    private String id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private Boolean enabled;
    private String phoneNumber;
    private List<String> organizationRequest;

    public static UserRepresentation toUserRepresentation(User user){
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(user.getEmail());
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(user.getEnabled());
        userRepresentation.setFirstName(user.getName());
        userRepresentation.setLastName(user.getSurname());
        userRepresentation.setId(user.getId());
        userRepresentation.setAttributes(new HashMap<>(){{
            put("organizationRequest", user.getOrganizationRequest());
            put("phoneNumber", List.of(user.getPhoneNumber()));
        }});

        return userRepresentation;
    }

    public static User fromPractitioner(Practitioner practitioner, String psw, String orgReq){
        User user = new User();
            if(practitioner.getIdentifier().size() > 0 && null != practitioner.getIdentifier().get(0).getValue()){
                user.setId(practitioner.getIdentifier().get(0).getValue());
            }
            user.setEnabled(practitioner.getActive());
            user.setName(practitioner.getName().get(0).getGivenAsSingleString());
            user.setSurname(practitioner.getName().get(0).getFamily());
            user.setEmail(practitioner.getTelecom().stream().filter(x -> x.getSystem().equals(ContactPoint.ContactPointSystem.EMAIL)).toList().get(0).getValue());
            user.setPhoneNumber(practitioner.getTelecom().stream().filter(x -> x.getSystem().equals(ContactPoint.ContactPointSystem.PHONE)).toList().get(0).getValue());
            if(null != psw){
                user.setOrganizationRequest(List.of(orgReq));
            }
            if(null != orgReq){
                user.setPassword(psw);
            }
        return user;
    }
/*

    //TODO: Fix this and add to headers 2
                       try {
        HashMap map = new ObjectMapper().readValue(jwt.getClaim("groups_id").toString(), HashMap.class);
    } catch (
    JsonProcessingException e) {
        throw new RuntimeException(e);
    }
*/

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getOrganizationRequest() {
        return organizationRequest;
    }

    public void setOrganizationRequest(List<String> organizationRequest) {
        this.organizationRequest = organizationRequest;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", organizationRequest=" + organizationRequest +
                '}';
    }
}
