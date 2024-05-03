/*
 * Takes data in one or more Series objects and creates the XML
 */
package gov.cdc.cdsixmlgenerator;

import java.io.PrintWriter;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import java.util.HashMap;

/**
 *
 * @author csnewman
 */
public class Output {
    static String xmlTag="";
    static String value="";
    static String xmlConceptTag="";
    static String textConcept="";
    static String xmlHeaderTag="";
    static String textHeader="";
    static String currentValue="";
    static int cvxFlag=0;
    
    public static void printImmunityOutput(Immunity currentImmunity,PrintWriter out){
        //At this point, hard code everything as there aren't any changes or evolution expected at this time
        //Print Clinical History Immunity from the first concept
        Concept clinHistory=(Concept) currentImmunity.conceptList.get(0);
        //get the number of values from the first header object
        Header guidelineHeader=(Header) clinHistory.headerList.get(0);
        int valueCount=guidelineHeader.valueList.size();
        for (int idx2=0;idx2<valueCount;idx2++){
            String guideline=(String) guidelineHeader.valueList.get(idx2);
            if (guideline.equalsIgnoreCase("n/a")) continue;
            printStartLine("clinicalHistory",out);
            int openPos=guideline.lastIndexOf('(');
            int closePos=guideline.lastIndexOf(')');
            String thisCode=guideline.substring(openPos+1,closePos);
            thisCode=thisCode.trim();
            String rest=guideline.substring(0,openPos);
            rest=rest.trim();
            printLineWrapper("guidelineCode",thisCode,out);
            printLineWrapper("guidelineTitle",rest,out);
            printEndLine("clinicalHistory",out);      
        }
        //Print Birth Date Immunity from the second concept
        Concept birthDate=(Concept) currentImmunity.conceptList.get(1);
        //get the number of values from the first header object
        Header dateHeader=(Header) birthDate.headerList.get(0);
        Header countryHeader=(Header) birthDate.headerList.get(1);
        Header exclusionHeader=(Header) birthDate.headerList.get(2);
        valueCount=dateHeader.valueList.size();
        //initialize the datecountry String
        String previousDateCountry="";
        //loop through each line in the Birth Date Immunity section
        for (int idx2=0;idx2<valueCount;idx2++){
            String date=(String) dateHeader.valueList.get(idx2);
            String country=(String) countryHeader.valueList.get(idx2);
            String exclusion=(String) exclusionHeader.valueList.get(idx2);
            //need to track when the combination of the date and country changes
            String dateCountry=date+country;
            //if both date and country are n/a then there is no data to format
            if (date.equalsIgnoreCase("n/a")&country.equalsIgnoreCase("n/a")) continue;
            //if date/country has changes, then print out
            //the date of birth and country data
            if (!dateCountry.equalsIgnoreCase(previousDateCountry)){
                printStartLine("dateOfBirth",out);
                if (date.isEmpty()|(date.equalsIgnoreCase("n/a"))){
                    printShortLine("immunityBirthDate",out);
                }
                else printLine("immunityBirthDate",date,out);
                if (country.isEmpty()|(country.equalsIgnoreCase("n/a"))){
                    printShortLine("birthCountry",out);
                }
                else printLine("birthCountry",country,out);
            }
            //there may be multiple exclusion conditions per date/country combination
            //but each exclusion condition will appear only on a single line
            //print out an "exclusion" wrapper tag and then the code and title before
            //closing the wrapper
                printStartLine("exclusion",out);
                int openPos=exclusion.lastIndexOf('(');
                int closePos=exclusion.lastIndexOf(')');
                String thisCode=exclusion.substring(openPos+1,closePos);
                thisCode=thisCode.trim();
                String rest=exclusion.substring(0,openPos);
                rest=rest.trim();
                printLineWrapper("exclusionCode",thisCode,out);
                printLineWrapper("exclusionTitle",rest,out);
                printEndLine("exclusion",out);
                
            // if this is the last line, then close the date of birth tag
            if (idx2==valueCount-1){
                printEndLine("dateOfBirth",out);
            }
            //if the next date/country is different than this one, then close the date of birth tag
            else {
                String nextDate=(String) dateHeader.valueList.get(idx2+1);
                String nextCountry=(String) countryHeader.valueList.get(idx2+1);
                String nextDateCountry=nextDate+nextCountry;
                if (!dateCountry.equals(nextDateCountry)){
                    printEndLine("dateOfBirth",out);
                }
            }
            //remember what this date/country value is for comparison later
            previousDateCountry=dateCountry;  
        }
    }
    
