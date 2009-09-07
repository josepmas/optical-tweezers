package tweezers;
import java.lang.Math;
public class Forces {
    public Forces() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    GaussianBeam gb= new GaussianBeam();
    public double[][] calc(Parametres par, Raigs raigs, double xc, double yc,double zc) {
        switch(par.regim){
            case 0:
                return(calcMie(par,raigs,xc,yc,zc));
            case 1:
                return(calcRayleigh(par,gb,xc,yc,zc));
            case 2:
        }
        double[][] zeros=new double[3][3];
        zeros[0]=new double[]{0.0,0.0,0.0};
        zeros[1]=new double[]{0.0,0.0,0.0};
        zeros[2]=new double[]{0.0,0.0,0.0};
        return(zeros);
    }
    
    public double[][] calcMie(Parametres par, Raigs raigs, double xc, double yc,double zc) {
    //Par�metres par=new Par�metres();
    double f=par.zcover;//par.f;
    double R=par.R;
    double n1=par.n1;
    double n2=par.n2;
    //POSICIONS
    double x0,y0,z0; //punt sobre la lent d'on ve el raig
    double x1, y1,z1; // punt del pla focal cap on va el raig
    double xf=0., yf=0., zf=0.; //focus de la lent (el prenem com a origen)

    //RAIGS:
    //Raigs raigs = new Raigs(par);
    int Nraig = par.Nraig;//raigs.Nmax; 

    double[] vr = raigs.vr; // posici� radial del punt d'incid�ncia sobre la lent (en mm)
    double[] vbeta = raigs.vbeta; //angle azimutal dels punts d'incid�ncia sobre la lent
    double[] nx = raigs.nx; // component x del vector director dels raigs (abans de la lent)
    double[] ny = raigs.ny; // component y     "
    double[] nz = raigs.nz; // component z     "
    double[] p = raigs.p; //fracci� de pot�ncia associada a cada raig del feix

    double fi, alpha, betap, gammap, d, theta, thetap, mu; //variables que depenen del raig i de la bola

    //FORCES
    double[][] Q = new double[3+Nraig][3];// [Qtot Qs Qg] [ x y z]
    double Qsp, Qss, Qgp, Qgs, Qs, Qg,Qtot;
    double F0,Fs,Fg,F,angle;
    double Qx=0,Qy=0,Qz=0,Qsx=0,Qsy=0,Qsz=0,Qgx=0,Qgy=0,Qgz=0;
    int k; //identificador del raig:
    
    //INICIALITZACI� DE LA MATRIU DE SORTIDA
    for (int i=0;i<3;i++){ // per les components de la for�a
        for (int j=0;j<3;j++){
            Q[i][j]=0;  
        }
    }
//    for (k=0;k<Nraig;k++){
//        Q[k+3][0]=-raigs.x0[k]/par.scale;
//        Q[k+3][1]=-raigs.y0[k]/par.scale;
//        Q[k+3][2]=par.f/par.scale;
//    }

    //xc=xc*R; // Posici� de la bola (en mm, ja que al dibuix es mesura en  "radis de bola")
    //yc=yc*R;
    //zc=zc*R;
    
    for (k=0; k<Nraig;k++){
        double r=vr[k];
        double beta=vbeta[k];
        x0=raigs.x0[k];
        y0=raigs.y0[k];
        z0=-f;
        //r=Math.sqrt(x0*x0+y0*y0); //Calcula r i beta a partir dels x0, que �s el que tindrem en realitat
        //beta = Math.atan2(z0,y0); // d�na angle en el quadrant adequat a partir de (y,x)

        x1=xf+nx[k]/nz[k]*f; //Punt del pla focal cap on es dirigeix el raig (considerant inclinaci� del raig incident)
        y1=yf+ny[k]/nz[k]*f;
        z1=zf;


        //-----------------Punt de tall raig-bola (xb,yb,zb)----------------------------

        fi=Math.atan(r/f);
        alpha=Math.PI/2-fi;
        //C�lcul angle d'incid�ncia
        betap=Math.atan(yc*Math.sin(beta)/(yc*Math.cos(beta)+zc/Math.tan(alpha))); //-->>> ALERTA QUE ALGUN COP EL DENOMINADOR �S ZERO!!!
        gammap=Math.acos(Math.cos(alpha)*Math.cos(betap));
        d=zc*Math.cos(betap)/Math.tan(alpha)+yc*Math.cos(beta-betap);
        //System.out.println(fi+" "+alpha+" "+betap+" "+gammap+" "+d+" ");
                
        // PUNT DE TALL (xb,yb,zb), FOCUS (x1,y1,z1), CENTRE DE LA BOLA (xc,yc,zc)
        // xb=x0+m*(x1-x0)
        // yb=y0+m(y1-y0)
        // zb=z0+m(z1-z0)
                
        //Imposant que |xb-xc|=R trobem: A*m^2+B*m+c=0
        double A,B,C,m;
        A=Math.pow(x1-x0,2)+Math.pow(y1-y0,2)+Math.pow(z1-z0,2);
        B=2*((x1-x0)*(x0-xc)+(y1-y0)*(y0-yc)+(z1-z0)*(z0-zc)); // Ha de ser positiu!!
        C=Math.pow(x0-xc,2)+Math.pow(y0-yc,2)+Math.pow(z0-zc,2)-R*R;


        if ((B*B-4*A*C)>=0) {//Si el discriminant fos negatiu no t� soluci�, el raig no toca la bola.
            //ens quedem amb la soluci� "-" perqu� el primer contacte raig-bola �s prop de la lent: (z1-z0) petit.
 
            m=(-B-Math.sqrt(B*B-4*A*C))/(2*A); //m ha de ser positiu
            Q[k+3][0]=(x0+m*(x1-x0))/par.scale; // Posicions del punt d'intersecci� raig-bola per aquest raig k
            Q[k+3][1]=(y0+m*(y1-y0))/par.scale; // en unitats de radis
            Q[k+3][2]=(z0+m*(z1-z0))/par.scale;

            //----------------Angle d'incid�ncia i coefs. Fresnel------------------------
            //theta2=asin((sz*sin(fi)+sy*Math.cos(fi))/R)
            theta=Math.asin(d*Math.sin(gammap)/R);
            mu=0.0;//Math.acos(Math.tan(alpha)/Math.tan(gammap));//Angle entre el pla d'incid�ncia i el pla vertical

            if (theta<par.anglelimit){            
                thetap=Math.asin(n1*Math.sin(theta)/n2);//angle del raig refractat
                //System.out.println("n1="+n1+" n2="+n2+" anglimt="+par.anglelimit+" theta="+theta+" thetap"+thetap);
                
                //Coeficients de transmissi� i reflexi� en cada component de polaritzaci� (paral�lela o perpendicular)
                double Tp2, Ts2, Rp, Rs; //coeficients de fresnel
                Tp2=Math.pow(2*Math.sin(thetap)*Math.cos(theta)*2*Math.sin(theta)*Math.cos(thetap),2)/Math.pow(Math.sin(thetap+theta)*Math.cos(thetap-theta),4);
                Ts2=Math.pow(2*Math.sin(thetap)*Math.cos(theta)*2*Math.sin(theta)*Math.cos(thetap),2)/Math.pow(Math.sin(theta+thetap),4);
                Rp=Math.pow((Math.tan(thetap-theta)/Math.tan(thetap+theta)),2);
                Rs=Math.pow((Math.sin(thetap-theta)/Math.sin(thetap+theta)),2);
                
                //fracci� de la pot�ncia incident en cada direcci� de polaritzaci�.
        //	En polaritzaci� circular, invertim la meitat de la pot�ncia en cada component (paral. i perp.)
                double fp=par.fp;
                double fs=par.fs;

                //C�lcul de forces de scattering i gradient:
                Qsp=1+Rp*Math.cos(2*theta)-Tp2*(Math.cos(2*theta-2*thetap)+Rp*Math.cos(2*theta))/(1+Rp*Rp+2*Rp*Math.cos(2*thetap));
                Qss=1+Rs*Math.cos(2*theta)-Ts2*(Math.cos(2*theta-2*thetap)+Rs*Math.cos(2*theta))/(1+Rs*Rs+2*Rs*Math.cos(2*thetap));
                Qgp=Rp*Math.sin(2*theta)-Tp2*(Math.sin(2*theta-2*thetap)+Rp*Math.sin(2*theta))/(1+Rp*Rp+2*Rp*Math.cos(2*thetap));
                Qgs=Rs*Math.sin(2*theta)-Ts2*(Math.sin(2*theta-2*thetap)+Rs*Math.sin(2*theta))/(1+Rs*Rs+2*Rs*Math.cos(2*thetap));
//                (System.out.println("Qsp="+Qsp+"Qss"+Qss+"Qgp"+Qgp))
                Qs=fp*Qsp+fs*Qss;
                Qg=fp*Qgp+fs*Qgs;                
                
                    Qsx=-Qs*Math.cos(alpha)*Math.sin(beta);
                    Qsy=-Qs*Math.cos(alpha)*Math.cos(beta);
                    Qsz=Qs*Math.sin(alpha);
                    Qgx=Qg*(Math.cos(mu)*Math.sin(alpha)*Math.sin(beta)-Math.sin(mu)*Math.cos(beta));
                    Qgy=Qg*(Math.cos(mu)*Math.sin(alpha)*Math.cos(beta)+Math.sin(mu)*Math.sin(beta));
                    Qgz=Qg*Math.cos(mu)*Math.cos(alpha);
                //System.out.println("theta="+theta*180/Math.PI+"thetap"+thetap+" mu="+mu+" alpha="+alpha+" beta="+beta+" Qgx="+Qgx+"Qgy="+Qgy+"Qgz="+Qgz+" Qsx="+Qsx+"Qsy="+Qsy+"Qsz="+Qsz);

                //C�lcul de la for�a total:
                //Qx=-Qs*Math.cos(alpha)*Math.sin(beta)+Qg*(Math.cos(mu)*Math.sin(alpha)*Math.sin(beta)-Math.sin(mu)*Math.cos(beta));                    Qy=-Qs*Math.cos(alpha)*Math.cos(beta)+Qg*Math.cos(mu)*Math.sin(alpha)*Math.cos(beta)+Qg*Math.sin(mu)*Math.sin(beta);
                //Qy=-Qs*Math.cos(alpha)+Qg*(Math.cos(mu)*Math.sin(alpha)*Math.cos(beta)+Math.sin(mu)*Math.sin(beta));
                //Qz=Qs*Math.sin(alpha)+Qg*Math.cos(mu)*Math.cos(alpha);
                
            }else{
                //System.out.println("Raigs "+k+" supera angle limit");
                Qsx=0.;
                Qsy=0.;
                Qsz=0.;
                Qgx=0.;
                Qgy=0.;
                Qgz=0.;
            }
        }else{
            //System.out.println("Raigs "+k+" no talla");
                Qsx=0.;
                Qsy=0.;
                Qsz=0.;
                Qgx=0.;
                Qgy=0.;
                Qgz=0.;
        }        
//fi de l'if
    Qx=Qsx+Qgx;
    Qy=Qsy+Qgy;
    Qz=Qsz+Qgz;        
if(Qx!=Qx & Qy!=Qy & Qz!=Qz){//Si algun dels tres és NaN
    raigs.dF[k]=0;
    //no sumar contribució d'aquest raig.
}else{
//Qtot
double dF=p[k]*par.n1/par.c;// força màxima associada a aquest raig (d'acord amb la seva potència)
raigs.dF[k]=dF*Math.sqrt(Qx*Qx+Qy*Qy+Qz*Qz);//prova
//System.out.println("dF["+k+"]="+dF);
Q[0][0]=Q[0][0]+Qx*dF;
Q[0][1]=Q[0][1]+Qy*dF;
Q[0][2]=Q[0][2]+Qz*dF;
//Qs
Q[1][0]=Q[1][0]+Qsx*dF;
Q[1][1]=Q[1][1]+Qsy*dF;
Q[1][2]=Q[1][2]+Qsz*dF;
//Qg
Q[2][0]=Q[2][0]+Qgx*dF;
Q[2][1]=Q[2][1]+Qgy*dF;
Q[2][2]=Q[2][2]+Qgz*dF;
//El Q[3][] �s el punt de tall amb la bola
            
}


} // fi del for que itera per a cada raig, sumant les contribucions a la for�a
//System.out.println(Math.sqrt(Math.pow(Q[0][0],2)+Math.pow(Q[0][1],2)+Math.pow(Q[0][2],2)));
    
//    System.out.println("Total force ="+ Q[2][0]*1.E12+","+Q[2][1]*1.E12+","+Q[2][2]*1.E12);
//    System.out.println("Scattering force ="+ Q[1][0]*1.E12+","+Q[1][1]*1.E12+","+Q[1][2]*1.E12);
//    System.out.println("Gradient force ="+ Q[0][0]*1.E12+","+Q[0][1]*1.E12+","+Q[0][2]*1.E12);
    return Q;
}

public double[][] calcRayleigh(Parametres par,GaussianBeam gb,double xc,double yc,double zc){
    double[][] F = new double[3][3];
    //falta una constant de proporcionalitat....
    //System.out.println("position: x="+xc+" y="+" z="+zc);
    double m=par.n2/par.n1;
    double w0=par.w0;
    double c=par.c;
    double a= par.R;
    double P=par.P;//agafa la potència total, i no només la que travessa la pupil·la (en Rayleigh no simulem pupil·la)
    double nmedi=par.n1;
    double ct=(m*m-1.)/(m*m+2);
    double kmedi=par.kmedi;
    double x=xc/w0;
    double y=yc/w0;
    double z=zc/(kmedi*w0*w0);
    double coefxy=P/(Math.PI*w0*w0)/(1.+4.*z*z)*Math.exp(-2.*(x*x+y*y)/(1.+4.*z*z));
    F[1][0]=0.;
    F[1][1]=0.;
    F[1][2]=nmedi/c*8./3.*Math.PI*Math.pow(kmedi*a,4)*Math.pow(a*ct,2)*2.*coefxy;
    //System.out.println("Rayleigh Scattering force: Fx="+F[1][0]+" Fy="+F[1][1]+" Fz="+F[1][2]);

    F[2][0]=-2.*Math.PI*nmedi*Math.pow(a,3)/c*ct*4.*x/w0/(1.+Math.pow(2.*z,2))*coefxy;
    F[2][1]=-2.*Math.PI*nmedi*Math.pow(a,3)/c*ct*4.*y/w0/(1.+Math.pow(2.*z,2))*coefxy;
    F[2][2]=-2.*Math.PI*nmedi*Math.pow(a,3)/c*ct*8.*z/(kmedi*w0*w0)/(1.+4.*z*z)*(1.-2.*(x*x+y*y)/(1+4.*z*z))*2.*coefxy;
    //System.out.println("Rayleigh Gradient Force: Fx="+F[2][0]+" Fy="+F[2][1]+" Fz="+F[2][2]);

    //double ct=2. *Math.PI*par.n1/par.c*Math.pow(par.R,3)*(m*m-1)/(m*m+2);
    F[0][0]=F[1][0]+F[2][0];
    F[0][1]=F[1][1]+F[2][1];
    F[0][2]=F[1][2]+F[2][2];
    return F;
}

public double[] molla (Parametres par, double x, double y, double z){
        double[] F = new double[3];
        F[0]=-x;
        F[1]=-y;
        F[2]=-z;
        
        return F;
        
    }
    public double[][] measuredforce(Parametres par, double x, double y, double z ,double[] angleout){
        double[] Tp = new double[par.Nraigd]        ;
        double[] Ts = new double[par.Nraigd]        ;
        Raigs raigsm = new Raigs(par);
        double T0=4.*par.n1*par.n2/Math.pow(par.n1+par.n2,2);
        for (int k=0;k<par.Nraigd;k++){
                /**th1[k]=raigs.angleout[k];
                th2[k] = Math.asin(n1*Math.sin(th1[k])/n2);
                tp[k]=2.*Math.sin(th1[k])*Math.cos(th2[k])/(Math.sin(th1[k]+th2[k])*Math.cos(th1[k]-th2[k]));
                ts[k]=2.*Math.sin(th1[k])*Math.cos(th2[k])/Math.sin(th1[k]+th2[k]);
                Tp[k]=tp[k]*tp[k]*n2*Math.cos(th2[k])/(n1*Math.cos(th1[k]));//
                Ts[k]=ts[k]*ts[k]*n2*Math.cos(th2[k])/(n1*Math.cos(th1[k]));
                raigs.p[k]=raigs.p[k]*Tp[k];**/

                double theta=angleout[k];               
                double thetap=Math.asin(par.n1*Math.sin(theta)/par.n2);//angle del raig refractat
                Tp[k]=Math.pow(2*Math.sin(thetap)*Math.cos(theta)*2*Math.sin(theta)*Math.cos(thetap),2)/Math.pow(Math.sin(thetap+theta)*Math.cos(thetap-theta),4)/T0;
                Ts[k]=Math.pow(2*Math.sin(thetap)*Math.cos(theta)*2*Math.sin(theta)*Math.cos(thetap),2)/Math.pow(Math.sin(theta+thetap),4)/T0;
                int h=1;
                //System.out.println("k="+k+" angle in="+"angle out="+theta*180/Math.PI+"Tp="+Tp[k]+" Ts="+Ts[k]);
                raigsm.p[k]=raigsm.power[k]*(par.fp*Math.pow(Tp[k],h)+par.fs*Math.pow(Ts[k],h));            
            }
       double[][] Fm = calcMie(par,raigsm,x,y,z);
       return Fm;
        
    }
    private void jbInit() throws Exception {
    }

}
