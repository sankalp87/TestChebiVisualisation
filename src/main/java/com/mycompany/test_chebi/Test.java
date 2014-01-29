package com.mycompany.test_chebi;

import org.apache.log4j.Logger;
import uk.ac.ebi.chebi.webapps.chebiWS.client.ChebiWebServiceClient;
import uk.ac.ebi.chebi.webapps.chebiWS.model.ChebiWebServiceFault_Exception;
import uk.ac.ebi.chebi.webapps.chebiWS.model.*;
//import uk.ac.ebi.chebi.webapps.chebiWS.model.DataItem;
//import uk.ac.ebi.chebi.webapps.chebiWS.model.Entity;
//import uk.ac.ebi.chebi.webapps.chebiWS.model.OntologyDataItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */

public class Test {

    public static void getOntologyChildrenExample() {

        try {

            // Create client
            ChebiWebServiceClient client = new ChebiWebServiceClient();
            System.out.println("Invoking getOntologyChildren");
            OntologyDataItemList children = client.getOntologyChildren("CHEBI:64227");
            List<OntologyDataItem> childrenList = children.getListElement();
            for (OntologyDataItem ontologyDataItem : childrenList) {
                System.out.println("CHEBI ID: " + ontologyDataItem.getChebiId());
            }

        } catch (ChebiWebServiceFault_Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static boolean hasNoChildren(String chebiID) throws ChebiWebServiceFault_Exception {

        ChebiWebServiceClient client = new ChebiWebServiceClient();
        OntologyDataItemList children = client.getOntologyChildren(chebiID);
        return children.getListElement().isEmpty();
    }

    public static void main(String[] args) throws ChebiWebServiceFault_Exception {
        System.out.println(hasNoChildren("CHEBI:74518"));
    }
}

/*public class Test 
 {
 public static void main( String[] args )
 {
 System.out.println( "Hello World!" );
 }
 }*/