    public static void printContraindicationOutput(Contraindication currentContraindication,PrintWriter out){
        //At this point, hard code everything as there aren't any changes or evolution expected at this time
            
        //Print Antigen Contraindications from the first concept
        Concept groupLevel=(Concept) currentContraindication.conceptList.get(0);
        //get the number of values from the first header object
        Header contraindicationHeader=(Header) groupLevel.headerList.get(0);
        Header textHeader=(Header) groupLevel.headerList.get(1);
        Header guidanceHeader=(Header) groupLevel.headerList.get(2);
        Header beginHeader=(Header) groupLevel.headerList.get(3);
        Header endHeader=(Header) groupLevel.headerList.get(4);
        int valueCount=contraindicationHeader.valueList.size();

        for (int idx2=0;idx2<valueCount;idx2++){
            String code=(String) contraindicationHeader.valueList.get(idx2);
            if (code.equalsIgnoreCase("n/a")) continue;
            if (idx2==0) printStartLine("vaccineGroup",out);
            printStartLine("contraindication",out);
            int openPos=code.lastIndexOf('(');
            int closePos=code.lastIndexOf(')');
            String thisCode=code.substring(openPos+1,closePos);
            thisCode=thisCode.trim();
            String rest=code.substring(0,openPos);
            rest=rest.trim();
            printLineWrapper("observationCode",thisCode,out);
            printLineWrapper("observationTitle",rest,out);
            String text=(String) textHeader.valueList.get(idx2);
            printLineWrapper("contraindicationText",text,out);    
            String guidance=(String) guidanceHeader.valueList.get(idx2);
            printLineWrapper("contraindicationGuidance",guidance,out); 
            String beginAge=(String) beginHeader.valueList.get(idx2);
            printLineWrapper("beginAge",beginAge,out); 
            String endAge=(String) endHeader.valueList.get(idx2);
            printLineWrapper("endAge",endAge,out); 
            printEndLine("contraindication",out);
        }
        printEndLine("vaccineGroup",out);

        //Print Vaccine Level Contraindications from the second concept
        Concept vaccineLevel=(Concept) currentContraindication.conceptList.get(1);
        //get the number of values from the first header object
        Header contraindicationHeaderVaccine=(Header) vaccineLevel.headerList.get(0);
        Header textHeaderVaccine=(Header) vaccineLevel.headerList.get(1);
        Header guidanceHeaderVaccine=(Header) vaccineLevel.headerList.get(2);
        Header vaccineHeaderVaccine=(Header) vaccineLevel.headerList.get(3);
        Header beginHeaderVaccine=(Header) vaccineLevel.headerList.get(4);
        Header endHeaderVaccine=(Header) vaccineLevel.headerList.get(5);
        int valueCountVaccine=contraindicationHeaderVaccine.valueList.size();
        
        //initialize the previousGuideline String
        String previousContraindication="";
        for (int idx2=0;idx2<valueCountVaccine;idx2++){
            String code=(String) contraindicationHeaderVaccine.valueList.get(idx2);
            if (code.equalsIgnoreCase("n/a")) continue;
            if (idx2==0) printStartLine("vaccine",out);
            int openPos=code.lastIndexOf('(');
            int closePos=code.lastIndexOf(')');
            String thisCode=code.substring(openPos+1,closePos);
            thisCode=thisCode.trim();
            String rest=code.substring(0,openPos);
            rest=rest.trim();
            if (!code.equalsIgnoreCase(previousContraindication)){
                printStartLine("contraindication",out);
                printLineWrapper("observationCode",thisCode,out);
                printLineWrapper("observationTitle",rest,out);
                String text=(String) textHeaderVaccine.valueList.get(idx2);
                printLineWrapper("contraindicationText",text,out);
                String vaccineGuidance=(String) guidanceHeaderVaccine.valueList.get(idx2);
                printLineWrapper("contraindicationGuidance",vaccineGuidance,out);
            }
            printStartLine("contraindicatedVaccine",out);
            String vaccine=(String) vaccineHeaderVaccine.valueList.get(idx2);
            cvxValue(null,vaccine,out);    
            String beginAge=(String) beginHeaderVaccine.valueList.get(idx2);
            printLineWrapper("beginAge",beginAge,out); 
            String endAge=(String) endHeaderVaccine.valueList.get(idx2);
            printLineWrapper("endAge",endAge,out);  
            printEndLine("contraindicatedVaccine",out);
                
            // if the current set ID is not the same as the next setID then print the concept end tag
            if (idx2==valueCountVaccine-1){
                printEndLine("contraindication",out);
                printEndLine("vaccine",out);
            }
            else {
                String nextContraindication=(String) contraindicationHeaderVaccine.valueList.get(idx2+1);
                if (!code.equals(nextContraindication)){
                    printEndLine("contraindication",out);
                }
            }
            previousContraindication=code;            
        }
    }
    
