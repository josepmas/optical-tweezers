/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
 
package tweezers;
import org.opensourcephysics.display.*;
import java.text.DecimalFormat;
import org.opensourcephysics.display2d.*;
import java.util.Random;
import java.awt.*;//color.*;
/**
 *
 * @author Propietario
 */
public class graf2d {
    Color transparent = new Color(0,0,0,0);
    Color orangeplus = new Color (255,175,0);    
    LambdaRGBColor rgb = new LambdaRGBColor();
    Random r = new Random();
    Parametres par1 = new Parametres();
    GaussianBeam gb = new GaussianBeam();
    InterpolatedPlot intensitymap = new InterpolatedPlot();
    Operacions op= new Operacions();
    int Nraigdmax=par1.Nraigdmax;//nombre de rajos del diagrama
    int Nraigd;
    InteractiveShape bola=InteractiveShape.createEllipse(0,0,2.*par1.R/par1.scale,2.*par1.R/par1.scale); ;
    BoundedShape lent,cover;
    InteractiveShape rectanglecamp;//=InteractiveShape.createRectangle(0.0, 0.0,par1.fieldforces[par1.regim]/par1.scale, par1.fieldforces[par1.regim]/par1.scale);//Nraigd, Nraigd, Nraigd, Nraigd)BoundedShape.createBoundedRectangle(0.0, 0.0,par.fieldforces[par.regim], par.fieldforces[par.regim]);              ;
    BoundedShape rectanglecolor = BoundedShape.createBoundedRectangle(0,0,par1.field[1]/par1.scale,par1.field[1]/par1.scale);
    Arrow[] force = {new Arrow(0, 0, 0, 1),new Arrow(0,0,0,1), new Arrow (0,0,0,1)};
    Arrow[] forceshadow = {new Arrow(0, 0, 0, 1),new Arrow(0,0,0,1), new Arrow (0,0,0,1)};
    BoundedShape feix[] = new BoundedShape[Nraigdmax];        
    BoundedShape reflec[] = new BoundedShape[Nraigdmax];        
    BoundedShape refrac1[] = new BoundedShape[Nraigdmax];        
    BoundedShape refrac2[] = new BoundedShape[Nraigdmax];
    BoundedShape reflec1[] = new BoundedShape[Nraigdmax];
    BoundedShape perfily=BoundedShape.createBoundedRectangle(0, 0, 0, 1);
    BoundedShape perfilz=BoundedShape.createBoundedRectangle(0, 0, 0, 1);
    Circle punt0[] = new Circle[Nraigdmax];
    Circle punt1[] = new Circle[Nraigdmax];
    Circle punt2[] = new Circle[Nraigdmax];
    
    double[][] p0 = new double[Nraigdmax][3];
    double[][] p1 = new double[Nraigdmax][3];
    double[][] p2 = new double[Nraigdmax][3];
    double[][] p3 = new double[Nraigdmax][3];
    double[][] v0 = new double[Nraigdmax][3];
    double[][] v1 = new double[Nraigdmax][3];
    double[][] v2 = new double[Nraigdmax][3];
    double[][] v1r = new double[Nraigdmax][3];
    double[][] p1r = new double[Nraigdmax][3];
    
    double[][] normal= new double[Nraigdmax][3];
    double[][] normal2= new double[Nraigdmax][3];
    boolean[] talla =new boolean[Nraigdmax];
    Forces forces = new Forces();
    double[] angleout=new double[Nraigdmax];
    double[] anglein=new double[Nraigdmax];
    Color gris = java.awt.Color.lightGray;
    Color gristransp=new Color(gris.getRed(),gris.getGreen(),gris.getBlue(),100);    

