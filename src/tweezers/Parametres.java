package tweezers;
import org.opensourcephysics.controls.*;

public class Parametres {

    Operacions op = new Operacions();
    int lang = 0; //language: 0=english, 1=catalan, 2=spanish
    boolean track=false;
    double stiffness;
    double kz,kx;
    double Qzmaxup,Qzmaxdown,Qxmax;
    boolean correction3D=false;
    int raymode=0;
    boolean traceractivated; // És irrellevant posar aquí "=true" or "false". El valor per defecte es defineix a les propietats del menu item via netbeans
    boolean forcedtrace=false; // When true, an anonimous trace file is automatically generated since the program is started
                               // When false, the trace may be optionally activated by selecting a CheckBox in the File menu, and entering a studentID
                               //This variable swithces between the classroom version (true) and the standard version (false)
    boolean allow_ray_brightness_mode=false;
    
    //All parameters are set in SI units
    double scaleP = 0.001;//Power in mW
    double Pmin=0.;//000000000001;
    double Pmax=100;//mW from the laser
    double Pdefault=50.*scaleP;
    double P= Pdefault;
    double incrP=1;//1mW
    int nticsP=100;//(int)((Pmax-Pmin)/incrP);

    double scaleL = Math.pow(10, -9);
    double lambda= 543.*scaleL;//longitud d'ona del laser en nm
    double lambdamin=300.;
    double lambdamax=1200.;
    double incrlambda=5.;
    int nticslambda=(int)((lambdamax-lambdamin)/incrlambda);
    
    
    //double nimmersion=1.33;//33;//1.516; 
    double n1min=1.33;
    double n1max=1.6;
    double n1default=1.331;
    double n1 = n1default;//1.333; //�ndex refracci� medi
    double incrn1=0.01;
    int nticsn1 = (int)((n1max-n1min)/incrn1)+1;
    
    double n2min=1.00;
    double n2max=3.;
    double n2default=1.58;
    double n2 = n2default; //�ndex bola poliestir�
    double incrn2=0.01;
    int nticsn2 = (int)((n2max-n2min)/incrn2)+1;
    
    double NAmin=0.1;//-n1;
    double NAmax=1.3;//nimmersion;//n1*0.99;
    double NAdefault=1.25;
    double NA=NAdefault;
    double incrNA= 0.01;
    int nticsNA = (int)((NAmax-NAmin)/incrNA);

    double scalew0=1.E-6;
    double w0max=2.*lambdamin*scaleL/(Math.PI*NAmin);
    double w0min=2.*lambdamax*scaleL/(Math.PI*NAmax);
    double w0default=2.*lambda/(Math.PI*0.65);
    double w0=w0default;//=2*lambda/(n1*Math.PI*NA);
    double incrw0=1.E-9;
    int nticsw0=(int)((w0max-w0min)/incrw0);
//    double augments = 100;
//    double augmentsmax=100;
//    double augmentsmin=10;
    
    //double fmin=0.250/augmentsmax;
    //double fmax=0.250/augmentsmin;
    //double Rpemin=0.001;//NAmin*fmin;
    //double Rpemax=0.1;//NAmax*fmax;
        
    double[] scaleR={Math.pow(10, -6),Math.pow(10,-9)};    //escala de R en el panell d'entrada de dades
    double[] Rmax ={10.E-6, 100.E-9}; //els limits es podrien recalcular en funcio de lambda.
    double[] Rmin = {1.E-6, 20.E-9};
    double[] Rdefault = {5.E-6,50.E-9};//{(Rmax[0]+Rmin[0])/2., (Rmax[1]+Rmin[1])/2.};
    double R = Rdefault[0]; //Radi en micròmetres (rang entre 0.08 i 100 micrometres)
    double[] incrR={0.1E-6,1E-9};
    int[] nticsR = {(int)((Rmax[0]-Rmin[0])/incrR[0]),(int) ((Rmax[1]-Rmin[1])/incrR[1])};
    
    double scalevisc=1E-3;
    double visc=8.90*Math.pow(10,-4); //viscositat de l'aigua en Pa�s
    double viscmax=1E-2;
    double viscmin=1E-4;
    double incrvisc=0.01*scalevisc;
    int nticsvisc=(int)((viscmax-viscmin)/incrvisc);
    boolean water=false;
    
    double T = 300;//temperatura en K
    double Tmax=330;
    double Tmin=270;
    double incrT=1;
    int nticsT = (int)((Tmax-Tmin)/incrT);
    
    int regim=0;//0:Mie, 1:Rayleigh, 2:free
    int perfil=0; //0:res 1:F(y)  2:F(z)
    int counts=0;
    boolean initialized=false;
    boolean showforcefieldrectangle=false;
    boolean calibrated=false;
    boolean showplotdetails=false;
    //int NraysYaxis=100;
    //int NraysYaxismin=1;
    //int NraysYaxismax=10000;    
 
