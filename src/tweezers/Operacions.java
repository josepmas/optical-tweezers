package tweezers;
import org.opensourcephysics.display.BoundedShape;
import org.opensourcephysics.numerics.*;
import java.util.*;
/**
 * <p>Title: Simulaci� Optical Tweezers</p>
 *
 * <p>Description: Applet de simulaci� de trampes �ptiques</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author Josep Mas
 * @version 1.0
 */

public class Operacions {

    
    public double mod(double[] F){    //MODUL
        double modF=Math.sqrt(F[0]*F[0]+F[1]*F[1]+F[2]*F[2]);
        return modF;
}
    //NORMALITZA UN VECTOR
    public double[] norm(double[] v){
        double modul=mod(v);
        double[] vnorm=new double[2];
        vnorm[0]=v[0]/modul;
        vnorm[1]=v[1]/modul;
        vnorm[2]=v[2]/modul;
        return vnorm;
}
    public double[] norm3d(double[] v){
    double modul=Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
    double[] vnorm=new double[3];
    vnorm[0]=v[0]/modul;
    vnorm[1]=v[1]/modul;
    vnorm[2]=v[2]/modul;
    return vnorm;
}


    //PRODUCTE ESCALAR
    public double pe(double[] v, double[] w){ //producte escalar de dos vectors
        double pe=v[0]*w[0]+v[1]*w[1]+v[2]*w[2];
        return pe;
    }

    //Angle entre vectors
    public double theta (double[] v, double[] w){
        double cos=pe(v,w)/(mod(v)*mod(w));
        return Math.acos(cos);
    }
    public double angle90 (double[] v, double[] w){
        double cos=pe(v,w)/(mod(v)*mod(w));
        if (cos<0){cos=-cos;}
        if (cos>1){cos=1.;}
        double acos=Math.acos(cos);
        return acos;
    }

    //Rotaci� d'angle theta d'un vector en el pla XY.
    public double[] rotar (double[] v, double th){
        double [] vr= new double[2];
        vr[0]=v[0]*Math.cos(th)-v[1]*Math.sin(th);
        vr[1]=v[0]*Math.sin(th)+v[1]*Math.cos(th);
        return vr;
    }

