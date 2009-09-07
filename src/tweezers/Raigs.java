package tweezers;

public class Raigs {
    Operacions op = new Operacions();
    Parametres par1 = new Parametres();
    double Rl = par1.Rpe; //radi de la pupil�la d'entrada, en mm

    //Objectes per al c�lcul
    double[] vr = new double[par1.Nraigmax]; // posici� radial del punt d'incid�ncia sobre la lent (en mm)
    double[] vbeta = new double[par1.Nraigmax]; //angle azimutal dels punts d'incid�ncia sobre la lent
    double[][] s =new double[par1.Nraigmax][3]; //vector director del raig
    double[] nx = new double[par1.Nraigmax]; // component x del vector director dels raigs (abans de la lent)
    double[] ny = new double[par1.Nraigmax]; // component y     "
    double[] nz = new double[par1.Nraigmax]; // component z     "
    double[] power = new double[par1.Nraigmax]; // fracci� de pot�ncia assignada al raig k-�ssim
    double[] p = new double[par1.Nraigmax];  //fracció de la potència assignada i pesada a voluntat
    double[] x0 = new double[par1.Nraigmax]; //punt d'incid�ncia x, y del raig k sobre la lent
    double[] y0 = new double[par1.Nraigmax];
    double[] xcoverslip = new double[par1.Nraigmax];
    double[] ycoverslip = new double[par1.Nraigmax];
    double[] dF=new double[par1.Nraigmax];//prova
    double zcoverslip;
    int Nraig;
    
    public Raigs(Parametres par) {
        Nraig=par.Nraig;//par.Nraigd;

        double xmax=par.Rcov;//radi del feix sobre el cobreobjectes
        double discr=2.*xmax/(double)(Nraig-1);
        for (int k=0;k<Nraig;k=k+1){
            x0[k] = 0.;
            y0[k] = -xmax+(double)k*discr;//(-par.Rpe+(double)k*discrPE);///par.R;                    
            
            double expx = Math.exp(-2.*y0[k]*y0[k]/Math.pow(par.wcov,2));//par.P*2./(Math.pow(par.wcov,2))*Math.exp(-x0[k]*x0[k]/Math.pow(par.wcov,2));////par.Puseful/(Math.PI*Math.pow(xmax,2))*Math.exp(-2.*(x0[k]*x0[k]+y0[k]*y0[k])/Math.pow(par.wcov,2))*discr;///(Math.PI*Math.pow(par.Nraig/2.,2));
            double erf=0.;
            double argumenterf=Math.sqrt(par.Rcov*par.Rcov-y0[k]*y0[k])*Math.sqrt(2)/par.wcov;
 
            if (par.correction3D){
/**                try{
                    if(Math.abs(argumenterf)<0.1){
                        erf=0.;
                    }else{
                        erf=org.apache.commons.math.special.Erf.erf(argumenterf);
                        //System.out.println("Raig "+k+" erf of ="+argumenterf);
                    }
                }catch(Exception e){
                
                };**/
                power[k]=expx*(Math.abs(2*y0[k]*discr)-discr*discr)/xmax/xmax;
            }else{
                power[k]=2*expx;//*erf*y0[k];
            }
            //System.out.println("expx="+expx+" erf="+erf+"x="+y0[k]);
            //System.out.println("Rcov="+par.Rcov+" waist(cov)="+par.wcov+" discr="+discr+" Puseful="+par.Puseful+" power["+k+"]="+power[k]);
            //p[k]=power[k];// en els càlculs aquesta potència es pot pesar, mantenint el valor original a power[k]
            //angle[k] = Math.atan(y0[k] / f);          
            vr[k] = Math.sqrt(Math.pow(x0[k], 2) + Math.pow(y0[k], 2));//coordenada radial, sobre la lent
            vbeta[k] = Math.atan2(x0[k], y0[k]);// �s a dir, pi // angle azimutal, respecte l'eix Y            
            nx[k] = 0.;
            ny[k] = 0.;
            nz[k] = 1.;
             
            //System.out.println("y["+k+"]="+y0[k]);
            //System.out.println("r["+k+"]="+vr[k]);
            //System.out.println("beta["+k+"]="+vbeta[k]);
        }
        double poweruseful=op.sumar(power);
        for (int k=0;k<Nraig;k++){
            p[k]=power[k]/poweruseful;//fracció de potència associada a aquest raig respecte la useful
            //System.out.println("p["+k+"]="+p[k]);
            p[k]=par.Puseful*p[k];//amb dimensions de potència.
        }


        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }     

    }

    private void jbInit() throws Exception {
    }

    public int comptar(double Rl, double d) {
        int k = 0; //comptador de raigs a partir del radi de la lent i l'interval de discretitzacio
        for (double y = -Rl; y <= Rl; y = y + d) {
            for (double x = -Math.sqrt(Rl * Rl - y * y);
                            x <= Math.sqrt(Rl * Rl - y * y);
                            x = x + d) {
                k++;
            }
        }
        return k;
    }
    }