    public static void printSeriesOutput(Series currentSeries,HashMap xmlTags,PrintWriter out){
        //Series level data
        xmlTag=(String) xmlTags.get("Series Name^");
        if (!xmlTag.isEmpty()){
            value=currentSeries.getName();
            if (value.isEmpty()){
                printShortLine(xmlTag,out);
            }
            else {
                printLine(xmlTag,value,out);
            }    
        }
        
        xmlTag=(String) xmlTags.get("Target Disease^");
        if (!xmlTag.isEmpty()){
            value=currentSeries.getDisease();
            if (value.isEmpty()){
                printShortLine(xmlTag,out);
            }
            else {
                printLine(xmlTag,value,out);
            }    
        }
        
        xmlTag=(String) xmlTags.get("Vaccine Group^");
        if (!xmlTag.isEmpty()){
            value=currentSeries.getGroup();
            if (value.isEmpty()){
                printShortLine(xmlTag,out);
            }
            else {
                printLine(xmlTag,value,out);
            }    
        }
        
        //series level data gets outputted
        int conceptCount=currentSeries.conceptList.size();
        for (int idx1=0;idx1<conceptCount;idx1++){
            Concept currentConcept=(Concept) currentSeries.conceptList.get(idx1);
            textConcept=currentConcept.toString();
            xmlConceptTag=(String) xmlTags.get(textConcept+"^");
            
            //From the first header, get a count of values that need to be dealt with
            Header headerForCount=(Header) currentConcept.headerList.get(0);            
            int valueCount=headerForCount.valueList.size();
            //Number of headers should be the same for all lines of values
            int headerCount=currentConcept.headerList.size();
            // If the concept is flagged as containing sets, then do a different logic
            // If we are dealing with sets, then the remaining line specific logic doesn't apply
            if (currentConcept.getType().equalsIgnoreCase("Set")) {
                printSetLine(currentConcept, valueCount, headerCount, xmlTags, out);
                continue; 
            }
            // If the concept is flagged as containing conditional skips, then do a different logic
            // If we are dealing with skips, then the remaining line specific logic doesn't apply
            if (currentConcept.getType().equalsIgnoreCase("enhancedSet")) {
                printEnhancedSetLine(currentConcept, valueCount, headerCount, xmlTags, out);
                continue; 
            }
            //for each line of values:
            for (int idx2=0;idx2<valueCount;idx2++){
                //  if the first value is n/a then print out the short line
                
                    Header firstHeader=(Header) currentConcept.headerList.get(0);
                    String firstValue=(String) firstHeader.valueList.get(0);
                    textHeader=firstHeader.toString();
                    xmlHeaderTag=(String) xmlTags.get(textConcept+"^"+textHeader);
                    if (firstValue.equalsIgnoreCase("n/a")){
                        //System.out.println(textHeader+"_"+xmlConceptTag+"_"+xmlHeaderTag);
                        if (xmlConceptTag!=null){
                            printShortLine(xmlConceptTag,out);
                        }
                        //this is used when there is only a single header for a concept
                        else if (xmlHeaderTag!=null){
                            printShortLine(xmlHeaderTag,out);
                        }
                    }
                    //  print out the concept start tag
                    //  loop through each header and print out the value
                    //  print out the concept end tag
                    else {
                        if (xmlConceptTag!=null){
                            printStartLine(xmlConceptTag,out);
                        }
                        for (int idx3=0;idx3<headerCount;idx3++){
                            Header currentHeader=(Header) currentConcept.headerList.get(idx3);
                            textHeader=currentHeader.toString();
                            if (textHeader.contains("(CVX)")){
                                cvxFlag=1;
                            }
                            else if(textHeader.contains("(MVX)")){
                                cvxFlag=2;
                            }
                            else if(textHeader.contains("(Code)")){
                                cvxFlag=3;
                            }
                            else cvxFlag=0;
                            xmlHeaderTag=(String) xmlTags.get(textConcept+"^"+textHeader);
                            currentValue=(String) currentHeader.valueList.get(idx2);
                            
                            //CVX code
                            if (cvxFlag==1){
                                if (currentValue.contains(")")){
                                    cvxValue(xmlHeaderTag,currentValue,out);
                                }
                                else {
                                    printShortLine(xmlHeaderTag,out);
                                } 
                            }
                            //MVX code
                            else if (cvxFlag==2){
                                if (currentValue.contains(")")){
                                    mvxValue(xmlHeaderTag,currentValue,out);
                                }
                                else {
                                    printShortLine(xmlHeaderTag,out);
                                    printShortLine("mvx",out);
                                }                                
                            }
                            //regular code
                            else if (cvxFlag==3){
                                if (currentValue.contains(")")){
                                    codeValue(xmlHeaderTag,currentValue,out);
                                }
                                else {
                                    printShortLine(xmlHeaderTag,out);
                                }                                
                            }
                            //neither CVX nor MVX nor regular code
                            else {
                                if (currentValue.equalsIgnoreCase("n/a")){
                                    printShortLine(xmlHeaderTag,out);
                                }
                                else {
                                    printLine(xmlHeaderTag,currentValue,out);
                                }
                            }
                        }
                        if (xmlConceptTag!=null) {
                            printEndLine(xmlConceptTag,out);
                        }
                    }   
                }
            }
            
        //Dose specific stuff starts here
        int doseCount=currentSeries.doseList.size();
        for (int idx0=0;idx0<doseCount;idx0++){
            printStartLine("seriesDose",out);
            Dose currentDose=(Dose) currentSeries.doseList.get(idx0);
            xmlTag=(String) xmlTags.get("Series Dose^");
            if (xmlTag.length()!=0){
                value=currentDose.getNumber();
                if (value.isEmpty()){
                    printShortLine(xmlTag,out);
                }
                else {
                    printLine(xmlTag,value,out);
                }    
            }
            int conceptCount2=currentDose.conceptList.size();
            for (int idx1=0;idx1<conceptCount2;idx1++){
                Concept currentConcept=(Concept) currentDose.conceptList.get(idx1);
                textConcept=currentConcept.toString();
                xmlConceptTag=(String) xmlTags.get(textConcept+"^");
                
                //From the first header, get a count of values that need to be dealt with
                Header headerForCount=(Header) currentConcept.headerList.get(0);
                int valueCount=headerForCount.valueList.size();
                //Number of headers should be the same for all lines of values
                int headerCount=currentConcept.headerList.size();
                // If the concept is flagged as containing sets, then do a different logic
                // If we are dealing with sets, then the remaining line specific logic doesn't apply
                if (currentConcept.getType().equalsIgnoreCase("Set")) {
                    printSetLine(currentConcept, valueCount, headerCount, xmlTags, out);
                    continue; //this may or may not work
                }
                // If the concept is flagged as containing conditional skips, then do a different logic
                // If we are dealing with skips, then the remaining line specific logic doesn't apply
                if (currentConcept.getType().equalsIgnoreCase("enhancedSet")) {
                    printEnhancedSetLine(currentConcept, valueCount, headerCount, xmlTags, out);
                    continue; 
                }
                //for each line of values:
                for (int idx2=0;idx2<valueCount;idx2++){
                    //  if the first value is n/a then print out the short line
                    //  Skip Dose is an exception, there we want to print out all headers
                    //  regardless of what the first value is
                
                    Header firstHeader=(Header) currentConcept.headerList.get(0);
                    String firstValue=(String) firstHeader.valueList.get(0);
                    textHeader=firstHeader.toString();
                    xmlHeaderTag=(String) xmlTags.get(textConcept+"^"+textHeader);
                     if (firstValue.equalsIgnoreCase("n/a")&!currentConcept.getName().equals("Skip Dose")&!currentConcept.getName().equals("Age")){  //added everything after the second &
                        if (xmlConceptTag!=null){
                            printShortLine(xmlConceptTag,out);
                        }
                        else if (xmlHeaderTag!=null){
                            printShortLine(xmlHeaderTag,out);
                        }
                     }
                    //  print out the concept start tag
                    //  loop through each header and print out the value
                    //  print out the concept end tag
                    else {
                        if (xmlConceptTag!=null){
                            printStartLine(xmlConceptTag,out);
                        }
                        for (int idx3=0;idx3<headerCount;idx3++){
                            Header currentHeader=(Header) currentConcept.headerList.get(idx3);
                            textHeader=currentHeader.toString();
                            
                            if (textHeader.contains("(CVX)")){
                                cvxFlag=1;
                            }
                            else if(textHeader.contains("(MVX)")){
                                cvxFlag=2;
                            }
                            else if(textHeader.contains("(Code)")){
                                cvxFlag=3;
                            }
                            else cvxFlag=0;
                            xmlHeaderTag=(String) xmlTags.get(textConcept+"^"+textHeader);
                            currentValue=(String) currentHeader.valueList.get(idx2);
                            // make sure volume doesn't get printed out in scientific notation
                            if(xmlHeaderTag != null && xmlHeaderTag.equalsIgnoreCase("Volume")){
                              //System.out.println("parseDouble = " + Double.valueOf((String)currentHeader.valueList.get(idx2)));
                              currentValue = Double.valueOf((String)currentHeader.valueList.get(idx2)).toString();
                            }
                            //CVX code
                            if (cvxFlag==1){
                                if (currentValue.contains(")")){
                                    cvxValue(xmlHeaderTag,currentValue,out);
                                }
                                else {
                                    printShortLine(xmlHeaderTag,out);
                                } 
                            }
                            //MVX code
                            else if (cvxFlag==2){
                                if (currentValue.contains(")")){
                                    mvxValue(xmlHeaderTag,currentValue,out);
                                }
                                else {
                                    printShortLine(xmlHeaderTag,out);
                                    printShortLine("mvx",out);
                                }                                
                            }
                            //regular code
                            else if (cvxFlag==3){
                                if (currentValue.contains(")")){
                                    codeValue(xmlHeaderTag,currentValue,out);
                                }
                                else {
                                    printShortLine(xmlHeaderTag,out);
                                }                                
                            }
                            //neither CVX nor MVX nor regular code
                            else {
                                if (currentValue.equalsIgnoreCase("n/a")){
                                    printShortLine(xmlHeaderTag,out);
                                }
                                else {
                                    printLine(xmlHeaderTag,currentValue,out);
                                }
                            }
                        }
                        if (xmlConceptTag!=null){
                            printEndLine(xmlConceptTag,out);
                        }
                    }   
                }
            }
            printEndLine("seriesDose",out);
        }
    }
    