    double pupil2waist=1.;
    double waist2pupil=1.;
    double waist2pupilmax=5.;
    double waist2pupilmin=0.1;
    double waist2pupildefault=1.;
    double incrwaist2pupil=0.01;
    int nticswaist2pupil = (int)((waist2pupilmax-waist2pupilmin)/incrwaist2pupil);
    
    double[] zoomdefault = {0.6,0.4};
    double zoom =zoomdefault[regim];
    double[] zoomin={0.05,0.05};
    double[] zoomax={2.,1.};
    double[] incrzoom={0.05,0.05};
    int[] nticszoom={(int)((zoomax[0]-zoomin[0])/incrzoom[0]),(int)((zoomax[1]-zoomin[1])/incrzoom[1])};

    double zcover=20.E-6;
    double ztrap;
    double fp=0.5; //entre 0 i 1 fracció d'energia en la component de polarització p (paral·lela al pla d'incidència)
    double fs =0.5; //fracció d'energia polaritzada s.
    //double[] fmax={0.,0.}; //maximum force en pN (Mie i Rayleigh), es calcula al començament de l'applet
    double[] fnorm = {20.,10.*5.};
    int Nraigd=100;
    int Nraigforce=100;//100;
    int Nraigperfils=100;//Nraigd;//1000;
    int Nraig=Nraigforce;//force;//el seu valor depen de què volguem calcular, precisions diferents
    //Alerta, si el número de rajos dibuix/calcul no és el mateix, no anirà.
    int Nraigdmax = Nraigd;
    int Nraigdmin=0;
    int Nraigmax=Math.max(Nraigforce,Nraigperfils); 
    int Npointsperfils=100;
    double wcov;//=0.001;
    double timemax=1.;
    double[] dtdefault ={1.e-4,5.0e-7};
    double dt=dtdefault[regim];
    double dtMax=1.e-2, dtMin=1.e-7;
    int nticsdt=10000;//(int)((dtMax-dtMin)/discrdt);
    double t=0.;
    double estimatedNA;
    
    //Calibració de forces:
    int Npath=100000;
    double dtXY=1.E-5;// gamma/k   
    
    //Constants:
    double dens = 1./1000.*1000000.; //g/cm^3--> kg/m3
    double  c = 3E8; //Caracter�stiques l�ser. 
    double kb=1.38*Math.pow(10, -23);//J/K boltzman constant
    
     //Visualització
    double scale=1.E-6;//R //m
    double field[]={5.,1.};//*R/scale;; 
    double[] binwidth={1.E-2,1.E-3};//histogram bins in um
    
    
    //double[] fieldmax={10,1};//{2.*Rmax[0]/scale,2.*Rmax[1]/scale};
    
    //parametres depenents:
    
    //double f;// = 0.250 / augments;// *0.001; //focal, t�picament f=2.5mm //double D = 4;//mm  D= NA * f; NA = 1.25; //Obertura relativa = di�metre / focal
    double Rpe;//=NA*f; //2.5; //Di�metre pupil�la d'entrada: t�picament 5mm
    double Rcov;
    double k;//=2*Math.PI/lambda;
    double m;//=dens*4./3.*Math.PI*Math.pow(R,3);//MASSA DE LA BOLA
    double L;//=w0*w0*Math.PI/lambda; //dist�ncia z0 a la qual la intensitat decau un #% en mm 
    // double F0;//=n1*P/c;    
    double gamma;//=6.*Math.PI*visc*R;
    int Npassos;// = (int)(timemax/dt);
    double sigma;
    double D;//diffusivity
    double kmedi;
    double[] fieldforcesdefault={4.*Rdefault[0],4.*Rdefault[1]};
    double[] fieldforces=fieldforcesdefault;
    double Puseful;
    double thmax0,thmax;
    double anglelimit;
    boolean changed=false; //true if something has changed since the last force field calculation and calibration
    public void calcParDependents(){
        //thmax0 = Math.asin(NA/nimmersion); //NA=n sin(thmax)
        //thmax=op.thetarefrac(nimmersion,n1,thmax0);
        pupil2waist=1./waist2pupil;
        k=2*Math.PI/lambda;
        kmedi=k*n1;
        m=dens*4./3.*Math.PI*Math.pow(R,3);//MASSA DE LA BOLA
        gamma=6.*Math.PI*visc*R;
        sigma=2*kb*T/gamma;
        D=kb*T/gamma;
        w0max=2.*lambda/(Math.PI*NAmin);
        w0min=2.*lambda/(Math.PI*NAmax);
        //=(int)(timemax/dt);
        //Rmin[0]=lambda; //limit inferior Mie
        //Rmax[1]=lambda;//lambda/20;// limit superior Rayleigh
        //fieldforces[0]=field[0]*scale;//2.*(3.*R);
        //fieldforces[1]=field[1]*scale;//2.*(20.*R);
        //ztrap=0.;//kmedi*w0*w0/(2.*Math.sqrt(3.));
        if (n2<n1){anglelimit=Math.asin(n2/n1);}else{anglelimit=Math.PI/2;}
        if(water){visc=2.414E-5*Math.pow(10,247.8/(T-140));}
        //System.out.println("w_pupil="+wpupil+" Rpe="+Rpe);
        /**if (R>Rmax[regim]){R=Rmax[regim];}else{
            if (R<Rmin[regim]){R=Rmin[regim];}
        }**/
        //dt = gamma*kb*T/Math.pow(fmax[regim]*1.E12,2);       
     if(regim==0){          
            thmax=Math.asin(NA/n1);//angle (respecte l'eix òptic)
            Rcov=zcover*Math.tan(thmax);//ombra de la pupil·la sobre el cubre
            wcov=Rcov/pupil2waist;///w0*Math.sqrt(1.+Math.pow((f/L),2));
            Puseful=P*(1.-Math.exp(-2.*Math.pow(pupil2waist,2)));//pupil2waist=R/w
            //f = 0.250 / augments; //focal, t�picament f=2.5mm //double D = 4;//mm  D= NA * f; NA = 1.25; //Obertura relativa = di�metre / focal
            //Rpe=NA*f; //******* 2.5; //Di�metre pupil�la d'entrada: t�picament 5mmMath.tan(thmax)
     }else{
        L=w0*w0*Math.PI/lambda; //dist�ncia z a la qual la intensitat decau un #% en mm     
        estimatedNA=2.*lambda/(Math.PI*w0);
        //w0=lambda/(Math.PI*NA);//0.51*lambda/(Math.PI*NA);//lambda/(Math.PI*NA);//2*lambda/(n1*Math.PI*NA);
     }
        

   }

