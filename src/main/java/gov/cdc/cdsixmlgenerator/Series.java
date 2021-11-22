/*
 * Series object
 * Represents one series worksheet in a spreadsheet
 * Contains both series level data (including concepts) and dose objects
 */
package gov.cdc.cdsixmlgenerator;
import java.util.*;
        
/**
 *
 * @author csnewman
 */
public class Series {
    private String seriesName;
    private String vaccineGroup;
    private String targetDisease;
    ArrayList conceptList=new ArrayList();
    ArrayList doseList=new ArrayList();
    
    public String getName() {
        return seriesName;
    }
    
    public void setName(String name) {
        this.seriesName=name;
    }
    
    public String getGroup() {
        return vaccineGroup;
    }
    
    public void setGroup(String group) {
        this.vaccineGroup=group;
    }
    
    public String getDisease() {
        return targetDisease;
    }
    
    public void setDisease(String disease) {
        this.targetDisease=disease;
    }
    
    public ArrayList getConcepts() {
        return conceptList;
    }
    
    public Concept setConcept(String concept) {
        Concept currentConcept=new Concept(concept);
        conceptList.add(currentConcept);
        return currentConcept;
    }
    
    public ArrayList getDoses() {
        return doseList;
    }
    
    public Dose setDose(String dose) {
        Dose currentDose=new Dose(dose);
        doseList.add(currentDose);
        return currentDose;
    }
}
