package com.jpexs.vdsltester.controller;

/**
 *
 * @author JPEXS
 */
public class Controller {
   public MainEventListener mainEventListener=new MainEventListener();
   public ConnectionEventListener connectionEventListener=new ConnectionEventListener();

   public Controller()
   {
      Arbiter.listen(new String[]{"connectingStart","exception","loggingInStart","doMeasureStart","finalupdateStart","finalupdateFinish","terminated","terminating"}, mainEventListener);
   }
}
