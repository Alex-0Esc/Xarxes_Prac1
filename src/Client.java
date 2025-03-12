import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        // check java version
        String javaVersion = System.getProperty("java.version");
        System.out.println("Versión de Java: " + javaVersion);

        String host = "localhost";
        int port = 12345;

        //host address from args
        if (args.length > 0) {
            host = args[0];
        }

        try {
            Socket s = new Socket(host, port);
            OutputStream os = s.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            //vamos a hacer que el cliente le envie chats al servidor para el punto de partida
            String entrada;
            Boolean fi = false;
            Scanner scanner = new Scanner(System.in);
            while (!fi){
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

            dos.flush();
            dos.close();
            s.close();

        }catch (Exception e){
            System.out.println("Ha ocorregut un error i paro");
            e.printStackTrace();
        }
    }
}