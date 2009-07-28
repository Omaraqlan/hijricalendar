package astroLib;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Calendar;

/**
 *
 * @author mehmetrg
 */
public class APC_Time {

//------------------------------------------------------------------------------
//
// GMST: Greenwich mean sidereal time
//
// Input:
//
//   MJD       Time as Modified Julian Date
//
// <return>:   GMST in [rad]
//*BU*
//------------------------------------------------------------------------------
public static double GMST (double MJD)
{
  //
  // Constants
  //*BU*
  double Secs = 86400.0;        // Seconds per day
  double pi2=2*Math.PI;

  //
  // Variables
  //
  double MJD_0, UT, T_0, T, gmst;


  MJD_0 = Math.floor ( MJD );
  UT    = Secs*(MJD-MJD_0);     // [s]
  T_0   = (MJD_0-51544.5)/36525.0;
  T     = (MJD  -51544.5)/36525.0;

  gmst  = 24110.54841 + 8640184.812866*T_0 + 1.0027379093*UT
          + (0.093104-6.2e-6*T)*T*T;      // [sec]

  return  (pi2/Secs)*(gmst%Secs);   // [Rad]
}
//------------------------------------------------------------------------------
//
// Mjd: Modified Julian Date from calendar date and time
//
// Input:
//
//   Year      Calendar date components
//   Month
//   Day
//   Hour      Time components (optional)
//   Min
//   Sec
//
// <return>:   Modified Julian Date
// *BU*
//------------------------------------------------------------------------------
public static double Mjd ( int Year, int Month, int Day,
             int Hour, int Min, double Sec )
{
  //
  // Variables
  //
  long    MjdMidnight;
  double  FracOfDay;
  int     b;


  if (Month<=2) { Month+=12; --Year;}

  if ( (10000L*Year+100L*Month+Day) <= 15821004L )
    b = -2 + ((Year+4716)/4) - 1179;     // Julian calendar
  else
    b = (Year/400)-(Year/100)+(Year/4);  // Gregorian calendar

  MjdMidnight = 365L*Year - 679004L + b + (int)(30.6001*(Month+1)) + Day;
  FracOfDay   = APC_Math.Ddd(Hour,Min,Sec) / 24.0;

  return MjdMidnight + FracOfDay;
}



//------------------------------------------------------------------------------
//
// CalDat: Calendar date and time from Modified Julian Date
//
// Input:
//
//   Mjd       Modified Julian Date
//
// Output:
//
//   Year      Calendar date components
//   Month
//   Day
//   Hour      Decimal hours
//
//------------------------------------------------------------------------------
public static Calendar CalDat ( double Mjd )
{
  //
  // Variables
  //*BU*
  long    a,b,c,d,e,f;
  double  FracOfDay,hour;
  int Year,Month,Day,Hour,Minute;
  Calendar cal = Calendar.getInstance();

  //GregorianCalendar.set (Year,Month,Day,Hour);
  //Date result=new Date (Year,Month,Day,Hour);
  // Convert Julian day number to calendar date
  a = (long)(Mjd+2400001.0);

  if ( a < 2299161 ) {  // Julian calendar
    b = 0;
    c = a + 1524;
  }
  else {                // Gregorian calendar
    b = (long)((a-1867216.25)/36524.25);
    c = a +  b - (b/4) + 1525;
  }

  d     = (long) ( (c-122.1)/365.25 );
  e     = 365*d + d/4;
  f     = (long)( (c-e)/30.6001 );

  Day   = (int) (c - e - (int) (30.6001*f));
  Month = (int) (f - 1 - 12 * (f / 14));
  Year  = (int) (d - 4715 - ((7 + Month) / 10));

  FracOfDay = Mjd - Math.floor(Mjd);

  hour = (24.0 * FracOfDay);//a dikkat/buray
  Minute =(int)Math.round((hour-(int)(hour))*60.0);
  //Minute =((int)(hour*60))%60;
  Hour=(int)hour;
  cal.set( Calendar.YEAR,Year);
  cal.set( Calendar.MONTH, Month-1 );
  cal.set( Calendar.DAY_OF_MONTH,Day );
  cal.set( Calendar.HOUR_OF_DAY, Hour );
  cal.set( Calendar.MINUTE,Minute);


 // cal.set(Year, Month, Day, Hour,0);
  return cal;
}

 public static String  DateTime(double Mjd) {

     //Calendar cal = Calendar.getInstance();
     Calendar cal=CalDat(Mjd);
     return cal.get(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR);
  }
static String  DateTimeHHMM(double Mjd) {

     //Calendar cal = Calendar.getInstance();
     Calendar  cal=CalDat(Mjd);
     return cal.get(Calendar.DAY_OF_MONTH)+"/"+
             (cal.get(Calendar.MONTH)+1)+"/"+
             cal.get(Calendar.YEAR)+" "+
             cal.get(Calendar.HOUR_OF_DAY)+":"+
             cal.get(Calendar.MINUTE);
  }


public  static String Time (double hour, boolean isMilitary)
    {
        String minuteStr, hourStr, amOrPm;
        amOrPm=" am";

        //int minute =((int)(hour*60))%60;
        int minute=(int)Math.round((hour-(int)(hour))*60.0);//More precise version of minute with round function
        hour=(int)hour;
        if  (minute==60)  {minute=0; hour++;}

         minuteStr =intTwoDigit(minute);
    
        if (isMilitary==true) {
            hourStr =intTwoDigit((int)hour);
            return hourStr + ":"+minuteStr;
        }
	    
        else {
	   	if (hour > 12)
	    	{ amOrPm=" pm";
                  hourStr=""+((int)hour - 12);
			}
			else if (hour > 0)
			    hourStr=""+(int)hour;
		    else
		    	hourStr="12";
                if (Integer.parseInt(hourStr)>9) hourStr=""+hourStr;
		    return hourStr + ":"+minuteStr+amOrPm;
			}
	        
}

public  static String SecTime (double hour)
    {
        int seconds=(((int)(hour*3600))%3600)%60;
        int minute =((int)(hour*60))%60;
        return intTwoDigit((int)(hour))+":"+intTwoDigit(minute)+":"+intTwoDigit(seconds);
}
    
 public final static String intTwoDigit(int i) {
         return ((i < 10) ? "0" : "") + i;
    }
public static String formatDoubleValue(double value){
             String doubleString = ""+value;
             String result = doubleString.substring(0,(doubleString.indexOf(".")+1+1));
             return result;
    }

}
