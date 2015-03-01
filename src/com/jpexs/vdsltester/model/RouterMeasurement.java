package com.jpexs.vdsltester.model;

import java.util.List;

/**
 *
 * @author JPEXS
 */
public class RouterMeasurement {

   public String name;
   public String mode;
   public String profile;
   public String type;
   public String wanIP;
   public String SWVersion;
   public String upTime;
   public String linkTime;
   public String reconnect;
   public String status;

   public String US_max_rate;
   public String US_actual_rate;
   public String US_power;
   public String US_snr;
   public String US_inp;
   public String US_delay;

   public String DS_max_rate;
   public String DS_actual_rate;
   public String DS_power;
   public String DS_snr;
   public String DS_inp;
   public String DS_delay;

   public String U0_latn;
   public String U0_satn;
   public String U0_snr;
   public String U0_power;

   public String D1_latn;
   public String D1_satn;
   public String D1_snr;
   public String D1_power;

   public String U1_latn;
   public String U1_satn;
   public String U1_snr;
   public String U1_power;

   public String D2_latn;
   public String D2_satn;
   public String D2_snr;
   public String D2_power;

   public String U2_latn;
   public String U2_satn;
   public String U2_snr;
   public String U2_power;

   public String D3_latn;
   public String D3_satn;
   public String D3_snr;
   public String D3_power;

   public String U3_latn;
   public String U3_satn;
   public String U3_snr;
   public String U3_power;

   public ErrorMeasurement errorsAll;
   public ErrorMeasurement errorsPrevious1Day;
   public ErrorMeasurement errorsLatest1Day;
   public ErrorMeasurement errorsPrevious15Min;
   public ErrorMeasurement errorsLatest15Min;

   public List<Double> graphBits;
   public List<Double> graphSNR;
   public List<Double> graphQLN;
   public List<Double> graphHlog;

   public List<Band> USbandPlanFinal;
   public List<Band> DSbandPlanFinal;
   public List<Band> USbandPlanInitial;
   public List<Band> DSbandPlanInitial;

