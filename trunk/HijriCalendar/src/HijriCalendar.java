/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Calendar;
/**
 *
 * @author http://www.cepmuvakkit.com
 */
public class HijriCalendar {
     private Calendar cal;
     private double MJD;
     private String ismiSuhiri[]={
       "MUHARRAM","SAFAR","REBIULAVVAL","REBIULAHIR",
       "JAMIZIALAVVAL","JAMIZIALAHIR","RAJAB","SHABAN",
       "RAMADHAN","SHAVVAL", "ZILKADE","ZILHICCE"};
     private int Lunation;
     private int hijriYear,hijriMonth,hijriDay;
     private boolean[]  isFound;
     private double newMoonMoment; //Calculated time  for the New Moon in ModifiedJulianDays UTC
     private double crescentMoonMoment; // Calculated time for the Crescent Moon in ModifiedJulianDays UTC
                                        // which is solved according to the 8 degrees elongation angle.
 //    private double timeDifferenceforET_UT;  // Correction variable due to  difference of ephemeris time and universal time


    public HijriCalendar(int Year, int Month, int Day)

    {

      //  MJD = MJD0 + Hour/24.0;//
      // Constants
      this.MJD=ModifiedJulianDay.Mjd(Year, Month, Day, 0,0,0);
      cal=ModifiedJulianDay.ToCalendarDate(MJD);
    // System.out.println("MJD "+ModifiedJulianDay.DateTimeHHMM(MJD));
      final double synmonth=29.530588687;// Synodic Month Period
      final double dT  = 7.0 / 36525.0;           // Step (1 week)
      final double dTc  = 3.0 / 36525.0;          // Step (3 days)
      final double Acc = (0.5/1440.0) / 36525.0;  // Desired Accuracy (0.5 min)
      final double MJD_J2000=51544.5;
      final double MLunatBase=23435.5;  //  Modified Base date for E. W. Brown's numbered  series of
                                        //  lunations (1923 January 16) 2423436-2400000.5=23435.5

      
      double  Tnow,T0,T1,TNewMoon,TCrescent; // Time( Ephemeris:disabled) in Julian centuries since J2000
      double  D0, D1;  //Difference between the longitude of the Moon from the Sun.
      Tnow = (MJD-MJD_J2000 ) / 36525.0;//0.09477070499657769
      T1=Tnow;//-1/36525.0;//0.09474332648870637 24/6/2009
      T0 = T1 - dT;  // decrease 1 week,//0.09455167693360712
      isFound= new boolean[1];
      isFound[0]=false;
      // Search for phases   bracket desired phase event
       MoonPhases newMoon=new NewMoon();
       MoonPhases crescentMoon=new CrescentMoon();
       D1 =newMoon.calculatePhase(T1);//0.044833495303684856
       D0 =newMoon.calculatePhase(T0);//-1.5562339964369751
       while ( (D0*D1>0.0) || (D1<D0) ) {
           T1=T0; D1=D0; T0-=dT; D0=newMoon.calculatePhase(T0);//Finds correct week for iteration
       }
      // Iterate NewMoon time
      TNewMoon=RootFinder (newMoon,T0,T1, Acc,isFound);
      // Correct for difference of ephemeris time and universal time currently disabled
      // ETminUT ( TPhase, ET_UT, valid );
      newMoonMoment = ( TNewMoon*36525.0+MJD_J2000);// - ET_UT/86400.0;
      //System.out.println("newMoonMoment "+ModifiedJulianDay.DateTimeHHMM(newMoonMoment ));
      Lunation=(int) Math.floor(( newMoonMoment+7-MLunatBase)/synmonth)+1;
      hijriYear= (Lunation+5)/12+1341;
      hijriMonth= (Lunation+5)%12;// Returns 0 for Zilhicce and 1 for Muharrem 2 for Safer  ....
      if (hijriMonth==0) hijriMonth=12;

      //hijriMonth= (Lunation+5)%12;//Starting from 0..11  1=Muharrem 11=Zilhicce
      if ( isFound[0]){
          TCrescent=RootFinder(crescentMoon,TNewMoon,TNewMoon+dTc,Acc,isFound);
          crescentMoonMoment=  TCrescent*36525.0+MJD_J2000;
         // System.out.println("crescentMoonMoment "+ModifiedJulianDay.DateTimeHHMM(crescentMoonMoment ));
      }
       //0.279166666666667 comes from the hours 5:18 am
   //  System.out.println(" MJD:" +(MJD));
        hijriDay=(int) (MJD-Math.round(crescentMoonMoment+0.279166666666667))+1;
        if (hijriDay==0)
        {   hijriDay=30;
            hijriMonth--;
            if (hijriMonth==0) hijriMonth=12;
        }
       

    }
   
