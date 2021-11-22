/*
 * Concept object
 * Represents values in column 1 of a Series worksheet
 * Contains a name and headers
 */
package gov.cdc.cdsixmlgenerator;
import java.io.*;
import java.util.*;
        
/**
 *
 * @author csnewman
 */
public class Concept {
    public Concept(String name){
       conceptName=name; 
    }
    private String conceptName;
    private String conceptType;
    ArrayList headerList=new ArrayList();

    public String getName() {
        return conceptName;
    }
    
    public void setType(String value) {
        this.conceptType=value;
    }
    
    public String getType() {
        return conceptType;
    }
   
    public Header setHeader(String header) {
        Header currentHeader=new Header(header);
        headerList.add(currentHeader);
        return currentHeader;
    }
    
    public String toString(){
        return this.conceptName;
    }
}