    public static void printLineWrapper(String xmlTag, String value,PrintWriter out) {
        if (value.equalsIgnoreCase("n/a")){
            printShortLine(xmlTag,out);
        }
        else {
            printLine(xmlTag,value,out);
        }
    }
        
    public static void printLine(String xmlTag, String value,PrintWriter out){
        String openingTag="<"+xmlTag+">";
        String closingTag="</"+xmlTag+">";
        if(value.contains("≥"))
            value = value.replace("≥", "&#x2265;");
            
        out.println(openingTag+value+closingTag);
    }
    public static void printStartLine(String xmlTag,PrintWriter out){
        out.println("<"+xmlTag+">");
    }
    public static void printEndLine(String xmlTag,PrintWriter out){
        out.println("</"+xmlTag+">");
    }
    public static void printShortLine(String xmlTag,PrintWriter out){
        out.println("<"+xmlTag+"/>");
    }
    public static void cvxValue(String xmlTag, String value,PrintWriter out){
        int openPos=value.lastIndexOf('(');
        int closePos=value.lastIndexOf(')');
        //System.out.println(value+"&"+openPos+"&"+closePos);
        String cvx=value.substring(openPos+1,closePos);
        cvx=cvx.trim();
        String rest=value.substring(0,openPos);
        rest=rest.trim();
        if (xmlTag!=null){
            printStartLine(xmlTag,out);
        }            
        printLine("vaccineType",rest,out);
        printLine("cvx",cvx,out);
        if (xmlTag!=null){
            printEndLine(xmlTag,out);
        } 
    }
    public static void codeValue(String xmlTag, String value,PrintWriter out){
        int openPos=value.lastIndexOf('(');
        int closePos=value.lastIndexOf(')');
        //System.out.println(value+"&"+openPos+"&"+closePos);
        String code=value.substring(openPos+1,closePos);
        code=code.trim();
        String rest=value.substring(0,openPos);
        rest=rest.trim();
        if (xmlTag!=null){
            printStartLine(xmlTag,out);
        }            
        printLine("text",rest,out);
        printLine("code",code,out);
        if (xmlTag!=null){
            printEndLine(xmlTag,out);
        } 
    }
    public static void mvxValue(String xmlTag, String value,PrintWriter out){
        int openPos=value.lastIndexOf('(');
        int closePos=value.lastIndexOf(')');
        String mvx=value.substring(openPos+1,closePos);
        mvx=mvx.trim();
        String rest=value.substring(0,openPos);
        rest=rest.trim();
        printLine(xmlTag,rest,out);
        printLine("mvx",mvx,out);
    }
    private static void printSetLine(Concept currentConcept, int valueCount, int headerCount, HashMap xmlTags,PrintWriter out){
        //assume the first header is the set ID
        //If the first set ID is n/a then just print the concept short line and quit
        Header firstHeader=(Header) currentConcept.headerList.get(0);
        String firstValue=(String) firstHeader.valueList.get(0);
        if (firstValue.equalsIgnoreCase("n/a")){
            textConcept=currentConcept.toString();
            xmlConceptTag=(String) xmlTags.get(textConcept+"^");
            if (xmlConceptTag!=null){
                printShortLine(xmlConceptTag,out);
            }
            return;
        }
        //initialize previousSetID (use a string in case it's non-numeric)
        String previousSetID="";
        //Loop through each line (valueCount)
        Header setIDHeader=(Header) currentConcept.headerList.get(0);
        String textSetIDHeader=setIDHeader.toString();
        String xmlSetIDHeaderTag=(String) xmlTags.get(textConcept+"^"+textSetIDHeader);
        for (int idx2=0;idx2<valueCount;idx2++){
            
            String currentSetID=(String) setIDHeader.valueList.get(idx2);
            //If the current SetID (value of the first header) is not the same previousSetID then print the concept start tag
            if (!currentSetID.equalsIgnoreCase(previousSetID)){
                printStartLine(xmlConceptTag,out);
            }
            // print the header start tag but don't print the actual set ID
            printStartLine(xmlSetIDHeaderTag,out);
            
            // loop through the remaining headers and print out their values
            for (int idx3=1;idx3<headerCount;idx3++){
                Header currentHeader=(Header) currentConcept.headerList.get(idx3);
                textHeader=currentHeader.toString();

                if (textHeader.contains("(CVX)")){
                    cvxFlag=1;
                }
                else if(textHeader.contains("(MVX)")){
                    cvxFlag=2;
                }
                else cvxFlag=0;
                xmlHeaderTag=(String) xmlTags.get(textConcept+"^"+textHeader);
                currentValue=(String) currentHeader.valueList.get(idx2);
                 
                //CVX code
                if (cvxFlag==1){
                    if (currentValue.contains(")")){
                        cvxValue(xmlHeaderTag,currentValue,out);
                    }
                    else {
                        printShortLine(xmlHeaderTag,out);
                    } 
                }
                //MVX code
                else if (cvxFlag==2){
                    if (currentValue.contains(")")){
                        mvxValue(xmlHeaderTag,currentValue,out);
                    }
                    else {
                        printShortLine(xmlHeaderTag,out);
                        printShortLine("mvx",out);
                    }                                
                }
                //neither CVX nor MVX
                else {
                    if (currentValue.equalsIgnoreCase("n/a")){
                        printShortLine(xmlHeaderTag,out);
                    }
                    else {
                        printLine(xmlHeaderTag,currentValue,out);
                    }
                }
            }
            
            //  print the header end tag
            printEndLine(xmlSetIDHeaderTag,out);
                
            // if the current set ID is not the same as the next setID then print the concept end tag
            if (idx2==valueCount-1){
                printEndLine(xmlConceptTag,out);
            }
            else {
                String nextSetID=(String) setIDHeader.valueList.get(idx2+1);
                if (!currentSetID.equals(nextSetID)){
                    printEndLine(xmlConceptTag,out);
                }
            }
            previousSetID=currentSetID;
        }
    }
    private static void printEnhancedSetLine(Concept currentConcept, int valueCount, int headerCount, HashMap xmlTags,PrintWriter out){

        //*csn 20171102 udpate "headerList.get" to use 2 instead of 1
        //              to account for the new Skip Context
        Header firstID=(Header) currentConcept.headerList.get(2);
        String firstIDValue=(String) firstID.valueList.get(0);
        //If the first set ID is n/a then just print the concept short line and quit
        if (firstIDValue.equalsIgnoreCase("n/a")){
            textConcept=currentConcept.toString();
            xmlConceptTag=(String) xmlTags.get(textConcept+"^");
            if (xmlConceptTag!=null){
                printShortLine(xmlConceptTag,out);
            }
            return;
        }
        //Otherwise print the start tag for the first group of sets
        //*csn+3 20171102 handle this below because we might have multiple repetitions
        //else {
            //printStartLine(xmlConceptTag,out);
        //}
        
        //only print the Set Logic tag once as it applies to all sets
        //*csn+4 20171102 move this below so it can repeat once per set group
        //Header firstHeader=(Header) currentConcept.headerList.get(0);
        //String firstValue=(String) firstHeader.valueList.get(0);
        //String xmlSetLogicTag=(String) xmlTags.get(textConcept+"^"+"Set Logic");
        //printLine(xmlSetLogicTag,firstValue,out);
        
        //initialize previousSetID (use a string in case it's non-numeric)
        String previousSetID="-";
        //*csn+1 20171102 create a previousSetGrouper variable
        String previousSetGrouper="-";
        int conditionIDcount;
        int setIDLocation=0;
        int conditionIDLocation=0;
        
        //find out which columns contain the set ID and condition ID
        for (int idx4=0;idx4<headerCount;idx4++){
            Header thisHeader=(Header) currentConcept.headerList.get(idx4);
            String headerText=thisHeader.toString(); 
            if (headerText.equalsIgnoreCase("Set ID")){
                setIDLocation=idx4;
            }
            if (headerText.equalsIgnoreCase("Condition ID")){
                conditionIDLocation=idx4;
            }
        }
        
        //sanity check to make sure we found a Set ID column
        if (setIDLocation==0) {
            System.out.println("No Set ID column found, please check the format of the file");
            return;
        }
        //sanity check to make sure we found a Set ID column
        if (conditionIDLocation==0) {
            System.out.println("No Condition ID column found, please check the format of the file");
            return;
        }
        
        //Loop through each line (valueCount)
        Header setIDHeader=(Header) currentConcept.headerList.get(setIDLocation);
        String textSetIDHeader=setIDHeader.toString();
        String xmlSetIDHeaderTag=(String) xmlTags.get(textConcept+"^"+textSetIDHeader);
        
        //loop through all the lines in the concept
        for (int idx2=0;idx2<valueCount;idx2++){
            

            String currentSetID=(String) setIDHeader.valueList.get(idx2);
            //*csn+3 20171102 get the current Set Group (which we assume is the first column of data)
            Header setGrouperHeader=(Header) currentConcept.headerList.get(0);
            String textSetGrouperHeader=setGrouperHeader.toString();
            String currentSetGrouper=(String) setGrouperHeader.valueList.get(idx2);
            
            //*csn+19 20171102
            //If the set grouper changes, then close out the previous group
            //and start a new group
            if (!currentSetGrouper.equalsIgnoreCase(previousSetGrouper)){
                //only close the tag if it's not the first time through
                //(ie. there is no previous set group)
                if (!previousSetGrouper.equals("-")) {
                    printEndLine("set",out);
                    printEndLine(xmlConceptTag,out);
                }
                printStartLine(xmlConceptTag,out);  
                Header firstHeader=(Header) currentConcept.headerList.get(0);
                String firstValue=(String) firstHeader.valueList.get(idx2);
                String xmlSetGrouperTag=(String) xmlTags.get(textConcept+"^"+textSetGrouperHeader);
                printLine(xmlSetGrouperTag,firstValue,out);
                Header secondHeader=(Header) currentConcept.headerList.get(1);
                String secondValue=(String) secondHeader.valueList.get(idx2);
                String xmlSetLogicTag=(String) xmlTags.get(textConcept+"^"+"Set Logic");
                printLine(xmlSetLogicTag,secondValue,out);
            }
            
            // loop through the remaining headers and print out their values
            // start at an index corresponding to the set ID column
            for (int idx3=setIDLocation;idx3<headerCount;idx3++){
                //If this is the setID and the current SetID is not the same previousSetID then print the set start tag
                if (idx3==setIDLocation&(!currentSetID.equalsIgnoreCase(previousSetID))){
                    //if this is not the first set, then close the last set
                    //*csn+1 20171102 only close the set if the grouper is the same (otherwise we close it at the end
                    if ((idx2>0)&(currentSetGrouper.equalsIgnoreCase(previousSetGrouper))) {
                        printEndLine("set",out);
                    }
                    printStartLine("set",out);
                }
                
                //don't print the set level data if the setID hasn't changed
                if (currentSetID.equalsIgnoreCase(previousSetID)&(idx3<conditionIDLocation)){
                    continue;
                }
                
                //print the condition tag if this is the condition ID column
                if (idx3==conditionIDLocation){
                    printStartLine("condition",out);
                }
                
                Header currentHeader=(Header) currentConcept.headerList.get(idx3);
                textHeader=currentHeader.toString();

                if (textHeader.contains("(CVX)")){
                    cvxFlag=1;
                }
                else if(textHeader.contains("(MVX)")){
                    cvxFlag=2;
                }
                else cvxFlag=0;
                xmlHeaderTag=(String) xmlTags.get(textConcept+"^"+textHeader);
                currentValue=(String) currentHeader.valueList.get(idx2);
                 
                //CVX code
                if (cvxFlag==1){
                    if (currentValue.contains(")")){
                        cvxValue(xmlHeaderTag,currentValue,out);
                    }
                    else {
                        printShortLine(xmlHeaderTag,out);
                    } 
                }
                //MVX code
                else if (cvxFlag==2){
                    if (currentValue.contains(")")){
                        mvxValue(xmlHeaderTag,currentValue,out);
                    }
                    else {
                        printShortLine(xmlHeaderTag,out);
                        printShortLine("mvx",out);
                    }                                
                }
                //neither CVX nor MVX
                else {
                    if (currentValue.equalsIgnoreCase("n/a")){
                        printShortLine(xmlHeaderTag,out);
                    }
                    else {
                        printLine(xmlHeaderTag,currentValue,out);
                    }
                }
                //print the condition close tag if we know this is the last header
                if (idx3==headerCount-1){
                    printEndLine("condition",out);
                }
            }
            
            //  print the header end tag
            //printEndLine(xmlSetIDHeaderTag,out);
                
            previousSetID=currentSetID;
            //*csn+1 20171102 save off the current set group as the previous set grouper
            previousSetGrouper=currentSetGrouper;
            
            //*csn+5 20171102 because ConditionalSkip can now repeat, we can't close the set
            //based solely on the line cout
            //close the set if we know this is the last line
            //if (idx2==valueCount-1){
            //    printEndLine("set",out);
            //}
        }
        //Print the closing tag for this concept
        //*csn+1 20171102 close the final set
        printEndLine("set",out);
        printEndLine(xmlConceptTag,out);
    }
}