    public void dibuixar(InteractivePanel panell, Parametres par){
        rectanglecamp= InteractiveShape.createRectangle(0.0, 0.0,par.fieldforces[par.regim]/par.scale, par.fieldforces[par.regim]/par.scale);//Nraigd, Nraigd, Nraigd, Nraigd)BoundedShape.createBoundedRectangle(0.0, 0.0,par.fieldforces[par.regim], par.fieldforces[par.regim]);              ;
        rectanglecamp.edgeColor=gristransp;
        rectanglecamp.color=gristransp;
        rectanglecamp.setEnabled(false);
        
        panell.clear();
        panell.setSquareAspect(true);
        //bola=//x0,y0,a,b
        panell.setInteractiveMouseHandler(panell);
        //actualitzarectanglecamp(par);
        force[0].setColor(orangeplus);
        force[1].setColor(java.awt.Color.RED);
        force[2].setColor(java.awt.Color.BLUE);
        forceshadow[0].setColor(java.awt.Color.BLACK);
        forceshadow[1].setColor(java.awt.Color.BLACK);
        forceshadow[2].setColor(java.awt.Color.BLACK);
        //System.out.println(force[0].getHeadSize());
        //force[0].edgeColor=java.awt.Color.YELLOW;
        //force[1].edgeColor=java.awt.Color.BLACK;
        //force[2].edgeColor=java.awt.Color.BLACK;
        for (int k = 0; k < Nraigdmax; k++) { //afegeix Nraig (o b� Nraigd!!) drawables
            feix[k]=BoundedShape.createBoundedRectangle(0,0,0,1);
            //feix[k].edgeColor = java.awt.Color.green;
            reflec1[k]=BoundedShape.createBoundedRectangle(0,0,0,1);
            //reflec1[k].edgeColor = java.awt.Color.lightGray;
            refrac1[k] = BoundedShape.createBoundedRectangle(0, 0, 0, 1);
            //refrac1[k].edgeColor = java.awt.Color.blue;
            refrac2[k] = BoundedShape.createBoundedRectangle(0, 0, 0, 1);
            //refrac2[k].edgeColor = java.awt.Color.red;
            //punt0[k] = new Circle(0, 0, 2);
            //punt1[k] = new Circle(0, 0, 2);
            //punt2[k] = new Circle(0, 0, 2);
            //punt0[k].color = java.awt.Color.green;
            //punt1[k].color = java.awt.Color.blue;
            //punt2[k].color = java.awt.Color.black;
            //fletxes[k]=new Arrow(0,0,0,1);
            //fletxes[k].setColor(java.awt.Color.LIGHT_GRAY);            
            //panell.addDrawable(fletxes[k]);
        }
        
        perfily.edgeColor = java.awt.Color.darkGray;
        perfilz.edgeColor = java.awt.Color.darkGray;
        panell.addDrawable(bola);
        bola.setXY(0., 1E-9);
        //System.out.println("S'ha executat 'dibuixar'");
            //Raigs raigs0 = new Raigs (par);
        //actualitzar(panell,par,raigs0);//primer ajust dels rajos
    }
//    public void actualitzarectanglecamp(Parametres par){
        //rectanglecamp.setWidth(par.fieldforces[par.regim]/par.scale);
        //rectanglecamp.setHeight((par.fieldforces[par.regim]/par.scale));
//        System.out.println("FIELD FORCES"+par.fieldforces[par.regim]/par.scale);
//        rectanglecamp= InteractiveShape.createRectangle(0.0, 0.0,par.fieldforces[par.regim]/par.scale, par.fieldforces[par.regim]/par.scale);//Nraigd, Nraigd, Nraigd, Nraigd)BoundedShape.createBoundedRectangle(0.0, 0.0,par.fieldforces[par.regim], par.fieldforces[par.regim]);              ;

        //=       
//    }
    
