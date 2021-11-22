/*
 * Immunity object
 * Represents the immunities in a spreadsheet
 */
package gov.cdc.cdsixmlgenerator;
import java.util.*;

/**
 *
 * @author csnewman
 */
public class Immunity {
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
