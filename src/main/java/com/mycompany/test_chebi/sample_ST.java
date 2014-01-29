/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.test_chebi;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import uk.ac.ebi.chebi.webapps.chebiWS.client.ChebiWebServiceClient;
import uk.ac.ebi.chebi.webapps.chebiWS.model.*;

/**
 *
 * @author sankalp
 */
public class sample_ST {

    public static void main(String[] args) throws IOException, ChebiWebServiceFault_Exception {

        //System.out.println(hasRoleAsParent("CHEBI:22315"));
        ChebiWebServiceClient client = new ChebiWebServiceClient();

        FileWriter writer = new FileWriter(new File("/Users/sankalp/Desktop/EBI/Test/data_role_5.json"));
        //writer.write("function loadData(){");
        //writer.write("\n json = {");
        writer.write("{");

        try {

            String chebiId = "CHEBI:22315";
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

    private static boolean hasRoleAsParent(String chebiID) throws ChebiWebServiceFault_Exception {

        ChebiWebServiceClient client = new ChebiWebServiceClient();
        OntologyDataItemList list = client.getOntologyChildren(chebiID);
        List<String> chebiIDs = new ArrayList<String>();
        List<OntologyDataItem> results = list.getListElement();

        if (results.size() > 0) {
            for (OntologyDataItem item : results) {
                String id = item.getChebiId();
                System.out.println(id);
                System.out.println(item.getType());
//                boolean cyclicRelationship = item.isCyclicRelationship();
//                System.out.println("Cyclic relationship: "+cyclicRelationship);
                LiteEntityList allInPath = client.getAllOntologyChildrenInPath(item.getChebiId(), RelationshipType.IS_A, true);
                int size = allInPath.getListElement().size();
                System.out.println(size);
                chebiIDs.add(id);
            }
        }
        return chebiIDs.contains("CHEBI:50906");

    }

    private static int findChildren(ChebiWebServiceClient client, String chebiId, String chebiName, FileWriter writer, int treeHeight) throws ChebiWebServiceFault_Exception, IOException {
        OntologyDataItemList lists = client.getOntologyChildren(chebiId);
        List<OntologyDataItem> results = lists.getListElement();
        ++treeHeight;
        int value = 0;
        int size = 0;
        boolean hasChildren = false;

        // we have more than one child
        if (results.size() > 0) {
            for (int iii = 0; iii < results.size(); iii++) {

                OntologyDataItem result = results.get(iii);

                if (!result.isCyclicRelationship()) {
                    if (result.getType().equals("is a")) {
                        LiteEntityList allInPath = client.getAllOntologyChildrenInPath(result.getChebiId(), RelationshipType.IS_A, true);
                        size = allInPath.getListElement().size();
                        if (size > 0) {
                            if (hasChildren == true) {
                                writer.write(",");
                            }
                            writer.write("{ \n         \"id\": \"" + result.getChebiId() + "\",   \n"
                                    + "         \"name\": \"" + result.getChebiName() + "\",  \n"
                                    + "         \"data\": {},");
                            writer.write("\n    \"children\": [");
                            System.out.println("Checking for children of: " + result.getChebiName());

                            value = findChildren(client, result.getChebiId(), result.getChebiName(), writer, treeHeight);
                            hasChildren = true;
                            if (treeHeight < value) {
                                writer.write("]  \n }");
                            }
                        } else if (size == 0) {

                            writer.write("{ \n         \"id\": \"" + result.getChebiId() + "\",   \n"
                                    + "         \"name\": \"" + result.getChebiName() + "\",  \n"
                                    + "         \"data\": {},");
                            writer.write("\n    \"children\": []}");
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