 public int getHijriYear()
    {
       
       //  1341 (29 CemuzuyelEvvel) is the hicri day for the 17 January 1923
       // which is the start day of the Brown's Lunation Number;
        return hijriYear;
    }
    public String getHijriMonthName()
    {
  //   System.out.println(hijriMonth);
        return ismiSuhiri[(hijriMonth-1)];
    }
    public int getHijriMonth()
    {
      
        return hijriMonth;
    }
    public int getHijriDay()
    {  
        return hijriDay;
    }
    public String getHicriTakvim ()
    {
       return getHijriDay()+ " " + getHijriMonthName()+ " " +getHijriYear();
    }
 /**
      * 1 Muharrem=Hijri New Year
      * 10 Muharrem= Day of Ashura
      * 11/12 Rebiulevvel= Mawlid-al Nabi
      * 1 Recep=Start of Holy Months
      * 1st Cuma day on Recep= Lailatul-Raghaib
      * 27 Recep=Lailatul-Me'rac
      * 14/15 Nisfu-Sha'aban
      * 1 Ramadhan=1. Day of Ramadhan
      * 27 Ramadhan= Lailatul-Qadr
      * 1 Sevval=1. Day of Eid-al-Fitr
      * 2 Sevval=2. Day of Eid-al-Fitr
      * 3 Sevval=3. Day of Eid-al-Fitr
      * 9 ZiLHiCCE= A'rafa
      * 10 Zilhicce= 1. Day of Eid-al-Adha
      * 11 Zilhicce= 2. Day of Eid-al-Adha
      * 12 Zilhicce= 3. Day of Eid-al-Adha
      * 13 Zilhicce= 4. Day of Eid-al-Adha
      * @return
      */
   public String checkIfHolyDay ()
        {
          //  cal=ModifiedJulianDay.CalDat(MJD) ;
            String holyDay="";
            switch (hijriMonth) {
            case 1: if (hijriDay==1) holyDay="NEWYEAR";
            else if (hijriDay==10) holyDay="ASHURA";
            break;
            case 3 : if ((hijriDay==11)||(hijriDay==12)) holyDay="MAWLID";
            break;
            case 7 :
                if ((hijriDay==1)&&(hijriMonth==7)) holyDay="HOLYMONTHS";
                if ((cal.get(Calendar.DAY_OF_WEEK)==6)&&(hijriDay<7)) holyDay="RAGHAIB";
                if (hijriDay==27) holyDay="MERAC";
            break;
            case 8: if (/*(hijriDay==14)||*/(hijriDay==15)) holyDay="BARAAT";
            break;
            case 9: if ((hijriDay==27)) holyDay="QADR";
            break;
                case 10:if ((hijriDay==1)||(hijriDay==2)||(hijriDay==3)) holyDay=hijriDay+"DAYOFEIDFITR";
            break;

            case 12:
                if (hijriDay==9) holyDay="AREFE";
                if ((hijriDay==10)||(hijriDay==11)||(hijriDay==12)||(hijriDay==13)) holyDay=(hijriDay-9)+"DAYOFEIDAHDA";
            break;
            }

            return holyDay;


   }

   public String getDay() {
        String daysName[]={"SUNDAY", "MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"};
    	return daysName[cal.get(Calendar.DAY_OF_WEEK)-1];

    }