    //EQUACIO 2ON GRAU
    public double[] ec2 (double a, double b, double c){
        double[] x= new double[2];
        x[0]=(-b+Math.sqrt(b*b-4.*a*c))/(2.*a);
        x[1]=(-b-Math.sqrt(b*b-4.*a*c))/(2.*a);
        return x;
    }

//  FEIX GAUSSI� TEM00
    double[] gf(double x, double y, double z, Parametres par){ //retorna el vector perpendicular al front d'ona en el punt x,y,z
     double[] grad = new double[3];
     grad[0]=par.k*z*x/(z*z+par.L*par.L);
     grad[1]=par.k*z*y/(z*z+par.L*par.L);
     grad[2]=par.k+par.k*(x*x+y*y)/2*(par.L*par.L-z*z)/Math.pow(z*z+par.L*par.L,2)-par.L/(par.L*par.L+z*z);
     grad=norm(grad);
     return grad;
    }
    
    
    // DIBUIXAR RAIG DONATS ELS EXTREMS
    public BoundedShape raig (BoundedShape raig1, double[] p0, double[] p1){
        double x0 = p0[1];//alerta, abans p0[0] i p0[1]
        double y0 = p0[2];
        //double z0 = p0[2];
        double x1 = p1[1];//alerta, abans p0[0] i p0[1]
        double y1 = p1[2];
        //double z1 = p1[2];
        double xm = x0+(x1 - x0)/2., ym = y0+(y1-y0)/2.;//, zm = (z1 + z0) / 2.; // centre del rectangle
        raig1.setXY(xm,ym);
        raig1.setHeight(Math.sqrt(Math.pow(x1 - x0, 2) + Math.pow(y1 - y0, 2))*1.);
        raig1.setTheta(Math.atan( -(x1 - x0) / (y1 - y0)));
        return raig1;
    }    

// INTERSECCI� D'UNA RECTA (origen p0, vector v), amb una ESFERA (centrada en xc,yc,zc, radi 1)
        public boolean booleantalla(double[] p, double[] v, double[] pc, Parametres par){
        boolean booleantalla;
        double x0=p[0], y0=p[1], z0=p[2];
        double vx=v[0], vy=v[1], vz=v[2];
        double xc=pc[0], yc=pc[1], zc=pc[2];
     
     double a=vx*vx+vy*vy+vz*vz;
     double b=2.*((x0-xc)*vx+(y0-yc)*vy+(z0-zc)*vz);
     double c=(x0-xc)*(x0-xc)+(y0-yc)*(y0-yc)+(z0-zc)*(z0-zc)-Math.pow(par.R/par.scale,2);
     if((b*b-4.*a*c)>=0){
         booleantalla=true;}
     else{
         booleantalla=false;}
     return booleantalla;
    }
    public double[] intersec(double[] p0, double[] v, double[] pc,double R){
     //p0: punt d'on prov� el raig (x0, y0, z0);
     // v: vector director del raig
     // xc, yc, zc: Posici� de la bola (en les unitats del dibuix)
     //p=p0+m*v
     //|p-pc|=R     /(p0x-pcx + m*vx)^2 +() + () = R^2
        double[] p1 = new double[3]; //punt de contacte raig-bola
     //v=norm3d(v);
     double x0=p0[0];
     double y0=p0[1];
     double z0=p0[2];
     double vx=v[0];
     double vy=v[1];
     double vz=v[2];
     double xc=pc[0];
     double yc=pc[1];
     double zc=pc[2];
     
     double a=vx*vx+vy*vy+vz*vz;
     double b=2.*((x0-xc)*vx+(y0-yc)*vy+(z0-zc)*vz);
     double c=(x0-xc)*(x0-xc)+(y0-yc)*(y0-yc)+(z0-zc)*(z0-zc)-R*R;
     
     //am^2+bm+c=0
     double m1, m2, m;
     if((b*b-4.*a*c)>=0){
         m1 = ( -b + Math.sqrt(b * b - 4. * a * c)) / (2. * a);
         m2 = ( -b - Math.sqrt(b * b - 4. * a * c)) / (2. * a);
         if (m1>0){
             if(m2>0){
                 m=Math.min(m1,m2);
             }else{
                 m=m1;
             }
         }else{
             if (m2>0){
                 m=m2;
             }else{
                 m=Math.abs(Math.max(m1,m2));
                 //System.out.println("alguna cosa falla a la intersecció m1="+m1+" m2="+m2);
             }
         }
     } else{
         m=mod(p0)*2.;
     }

     //suposant que el punt p0 �s abans de la bola, el par�metre adequat per trobar la primera
     //intersecci� del raig amb la bola �s el m m�s petit
    /** switch(i){
         case 1:
             m=m2;
             break;
         case 2:
             m=m1;
             break;
     }**/
   

     p1[0]=p0[0]+m*v[0];
     p1[1]=p0[1]+m*v[1];
     p1[2]=p0[2]+m*v[2];

     return p1;
 }
    public double anglelimit(double n1, double n2){
        //Calcula l'angle limit pels rajos d'entrada a la bola (necessari quan n2<n1)
        //n1 sin (ainc) = n2 sin (arefr)
        double anglelimit;
        if (n2<n1){
            anglelimit=Math.asin(n2/n1);
        }else{
            anglelimit=Math.PI/2.;
        }
        return anglelimit;
    }
    public double brownianforce(Parametres par){
        Random r = new Random();
        double x=r.nextGaussian();//genera nombres aleatoris amb distribució gaussiana de mitjana=0 i sigma=1
        double sigma=Math.sqrt(4.*par.gamma*par.kb*par.T);//sigma for brownian force
        double meanforce=0.0;
        double f=meanforce+sigma*x;
        //System.out.println("brownian force="+f*Math.pow(10,12)+"pN");
        return f;
        //P(f)=1/Math.sqrt(2*Math.pi*sigma)*Math.exp(-f/d/sigma/sigma);
    }
    public double[][] calculateYZpath(Parametres par,Raigs raigs,double[] pc){
        Forces forces = new Forces();
        Random r = new Random(); 
        double[][] data = new double[par.Npath][3];// t, y, z per cada punt
            // PATH SIMULATION
            double t=0.;
            double[] f;
            data[0][0]=0.;//time inicial
            data[0][1]=pc[1];//y inicial
            data[0][2]=pc[2];//z inicial
            
            for (int i=1; i<par.Npath;i++){
                f=forces.calc(par, raigs, 0.,data[i-1][1], data[i-1][2])[par.q];
                //x[i]=x[i-1]+1./par.gamma*f[0]+Math.sqrt(2.*par.D*par.dt)*r.nextGaussian();
                data[i][0]=data[i-1][0]+par.dt;
                data[i][1]=data[i-1][1]+1./par.gamma*f[1]+Math.sqrt(2.*par.D*par.dt)*r.nextGaussian();
                data[i][2]=data[i-1][2]+1./par.gamma*f[1]+Math.sqrt(2.*par.D*par.dt)*r.nextGaussian();
            }
        
        return data;
    }
    

///Classes a mig fer:


