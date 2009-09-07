package tweezers;
//import org.opensourcephysics.display.DrawingFrame;
//import org.opensourcephysics.display.DrawingPanel;
import org.opensourcephysics.display2d.VectorPlot;
import org.opensourcephysics.tools.ResourceLoader;
import org.opensourcephysics.tools.Resource;
//import org.opensourcephysics.display.MeasuredImage;
import java.text.DecimalFormat;
import org.opensourcephysics.display.*;
//import javax.swing.JProgressBar;
public class Camp {
    Captions cap = new Captions();
    Forces forces = new Forces();
    GaussianBeam gb = new GaussianBeam();
    double [][] F;
    double fmax;
    public void calc(PlottingPanel plottingpanelcamp, DrawingPanel legend, java.awt.Label[] labels, Raigs raigs,Parametres par) {
        int N=20; // n�mero de discretitzacions
        double discret = 2.*2.5/(double)N;
        double[][][] data = new double[2][N][N+1];
        VectorPlot plot=new VectorPlot();
        int q=par.q;
        double smax=par.fieldforces[par.regim]/2./par.scale;
        plot.setAll(data,-smax,smax,-smax,smax);
        //plot.showLegend();
        plot.setPaletteType(0);
        String s="";
        if (q==0){s="(For�a Total)";}
        if (q==1){s="(For�a de scattering)";}
        if (q==2){s="(For�a de gradient)";}
        fmax =0.;
        double modF=0.;
        int Nmax=N;
        for (int i=0;i<N;i++){
            double yc=plot.indexToX(i);//*par.scale;
            for (int j=0;j<=N;j++){
                double zc=plot.indexToY(j);//*par.scale;
                F=forces.calc(par,raigs,0,yc*par.scale,zc*par.scale);
                data[0][i][j]=F[q][1]*1.E12;
                data[1][i][j]=F[q][2]*1.E12;                
                modF=Math.sqrt(F[q][1]*F[q][1]+F[q][2]*F[q][2]);
                if(modF>fmax){fmax=modF;}
            }

        }
        plot.setAll(data,-smax,smax,-smax,smax);
        plottingpanelcamp.clear();     
        plottingpanelcamp.setSquareAspect(false);
        plottingpanelcamp.setAutoscaleX(true);
        plottingpanelcamp.setAutoscaleY(true);
        plottingpanelcamp.addDrawable(plot);//panellcamp.addDrawable(plot)
        plottingpanelcamp.repaint();
        plottingpanelcamp.setXLabel(cap.forceFieldXlabel[par.lang]);
        plottingpanelcamp.setYLabel(cap.forceFieldYlabel[par.lang]);
        
        String imagename="legend.png";
        Resource res= ResourceLoader.getResource(imagename);
        MeasuredImage mi = new MeasuredImage(res.getBufferedImage(),-1,1,-1,1);
        legend.addDrawable(mi);
        legend.repaint();
        DecimalFormat df = new DecimalFormat("0.00");
        fmax=fmax*Math.pow(10,12);
        labels[0].setText("F="+df.format(0.)+"pN");
        labels[1].setText("F="+df.format(fmax/4.)+"pN");
        labels[2].setText("F="+df.format(fmax/2.)+"pN");
        labels[3].setText("F="+df.format(fmax*3./4.)+"pN");
        labels[4].setText("F="+df.format(fmax)+"pN");    
        plottingpanelcamp.setShowCoordinates(true);
        plottingpanelcamp.repaint();
        //plot.showLegend();
    }
}