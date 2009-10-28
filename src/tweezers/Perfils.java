package tweezers;
import org.opensourcephysics.frames.*;
import org.opensourcephysics.display.*;

public class Perfils {
    Captions cap = new Captions();
    Operacions op=new Operacions();
    Forces forces=new Forces();
    GaussianBeam gb = new GaussianBeam();
    java.awt.Color orangeplus = new java.awt.Color (255,150,0);    
    //Raigs raigs = new Raigs();
    double[][] QY;
    double QYtot, QYs, QYg;
    double[][] QZ;
    double QZtot, QZs, QZg;
    double zleft=0., zright=0., Qleft=0., Qright=0.;
    double[] F,z;
    Dataset[] datasetZ = {new Dataset(),new Dataset(), new Dataset()};
    Dataset[] datasetY = {new Dataset(),new Dataset(), new Dataset()};
    Dataset calibration = new Dataset();
    double ymax;
    double fmaxy=0.,fminy=0.;
    public void perfilY(PlottingPanel panel, Parametres par, double zc) {
        for (int i=0;i<3;i++){
            datasetZ[i]=new Dataset();
            datasetY[i]=new Dataset();
        }
        par.Nraig=par.Nraigperfils;
        Raigs raigs = new Raigs(par);
        ymax= par.fieldforces[par.regim]/2.;//R;//2.*par.R*par.NA/par.n1;
        double discrY=2.*ymax/(double)par.Npointsperfils;
        double ztrap=par.ztrap;
            
        Dataset dataset = new Dataset();
        dataset.setConnected(true);
        dataset.setSorted(true);
        fmaxy=0.;
        fminy=0.;
        double QYantic;
        for (double yc=-ymax;yc<=ymax;yc=yc+discrY){
            QY=forces.calc(par,raigs,0.,yc,ztrap);
            QYtot=QY[0][1];
            QYs=QY[1][1];
            QYg=QY[2][1];
            double[] Qcase= new double[3];
            Qcase[0]=QYtot;
            Qcase[1]=QYs;
            Qcase[2]=QYg;
            if (Qcase[0]>fmaxy){
                fmaxy=Qcase[0];
            }
            if (Qcase[0]<fminy){
                fminy=Qcase[0];
            }
            for (int i=0; i<3; i++){
                datasetY[i].append(yc/par.scale,Qcase[i]*1.E12);
            }
        }
        par.Qxmax=fmaxy*1.E12;
//        double[] pleft =datasetY[0].getPoints()[(int)par.Npointsperfils/2-2];
//        double[] pright = datasetY[0].getPoints()[(int)par.Npointsperfils/2+2];
        //par.kx=(pleft[1]-pright[1])/(pleft[0]-pright[0]);
        double xleft=-par.dx4kx[par.regim];
        double xright=par.dx4kx[par.regim];
        double Fyleft=forces.calc(par, raigs, 0., xleft, ztrap)[0][1];
        double Fyright=forces.calc(par, raigs, 0., xright, ztrap)[0][1];
        par.kx=(Fyleft-Fyright)/(xleft-xright);

        for (int i=0;i<3;i++){
            datasetY[i].setConnected(true);
            datasetY[i].setSorted(true);
            datasetY[i].setMarkerShape(Dataset.PIXEL);
        }
        datasetY[0].setLineColor(orangeplus);
        datasetY[1].setLineColor(java.awt.Color.RED);
        datasetY[2].setLineColor(java.awt.Color.BLUE);
    //panel.setXLabel(cap.xcurvexlabel[par.lang]);
    //panel.setYLabel("force(pN)");
    
    /**panel.clear();
    dataset.setMarkerShape(Dataset.PIXEL);//CIRCLE);
    panel.addDrawable(dataset);
    panel.repaint();
    panel.setXLabel("x(m)");
    panel.setYLabel("force(pN)");**/
    par.Nraig=par.Nraigforce;
}

public void perfilZ(PlottingPanel panell, Parametres par,double yc){
    panell.clear();
    par.Nraig=par.Nraigperfils;
    par.correction3D=true;
    Raigs raigs=new Raigs(par);
    par.correction3D=false;

    double zmax=par.fieldforces[par.regim]/2.;//2.*par.R*Math.sqrt(1.-Math.pow(par.NA/par.n1,2));
    double discr=par.fieldforces[par.regim]/(double)(par.Npointsperfils-1);
    double[][] Fy= new double[3][par.Npointsperfils];
    int k=0;
    double Qzantic=0.;
    double zcprevious=0.;
    par.ztrap=-1;
    int countztraps=0;
    par.ztrapfound=false;

    double kz;//per kz
    int count=0;//per kz
    int indexzeq=0;//per kz
    double[] F = new double[par.Npointsperfils];//per kz
    double[] z = new double [par.Npointsperfils];//per kz
    double[] qz = new double [par.Npointsperfils];
    for (double zc=-zmax;zc<=zmax;zc=zc+discr){
        QZ=forces.calc(par,raigs,0.,yc,zc);
        QZtot=QZ[0][2];
        QZs=QZ[1][2];
        QZg=QZ[2][2];
        F[count]=QZtot;//per kz
        z[count]=zc;//per kz
        qz[count]=QZtot*1.E12;
        count++;
        if (!par.ztrapfound && (Qzantic*QZ[0][2])<0 && zc>0.){
            Qleft=Qzantic;
            Qright=QZ[0][2];
            zleft=zcprevious;
            zright=zc;
            par.ztrap=-Qleft*((zright-zleft)/(Qright-Qleft))+zleft;
            par.ztrapfound=true;
            indexzeq=count; //per kz
            par.kz=(F[indexzeq-1]-F[indexzeq+1])/(z[indexzeq-1]-z[indexzeq+1]);

            //Refinar ztrap i kz
            double Fleftfi,Frightfi,zleftfi,zrightfi,Qzfi,Qzfiantic=1,zfineantic=zleft;
            boolean zfinefound=false;
            for(double zfine=zleft;zfine<=zright;zfine=zfine+(zright-zleft)/(double)par.Npointsrefinats){
                Qzfi=forces.calc(par,raigs,0.,yc,zfine)[0][2];
                if(!zfinefound && (Qzfiantic*Qzfi)<0. && zfine>0.){
                    zfinefound=true;
                    Fleftfi=Qzfiantic;
                    Frightfi=Qzfi;
                    zleftfi=zfineantic;
                    zrightfi=zfine;
                    //System.out.println(par.kz+" "+par.ztrap);//zleft+" "+zright+" "+Qleft+" "+Qright+" "+
                    par.ztrap=-Fleftfi*((zrightfi-zleftfi)/(Frightfi-Fleftfi))+zleftfi;
                    par.kz=(Frightfi-Fleftfi)/(zrightfi-zleftfi);
                    //System.out.println(par.kz+" "+par.ztrap);//zleftfi+" "+zrightfi+" "+Fleftfi+" "+Frightfi+" "+
                    //System.out.println("-----------");
                }
                zfineantic=zfine;
                Qzfiantic=Qzfi;
            }

            //System.out.println(indexzeq);
//            System.out.println("Trap candidates: "+zc+" Qzantic="+Qzantic+" QZactual="+QZ[0][2]);
        }
        Qzantic=QZ[0][2];
        zcprevious=zc;
        Fy[0][k]=QZtot;
        Fy[1][k]=QZs;
        Fy[2][k]=QZg;

        for (int i=0; i<3; i++){
            datasetZ[i].append(zc/par.scale,Fy[i][k]*1.E12);
        }
        k++;

    }
    if (!par.ztrapfound){
        par.ztrap=0.;
    }else{
        par.Qzmaxdown=op.max(qz);
        par.Qzmaxup=op.min(qz);
    }


    int Npoints=k;
    for (int i=0;i<3;i++){
        datasetZ[i].setConnected(true);
        datasetZ[i].setSorted(true);
        datasetZ[i].setMarkerShape(Dataset.PIXEL);
    }
    datasetZ[0].setLineColor(orangeplus);
    datasetZ[1].setLineColor(java.awt.Color.RED);
    datasetZ[2].setLineColor(java.awt.Color.BLUE);
    //panell.setXLabel(cap.zcurvexlabel[par.lang]);
    //panell.setYLabel("force(pN)");
    par.Nraig=par.Nraigforce;
}
public void calibrate (PlottingPanel panellY, Parametres par){
            calibration = new Dataset();
            double xleft = fmaxy/(-par.stiffness)/par.scale;
            double xright= fminy/(-par.stiffness)/par.scale;

            calibration.append(xleft,fmaxy*1.E12);
            calibration.append(xright, fminy*1.E12);
            
            calibration.setConnected(true);
            calibration.setMarkerShape(calibration.PIXEL);
            calibration.setLineColor(new java.awt.Color (0,200,0));
            calibration.setMarkerColor(java.awt.Color.GREEN); 
            par.calibrated=true;
}
public void representar(PlottingPanel panellZ, PlottingPanel panellY, Parametres par){
    panellZ.clear();
    panellY.clear();
    for(int i=2; i>=0;i--){
        if (par.selectedforces[i]){
            panellZ.addDrawable(datasetZ[i]);
            panellY.addDrawable(datasetY[i]);
        }
        if(par.calibrated){
            calibrate(panellY,par);
            panellY.addDrawable(calibration);
        }
    }
    panellZ.repaint();
    panellY.repaint();
}

