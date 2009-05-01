/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author http://www.cepmuvakkit.com
 */
public interface MoonPhases {
  public   double calculatePhase(double T);
}
class NewMoon implements  MoonPhases {

//------------------------------------------------------------------------------
//
// calculatePhase: Goal function is  finding  the new moon, briefly conjuction
//
// Input:
//
//   T         Ephemeris Time (Julian centuries since J2000)
//
// <return>:   Difference between the longitude of the Moon from the Sun
//             New Moon =0  (in [rad])
//
// Global:
//
//   Phase     Flag for desired lunar phase
//
//------------------------------------------------------------------------------
public  double calculatePhase(double T)
{
  //
  // Constants
  //
  double tau_Sun  = 8.32 / (1440.0*36525.0);    // 8.32 min  [cy]
  double l_Moon,l_Sun;
  double [] moonLongLat;
  double pi2=2*Math.PI;
  //
  // Variables
  //
  l_Sun=EclipticPosition.getMiniSunLongitude (T-tau_Sun);
  moonLongLat=EclipticPosition.getMiniMoon (T);//4.348761468846075
  l_Moon=moonLongLat[0];//-0.039226153787816305
 // beta=moonLongLat[1];


  double LongDiff = l_Moon - l_Sun;


  //if (LongDiff<0)  LongDiff=LongDiff+pi2;
  return    (LongDiff+Math.PI%pi2)-Math.PI;
}

}
 class CrescentMoon implements MoonPhases {

// EclipticPosition eclipPos;
//------------------------------------------------------------------------------
//
// PhasesFunc: Goal function for search of phase events [-pi, pi]
//
// Input:
//
//   T         Ephemeris Time (Julian centuries since J2000)
//
// <return>:   Difference between the longitude of the Moon from the Sun
//             and the nominal value for a given phase (New Moon 0, First
//             Quarter pi/2, etc.) (in [rad])
//
// Global:
//
//   Phase     Flag for desired lunar phase
//
//------------------------------------------------------------------------------
public double calculatePhase(double T)
{
  //
  // Constants
  //
  double tau_Sun  = 8.32 / (1440.0*36525.0);    // 8.32 min  [cy]
  double beta,l_Moon,l_Sun;
  double [] moonLongLat;
  double angle=8;// 8 degree; crescent visibility criteria
  //
  // Variables
  //
  l_Sun=EclipticPosition.getMiniSunLongitude (T-tau_Sun);
  moonLongLat=EclipticPosition.getMiniMoon (T);
  l_Moon=moonLongLat[0];
  beta=moonLongLat[1];


  double LongDiff = l_Moon - l_Sun;
  //We can take root mean square at small angles sin(a)=a
  double elongation = Math.sqrt(LongDiff*LongDiff+beta*beta);// RMS root mean square

  return   (Math.PI*angle/180.0-elongation);
}
 }