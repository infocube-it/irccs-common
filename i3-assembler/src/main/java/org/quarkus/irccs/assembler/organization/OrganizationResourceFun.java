package org.quarkus.irccs.assembler.organization;

import org.hl7.fhir.r5.model.*;

import java.util.ArrayList;
import java.util.List;

public class OrganizationResourceFun {


    //todo:: this is a missed param
    /*
    Descr                           Alba- Oncologia-Faggiuolo
    indirizzo                       Via Pierino Belli 26
    Referente                       Dr. Federico  Castiglione
    tel                             0173/316455-56-46-45 Segr.
    fax                             0173/316222
    StudioE-mail                    ELVISGEM-VIN
    Campo12                         -
    cod_cei                         112
    id_centri_grp                   -2
    note                            01012001
    codossc                         -
    refamm                          Silvana Almondo 0173-316200\nsalmondo@aslcn2.it\nper info su la delibera amministrativa\nsegreteriadirezionegenerale@aslcn2.it
    ext_tab                         -
    ext_id                          -
     */

    public static Organization getMockedOrganizationOne() {
        Address address = new Address();
        Identifier identifier = new Identifier();
        Organization organization = new Organization();
        List<Address> addressList = new ArrayList<>();
        List<Identifier> identifierList = new ArrayList<>();

        ExtendedContactDetail contactDetail = new ExtendedContactDetail();
        List<ExtendedContactDetail> extendedContactDetails = new ArrayList<>();
        List<HumanName> humanNames = new ArrayList<>();
        humanNames.add(new HumanName().setFamily("Dr.Faggiuolo Roberto (Dr. Gianfranco Porcile)"));
        contactDetail.setName(humanNames);
        extendedContactDetails.add(contactDetail);

        identifier.setValue("21");
        address.setCity("Alba");
        address.setCountry("CN");
        address.setPostalCode("12051");
        address.setState("IT");

        identifierList.add(identifier);
        addressList.add(address);
        organization.setName("Servizio di Oncologia Medica Osp. San Lazzaro");
        organization.setIdentifier(identifierList);
        organization.setContact(extendedContactDetails);
        return organization;
    }

    public  static Organization getMockedOrganizationTwo() {
        Address address = new Address();
        Identifier identifier = new Identifier();
        Organization organization = new Organization();
        List<Address> addressList = new ArrayList<>();
        List<Identifier> identifierList = new ArrayList<>();
        //todo:: Not present in R5
        //List<Organization.OrganizationContactComponent> organizationContactComponentList = new ArrayList<>();
        //Organization.OrganizationContactComponent organizationContactComponent = new Organization.OrganizationContactComponent();

        identifier.setValue("22");
        address.setCity("Alba");
        address.setCountry("CN");
        address.setPostalCode("12051");
        address.setState("IT");

        //organizationContactComponent.setName(new HumanName().setFamily("Dr.Carlo Ammanasso (Dr. Franco Docile)"));
        //organizationContactComponentList.add(organizationContactComponent);
        identifierList.add(identifier);
        addressList.add(address);
        organization.setName("Dipartimento A di Oncologia Medica");
        organization.setIdentifier(identifierList);
        //organization.setAddress(addressList);
        //organization.setContact(organizationContactComponentList);
        return organization;
    }
}
