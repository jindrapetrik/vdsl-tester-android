package com.jpexs.vdsltester.model;

/**
 *
 * @author JPEXS
 */
public class ErrorMeasurement {
   public String US_ES;
   public String US_UAS;
   public String US_CRC;
   public String US_FEC;

   public String DS_ES;
   public String DS_UAS;
   public String DS_CRC;
   public String DS_FEC;

   public String detail;

   @Override
   public String toString() {
      return detail+"\r\n"
              +"Down:\r\n"
              +" - ES:"+Utils.nvl(DS_ES)+"\r\n"
              +" - UAS:"+Utils.nvl(DS_UAS)+"\r\n"
              +" - CRC:"+Utils.nvl(DS_CRC)+"\r\n"
              +" - FEC:"+Utils.nvl(DS_FEC)+"\r\n"
              +"Up:\r\n"
              +" - ES:"+Utils.nvl(US_ES)+"\r\n"
              +" - UAS:"+Utils.nvl(US_UAS)+"\r\n"
              +" - CRC:"+Utils.nvl(US_CRC)+"\r\n"
              +" - FEC:"+Utils.nvl(US_FEC)+"\r\n"
              ;
   }


}
