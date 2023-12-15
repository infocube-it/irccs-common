package org.quarkus.irccs.assembler.researchstudy;

import org.hl7.fhir.r5.model.*;
import org.hl7.fhir.utilities.xhtml.NodeType;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;

import java.util.ArrayList;
import java.util.List;

public class PatientAssembler {

    public static Patient createEmptyPatient() {
        Patient patient = new Patient();
        patient.setActive(false);
        return patient;
    }

    public static Patient createPatientOfCarePlan() {
        Patient patient = new Patient();
        List<Address> addressList = new ArrayList<>();
        List<StringType> stringTypeList = new ArrayList<>();
        List<ContactPoint> contactPoints =  new ArrayList<>();
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setSystem(ContactPoint.ContactPointSystem.PHONE);
        contactPoints.add(contactPoint);
        patient.setGender(Enumerations.AdministrativeGender.UNKNOWN);
        patient.setTelecom(contactPoints);


        Address address = new Address();
        stringTypeList.add(new StringType(""));
        address.setLine(stringTypeList);
        patient.setAddress(addressList);

        XhtmlNode xhtmlNode = new XhtmlNode(NodeType.Element);
        xhtmlNode.setName("div");
        xhtmlNode.setAttribute("xmlns", "http://www.w3.org/1999/xhtml");

        XhtmlNode childNode = new XhtmlNode();
        childNode.setNodeType(NodeType.Element);
        childNode.setName("table");
        childNode.setLocation(new XhtmlNode.Location(1,44));
        childNode.setAttribute("class","hapiPropertyTable");
        childNode.setEmptyExpanded(true);

        XhtmlNode childNodeTBody = new XhtmlNode(NodeType.Element);
        childNodeTBody.setName("tbody");
        childNodeTBody.setLocation(new XhtmlNode.Location(1,77));

        XhtmlNode childeGenerated = new XhtmlNode(NodeType.Text);
        childeGenerated.setLocation(new XhtmlNode.Location(1,82));
        childeGenerated.setContent("Generated");

        List<XhtmlNode> list = new ArrayList<>();
        list.add(childeGenerated);
        childNodeTBody.getChildNodes().addAll(list);
        list = new ArrayList<>();
        list.add(childNodeTBody);
        childNode.getChildNodes().addAll(list);
        list = new ArrayList<>();
        list.add(childNode);
        xhtmlNode.getChildNodes().addAll(list);

        patient.setText(new Narrative(Narrative.NarrativeStatus.GENERATED, xhtmlNode));
        return patient;
    }

}