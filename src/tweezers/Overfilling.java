package tweezers;
import org.opensourcephysics.display.*;
import org.opensourcephysics.display2d.*;
public class Overfilling{
    
    LambdaRGBColor rgb = new LambdaRGBColor();
    GaussianBeam gb=new GaussianBeam();
    BoundedShape pupil;
    InterpolatedPlot beam;//BoundedShape beam;
    int size=10;
    BoundedShape rectanglecolor = BoundedShape.createBoundedRectangle(0,0,size,size);
    PlottingPanel plot = new PlottingPanel("","","");//("radial coordinate (mm)","Beam intensity","Entrance pupil");
    
    Dataset beamprofile = new Dataset();
    BoundedShape pupileft = BoundedShape.createBoundedRectangle(0,0,0.1,0.1);
    BoundedShape pupilright = BoundedShape.createBoundedRectangle(0,0,0.1,0.1);
    public void dibuixar1D(DrawingPanel panel, Parametres par){

//        panel.add(plot);
        pupileft.color=java.awt.Color.BLACK;
        pupileft.edgeColor=java.awt.Color.BLACK;
        pupileft.setWidth(size);
        panel.addDrawable(pupileft);
        pupilright.color=java.awt.Color.BLACK;
        pupilright.edgeColor=java.awt.Color.BLACK;
        pupilright.setWidth(size);
        panel.addDrawable(pupilright);   
        beamprofile.setConnected(true);
        beamprofile.setMarkerShape(beamprofile.PIXEL);
    }
    public void actualitzar1D(DrawingPanel panel, Parametres par){
        beamprofile.clear();
        double pupil=5.;//entrance pupil size (mm)
        double xmax=2.*pupil;
        double w= pupil/par.pupil2waist; //beam waist at entrance pupil (mm)
        double discrx=2.*xmax/100.;
        double intensity;
        for (double x=-xmax;x<xmax;x=x+discrx){
                intensity=Math.exp(-2.*x*x/(w*w));
                beamprofile.append(x, intensity);
        }
        panel.addDrawable(beamprofile);
        pupileft.setXY(pupil/2+size/2,beamprofile.getYMax());
        pupilright.setXY(-pupil/2-size/2,beamprofile.getYMax());
        pupileft.setHeight((beamprofile.getYMax()-beamprofile.getYMin())/20.);
        pupilright.setHeight((beamprofile.getYMax()-beamprofile.getYMin())/20.);
        panel.repaint();
    }
    
    public void dibuixar(DrawingPanel panel,Parametres par){
        beam=gb.beamXY(par);
        panel.setBounds(-size, size, -size, size);
        panel.setShowCoordinates(false);
        pupil = BoundedShape.createBoundedCircle(0, 0, 0.8*size*2);
        pupil.color=java.awt.Color.WHITE;        
        panel.addDrawable(pupil);
        panel.addDrawable(beam); 
        actualitzar(panel,par);
    }
    
    public void actualitzar(DrawingPanel panel, Parametres par){       
        beam=gb.beamXY(par);
        pupil = BoundedShape.createBoundedCircle(0, 0, 2*par.pupil2waist);
        actualitzarcolor(panel,par);
        panel.clear();
        panel.addDrawable(beam);
        panel.addDrawable(pupil);
        panel.repaint();
    }
    public void actualitzarcolor(DrawingPanel panel, Parametres par){
        pupil.color = rgb.lambda2RGB(par,150);//(int)(par.Puseful/par.scaleP/par.Pmax*255));    
        pupil.edgeColor=pupil.color;
        panel.repaint();
    }
    
    
    
}