    public void repintaregim (InteractivePanel panell, Parametres par, Raigs raigs){
    //Mostrar elements del feix d'acord amb el règim actiu (rajos i feix gaussià)
        panell.clear();
        double xmax=par.field[par.regim]/2., ymax=par.field[par.regim]/2.;
        panell.setPreferredMinMaxX(-xmax, xmax);
        panell.setPreferredMinMaxY(-ymax, ymax);
        rectanglecamp.setWidth(par.fieldforces[par.regim]/par.scale);
        rectanglecamp.setHeight(par.fieldforces[par.regim]/par.scale);
        double f=par.zcover;
        
       switch (par.regim) {
            case 0:
                panell.addDrawable(gb.black(par));
                //par.showrays = true;//jCheckBox10.isSelected();
                //par.showEMintensity = false;
                for (int k=0;k<Nraigd;k++){
                    panell.addDrawable(feix[k]);
                    panell.addDrawable(refrac1[k]);
                    panell.addDrawable(refrac2[k]);
                    //panell.addDrawable(punt0[k]);
                    //panell.addDrawable(punt1[k]);
                    //panell.addDrawable(punt2[k]);
                    
                    
                    //PUNTS D'ORIGEN:
                    double discr=2.*par.Rcov/(double)(Nraigd-1);//separació entre els orígens dels rajos
                    p0[k][0]=0.;
                    p0[k][1]=(-par.Rcov+(double)k*discr)/par.scale;
                    p0[k][2]=-f/par.scale;
                    v0[k][0]=0.-p0[k][0];
                    v0[k][1]=0.-p0[k][1];
                    v0[k][2]=0.-p0[k][2];
                    v0[k]=op.norm3d(v0[k]);
                    
                    /**if(par.showreflected){ 
                        panell.addDrawable(reflec1[k]);
                    }else{
                        panell.removeDrawable(reflec1[k]);
                    }**/
                    pintaraigs(panell,par,raigs);
                }
                break;
            case 1:
                if (par.P>0){
                    intensitymap=gb.IntensityMapYZ(par);
                    panell.addDrawable(intensitymap);
                }else{
                    panell.addDrawable(gb.black(par));
                }
                panell.addDrawable(rectanglecolor);
                rectanglecolor.setEnabled(false);
                colorejar(par);
                break;
            case 2:
                break;
            }
       panell.removeDrawable(bola) ;
       panell.addDrawable(bola);
//       bola.setXY(0.,0.);
       
    }
    public void colorejar(Parametres par){ //pinta el rectangle semitransparent per aparentar feix de color lambda en Rayleigh
        Color colorgb=rgb.lambda2RGB(par,50);
        rectanglecolor.color=colorgb;
        rectanglecolor.edgeColor=colorgb;
    }
    
