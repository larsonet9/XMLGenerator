/*
 * Dose object
 * Represents everything on a series tab of a spreadsheet between two Dose lines
 * Contains a dose number and concepts
 */
package gov.cdc.cdsixmlgenerator;
import java.util.*;
        
/**
 *
 * @author csnewman
 */
public class Dose {
    public Dose(String number){
       doseNumber=number; 
    }
    private String doseNumber;
    ArrayList conceptList=new ArrayList();

    public String getNumber() {
        return doseNumber;
    }
    
    public ArrayList getConcepts() {
        return conceptList;
    }
    
    public Concept setConcept(String concept) {
        Concept currentConcept=new Concept(concept);
        conceptList.add(currentConcept);
        return currentConcept;
    }
    
    public String toString(){
        return this.doseNumber;
    }
}
