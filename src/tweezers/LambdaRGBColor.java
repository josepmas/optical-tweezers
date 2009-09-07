package tweezers;

import java.awt.*;
import java.awt.color.*;

/**
 * <p>Title: Experimento de Young: Metodos de Color</p>
 * <p>Description: Metodos para transformar longitud de onda a RGB</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author R. Tudela
 * @version 1.0
 */

public class LambdaRGBColor {
    //
    // CIE 1964 chromaticity coordinates of spectral stimuli
    // (only xy,  z = 1 - x - y)
    // from G. Wyszecki & W.S. Stiles, "Color Science: concepts and Methods, Quantitative Data and Formulas" John Wiley & Sons, New York 1967
    static double spectrum_xy[][] = {
    {380.0,0.1813, 0.0197}, {385.0,0.1809, 0.0195}, {390.0,0.1803, 0.0194}, {395.0,0.1795, 0.0190},
    {400.0,0.1784, 0.0187}, {405.0,0.1771, 0.0184}, {410.0,0.1755, 0.0181}, {415.0,0.1732, 0.0178},
    {420.0,0.1706, 0.0179}, {425.0,0.1679, 0.0187}, {430.0,0.1650, 0.0203}, {435.0,0.1622, 0.0225},
    {440.0,0.1590, 0.0257}, {445.0,0.1554, 0.0300}, {450.0,0.1510, 0.0364}, {455.0,0.1459, 0.0452},
    {460.0,0.1389, 0.0589}, {465.0,0.1295, 0.0779}, {470.0,0.1152, 0.1090}, {475.0,0.0957, 0.1591},
    {480.0,0.0728, 0.2292}, {485.0,0.0452, 0.3275}, {490.0,0.0210, 0.4401}, {495.0,0.0073, 0.5625},
    {500.0,0.0056, 0.6745}, {505.0,0.0219, 0.7526}, {510.0,0.0495, 0.8023}, {515.0,0.0850, 0.8170},
    {520.0,0.1252, 0.8102}, {525.0,0.1664, 0.7922}, {530.0,0.2071, 0.7663}, {535.0,0.2436, 0.7399},
    {540.0,0.2786, 0.7113}, {545.0,0.3132, 0.6813}, {550.0,0.3473, 0.6501}, {555.0,0.3812, 0.6182},
    {560.0,0.4142, 0.5858}, {565.0,0.4469, 0.5531}, {570.0,0.4790, 0.5210}, {575.0,0.5096, 0.4904},
    {580.0,0.5386, 0.4614}, {585.0,0.5654, 0.4346}, {590.0,0.5900, 0.4100}, {595.0,0.6116, 0.3884},
    {600.0,0.6306, 0.3694}, {605.0,0.6471, 0.3529}, {610.0,0.6612, 0.3388}, {615.0,0.6731, 0.3269},
    {620.0,0.6827, 0.3173}, {625.0,0.6898, 0.3102}, {630.0,0.6955, 0.3045}, {635.0,0.7010, 0.2990},
    {640.0,0.7059, 0.2941}, {645.0,0.7103, 0.2898}, {650.0,0.7137, 0.2863}, {655.0,0.7156, 0.2844},
    {660.0,0.7168, 0.2832}, {665.0,0.7179, 0.2821}, {670.0,0.7187, 0.2813}, {675.0,0.7193, 0.2807},
    {680.0,0.7198, 0.2802}, {685.0,0.7200, 0.2800}, {690.0,0.7202, 0.2798}, {695.0,0.7203, 0.2797},
    {700.0,0.7204, 0.2796}, {705.0,0.7203, 0.2797}, {710.0,0.7202, 0.2798}, {715.0,0.7201, 0.2799},
    {720.0,0.7199, 0.2801}, {725.0,0.7197, 0.2803}, {730.0,0.7195, 0.2806}, {735.0,0.7192, 0.2808},
    {740.0,0.7189, 0.2811}, {745.0,0.7186, 0.2814}, {750.0,0.7183, 0.2817}, {755.0,0.7180, 0.2820},
    {760.0,0.7176, 0.2824}, {765.0,0.7172, 0.2828}, {770.0,0.7169, 0.2831}, {775.0,0.7165, 0.2835},
    {780.0,0.7161, 0.2839}};
    int n_lambdas=81;

  // constructor
  public LambdaRGBColor() {
  }