   @Override
   public String toString() {
      return "Modem: "+Utils.nvl(name)+"\r\n"
              +"Mode: "+Utils.nvl(mode)+"\r\n"
              +"Profile: "+Utils.nvl(profile)+"\r\n"
              +"Type: "+Utils.nvl(type)+"\r\n"
              +"WAN IP: "+Utils.nvl(wanIP)+"\r\n"
              +"SWVersion: "+Utils.nvl(SWVersion)+"\r\n"
              +"upTime: "+Utils.nvl(upTime)+"\r\n"
              +"linkTime: "+Utils.nvl(linkTime)+"\r\n"
              +"reconnect: "+Utils.nvl(reconnect)+"\r\n"
              +"\r\n"
              +"Down: \r\n"
              +" - actual rate: "+Utils.nvl(DS_actual_rate)+"\r\n"
              +" - max rate: "+Utils.nvl(DS_max_rate)+"\r\n"
              +" - power: "+Utils.nvl(DS_power)+"\r\n"
              +" - SNR: "+Utils.nvl(DS_snr)+"\r\n"
              +" - INP: "+Utils.nvl(DS_inp)+"\r\n"
              +" - delay: "+Utils.nvl(DS_delay)+"\r\n"
              +"Up: \r\n"
              +" - actual rate: "+Utils.nvl(US_actual_rate)+"\r\n"
              +" - max rate: "+Utils.nvl(US_max_rate)+"\r\n"
              +" - power: "+Utils.nvl(US_power)+"\r\n"
              +" - SNR: "+Utils.nvl(US_snr)+"\r\n"
              +" - INP: "+Utils.nvl(US_inp)+"\r\n"
              +" - delay: "+Utils.nvl(US_delay)+"\r\n"
              +"\r\n"
              +"U0: \r\n"
              +" - LATN: "+Utils.nvl(U0_latn)+"\r\n"
              +" - SATN: "+Utils.nvl(U0_satn)+"\r\n"
              +" - Margin: "+Utils.nvl(U0_snr)+"\r\n"
              +" - Power: "+Utils.nvl(U0_power)+"\r\n"
              +"D1: \r\n"
              +" - LATN: "+Utils.nvl(D1_latn)+"\r\n"
              +" - SATN: "+Utils.nvl(D1_satn)+"\r\n"
              +" - Margin: "+Utils.nvl(D1_snr)+"\r\n"
              +" - Power: "+Utils.nvl(D1_power)+"\r\n"
              +"U1: \r\n"
              +" - LATN: "+Utils.nvl(U1_latn)+"\r\n"
              +" - SATN: "+Utils.nvl(U1_satn)+"\r\n"
              +" - Margin: "+Utils.nvl(U1_snr)+"\r\n"
              +" - Power: "+Utils.nvl(U1_power)+"\r\n"
              +"D2: \r\n"
              +" - LATN: "+Utils.nvl(D2_latn)+"\r\n"
              +" - SATN: "+Utils.nvl(D2_satn)+"\r\n"
              +" - Margin: "+Utils.nvl(D2_snr)+"\r\n"
              +" - Power: "+Utils.nvl(D2_power)+"\r\n"
              +"U2: \r\n"
              +" - LATN: "+Utils.nvl(U2_latn)+"\r\n"
              +" - SATN: "+Utils.nvl(U2_satn)+"\r\n"
              +" - Margin: "+Utils.nvl(U2_snr)+"\r\n"
              +" - Power: "+Utils.nvl(U2_power)+"\r\n"
              +"D3: \r\n"
              +" - LATN: "+Utils.nvl(D3_latn)+"\r\n"
              +" - SATN: "+Utils.nvl(D3_satn)+"\r\n"
              +" - Margin: "+Utils.nvl(D3_snr)+"\r\n"
              +" - Power: "+Utils.nvl(D3_power)+"\r\n"
              +"U3: \r\n"
              +" - LATN: "+Utils.nvl(U3_latn)+"\r\n"
              +" - SATN: "+Utils.nvl(U3_satn)+"\r\n"
              +" - Margin: "+Utils.nvl(U3_snr)+"\r\n"
              +" - Power: "+Utils.nvl(U3_power)+"\r\n"
              +"Errors All:"+Utils.nvl(errorsAll)+"\r\n"
              +"Errors Latest 15 min:"+Utils.nvl(errorsLatest15Min)+"\r\n"
              +"Errors Previous 15 min:"+Utils.nvl(errorsPrevious15Min)+"\r\n"
              +"Errors Latest 1 day:"+Utils.nvl(errorsLatest1Day)+"\r\n"
              +"Errors Previous 1 day:"+Utils.nvl(errorsPrevious1Day)+"\r\n"
              +"Graph bits:\r\n"+Utils.graphToString(graphBits)+"\r\n"
              +"Graph SNR:\r\n"+Utils.graphToString(graphSNR)+"\r\n"
              +"Graph QLN:\r\n"+Utils.graphToString(graphQLN)+"\r\n"
              +"Graph Hlog:\r\n"+Utils.graphToString(graphHlog)+"\r\n"
              ;
   }

   public void fixBandPlans()
   {
      if((USbandPlanFinal!=null)&&(!USbandPlanFinal.isEmpty())){         
          USbandPlanFinal.get(0).from = 0;
          USbandPlanFinal.get(0).to = 55;
      }
      if((USbandPlanInitial!=null)&&(!USbandPlanInitial.isEmpty())){         
          USbandPlanInitial.get(0).from = 0;
          USbandPlanInitial.get(0).to = 55;
      }
      if((DSbandPlanFinal!=null)&&(!DSbandPlanFinal.isEmpty())){         
          DSbandPlanFinal.get(0).from = 56;
      }
      if((DSbandPlanInitial!=null)&&(!DSbandPlanInitial.isEmpty())){         
          DSbandPlanInitial.get(0).from = 56;
      }
   }


}
