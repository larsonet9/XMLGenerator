/*
 * Header object
 * Represents data in columns other than the first column
 * Contains an array of cell values for that header
 * Assume all headers in a given row contain the same number of lines in the the value array
 */
package gov.cdc.cdsixmlgenerator;

import java.util.ArrayList;

/**
 *
 * @author csnewman
 */
public class Header {
public Header(String name){
       headerName=name; 
    }
    private String headerName;
    ArrayList valueList=new ArrayList();

    public String getName() {
        return headerName;
    }
    
    public ArrayList getValue() {
        return valueList;
    }
    
    public ArrayList setValue(String value) {
        valueList.add(value);
        //System.out.println("after immeidately adding a value, the size of valueList is: "+valueList.size());
        //System.out.println("here is the header we added to: "+this.toString());
        return valueList;
    }
    
    public String toString(){
        return this.headerName;
    }
}
