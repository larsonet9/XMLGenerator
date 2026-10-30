/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gov.cdc.cdsixmlgenerator;

import java.util.ArrayList;

/**
 *
 * @author Eric
 */
public class RecommendationCategory {
    public RecommendationCategory(){
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

