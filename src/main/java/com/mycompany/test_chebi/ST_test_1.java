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
public class ST_test_1 {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        ChebiWebServiceClient client = new ChebiWebServiceClient();
        
        FileWriter writer = new FileWriter(new File("/Users/sankalp/Desktop/EBI/Test/Final/URL/test_2_url.json"));
        writer.write("{");
        
        try {
            
            String chebiId = "CHEBI:74518";
            
            int treeHeight = 0;
            
            Entity entity = client.getCompleteEntity(chebiId);
            
            
            
            writer.write("\n     \"id\": \"" + entity.getChebiId() + "\",");
            writer.write("\n     \"name\": \"" + entity.getChebiAsciiName() + "\",");
             writer.write("\n     \"definition\": \"" + entity.getDefinition() + "\",");
            writer.write("\n     \"url\": \"http://www.ebi.ac.uk/chebi/searchId.do;45D01B98B7315EDB33546A484BCC4046?chebiId=" + entity.getChebiId() + "\",");
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
        
        String jsonFilePath = "/Users/sankalp/Desktop/EBI/Test/Final/URL/test_2_url.json";
        Utility.JSON.resolveJsonChildren(jsonFilePath);
        
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
                            
                            Entity child = client.getCompleteEntity(result.getChebiId());
                            System.out.println(child.getDefinition());
                            
                            writer.write("{ \n         \"id\": \"" + result.getChebiId() + "\",   \n"
                                    + "         \"name\": \"" + result.getChebiName() + "\",  \n"
                                    + "         \"data\": {},");
                            writer.write("\n     \"definition\": \"" + child.getDefinition() + "\",");
                            writer.write("\n     \"url\": \"http://www.ebi.ac.uk/chebi/searchId.do;45D01B98B7315EDB33546A484BCC4046?chebiId=" + result.getChebiId() + "\",");
                            writer.write("\n    \"children\": [");
                            System.out.println("Checking for children of: " + result.getChebiName());
                            
                            value = findChildren(client, result.getChebiId(), result.getChebiName(), writer, treeHeight);
                            
                            hasChildren = true;
                            if (hasChildren == true) {
                                writer.write("]\n}");
                            }

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
