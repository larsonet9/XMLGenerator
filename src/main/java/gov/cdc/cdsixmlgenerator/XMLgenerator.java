 /*
 * This is intended to take Excel files of CDSi supporting data
 * and convert them into XML format.
 * Converts both Series and Immunity data from antigen specific spreadsheets
 * and Schedule specific data from the supporting spreadsheets
 *
 *Project Edit History
 *csn 20171102 - copied from original project and updated to handle
 *               conditionalSkip context for version 4.0 supporting data
 */
package gov.cdc.cdsixmlgenerator;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author csnewman
 */
public class XMLgenerator {
    
    public static void main(String[] args) {
        // create and load default properties
        Properties defaultProps = new Properties();
        DataInputStream in=null;
        try {
            //String filepath="\\\\cdc\\project\\NIP_ISD_Store1\\IRSB\\IISSB Projects\\Clinical Decision Support (CDSi)\\Publication\\Supporting Data\\XML generation V4_0+\\defaultProperties.txt";
            String filepath="C:\\Users\\Eric\\Documents\\NetBeansProjects\\CDSiXMLGenerator\\src\\main\\java\\gov\\cdc\\cdsixmlgenerator\\defaultProperties.txt";
            if (args.length>0){
                filepath=args[0];  //allow an override for the default properties file
            }
            File file=new File(filepath);           
            in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        }
        catch (Exception e){
            System.out.println("Default configuration file could not be found");
            System.exit(0);
        }
        try{
            defaultProps.load(in);
            in.close();
        }
        catch (Exception e){
            System.out.println("Could not process default configuration file");
            System.exit(0);
        }
        
        String mappingFile=defaultProps.getProperty("mappingFile");
        String inputFileDir=defaultProps.getProperty("inputFileDirectory");
        String outputFileDir=defaultProps.getProperty("outputFileDirectory");
        
        HashMap xmlTags=new HashMap();
        //A separate file isused to contain mappings from the Excel file to XML tags
        XMLmappings.readMappings(mappingFile,xmlTags);
        //read through the directory
        File fileDirectory=new File(inputFileDir);
        if (!fileDirectory.isDirectory()){
            System.out.println("initial directory doesn't exist");                   
        }        
        //For each file in the directory, call readOneFile for each antigen file
        //to read the spreadsheet, move the data to an intermediate
        //data structure and then print out the XML
        //Store all the supporting data to a Schedule object and
        //print out the XML after all the files in the directory have been read
        else {
            //pre-create a schedule object for the supporting data
            Schedule scheduleSupportingData=new Schedule();
            File[] files=fileDirectory.listFiles();
            String supportFiles="";
            for (File f:files){
                System.out.println(f);
                if (f.isFile()) {
                    String fName=f.getName();
                    if (fName.contains("AntigenSupportingData")){
                        readOneFile(f,xmlTags,outputFileDir);
                    }
                    else if (fName.contains("ScheduleSupportingData")){
                        if (fName.contains("Coded Observations")){
                            scheduleSupportingData.conditions=new Concept("Conditions");
                            scheduleSupportingData.readScheduleFile(f,scheduleSupportingData.conditions);
                            supportFiles="found";
                        }
                        //Contraindications were moved to the antigen files in version 3.0
                        else if (fName.contains("Contraindications")){
                            scheduleSupportingData.contraindications=new Concept("Contraindications");
                            scheduleSupportingData.readScheduleFile(f,scheduleSupportingData.contraindications);
                            supportFiles="found";
                        }
                        else if (fName.contains("CVX to Antigen Map")){
                            scheduleSupportingData.cvxToAntigen=new Concept("CVX to Antigen Map");
                            scheduleSupportingData.readScheduleFile(f,scheduleSupportingData.cvxToAntigen);
                            supportFiles="found";
                        }
                        else if (fName.contains("Live Virus Conflicts")){
                            scheduleSupportingData.liveConflict=new Concept("Live Virus Conflicts");
                            scheduleSupportingData.readScheduleFile(f,scheduleSupportingData.liveConflict);
                            supportFiles="found";
                        }
                        else if (fName.contains("Vaccine Group to Antigen Map")){
                            scheduleSupportingData.groupToAntigen=new Concept("Vaccine Group to Antigen Map");
                            scheduleSupportingData.readScheduleFile(f,scheduleSupportingData.groupToAntigen);
                            supportFiles="found";
                        }
                        else if (fName.contains("Vaccine Group")){
                            scheduleSupportingData.vaccineGroup=new Concept("Vaccine Group");
                            scheduleSupportingData.readScheduleFile(f,scheduleSupportingData.vaccineGroup);
                            supportFiles="found";
                        }
                        else{
                            System.out.println("File ("+fName+") is not recognized and will not be processed as supporting data.");
                        }
                    }
                    else {
                        System.out.println("File ("+fName+") is not recognized and will not be processed.");
                    }
                }
            }
            if (supportFiles=="found"){
                scheduleSupportingData.writeXML(scheduleSupportingData,outputFileDir);
            }
        }
    }
    
