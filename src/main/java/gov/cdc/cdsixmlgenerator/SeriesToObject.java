/*
 * This takes series data and extracts it before saving it to a Series object
 * This gets called once per series for an antigen
 * Each series has attributes of:
 *      Series Name
 *      Target Disease
 *      Vaccine Group
 */
package gov.cdc.cdsixmlgenerator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 *
 * @author csnewman
 */
public class SeriesToObject {
    
    public static void readSeriesFile(Series currentSeries,Sheet sheet){

    ArrayList conceptList=new ArrayList();
    String concept="";
    String conceptMasterList="";
    String doseNumber="";
    String previousConcept="";
    
    Iterator rowIterator = sheet.iterator();
    while (rowIterator.hasNext()) {
        Row row=(Row) rowIterator.next();
        //System.out.println(row);
        Cell firstCell=row.getCell(0);
        //System.out.println(firstCell+"%"+firstCell.toString()+"^"+row.getCell(1));
        if (firstCell==null) continue;
        //System.out.println(firstCell.toString()+"^"+row.getCell(1));
        //only deal with rows that have data in the first column
        concept=firstCell.toString();
        String secondCellValue=row.getCell(1).toString();
        //Series Name exists outside of a dose
        if (concept.equalsIgnoreCase("Series Name")){
            currentSeries.setName(secondCellValue);
            //System.out.println(secondCellValue);
        }
        //Target Name exists outside of a dose
        else if (concept.equalsIgnoreCase("Target Disease")){
            currentSeries.setDisease(secondCellValue);
            //System.out.println(secondCellValue);
        }
        //Vaccine Group exists outside of a dose
        else if (concept.equalsIgnoreCase("Vaccine Group")){
            currentSeries.setGroup(secondCellValue);
            //System.out.println(secondCellValue);
        }
        //When Series Dose is found, then create a new Dose object
        //and added it to the doseList of the Series
        else if (concept.equalsIgnoreCase("Series Dose")){
            doseNumber=secondCellValue;
            Dose addedDose=currentSeries.setDose(doseNumber);
            //System.out.println("here is the newly created dose value: "+addedDose);
        }
        //Some concepts exist outside of a dose and so they need
        //to be stored to the Series object and not the Dose object.
        //This would be stuff like the Select Best Patient Series and
        //Risk Indications
        else if (currentSeries.doseList.isEmpty()){
            //code to handle concepts before the first dose
            //When a new concept is found, add it to the conceptMasterList,
            //create a new concept object and add it to the Series object
            //then add each header to the Concept object
            if (!conceptMasterList.contains("No Dose"+"&"+concept)){
                conceptMasterList=conceptMasterList+"^"+"No Dose"+"&"+concept;
                previousConcept=concept;
                Concept thisConcept=currentSeries.setConcept(concept);
                //flag the concept as containing sets or containing ordinary lines
                //this is used to output the XML differently
                if (row.getCell(1).toString().contains("Set")) {
                    thisConcept.setType("Set");
                }
                //if the second header is "Set Logic", then we should used the 
                //enhanced set logic
                //*csn 20171102 udpate "getCell" to use 2 instead of 1
                //              and check the number of cells
                //              to account for the new Skip Context
                if ((row.getLastCellNum()>2) && (row.getCell(2).toString().contains("Set Logic"))) {
                    thisConcept.setType("enhancedSet");
                }
                else thisConcept.setType("Line");
                //add each of the headers in the row
                Iterator cellIterator=row.cellIterator();
                //need to not use the first cell
                Cell cell =(Cell) cellIterator.next();                    
                while (cellIterator.hasNext()){
                    cell =(Cell) cellIterator.next();
                    String cellValue=cell.toString();
                    //System.out.println(cellValue);
                    if (!cellValue.isEmpty()){
                        thisConcept.setHeader(cellValue);
                   }
                }
            //System.out.println("number of headers: "+thisConcept.headerList.size());
            }
           //Look to see if the value in the first column is the current concept
           //If so, treat the line as data and store to header objects
            else if (concept.equals(previousConcept)){
                Concept thisConcept=(Concept) currentSeries.conceptList.get(currentSeries.conceptList.size()-1);
                //assume that the column count for each line of data is the same as number of headers
                int headerCount=thisConcept.headerList.size();
                //System.out.println("The "+thisConcept.toString()+" concept has "+headerCount+" headers.");
                for (int x=0; x<(headerCount);x++){

                    int y=x+1;
                    //get the header object for this column
                    Header thisHeader=(Header) thisConcept.headerList.get(x);
                    Cell cell=row.getCell(y);
                    //System.out.println(thisHeader+": "+cell+"^"+cell.getCellType());
                    
                    String cellValue="";
                    if (cell.getCellType()==0){
                        if (DateUtil.isCellDateFormatted(cell)) {
                            //System.out.println("this is a date: "+cell+"&"+cell.getCellType());
                            Date cellDate=cell.getDateCellValue();
                            //System.out.println(cellDate+"^"+cellDate.toString());
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                            //System.out.println("The date is: "+dateFormat.format(cellDate));
                            cellValue=dateFormat.format(cellDate);
                        }
                        else {
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            cellValue=cell.toString();
                        }
                    }
                    else {
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        cellValue=cell.toString();
                    }

                    //System.out.println("the header we are going to add a value to: "+thisHeader.toString());
                    //add the value of the cell to the header object
                    thisHeader.setValue(cellValue);
                    //System.out.println("the concept "+thisConcept.toString()+" now has "+thisHeader.valueList.size());
                    //System.out.println("and the value is: "+cellValue);
                }
            }
        }
        //When a new concept is found, add it to the conceptMasterList,
        //create a new concept object and add it to the Dose object
        //then add each header to the Concept object
        else if (!conceptMasterList.contains(doseNumber+"&"+concept)){
            conceptMasterList=conceptMasterList+"^"+doseNumber+"&"+concept;
            previousConcept=concept;
            //System.out.println("new concept showing up  "+concept);
            Dose currentDose=(Dose) currentSeries.doseList.get(currentSeries.doseList.size()-1);
            //System.out.println("here is the currentDose value: "+currentDose);
            Concept thisConcept=currentDose.setConcept(concept);
            //System.out.println(currentDose.getConcepts());
            //flag the concept as containing sets or containing ordinary lines
            //this is used to output the XML differently
            //*csn 20171102 udpate "getCell" to use 2 instead of 1
            //              to account for the new Skip Context
            if (row.getCell(1).toString().contains("Set")) {
                thisConcept.setType("Set");
            }
            //if the second header is "Set Logic", then we should used the 
            //enhanced set logic
            //*csn 20171102 udpate "getCell" to use 2 instead of 1
            //              and check the number of cells
            //              to account for the new Skip Context
            if ((row.getLastCellNum()>2) && (row.getCell(2).toString().contains("Set Logic"))) {
                thisConcept.setType("enhancedSet");
            }
            else thisConcept.setType("Line");
            //add each of the headers in the row
            Iterator cellIterator=row.cellIterator();
            //need to not use the first cell as it is te concept
            Cell cell =(Cell) cellIterator.next();                    
            while (cellIterator.hasNext()){
                cell =(Cell) cellIterator.next();
                String cellValue=cell.toString();
                //System.out.println(cellValue);
                if (!cellValue.isEmpty()){
                    thisConcept.setHeader(cellValue);
                }
            }
           //System.out.println("number of headers: "+thisConcept.headerList.size());
        }
        //If the concept is the same as the last concept, then add
        //the values in each column to the appropriate concept record
        else if (concept.equals(previousConcept)){
            //System.out.println("repeat line with same concept  "+concept);
            Dose currentDose=(Dose) currentSeries.doseList.get(currentSeries.doseList.size()-1);
            //System.out.println("(repeat) here is the currentDose value: "+currentDose.getNumber());
            Concept thisConcept=(Concept) currentDose.conceptList.get(currentDose.conceptList.size()-1);
            //System.out.println("this is the concept we will be adding values to: "+thisConcept.toString());
            int headerCount=thisConcept.headerList.size();
            //System.out.println("The "+thisConcept.toString()+" concept has "+headerCount+" headers.");
            for (int x=0; x<(headerCount);x++){
                int y=x+1;
                String value="";
                Cell cell=row.getCell(y);
                String cellValue="";
                //System.out.println(cell+"^"+cell.getCellType());
                if (cell.getCellType()==0){
                    if (DateUtil.isCellDateFormatted(cell)) {
                        //System.out.println("this is a date: "+cell+"&"+cell.getCellType());
                        Date cellDate=cell.getDateCellValue();
                        //System.out.println(cellDate+"^"+cellDate.toString());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                        //System.out.println("The date is: "+dateFormat.format(cellDate));
                        cellValue=dateFormat.format(cellDate);
                    }
                    else {
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        cellValue=cell.toString();
                    }
                }
                else {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    cellValue=cell.toString();
                }
                //get the header object for this column
                Header thisHeader=(Header) thisConcept.headerList.get(x);
                //System.out.println("the header we are going to add a value to: "+thisHeader.toString());
                //add the value of the cell to the header object
                thisHeader.setValue(cellValue);
                //System.out.println("the concept "+thisConcept.toString()+" now has "+thisHeader.valueList.size());
                //System.out.println("and the value is: "+cellValue);
            }
        }
    }
    }
}
