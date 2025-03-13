import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    //Esto de necesitar dos puertos me parece rarete pero con 1 se me cierra
    private static int port1 = 12345;
    private static int port2 = 54321;
    public static void main(String[] args) {
        try{
            //Obrir els ports de entrada i sortida (Cambiar un minimo el orden de esto hace que todo explote)
            ServerSocket ss1 = new ServerSocket(port1);
            ServerSocket ss2 = new ServerSocket(port2);
            Socket s_entrada = ss1.accept();
            System.out.println("ok1");
            Socket s_sortida = ss2.accept();
            System.out.println("ok2");

            //EL DIS
            InputStream is = s_entrada.getInputStream();
            BufferedReader dis = new BufferedReader(new InputStreamReader(is));

            //EL DOS
            OutputStream os = s_sortida.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


            //intentarem llegir-ho tot i ho retornarem per pantalla fins que ens diguin FI
            Boolean fi = false;//quizas estaria bien que fi fuera una variable compartida pero puede que eso de problemas al forzar cerrado
            String sortida;
            Scanner scanner = new Scanner(System.in);
            String entrada;
            while(!fi){
                //Debugger.debug("1");
                //Zona de envios
                if(reader.ready()){
                    entrada = reader.readLine();
                    //Filtramos mensajes vacios
                    if(entrada != null && !entrada.trim().isEmpty()){
                        dos.writeUTF(entrada + "\n");
                        dos.flush();
                        //Debugger.debug("He escrito: " + entrada);
                    }
                    if(entrada.equals("FI")){
                        fi = true;
                        System.out.println("Has acabat la conexió");
                    }
                }else{
                    //Debugger.debug("estamos aqui?");
                }

                //Debugger.debug("2");
                //zona de lectura
                if(dis.ready()){
                    Debugger.debug("He detectado algo que leer!");
                    sortida = dis.readLine();
                    sortida = sortida.replaceAll("[^\\x20-\\x7E]", ""); // Elimina caracteres fuera del rango imprimible ASCII
                    //Filtramos mensajes vacios
                    if(sortida != null && !sortida.trim().isEmpty()) {
                        System.out.println("Client: " + sortida);
                    }

                    if(sortida.equals("FI")){
                        System.out.println("El client ha tancat la conexió");
                    }
                }
            }
            System.out.println();

            //tancament de canals
            dis.close();
            s_entrada.close();
            ss1.close();
            dos.close();
            s_sortida.close();
            ss2.close();
        }catch(Exception e){
            System.out.println("Hay un problema :(" + e);
        }
    }
}