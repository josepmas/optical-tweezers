package tweezers;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.*;
import javax.swing.JOptionPane;

//xavi-todo:
// * (xavi): add description bit for all events in a Human readable format

public class Tracer{
    static javax.swing.JFileChooser jFileChooser2 = new javax.swing.JFileChooser();
    // Define part for XML handled from OSP
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static SimpleDateFormat sdf_filename = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
    static Date now_ini = new Date();
    static String time_ini =sdf_filename.format(now_ini.getTime());//df.format(System.currentTimeMillis());
    static boolean traceON=true;
    static int comptador = 1;
    static String user_id;
    static String session_id;
    static int exercise_id = 1;
    static FileWriter escriptor;
    static String filename2;
    // Define part for Hand made xml file from ascii output file
    
    public static void set_user() {
/**        do {
            user_id=JOptionPane.showInputDialog("Introdueix el teu nom","");
        } while (user_id==null || user_id.equals("") || 
                user_id.equals("null"));
        session_id=time_ini;
**/

        //Josep - si Ok amb text="" o Cancel, deixar user_ID=null (i que no traci).
        user_id=JOptionPane.showInputDialog("Student ID:","");
        if (user_id==null || user_id.equals("")|| user_id.equals("null")){
            user_id=null;
        }else{
            session_id=time_ini;
        }
    }
   
    
    public static void newtrace(){
            jFileChooser2.showSaveDialog(null);

            filename2 = jFileChooser2.getSelectedFile().toString();   
            System.out.println(filename2);
    }
    
    public static void start_trace() {

        //set_user();
        filename2 = "logs/trace-"+user_id+"_exercise_"+ exercise_id +".xml";
        try {
            escriptor = new FileWriter(filename2,true);
//            escriptor.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//            escriptor.write("\n");
//            escriptor.write("<!DOCTYPE log SYSTEM \"Logging_v1.dtd\">");
//            escriptor.write("\n");
            escriptor.write("<log>");
            escriptor.write("\n");
            Date now = new Date();
            String time =sdf.format(now.getTime());//df.format(System.currentTimeMillis());
            escriptor.write("<event application=\"Tweezers\" action=\"Tweezers_init\" user=\"" + 
                    user_id + "\" session=\"tweezers_"+time+"\" time=\""+ time + "\" time_ms=\""+ System.currentTimeMillis() + "\" number=\""+comptador+"\">");
            escriptor.write("\n");
            escriptor.write("<param name=\"version\" value=\"1.0 (May 28th, 2009)\"/>");
            escriptor.write("\n");
            escriptor.write("<description>The Tweezers applet (1.0 (May 28th, 2009)) is started.</description>");
            escriptor.write("\n");
            escriptor.write("</event>");
            escriptor.write("\n");
            escriptor.close();

        } catch (IOException ignored) {}

    }
    public static void write_all_param(Parametres par) {
        write_param("power",par.P);
        write_param("Wavelength",par.lambda);
        write_param("Refractive Index - N1",par.n1);
        write_param("N2",par.n2);
        write_param("NA",par.NA);
        write_param("w0",par.w0);
        write_param("R",par.R);
        write_param("Viscosity (n)",par.visc);
        if (par.water==true) {
            write_param("Medium Water", Double.valueOf("1"));
        }
            write_param("Temperature",par.T);
        write_param("Regime",Double.valueOf(par.regim));
        write_param("Pupil2waist",par.pupil2waist);
        write_param("Zoom",par.zoom);
        write_param("Time step - Dt (s)",par.dt);
        if (par.selectedforces[0]==true) {
              write_param("Total force",Double.valueOf("1"));
        }
        if (par.selectedforces[1]==true) {
              write_param("Scattering force",Double.valueOf("1"));
        }
        if (par.selectedforces[2]==true) {
              write_param("Gradient force",Double.valueOf("1"));
        }
        if (par.ztrapfound==true) {
            write_param("Z equilibrium found",Double.valueOf("1"));
        }
        if (par.ztrapfound == true) {
            write_param("Z equilibrium",par.ztrap);
        }
        write_param("Trap Stiffness - k (pN/m)",par.stiffness);


// Todo xavi  add all params that I want to have recorded at startup, + calibration time + animation time
    }
    


    public static void write_action_ini(String action_name) {
        try {
            comptador = comptador +1;
            escriptor = new FileWriter(filename2,true);
            escriptor.write("\t<event application=\"Tweezers-applet\"");
            escriptor.write(" action=\""+action_name+"\"");
            escriptor.write(" user=\""+user_id+"\"");
            escriptor.write(" session=\""+session_id+"\"");
            escriptor.write(" time=\""+System.currentTimeMillis()+"\"");
            escriptor.write(" number=\""+comptador+"\"");
            escriptor.write(">\n");
            escriptor.close();
        } catch (IOException ignored) {}

    }
     public static void write_param(String param_name, Double param_value) {
         try {
            FileWriter escriptor = new FileWriter(filename2,true);
            escriptor.write("\t\t<param name=\""+param_name+"\"");
            escriptor.write(" value=\""+param_value+"\"/>\n");
            escriptor.close();
        } catch (IOException ignored) {}

    }
    public static void write_param(String param_name, String param_value) {
    try {
         FileWriter escriptor = new FileWriter(filename2,true);
         escriptor.write("\t\t<param name=\""+param_name+"\"");
         escriptor.write(" value=\""+param_value+"\"/>\n");
         escriptor.close();
    } catch (IOException ignored) {}

    }

     public static void write_action_end() {
         try {
            FileWriter escriptor = new FileWriter(filename2,true);
            escriptor.write("\t</event>\n");
            escriptor.close();
        } catch (IOException ignored) {}

    }

    public static void end_trace() {
        try {
            FileWriter escriptor = new FileWriter(filename2,true);
            escriptor.write("\t<event application=\"Tweezers-applet\"");
            escriptor.write(" action=\"Tweezers applet closed\"");
            escriptor.write(" user=\""+user_id+"\"");
            escriptor.write(" session=\""+session_id+"\"");
            escriptor.write(" time=\""+System.currentTimeMillis()+"\"");
            escriptor.write(" number=\""+comptador+"\"");
            escriptor.write(">\n");
            escriptor.write("\t</event>\n");
            escriptor.write("</log>\n");
            escriptor.close();

        } catch (IOException ignored) {}

    }

}