  // lambda2RGB funcion
  public Color lambda2RGB(Parametres par, int alpha){
    //alpha va de 0 a 255
    int lambda = (int)(par.lambda/par.scaleL);
    double lambda_x=0.0,lambda_y=0.0;
    double r,g,b;
    ColorSpace cs=ColorSpace.getInstance(ColorSpace.CS_sRGB);
    float coordcolor[]={0,0,0},coordrgb[]={0,0,0};
    float hsb[]={0,0,0};

    for(int i=0; i<n_lambdas-1; i++){ // interpolamos linealmente la coordenada de lamba introducida entre las conocidasa las conocidas
      if(lambda>=spectrum_xy[i][0] && lambda<=spectrum_xy[i+1][0]){
        lambda_x=spectrum_xy[i][1]+((((double)lambda)-spectrum_xy[i][0])/(spectrum_xy[i+1][0]-spectrum_xy[i][0]))*(spectrum_xy[i+1][1]-spectrum_xy[i][1]);
        lambda_y=spectrum_xy[i][2]+((((double)lambda)-spectrum_xy[i][0])/(spectrum_xy[i+1][0]-spectrum_xy[i][0]))*(spectrum_xy[i+1][2]-spectrum_xy[i][2]);
        //System.out.println("lambdas: "+lambda+" entre "+spectrum_xy[i][0]+" y "+spectrum_xy[i+1][0]);
        break;
      }
    }

    coordcolor[0]=(float)(lambda_x);
    coordcolor[1]=(float)(lambda_y);
    coordcolor[2]=(float)(1.0-lambda_x-lambda_y);
    if(coordcolor[2]<0) coordcolor[2]=0;
    coordrgb=cs.fromCIEXYZ(coordcolor);
    r=coordrgb[0];
    g=coordrgb[1];
    b=coordrgb[2];
    //System.out.println("xyz: "+coordcolor[0]+" "+coordcolor[1]+" "+coordcolor[2]);
    //System.out.println("rgb: "+r+" "+g+" "+b);

    if (lambda > 780 || lambda < 400){
        Color gris = new Color (220,220,220,(int)alpha);//(java.awt.Color.LIGHT_GRAY.getRed(),java.awt.Color.LIGHT_GRAY.getGreen(),java.awt.Color.LIGHT_GRAY.getBlue(),(int)alpha);
        return gris;
    }
    /** COLORS FORA DEL VISIBLE
    if (lambda>780){
        //double IRxmin=0.71, IRymin=0.28, IRxmax=0.479,IRymax=0.152;        
        double IRrmin=100, IRgmin=0, IRbmin=0;
        double IRrmax=40, IRgmax=0, IRbmax=20;
        double IRrslope=(IRrmax-IRrmin)/(par.lambdamax*1.E9-780.);
        double IRgslope=(IRgmax-IRgmin)/(par.lambdamax*1.E9-780.);
        double IRbslope=(IRbmax-IRbmin)/(par.lambdamax*1.E9-780.);
        Color colorIR = new Color ((int)((IRrmin+IRrslope*lambda)),(int)(IRgmin+IRgslope*lambda),(int)(IRbmin+IRbslope*lambda),(int)alpha);
        return colorIR;
    }else{
        if (lambda<380){
            double UVxmin=0.179, UVymin=0.019, UVxmax=0.294,UVymax=0.07;
            Color colorUV = new Color ((int)(19*2.55),0,(int)(51*2.55),(int)alpha);
            return colorUV;
        }
    }**/
    if(r>=0.0 && r<=1.0 && g>=0.0 && g<=1.0 && b>=0.0 && b<=1.0){
      Color tmpcolor=new Color((float)r,(float)g,(float)b);
      hsb=tmpcolor.RGBtoHSB((int)(r*255),(int)(g*255),(int)(b*255),hsb);
      hsb[1]=1;hsb[2]=1; // ponemos la saturacion y el brillo al m�ximo, para cambiar los colores
      int rgbint=tmpcolor.HSBtoRGB(hsb[0],hsb[1],hsb[2]);
      Color color=new Color(tmpcolor.getRed(),tmpcolor.getGreen(),tmpcolor.getBlue(),(int)alpha);
      return color;
    }
    else{
      Color color=new Color(0,0,0);
      return color;
    }


  }//////////////////////// fin lambda2RGB

  // convierte longitud de onda de 380 nm a 800 nm a color RGB en un falso color
  public Color falsalambda2RGB(int lambda){

    int red=0,green=0,blue=0;
    //Color colorRgb;

    for(int i=0; i<=83; i++){
       if(lambda==380+i )   {red=250-i*3; green=0; blue=250;}  //violeta-azul
       if(lambda==464+i )   {red=0; green=0+i*3; blue=250;}    //azul-cyan
       if(lambda==548+i )   {red=0; green=250; blue=250-i*3;}  //cyan-verde
       if(lambda==632+i )   {red=0+i*3; green=250; blue=0;}    //verde-amarillo
       if(lambda==716+i )   {red=250; green=250- i*3; blue=0;} //amarillo-rojo
       if(lambda==800 )   {red=250; green=0; blue=0;} //amarillo-rojo

    }
    Color colorRgb=new Color(red,green,blue);
    return colorRgb;
  }

}


