import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {

        String host = "localhost";
        int port1 = 12345;
        int port2 = 54321;

        try {
            //Los socket
            Socket s1 = new Socket(host, port1);
            Socket s2 = new Socket(host, port2);

            //EL DOS
            OutputStream os = s1.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            //EL DIS
            InputStream is = s2.getInputStream();
            BufferedReader dis = new BufferedReader(new InputStreamReader(is));

            String entrada;
            Boolean fi = false;
            Scanner scanner = new Scanner(System.in);
            String sortida;
            while (!fi){
                //Zona de envios
                if(scanner.hasNextLine()){
                    entrada = scanner.nextLine();
                    //Filtramos mensajes vacios
                    if(entrada != null && !entrada.trim().isEmpty()){
                        dos.writeUTF(entrada + "\n");
                        dos.flush();
                        Debugger.debug("He escrito: " + entrada);
                    }
                    if(entrada.equals("FI")){
                        fi = true;
                        System.out.println("Has acabat la conexió");
                    }
                }
                //Zona de lectura
                if(dis.ready()){
                    Debugger.debug("He detectado algo que leer!");
                    sortida = dis.readLine();
                    sortida = sortida.replaceAll("[^\\x20-\\x7E]", ""); // Elimina caracteres fuera del rango imprimible ASCII
                    //Filtramos mensajes vacios
                    if(sortida != null && !sortida.trim().isEmpty()) {
                        System.out.println("Servidor: " + sortida);
                    }

                    if(sortida.equals("FI")){
                        System.out.println("El servidor ha tancat la conexió");
                    }
                }
            }

            //tancament de canals
            dis.close();
            s1.close();
            dos.close();
            s2.close();
        }catch (Exception e){
            System.out.println("El servidor no esta disponible");
            //e.printStackTrace();
        }
    }
}