    public void pintaraigs (InteractivePanel panell, Parametres par, Raigs raigs){
        for (int k=0;k<Nraigd;k++){
            pintaraigk(par,raigs, k);        
        }
    }
    public void pintaraigk (Parametres par, Raigs raigs, int k){
        int alpha;
        if(par.raymode==0){
            double rayrelativepower = raigs.p[k]/op.max(raigs.p);//Math.exp(-2.*(Math.pow(p0[k][0]*par.scale,2)+Math.pow(p0[k][1]*par.scale,2))/par.wcov);//op.w(-par.f, par));
            double beampower= par.Puseful/par.scaleP/par.Pmax;
            alpha = (int)(rayrelativepower*beampower*255);
        }else{
            //proves per veure quins rajos fan mes força_
            double relativeforce=raigs.dF[k]/op.max(raigs.dF);//prova
            alpha = (int)(relativeforce*255.);//prova
        }
            Color colorgb=rgb.lambda2RGB(par,alpha);                   
            feix[k].edgeColor=colorgb;//
            refrac1[k].edgeColor=colorgb;
            refrac2[k].edgeColor=colorgb;
    }
    double Ft=0.;
    public void actualitzar (InteractivePanel panell, Parametres par,Raigs raigs){ //pc: punt central de la bola, en mm

        //AJUSTAR MIDA DEL PANELL AL TAMANY DE LA BOLA:
       double size =2*par.R/par.scale; // OJO!!! 0.04 mida mínima de la pilota perquè setWidth funcioni
       //System.out.println("0 ball size="+bola.getWidth());
       //System.out.println("set size ="+size);
       bola.setWidth(size);
       //System.out.println("2 ball size="+bola.getWidth()); 
       bola.setHeight(size);
       panell.repaint();
                
       double field = par.field[par.regim];///2.;
       double actualXMax=panell.getXMax();

       if (size>actualXMax){
            panell.setPreferredMinMax(-size, size, -size, size);
       }else{
           if(size>field){
               panell.setPreferredMinMax(-size, size, -size, size);              
           }else{
               panell.setPreferredMinMax(-field, field, -field, field);
           }
       }
       
       
       Nraigd=par.Nraigd;
       double[] pc = getPosition(); //Central position of the ball

       //CALCUL FORÇA
       double[][] F = forces.calc(par,raigs, 0.0, pc[1]*par.scale, pc[2]*par.scale);
       //passar a pN
       double Fx=F[0][0]*Math.pow(10,12);
       double Fy=F[0][1]*Math.pow(10,12);
       double Fz=F[0][2]*Math.pow(10,12);
       Ft=Math.sqrt(Fx*Fx+Fy*Fy+Fz*Fz);
       double Fnorm=1.E-3;//par.F0*1.E12/1.;//pN
       double arrowsize=panell.getXMax()/5.;

       //DIBUIXAR REFRACCIONS DELS RAJOS
       if (par.regim==0){
           double anglelimit=op.anglelimit(par.n1,par.n2);       
                //System.out.println("angle limit="+anglelimit);       
           for (int k=0;k<Nraigd;k++){//fer-ho per cada raig del dibuix

            //PUNT D'INTERSECCIÓ AMB LA BOLA:
            //System.out.println("p0"+p0[k][1]+","+p0[k][2]+" v0="+v0[k][1]+","+v0[k][2]+" pc="+pc[1]+","+pc[2]);
                p1[k]=op.intersec(p0[k], v0[k], pc,par.R/par.scaleR);
                feix[k]=op.raig(feix[k],p0[k],p1[k]);
                talla[k]=op.booleantalla(p0[k],v0[k],pc,par)    ;

                if (talla[k]){//el raig k intersecciona amb la bola
                    pintaraigk(par,raigs,k);
                    normal[k][0]=p1[k][0]-pc[0];
                    normal[k][1]=p1[k][1]-pc[1];
                    normal[k][2]=p1[k][2]-pc[2];
                    normal[k]=op.norm3d(normal[k]);

                    //RAIG REFLECTIT:
                    v1r[k]=reflec2d(v0[k],normal[k]);
                    double m=10.;
                    p1r[k][0]=p1[k][0]+m*v1r[k][0];
                    p1r[k][1]=p1[k][1]+m*v1r[k][1];
                    p1r[k][2]=p1[k][2]+m*v1r[k][2];
                    reflec1[k]=op.raig(reflec1[k], p1[k],p1r[k]);

                    //double pe=-op.pe(normal[k],v0[k])/op.mod(normal[k])/op.mod(v0[k]);//;, pc)op.theta(normal[k],v0[k]);//op.angle90(normal[k], v0[k]);
                    anglein[k]=op.angle90(normal[k], v0[k]);//Math.acos(pe);
                    // SI L'ANGLE ÉS INFERIOR A L'ANGLE LIMIT, TRAÇAR
                    if (anglein[k]<=anglelimit){             
                        //RAIG REFRACTAT I SEGON PUNT D'INTERSECCIÓ:
                        v1[k]=refrac2d(v0[k],normal[k],par.n1,par.n2);//direcció del raig refractat
                        //p2[k]=op.intersec(p1[k], v1[k], pc, par.R/par.scale);

                        m=2.*(-op.pe(normal[k], v1[k]))*par.R/par.scale;//R=1
                        p2[k][0]=p1[k][0]+m*v1[k][0];
                        p2[k][1]=p1[k][1]+m*v1[k][1];
                        p2[k][2]=p1[k][2]+m*v1[k][2];
                        refrac1[k]=op.raig(refrac1[k],p1[k],p2[k]); //p1 i p2 s�n correctes. 

                        //SEGON RAIG REFRACTAT:
                        normal2[k][0]=p2[k][0]-pc[0];
                        normal2[k][1]=p2[k][1]-pc[1];
                        normal2[k][2]=p2[k][2]-pc[2];
                        normal2[k]=op.norm3d(normal2[k]);
                        v2[k]=refrac2d(v1[k],normal2[k],par.n2,par.n1);
                        m=par.field[par.regim]*10.;
                        p3[k][0]=p2[k][0]+m*v2[k][0];
                        p3[k][1]=p2[k][1]+m*v2[k][1];
                        p3[k][2]=p2[k][2]+m*v2[k][2];
                        refrac2[k]=op.raig(refrac2[k],p2[k],p3[k]); //p1 i p2 s�n correctes. 
                        angleout[k]=op.ppangle(p2[k],p3[k]);
                        //panell.addDrawable(reflec1[k]);
                         //System.out.println("raig "+k+" angle incidencia="+anglein[k]*180./Math.PI+" angle out "+angleout[k]*180/Math.PI+" Tp=");
                    }else{
                        refrac1[k].edgeColor=transparent;
                        refrac2[k].edgeColor=transparent;
                        //panell.removeDrawable(refrac1[k]);
                        //panell.removeDrawable(refrac2[k]);
                        //panell.replaceDrawable(bola, bola);
                        //refrac2[k].setXY(p1[k][1], p1[k][2]);
                        //refrac2[k].setHeight(0.);
                        anglein[k]=op.angle90(p0[k],p1[k]);
                        angleout[k]=anglein[k];//op.ppangle(p0[k],p1r[k]);
                        //System.out.println("Raig "+k+" no es refracta. Supera angle limit. Angle incidència ="+anglein[k]*180./Math.PI+" v0="+v0[k][0]+" v1="+v0[k][1]+" v2="+v0[k][2]);

                        /**v1[k]=reflec2d(v0[k],normal[k]);
                        m=par.field*10.;
                        p2[k][0]=p1[k][0]+m*v1[k][0];
                        p2[k][1]=p1[k][1]+m*v1[k][1];
                        p2[k][2]=p1[k][2]+m*v1[k][2];
                        refrac1[k]=op.raig(refrac1[k],p1[k],p2[k]);
                        //refrac1[k].setXY(p1[k][1], p1[k][2]);
                        //refrac1[k].setHeight(0.);**/
                    }            
                } else { //si no talla la bola, no es mostren els rajos refractats
                    refrac1[k].edgeColor=transparent;
                    refrac2[k].edgeColor=transparent;
                    if (par.raymode==1){
                        feix[k].edgeColor=transparent;//prova
                    }
                    //panell.removeDrawable(refrac1[k]);
                    //panell.removeDrawable(refrac2[k]);
                    //refrac2[k].setXY(p1[k][1], p1[k][2]);
                    //refrac2[k].setHeight(0.);
                    angleout[k]=op.ppangle(p0[k], p1[k]) ;
                    //System.out.println("Raig "+k+" no talla");    
                        //refrac1[k].setXY(p1[k][1], p1[k][2]);
                        //refrac1[k].setHeight(0.);
                }                
                //punt0[k].setXY(p0[k][1], p0[k][2]);
                //punt1[k].setXY(p1[k][1], p1[k][2]);
                //punt2[k].setXY(p2[k][1], p2[k][2]);            
            }//fi for per cada raig
       }else{//RAYLEIGH
           intensitymap=gb.IntensityMapYZ(par);
       }
       //if (par.showforce) {panell.addDrawable(force);} else {panell.removeDrawable(force);}        

       //DIBUIXAR VECTORS DE FORÇA
       for (int i=0; i<3; i++){
           if(par.selectedforces[i]){
   /**            double fnorm=par.fmax[par.regim]*panell.getXMax()/10.;
               if(par.regim==0){
                   fnorm=fnorm/5.;
               }else{
                   fnorm=fnorm/100.;
               }**/
               double fmod=op.mod(F[i]);
               if (fmod>0.){
                    force[i].setXlength(F[i][1]*1.E12/par.fnorm[par.regim]);//op.mod(F[i])*arrowsize);//*1.E12/Fnorm);
                    force[i].setYlength(F[i][2]*1.E12/par.fnorm[par.regim]);//op.mod(F[i])*arrowsize);//*1.E12/Fnorm);
               }else{
                    force[i].setXlength(0.);//setXlength(0.);//*1.E12/Fnorm);
                    force[i].setYlength(0.);//*1.E12/Fnorm);                   
               }
               force[i].setXY(bola.getX(),bola.getY());
               //panell.removeDrawable(force[i]);
               panell.addDrawable(force[i]);
           }else{
               panell.removeDrawable(force[i]);
           }
       }

       
       String fmt = "0.00";
       DecimalFormat df = new DecimalFormat(fmt);
       double[][] Fm = new double[3][3];
       //Fm=forces.measuredforce(par,0.0, pc[1]*par.scale, pc[2]*par.scale, angleout);
       //panell.setMessage("F(total) = "+df.format(Ft)+" pN ");// Fm="+df.format(op.mod(Fm[0])*1.E12));
       panell.repaint();
    }
    /**public void step(Parametres par,Raigs raigs){
        double y0,y1,z0,z1;
        double h=par.dt;
        y0=bola.getX()*par.scale;
        z0=bola.getY()*par.scale;
        double[][] F = forces.calc(par,raigs, 0.0, y0,z0);
        y1=y0+h*F[0][1]/par.gamma+par.sigma*r.nextGaussian();
        z1=z0+h*F[0][2]/par.gamma+par.sigma*r.nextGaussian();
        bola.setX(y1/par.scale);
        bola.setY(z1/par.scale);
        //System.out.println("Step!: force="+r.nextGaussian()+"x="+y1+"y="+z1);
        
    }**/
    public double[] getPosition(){
        double[] rc = new double[3];
        rc[0] = 0.;//sempre que comencem la simulació, se situa la bola a x=0
        rc[1] = bola.getX();
        rc[2] = bola.getY();
        return rc;  
    }
    /**public void removerays(InteractivePanel panell,Parametres par){
        for(int k=0;k<Nraigdmax;k++){
        panell.removeDrawable(feix[k]);
        panell.removeDrawable(refrac1[k]);
        panell.removeDrawable(refrac2[k]);
        panell.removeDrawable(reflec1[k]);
        }       
        
    }**/
    public BoundedShape qqr(double[] pa, double[] pb){ // de dos punts dona un raig
        BoundedShape ray;
        double x0=pa[1];
        double y0=pa[2];
        double x1=pb[1];
        double y1=pb[2];
        
        double r = Math.sqrt(Math.pow(x1-x0,2)+Math.pow(y1-y0,2));
        double angle = Math.atan2(y1-y0, x1-x0);
        ray = BoundedShape.createBoundedRectangle(x0+(x1-x0)/2,y0+(y1-y0)/2,r,0);
        
        ray.setTheta(angle);
        return ray;
    }

