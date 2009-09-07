/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tweezers;
import org.opensourcephysics.display.*;
import org.opensourcephysics.display2d.*;
/**
 *
 * @author pep
 */
public class GaussianBeam {
    Operacions op = new Operacions();
    public InterpolatedPlot IntensityMapYZ(Parametres par) {
        int N=100; // n�mero de discretitzacions
        double smax=par.field[par.regim]*2.1;//par.Rmax[par.regim]/par.scale*2.*1.5; //mida en radis de bola
        double discret = smax/(double)N;
        double[][] data = new double[N][N];
       
//        GridPlot plot = new GridPlot();
        InterpolatedPlot plot=new InterpolatedPlot();
            //BoundedShape rectanglecolor = BoundedShape.createBoundedRectangle(0,0,par1.field[1]/par1.scale,par1.field[1]/par1.scale);
//        ContourPlot plot = new ContourPlot();
        plot.setAll(data,-smax,smax,-smax,smax);
        plot.setPaletteType(1);//.setFloorCeilColor(java.awt.Color.black,java.awt.Color.white);
        double Imax=0.;
        for (int i=0;i<N;i++){
            double y=plot.indexToX(i)*par.scale;
            for (int j=0;j<N;j++){
                double z=plot.indexToY(j)*par.scale;
                data[i][j]=Math.pow(op.I(y,z,par),0.25);//Math.log(1.+op.I(y,z,par));//Math.exp(-y*y-z*z);//
                if (data[i][j]>Imax){Imax=data[i][j];}
            }
        }
        double Itot=0.;
        for (int i=0;i<N;i++){
            for (int j=0;j<N;j++){
                Itot=Itot+data[i][j];///Imax;
            }
        }
        plot.setAll(data);
        plot.setAutoscaleZ(false, 0, Imax/par.P*par.Pmax*par.scaleP);
        //System.out.println("Ceiling="+plot.getCeiling()+" Floor="+plot.getFloor());
        return plot;
        }
    public InterpolatedPlot black(Parametres par) {        
        int N=2;///size
        double[][] data = new double[N][N];
       
//        GridPlot plot = new GridPlot();
        InterpolatedPlot plot=new InterpolatedPlot();   
//        ContourPlot plot = new ContourPlot();
        double smax=25;//mida en radis de bola
        plot.setAll(data,-smax,smax,-smax,smax);
        plot.setPaletteType(1);//.setFloorCeilColor(java.awt.Color.black,java.awt.Color.white);
        for (int i=0;i<N;i++){
            double y=plot.indexToX(i)*par.scale;
            for (int j=0;j<N;j++){
                double z=plot.indexToY(j)*par.scale;
                data[i][j]=0.;//Math.log(0.1+op.I(y,z,par));//Math.exp(-y*y-z*z);//
            }
        }
        plot.setAll(data);
        return plot;    
    }

    
public InterpolatedPlot beamXY(Parametres par){
    //ESCALA DE GRIOSS TRANSP:
    int nscale=100;
    java.awt.Color[] transpalette = new java.awt.Color[nscale];
    double[] a = new double[4];
    
    for (int i=0;i<100;i++){
        transpalette[i]=new java.awt.Color (0,255,0,i);//(int)i/nscale*maxcolor);
    }
    int N=100; // n�mero de discretitzacions
        //distancies relatives al beam waist
        double pupil=par.pupil2waist; //entrance pupil size
        double w=1.; //beam waist
        
        double smax=par.waist2pupilmax; //tamany màxim de la pupil·la relatiu al feix.
        double discret = smax/(double)N;
        double[][] data = new double[N][N];

        InterpolatedPlot plot=new InterpolatedPlot();   
//        ContourPlot plot = new ContourPlot();
        plot.setAll(data,-smax,smax,-smax,smax);
        plot.setPaletteType(1);
        //plot.setPaletteType(1);//.setFloorCeilColor(java.awt.Color.black,java.awt.Color.white);
        
        double Imax=0.;
        for (int i=0;i<N;i++){
            double x=plot.indexToX(i);
            for (int j=0;j<N;j++){
                double y=plot.indexToY(j);
                data[i][j]=Math.exp(-2.*(x*x+y*y)/(w*w)); //intensitat transversal, excepte factors que depenen de z
                if (data[i][j]>Imax){Imax=data[i][j];}
            }
        }
        for (int i=0;i<N;i++){
            for (int j=0;j<N;j++){
                data[i][j]=data[i][j]/Imax;
            }
        }
        plot.setAll(data);
        return plot;
}
}

