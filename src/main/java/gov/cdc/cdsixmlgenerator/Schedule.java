/*
 * This code reads the Schedule Supporting files and creates a single 
 * output XML file
 */
package gov.cdc.cdsixmlgenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author csnewman
 */
public class Schedule {
    Concept conditions;
    Concept contraindications;
    Concept cvxToAntigen;
    Concept liveConflict;
    Concept groupToAntigen;
    Concept vaccineGroup;
    
    public static void readScheduleFile(File file, Concept thisConcept){
        //this code is called for type of file with a different concept
        //Coded Conditions
        //Contraindications - deprecated with Version 3.0
        //CVX to Antigen
        //Live Virus Conflicts
        //Vaccine Group to Antigen Map
        //Vaccine Group
        try{
            FileInputStream fis=new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fis);
            //Assume just one worksheet that is of interest
            Sheet sheet=workbook.getSheetAt(0);
            Iterator rowIterator = sheet.iterator();
            Row row =(Row) rowIterator.next();
            //add each of the headers in the first row of the file
            Iterator cellIterator=row.cellIterator();                  
            while (cellIterator.hasNext()){
                Cell cell =(Cell) cellIterator.next();
                String cellValue=cell.toString();
                //System.out.println(cellValue);
                if (!cellValue.isEmpty()){
                    thisConcept.setHeader(cellValue);
                }
            }
            //process the rest of the lines in the file
            //assume that the column count for each line of data is the same as number of headers
            int headerCount=thisConcept.headerList.size();
            while (rowIterator.hasNext()){
                row=(Row) rowIterator.next();
                for (int x=0; x<(headerCount);x++){
                    //get the header object for this column
                    Header thisHeader=(Header) thisConcept.headerList.get(x);
                    Cell lineCell=row.getCell(x);
                    //System.out.println(thisHeader+": "+lineCell+"^"+lineCell.getCellType());
                    
                    String lineCellValue="";
                    //number containing cells
                    if (lineCell.getCellType()==0){
                        //handle dates separately
                        if (DateUtil.isCellDateFormatted(lineCell)) {
                            //System.out.println("this is a date: "+cell+"&"+cell.getCellType());
                            Date cellDate=lineCell.getDateCellValue();
                            //System.out.println(cellDate+"^"+cellDate.toString());
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                            //System.out.println("The date is: "+dateFormat.format(cellDate));
                            lineCellValue=dateFormat.format(cellDate);
                        }
                        //if not a date, then just treat as a string
                        //this is important for numbers
                        else {
                            lineCell.setCellType(Cell.CELL_TYPE_STRING);
                            lineCellValue=lineCell.toString();
                        }
                    }
                    else {
                        lineCell.setCellType(Cell.CELL_TYPE_STRING);
                        lineCellValue=lineCell.toString();
                    }

                    //System.out.println("the header we are going to add a value to: "+thisHeader.toString());
                    //add the value of the cell to the header object
                    thisHeader.setValue(lineCellValue);
                    //System.out.println("the concept "+thisConcept.toString()+" now has "+thisHeader.valueList.size());
                    //System.out.println("and the value is: "+lineCellValue);
                }
            }
            fis.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void writeXML(Schedule thisSchedule,String outputFileDir){
        //take data out of the individual concept records
        //and create a single XML file
        String newFile=outputFileDir+"\\ScheduleSupportingData.xml";
        PrintWriter out=XMLgenerator.openWriter(newFile);
        out.println("<scheduleSupportingData>");
        //Live Virus Conflicts
        writeLiveConflict(thisSchedule,out);        
        //Contraindications - deprecated with version 3.0
        //writeContraindications(thisSchedule,out);
        //Vaccine Groups
        writeVaccineGroups(thisSchedule,out);
        //Group to Antigen
        writeGroupToAntigen(thisSchedule,out);
        //CVX to Antigen
        writeCVXToAntigen(thisSchedule,out);
        //Coded Condtions
        writeCodedConditions(thisSchedule,out);
        
        out.println("</scheduleSupportingData>");
        
    }

    private static void writeCodedConditions(Schedule thisSchedule,PrintWriter out){
        //Coded Condtions
        //each column is at the same level (no grouping)
        Concept condition=thisSchedule.conditions;
        if (condition.headerList.size()!=0){
            //System.out.println("has coded conditions");
            out.println("<observations>");
            
            
            //get the number of values from the first header object
            Header codeHeader=(Header) condition.headerList.get(0);
            Header titleHeader=(Header) condition.headerList.get(1);
            //Header groupHeader=(Header) condition.headerList.get(2);
            Header indicationTextHeader=(Header) condition.headerList.get(2);
            Header contraindicationTextHeader=(Header) condition.headerList.get(3);
            Header clarifyingTextHeader=(Header) condition.headerList.get(4);
            Header snomedHeader=(Header) condition.headerList.get(5);
            Header cvxHeader=(Header) condition.headerList.get(6);
            Header phinvsHeader=(Header) condition.headerList.get(7);
            int valueCount=codeHeader.valueList.size();
 
            for (int idx2=0;idx2<valueCount;idx2++){
                //code
                String conditionCode=(String) codeHeader.valueList.get(idx2);
                if (conditionCode.isEmpty()) continue;
                Output.printStartLine("observation",out);
                printLine("observationCode",conditionCode,out);
                
                //title
                String conditionTitle=(String) titleHeader.valueList.get(idx2);
                printLine("observationTitle",conditionTitle,out);
                
                //group - there is no group in the supporting file for now
                //String group=(String) groupHeader.valueList.get(idx2);
                printLine("group","",out);
                
                //indication text
                String indicationText=(String) indicationTextHeader.valueList.get(idx2);
                printLine("indicationText",indicationText,out);
                
                //contraindication text
                String contraindicationText=(String) contraindicationTextHeader.valueList.get(idx2);
                printLine("contraindicationText",contraindicationText,out);
                
                //clarifying text
                String clarifyingText=(String) clarifyingTextHeader.valueList.get(idx2);
                printLine("clarifyingText",clarifyingText,out);
                
                //coded values
               
                //snomedList
                String snomedList=(String) snomedHeader.valueList.get(idx2);
                //cvxList
                String cvxList=(String) cvxHeader.valueList.get(idx2);
                //phinvsList
                String phinvsList=(String) phinvsHeader.valueList.get(idx2);
                
                if ((snomedList.equalsIgnoreCase("n/a")) && (cvxList.equalsIgnoreCase("n/a"))) {
                    Output.printShortLine("codedValues",out);
                }
                else {
                    Output.printStartLine("codedValues", out);
                    //print out SNOMED codes
                    if (!snomedList.equalsIgnoreCase("n/a")) {
                        String[] codes=snomedList.split(";");
                        for (int codeCount=0; codeCount<codes.length; codeCount++){
                            String value=codes[codeCount];
                            //System.out.println(value);
                            int openPos=value.lastIndexOf('(');
                            int closePos=value.lastIndexOf(')');
                            String thisCode=value.substring(openPos+1,closePos);
                            thisCode=thisCode.trim();
                            String rest=value.substring(0,openPos);
                            rest=rest.trim();
                            Output.printStartLine("codedValue", out);
                            printLine("code",thisCode,out);
                            printLine("codeSystem","SNOMED",out);
                            printLine("text",rest,out);
                            Output.printEndLine("codedValue", out);
                        }
                    }
                    //print out CVX codes
                    if (!cvxList.equalsIgnoreCase("n/a")) {
                        String[] codes=cvxList.split(";");
                        for (int codeCount=0; codeCount<codes.length; codeCount++){
                            String value=codes[codeCount];
                            int openPos=value.lastIndexOf('(');
                            int closePos=value.lastIndexOf(')');
                            String thisCode=value.substring(openPos+1,closePos);
                            thisCode=thisCode.trim();
                            String rest=value.substring(0,openPos);
                            rest=rest.trim();
                            Output.printStartLine("codedValue", out);
                            printLine("code",thisCode,out);
                            printLine("codeSystem","CVX",out);
                            printLine("text",rest,out);
                            Output.printEndLine("codedValue", out);
                        }
                    }
                    //print out PHIN VS codes
                    if (!phinvsList.equalsIgnoreCase("n/a")) {
                        String[] codes=phinvsList.split(";");
                        for (int codeCount=0; codeCount<codes.length; codeCount++){
                            String value=codes[codeCount];
                            int openPos=value.lastIndexOf('(');
                            int closePos=value.lastIndexOf(')');
                            String thisCode=value.substring(openPos+1,closePos);
                            thisCode=thisCode.trim();
                            String rest=value.substring(0,openPos);
                            rest=rest.trim();
                            Output.printStartLine("codedValue", out);
                            printLine("code",thisCode,out);
                            printLine("codeSystem","CDCPHINVS",out);
                            printLine("text",rest,out);
                            Output.printEndLine("codedValue", out);
                        }
                    }
                    Output.printEndLine("codedValues", out);
                }
                
                
                Output.printEndLine("observation",out);        
            }           
            
            out.println("</observations>");
        }
    }    
    
    //Contraindications were moved to the antigen spreadsheets in version 3.0
    private static void writeContraindications(Schedule thisSchedule,PrintWriter out){
        //Contraindications
        //each column is at the same level (no grouping)
        Concept contraindication=thisSchedule.contraindications;
        if (contraindication.headerList.size()!=0){
            //System.out.println("has contraindications");
            out.println("<contraindications>");
            
            
            //get the number of values from the first header object
            Header antigenHeader=(Header) contraindication.headerList.get(0);
            Header languageHeader=(Header) contraindication.headerList.get(1);
            Header codingsystemHeader=(Header) contraindication.headerList.get(2);
            Header codeHeader=(Header) contraindication.headerList.get(3);
            Header codeTextHeader=(Header) contraindication.headerList.get(4);
            Header cvxListHeader=(Header) contraindication.headerList.get(5);
            int valueCount=antigenHeader.valueList.size();
 
            for (int idx2=0;idx2<valueCount;idx2++){
                //antigen
                String antigen=(String) antigenHeader.valueList.get(idx2);
                if (antigen.isEmpty()) continue;
                Output.printStartLine("contraindication",out);
                printLine("antigen",antigen,out);
                
                //language
                String language=(String) languageHeader.valueList.get(idx2);
                printLine("contraindicationLanguage",language,out);
                
                //codingsystem
                String codingsystem=(String) codingsystemHeader.valueList.get(idx2);
                printLine("concept",codingsystem,out);
                
                //code
                String code=(String) codeHeader.valueList.get(idx2);
                printLine("conceptCode",code,out);
                
                //codeText
                String codeText=(String) codeTextHeader.valueList.get(idx2);
                printLine("conceptText",codeText,out);
                
                //cvxList
                String cvxList=(String) cvxListHeader.valueList.get(idx2);
                printLine("cvxList",cvxList,out);
                
                Output.printEndLine("contraindication",out);        
            }           
            
            out.println("</contraindications>");
        }
    }
    private static void writeCVXToAntigen(Schedule thisSchedule,PrintWriter out){
        //CVX to Antigen Map
        //the last three columns are grouped as an association
        //multiple lines can be grouped under a single CVX code
        Concept currentConcept=thisSchedule.cvxToAntigen;
        if (currentConcept.headerList.size()!=0){
            out.println("<cvxToAntigenMap>");
            
            
            //get the number of values from the first header object
            Header cvxHeader=(Header) currentConcept.headerList.get(0);
            Header descriptionHeader=(Header) currentConcept.headerList.get(1);
            Header antigenHeader=(Header) currentConcept.headerList.get(2);
            Header beginAgeHeader=(Header) currentConcept.headerList.get(3);
            Header endAgeHeader=(Header) currentConcept.headerList.get(4);

            int valueCount=cvxHeader.valueList.size();
            //initialize the previousGuideline String
            String previousCVX="";
            for (int idx2=0;idx2<valueCount;idx2++){
                //cvx
                //group lines if the CVX code is the same
                String cvx=(String) cvxHeader.valueList.get(idx2);
                if (cvx.isEmpty()) continue;
                if (!cvx.equalsIgnoreCase(previousCVX)){
                    Output.printStartLine("cvxMap",out);
                    printLine("cvx", cvx, out);

                //description
                String description=(String) descriptionHeader.valueList.get(idx2);
                printLine("shortDescription",description,out);
                }
                
                Output.printStartLine("association",out);
                
                //antigen
                String antigen=(String) antigenHeader.valueList.get(idx2);
                printLine("antigen",antigen,out);
                
                //beginAge
                String beginAge=(String) beginAgeHeader.valueList.get(idx2);
                printLine("associationBeginAge",beginAge,out);
                
                //endAge
                String endAge=(String) endAgeHeader.valueList.get(idx2);
                printLine("associationEndAge",endAge,out);
                
                Output.printEndLine("association",out);
                
                // if this is hte last line or the current set ID is not the 
                //same as the next setID then print the end tag
                if (idx2==valueCount-1){
                    Output.printEndLine("cvxMap",out);
                }
                else {
                    String nextCVX=(String) cvxHeader.valueList.get(idx2+1);
                    if (!cvx.equals(nextCVX)){
                        Output.printEndLine("cvxMap",out);
                    }
                }
                previousCVX=cvx; 
     
            }
            
            
            out.println("</cvxToAntigenMap>");
        }
    }
    private static void writeLiveConflict(Schedule thisSchedule,PrintWriter out){
        //Live Virus Conflicts
        //each column is at the same level (no grouping)
        //each CVX column is split into two tags
        Concept currentConcept=thisSchedule.liveConflict;
        if (currentConcept.headerList.size()!=0){
            out.println("<liveVirusConflicts>");
            
            
            //get the number of values from the first header object
            Header previousHeader=(Header) currentConcept.headerList.get(0);
            Header currentHeader=(Header) currentConcept.headerList.get(1);
            Header beginHeader=(Header) currentConcept.headerList.get(2);
            Header minIntervalHeader=(Header) currentConcept.headerList.get(3);
            Header endHeader=(Header) currentConcept.headerList.get(4);

            int valueCount=previousHeader.valueList.size();
            for (int idx2=0;idx2<valueCount;idx2++){
                //previous cvx
                String cvx=(String) previousHeader.valueList.get(idx2);
                if (cvx.isEmpty()) continue;
                Output.printStartLine("liveVirusConflict",out);
                Output.cvxValue("previous", cvx, out);
                
                //current cvx
                cvx=(String) currentHeader.valueList.get(idx2);
                Output.cvxValue("current", cvx, out);
                
                //conflict begin interval
                String begin=(String) beginHeader.valueList.get(idx2);
                printLine("conflictBeginInterval",begin,out);
                
                //conflict end interval
                String minInterval=(String) minIntervalHeader.valueList.get(idx2);
                printLine("minConflictEndInterval",minInterval,out);
                
                //minimum conflict end interval
                String end=(String) endHeader.valueList.get(idx2);
                printLine("conflictEndInterval",end,out);               
                
                Output.printEndLine("liveVirusConflict",out);        
            }
            
            
            out.println("</liveVirusConflicts>");
        }
    }
    private static void writeGroupToAntigen(Schedule thisSchedule,PrintWriter out){
        //Vaccine Group to Antigen Map
        //multiple lines can be grouped under a single vaccine group
        Concept currentConcept=thisSchedule.groupToAntigen;
        if (currentConcept.headerList.size()!=0){
            out.println("<vaccineGroupToAntigenMap>");
            
            
            //get the number of values from the first header object
            Header groupHeader=(Header) currentConcept.headerList.get(0);
            Header antigenHeader=(Header) currentConcept.headerList.get(1);

            int valueCount=groupHeader.valueList.size();
            //initialize the previousGuideline String
            String previousGroup="";
            for (int idx2=0;idx2<valueCount;idx2++){
                //group
                String group=(String) groupHeader.valueList.get(idx2);
                if (group.isEmpty()) continue;
                if (!group.equalsIgnoreCase(previousGroup)){
                    Output.printStartLine("vaccineGroupMap",out);
                    printLine("name", group, out);
                }
                
                //antigen
                String antigen=(String) antigenHeader.valueList.get(idx2);
                Output.printLine("antigen", antigen, out);
                
                // if the current set ID is not the same as the next setID then print the concept end tag
                if (idx2==valueCount-1){
                    Output.printEndLine("vaccineGroupMap",out);
                }
                else {
                    String nextGroup=(String) groupHeader.valueList.get(idx2+1);
                    if (!group.equals(nextGroup)){
                        Output.printEndLine("vaccineGroupMap",out);
                    }
                }
                previousGroup=group;       
                }
            
            
            out.println("</vaccineGroupToAntigenMap>");
        }
    }
    private static void writeVaccineGroups(Schedule thisSchedule,PrintWriter out){
        //Vaccine Groups
        //each column is at the same level (no grouping)
        Concept thisConcept=thisSchedule.vaccineGroup;
        if (thisConcept.headerList.size()!=0){
            //System.out.println("has contraindications");
            out.println("<vaccineGroups>");
            
            
            //get the number of values from the first header object
            Header groupHeader=(Header) thisConcept.headerList.get(0);
            Header administerFullHeader=(Header) thisConcept.headerList.get(1);
            int valueCount=groupHeader.valueList.size();
            //initialize the previousGuideline String
            //String previousGuideline="";
            for (int idx2=0;idx2<valueCount;idx2++){
                //vaccine group
                String group=(String) groupHeader.valueList.get(idx2);
                if (group.isEmpty()) continue;
                Output.printStartLine("vaccineGroup",out);
                printLine("name",group,out);
                
                //Administer Full Vaccine Group
                String administerFull=(String) administerFullHeader.valueList.get(idx2);
                printLine("administerFullVaccineGroup",administerFull,out);
                
                Output.printEndLine("vaccineGroup",out);        
            }
            
            
            out.println("</vaccineGroups>");
        }
    }
    private static void printLine(String xmlTag, String value,PrintWriter out){
        if (value.isEmpty()|value.equalsIgnoreCase("n/a")){
            Output.printShortLine(xmlTag, out);
        }
        else {
            Output.printLine(xmlTag, value, out);
        }
    }
}
