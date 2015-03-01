package com.jpexs.vdsltester.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JPEXS
 */
public class Utils {

   public static List<String> getColumns(String s){
      return getColumns(s,0);
   }
   
   public static String replaceStr(String haystack,String needle,String replacement){
        String ret="";
        int pos=0;

        int npos=-1;
        do{
            npos=haystack.substring(pos).indexOf(needle);
            if(npos==-1)
                break;
            ret+=haystack.substring(pos,pos+npos)+replacement;
            pos=pos+npos+needle.length();
        }while(npos>-1);
        ret=ret+haystack.substring(pos);
        return ret;
    }
   
   public static List<String> getColumns(String s,int mincolumnCount){
      List<String> ret=new ArrayList<String>();
      s=s.trim();
      s=replaceStr(s, "\t", " ");
      while(s.indexOf(" ")>-1){
         ret.add(s.substring(0,s.indexOf(" ")));
         s=s.substring(s.indexOf(" "));
         s=s.trim();
      }
      if(!s.trim().equals("")){
         ret.add(s);
      }
      if(mincolumnCount>0){
        for(int i=ret.size();i<mincolumnCount;i++){
           ret.add("");
        }
      }
      return ret;
   }

   public static String formatSeconds(String time){      
      try{
         double timeD=Double.parseDouble(time);
         return formatSeconds(timeD);
      }catch(NumberFormatException nfe){
         
      }
      return null;
   }
           
   
   public static String formatSeconds(double timeD){
      String ret="";
      long time=(long)Math.floor(timeD);
      long msec=(long)((timeD-Math.floor(timeD))*1000);
      long sec=1;
      long min=60*sec;
      long hour=60*min;
      long day=hour*24;
      long remainder;
      long days= time /day;
      remainder = time - days*day;
      long hours = time / hour;
      remainder = time - hours * hour;
      long mins = remainder / min;
      remainder = remainder - mins * min;
      long secs = remainder;

      if(days>0){
         ret+=days+"d ";
      }
      if((hours>0)||(days>0)){
         ret+=hours+"h ";
      }
      if((hours>0)||(days>0)||(mins>0)){
         ret+=mins+"m ";
      }            
      ret+=secs+"s";    
      
      if(msec!=0){
         ret+= " "+msec + "ms";
      }
      return ret;
   }
   
   public static String getStringBetween(String a,String b,String content){
      if(content==null){
         return null;
      }
      if((a==null)&&(b==null)){
         return content;
      }
      if((a!=null)&&(b==null)){
         if(content.indexOf(a)==-1){
            return null;
         }
         return content.substring(content.indexOf(a)+a.length());
      }
      if((a==null)&&(b!=null)){
         if(content.indexOf(b)==-1){
            return null;
         }
         return content.substring(0,content.indexOf(b));
      }
      if((content.indexOf(a)==-1)||(content.indexOf(b)==-1)){
         return null;
      }
      return content.substring(content.indexOf(a)+a.length(),content.indexOf(b,content.indexOf(a)+a.length()));
   }

   public static String nvl(Object o){
      if(o==null){
         return "?";
      }else{
         return o.toString();
      }
   }

   public static String graphToString(List<Double> graph)
   {
      if(graph==null){
         return "?";
      }
      String ret="";
      for(int i=0;i<graph.size();i++){
         ret+=""+i+":"+graph.get(i)+"\r\n";
      }
      return ret;
   }



}
