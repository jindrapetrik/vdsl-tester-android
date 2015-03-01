package com.jpexs.vdsltester.model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;

/**
 *
 * @author JPEXS
 */
public class ProgramLog {

    private static boolean souborOtevren=false;
    private static FileOutputStream logStream=null;

    public static final boolean SHOWDIRECTIONS=false;
    
    public static final int MODE_INCOMING=1;
    public static final int MODE_OUTCOMING=2;
    public static int mode=0;

    public static final int logMode=MODE_INCOMING;


    public static void startIncoming()
    {
       if(SHOWDIRECTIONS){
            print("<");
       }
       mode=MODE_INCOMING;
    }

    public static void startOutcoming()
    {
       if(SHOWDIRECTIONS)
       {
            print(">");
       }
       mode=MODE_OUTCOMING;
    }

    private static boolean kontrolaSouboru(){
        if(!souborOtevren){
            Calendar cal = Calendar.getInstance();
            String date = "" + cal.get(Calendar.DAY_OF_MONTH) + "." + (cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.YEAR);
            String min = ""+cal.get(Calendar.MINUTE);
            if(min.length()==1) min="0"+min;
            String hr = ""+cal.get(Calendar.HOUR_OF_DAY);
            if(hr.length()==1) hr="0"+hr;
            String time = hr + "_"+min;
            String file="programlog" + date + " " + time + ".txt";
            try {
                logStream = new FileOutputStream(file);
            } catch (FileNotFoundException ex) {
                return false;
            }
            souborOtevren=true;
        }
        return true;
    }

    public static void println(String s){
        if(!Main.isDebugMode()) return;
        if(kontrolaSouboru()){
            print(s);
            print("\r\n");
        }
    }

    public static void println(){
        println("");
    }

    public static void print(String s){
        if(!Main.isDebugMode()) return;
        if((logMode&mode)!=mode) return;
        if(kontrolaSouboru()){
            System.out.print(s);
            try {
                logStream.write(s.getBytes());
            } catch (IOException ex) {
            }
        }
    }

    public static void printException(Exception ex)
    {
         if(!Main.isDebugMode()) return;
         if(kontrolaSouboru()){
               ex.printStackTrace();
               ex.printStackTrace(new PrintStream(logStream));
         }
    }
    public static void close(){
        try {
            logStream.close();
        } catch (IOException ex) {

        }
        souborOtevren=false;
    }
}
