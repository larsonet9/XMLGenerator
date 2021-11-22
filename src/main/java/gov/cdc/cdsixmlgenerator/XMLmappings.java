/*
 * This takes an input file and converts it to a HashMap
 * This maps the Excel concepts and headers into XML tags
 */
package gov.cdc.cdsixmlgenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author csnewman
 */
public class XMLmappings {
    public static void readMappings(String name, HashMap xmlTags) {
        String line="";
        String[] data;
        String concept;
        String header;
        String xml;
        File dir=new File(name);
        BufferedReader in = null;
        try
        {
            File file = new File(name);
            in = new BufferedReader(
                new FileReader(file) );
        }
        catch (FileNotFoundException e)
        {
            System.out.println(
                "The file ("+name+") doesn't exist.");
            System.exit(0);
        }
        try
        {
            line = in.readLine();
        }
        catch (IOException e)
        {
            System.out.println("I/O Error");
             System.exit(0);
        }
        while(line != null)
        {
                           
            data = line.split(",");
            //A "concept" is a grouper of data elements
            //It is what is found in column A of the Excel Spreadsheet
            //For example, Preferable Vaccine or Interval
            concept = data[0];
            
            //A header is a data element under a concept
            //For example, Minimum Age and Earliest Recommended Age for the Age concept
            header = data[1];
            
            xml =(String) data[2];

            //Index the HashMap with the combination of both the concept
            //and the header so that headers can be re-used
            if (!xml.isEmpty()){
                xmlTags.put(concept+"^"+header,xml); 
            }
        try
            {
                line = in.readLine();
            }
            catch (IOException e)
            {
                System.out.println("I/O Error");
                System.exit(0);
            }       
        }
    }
}