 //------------------------------------------------------------------------------
//
// Pegasus: Root finder using the Pegasus method
//
// Input:
//
//   PegasusFunct  Pointer to the function to be examined
//
//   LowerBound    Lower bound of search interval
//   UpperBound    Upper bound of search interval
//   Accuracy      Desired accuracy for the root
//
// Output:
//
//   Root          Root found (valid only if Success is true)
//   Success       Flag indicating success of the routine
//
// References:
//
//   Dowell M., Jarratt P., 'A modified Regula Falsi Method for Computing
//     the root of an equation', BIT 11, p.168-174 (1971).
//   Dowell M., Jarratt P., 'The "PEGASUS Method for Computing the root
//     of an equation', BIT 12, p.503-508 (1972).
//   G.Engeln-Muellges, F.Reutter, 'Formelsammlung zur Numerischen
//     Mathematik mit FORTRAN77-Programmen', Bibliogr. Institut,
//     Zuerich (1986).
//
// Notes:
//
//   Pegasus assumes that the root to be found is bracketed in the interval
//   [LowerBound, UpperBound]. Ordinates for these abscissae must therefore
//   have different signs.
//
//------------------------------------------------------------------------------
public static double RootFinder (MoonPhases moonPhase, double LowerBound, double UpperBound, double  Accuracy, boolean[] Success )
{


 double x1=LowerBound;
 double x2=UpperBound;
 double f1 = moonPhase.calculatePhase(x1);
 double f2 = moonPhase.calculatePhase(x2);
 double x3 ,f3,Root;
 int MaxIterat = 30;

  int Iterat = 0;

   // Initialization
  Success[0] = false;
  Root    = x1;


  // Iteration
  if ( f1 * f2 < 0.0 )
    do
    {
      // Approximation of the root by interpolation
      x3 = x2 - f2/( (f2-f1)/(x2-x1) ); f3 = moonPhase.calculatePhase(x3);

      // Replace (x1,f2) and (x2,f2) by new values, such that
      // the root is again within the interval [x1,x2]
      if ( f3 * f2 <= 0.0 ) {
        // Root in [x2,x3]
        x1 = x2; f1 = f2; // Replace (x1,f1) by (x2,f2)
        x2 = x3; f2 = f3; // Replace (x2,f2) by (x3,f3)
      }
      else {
        // Root in [x1,x3]
        f1 = f1 * f2/(f2+f3); // Replace (x1,f1) by (x1,f1')
        x2 = x3; f2 = f3;     // Replace (x2,f2) by (x3,f3)
      }

      if (Math.abs(f1) < Math.abs(f2))
       Root = x1;
      else
        Root = x2;

      Success[0]  = (Math.abs(x2-x1) <= Accuracy);
      Iterat++;
    }
    while ( !Success[0]  && (Iterat<MaxIterat) );



 return Root;
}

/*
//------------------------------------------------------------------------------
//
// ETminUT: Difference ET-UT of ephemeris time and universal time
//
// Input:
//
//   T         Time in Julian centuries since J2000
//
// Output:
//
//   DTsec     ET-UT in [s]
//   valid     Flag indicating T in domain of approximation
//
// Notes: The approximation spans the years from 1825 to 2005
//
//------------------------------------------------------------------------------
void getETDifferenceInMinUT (double T, double& DTsec, bool& valid)
{
  //
  // Variables
  //
  int    i = (int) floor(T/0.25);
  double t = T-i*0.25;


  if ( (T<-1.75) || (0.05<T) ) {
    valid = false;
    DTsec = 0.0;
  }
  else {
    valid = true;
    switch (i) {
      case -7: DTsec=10.4+t*(-80.8+t*( 413.9+t*( -572.3))); break; // 1825-
      case -6: DTsec= 6.6+t*( 46.3+t*(-358.4+t*(   18.8))); break; // 1850-
      case -5: DTsec=-3.9+t*(-10.8+t*(-166.2+t*(  867.4))); break; // 1875-
      case -4: DTsec=-2.6+t*(114.1+t*( 327.5+t*(-1467.4))); break; // 1900-
      case -3: DTsec=24.2+t*( -6.3+t*(  -8.2+t*(  483.4))); break; // 1925-
      case -2: DTsec=29.3+t*( 32.5+t*(  -3.8+t*(  550.7))); break; // 1950-
      case -1: DTsec=45.3+t*(130.5+t*(-570.5+t*( 1516.7))); break; // 1975-
      case  0: t+=0.25;
               DTsec=45.3+t*(130.5+t*(-570.5+t*( 1516.7)));        // 2000-
    }                                                              // 2005
  }
}*/
}