            /**for (int k=0;k<par.Nraigd;k++){
                th1[k]=raigs.angleout[k];
                th2[k] = Math.asin(n1*Math.sin(th1[k])/n2);
                tp[k]=2.*Math.sin(th1[k])*Math.cos(th2[k])/(Math.sin(th1[k]+th2[k])*Math.cos(th1[k]-th2[k]));
                ts[k]=2.*Math.sin(th1[k])*Math.cos(th2[k])/Math.sin(th1[k]+th2[k]);
                Tp[k]=tp[k]*tp[k]*n2*Math.cos(th2[k])/(n1*Math.cos(th1[k]));//
                Ts[k]=ts[k]*ts[k]*n2*Math.cos(th2[k])/(n1*Math.cos(th1[k]));
                raigs.p[k]=raigs.p[k]*Tp[k];

                double theta=raigs.angleout[k];
                double thetap=Math.asin(n1*Math.sin(theta)/n2);//angle del raig refractat
                Tp[k]=Math.pow(2*Math.sin(thetap)*Math.cos(theta)*2*Math.sin(theta)*Math.cos(thetap),2)/Math.pow(Math.sin(thetap+theta)*Math.cos(thetap-theta),4);
                Ts[k]=Math.pow(2*Math.sin(thetap)*Math.cos(theta)*2*Math.sin(theta)*Math.cos(thetap),2)/Math.pow(Math.sin(theta+thetap),4);
                int h=1;
                System.out.println("k="+k+objectesYZ.talla[k] +" angle="+theta*180/Math.PI+"Tp="+Tp[k]+" Ts="+Ts[k]);
                raigs.p[k]=raigs.power[k]*(par.fp*Math.pow(Tp[k],h)+par.fs*Math.pow(Ts[k],h));            
            }
            F=forces.calc(par,raigs,0.,yc,ztrap)**/
        //PERFIL Z
/**        double zmax=2.*par.fieldforces/2.;//2.*par.R*Math.sqrt(1.-Math.pow(par.NA/par.n1,2));
        double y_z=0.;
        double discrz=zmax/par.Npointsperfils;
        for (double zc=-zmax;zc<zmax;zc=zc+discrz){
            objectesYZ.bola.setXY(y_z/par.scale, zc/par.scale);
            actualitzar();//objectesYZ.actualitzar(panellYZ, par, raigs);
            for (int k=0;k<par.Nraigd;k++){
                double theta=raigs.angleout[k];
                double thetap=Math.asin(n1*Math.sin(theta)/n2);//angle del raig refractat
                Tp[k]=Math.pow(2*Math.sin(thetap)*Math.cos(theta)*2*Math.sin(theta)*Math.cos(thetap),2)/Math.pow(Math.sin(thetap+theta)*Math.cos(thetap-theta),4);
                Ts[k]=Math.pow(2*Math.sin(thetap)*Math.cos(theta)*2*Math.sin(theta)*Math.cos(thetap),2)/Math.pow(Math.sin(theta+thetap),4);
                int h=1;
                System.out.println("k="+k+"x="+objectesYZ.bola.getX()+" z="+objectesYZ.bola.getY()+" angle="+theta*180/Math.PI+"Tp="+Tp[k]+" Ts="+Ts[k]);
                raigs.p[k]=raigs.p[k]*(par.fp*Math.pow(Tp[k],h)+par.fs*Math.pow(Ts[k],h));            
            }
            F=forces.calc(par,raigs,0.,y_z,zc);
            fz=F[0][2];
            datasetmfZ.append(zc,fz*1.E12);            
        }
**/

/**
public void perfilFr(PlottingPanel panel, Parametres par){

    //RAIGS:
    par.numraig=0;//1 sol raig
    double Rpe0=par.Rpe;
    int N=1000;
    double discr=2*Rpe0/(double)(N-1);
    double[][] Q;
    double Qi;
    Raigs raigs;
    Dataset dataset = new Dataset();
    Dataset disc = new Dataset();
    dataset.setConnected(false);
    dataset.setSorted(true);
    int n=0;
    double Qsum=0, Qant=0;
    for (double r=-Rpe0;r<=Rpe0;r=r+discr){
        n=n+1;
        par.Rpe=r;
        raigs = new Raigs(par);
        raigs.Nmax=1;
        Q=forces.calc(par,raigs,0,0.1E-9,1E-9);
        Qi=Q[par.q][2];
        Qsum=Qsum+Qi;
        dataset.append(r,Qi);
        
        if (Math.abs(Qi-Qant)>0.01){
            Qi=0.01;
        }
        Qant=Qi;
    }

    panel.clear();
    dataset.setMarkerShape(Dataset.PIXEL);
    panel.addDrawable(dataset);
    panel.repaint();
}
**/
}



/**
 *         private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {                                         
            // TODO add your handling code here:
            perfilmesurat(Yforceprofilepanel, Zforceprofilepanel, raigs, par,panellYZ);
            jPanelForce.repaint();
        }
 */