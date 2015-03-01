package com.jpexs.vdsltester.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jpexs.vdsltester.model.ProgramLog;

/**
 *
 * @author JPEXS
 */
public class Arbiter {
   static HashMap<String,List<MyListener>> listeners=new HashMap<String,List<MyListener>>();

   public static void listen(String events[],MyListener listener)
   {
      for(int i=0;i<events.length;i++){
         listen(events[i],listener);
      }
   }

   public static void listen(String event,MyListener listener)
   {
      List<MyListener> mylisteners=listeners.get(event);
      if(mylisteners==null){
         mylisteners=new ArrayList<MyListener>();
      }
      mylisteners.add(listener);
      listeners.put(event, mylisteners);
   }
   public static void inform(String event)
   {
      inform(event,null);
   }
   public static void inform(String event,Object data)
   {
       //ProgramLog.println("<inform "+event+">");
       List<MyListener> mylisteners=listeners.get(event);
        if(mylisteners!=null)
        {
           for(int i=0;i<mylisteners.size();i++){
              mylisteners.get(i).eventHandler(event,data);
           }
        }
   }
}