    //double[] tT = {10.,20.,30.,40.,50.,60.,70.,80.,90.,100.};
    //double[] tVisc = {1.308E-3, 1.003E-3,7.978E-4,6.531E-4,5.471E-4,4.668E-4,4.044E-4,3.550E-4,3.150E-4,2.822E-4};
    

        //VISUALITZACIÓ
        boolean showrays=false;
        boolean showreflected=false;
//        boolean showforce=true;
        boolean showEMintensity=false;
        double useviscosity=0.;
        double useBM=0.;
        int nraystoshow=3;
        boolean ztrapfound=false;
        //boolean lostparticle=false;
        
        //VISUALITZACI� DIAGRAMA DE RAIGS
        int numraig=2; //0 vol dir 2 rajos, 1 vol dir molts rajos, 2 vol dir mooolts
        int q=0; //0 tot, 1 scat, 2 grad
        boolean[] selectedforces ={false,false,false}; //força neta, força de scattering i força de gradient
        int feix=0; //0 ona plana, 1 feix gaussi�.
        //boolean map=false;
        //boolean perfy=false;
        //boolean perfz=false;
        
        //Taula viscositat de l'aigue en funció de la temperatura:
        
        //RAJOS:
        XMLControl xmlpar = new XMLControlElement();
        public void write(String filename){
            if(filename.endsWith(".xml")){}else{
                filename=filename+".xml";
            }
            xmlpar.setValue("Power",P);
            xmlpar.setValue("Wavelength",lambda);
            xmlpar.setValue("n_medium",n1);
            xmlpar.setValue("n_bead",n2);
            xmlpar.setValue("NA",NA);
            xmlpar.setValue("Overfilling",waist2pupil);
            //xmlpar.setValue("Magnification",augments);
            xmlpar.setValue("Radius",R);
            xmlpar.setValue("Viscosity",visc);
            xmlpar.setValue("Temperature",T);
            xmlpar.setValue("Time Step",dt);
            xmlpar.setValue("Beam waist (Rayleigh)",w0);
            //etc
            
            xmlpar.write(filename);
        }
        public void read (String filename){
            xmlpar.read(filename);
            P=xmlpar.getDouble("Power");
            lambda=xmlpar.getDouble("Wavelength");
            n1=xmlpar.getDouble("n_medium");
            n2=xmlpar.getDouble("n_bead");
            NA=xmlpar.getDouble("NA");
            waist2pupil=xmlpar.getDouble("Overfilling");
            //augments=xmlpar.getDouble("Magnification");
            R=xmlpar.getDouble("Radius");
            visc=xmlpar.getDouble("Viscosity");
            T=xmlpar.getDouble("Temperature");
            dt=xmlpar.getDouble("Time Step");
            w0=xmlpar.getDouble("Beam waist (Rayleigh)");
            //etc
        }
        
    public Parametres() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
    }

    static String[] lasers = {"HeNe (633nm)", "C02 (9600)","C02((10600)" };
}
