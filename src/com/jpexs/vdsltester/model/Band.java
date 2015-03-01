package com.jpexs.vdsltester.model;

/**
 *
 * @author JPEXS
 */
public class Band {
   public int from;
   public int to;

   public Band(int from, int to) {
      this.from = from;
      this.to = to;
   }

   @Override
   public String toString() {
      return "("+from+","+to+")";
   }

   public boolean contains(int carrier){
      return (carrier>=from)&&(carrier<=to);
   }

}
