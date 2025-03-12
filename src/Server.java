import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
public class Server {
    private static int port = 12345;
    public static void main(String[] args) {
        try{
            // obrir el port
            ServerSocket ss = new ServerSocket(port);
            Socket entrada = ss.accept();
            // crear el DIS
            InputStream is = entrada.getInputStream();
            //DataInputStream dis = new DataInputStream(is);

            //literalmente me ha dicho el intelij que lo haga por que si no no lee por lineas o una cosa rara, veamos que tal y dejo lo aterior para no liarla
            BufferedReader dis = new BufferedReader(new InputStreamReader(is));

            //intentarem llegir-ho tot i ho retornarem per pantalla fins que ens diguin FI
            Boolean fi = false;//quizas estaria bien que fi fuera una variable compartida
            String sortida;
            while(!fi){
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
            System.out.println();

            //tancament de canals
            dis.close();
            entrada.close();
            ss.close();
        }catch(Exception e){
            System.out.println("Hay un problema :(" + e);
        }
    }
}