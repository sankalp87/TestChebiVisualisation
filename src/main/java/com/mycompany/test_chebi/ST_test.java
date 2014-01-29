/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.test_chebi;

/**
 *
 * @author sankalp
 */
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.List;
import uk.ac.ebi.chebi.webapps.chebiWS.client.ChebiWebServiceClient;
import uk.ac.ebi.chebi.webapps.chebiWS.model.*;

/**
 *
 *
 */
public class ST_test {

    public static boolean hasNoChildren(String chebiID) throws ChebiWebServiceFault_Exception {

        ChebiWebServiceClient client = new ChebiWebServiceClient();
        OntologyDataItemList children = client.getOntologyChildren(chebiID);
        return children.getListElement().isEmpty();
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        ChebiWebServiceClient client = new ChebiWebServiceClient();

        FileWriter writer = new FileWriter(new File("/Users/sankalp/Desktop/EBI/Test/Final/test_2.json"));
        writer.write("{");

        try {

            String chebiId = "CHEBI:70781";
            int treeHeight = 0;

            Entity entity = client.getCompleteEntity(chebiId);

            writer.write("\n     \"id\": \"" + entity.getChebiId() + "\",");
            writer.write("\n     \"name\": \"" + entity.getChebiAsciiName() + "\",");
            writer.write("\n     \"data\": {},");
            writer.write("\n     \"children\": [");
            findChildren(client, chebiId, entity.getChebiAsciiName(), writer, treeHeight);
            writer.write("]\n }");

        } catch (ChebiWebServiceFault_Exception e) {
            System.err.println("Oops! Something went wrong..." + e.getMessage());

            // clean up the file operations
        } finally {
            writer.close();
        }

    }

    private static int findChildren(ChebiWebServiceClient client, String chebiId, String chebiName, FileWriter writer, int treeHeight) throws ChebiWebServiceFault_Exception, IOException {
        OntologyDataItemList lists = client.getOntologyChildren(chebiId);
        List<OntologyDataItem> results = lists.getListElement();
        ++treeHeight;
        int value = 0;
        int size = 0;
        int size_isa = 0;
        int size_hasrole = 0;
        boolean hasChildren = false;

        // we have more than one child
        if (results.size() > 0) {
            for (int iii = 0; iii < results.size(); iii++) {

                OntologyDataItem result = results.get(iii);
                String childID = result.getChebiId();

                if (!result.isCyclicRelationship()) {
                    if (result.getType().equals("is a") || result.getType().equals("has role")) {
                        LiteEntityList allInPath_isa = client.getAllOntologyChildrenInPath(result.getChebiId(), RelationshipType.IS_A, true);
                        LiteEntityList allInPath_hasrole = client.getAllOntologyChildrenInPath(result.getChebiId(), RelationshipType.HAS_ROLE, true);
                        size_isa = allInPath_isa.getListElement().size();
                        size_hasrole = allInPath_hasrole.getListElement().size();
                        size = size_isa + size_hasrole;

                        if (size >= 0) {

                            if (hasChildren == true) {
                                writer.write(",");
                            }

                            writer.write("{ \n         \"id\": \"" + result.getChebiId() + "\",   \n"
                                    + "         \"name\": \"" + result.getChebiName() + "\",  \n"
                                    + "         \"data\": {}\n,");

                            
                            if (!hasNoChildren(childID)) {
                                writer.write(",");
                                writer.write("\n    \"children\": [");
                                System.out.println("Checking for children of: " + result.getChebiName());
                                value = findChildren(client, result.getChebiId(), result.getChebiName(), writer, treeHeight);

                                hasChildren = true;
                                if (hasChildren == true) {
                                    writer.write("]\n}");
                                    
                                }
                                
                            } else if (hasNoChildren(childID)){

                                writer.write("\n}");

                            }
                            else{
                                writer.write("\n},");
                            }

//                            hasChildren = true;
//                            if (hasChildren == true) {
//                                writer.write("]\n}");
//                            }
//                            if (treeHeight < value) {
//                                writer.write("]  \n }");
//                            }
                        }

                    }
                }
                if (iii == (results.size() - 1)) {
                    value = treeHeight + 1;
                    hasChildren = false;
                }

            }

        }

        return value;

    }
}
