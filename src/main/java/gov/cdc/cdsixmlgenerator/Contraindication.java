/*
 * Contraindication object
 * Represents the contraindications in a spreadsheet
 */
package gov.cdc.cdsixmlgenerator;

import java.util.ArrayList;

/**
 *
 * @author csnewman
 */
public class Contraindication {
    ArrayList conceptList=new ArrayList();

    public ArrayList getConcepts() {
        return conceptList;
    }
    
    public Concept setConcept(String concept) {
        Concept currentConcept=new Concept(concept);
        conceptList.add(currentConcept);
        return currentConcept;
    }
}
