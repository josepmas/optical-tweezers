/*
 * NewJFrame.java
 *
 * Created on 18 / gener / 2008, 11:36
 */
package tweezers;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import org.opensourcephysics.display.*;
import org.opensourcephysics.numerics.*;
import org.opensourcephysics.frames.*;
import java.text.DecimalFormat;
import java.util.Random;

/**
 *
 * @author 
 */
public class NewJFrame1 extends javax.swing.JFrame implements WindowListener {
    Captions cap = new Captions();
    Parametres par = new Parametres();
    Raigs raigs = new Raigs(par);
    Forces forces = new Forces();
    Camp camp = new Camp();
    Operacions op = new Operacions();
    Perfils profile = new Perfils();
    Overfilling overfilling = new Overfilling();
    PlottingPanel panellYZ = new PlottingPanel("", "", "");//"y position (sphere radius)", "z position (sphere radius)", "");
    PlottingPanel panellXY = new PlottingPanel(cap.forceFieldXlabel[par.lang], cap.forceFieldYlabel[par.lang], cap.panellXY_title[par.lang]);//"y position (sphere radius)", "z position (sphere radius)", "");
    PlottingPanel panellhistogram = new PlottingPanel("x ("+"\u03BC"+"m)", "Counts", "Position histogram");
    PlottingPanel Yforceprofilepanel = new PlottingPanel(cap.xcurvexlabel[par.lang], "force (pN)", "Fx(x)");
    PlottingPanel Zforceprofilepanel = new PlottingPanel(cap.zcurvexlabel[par.lang], "force (pN)", "Fz(z)");
    //PlottingPanel calibrationpanel = new PlottingPanel("Position (m)", "Force(pN)", "Fx(x)");
    PlottingPanel plottingpanelcamp = new PlottingPanel("x (m)", "z (m)", cap.panellcamp_title[par.lang]);
    PlottingPanel panellspectrum = new PlottingPanel("f(Hz)", "P", "Power Spectrum");
    //PlottingPanel panellPower = new PlottingPanel("x/Rpe", "Power(mW)","");
    java.awt.Label[] labelslegend= new java.awt.Label[5];
    graf2d objectesYZ = new graf2d(); // dibuixa i actualitza els objectes
    double[] pc = new double[3]; //posició del centre de la bola. Variable global.
    double time=0.;
    DecimalFormat df = new DecimalFormat("0.00");
    DecimalFormat df1 = new DecimalFormat("0.0");
    DecimalFormat intf = new DecimalFormat("#");
    DecimalFormat ef= new DecimalFormat("0.0E0");
    Thread resoldre = new calcThread();
    Thread bmthread = new bmThread();
    boolean running = false;
    Dataset datasetpath = new Dataset();
    Dataset datasethist=new Dataset();
    double[] x = new double[par.Npath];
    double[] y = new double[par.Npath];
    double[] z = new double[par.Npath];
    double[] t = new double[par.Npath];
    Random r = new Random();
    Dataset datasetspectrum = new Dataset();
    Dataset datasetfft = new Dataset();
    Histogram hist;

    
    /** Creates new form NewJFrame */
//    public NewJFrame1(NewJApplet applet) {
    public NewJFrame1() {
        par.initialized = false;
        this.addWindowListener(this);
        initComponents();
        //set nombre de tics a cada slider
        jSlider14.setMaximum(par.nticslambda);
        jSlider1.setMaximum(par.nticsP);
        jSlider15.setMaximum(par.nticsNA);
        jSlider16.setMaximum(par.nticswaist2pupil);
        jSlidern1.setMaximum(par.nticsn1);
        jSlidern2.setMaximum(par.nticsn2);
        jSlider20.setMaximum(par.nticsvisc);
        jSlider21.setMaximum(par.nticsT);
        jSlider5.setMaximum(par.nticsR[par.regim]);
        jSlider2.setMaximum(par.nticsw0);
        jSliderdt.setMaximum(par.nticsdt);
        //jSliderdt.setVisible(false);
        //jLabeldt.setVisible(false);
        jSliderfield.setMaximum(par.nticszoom[par.regim]);
        
        javax.swing.filechooser.FileFilter filterxml = new javax.swing.filechooser.FileFilter() {

            @Override
            public boolean accept(File f) {
                String filename = f.getName();
                return filename.endsWith(".xml");
                
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public String getDescription() {
                return "*.xml";
                //throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        jFileChooser1.setFileFilter(filterxml);
        par.P=par.Pdefault;
        par.pupil2waist=par.waist2pupildefault;
        par.n2=par.n2default;
        par.n1=par.n1default;
        par.NA=par.NAdefault;
        jPaneldt.setVisible(false);
        setDefaultSettings();
        par.calcParDependents();
        etiquetar();
        //par.initialized = true;
        drawElements();
        actualitzar();
        raigs = new Raigs(par);
        panellYZ.enableInspector(false);
        panellXY.enableInspector(false);
        panellhistogram.enableInspector(false);
        drawingPanelHistogram.enableInspector(false);
        plottingpanelcamp.enableInspector(false);
        Yforceprofilepanel.enableInspector(false);
        Zforceprofilepanel.enableInspector(false);

        //panellYZ.hideInspector();
        panellYZ.setComponentPopupMenu(null);
        objectesYZ.bola.setXY(1.E-12, 1.E-12);
        objectesYZ.dibuixar(panellYZ, par);
        objectesYZ.repintaregim(panellYZ, par, raigs);
        actualitzar();
        labelslegend[0]=label1;
        labelslegend[1]=label5;
        labelslegend[2]=label4;
        labelslegend[3]=label3;
        labelslegend[4]=label2;
        //calcMaxForce();//de moment millor fixar a mà.
        setDefaultSettings();
        //set labels:
        jLabel1.setText(cap.labelPower[par.lang]);
        jLabel180.setText(cap.labelWavelength[par.lang]);
        jLabel1.setText(cap.labelPower[par.lang]);
        jLabel22.setText(cap.labelNumericalAperture[par.lang]);
        jLabel13.setText(cap.labelRefractiveIndexMedium[par.lang]);
        jLabel36.setText(cap.labelViscosity[par.lang]);
        jLabel184.setText(cap.labelTemperature[par.lang]);
        jLabel187.setText(cap.labelRefractiveIndexSphere[par.lang]);
        jLabel10.setText(cap.labelRadius[par.lang]);
        jComboBox1.setSelectedIndex(1);//aixo es perque refresci el dt de l'slider.
        jComboBox1.setSelectedIndex(0);
        par.initialized = true;
        overfillingOK();
//        Tracer.write_action_ini("Applet started");
//            Tracer.write_all_param(par);
//        Tracer.write_action_end();
    }
    
    /** This method writes the appropiate labels for some objects of the UI, it must be completed in order to set ALL labels in NewJFrame1 for appropiate multilanguage labelling*/
    public void etiquetar() {
        panellYZ.setXLabel(cap.grafXlabel[par.lang]);
        panellYZ.setYLabel(cap.grafYlabel[par.lang]);
        panellYZ.repaint();
    }
    
    /** This method calculates the maximum force in both regimes and saves it 
     * to Parameters, in order to normalize the force Arrow length in the trapping panel**/
    public void calcMaxForce(){
        //DESACTIVAT!
        par.P=par.Pmax;
        par.waist2pupil=par.waist2pupilmin;
        par.n2=par.n2max;
        par.n1=par.n1min;
        par.NA=par.NAmin;
        par.calcParDependents();
        
        par.regim=0;
        camp.calc(plottingpanelcamp, drawingPanel1, labelslegend, raigs, par);
        par.fnorm[0]=camp.fmax;
        //System.out.println("fmax mie="+par.fmax[0]);
        par.regim=1;
        camp.calc(plottingpanelcamp, drawingPanel1, labelslegend, raigs, par);
        par.fnorm[1]=camp.fmax;

        par.regim=0;
        netejarpanellforces();

        par.P=par.Pdefault;
        par.pupil2waist=par.waist2pupildefault;
        par.n2=par.n2default;
        par.n1=par.n1default;
        par.NA=par.NAdefault;
        par.calcParDependents();
        setDefaultSettings();
    }
    
    /** This method sets the deafult values to all sliders and textboxes in NewJFrame1*/
    public void setDefaultSettings() {
        //Sliders
        jSlider1.setValue((int) ((par.P / par.scaleP - par.Pmin) / (par.Pmax - par.Pmin) * (double)jSlider1.getMaximum()));
        jText1.setText(intf.format(par.P / par.scaleP));
        jSlidern2.setValue((int) ((par.n2 - par.n2min) / (par.n2max - par.n2min) * (double)jSlidern2.getMaximum()));
        jText7.setText(df.format(par.n2));
        jSlidern1.setValue((int) ((par.n1 - par.n1min) / (par.n1max - par.n1min) * (double)jSlidern1.getMaximum()));
        jText6.setText(df.format(par.n1));
        jSlider15.setValue((int) ((par.NA - par.NAmin) / (par.NAmax - par.NAmin) * (double)jSlider15.getMaximum()));
        jText15.setText(df.format(par.NA));
        jSlider16.setValue((int) ((par.waist2pupil - par.waist2pupilmin) / (par.waist2pupilmax - par.waist2pupilmin) * (double)jSlider16.getMaximum()));
        jText16.setText(df.format(par.waist2pupil));
        jSlider20.setValue((int) ((par.visc - par.viscmin) / (par.viscmax - par.viscmin) * (double)jSlider20.getMaximum()));
        jFormattedTextField1.setText(df.format(par.visc/par.scalevisc));
        jSlider21.setValue((int) ((par.T - par.Tmin) / (par.Tmax - par.Tmin) * (double)jSlider21.getMaximum()));
        jFormattedTextField28.setText(intf.format(par.T));
        //jComboBox1.setSelectedIndex(par.regim);
        jSlider14.setValue((int) ((par.lambda / par.scaleL - par.lambdamin) / (par.lambdamax - par.lambdamin) * (double)jSlider14.getMaximum()));
        jFormattedTextField27.setText(intf.format(par.lambda / par.scaleL));
        jSlider5.setValue((int) ((par.R - par.Rmin[par.regim]) / (par.Rmax[par.regim] - par.Rmin[par.regim]) * (double)jSlider5.getMaximum()));
        //jText8.setText(df.format(par.R / par.scaleR));
        jSliderdt.setValue((int)((Math.log10(par.dt)-Math.log10(par.dtMin))/(Math.log10(par.dtMax)-Math.log10(par.dtMin))*jSliderdt.getMaximum()));
        //jSliderdt.setValue((int)((Math.log10(par.dt)-Math.log10(par.dtMin))/(Math.log10(par.dtMax)-Math.log10(par.dtMin))*jSliderdt.getMaximum()));
        jText2.setText(df.format(par.w0/par.scalew0));
        jSlider2.setValue((int)((Math.log10(par.w0)-Math.log10(par.w0min))/(Math.log10(par.w0max)-Math.log10(par.w0min))*jSlider2.getMaximum()));
        //jSlider2.setValue((int) ((par.w0 - par.w0min) / (par.w0max - par.w0min) * (double)jSlider2.getMaximum()));
    }
    
    /**draws the graphical objects in each drawingframe, and adds them to the appropiate panels**/
    public void drawElements() {
        overfilling.dibuixar1D(drawingPanel7, par);      
        drawingPanel1.add(panellYZ);
        objectesYZ.dibuixar(panellYZ, par);
        panellYZ.addMouseMotionListener(mousemotion);
        drawingPanel3.add(plottingpanelcamp);
        drawingPanel4.add(Yforceprofilepanel);
        drawingPanel6.add(Zforceprofilepanel);
    }
    
    java.awt.event.MouseMotionListener mousemotion = new java.awt.event.MouseMotionAdapter() {

        public void mouseDragged(java.awt.event.MouseEvent evt) {
            Interactive interac = panellYZ.getInteractive(); // identifica objecte interactiu
            if (interac != null) {
                actualitzar();
            //if(par.refrescar){actualitzarpanell2();}
            } else {
                //panellYZ.setMessage(null);
            }
        }

        public void mouseReleased(java.awt.event.MouseEvent evt){
            //xavi-TODO: traçar quan l'usuari deixa anar el ratolí. bola set to: x=? z=?
        }
        
        

    };
    /**Recalculates the dependent parameters and refresh the trapping diagram*/
    public void actualitzar() {
        par.calcParDependents();
        objectesYZ.actualitzar(panellYZ, par, raigs);
        jLabelforce.setText("Net Force = "+df.format(objectesYZ.Ft)+" pN ");
    }
    /**Returns true if tha ball has gone outside the panel*/
    public boolean bolaperduda(){
        boolean shaperdut=Math.max(Math.abs(objectesYZ.bola.getX()), Math.abs(objectesYZ.bola.getY())) > panellYZ.getXMax();//par.field[par.regim] / 2.;
        //System.out.println("XoYbola="+Math.max(Math.abs(objectesYZ.bola.getX()), Math.abs(objectesYZ.bola.getY()))+" Xmax="+panellYZ.getXMax());
        return shaperdut;
    }
    /**Shows a gray rectangle with the size of the field displayed in the force map*/
    public void mostrarectanglecamp(){
        if (jTabbedPane1.getSelectedIndex()==1){
            objectesYZ.rectanglecamp.setWidth(par.fieldforces[par.regim]/par.scale);
            objectesYZ.rectanglecamp.setHeight(par.fieldforces[par.regim]/par.scale);
            panellYZ.addDrawable(objectesYZ.rectanglecamp);
            panellYZ.removeDrawable(objectesYZ.bola);
            panellYZ.addDrawable(objectesYZ.bola);
            panellYZ.repaint();
        }else{
            panellYZ.removeDrawable(objectesYZ.rectanglecamp);
            panellYZ.repaint();
        }
    }
    
    //Selects which components of the force must be shown in the trapping diagram
    public void seleccionaforces(){
        if (jTabbedPane1.getSelectedIndex()==1){
            jCheckBox1.setSelected(true);
            jCheckBox2.setSelected(true);
            jCheckBox3.setSelected(true);            
        }else{
            jCheckBox1.setSelected(false);
            jCheckBox2.setSelected(false);
            jCheckBox3.setSelected(false);     
        }
    }
    /**Refreshes some labels*/
    public void actualitzarlabels() {
        jLabel7.setText(df.format(par.w0 * 1.E9));
        jLabel4.setText(df.format(par.Rpe * 1000.));
    }
    /** Writes the useful power to the appropiate label*/
    public void showusefulpower(){
        jText17.setText("Useful power= "+df1.format(par.Puseful/par.P*100.)+"%");
    }

    /**Refreshes the elements in jPanelForce (Force map, Y force profile, Z force profile*/
    public void actualitzarpanellforces(){
        par.calcParDependents();
        actualitzar();
        label1.setEnabled(true);
        label2.setEnabled(true);
        label3.setEnabled(true);
        label4.setEnabled(true);
        label5.setEnabled(true);
        camp.calc(plottingpanelcamp, drawingPanel2, labelslegend, raigs, par);
        profile.perfilZ(Zforceprofilepanel, par, 0.);//Fz(x=0,y=ybola,z=objectesYZ.getPosition()[1])
        profile.perfilY(Yforceprofilepanel, par, par.ztrap);//Fy(x=0,y=?,z=objectesYZ.getPosition()[2])
        profile.perfilZ(Zforceprofilepanel, par, 0.);//Fz(x=0,y=ybola,z=objectesYZ.getPosition()[1])        
        if(par.ztrapfound){
            jToggleButton2.setEnabled(true);
            jLabel15.setVisible(true);
            jLabel16.setVisible(true);
            jLabelzc.setText(df.format(par.ztrap/par.scale));
            jLabelzc2.setText(df.format(par.ztrap/par.scale));
            jLabelkz.setText(df.format(par.kz*1.E6));
            jLabelQzmaxup.setText(df.format(par.Qzmaxup));
            jLabelQzmaxdown.setText(df.format(par.Qzmaxdown));
            jLabelQxmax.setText(df.format(par.Qxmax));
            jLabelkx.setText(df.format(par.kx));
        }else{
            jToggleButton2.setEnabled(false);
            //jLabel15.setVisible(false);
            jLabel16.setVisible(false);
            jLabelzc.setText("Not found!");
            jLabelzc2.setText("0.00");
            jLabelkz.setText("-");
            jLabelQzmaxup.setText("-");
            jLabelQzmaxdown.setText("-");
            jLabelQxmax.setText(df.format(par.Qxmax));
            jLabelkx.setText(df.format(par.kx));            
        }
        profile.representar(Zforceprofilepanel, Yforceprofilepanel, par);
        jPanelForce.repaint();
    }
    /**Clears data in all panels of jPanelForces*/
    public void netejarpanellforces(){
        camp = new Camp();
        profile = new Perfils();
        plottingpanelcamp.clear();
        Yforceprofilepanel.clear();
        Zforceprofilepanel.clear();
        label1.setText("");
        label2.setText("");
        label3.setText("");
        label4.setText("");
        label5.setText("");
        jPanelForce.repaint();
        panellXY.clear();
        panellhistogram.clear();
        jLabelK.setText("[?]");
    }

    public void actualitzarpanellcalib() {
        drawingPanelHistogram.clear();
        drawingPanelHistogram.removeAll();

        int item = 1;//jComboBox6.getSelectedIndex();
        switch (item) {
            case 0: //simulated path
                drawingPanelHistogram.add(panellhistogram);
                break;                
            case 1:
                panellspectrum.removeDrawable(datasetspectrum);
                datasetspectrum = new Dataset();
                datasetspectrum.clear();
                //FFTFrame fftframe=new FFTFrame("freq","amplitude","");
                //fftframe.setDomainType(FFTFrame.FREQ);
                //fftframe.doFFT(x, par.dt, par.dt*par.Npath);
                //fftframe.setLogScale(true, true);
                //XMLControl xmlspectrum=new XMLControlElement();
                //fftframe.saveXML();
                //fftframe.showDataTable(true);
                //fftframe.setVisible(true);

                int Npath=x.length;
                //HA DE SER PARELL!!!
                FFTReal realFFT = new FFTReal(Npath);
                double[] xfft = x;//new double[2*x.length];
                //for (int i=0;i<x.length;i++){
                  //  xfft[2*i]=x[i];
                    //xfft[2*i+1]=0.;
                //}
                realFFT.transform(xfft);
                double[]f = realFFT.getNaturalFreq(par.dtXY);
                panellspectrum.setLogScaleX(true);
                panellspectrum.setLogScaleY(true);
                datasetspectrum.setConnected(false);
                datasetspectrum.setMarkerShape(datasetspectrum.PIXEL);
                TableFrame frame = new TableFrame("RealFFT");
                for (int i = 2; i < Npath; i+=2) {
                   datasetspectrum.append(f[i/2],Math.pow(xfft[i],2)+Math.pow(xfft[i+1],2));
                   frame.appendRow(new double[] {f[i/2],xfft[i],xfft[i+1]});
                }
                frame.setVisible(true);
                //double[] freq=fft.toWrapAroundOrder(x);
                panellspectrum.addDrawable(datasetspectrum);
                //panellspectrum.repaint();
                drawingPanelHistogram.add(panellspectrum);
                drawingPanelHistogram.repaint();
                break;
        }
        jTabbedPane1.repaint();
    }

public void dostep(){
            pc = objectesYZ.getPosition();

            double y = pc[1] * par.scale, z = pc[2] * par.scale;
            double[] f;
            f = forces.calc(par, raigs, 0., y, z)[0];//par.q];
            par.t = par.t + par.dt;
            y = y + 1. / par.gamma * f[1] * par.dt + Math.sqrt(2. * par.D * par.dt) * r.nextGaussian();
            z = z + 1. / par.gamma * f[2] * par.dt + Math.sqrt(2. * par.D * par.dt) * r.nextGaussian();
            //System.out.println(y + "" + z);
            objectesYZ.bola.setXY(y / par.scale, z / par.scale);
            objectesYZ.force[0].setXY(y / par.scale, z / par.scale);
            objectesYZ.force[1].setXY(y / par.scale, z / par.scale);
            objectesYZ.force[2].setXY(y / par.scale, z / par.scale);
            actualitzar();
            //panellYZ.setMessage("Time ="+df.format(par.t*1000.)+"ms",2);
            jLabeltime.setText("Time = "+df.format(par.t*1000.)+" ms");
            //System.out.println("Time ="+df.format(par.t*1000.)+"ms");
            if (bolaperduda()) {
                //panellYZ.setMessage(cap.lostparticle[par.lang],5);
                jLabelforce.setText(cap.lostparticle[par.lang]);
                jToggleButton1.setSelected(false);
                jToggleButton1.setText(cap.startsim[par.lang]);
            }
            if (par.t > par.timemax) {
                jToggleButton1.setSelected(false);
            }            
}

    public class calcThread extends Thread {

        public void run() {
            panellYZ.removeMouseMotionListener(mousemotion);
            objectesYZ.bola.setEnabled(false);
            //jButton3.setEnabled(false);
            jButton4.setEnabled(false);
            jComboBox1.setEnabled(false);
            par.t=0.;
            while (jToggleButton1.isSelected()) {
                try {
                    this.sleep(100);
                } catch (InterruptedException ie) {
                }
                dostep();
                //System.out.println("time= "+par.t);
            }
            panellYZ.addMouseMotionListener(mousemotion);
            objectesYZ.bola.setEnabled(true);
            //jButton3.setEnabled(true);
            jButton4.setEnabled(true);
            jComboBox1.setEnabled(true);
        }
    }

    public class bmThread extends Thread {

        public void run() {
            //jComboBox6.setEnabled(false);
            jLabelK.setText(cap.labelKcalibrating[par.lang]);            
            panellXY.clear();
            datasetpath = new Dataset();
            datasetpath.setConnected(true);
            datasetpath.setMarkerShape(Dataset.PIXEL);
            panellXY.addDrawable(datasetpath);

            drawingPanelHistogram.removeAll();
            drawingPanelHistogram.clear();
            panellhistogram.clear();
            drawingPanelHistogram.add(panellhistogram);
            //datasethist=new Dataset();
            //datasethist.setMarkerShape(Dataset.BAR);
            hist = new Histogram();
                //hist.clear();
                //hist.setBinWidth(bin);
                //hist.append(x);
            panellhistogram.addDrawable(hist);
            hist.setBinWidth(par.binwidth[par.regim]);
            jTabbedPane1.repaint();
            //drawingPanelPath.repaint();
            //jPanelCalib.repaint();
            
            double[] f;
            x = new double[par.Npath];
            y = new double[par.Npath];
            t = new double[par.Npath];
            double dt=par.dt;//dtXY
            x[0] = -1.E-12;
            y[0] = -1.E-12;
            t[0]=0.;

            //double ztrap=0.;//0.66E-6;//objectesYZ.bola.getY()*par.scale;


            int i=0;
            par.counts=0;
            try {
                this.sleep(10);
            } catch (InterruptedException ie) {
            }
            double fx,fy;
            //double k=1.E-5;
            double z=par.ztrap;//objectesYZ.bola.getY()*par.scale;//par.ztrap;//
            while(jToggleButton2.isSelected()){
                i++;
                par.counts=i;
                fx = forces.calc(par, raigs, 0., x[i-1], z)[0][1];//par.q];//-k*x[i-1];//
                fy = forces.calc(par, raigs, 0., y[i-1], z)[0][1];//par.q];//-k*y[i-1];//
                t[i]=t[i-1]+dt;////par.dtXY;
                x[i] = x[i-1] + 1. / par.gamma * fx * dt + Math.sqrt(2. * par.D * dt) * r.nextGaussian();
                //y[i] = y[i-1] + 1. / par.gamma * fy * dt + Math.sqrt(2. * par.D * dt) * r.nextGaussian();
                //datasetpath.append(x[i]/par.scale, y[i]/par.scale );
                par.stiffness=par.kb*par.T/op.variance(x,par);
                jLabelK.setText(df1.format(par.stiffness*1.E6));                
                datasethist.append(hist.getXPoints(),hist.getYPoints());
                hist.append(x[i]/par.scale);
                hist.setBinWidth((hist.getXMax()-hist.getXMin())/50.);
                panellXY.repaint();
                panellhistogram.repaint();
                drawingPanelHistogram.repaint();
                jTabbedPane1.repaint();
                //System.out.println("Dibuixat punt" + i + " Força x=" + f[0] + " forçaY=" + f[1]);
                if (i > (par.Npath-2)) {
                    jToggleButton2.setSelected(false);
                    //panellhistogram.setMessage(par.Npath+" points. Calibration finished");
                }
            }//fi de la calibració
            calibrateOFF();
            if (par.initialized && par.traceractivated) {
                Tracer.write_action_ini("Trap Calibrated");
                    Tracer.write_all_param(par);
                    // Xavi - todo: ara graba paràmetres dues vegades: una en clicar per activar-ho i una altra en clicar per desactivar-ho.
                    // Com fer que grabi només una vegada per animació o calibració?
                Tracer.write_action_end();
            }
            //System.out.println("acaba bucle!");

            //jComboBox6.setSelectedIndex(0);
            //jComboBox6.setEnabled(true);
        }
    }

/**
    class eqmov implements ODE {
        // state conté y vy z vz t
        double[] state = new double[]{objectesYZ.bola.getX(), 0.0, objectesYZ.bola.getY(), 0.0, 0.0};
        Forces forces = new Forces();

        public double[] getState() {
            return state;
        }

        public void getRate(double[] state, double[] rate) {
            double[] Q = forces.calc(par, raigs, 0.0, state[0], state[2])[par.q];
            double f2a = 1. / par.m * (1000. / par.R); //escala d'acceleracions (radis/s^2)
            double kvisc = 6. * Math.PI * par.visc * (par.R / 1000.);
            double[] Fvisc = {kvisc * state[0], kvisc * state[2], kvisc * state[3]};
            rate[0] = state[1];
            rate[1] = f2a * (Q[1] * par.F0 - kvisc * rate[0] * par.R / 1000.); //ay en Radis/s^2
            rate[2] = state[3];
            rate[3] = f2a * (Q[2] * par.F0 - kvisc * rate[2] * par.R / 1000.);
            rate[4] = 1.;

            double xb = objectesYZ.bola.getX();
            double yb = objectesYZ.bola.getY();
            double xm = Math.abs(xb);
            double ym = Math.abs(yb);
            double s = Math.max(xm, ym);
            System.out.println("S'ha executat getRate: x=" + xb + " y=" + yb + " vx=" + state[1] + " vy=" + state[3] + " fx=" + rate[1] + " fy=" + rate[2]);
            s = 1.3 * s;
            if (panellYZ.contains(panellYZ.xToPix(s), panellYZ.yToPix(s))) {
            } else {
                panellYZ.setPreferredMinMax(-s, s, -s, s);
            }
        }
    }
**/
//SET PARAMETERS AFTER SLIDERS
public void lambdaOK(){
           if (par.initialized && par.traceractivated) {
                Tracer.write_action_ini("Change in Wavelength");
                Tracer.write_param("Wavelength (nm)", par.lambda);
                Tracer.write_action_end();
            }
            par.changed=true;
            if(par.regim==1){//en canviar la wavelength, mantenint NAestimated. El w0 varia!
                par.w0=2.*par.lambda/(Math.PI*par.estimatedNA);
                par.w0max=2.*par.lambda/(Math.PI*par.NAmin);
                par.w0min=2.*par.lambda/(Math.PI*par.NAmax);
                setDefaultSettings();
            }

            actualitzarlabels();
            if (par.initialized) {
                par.calcParDependents();
                objectesYZ.repintaregim(panellYZ, par, raigs);
                actualitzar();
                //overfilling.actualitzarcolor(drawingPanel7,par);
            }
            //(canvia sliders per si algun depèn de lambda)
    }
public void powerOK(){
            if (par.initialized && par.traceractivated) {
                Tracer.write_action_ini("Power changed");
                Tracer.write_param("Power (mW)", par.P);
                Tracer.write_action_end();
            }
            par.changed=true;
            if (par.initialized) {
                par.calcParDependents();
                actualitzarlabels();
                raigs = new Raigs(par);
                objectesYZ.repintaregim(panellYZ, par,raigs);
                actualitzar();
            }
            //showusefulpower();
}
public void NAok(){
            if (par.initialized && par.traceractivated) {
                Tracer.write_action_ini("NA changed");
                 Tracer.write_param("NA", par.NA);
                 Tracer.write_action_end();
            }
            par.changed=true;
            par.calcParDependents();
            raigs = new Raigs(par);
            actualitzarlabels();
            if (par.initialized) {
                objectesYZ.repintaregim(panellYZ, par,raigs);
                actualitzar();
            }
}
public void overfillingOK(){
            if (par.initialized && par.traceractivated) {
                Tracer.write_action_ini("Overfilling changed");
                Tracer.write_param("Overfilling", par.pupil2waist);
                Tracer.write_action_end();
            }
            par.changed=true;
            par.calcParDependents();
            showusefulpower();
            raigs=new Raigs(par);
            //raigs = new Raigs(par);
            if (par.initialized) {
                objectesYZ.repintaregim(panellYZ, par, raigs);
                actualitzar();
                overfilling.actualitzar1D(drawingPanel7,par);
            }
}
public void n1OK(){
                 if (par.initialized && par.traceractivated) {
                Tracer.write_action_ini("Refractive index (n1) changed");
                Tracer.write_param("Refractive index (n1)", par.n1);
                Tracer.write_action_end();
             }
            par.changed=true;
/**            if (par.NA > par.NAmax) {
                jSlider15.setValue(jSlider15.getMaximum());
                double NA = ((double) jSlider15.getValue() * (par.NAmax - par.NAmin) / (double)jSlider15.getMaximum() + par.NAmin);
                par.calcParDependents();
                jText15.setText(df.format(NA));
                par.NA = NA;
            } else {
                par.calcParDependents();
                jSlider15.setValue((int) ((par.NA - par.NAmin) / (par.NAmax - par.NAmin) * (double)jSlider15.getMaximum()));
            }**/
            par.calcParDependents();
            if (par.initialized) {
                objectesYZ.repintaregim(panellYZ, par, raigs);
                actualitzar();
            }
}
public void viscOK(){
            if (par.initialized && par.traceractivated) {
                Tracer.write_action_ini("Viscosity changed");
                Tracer.write_param("Viscosity (mPa - s)", par.visc);
                Tracer.write_action_end();
            }
            par.changed=true;
            par.calcParDependents();
            //System.out.println("Viscositat (Pa·s)= "+visc);
            if (par.initialized) {
                actualitzar();
            }
}
public void Tok(){
            par.calcParDependents();
            setDefaultSettings();
             if (par.initialized && par.traceractivated) {
                Tracer.write_action_ini("Temperature changed");
                Tracer.write_param("Temperature (K)", par.T);
                Tracer.write_action_end();
             }
            par.changed=true;
            if (par.initialized) {
                actualitzar();
            }
}
public void n2OK(){
            if (par.initialized && par.traceractivated) {
                Tracer.write_action_ini("n2 changed");
                Tracer.write_param("n2", par.n2);
                Tracer.write_action_end();
             }
            par.changed=true;
            par.calcParDependents();
            if (par.initialized) {
                actualitzar();
            }
}
public void dtOK() {
        if (par.initialized && par.traceractivated) {
            Tracer.write_action_ini("Time step changed");
            Tracer.write_param("Time step", par.dt);
            Tracer.write_action_end();
        }
}
public void zoomOK(){
        par.fieldforces[par.regim]=2.*panellYZ.getXMax()*par.scale*par.zoom;
        //System.out.println(par.fieldforces[par.regim]);

        //objectesYZ.actualitzarectanglecamp(par);
        //System.out.println("xmax camp="+objectesYZ.rectanglecamp.getXMax());
        if(par.initialized){
            actualitzar();
            mostrarectanglecamp();
            actualitzarpanellforces();
        }

}
public void calibrateON(){
        bmthread.start();
        jToggleButton2.setText(cap.buttonPathCalculationON[par.lang]);
        jTabbedPane1.repaint();
        jComboBox1.setEnabled(false);
        jToggleButton1.setEnabled(false);
}
public void calibrateOFF(){
        bmthread.stop();
        jToggleButton2.setSelected(false);
        jToggleButton2.setText(cap.buttonPathCalculationOFF[par.lang]);
        
        //actualitzarpanellforces(); //perquè sinó no representa bé la corba...
        profile.representar(Zforceprofilepanel,Yforceprofilepanel, par);
        profile.calibrate(Yforceprofilepanel, par);
        jComboBox1.setEnabled(true);
        jToggleButton1.setEnabled(true);
}
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox3 = new javax.swing.JComboBox();
        jComboBoxParticles = new javax.swing.JComboBox();
        jCheckBox10 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jPanelSim = new javax.swing.JPanel();
        jTabbedPane5 = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        jLabel68 = new javax.swing.JLabel();
        jSlider22 = new javax.swing.JSlider();
        jLabel70 = new javax.swing.JLabel();
        jComboBox5 = new javax.swing.JComboBox();
        jLabel69 = new javax.swing.JLabel();
        jTabbedPane6 = new javax.swing.JTabbedPane();
        jPanel10 = new javax.swing.JPanel();
        jFormattedTextField9 = new javax.swing.JFormattedTextField();
        jLabel72 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jFormattedTextField10 = new javax.swing.JFormattedTextField();
        jSlider23 = new javax.swing.JSlider();
        jLabel73 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jLabel74 = new javax.swing.JLabel();
        paperera = new javax.swing.JPanel();
        jSlider11 = new javax.swing.JSlider();
        jLabel19 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        label3 = new java.awt.Label();
        label4 = new java.awt.Label();
        label5 = new java.awt.Label();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel193 = new javax.swing.JLabel();
        jLabel194 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel195 = new javax.swing.JLabel();
        jFileChooser1 = new javax.swing.JFileChooser();
        jFrameAbout = new javax.swing.JFrame();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jFrameLicense = new javax.swing.JFrame();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane2 = new javax.swing.JTextPane();
        jFrameHelp = new javax.swing.JFrame();
        jLabel14 = new javax.swing.JLabel();
        jFrameNumbers = new javax.swing.JFrame();
        jPanel12 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabelkz = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabelkx = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabelQzmaxdown = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabelQzmaxup = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabelQxmax = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabelzc2 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabelzc = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelSetup = new javax.swing.JPanel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel49 = new javax.swing.JPanel();
        jLabel180 = new javax.swing.JLabel();
        jFormattedTextField27 = new javax.swing.JFormattedTextField();
        jSlider14 = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        jText1 = new javax.swing.JFormattedTextField();
        jLabel191 = new javax.swing.JLabel();
        jComboBoxObj1 = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanelMie = new javax.swing.JPanel();
        jLabel179 = new javax.swing.JLabel();
        jSlider16 = new javax.swing.JSlider();
        jComboBoxObj = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        jSlider15 = new javax.swing.JSlider();
        jLabel24 = new javax.swing.JLabel();
        drawingPanel7 = new org.opensourcephysics.display.DrawingPanel();
        jText17 = new javax.swing.JLabel();
        jText15 = new javax.swing.JFormattedTextField();
        jText16 = new javax.swing.JFormattedTextField();
        jPanelRayleigh = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jSlider2 = new javax.swing.JSlider();
        jText2 = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabelNA = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jTabbedPane7 = new javax.swing.JTabbedPane();
        jPanel50 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jSlidern1 = new javax.swing.JSlider();
        jLabel36 = new javax.swing.JLabel();
        jSlider20 = new javax.swing.JSlider();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jLabel184 = new javax.swing.JLabel();
        jSlider21 = new javax.swing.JSlider();
        jFormattedTextField28 = new javax.swing.JFormattedTextField();
        jText6 = new javax.swing.JFormattedTextField();
        jComboBoxMed = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel187 = new javax.swing.JLabel();
        jSlidern2 = new javax.swing.JSlider();
        jText7 = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        jSlider5 = new javax.swing.JSlider();
        jText8 = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        jPanelForce = new javax.swing.JPanel();
        drawingPanel2 = new org.opensourcephysics.display.DrawingPanel();
        drawingPanel3 = new org.opensourcephysics.display.DrawingPanel();
        label1 = new java.awt.Label();
        label2 = new java.awt.Label();
        drawingPanel4 = new org.opensourcephysics.display.DrawingPanel();
        drawingPanel6 = new org.opensourcephysics.display.DrawingPanel();
        jToggleButton2 = new javax.swing.JToggleButton();
        drawingPanelHistogram = new org.opensourcephysics.display.DrawingPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabelK = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jToggleButton3 = new javax.swing.JToggleButton();
        jTabbedPane8 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        drawingPanel1 = new org.opensourcephysics.display.DrawingPanel();
        jButton4 = new javax.swing.JButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        jPanel7 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jComboBox1 = new javax.swing.JComboBox();
        jPanelfield = new javax.swing.JPanel();
        jSliderfield = new javax.swing.JSlider();
        jLabelfield = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jPaneldt = new javax.swing.JPanel();
        jLabeldt = new javax.swing.JLabel();
        jSliderdt = new javax.swing.JSlider();
        jLabel25 = new javax.swing.JLabel();
        jLabeltime = new javax.swing.JLabel();
        jLabelforce = new javax.swing.JLabel();
        jMenuBar3 = new javax.swing.JMenuBar();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jCheckBoxMenuItem2 = new javax.swing.JCheckBoxMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem2 = new javax.swing.JRadioButtonMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "select map...", "Force Field", "Fy force along the Y-axis", "Fz force along the Z-axis" }));
        jComboBox3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox3ItemStateChanged(evt);
            }
        });

        jComboBoxParticles.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Polystyrenne", "Sillica", "Air", "User-defined" }));
        jComboBoxParticles.setEnabled(false);
        jComboBoxParticles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxParticlesActionPerformed(evt);
            }
        });

        jCheckBox10.setText("Show laser beam");
        jCheckBox10.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox10StateChanged(evt);
            }
        });

        jCheckBox7.setSelected(true);
        jCheckBox7.setText("Brownian Motion");
        jCheckBox7.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox7StateChanged(evt);
            }
        });
        jCheckBox7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox7ActionPerformed(evt);
            }
        });

        jLabel68.setText("Ray discretization");
        jLabel68.setToolTipText("Set the number of rays along de Y axis for the discretitzation of the wave front");

        jSlider22.setEnabled(false);

        jLabel70.setText("jLabel20");

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Spherical wave", "Gaussian Beam" }));

        jLabel69.setText("Electromagnetic beam:");
        jLabel69.setToolTipText("Set the number of rays along de Y axis for the discretitzation of the wave front");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel68)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider22, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel69)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(70, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel68)
                        .addGap(4, 4, 4))
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel70)
                        .addComponent(jSlider22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel69)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Force Calculation", jPanel9);

        jFormattedTextField9.setText("5");
        jFormattedTextField9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField9ActionPerformed(evt);
            }
        });
        jFormattedTextField9.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFormattedTextField9PropertyChange(evt);
            }
        });

        jLabel72.setText("Steps per second:");

        jLabel71.setText("Duration (s):");

        jFormattedTextField10.setText("10");
        jFormattedTextField10.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFormattedTextField10PropertyChange(evt);
            }
        });

        jSlider23.setEnabled(false);

        jLabel73.setText("Time rate (sim sec / real sec)");
        jLabel73.setToolTipText("Set the number of rays along de Y axis for the discretitzation of the wave front");

        jButton8.setText("Save configuration");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel74.setText("jLabel20");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton8)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel73)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSlider23, 0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel71)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jFormattedTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel72)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jFormattedTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel74)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel71)
                            .addComponent(jFormattedTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel72)
                            .addComponent(jFormattedTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel73)
                                .addGap(4, 4, 4))
                            .addComponent(jSlider23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel74))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane6.addTab("Dynamic settings", jPanel10);

        javax.swing.GroupLayout jPanelSimLayout = new javax.swing.GroupLayout(jPanelSim);
        jPanelSim.setLayout(jPanelSimLayout);
        jPanelSimLayout.setHorizontalGroup(
            jPanelSimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSimLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jTabbedPane6, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelSimLayout.setVerticalGroup(
            jPanelSimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSimLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jSlider11.setMaximum(25);
        jSlider11.setMinimum(2);
        jSlider11.setValue(3);
        jSlider11.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider11StateChanged(evt);
            }
        });

        jLabel19.setText("3");

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("Magnification");
        jLabel23.setPreferredSize(new java.awt.Dimension(125, 25));

        label3.setEnabled(false);
        label3.setText("F=  ? pN");

        label4.setEnabled(false);
        label4.setText("F=  ? pN");

        label5.setEnabled(false);
        label5.setText("F=  ? pN");

        javax.swing.GroupLayout papereraLayout = new javax.swing.GroupLayout(paperera);
        paperera.setLayout(papereraLayout);
        papereraLayout.setHorizontalGroup(
            papereraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 359, Short.MAX_VALUE)
            .addGroup(papereraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(papereraLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jSlider11, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(papereraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(papereraLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(248, 248, 248)))
            .addGroup(papereraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(papereraLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(papereraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addContainerGap()))
        );
        papereraLayout.setVerticalGroup(
            papereraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
            .addGroup(papereraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(papereraLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(papereraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(papereraLayout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jSlider11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(papereraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(papereraLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(papereraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(papereraLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jLabel4.setText("2.5 mm");

        jLabel7.setText("2.5 mm");

        jLabel193.setText("Entrance Pupil diameter (mm):");

        jLabel194.setText("Focal length (mm):");

        jLabel5.setText("jLabel5");

        jLabel195.setText("Beam waist (nm) w0 =");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel193)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel194)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel195)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel195)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel193)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel194)
                    .addComponent(jLabel5))
                .addContainerGap())
        );

        jFileChooser1.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);

        jFrameAbout.setTitle("About");
        jFrameAbout.setMinimumSize(new java.awt.Dimension(400, 300));

        jTextPane1.setEditable(false);
        jTextPane1.setText("Grup d'Innovació Docent en Òptica Física i Fotònica\nUniversitat de Barcelona, 2008\nhttp//www.ub.edu/javaoptics\n\nThis program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.\n \nThis program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.\n \nYou should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.\n \nThis program uses part of the Open Source Physics Library, http://www.opensourcephysics.org");
        jScrollPane1.setViewportView(jTextPane1);

        javax.swing.GroupLayout jFrameAboutLayout = new javax.swing.GroupLayout(jFrameAbout.getContentPane());
        jFrameAbout.getContentPane().setLayout(jFrameAboutLayout);
        jFrameAboutLayout.setHorizontalGroup(
            jFrameAboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrameAboutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );
        jFrameAboutLayout.setVerticalGroup(
            jFrameAboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrameAboutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addContainerGap())
        );

        jFrameLicense.setTitle("GNU License");
        jFrameLicense.setMinimumSize(new java.awt.Dimension(600, 400));

        jScrollPane2.setEnabled(false);

        jTextPane2.setEditable(false);
        jTextPane2.setText("GNU GENERAL PUBLIC LICENSE\n\nVersion 3, 29 June 2007\n\nCopyright © 2007 Free Software Foundation, Inc. <http://fsf.org/>\n\nEveryone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.\nPreamble\n\nThe GNU General Public License is a free, copyleft license for software and other kinds of works.\n\nThe licenses for most software and other practical works are designed to take away your freedom to share and change the works. By contrast, the GNU General Public License is intended to guarantee your freedom to share and change all versions of a program--to make sure it remains free software for all its users. We, the Free Software Foundation, use the GNU General Public License for most of our software; it applies also to any other work released this way by its authors. You can apply it to your programs, too.\n\nWhen we speak of free software, we are referring to freedom, not price. Our General Public Licenses are designed to make sure that you have the freedom to distribute copies of free software (and charge for them if you wish), that you receive source code or can get it if you want it, that you can change the software or use pieces of it in new free programs, and that you know you can do these things.\n\nTo protect your rights, we need to prevent others from denying you these rights or asking you to surrender the rights. Therefore, you have certain responsibilities if you distribute copies of the software, or if you modify it: responsibilities to respect the freedom of others.\n\nFor example, if you distribute copies of such a program, whether gratis or for a fee, you must pass on to the recipients the same freedoms that you received. You must make sure that they, too, receive or can get the source code. And you must show them these terms so they know their rights.\n\nDevelopers that use the GNU GPL protect your rights with two steps: (1) assert copyright on the software, and (2) offer you this License giving you legal permission to copy, distribute and/or modify it.\n\nFor the developers' and authors' protection, the GPL clearly explains that there is no warranty for this free software. For both users' and authors' sake, the GPL requires that modified versions be marked as changed, so that their problems will not be attributed erroneously to authors of previous versions.\n\nSome devices are designed to deny users access to install or run modified versions of the software inside them, although the manufacturer can do so. This is fundamentally incompatible with the aim of protecting users' freedom to change the software. The systematic pattern of such abuse occurs in the area of products for individuals to use, which is precisely where it is most unacceptable. Therefore, we have designed this version of the GPL to prohibit the practice for those products. If such problems arise substantially in other domains, we stand ready to extend this provision to those domains in future versions of the GPL, as needed to protect the freedom of users.\n\nFinally, every program is threatened constantly by software patents. States should not allow patents to restrict development and use of software on general-purpose computers, but in those that do, we wish to avoid the special danger that patents applied to a free program could make it effectively proprietary. To prevent this, the GPL assures that patents cannot be used to render the program non-free.\n\nThe precise terms and conditions for copying, distribution and modification follow.\nTERMS AND CONDITIONS\n0. Definitions.\n\n“This License” refers to version 3 of the GNU General Public License.\n\n“Copyright” also means copyright-like laws that apply to other kinds of works, such as semiconductor masks.\n\n“The Program” refers to any copyrightable work licensed under this License. Each licensee is addressed as “you”. “Licensees” and “recipients” may be individuals or organizations.\n\nTo “modify” a work means to copy from or adapt all or part of the work in a fashion requiring copyright permission, other than the making of an exact copy. The resulting work is called a “modified version” of the earlier work or a work “based on” the earlier work.\n\nA “covered work” means either the unmodified Program or a work based on the Program.\n\nTo “propagate” a work means to do anything with it that, without permission, would make you directly or secondarily liable for infringement under applicable copyright law, except executing it on a computer or modifying a private copy. Propagation includes copying, distribution (with or without modification), making available to the public, and in some countries other activities as well.\n\nTo “convey” a work means any kind of propagation that enables other parties to make or receive copies. Mere interaction with a user through a computer network, with no transfer of a copy, is not conveying.\n\nAn interactive user interface displays “Appropriate Legal Notices” to the extent that it includes a convenient and prominently visible feature that (1) displays an appropriate copyright notice, and (2) tells the user that there is no warranty for the work (except to the extent that warranties are provided), that licensees may convey the work under this License, and how to view a copy of this License. If the interface presents a list of user commands or options, such as a menu, a prominent item in the list meets this criterion.\n1. Source Code.\n\nThe “source code” for a work means the preferred form of the work for making modifications to it. “Object code” means any non-source form of a work.\n\nA “Standard Interface” means an interface that either is an official standard defined by a recognized standards body, or, in the case of interfaces specified for a particular programming language, one that is widely used among developers working in that language.\n\nThe “System Libraries” of an executable work include anything, other than the work as a whole, that (a) is included in the normal form of packaging a Major Component, but which is not part of that Major Component, and (b) serves only to enable use of the work with that Major Component, or to implement a Standard Interface for which an implementation is available to the public in source code form. A “Major Component”, in this context, means a major essential component (kernel, window system, and so on) of the specific operating system (if any) on which the executable work runs, or a compiler used to produce the work, or an object code interpreter used to run it.\n\nThe “Corresponding Source” for a work in object code form means all the source code needed to generate, install, and (for an executable work) run the object code and to modify the work, including scripts to control those activities. However, it does not include the work's System Libraries, or general-purpose tools or generally available free programs which are used unmodified in performing those activities but which are not part of the work. For example, Corresponding Source includes interface definition files associated with source files for the work, and the source code for shared libraries and dynamically linked subprograms that the work is specifically designed to require, such as by intimate data communication or control flow between those subprograms and other parts of the work.\n\nThe Corresponding Source need not include anything that users can regenerate automatically from other parts of the Corresponding Source.\n\nThe Corresponding Source for a work in source code form is that same work.\n2. Basic Permissions.\n\nAll rights granted under this License are granted for the term of copyright on the Program, and are irrevocable provided the stated conditions are met. This License explicitly affirms your unlimited permission to run the unmodified Program. The output from running a covered work is covered by this License only if the output, given its content, constitutes a covered work. This License acknowledges your rights of fair use or other equivalent, as provided by copyright law.\n\nYou may make, run and propagate covered works that you do not convey, without conditions so long as your license otherwise remains in force. You may convey covered works to others for the sole purpose of having them make modifications exclusively for you, or provide you with facilities for running those works, provided that you comply with the terms of this License in conveying all material for which you do not control copyright. Those thus making or running the covered works for you must do so exclusively on your behalf, under your direction and control, on terms that prohibit them from making any copies of your copyrighted material outside their relationship with you.\n\nConveying under any other circumstances is permitted solely under the conditions stated below. Sublicensing is not allowed; section 10 makes it unnecessary.\n3. Protecting Users' Legal Rights From Anti-Circumvention Law.\n\nNo covered work shall be deemed part of an effective technological measure under any applicable law fulfilling obligations under article 11 of the WIPO copyright treaty adopted on 20 December 1996, or similar laws prohibiting or restricting circumvention of such measures.\n\nWhen you convey a covered work, you waive any legal power to forbid circumvention of technological measures to the extent such circumvention is effected by exercising rights under this License with respect to the covered work, and you disclaim any intention to limit operation or modification of the work as a means of enforcing, against the work's users, your or third parties' legal rights to forbid circumvention of technological measures.\n4. Conveying Verbatim Copies.\n\nYou may convey verbatim copies of the Program's source code as you receive it, in any medium, provided that you conspicuously and appropriately publish on each copy an appropriate copyright notice; keep intact all notices stating that this License and any non-permissive terms added in accord with section 7 apply to the code; keep intact all notices of the absence of any warranty; and give all recipients a copy of this License along with the Program.\n\nYou may charge any price or no price for each copy that you convey, and you may offer support or warranty protection for a fee.\n5. Conveying Modified Source Versions.\n\nYou may convey a work based on the Program, or the modifications to produce it from the Program, in the form of source code under the terms of section 4, provided that you also meet all of these conditions:\n\n    * a) The work must carry prominent notices stating that you modified it, and giving a relevant date.\n    * b) The work must carry prominent notices stating that it is released under this License and any conditions added under section 7. This requirement modifies the requirement in section 4 to “keep intact all notices”.\n    * c) You must license the entire work, as a whole, under this License to anyone who comes into possession of a copy. This License will therefore apply, along with any applicable section 7 additional terms, to the whole of the work, and all its parts, regardless of how they are packaged. This License gives no permission to license the work in any other way, but it does not invalidate such permission if you have separately received it.\n    * d) If the work has interactive user interfaces, each must display Appropriate Legal Notices; however, if the Program has interactive interfaces that do not display Appropriate Legal Notices, your work need not make them do so.\n\nA compilation of a covered work with other separate and independent works, which are not by their nature extensions of the covered work, and which are not combined with it such as to form a larger program, in or on a volume of a storage or distribution medium, is called an “aggregate” if the compilation and its resulting copyright are not used to limit the access or legal rights of the compilation's users beyond what the individual works permit. Inclusion of a covered work in an aggregate does not cause this License to apply to the other parts of the aggregate.\n6. Conveying Non-Source Forms.\n\nYou may convey a covered work in object code form under the terms of sections 4 and 5, provided that you also convey the machine-readable Corresponding Source under the terms of this License, in one of these ways:\n\n    * a) Convey the object code in, or embodied in, a physical product (including a physical distribution medium), accompanied by the Corresponding Source fixed on a durable physical medium customarily used for software interchange.\n    * b) Convey the object code in, or embodied in, a physical product (including a physical distribution medium), accompanied by a written offer, valid for at least three years and valid for as long as you offer spare parts or customer support for that product model, to give anyone who possesses the object code either (1) a copy of the Corresponding Source for all the software in the product that is covered by this License, on a durable physical medium customarily used for software interchange, for a price no more than your reasonable cost of physically performing this conveying of source, or (2) access to copy the Corresponding Source from a network server at no charge.\n    * c) Convey individual copies of the object code with a copy of the written offer to provide the Corresponding Source. This alternative is allowed only occasionally and noncommercially, and only if you received the object code with such an offer, in accord with subsection 6b.\n    * d) Convey the object code by offering access from a designated place (gratis or for a charge), and offer equivalent access to the Corresponding Source in the same way through the same place at no further charge. You need not require recipients to copy the Corresponding Source along with the object code. If the place to copy the object code is a network server, the Corresponding Source may be on a different server (operated by you or a third party) that supports equivalent copying facilities, provided you maintain clear directions next to the object code saying where to find the Corresponding Source. Regardless of what server hosts the Corresponding Source, you remain obligated to ensure that it is available for as long as needed to satisfy these requirements.\n    * e) Convey the object code using peer-to-peer transmission, provided you inform other peers where the object code and Corresponding Source of the work are being offered to the general public at no charge under subsection 6d.\n\nA separable portion of the object code, whose source code is excluded from the Corresponding Source as a System Library, need not be included in conveying the object code work.\n\nA “User Product” is either (1) a “consumer product”, which means any tangible personal property which is normally used for personal, family, or household purposes, or (2) anything designed or sold for incorporation into a dwelling. In determining whether a product is a consumer product, doubtful cases shall be resolved in favor of coverage. For a particular product received by a particular user, “normally used” refers to a typical or common use of that class of product, regardless of the status of the particular user or of the way in which the particular user actually uses, or expects or is expected to use, the product. A product is a consumer product regardless of whether the product has substantial commercial, industrial or non-consumer uses, unless such uses represent the only significant mode of use of the product.\n\n“Installation Information” for a User Product means any methods, procedures, authorization keys, or other information required to install and execute modified versions of a covered work in that User Product from a modified version of its Corresponding Source. The information must suffice to ensure that the continued functioning of the modified object code is in no case prevented or interfered with solely because modification has been made.\n\nIf you convey an object code work under this section in, or with, or specifically for use in, a User Product, and the conveying occurs as part of a transaction in which the right of possession and use of the User Product is transferred to the recipient in perpetuity or for a fixed term (regardless of how the transaction is characterized), the Corresponding Source conveyed under this section must be accompanied by the Installation Information. But this requirement does not apply if neither you nor any third party retains the ability to install modified object code on the User Product (for example, the work has been installed in ROM).\n\nThe requirement to provide Installation Information does not include a requirement to continue to provide support service, warranty, or updates for a work that has been modified or installed by the recipient, or for the User Product in which it has been modified or installed. Access to a network may be denied when the modification itself materially and adversely affects the operation of the network or violates the rules and protocols for communication across the network.\n\nCorresponding Source conveyed, and Installation Information provided, in accord with this section must be in a format that is publicly documented (and with an implementation available to the public in source code form), and must require no special password or key for unpacking, reading or copying.\n7. Additional Terms.\n\n“Additional permissions” are terms that supplement the terms of this License by making exceptions from one or more of its conditions. Additional permissions that are applicable to the entire Program shall be treated as though they were included in this License, to the extent that they are valid under applicable law. If additional permissions apply only to part of the Program, that part may be used separately under those permissions, but the entire Program remains governed by this License without regard to the additional permissions.\n\nWhen you convey a copy of a covered work, you may at your option remove any additional permissions from that copy, or from any part of it. (Additional permissions may be written to require their own removal in certain cases when you modify the work.) You may place additional permissions on material, added by you to a covered work, for which you have or can give appropriate copyright permission.\n\nNotwithstanding any other provision of this License, for material you add to a covered work, you may (if authorized by the copyright holders of that material) supplement the terms of this License with terms:\n\n    * a) Disclaiming warranty or limiting liability differently from the terms of sections 15 and 16 of this License; or\n    * b) Requiring preservation of specified reasonable legal notices or author attributions in that material or in the Appropriate Legal Notices displayed by works containing it; or\n    * c) Prohibiting misrepresentation of the origin of that material, or requiring that modified versions of such material be marked in reasonable ways as different from the original version; or\n    * d) Limiting the use for publicity purposes of names of licensors or authors of the material; or\n    * e) Declining to grant rights under trademark law for use of some trade names, trademarks, or service marks; or\n    * f) Requiring indemnification of licensors and authors of that material by anyone who conveys the material (or modified versions of it) with contractual assumptions of liability to the recipient, for any liability that these contractual assumptions directly impose on those licensors and authors.\n\nAll other non-permissive additional terms are considered “further restrictions” within the meaning of section 10. If the Program as you received it, or any part of it, contains a notice stating that it is governed by this License along with a term that is a further restriction, you may remove that term. If a license document contains a further restriction but permits relicensing or conveying under this License, you may add to a covered work material governed by the terms of that license document, provided that the further restriction does not survive such relicensing or conveying.\n\nIf you add terms to a covered work in accord with this section, you must place, in the relevant source files, a statement of the additional terms that apply to those files, or a notice indicating where to find the applicable terms.\n\nAdditional terms, permissive or non-permissive, may be stated in the form of a separately written license, or stated as exceptions; the above requirements apply either way.\n8. Termination.\n\nYou may not propagate or modify a covered work except as expressly provided under this License. Any attempt otherwise to propagate or modify it is void, and will automatically terminate your rights under this License (including any patent licenses granted under the third paragraph of section 11).\n\nHowever, if you cease all violation of this License, then your license from a particular copyright holder is reinstated (a) provisionally, unless and until the copyright holder explicitly and finally terminates your license, and (b) permanently, if the copyright holder fails to notify you of the violation by some reasonable means prior to 60 days after the cessation.\n\nMoreover, your license from a particular copyright holder is reinstated permanently if the copyright holder notifies you of the violation by some reasonable means, this is the first time you have received notice of violation of this License (for any work) from that copyright holder, and you cure the violation prior to 30 days after your receipt of the notice.\n\nTermination of your rights under this section does not terminate the licenses of parties who have received copies or rights from you under this License. If your rights have been terminated and not permanently reinstated, you do not qualify to receive new licenses for the same material under section 10.\n9. Acceptance Not Required for Having Copies.\n\nYou are not required to accept this License in order to receive or run a copy of the Program. Ancillary propagation of a covered work occurring solely as a consequence of using peer-to-peer transmission to receive a copy likewise does not require acceptance. However, nothing other than this License grants you permission to propagate or modify any covered work. These actions infringe copyright if you do not accept this License. Therefore, by modifying or propagating a covered work, you indicate your acceptance of this License to do so.\n10. Automatic Licensing of Downstream Recipients.\n\nEach time you convey a covered work, the recipient automatically receives a license from the original licensors, to run, modify and propagate that work, subject to this License. You are not responsible for enforcing compliance by third parties with this License.\n\nAn “entity transaction” is a transaction transferring control of an organization, or substantially all assets of one, or subdividing an organization, or merging organizations. If propagation of a covered work results from an entity transaction, each party to that transaction who receives a copy of the work also receives whatever licenses to the work the party's predecessor in interest had or could give under the previous paragraph, plus a right to possession of the Corresponding Source of the work from the predecessor in interest, if the predecessor has it or can get it with reasonable efforts.\n\nYou may not impose any further restrictions on the exercise of the rights granted or affirmed under this License. For example, you may not impose a license fee, royalty, or other charge for exercise of rights granted under this License, and you may not initiate litigation (including a cross-claim or counterclaim in a lawsuit) alleging that any patent claim is infringed by making, using, selling, offering for sale, or importing the Program or any portion of it.\n11. Patents.\n\nA “contributor” is a copyright holder who authorizes use under this License of the Program or a work on which the Program is based. The work thus licensed is called the contributor's “contributor version”.\n\nA contributor's “essential patent claims” are all patent claims owned or controlled by the contributor, whether already acquired or hereafter acquired, that would be infringed by some manner, permitted by this License, of making, using, or selling its contributor version, but do not include claims that would be infringed only as a consequence of further modification of the contributor version. For purposes of this definition, “control” includes the right to grant patent sublicenses in a manner consistent with the requirements of this License.\n\nEach contributor grants you a non-exclusive, worldwide, royalty-free patent license under the contributor's essential patent claims, to make, use, sell, offer for sale, import and otherwise run, modify and propagate the contents of its contributor version.\n\nIn the following three paragraphs, a “patent license” is any express agreement or commitment, however denominated, not to enforce a patent (such as an express permission to practice a patent or covenant not to sue for patent infringement). To “grant” such a patent license to a party means to make such an agreement or commitment not to enforce a patent against the party.\n\nIf you convey a covered work, knowingly relying on a patent license, and the Corresponding Source of the work is not available for anyone to copy, free of charge and under the terms of this License, through a publicly available network server or other readily accessible means, then you must either (1) cause the Corresponding Source to be so available, or (2) arrange to deprive yourself of the benefit of the patent license for this particular work, or (3) arrange, in a manner consistent with the requirements of this License, to extend the patent license to downstream recipients. “Knowingly relying” means you have actual knowledge that, but for the patent license, your conveying the covered work in a country, or your recipient's use of the covered work in a country, would infringe one or more identifiable patents in that country that you have reason to believe are valid.\n\nIf, pursuant to or in connection with a single transaction or arrangement, you convey, or propagate by procuring conveyance of, a covered work, and grant a patent license to some of the parties receiving the covered work authorizing them to use, propagate, modify or convey a specific copy of the covered work, then the patent license you grant is automatically extended to all recipients of the covered work and works based on it.\n\nA patent license is “discriminatory” if it does not include within the scope of its coverage, prohibits the exercise of, or is conditioned on the non-exercise of one or more of the rights that are specifically granted under this License. You may not convey a covered work if you are a party to an arrangement with a third party that is in the business of distributing software, under which you make payment to the third party based on the extent of your activity of conveying the work, and under which the third party grants, to any of the parties who would receive the covered work from you, a discriminatory patent license (a) in connection with copies of the covered work conveyed by you (or copies made from those copies), or (b) primarily for and in connection with specific products or compilations that contain the covered work, unless you entered into that arrangement, or that patent license was granted, prior to 28 March 2007.\n\nNothing in this License shall be construed as excluding or limiting any implied license or other defenses to infringement that may otherwise be available to you under applicable patent law.\n12. No Surrender of Others' Freedom.\n\nIf conditions are imposed on you (whether by court order, agreement or otherwise) that contradict the conditions of this License, they do not excuse you from the conditions of this License. If you cannot convey a covered work so as to satisfy simultaneously your obligations under this License and any other pertinent obligations, then as a consequence you may not convey it at all. For example, if you agree to terms that obligate you to collect a royalty for further conveying from those to whom you convey the Program, the only way you could satisfy both those terms and this License would be to refrain entirely from conveying the Program.\n13. Use with the GNU Affero General Public License.\n\nNotwithstanding any other provision of this License, you have permission to link or combine any covered work with a work licensed under version 3 of the GNU Affero General Public License into a single combined work, and to convey the resulting work. The terms of this License will continue to apply to the part which is the covered work, but the special requirements of the GNU Affero General Public License, section 13, concerning interaction through a network will apply to the combination as such.\n14. Revised Versions of this License.\n\nThe Free Software Foundation may publish revised and/or new versions of the GNU General Public License from time to time. Such new versions will be similar in spirit to the present version, but may differ in detail to address new problems or concerns.\n\nEach version is given a distinguishing version number. If the Program specifies that a certain numbered version of the GNU General Public License “or any later version” applies to it, you have the option of following the terms and conditions either of that numbered version or of any later version published by the Free Software Foundation. If the Program does not specify a version number of the GNU General Public License, you may choose any version ever published by the Free Software Foundation.\n\nIf the Program specifies that a proxy can decide which future versions of the GNU General Public License can be used, that proxy's public statement of acceptance of a version permanently authorizes you to choose that version for the Program.\n\nLater license versions may give you additional or different permissions. However, no additional obligations are imposed on any author or copyright holder as a result of your choosing to follow a later version.\n15. Disclaimer of Warranty.\n\nTHERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM “AS IS” WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE, YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.\n16. Limitation of Liability.\n\nIN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MODIFIES AND/OR CONVEYS THE PROGRAM AS PERMITTED ABOVE, BE LIABLE TO YOU FOR DAMAGES, INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR INABILITY TO USE THE PROGRAM (INCLUDING BUT NOT LIMITED TO LOSS OF DATA OR DATA BEING RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD PARTIES OR A FAILURE OF THE PROGRAM TO OPERATE WITH ANY OTHER PROGRAMS), EVEN IF SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.\n17. Interpretation of Sections 15 and 16.\n\nIf the disclaimer of warranty and limitation of liability provided above cannot be given local legal effect according to their terms, reviewing courts shall apply local law that most closely approximates an absolute waiver of all civil liability in connection with the Program, unless a warranty or assumption of liability accompanies a copy of the Program in return for a fee.\n\nEND OF TERMS AND CONDITIONS\nHow to Apply These Terms to Your New Programs\n\nIf you develop a new program, and you want it to be of the greatest possible use to the public, the best way to achieve this is to make it free software which everyone can redistribute and change under these terms.\n\nTo do so, attach the following notices to the program. It is safest to attach them to the start of each source file to most effectively state the exclusion of warranty; and each file should have at least the “copyright” line and a pointer to where the full notice is found.\n\n    <one line to give the program's name and a brief idea of what it does.>\n    Copyright (C) <year>  <name of author>\n\n    This program is free software: you can redistribute it and/or modify\n    it under the terms of the GNU General Public License as published by\n    the Free Software Foundation, either version 3 of the License, or\n    (at your option) any later version.\n\n    This program is distributed in the hope that it will be useful,\n    but WITHOUT ANY WARRANTY; without even the implied warranty of\n    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n    GNU General Public License for more details.\n\n    You should have received a copy of the GNU General Public License\n    along with this program.  If not, see <http://www.gnu.org/licenses/>.\n\nAlso add information on how to contact you by electronic and paper mail.\n\nIf the program does terminal interaction, make it output a short notice like this when it starts in an interactive mode:\n\n    <program>  Copyright (C) <year>  <name of author>\n    This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.\n    This is free software, and you are welcome to redistribute it\n    under certain conditions; type `show c' for details.\n\nThe hypothetical commands `show w' and `show c' should show the appropriate parts of the General Public License. Of course, your program's commands might be different; for a GUI interface, you would use an “about box”.\n\nYou should also get your employer (if you work as a programmer) or school, if any, to sign a “copyright disclaimer” for the program, if necessary. For more information on this, and how to apply and follow the GNU GPL, see <http://www.gnu.org/licenses/>.\n\nThe GNU General Public License does not permit incorporating your program into proprietary programs. If your program is a subroutine library, you may consider it more useful to permit linking proprietary applications with the library. If this is what you want to do, use the GNU Lesser General Public License instead of this License. But first, please read <http://www.gnu.org/philosophy/why-not-lgpl.html>.\n");
        jTextPane2.setMaximumSize(null);
        jTextPane2.setPreferredSize(null);
        jScrollPane2.setViewportView(jTextPane2);

        javax.swing.GroupLayout jFrameLicenseLayout = new javax.swing.GroupLayout(jFrameLicense.getContentPane());
        jFrameLicense.getContentPane().setLayout(jFrameLicenseLayout);
        jFrameLicenseLayout.setHorizontalGroup(
            jFrameLicenseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrameLicenseLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
                .addContainerGap())
        );
        jFrameLicenseLayout.setVerticalGroup(
            jFrameLicenseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrameLicenseLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                .addContainerGap())
        );

        jFrameHelp.setTitle("Help");
        jFrameHelp.setMinimumSize(new java.awt.Dimension(200, 200));

        jLabel14.setText("Soon available...");

        javax.swing.GroupLayout jFrameHelpLayout = new javax.swing.GroupLayout(jFrameHelp.getContentPane());
        jFrameHelp.getContentPane().setLayout(jFrameHelpLayout);
        jFrameHelpLayout.setHorizontalGroup(
            jFrameHelpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrameHelpLayout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(jLabel14)
                .addContainerGap(78, Short.MAX_VALUE))
        );
        jFrameHelpLayout.setVerticalGroup(
            jFrameHelpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrameHelpLayout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jLabel14)
                .addContainerGap(97, Short.MAX_VALUE))
        );

        jFrameNumbers.setTitle("Force curves parameters");
        jFrameNumbers.setMinimumSize(new java.awt.Dimension(400, 300));
        jFrameNumbers.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                jFrameNumbersWindowClosing(evt);
            }
        });

        jLabel27.setText("Slope (in z=zeq) =");

        jLabelkz.setText("jLabel16");

        jLabel26.setText("pN/"+"\u03BC"+"m");

        jLabel30.setFont(new java.awt.Font("Tahoma", 3, 11));
        jLabel30.setText("LOGITUDINAL FORCE CURVE (z axis)");

        jLabel28.setText("Slope (in x=0) =");

        jLabelkx.setText("jLabel3");

        jLabel29.setText("pN/"+"\u03BC"+"m");

        jLabel31.setText("Max. force under the trap =");

        jLabel32.setText("pN");

        jLabelQzmaxdown.setText("jLabel33");

        jLabel34.setText("Max. force over the trap =");

        jLabelQzmaxup.setText("jLabel35");

        jLabel37.setText("pN");

        jLabel38.setFont(new java.awt.Font("Tahoma", 3, 11));
        jLabel38.setText("TRANSVERSAL FORCE CURVE");

        jLabel39.setText("Max. force =");

        jLabelQxmax.setText("jLabel33");

        jLabel41.setText("pN");

        jLabel33.setText("(at z =");

        jLabelzc2.setText("jLabel3");

        jLabel35.setText("\u03BC"+"m)");

        jLabel15.setText("Z equilibrium =");

        jLabelzc.setText("jLabel16");

        jLabel16.setText("\u03BC"+"m");

        jButton2.setText("Close");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelQzmaxdown)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel32))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelQzmaxup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel37))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addGap(61, 61, 61)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelQxmax)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel41))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelkx)
                                .addGap(9, 9, 9)
                                .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel38)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel33)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelzc2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel35))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelkz)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelzc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelzc)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jLabelkz)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(jLabel32)
                    .addComponent(jLabelQzmaxdown))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(jLabelQzmaxup)
                    .addComponent(jLabel37))
                .addGap(25, 25, 25)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(jLabel33)
                    .addComponent(jLabelzc2)
                    .addComponent(jLabel35))
                .addGap(6, 6, 6)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jLabelkx)
                    .addComponent(jLabel29))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39)
                    .addComponent(jLabelQxmax)
                    .addComponent(jLabel41))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jFrameNumbersLayout = new javax.swing.GroupLayout(jFrameNumbers.getContentPane());
        jFrameNumbers.getContentPane().setLayout(jFrameNumbersLayout);
        jFrameNumbersLayout.setHorizontalGroup(
            jFrameNumbersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jFrameNumbersLayout.setVerticalGroup(
            jFrameNumbersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane3.setAutoscrolls(true);

        jPanel1.setAutoscrolls(true);
        jPanel1.setMaximumSize(null);

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jLabel180.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel180.setText("Wavelength (nm):");

        jFormattedTextField27.setText("0000.0");
        jFormattedTextField27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField27ActionPerformed(evt);
            }
        });

        jSlider14.setMaximum(10000);
        jSlider14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider14MouseReleased(evt);
            }
        });
        jSlider14.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider14StateChanged(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("Power (mW):");
        jLabel1.setToolTipText("Total power at the entrance pupil");
        jLabel1.setPreferredSize(new java.awt.Dimension(125, 25));

        jSlider1.setMaximum(10000);
        jSlider1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider1MouseReleased(evt);
            }
        });
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        jText1.setText("0000.0");
        jText1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jText1ActionPerformed1(evt);
            }
        });

        jLabel191.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel191.setText("Laser type:");

        jComboBoxObj1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "User-defined", "HeNe", "Nd:YAG", "Nd:YAG (doubled)", "Argon" }));
        jComboBoxObj1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxObj1ActionPerformed(evt);
            }
        });

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("nm");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("mW");

        javax.swing.GroupLayout jPanel49Layout = new javax.swing.GroupLayout(jPanel49);
        jPanel49.setLayout(jPanel49Layout);
        jPanel49Layout.setHorizontalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel49Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel49Layout.createSequentialGroup()
                        .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                            .addComponent(jLabel180, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSlider14, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)))
                    .addGroup(jPanel49Layout.createSequentialGroup()
                        .addComponent(jLabel191, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxObj1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel49Layout.createSequentialGroup()
                        .addComponent(jText1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8))
                    .addGroup(jPanel49Layout.createSequentialGroup()
                        .addComponent(jFormattedTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)))
                .addGap(197, 197, 197))
        );

        jPanel49Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jFormattedTextField27, jText1});

        jPanel49Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jComboBoxObj1, jSlider1, jSlider14});

        jPanel49Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel180, jLabel191});

        jPanel49Layout.setVerticalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel49Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel191, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxObj1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel180, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                        .addGroup(jPanel49Layout.createSequentialGroup()
                            .addGap(2, 2, 2)
                            .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jFormattedTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jSlider14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                    .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jText1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel49Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBoxObj1, jFormattedTextField27, jLabel1, jLabel180, jLabel191, jSlider1, jSlider14, jText1});

        jTabbedPane3.addTab("LASER", jPanel49);

        jLabel179.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel179.setText("Objective:");

        jSlider16.setMaximum(100000);
        jSlider16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider16MouseReleased(evt);
            }
        });
        jSlider16.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider16StateChanged(evt);
            }
        });

        jComboBoxObj.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "4X,  NA=0.1", "10X,  NA=0.25", "20X,  NA=0.4", "40X,  NA=0.65", "60X,  NA=0.85", "100X,  NA=1.25", "User-defined" }));
        jComboBoxObj.setSelectedIndex(6);
        jComboBoxObj.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxObjItemStateChanged(evt);
            }
        });

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("Numerical apperture NA:");
        jLabel22.setPreferredSize(new java.awt.Dimension(125, 25));

        jSlider15.setMaximum(10000);
        jSlider15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider15MouseReleased(evt);
            }
        });
        jSlider15.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider15StateChanged(evt);
            }
        });

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText("Overfilling:");
        jLabel24.setToolTipText("Diameter of the entrance pupile divided by the input beam waist");
        jLabel24.setPreferredSize(new java.awt.Dimension(125, 25));

        drawingPanel7.setMaximumSize(new java.awt.Dimension(80, 80));
        drawingPanel7.setMinimumSize(new java.awt.Dimension(80, 80));
        drawingPanel7.setPreferredSize(new java.awt.Dimension(80, 80));

        jText17.setText("jLabel20");

        jText15.setText("R");
        jText15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jText8ActionPerformed(evt);
            }
        });

        jText16.setText("R");
        jText16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jText16ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMieLayout = new javax.swing.GroupLayout(jPanelMie);
        jPanelMie.setLayout(jPanelMieLayout);
        jPanelMieLayout.setHorizontalGroup(
            jPanelMieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMieLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanelMieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .addComponent(jLabel179, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMieLayout.createSequentialGroup()
                        .addGroup(jPanelMieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSlider16, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                            .addComponent(jComboBoxObj, 0, 140, Short.MAX_VALUE)
                            .addComponent(jSlider15, 0, 140, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelMieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jText15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                            .addComponent(jText16, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(jText17, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(drawingPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(47, Short.MAX_VALUE))
        );

        jPanelMieLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jComboBoxObj, jSlider15, jSlider16});

        jPanelMieLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel179, jLabel22, jLabel24});

        jPanelMieLayout.setVerticalGroup(
            jPanelMieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMieLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMieLayout.createSequentialGroup()
                        .addGroup(jPanelMieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel179, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxObj, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelMieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSlider15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jText15, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelMieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jText16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelMieLayout.createSequentialGroup()
                                .addGroup(jPanelMieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSlider16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jText17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(235, 235, 235))))
                    .addGroup(jPanelMieLayout.createSequentialGroup()
                        .addComponent(drawingPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(48, Short.MAX_VALUE))))
        );

        jPanelMieLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBoxObj, jLabel179, jLabel22, jLabel24, jSlider15, jSlider16, jText15, jText16, jText17});

        jTabbedPane4.addTab("BEAM GEOMETRY", jPanelMie);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Beam waist (w0):");
        jLabel17.setToolTipText("Total power at the entrance pupil");
        jLabel17.setPreferredSize(new java.awt.Dimension(125, 25));

        jSlider2.setMaximum(10000);
        jSlider2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider2StateChanged(evt);
            }
        });

        jText2.setText("0000.0");
        jText2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jText2ActionPerformed1(evt);
            }
        });

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Numerical apperture (NA):");
        jLabel18.setToolTipText("Total power at the entrance pupil");
        jLabel18.setPreferredSize(new java.awt.Dimension(125, 25));

        jLabelNA.setText("jLabel20");
        jLabelNA.setToolTipText("Estimated NA according to the paraxial approximation (it fails for angles > 30º)");

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("\u03BC"+"m");
        jLabel20.setToolTipText("Total power at the entrance pupil");
        jLabel20.setPreferredSize(new java.awt.Dimension(125, 25));

        javax.swing.GroupLayout jPanelRayleighLayout = new javax.swing.GroupLayout(jPanelRayleigh);
        jPanelRayleigh.setLayout(jPanelRayleighLayout);
        jPanelRayleighLayout.setHorizontalGroup(
            jPanelRayleighLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRayleighLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelRayleighLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelRayleighLayout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jSlider2, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE))
                    .addGroup(jPanelRayleighLayout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelNA, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jText2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanelRayleighLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel17, jLabel18});

        jPanelRayleighLayout.setVerticalGroup(
            jPanelRayleighLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRayleighLayout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addGroup(jPanelRayleighLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelRayleighLayout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelRayleighLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelNA, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelRayleighLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jText2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(66, 66, 66))
        );

        jTabbedPane4.addTab("BEAM GEOMETRY", jPanelRayleigh);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("n (medium):");
        jLabel13.setPreferredSize(new java.awt.Dimension(125, 25));

        jSlidern1.setMaximum(10000);
        jSlidern1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlidern1MouseReleased(evt);
            }
        });
        jSlidern1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlidern1StateChanged(evt);
            }
        });

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel36.setText("Viscosity (mPa·s)");
        jLabel36.setMaximumSize(null);
        jLabel36.setMinimumSize(null);
        jLabel36.setPreferredSize(new java.awt.Dimension(125, 25));

        jSlider20.setMaximum(10000);
        jSlider20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider20MouseReleased(evt);
            }
        });
        jSlider20.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider20StateChanged(evt);
            }
        });

        jFormattedTextField1.setText("visc");
        jFormattedTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField1ActionPerformed(evt);
            }
        });

        jLabel184.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel184.setText("Temperature (K):");

        jSlider21.setMaximum(10000);
        jSlider21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider21MouseReleased(evt);
            }
        });
        jSlider21.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider21StateChanged(evt);
            }
        });

        jFormattedTextField28.setText("T");
        jFormattedTextField28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField28ActionPerformed(evt);
            }
        });

        jText6.setText("R");
        jText6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jText6ActionPerformed(evt);
            }
        });

        jComboBoxMed.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "User-defined", "Water" }));
        jComboBoxMed.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxMedItemStateChanged(evt);
            }
        });

        jLabel9.setText("mPa·s");

        jLabel11.setText("K");

        javax.swing.GroupLayout jPanel50Layout = new javax.swing.GroupLayout(jPanel50);
        jPanel50.setLayout(jPanel50Layout);
        jPanel50Layout.setHorizontalGroup(
            jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel50Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel50Layout.createSequentialGroup()
                        .addComponent(jComboBoxMed, 0, 380, Short.MAX_VALUE)
                        .addGap(109, 109, 109))
                    .addGroup(jPanel50Layout.createSequentialGroup()
                        .addGroup(jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                            .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                            .addComponent(jLabel184, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSlidern1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSlider20, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(jSlider21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jFormattedTextField28, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                            .addComponent(jFormattedTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                            .addComponent(jText6, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE))
                        .addGap(96, 96, 96))))
        );

        jPanel50Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel13, jLabel184, jLabel36});

        jPanel50Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jSlider20, jSlider21, jSlidern1});

        jPanel50Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jFormattedTextField1, jFormattedTextField28, jText6});

        jPanel50Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel11, jLabel9});

        jPanel50Layout.setVerticalGroup(
            jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel50Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBoxMed, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jText6, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                    .addComponent(jSlidern1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel50Layout.createSequentialGroup()
                        .addGroup(jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jSlider20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jFormattedTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jFormattedTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSlider21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel50Layout.createSequentialGroup()
                        .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel184, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(31, 31, 31))
        );

        jPanel50Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jFormattedTextField1, jFormattedTextField28, jLabel11, jLabel13, jLabel184, jLabel36, jLabel9, jSlider20, jSlider21, jSlidern1, jText6});

        jTabbedPane7.addTab("MEDIUM", jPanel50);

        jLabel187.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel187.setText("label n2");

        jSlidern2.setMaximum(10000);
        jSlidern2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlidern2MouseReleased(evt);
            }
        });
        jSlidern2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlidern2StateChanged(evt);
            }
        });

        jText7.setText("R");
        jText7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField30ActionPerformed(evt);
            }
        });

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("label radius");
        jLabel10.setPreferredSize(new java.awt.Dimension(125, 25));

        jSlider5.setMaximum(10000);
        jSlider5.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider5StateChanged(evt);
            }
        });

        jText8.setText("R");
        jText8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jText8jFormattedTextField30ActionPerformed(evt);
            }
        });

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel12.setText("\u03BC"+"m");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel187, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, 0, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jSlider5, 0, 180, Short.MAX_VALUE)
                    .addComponent(jSlidern2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jText7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                    .addComponent(jText8, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addGap(199, 199, 199))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jSlider5, jSlidern2});

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel187});

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jText7, jText8});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel187, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jText7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSlidern2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jText8, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jSlider5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel10, jLabel12, jLabel187, jSlider5, jSlidern2, jText7, jText8});

        jTabbedPane2.addTab("PARTICLE", jPanel4);

        javax.swing.GroupLayout jPanelSetupLayout = new javax.swing.GroupLayout(jPanelSetup);
        jPanelSetup.setLayout(jPanelSetupLayout);
        jPanelSetupLayout.setHorizontalGroup(
            jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSetupLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTabbedPane2, 0, 0, Short.MAX_VALUE)
                    .addComponent(jTabbedPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                    .addComponent(jTabbedPane3, 0, 0, Short.MAX_VALUE)
                    .addComponent(jTabbedPane4))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanelSetupLayout.setVerticalGroup(
            jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSetupLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jTabbedPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jTabbedPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Experimental settings", jPanelSetup);

        drawingPanel3.setMaximumSize(new java.awt.Dimension(300, 300));
        drawingPanel3.setMinimumSize(new java.awt.Dimension(300, 300));

        label1.setEnabled(false);
        label1.setText("Fmin=0 pN");

        label2.setAlignment(java.awt.Label.RIGHT);
        label2.setEnabled(false);
        label2.setText("Fmax =  ? pN");

        drawingPanel4.setToolTipText("Transversal force profile at z_equilibrium (if not possible, z=0 is taken)");
        drawingPanel4.setMaximumSize(new java.awt.Dimension(200, 200));
        drawingPanel4.setMinimumSize(new java.awt.Dimension(200, 200));

        jToggleButton2.setText("Calibrate");
        jToggleButton2.setToolTipText("Trap calibration from Boltzmann statistics. p(x) = C exp [-(x^2)/(2*sd^2)] = - C exp (-U(x)/kbT)\n");
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton2ActionPerformed(evt);
            }
        });

        drawingPanelHistogram.setMaximumSize(new java.awt.Dimension(200, 200));
        drawingPanelHistogram.setMinimumSize(new java.awt.Dimension(200, 200));

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Trap stiffness:");

        jLabelK.setText("jLabel3");

        jLabel3.setText("pN/"+"\u03BC"+"m");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelK, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(64, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel2)
                .addComponent(jLabelK)
                .addComponent(jLabel3))
        );

        jToggleButton3.setText("plot details");
        jToggleButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelForceLayout = new javax.swing.GroupLayout(jPanelForce);
        jPanelForce.setLayout(jPanelForceLayout);
        jPanelForceLayout.setHorizontalGroup(
            jPanelForceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelForceLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelForceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(drawingPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelForceLayout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(jPanelForceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(drawingPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelForceLayout.createSequentialGroup()
                                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(drawingPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelForceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(drawingPanelHistogram, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelForceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jToggleButton3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jToggleButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(drawingPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)))
                .addGap(46, 46, 46))
        );

        jPanelForceLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {drawingPanel3, drawingPanel4, drawingPanel6, drawingPanelHistogram});

        jPanelForceLayout.setVerticalGroup(
            jPanelForceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelForceLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelForceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelForceLayout.createSequentialGroup()
                        .addComponent(drawingPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(drawingPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelForceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelForceLayout.createSequentialGroup()
                        .addComponent(drawingPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButton2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelForceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(drawingPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(drawingPanelHistogram, 0, 260, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanelForceLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {drawingPanel3, drawingPanel4, drawingPanel6, drawingPanelHistogram});

        jTabbedPane1.addTab("Force analysis", jPanelForce);

        drawingPanel1.setToolTipText("YZ-plane view. Shows the refraction of the rays through the particle in the propagation direction");
        drawingPanel1.setMaximumSize(new java.awt.Dimension(300, 300));
        drawingPanel1.setMessage("");
        drawingPanel1.setMinimumSize(new java.awt.Dimension(300, 300));
        drawingPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                drawingPanel1MouseReleased(evt);
            }
        });

        jButton4.setText("Take bead to focus");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jToggleButton1.setText("Animation");
        jToggleButton1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jToggleButton1StateChanged(evt);
            }
        });
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jCheckBox1.setText("Net force (orange)");
        jCheckBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox1ItemStateChanged(evt);
            }
        });

        jCheckBox2.setText("Scattering force (red)");
        jCheckBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox2ItemStateChanged(evt);
            }
        });

        jCheckBox3.setText("Gradient force (blue)");
        jCheckBox3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox3ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheckBox3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox2, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                            .addComponent(jCheckBox1, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE))
                        .addGap(75, 75, 75)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Mie Regime (Ray optics)", "Rayleigh Regime (dipolar approach)" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jPanelfield.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jSliderfield.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSliderfieldMouseReleased(evt);
            }
        });
        jSliderfield.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderfieldStateChanged(evt);
            }
        });

        jLabelfield.setText("jLabel14");

        jLabel21.setText("Zoom:");
        jLabel21.setToolTipText("Set the size of the region for force analysis respect to the trap diagram size");

        javax.swing.GroupLayout jPanelfieldLayout = new javax.swing.GroupLayout(jPanelfield);
        jPanelfield.setLayout(jPanelfieldLayout);
        jPanelfieldLayout.setHorizontalGroup(
            jPanelfieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelfieldLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSliderfield, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelfield, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(53, Short.MAX_VALUE))
        );
        jPanelfieldLayout.setVerticalGroup(
            jPanelfieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelfieldLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelfieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabelfield, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSliderfield, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanelfieldLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel21, jLabelfield, jSliderfield});

        jPaneldt.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabeldt.setText("jLabel14");

        jSliderdt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSliderdtMouseReleased(evt);
            }
        });
        jSliderdt.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderdtStateChanged(evt);
            }
        });

        jLabel25.setText("Time step:");

        javax.swing.GroupLayout jPaneldtLayout = new javax.swing.GroupLayout(jPaneldt);
        jPaneldt.setLayout(jPaneldtLayout);
        jPaneldtLayout.setHorizontalGroup(
            jPaneldtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPaneldtLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSliderdt, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabeldt, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(54, Short.MAX_VALUE))
        );
        jPaneldtLayout.setVerticalGroup(
            jPaneldtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPaneldtLayout.createSequentialGroup()
                .addGroup(jPaneldtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPaneldtLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPaneldtLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPaneldtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabeldt)
                            .addComponent(jSliderdt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        jPaneldtLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel25, jLabeldt, jSliderdt});

        jLabeltime.setText("Time = 0.00 ms");

        jLabelforce.setText("jLabel40");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPaneldt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox1, 0, 315, Short.MAX_VALUE)
                    .addComponent(drawingPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                    .addComponent(jPanelfield, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jToggleButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelforce, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabeltime, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(drawingPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jToggleButton1)
                    .addComponent(jLabeltime))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jLabelforce))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(jPaneldt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPaneldt, jPanelfield});

        jTabbedPane8.addTab("Dielectric sphere within an Optical Trap", jPanel2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jTabbedPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 549, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(45, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, 0, 0, Short.MAX_VALUE)
                    .addComponent(jTabbedPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 677, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jScrollPane3.setViewportView(jPanel1);

        jMenu5.setText("File");

        jMenuItem10.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem10.setText("Load settings");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem10);

        jMenuItem9.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem9.setText("Save settings");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem9);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Exit");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed1(evt);
            }
        });
        jMenu5.add(jMenuItem2);

        jCheckBoxMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItem2.setText("Record activity in file");
        jCheckBoxMenuItem2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItem2ItemStateChanged(evt);
            }
        });
        jMenu5.add(jCheckBoxMenuItem2);

        jMenuBar3.add(jMenu5);

        jMenu1.setText("View");

        jCheckBoxMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItem1.setText("Show time step slider");
        jCheckBoxMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jCheckBoxMenuItem1);

        jMenu3.setText("Ray brightness");

        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText("Proportional to power");
        jRadioButtonMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem1);

        jRadioButtonMenuItem2.setText("Proprotional to relative force contribution");
        jRadioButtonMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem2ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem2);

        jMenu1.add(jMenu3);

        jMenuBar3.add(jMenu1);

        jMenu2.setText("Help");

        jMenuItem4.setText("About");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuItem5.setText("GNU License");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuItem6.setText("Tweezers Help");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem6);

        jMenuBar3.add(jMenu2);

        setJMenuBar(jMenuBar3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 902, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
        private void jCheckBox10StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox10StateChanged
            // TODO add your handling code here:
            objectesYZ.repintaregim(panellYZ, par, raigs);
            mostrarectanglecamp();
        }//GEN-LAST:event_jCheckBox10StateChanged

        private void jComboBox3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox3ItemStateChanged
            // TODO add your handling code here:
            actualitzarpanellforces();
        }//GEN-LAST:event_jComboBox3ItemStateChanged

        private void jSlider11StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider11StateChanged
            // TODO add your handling code here:
            par.Nraigd = jSlider11.getValue();
            //par.NraysYaxis = par.Nraigd;
            jLabel19.setText(String.valueOf(par.Nraigd));
            //objectesYZ.removerays(panellYZ, par);
            actualitzar();
            panellYZ.repaint();
        }//GEN-LAST:event_jSlider11StateChanged

        private void jSlider14StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider14StateChanged
            // TODO add your handling code here:
            double wavelength = ((double) jSlider14.getValue() * (par.lambdamax - par.lambdamin) / (double)jSlider14.getMaximum() + par.lambdamin);
            jFormattedTextField27.setText(intf.format(wavelength));
            par.lambda = wavelength * par.scaleL;
                        
        }//GEN-LAST:event_jSlider14StateChanged

        private void jSlider15StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider15StateChanged
            // TODO add your handling code here:
            double NA = ((double) jSlider15.getValue() * (par.NAmax - par.NAmin) / (double)jSlider15.getMaximum() + par.NAmin);
            jText15.setText(df.format(NA));
            par.NA = NA;
        }//GEN-LAST:event_jSlider15StateChanged

        private void jSlider16StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider16StateChanged
            // TODO add your handling code here:
            double waist2pupil = ((double) jSlider16.getValue() * (par.waist2pupilmax - par.waist2pupilmin) / (double)jSlider16.getMaximum() + par.waist2pupilmin);
            par.waist2pupil= waist2pupil;
            jText16.setText(df.format(waist2pupil));
        }//GEN-LAST:event_jSlider16StateChanged

        private void jSlider20StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider20StateChanged
            // TODO add your handling code here:
            double visc;
            visc = ((double) jSlider20.getValue() * (par.viscmax - par.viscmin)/(double)jSlider20.getMaximum() + par.viscmin);
            jFormattedTextField1.setText(df.format(visc /par.scalevisc));
            par.visc = visc;
            //viscOK();
        }//GEN-LAST:event_jSlider20StateChanged

        private void jSlider21StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider21StateChanged
            // TODO add your handling code here:
            double T = ((double) jSlider21.getValue() * (par.Tmax - par.Tmin) / (double)jSlider21.getMaximum() + par.Tmin);
            jFormattedTextField28.setText(intf.format(T));
            par.T = T;
            //Tok();
        }//GEN-LAST:event_jSlider21StateChanged

        private void jCheckBox7StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox7StateChanged
            // TODO add your handling code here:
            if (jCheckBox7.isSelected()) {
                par.useBM = 1.0;
            } else {
                par.useBM = 0.0;
            }
        }//GEN-LAST:event_jCheckBox7StateChanged

        private void jFormattedTextField9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField9ActionPerformed
        // TODO add your handling code here:
        }//GEN-LAST:event_jFormattedTextField9ActionPerformed

        private void jFormattedTextField9PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jFormattedTextField9PropertyChange
            // TODO add your handling code here:
            par.timemax = op.string2double(jFormattedTextField9.getText());
        }//GEN-LAST:event_jFormattedTextField9PropertyChange

        private void jFormattedTextField10PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jFormattedTextField10PropertyChange
            // TODO add your handling code here:
        }//GEN-LAST:event_jFormattedTextField10PropertyChange

        private void jFormattedTextField27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField27ActionPerformed
            // TODO add your handling code here:
            par.lambda = op.string2double(jFormattedTextField27.getText()) * par.scaleL;
            setDefaultSettings();
            lambdaOK();

        }//GEN-LAST:event_jFormattedTextField27ActionPerformed

        private void jFormattedTextField28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField28ActionPerformed
        // TODO add your handling code here:
            par.T = op.string2double(jFormattedTextField28.getText());
            setDefaultSettings();
            Tok();
        }//GEN-LAST:event_jFormattedTextField28ActionPerformed

        private void jComboBoxParticlesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxParticlesActionPerformed
            // TODO add your handling code here:
            int material = jComboBoxParticles.getSelectedIndex();
            if (material < jComboBoxParticles.getItemCount() - 1) {
                jSlidern2.setEnabled(false);
            } else {
                jSlidern2.setEnabled(true);
            }
            switch (material) {
                case 0: //Polystyrene
                    par.n2 = 1.55;
                    break;
                case 1: //Air
                    par.n2 = 1.;
                    break;
            }
            jText7.setText(df.format(par.n2));
}//GEN-LAST:event_jComboBoxParticlesActionPerformed

        private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
            // TODO add your handling code here:
            jFileChooser1.showSaveDialog(null);
            try{
                String s = jFileChooser1.getSelectedFile().toString();
                par.write(s);
            }catch(Exception e){
                
            }
        }//GEN-LAST:event_jMenuItem1ActionPerformed

        private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
            // TODO add your handling code here:
            jFileChooser1.showOpenDialog(null);
            try {
                String s = jFileChooser1.getSelectedFile().toString();
                par.read(s);
                setDefaultSettings();
            }catch (Exception e){}

        }//GEN-LAST:event_jMenuItem2ActionPerformed

        private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
            // TODO add your handling code here:

            jTabbedPane1.add(jPanelSetup);

        //jFrameSet.setVisible(true);

        }//GEN-LAST:event_jMenuItem3ActionPerformed

        private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
            jFrameAbout.setVisible(true);
        }//GEN-LAST:event_jMenuItem4ActionPerformed

        private void jCheckBoxMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem1ActionPerformed
        // TODO add your handling code here:
        //jSliderdt.setVisible(jCheckBoxMenuItem1.isSelected());//ToggleButton3.isSelected());
        //jLabeldt.setVisible(jCheckBoxMenuItem1.isSelected());
        jPaneldt.setVisible(jCheckBoxMenuItem1.isSelected());
        jSliderdt.setValue((int)((Math.log10(par.dt)-Math.log10(par.dtMin))/(Math.log10(par.dtMax)-Math.log10(par.dtMin))*jSliderdt.getMaximum()));
        }//GEN-LAST:event_jCheckBoxMenuItem1ActionPerformed

        private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
            // TODO add your handling code here:
            if(par.regim==0){//pedas perque faci bé el canvi de tamany en canviar el regim
                par.R=2.E-6;//
                setDefaultSettings();
                overfillingOK();
            }else{// perque canvii el tamany del camp de forces en passar a rayleigh (falla per widths petits)
                par.estimatedNA=par.NA;
                objectesYZ.rectanglecamp.setWidth(0.8104803493449781);//inexplicablement, sense això sense aquest canvi de tamany intermedi no canvia el tamany correctament en fer setWidth i setHeight
                objectesYZ.rectanglecamp.setHeight(0.8104803493449781);
                par.w0=2.*par.lambda/Math.PI/par.estimatedNA;
                setDefaultSettings();
            }

            par.regim = jComboBox1.getSelectedIndex();
            par.zoom=par.zoomdefault[par.regim];
            par.R=par.Rdefault[par.regim];
            jSlider5.setMaximum(par.nticsR[par.regim]);
            jSliderfield.setMaximum(par.nticszoom[par.regim]);
            panellYZ.setPreferredMinMax(-par.field[par.regim], -par.field[par.regim], -par.field[par.regim], -par.field[par.regim]);
            
            par.dt=par.dtdefault[par.regim];
            jSliderdt.setValue((int)((Math.log10(par.dt)-Math.log10(par.dtMin))/(Math.log10(par.dtMax)-Math.log10(par.dtMin))*jSliderdt.getMaximum()));        

            if (par.regim==0){
                jTabbedPane4.add(jPanelMie,"BEAM GEOMETRY");
                jTabbedPane4.remove(jPanelRayleigh);
            }else{
                jTabbedPane4.add(jPanelRayleigh, "BEAM GEOMETRY");
                jTabbedPane4.remove(jPanelMie);                
            }
            jTabbedPane1.setSelectedIndex(0);
             if (par.initialized && par.traceractivated) {
                Tracer.write_action_ini("Selected regime changed");
                Tracer.write_param("Selected regime", Double.valueOf(par.regim));
                Tracer.write_action_end();
             }
            par.changed=true;

            par.calcParDependents();
            setDefaultSettings();
            //System.out.println("Rdespresslider="+par.R);
            objectesYZ.repintaregim(panellYZ, par,raigs);
            netejarpanellforces();
            jCheckBox1.setSelected(false);
            jCheckBox2.setSelected(false);
            jCheckBox3.setSelected(false);
            objectesYZ.bola.setXY(1.E-8,1.E-8);
            actualitzar();
            //descativa overfilling per rayleigh
            if(par.regim==0){
                par.pupil2waist=par.waist2pupildefault;
                setDefaultSettings();
                jLabel24.setEnabled(true);
                jSlider16.setEnabled(true);
                jSlider16.setValue((int) ((par.pupil2waist - par.waist2pupilmin) / (par.waist2pupilmax - par.waist2pupilmin) * (double)jSlider16.getMaximum()));
                overfilling.dibuixar1D(drawingPanel7, par);
                jText16.setEnabled(true);
                jText17.setEnabled(true);
            }else{
                jSlider16.setValue((int) ((par.waist2pupilmax - par.waist2pupilmin) / (par.waist2pupilmax - par.waist2pupilmin) * (double)jSlider16.getMaximum()));
                drawingPanel7.clear();//setVisible(false);
                jLabel24.setEnabled(false);
                jSlider16.setEnabled(false);
                jText16.setEnabled(false);                
                jText17.setEnabled(false);
            }

        }//GEN-LAST:event_jComboBox1ItemStateChanged

        private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

            // TODO add your handling code here: "Take bead to focus"
             if (par.initialized && par.traceractivated) {
                Tracer.write_action_ini("Take bead to focus");
                Tracer.write_action_end();
             }
            //objectesYZ.repintaregim(panellYZ, par,raigs);
            //objectesYZ.actualitzar(panellYZ, par, raigs);
            mostrarectanglecamp();
            objectesYZ.dibuixar(panellYZ, par);
            objectesYZ.repintaregim(panellYZ, par, raigs);

            objectesYZ.bola.setXY(1.E-8,1.E-8);
            actualitzar();
        }//GEN-LAST:event_jButton4ActionPerformed

        private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
            // TODO add your handling code here:
            //objectesYZ.repintaregim(panellYZ, par,raigs);
            actualitzar();
             if (par.initialized && par.traceractivated) {
                Tracer.write_action_ini("Animation button clicked");
                    Tracer.write_all_param(par);
                Tracer.write_action_end();
             }
            mostrarectanglecamp();
            
            if (bolaperduda()){
                //System.out.println("lost ball!");
                objectesYZ.bola.setXY(1.E-12,1.E-12);
                actualitzar();
                 if (par.initialized && par.traceractivated) {
                       Tracer.write_action_ini("Lost ball");
                       Tracer.write_action_end();
                 }
            }
            if (jToggleButton1.isSelected()) {
                jToggleButton1.setText(cap.stopsim[par.lang]);
            } else {
                jToggleButton1.setText(cap.startsim[par.lang]);
            }

            if (jToggleButton1.isSelected()) {
                //panellYZ.setMessage(null);
                running = true;
                resoldre = new calcThread();
                resoldre.start();
                jToggleButton1.setText(cap.stopsim[par.lang]);
            } else {
                jToggleButton1.setText(cap.startsim[par.lang]);
                running = false;
                //par.t=0.00;
                //panellYZ.setMessage("",2);
            }

        }//GEN-LAST:event_jToggleButton1ActionPerformed

        private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
            // TODO add your handling code here:
            double P = ((double) jSlider1.getValue() * (par.Pmax - par.Pmin) / (double)jSlider1.getMaximum() + par.Pmin);
            jText1.setText(intf.format(P));
            par.P = P * par.scaleP;
        }//GEN-LAST:event_jSlider1StateChanged

        private void jSlider5StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider5StateChanged
            // TODO add your handling code here:
                //System.out.println("R inicial="+par.R);
                double size = ((double) jSlider5.getValue() * (par.Rmax[par.regim] - par.Rmin[par.regim]) / (double)jSlider5.getMaximum() + par.Rmin[par.regim]);
                par.R = size;// * par.scaleR;

         if (par.initialized && par.traceractivated) {
            Tracer.write_action_ini("R changed");
            Tracer.write_param("R", par.R);
            Tracer.write_action_end();
         }
            par.changed=true;
                //System.out.println("R_Slider="+par.R);
                jText8.setText(df.format(par.R/par.scaleR));
                par.calcParDependents();
                //objectesYZ.actualitzarectanglecamp(par);
                mostrarectanglecamp();
                if(par.initialized){
                    actualitzar();
                }
        }//GEN-LAST:event_jSlider5StateChanged

        private void jSlidern2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlidern2StateChanged
            // TODO add your handling code here:
            double nsphere = ((double) jSlidern2.getValue() * (par.n2max - par.n2min) / (double)jSlidern2.getMaximum() + par.n2min);
            jText7.setText(df.format(nsphere));
            par.n2 = nsphere;
            //n2OK();
}//GEN-LAST:event_jSlidern2StateChanged

        private void jSlidern1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlidern1StateChanged
            // TODO add your handling code here:
            double nmedi = ((double) jSlidern1.getValue() * (par.n1max - par.n1min) / (double)jSlidern1.getMaximum()+ par.n1min);
            jText6.setText(df.format(nmedi));
            par.n1 = nmedi;
            //n1OK();
}//GEN-LAST:event_jSlidern1StateChanged

        private void jCheckBox7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox7ActionPerformed
        // TODO add your handling code here:
        }//GEN-LAST:event_jCheckBox7ActionPerformed

        private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:

        }//GEN-LAST:event_jButton8ActionPerformed

        private void jComboBoxObj1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxObj1ActionPerformed

            // TODO add your handling code here:
             if (par.initialized && par.traceractivated) {
                Tracer.write_action_ini("Laser type changed");
                Tracer.write_param("Laser type", Double.valueOf(jComboBoxObj1.getSelectedIndex()));
                Tracer.write_action_end();
             }
            par.changed=true;
            switch(jComboBoxObj1.getSelectedIndex()){
                case 0:
                    par.lambda=500.*par.scaleL;
                    jSlider14.setEnabled(true);
                    break;
                case 1://HeNe
                    par.lambda=633.E-9;
                    jSlider14.setEnabled(false);
                    break;
                case 2://Nd:YAG
                    par.lambda=1064.E-9;
                    jSlider14.setEnabled(false);
                    break;
                case 3: //Nd:YAG doubled
                    par.lambda=532.E-9;
                    jSlider14.setEnabled(false);
                    break;
                case 4:
                    par.lambda=490.E-9;
                    jSlider14.setEnabled(false);
                    break;
            }
            setDefaultSettings();
        }//GEN-LAST:event_jComboBoxObj1ActionPerformed

        private void jComboBoxObjItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxObjItemStateChanged
            // TODO add your handling code here:
            int objective = jComboBoxObj.getSelectedIndex();
                 if (par.initialized && par.traceractivated) {
                    Tracer.write_action_ini("Standard objective changed");
                    Tracer.write_param("Standard objective", Double.valueOf(objective));
                    Tracer.write_action_end();
                 }
            par.changed=true;
            boolean usrobj = (objective == jComboBoxObj.getItemCount() - 1);
            jSlider15.setEnabled(usrobj);
            //jSlider16.setEnabled(usrobj);
            jLabel22.setEnabled(usrobj);
            /////////////jLabel23.setEnabled(usrobj);
            jText15.setEditable(usrobj);
            //jText16.setEnabled(usrobj);
            switch (objective) {
                case 0:
//                    par.augments = 4;
                    par.NA = 0.1;
                    break;
                case 1:
//                    par.augments = 10;
                    par.NA = 0.25;
                    break;
                case 2:
//                    par.augments = 20;
                    par.NA = 0.4;
                    break;
                case 3:
//                    par.augments = 40;
                    par.NA = 0.65;
                    break;
                case 4:
//                    par.augments = 60;
                    par.NA = 0.85;
                    break;
                case 5:
//                    par.augments = 100;
                    par.NA = 1.25;
                    break;
                case 6:
//                    par.augments = (double) jSlider16.getValue() * (par.augmentsmax - par.augmentsmin) / 10000. + par.augmentsmin;
            }
            par.calcParDependents();
            setDefaultSettings();
            actualitzarlabels();
            objectesYZ.repintaregim(panellYZ, par,raigs);
            actualitzar();
        }//GEN-LAST:event_jComboBoxObjItemStateChanged

        private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton2ActionPerformed

            bmthread = new bmThread();
            if (jToggleButton2.isSelected()){
                calibrateON();
            }else{
                calibrateOFF();
            }
        }//GEN-LAST:event_jToggleButton2ActionPerformed

        private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged

            // TODO add your handling code here:
            if (par.initialized && par.traceractivated) {
                Tracer.write_action_ini("Tab changed");
                Tracer.write_param("Tab set to",jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()));
                Tracer.write_action_end();
            }
            if (par.initialized){
                calibrateOFF();
                if (jToggleButton1.isSelected()){
                    jComboBox1.setEnabled(false);
                }
            }

            //jToggleButton2.setSelected(false);//Atura calibració quan canvia de pestanya
            //objectesYZ.actualitzarectanglecamp(par);
            mostrarectanglecamp();
            seleccionaforces();
            if(par.changed){
                netejarpanellforces();
                par.calibrated=false;
                //mostrarectanglecamp();
                par.changed=false;
            }
            if(jTabbedPane1.getSelectedIndex()==1){
                jCheckBox1.setSelected(true);
                mostrarectanglecamp();
                jPanelfield.setVisible(true);
                jSliderfield.setValue((int)Math.round((par.zoom-par.zoomin[par.regim])/par.incrzoom[par.regim]));//(par.zoomax-par.zoomin)*(double)jSliderfield.getMaximum()));            //mostrarectanglecamp();
                if (par.initialized){
                    actualitzarpanellforces();
                    jFrameNumbers.setVisible(par.showplotdetails);
                }
                 if (par.initialized && par.traceractivated) {
                            Tracer.write_action_ini("Calculation of force field and force curves");
                            Tracer.write_action_end();
                 }
                if (par.ztrapfound) {
                     if (par.initialized && par.traceractivated) {
                        Tracer.write_action_ini("Z equilibrium changed");
                        Tracer.write_param("Z equilibrium (m)", par.ztrap);
                        Tracer.write_action_end();
                     }
                } else {
                         if (par.initialized && par.traceractivated) {
                            Tracer.write_action_ini("Z equilibrium not found");
                            Tracer.write_action_end();
                         }
                }
           }else{
                jPanelfield.setVisible(false);
                jFrameNumbers.setVisible(false);
                mostrarectanglecamp();
            }
                        
        }//GEN-LAST:event_jTabbedPane1StateChanged

        private void jText6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jText6ActionPerformed
            // TODO add your handling code here:
            double n1 = op.string2double(jText6.getText());
            par.n1=op.coherce(n1,par.n1min,par.n1max);
            setDefaultSettings();
            n1OK();
}//GEN-LAST:event_jText6ActionPerformed

        private void jFormattedTextField30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField30ActionPerformed
            // TODO add your handling code here:
            double n2= op.string2double(jText7.getText());
            par.n2=op.coherce(n2,par.n2min,par.n2max);
            setDefaultSettings();
            n2OK();
        }//GEN-LAST:event_jFormattedTextField30ActionPerformed

        private void jFormattedTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField1ActionPerformed
            // TODO add your handling code here:
            double visc = op.string2double(jFormattedTextField1.getText())*par.scalevisc;
            par.visc=op.coherce(visc,par.viscmin,par.viscmax);
            setDefaultSettings();
            viscOK();
        }//GEN-LAST:event_jFormattedTextField1ActionPerformed

        private void jText16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jText16ActionPerformed
            // TODO add your handling code here:
            double waist2pupil= op.string2double(jText16.getText());
            par.waist2pupil=op.coherce(waist2pupil,par.waist2pupilmin,par.waist2pupilmax);
            setDefaultSettings();
            overfillingOK();
}//GEN-LAST:event_jText16ActionPerformed

        private void jText8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jText8ActionPerformed
            // TODO add your handling code here:
            double NA= op.string2double(jText15.getText());
            par.NA=op.coherce(NA,par.NAmin,par.NAmax);
            setDefaultSettings();
            NAok();
        }//GEN-LAST:event_jText8ActionPerformed

        private void jText1ActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jText1ActionPerformed1
            // TODO add your handling code here:
            par.P = op.string2double(jText1.getText()) * par.scaleP;
            setDefaultSettings();
            powerOK();
	  }//GEN-LAST:event_jText1ActionPerformed1

        private void jText8jFormattedTextField30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jText8jFormattedTextField30ActionPerformed
            // TODO add your handling code here:
            double R = op.string2double(jText8.getText())*par.scaleR;
            par.R=op.coherce(R,par.Rmin[par.regim],par.Rmax[par.regim]);
            //System.out.println("R_text="+par.R);
            setDefaultSettings();
            //System.out.println("R_afterSDF="+par.R);
        }//GEN-LAST:event_jText8jFormattedTextField30ActionPerformed

        private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
            // TODO add your handling code here:
            jFrameLicense.setVisible(true);
        }//GEN-LAST:event_jMenuItem5ActionPerformed

        private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
            // TODO add your handling code here:
            jFrameHelp.setVisible(true);
        }//GEN-LAST:event_jMenuItem6ActionPerformed

        private void jMenuItem2ActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed1
            // TODO add your handling code here:
           if (par.initialized && par.traceractivated) {
                Tracer.end_trace();
           }
            System.exit(-1);
            //this.setVisible(false);
        }//GEN-LAST:event_jMenuItem2ActionPerformed1

        private void jComboBoxMedItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxMedItemStateChanged
            // TODO add your handling code here:
            int medium = jComboBoxMed.getSelectedIndex();
            switch (medium) {
                case 0: //User-defined

                    //User-defined
                 if (par.initialized && par.traceractivated) {
                    Tracer.write_action_ini("Medium changed to User-defined");
                    Tracer.write_param("Medium", Double.valueOf("User-defined"));
                    Tracer.write_action_end();
                 }
            jSlidern1.setEnabled(true);
                    jSlider20.setEnabled(true);
                    jFormattedTextField1.setEditable(true);
                    jText6.setEditable(true);
                    //par.n1 = 1.33;
                    par.water=false;

//                    jText6.setText(df.format(par.n1));
//                    par.visc = 1.E-3;//8.90*Math.pow(10,-4);
//                    jFormattedTextField1.setText(df.format(par.visc * 1000.) + "mPa·s");
                    break;
                case 1: //water

                    //water
                 if (par.initialized && par.traceractivated) {
                    Tracer.write_action_ini("Medium changed to Water");
                    Tracer.write_param("Medium", Double.valueOf("Water"));
                    Tracer.write_action_end();
                 }
                    jSlidern1.setEnabled(false);
                    jSlider20.setEnabled(false);
                    jFormattedTextField1.setEditable(false);
                    jText6.setEditable(false);
                    par.water=true;
                    par.n1 = 1.33;
                    par.calcParDependents();
                    jFormattedTextField1.setText(df.format(par.visc*1000.));
//                    jText6.setText(df.format(par.n1));
//                    par.visc = 8.90 * Math.pow(10, -4);
//                    jFormattedTextField1.setText(df.format(par.visc * 1000.) + "mPa·s");
                    break;
            }            
            setDefaultSettings();
        }//GEN-LAST:event_jComboBoxMedItemStateChanged

        private void jToggleButton1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jToggleButton1StateChanged
            // TODO add your handling code here:
        }//GEN-LAST:event_jToggleButton1StateChanged

        private void jSliderdtStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderdtStateChanged
            // TODO add your handling code here:
            par.dt=Math.pow(10,jSliderdt.getValue()*(Math.log10(par.dtMax)-Math.log10(par.dtMin))/jSliderdt.getMaximum()+Math.log10(par.dtMin));
            jLabeldt.setText(ef.format(par.dt)+" s");//String.valueOf(par.dt[par.regim]));
            dtOK();
        }//GEN-LAST:event_jSliderdtStateChanged

        private void jSlider2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider2StateChanged
            // TODO add your handling code here:
            double w0=Math.pow(10,jSlider2.getValue()*(Math.log10(par.w0max)-Math.log10(par.w0min))/jSlider2.getMaximum()+Math.log10(par.w0min));
            //double w0 = ((double) jSlider2.getValue() * (par.w0max - par.w0min) / (double)jSlider2.getMaximum() + par.w0min);
            jText2.setText(df.format(w0/par.scalew0));
            par.w0 = w0;
            par.estimatedNA=2.*par.lambda/Math.PI/par.w0;
            double angleNA=Math.asin(par.estimatedNA/par.n1);
            jLabelNA.setText(df.format(par.estimatedNA));
            if(angleNA*180/Math.PI<30){
                jLabelNA.setForeground(java.awt.Color.black);
            }else{
                jLabelNA.setForeground(java.awt.Color.red);
            }