 double variance(double[] x,Parametres par){
    double sum=0.;
    double mean=meanx(x,par);
    for (int i=0;i<par.counts;i++){
         sum=sum+Math.pow(x[i]-mean,2);
    }
    sum=sum/par.counts;
    double variance=sum;
/**    double sumx2=0.;
    double sumx=0.;    
    int N=x.length;
     for (int i=0;i<N;i++){
         sumx=sumx+x[i];
         sumx2=sumx2+x[i]*x[i];
     }
     double xmean=sumx/(double)N;
     double variance=(sumx2/(double)N-xmean*xmean)/N;**/
     return variance;
 }



    double meanx(double[] x,Parametres par){
        double sum=0.;
        for (int i=0;i<par.counts;i++){
            sum=sum+x[i];
        }
        double mean=sum/par.counts;
        return mean;
    }
//rotaci� d'un vector al voltant d'un punt    
 double[] rot(double[] v, double theta){
        double vx=v[1], vy=v[2];
        //...
        double[] r=new double[3];
        return r;
    }
    double max(double[] x){
        //finds the maximum of an array
        double max=Double.MIN_VALUE;
        for(int i=0;i<x.length;i++){
            if (x[i]>max){
                max=x[i];
            }
        }
        return max;
    }
    double min(double[] x){
        double min=Double.MAX_VALUE;
        for(int i=0;i<x.length;i++){
            if (x[i]<min){
                min=x[i];
            }
        }
        return min;
    }
//Formules feixos gaussians
/**
    public double[] gradient(double x, double y, double z, Parametres par){
            double[] gradient = new double[3];
            double r= Math.sqrt(x*x+y*y)*par.R;
            double wz=w(z,par);
            gradient[0]=I(r,z,par)*(-4.*x/(wz*wz));
            gradient[1]=I(r,z,par)*(-4.*y/(wz*wz));
            double I0 = par.P*2./(Math.PI*Math.pow(par.w0,2));
            gradient[2]=I0*Math.exp(-2.*r*r/(wz*wz))*(-2.*z*Math.pow(par.w0/wz,4))/(par.L*par.L);
            return gradient;
        }**/
        public double w(double z, Parametres par){
            double zR = par.L;//Math.PI*par.w0*par.w0/par.lambda;
            double w = par.w0*Math.sqrt(1.+Math.pow(z/zR,2));
            return w;
        }
        public double I0(Parametres par){
            //3D
            double I0 = par.Puseful*2./(Math.PI*Math.pow(par.w0,2));//TOTAL POWER: 1/2 PI I0 w0^2            
            return I0;
        }
        /**public double I02D(double z, Parametres par){
            //2D
            double I0x = par.P*Math.sqrt(2./Math.PI)(Math.PI*Math.pow(par.w0,2));//TOTAL POWER: 1/2 PI I0 w0^2            
            return I0;            
        }**/
        public double I(double r,double z,Parametres par){
            double I = I0(par)*Math.pow(par.w0/w(z,par),2)*Math.exp(-2.*(r*r)/Math.pow(w(z,par),2));
            return I;
        }    

    public double ppangle (double[] p0, double[] p1){
        //dona l'angle de la línia que uneix dos punts respecte l'eix z
        double x0 = p0[1];//alerta, abans p0[0] i p0[1]
        double y0 = p0[2];
        //double z0 = p0[2];
        double x1 = p1[1];//alerta, abans p0[0] i p0[1]
        double y1 = p1[2];
        //double z1 = p1[2];
        double theta= Math.atan( -(x1 - x0) / (y1 - y0));
        return theta;
    }        
    public double thetarefrac(double n1, double n2, double thetain){
        double thetarefrac;
        double anglelimit=anglelimit(n1,n2);
        if (thetain<anglelimit){
            thetarefrac=Math.asin(n1*Math.sin(thetain)/n2);
        }else{
            thetarefrac=Math.asin(n1*Math.sin(anglelimit*0.9999)/n2);
        }
        return thetarefrac;
    }
    public double[][] meanfilter(double[] x, double[] f, int n){
        //n= ordre del filtre
        int N = x.length;
        int Nfilt=N/n+1;
        double[] xfilt=new double[Nfilt];
        double[] ffilt=new double[Nfilt];
        int k=0;
        for (int i=0;i<N;i=i+n){
            xfilt[k]=org.opensourcephysics.numerics.Util.computeAverage(x,i, n);
            ffilt[k]=org.opensourcephysics.numerics.Util.computeAverage(f,i, n);
            k++;
        }
        double[][] output = new double[2][k];
        output[0]=xfilt;
        output[1]=ffilt;
        return output;      
    }
    
    public double coherce (double x, double xmin, double xmax){
        if (x<xmin){x=xmin;}
        if (x>xmax){x=xmax;}
        return x;
    }
    public double string2double(String s){
        s=s.replace(',', '.');
        double d=Double.valueOf(s);
        return d;
    }
    public double sumar(double[] x){
        double suma=0;
        for(int i=0;i<x.length;i++){
            suma=suma+x[i];
        }
        return suma;
    }
}



