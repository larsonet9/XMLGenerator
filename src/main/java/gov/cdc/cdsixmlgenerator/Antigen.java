/*
 * Antigen object
 * Represents a spreadsheet
 * Contains a name and a set of series
 */
package gov.cdc.cdsixmlgenerator;
import java.util.*;
/**
 *
 * @author csnewman
 */
public class Antigen {
    private String antigenName;
    String inputFile;
    ArrayList seriesList=new ArrayList();
    ArrayList immunityList= new ArrayList();
    ArrayList contraindicationList= new ArrayList();
    
public Antigen(String name){
    this.antigenName=name;
}

public void setInputFile(String fileName) {
        this.inputFile=fileName;
}  

public String getName() {
        return antigenName;
}   
    
public ArrayList getSeries() {
    return seriesList;
}

public Series setSeries() {
    Series currentSeries=new Series();
    seriesList.add(currentSeries);
    return currentSeries;
}

public ArrayList getImmunity() {
    return immunityList;
}

public Immunity setImmunity() {
    Immunity currentImmunity=new Immunity();
    immunityList.add(currentImmunity);
    return currentImmunity;
} 

public ArrayList getContraindication() {
    return contraindicationList;
}

public Contraindication setContraindication() {
    Contraindication currentContraindication=new Contraindication();
    contraindicationList.add(currentContraindication);
    return currentContraindication;
} 
}

