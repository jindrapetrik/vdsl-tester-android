package com.jpexs.vdsltester.controller;


import com.jpexs.vdsltester.model.Main;

/**
 *
 * @author JPEXS
 */
public class MainEventListener implements MyListener{

	@Override
	public void eventHandler(String event, Object data) {
		
		
	}
	/*implements EventListener,MyListener{


   @Override
   public void onEvent(Event event) {
      String action="NOTHING";
      if(event.type==ControlEvent.PRESSED)
      {
          ControlEvent cevent=(ControlEvent)event;
          action=cevent.action;
      }
      if (event.type == MenuEvent.SELECTED) {
            MenuEvent mev = (MenuEvent) event;
            action=((MenuItem)mev.selectedItem).action;
      }
      
      if(event.type==PenEvent.PEN_MOVE){
         if(event.target instanceof GraphPanel)
         {
               GraphPanel graph=(GraphPanel)event.target;
               PenEvent pe=(PenEvent)event;
               GraphFrame graphFrame=(GraphFrame)graph.getParent();
               graphFrame.setDisplay(pe.x,pe.y);
         }
      }


       if(action.startsWith("TABSWITCH_")){
          String cname=action.substring(10);
          if(!Main.view.mainForm.getSelectedCard().equals(cname))
          {
               Main.view.setCard(cname);
               Main.updateValues(cname);
          }
       }
       if(action.equals("CLOSE")){
          Main.exit();
       }
       if(action.equals("CHANGEMODEM")){
            model.Main.view.showConfig();
       }
      
   }

   @Override
   public void eventHandler(String event,Object data) {

      //"connectingStart","exception","loggingInStart","doMeasureStart","finalupdateStart","finalupdateFinish"

      if(model.Main.view==null){
         return;
      }
      if(model.Main.view.mainForm==null){
         return;
      }
      if(model.Main.view.mainForm.statusDisplay==null){
         return;
      }
      StatusDisplay status=model.Main.view.mainForm.statusDisplay;

      if(event.equals("connectingStart")){
         status.setStatusTextAndType(model.Main.view.language.connecting,StatusDisplay.TYPE_YELLOW);
      }
      if(event.equals("exception")){
         status.setStatusTextAndType(model.Main.view.language.cannotConnect,StatusDisplay.TYPE_RED);
      }
      if(event.equals("loggingInStart")){
         status.setStatusTextAndType(model.Main.view.language.loggingIn,StatusDisplay.TYPE_YELLOW);
      }

      if(event.equals("doMeasureStart")){
         status.setStatusTextAndType(model.Main.view.language.measuring,StatusDisplay.TYPE_GREEN);
      }

      if(event.equals("finalupdateStart")){
         status.setStatusTextAndType(model.Main.view.language.updatingView,StatusDisplay.TYPE_GREEN);
      }

      if(event.equals("finalupdateFinish")){
         status.setDisplayed(false);
      }
      
      if(event.equals("terminating")){
         Main.terminating();
      }
      if(event.equals("terminated")){
         
      }
      
   }*/

}
