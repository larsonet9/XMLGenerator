/*
 * This takes contraindication data and extracts it before saving it to a Contraindication object
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
public class ContraindicationToObject {
    
    public static void readContraindicationFile(Contraindication currentContraindication,Sheet sheet){

    ArrayList conceptList=new ArrayList();
    String concept="";
    String conceptMasterList="";
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

        //When a new concept is found, add it to the conceptMasterList,
        //create a new concept object and add it to the Immunity object
        //then add each header to the Concept object
        if (!conceptMasterList.contains(concept)){
            conceptMasterList=conceptMasterList+"^"+concept;
            previousConcept=concept;
            Concept thisConcept=currentContraindication.setConcept(concept);
            //flag the concept as containing sets or containing ordinary lines
            //this is used to output the XML differently
            if (row.getCell(1).toString().contains("Set")) {
                thisConcept.setType("Set");
                //System.out.println(thisConcept.getName());
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
            Concept thisConcept=(Concept) currentContraindication.conceptList.get(currentContraindication.conceptList.size()-1);
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
    }
}