//                 if (par.initialized && par.traceractivated) {
//                        Tracer.write_action_ini("Wavelength changed");
// Xavi ToDo: això sembla repetit amb "Change in wavelength" més a munt(i s'escriuenels dos al log en caviar el tipus de laser. Eliminar-ne un?
            // N'elimino aquest deixant-lo comentat per si cal re-activar-lo (si manca la traça a algun lloc, etc)
//                        Tracer.write_param("Wavelength (nm)", par.lambda);
//                        Tracer.write_action_end();
//                 }
                par.changed=true;

            //setDefaultSettings(); (canvia sliders per si algun depèn de lambda)
            actualitzarlabels();
            if (par.initialized) {
                par.calcParDependents();
                objectesYZ.repintaregim(panellYZ, par, raigs);
                actualitzar();
                //overfilling.actualitzarcolor(drawingPanel7,par);
            }
            
        }//GEN-LAST:event_jSlider2StateChanged

        private void jText2ActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jText2ActionPerformed1
            // TODO add your handling code here:
            par.w0 = op.string2double(jText2.getText()) * par.scalew0;
            setDefaultSettings();            
        }//GEN-LAST:event_jText2ActionPerformed1

        private void jSliderfieldStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderfieldStateChanged
            // TODO add your handling code here:
            par.zoom = (double)jSliderfield.getValue()*par.incrzoom[par.regim]+par.zoomin[par.regim];
            jLabelfield.setText(df.format(par.zoom)+"x");//(df.format(par.fieldforces[par.regim]/par.scale)+"\u03BC"+"m");

        }//GEN-LAST:event_jSliderfieldStateChanged

        private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
            // TODO add your handling code here:
            Tracer.end_trace();
        }//GEN-LAST:event_formWindowClosed

        private void jToggleButton3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jToggleButton3KeyPressed
            // TODO add your handling code here:
        }//GEN-LAST:event_jToggleButton3KeyPressed

        private void jToggleButton3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jToggleButton3KeyReleased
            // TODO add your handling code here:
                 if (par.initialized && par.traceractivated) {
                    Tracer.write_action_ini("Set time pressed");
                    Tracer.write_action_end();
                 }
        }//GEN-LAST:event_jToggleButton3KeyReleased

        private void jSlider14MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider14MouseReleased
            // TODO add your handling code here:
            lambdaOK();
        }//GEN-LAST:event_jSlider14MouseReleased

        private void jSlider1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider1MouseReleased
            // TODO add your handling code here:
            powerOK();
        }//GEN-LAST:event_jSlider1MouseReleased

        private void jSlider15MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider15MouseReleased
            // TODO add your handling code here:
            NAok();
        }//GEN-LAST:event_jSlider15MouseReleased

        private void jSlider16MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider16MouseReleased
            // TODO add your handling code here:
            overfillingOK();
        }//GEN-LAST:event_jSlider16MouseReleased

        private void jSlidern1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlidern1MouseReleased
            // TODO add your handling code here:
            n1OK();
        }//GEN-LAST:event_jSlidern1MouseReleased

        private void jSliderfieldMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSliderfieldMouseReleased
            // TODO add your handling code here:
            zoomOK();
        }//GEN-LAST:event_jSliderfieldMouseReleased

        private void drawingPanel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_drawingPanel1MouseReleased
            // TODO add your handling code here:
                if (par.initialized && par.traceractivated) {
                System.out.println("Hola!");
                Tracer.write_action_ini("Ball moved with the mouse pointer to a new location");
                Tracer.write_param("X",objectesYZ.bola.getX());
                Tracer.write_param("Y",objectesYZ.bola.getY());
                Tracer.write_action_end();
             }
        }//GEN-LAST:event_drawingPanel1MouseReleased

        private void jCheckBoxMenuItem2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem2ItemStateChanged
            // TODO add your handling code here:
            par.traceractivated=jCheckBoxMenuItem2.isSelected();
             if (par.initialized) {
                    // Xavi - Start the trace only once the user has clicked on the appropiate menu item to record
                    // Josep - Si es fa clic al botó cancel a la finestra d'input, que no traci
                 if (par.traceractivated) {
                        Tracer.set_user();
                        if (Tracer.user_id==null){
                            par.traceractivated=false;
                            jCheckBoxMenuItem2.setSelected(false);
                        }else{
                            Tracer.start_trace();
                            Tracer.write_action_ini("Recording activity changed to " +par.traceractivated);
                            Tracer.write_all_param(par);
                            Tracer.write_action_end();
                        }
                 }
             }
        }//GEN-LAST:event_jCheckBoxMenuItem2ItemStateChanged

        private void jCheckBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox1ItemStateChanged
            // TODO add your handling code here:
                       par.selectedforces[0]=jCheckBox1.isSelected();

             if (par.initialized && par.traceractivated) {
                Tracer.write_action_ini("Net force (orange) changed to: "+par.selectedforces[0]);
                Tracer.write_action_end();
             }
            profile.representar(Zforceprofilepanel,Yforceprofilepanel,par);
            actualitzar();
            if (jCheckBox1.isSelected()){
                jCheckBox1.setForeground(profile.orangeplus);

            }else{
                jCheckBox1.setForeground(java.awt.Color.BLACK);
            }

        }//GEN-LAST:event_jCheckBox1ItemStateChanged

        private void jCheckBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox2ItemStateChanged
            // TODO add your handling code here:
                        par.selectedforces[1]=jCheckBox2.isSelected();
                 if (par.initialized && par.traceractivated) {
                    Tracer.write_action_ini("Scattering force (red) changed to: " + par.selectedforces[1]);
                    Tracer.write_action_end();
                 }
             profile.representar(Zforceprofilepanel, Yforceprofilepanel,par);
             if (jCheckBox2.isSelected()){
                jCheckBox2.setForeground(java.awt.Color.RED);
             }else{
                jCheckBox2.setForeground(java.awt.Color.BLACK);
            }
            actualitzar();
        }//GEN-LAST:event_jCheckBox2ItemStateChanged

        private void jCheckBox3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox3ItemStateChanged
            // TODO add your handling code here:
                        par.selectedforces[2]=jCheckBox3.isSelected();
                 if (par.initialized && par.traceractivated) {
                    Tracer.write_action_ini("Gradient force (blue) changed to: " + par.selectedforces[2]);
                    Tracer.write_action_end();
                 }
            profile.representar(Zforceprofilepanel, Yforceprofilepanel,par);
            actualitzar();
             if (jCheckBox3.isSelected()){
                jCheckBox3.setForeground(java.awt.Color.BLUE);
             }else{
                jCheckBox3.setForeground(java.awt.Color.BLACK);
             }
        }//GEN-LAST:event_jCheckBox3ItemStateChanged

        private void jSlider20MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider20MouseReleased
            // TODO add your handling code here:
            viscOK();
        }//GEN-LAST:event_jSlider20MouseReleased

        private void jSlider21MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider21MouseReleased
            // TODO add your handling code here:
            Tok();
        }//GEN-LAST:event_jSlider21MouseReleased

        private void jSlidern2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlidern2MouseReleased
            // TODO add your handling code here:
            n2OK();
        }//GEN-LAST:event_jSlidern2MouseReleased

        private void jSliderdtMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSliderdtMouseReleased
            // TODO add your handling code here:
            dtOK();
        }//GEN-LAST:event_jSliderdtMouseReleased

        private void jFrameNumbersWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_jFrameNumbersWindowClosing
            // TODO add your handling code here:
            jToggleButton3.setSelected(false);
            par.showplotdetails=jToggleButton3.isSelected();
        }//GEN-LAST:event_jFrameNumbersWindowClosing

        private void jRadioButtonMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem1ActionPerformed
            // TODO add your handling code here:
            if (jRadioButtonMenuItem1.isSelected()){
                par.raymode=0;
                jRadioButtonMenuItem2.setSelected(false);
            }else{
                par.raymode=1;
                jRadioButtonMenuItem2.setSelected(true);
            }
            objectesYZ.pintaraigs(panellYZ,par,raigs);
            actualitzar();
        }//GEN-LAST:event_jRadioButtonMenuItem1ActionPerformed

        private void jRadioButtonMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem2ActionPerformed
            // TODO add your handling code here:
            if (jRadioButtonMenuItem2.isSelected()){
                par.raymode=1;
                jRadioButtonMenuItem1.setSelected(false);
            }else{
                par.raymode=0;
                jRadioButtonMenuItem1.setSelected(true);
            }
            objectesYZ.pintaraigs(panellYZ,par,raigs);
            actualitzar();
        }//GEN-LAST:event_jRadioButtonMenuItem2ActionPerformed

        private void jToggleButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton3ActionPerformed
            // TODO add your handling code here:
            jFrameNumbers.setVisible(jToggleButton3.isSelected());
            par.showplotdetails=jToggleButton3.isSelected();
        }//GEN-LAST:event_jToggleButton3ActionPerformed

        private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
            // TODO add your handling code here:
            jFrameNumbers.setVisible(false);
            jToggleButton3.setSelected(false);
            par.showplotdetails=jToggleButton3.isSelected();
        }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {

//            public void run() {
//                new NewJFrame1().setVisible(true);
//            }
//        });
//    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private org.opensourcephysics.display.DrawingPanel drawingPanel1;
    private org.opensourcephysics.display.DrawingPanel drawingPanel2;
    private org.opensourcephysics.display.DrawingPanel drawingPanel3;
    private org.opensourcephysics.display.DrawingPanel drawingPanel4;
    private org.opensourcephysics.display.DrawingPanel drawingPanel6;
    private org.opensourcephysics.display.DrawingPanel drawingPanel7;
    private org.opensourcephysics.display.DrawingPanel drawingPanelHistogram;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton8;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox10;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem2;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox5;
    private javax.swing.JComboBox jComboBoxMed;
    private javax.swing.JComboBox jComboBoxObj;
    private javax.swing.JComboBox jComboBoxObj1;
    private javax.swing.JComboBox jComboBoxParticles;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JFormattedTextField jFormattedTextField10;
    private javax.swing.JFormattedTextField jFormattedTextField27;
    private javax.swing.JFormattedTextField jFormattedTextField28;
    private javax.swing.JFormattedTextField jFormattedTextField9;
    private javax.swing.JFrame jFrameAbout;
    private javax.swing.JFrame jFrameHelp;
    private javax.swing.JFrame jFrameLicense;
    private javax.swing.JFrame jFrameNumbers;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel179;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel180;
    private javax.swing.JLabel jLabel184;
    private javax.swing.JLabel jLabel187;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel191;
    private javax.swing.JLabel jLabel193;
    private javax.swing.JLabel jLabel194;
    private javax.swing.JLabel jLabel195;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelK;
    private javax.swing.JLabel jLabelNA;
    private javax.swing.JLabel jLabelQxmax;
    private javax.swing.JLabel jLabelQzmaxdown;
    private javax.swing.JLabel jLabelQzmaxup;
    private javax.swing.JLabel jLabeldt;
    private javax.swing.JLabel jLabelfield;
    private javax.swing.JLabel jLabelforce;
    private javax.swing.JLabel jLabelkx;
    private javax.swing.JLabel jLabelkz;
    private javax.swing.JLabel jLabeltime;
    private javax.swing.JLabel jLabelzc;
    private javax.swing.JLabel jLabelzc2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar3;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel49;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel50;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelForce;
    private javax.swing.JPanel jPanelMie;
    private javax.swing.JPanel jPanelRayleigh;
    private javax.swing.JPanel jPanelSetup;
    private javax.swing.JPanel jPanelSim;
    private javax.swing.JPanel jPaneldt;
    private javax.swing.JPanel jPanelfield;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider11;
    private javax.swing.JSlider jSlider14;
    private javax.swing.JSlider jSlider15;
    private javax.swing.JSlider jSlider16;
    private javax.swing.JSlider jSlider2;
    private javax.swing.JSlider jSlider20;
    private javax.swing.JSlider jSlider21;
    private javax.swing.JSlider jSlider22;
    private javax.swing.JSlider jSlider23;
    private javax.swing.JSlider jSlider5;
    private javax.swing.JSlider jSliderdt;
    private javax.swing.JSlider jSliderfield;
    private javax.swing.JSlider jSlidern1;
    private javax.swing.JSlider jSlidern2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTabbedPane jTabbedPane5;
    private javax.swing.JTabbedPane jTabbedPane6;
    private javax.swing.JTabbedPane jTabbedPane7;
    private javax.swing.JTabbedPane jTabbedPane8;
    private javax.swing.JFormattedTextField jText1;
    private javax.swing.JFormattedTextField jText15;
    private javax.swing.JFormattedTextField jText16;
    private javax.swing.JLabel jText17;
    private javax.swing.JFormattedTextField jText2;
    private javax.swing.JFormattedTextField jText6;
    private javax.swing.JFormattedTextField jText7;
    private javax.swing.JFormattedTextField jText8;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextPane jTextPane2;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JToggleButton jToggleButton3;
    private java.awt.Label label1;
    private java.awt.Label label2;
    private java.awt.Label label3;
    private java.awt.Label label4;
    private java.awt.Label label5;
    private javax.swing.JPanel paperera;
    // End of variables declaration//GEN-END:variables

    public void windowOpened(WindowEvent arg0) {
        
    }

    public void windowClosing(WindowEvent arg0) {
        // Record something in the log
       if (par.initialized && par.traceractivated) {
            Tracer.end_trace();
       }
    }

    public void windowClosed(WindowEvent arg0) {
        
    }

    public void windowIconified(WindowEvent arg0) {
        
    }

    public void windowDeiconified(WindowEvent arg0) {
        
    }

    public void windowActivated(WindowEvent arg0) {
        
    }

    public void windowDeactivated(WindowEvent arg0) {
        
    }
}