    private static void readOneFile(File file, HashMap xmlTags, String outputFileDir){
        String fileName=file.getName();
        //System.out.println(file);
        int dashLoc=fileName.lastIndexOf('-');
        int dotLoc=fileName.lastIndexOf('.');
        String antigenName=fileName.substring(dashLoc+2, dotLoc);
        //System.out.println(antigenName);
        Antigen currentAntigen=new Antigen(antigenName);
        currentAntigen.setInputFile(fileName);
        //System.out.println(currentAntigen.getName());
        try{
            FileInputStream fis=new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fis);
            int numberOfSheets = workbook.getNumberOfSheets();
            //System.out.println(numberOfSheets);
            for (int idx1=0; idx1<numberOfSheets; idx1++){
                Sheet sheet=workbook.getSheetAt(idx1);
                Iterator rowIterator = sheet.iterator();
                Row firstRow =(Row) rowIterator.next();
                Iterator cellIterator = firstRow.cellIterator();
                Cell firstCell=(Cell) cellIterator.next();
                if (sheet.getSheetName().equalsIgnoreCase("Immunity")){
                    Immunity currentImmunity=currentAntigen.setImmunity();
                    ImmunityToObject.readImmunityFile(currentImmunity,sheet);
                }
                if (sheet.getSheetName().equalsIgnoreCase("Contraindications")){
                    Contraindication currentContraindication=currentAntigen.setContraindication();
                    ContraindicationToObject.readContraindicationFile(currentContraindication,sheet);
                }
                if (firstCell.toString().equals("Series Name")){
                    Series currentSeries=currentAntigen.setSeries();
                    SeriesToObject.readSeriesFile(currentSeries,sheet);
                    //System.out.println(antigenName+"|"+cellIterator.next().toString()+"|"+sheet.getSheetName());
                }
            }
            fis.close();
            printOneFile(currentAntigen,xmlTags,outputFileDir);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    private static void printOneFile(Antigen currentAntigen, HashMap xmlTags, String outputFileDir){
        int seriesCount=currentAntigen.seriesList.size();
        if (seriesCount>0){
            String inputFile=currentAntigen.inputFile;
            int dotLoc=inputFile.lastIndexOf('.');
            String newName=inputFile.substring(0, dotLoc)+".xml";
            String newFile=outputFileDir+"\\"+newName;
            PrintWriter out=openWriter(newFile);
            //out.println(newName);
            out.println("<antigenSupportingData>");
            
            //print out the immunity data
            out.println("<immunity>");
            //there should only be one immunity object per antigen
            Immunity currentImmunity=(Immunity) currentAntigen.immunityList.get(0);
            Output.printImmunityOutput(currentImmunity,out);
            out.println("</immunity>");
            
            //print out the contraindication data
            out.println("<contraindications>");
            //there should only be one immunity object per antigen
            Contraindication currentContraindication=(Contraindication) currentAntigen.contraindicationList.get(0);
            Output.printContraindicationOutput(currentContraindication,out);
            out.println("</contraindications>");

            //System.out.println(seriesCount);
            for (int thisSeriesNum=0;thisSeriesNum<seriesCount;thisSeriesNum++){
                Series thisSeries=(Series) currentAntigen.seriesList.get(thisSeriesNum);
                //Output the XML file
                out.println("<series>");
                Output.printSeriesOutput(thisSeries,xmlTags,out);
                out.println("</series>");
            }
            out.println("</antigenSupportingData>");
            out.close();
        }
        
    }
    public static PrintWriter openWriter(String name){
        try{
            File file=new File(name);
            PrintWriter out=new PrintWriter(new BufferedWriter(new FileWriter(file)),true);
            return out;
        }
        catch (Exception e){
            System.out.println("couldn't create file");
            System.exit(0);
        }
        return null;
    }
}
