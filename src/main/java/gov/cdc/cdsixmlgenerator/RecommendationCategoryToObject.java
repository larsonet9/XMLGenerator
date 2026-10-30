/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
 * @author Eric
 */
public class RecommendationCategoryToObject {
    
  public static void readRecommendationCategoryFile(RecommendationCategory currentVRC,Sheet sheet){
    Iterator rowIterator = sheet.iterator();
    //skip the first line 
    rowIterator.next();
    while (rowIterator.hasNext()) {
      Row row=(Row) rowIterator.next();

      //add each of the headers in the first row of the file
      Iterator cellIterator=row.cellIterator();                  
      while (cellIterator.hasNext()){
        Cell cell =(Cell) cellIterator.next();
        String cellValue=cell.toString();
        //System.out.println(cellValue);
        if (!cellValue.isEmpty()){
          currentVRC.setHeader(cellValue);
        }
      }
      
      //process the rest of the lines in the file
      //assume that the column count for each line of data is the same as number of headers
      int headerCount=currentVRC.headerList.size();
      while (rowIterator.hasNext()){
        row=(Row) rowIterator.next();
        for (int x=0; x<(headerCount);x++){
          //get the header object for this column
          Header thisHeader=(Header) currentVRC.headerList.get(x);
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
    }
  }
}