    //RAIG REFRACTAT en un canvi de medi n1-->n2, amb raig incident "vinc", i normal "norm"
    public double[] refrac2d ( double[] vinc, double[] norm, double n1, double n2){
        double thetanorm=Math.atan2(norm[2], norm[1]); //angle d'orientació del vector normal respecte l'eix x (valor entre -PI o PI)
        double thetainc=Math.atan2(vinc[2],vinc[1]);
        /**System.out.println("Angle normal "+thetanorm*180/Math.PI+"  Angle raig incident: "+thetainc*180/Math.PI);
        double i=op.angle90(vinc, norm);//ens dona l'angle d'incidència (entre 0º i 90º), en valor absolut
        System.out.println("incident "+i*180/Math.PI);
        double r=Math.asin(Math.sin(i)*n1/n2);//angle de refracció
        System.out.println("refractat "+r*180/Math.PI); //CORRECTE
        System.out.println("vector incident: Vix="+vinc[1]+" Viy="+vinc[2]);
        System.out.println("normal: nx="+norm[1]+" ny="+norm[2]);
        
        double threfrac;
        thetanorm=thetanorm+Math.PI;
        if(thetainc>-thetanorm){
            threfrac=thetanorm-r;
        }else{
            threfrac=thetanorm+r;
        }**/
        double isign=thetainc-thetanorm;
        double rsign=Math.asin(Math.sin(isign)*n1/n2);
        double threfracsign=thetanorm-rsign;
        double v1x = Math.cos(threfracsign);
        double v1y = Math.sin(threfracsign);
        
        // METODE 1
        /**double[] v0_xy=new double[2];
        double[] v0_pn= new double[2];
        double[] v1_pn = new double[2];
        double[] v1_xy=new double[2];
        v0_xy[0]=vinc[1]; //vector incident en el sistema XY
        v0_xy[1]=vinc[2];
        v0_pn=op.rotar(v0_xy, thetanorm);//ara estem en el sistema de coordenades on el pla de la frontera entre medis és horitzontal (eix p) i la normal és l'eix vertical (n)
        v1_pn[0]=Math.signum(v0_pn[0])*Math.sin(r);
        v1_pn[1]=Math.cos(r);
        v1_xy=op.rotar(v1_pn, -thetanorm);
        double v1x=-v1_xy[0];
        double v1y=-v1_xy[1];**/
        //METODE 2
        //Equació de segon grau que surt quan imposem que v1 sigui normalitzat i formi un angle de "r" amb la normal canviada de signe (condicions imposades en 2d)
        /**double a=1.;
        double b=-2.*Math.cos(r)*norm[1];
        double c=-Math.pow(norm[2],2)+Math.pow(Math.cos(r),2);
        double[] sol = op.ec2(a, b, c);
        double v1x=sol[0];
        double v1y=Math.sqrt(1.-v1x*v1x);**/
        
        //METODE 3
        /**double B= Math.sin(i-r)/Math.sin(i);
        double A= Math.sin(r)/Math.sin(i);
        double v1x = A*vinc[1]+B*norm[1];
        double v1y = A*vinc[2]+B*norm[2];
         System.out.println("A="+A+"B="+B);**/
       // System.out.println("Vector refractat: v1x="+v1x+"  v1y="+v1y);
        double[] vrefrac = new double[3];//Vector refractat
        vrefrac[0]=0.;
        vrefrac[1]=v1x;//v1_xy[0];
        vrefrac[2]=v1y;//v1_xy[1];
        //vrefrac[3]=r;//necessitem l'angle de refracció...
        //System.out.println(Math.atan2(v0_xy[0], v0_xy[1])*180/Math.PI+" "+Math.atan2(v0_pn[0], v0_pn[1])*180/Math.PI);
        return vrefrac;

    }
    
