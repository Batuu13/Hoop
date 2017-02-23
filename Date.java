package com.batuhanyaman.huchat;

import java.util.Calendar;

public class Date {

    //// pattern -> dd.mm.yyyy_MM:HH

   public static String getDate(String pattern)
    {
        try {
            String[] str = pattern.split("_");
            String[] str2 = str[0].split("\\.");
            String date = str2[0];
            date += " " + getMonthName(Integer.parseInt(str2[1]));
            date += " " + str2[2];

            return date;
        }catch (Exception e)
        {
         
            return pattern;
        }

    }
   /*
    * It returns as pattern
    */
   	public static String getDate()
   	{
   		return String.format("%d.%d.%d_%02d:%02d",Calendar.getInstance().get(Calendar.DAY_OF_MONTH),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE));
   	}
    private static String getMonthName(int i)
    {
        switch (i)
        {
            case 1:
                return "Ocak";
            case 2:
                return "Şubat";

            case 3:
                return "Mart";

            case 4:
                return "Nisan";

            case 5:
                return "Mayıs";

            case 6:
                return "Haziran";

            case 7:
                return "Temmuz";

            case 8:
                return "Ağustos";

            case 9:
                return "Eylül";

            case 10:
                return "Ekim";

            case 11:
                return "Kasım";

            case 12:
                return "Aralık";

            default:
                return "" + i;

        }
    }

    public static String getTime(String pattern){
        try {
            String[] str = pattern.split("_");

            return str[1];
        }catch (Exception e)
        {
            return pattern;
        }

    }

    public static boolean isDifferent(String date1,String date2)
    {
        if(!date1.equals(date2))
            return true;
        else
            return false;
           }
}