    // RAIG REFLECTIT
    public double[] reflec2d(double[] vinc, double[] norm){
        /**double thetanorm=Math.atan2(norm[2], norm[1]); //angle girat per la normal respecte l'eix x
        double[] v0_xy=new double[2];
        double[] v0_pn= new double[2];
        double[] v1_pn = new double[2];
        double[] v1_xy=new double[2];
        v0_xy[0]=vinc[1]; //vector incident en el sistema XY
        v0_xy[1]=vinc[2];
        v0_pn=op.rotar(v0_xy, -thetanorm);//ara estem en el sistema de coordenades on el pla de la frontera entre medis és horitzontal (eix p) i la normal és l'eix vertical (n)
        v1_pn[0]=v0_pn[0];
        v1_pn[1]=-v0_pn[1];
        v1_xy=op.rotar(v1_pn, thetanorm);
        
        double[] vreflec = new double[3];//Vector refractat
        vreflec[0]=0.;
        vreflec[1]=v1_xy[0];
        vreflec[2]=v1_xy[1];**/
        double i = op.angle90(vinc, norm);
        double[] vreflec = new double[3];
        vreflec[0]=0.;
        vreflec[1]=vinc[1]+2.*Math.cos(i)*norm[1];
        vreflec[2]=vinc[2]+2.*Math.cos(i)*norm[2];        
        return vreflec;
    }
/**    
    public Arrow fletxa(double[] punt, double[] vec, Arrow fletxa){
        //dibuixar una fletxa, segons el vector
        vec=op.norm3d(vec);
        fletxa.setXY(punt[1],punt[2]);
        fletxa.setXlength(vec[1]);
        fletxa.setYlength(vec[2]);
        return fletxa;
    }**/
/**
    public void mostrarperfil(InteractivePanel panell,Parametres par)   {
       switch (par.perfil){
           case 0: //nothing
               panell.removeDrawable(perfily);
               panell.removeDrawable(perfilz);
               break;
           case 1: //y profile
               double[] py0 = new double[]{0.0,-par.field[par.regim]/2.,bola.getY()};
               double[] py1 = new double[]{0.0,par.field[par.regim]/2.,bola.getY()};
               perfily=op.raig(perfily, py0,py1);
               panell.addDrawable(perfily);
               panell.removeDrawable(perfilz);
               break;
           case 2: //z profile
               double[] pz0 = new double[]{0.0,bola.getX(),-par.field[par.regim]/2.};
               double[] pz1 = new double[]{0.0,bola.getX(),par.field[par.regim]/2.};
               perfilz=op.raig(perfilz, pz0,pz1);
               panell.addDrawable(perfilz);
               panell.removeDrawable(perfily);
               break;
       }
       panell.repaint();
    }**/
    
/**    public void showsetup(Parametres par, InteractivePanel panell){
        double xmax=par.f/par.scale, ymax=xmax;
        panell.setPreferredMinMaxX(-xmax, xmax);
        panell.setPreferredMinMaxY(-ymax, ymax);
        
        lent=BoundedShape.createBoundedEllipse(0,-par.f/par.R,2*par.Rpe/par.R,0.1*par.Rpe/par.R); //x0,y0,a,b
        panell.addDrawable(lent);
        cover=BoundedShape.createBoundedRectangle(0, 1, 2, 3);
        panell.repaint();
    }**/

}

