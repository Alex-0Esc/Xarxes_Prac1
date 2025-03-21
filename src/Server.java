import javax.management.monitor.Monitor;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int port1 = 12345;
    private static final int port2 = 54321;
    private static final int port3 = 50000;
    public static volatile Boolean fi = false; // Volatile para evitar problemas de concurrencia

    public static void main(String[] args) throws IOException {
        try {

            //Inicialització de dades i estructures
            ServerSocket ss1 = new ServerSocket(port1);
            ServerSocket ss2 = new ServerSocket(port2);
            ServerSocket ss3 = new ServerSocket(port3);

            System.out.println("Esperant connexions...");

            Socket sEntrada = ss1.accept();
            Socket sSortida = ss2.accept();
            Socket sAlive = ss3.accept();


            System.out.println("Connexió acceptada.");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(sSortida.getOutputStream()));
            writer.write("Connexió acceptada.\n");
            writer.flush();

            // Thread per detectar el tancament abrupte per part del client
            Thread monitor = new Thread(new Alive(sAlive));
            monitor.setDaemon(true);
            monitor.start();

            //Thread per llegir
            Thread readThread = new Thread(new ReadInput(sEntrada));

            //Thread per escriure
            Thread writeThread = new Thread(new WriteOutput(sSortida));

            readThread.start();
            writeThread.start();

            //Esperem a que acabin els threads
            readThread.join();
            writeThread.join();

            System.out.println("Tancant connexió...");

        } catch (Exception e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }

    //Classe per escriure missatges
    public static class WriteOutput implements Runnable {
        private final Socket mySocket;
        private final BufferedWriter writer;
        private final BufferedReader reader;

        public WriteOutput(Socket socket) {
            this.mySocket = socket;
            try {
                this.writer = new BufferedWriter(new OutputStreamWriter(mySocket.getOutputStream()));
                this.reader = new BufferedReader(new InputStreamReader(System.in));
            } catch (IOException e) {
                throw new RuntimeException("Error al inicializar la salida", e);
            }
        }

        @Override
        public void run() {
            try {
                String entrada;
                //Si no s'ha rebut fi, i hi ha algo per llegir, llegim i filtrem missatges buits
                while (!fi && (entrada = reader.readLine()) != null) {
                    if (!entrada.trim().isEmpty()) {
                        writer.write(entrada + "\n");
                        writer.flush();
                    }
                    if (entrada.equalsIgnoreCase("FI")) {
                        System.out.println("Has tancat la connexió.");
                        fi = true;
                        System.exit(0);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error en la escritura del servidor.");
            } finally {
                try {
                    writer.close();
                    reader.close();
                    mySocket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    //Classe per llegir l'unput
    public static class ReadInput implements Runnable {
        private final Socket mySocket;
        private final BufferedReader reader;

        public ReadInput(Socket socket) {
            this.mySocket = socket;
            try {
                this.reader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException("Error al inicializar la entrada", e);
            }
        }
        @Override
        public void run() {
            try {
                String entrada;
                while (!fi && (entrada = reader.readLine()) != null) {
                     // Filtra caracteres no imprimibles
                    if (!entrada.trim().isEmpty()) {
                        System.out.println("Client: <<" + entrada + ">>");
                    }
                    if (entrada.equalsIgnoreCase("FI")) {
                        System.out.println("El client ha tancat la connexió.");
                        fi = true;
                        System.exit(0);
                    }
                }
            } catch (IOException e) {
                System.out.println("Connexió tancada.");
                System.exit(0);
            } finally {
                try {
                    reader.close();
                    mySocket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    //Aquesta classe serveix per comprobar si el Client està viu, i per tant, continuar executant-se
    public static class Alive implements Runnable{

        private final Socket mySocket;
        private InputStream inputStream;
        private byte[] buffer;
        private int bytesRead;
        public Alive(Socket socket){
            this.mySocket = socket;
            try {
                this.inputStream = socket.getInputStream();
                this.buffer = new byte[1];
            } catch (IOException e) {
                throw new RuntimeException("Error al inicializar la entrada", e);
            }
        }

        @Override
        public void run() {

            try {
                while (!mySocket.isClosed()) {
                    this.bytesRead = inputStream.read(buffer);
                    if (bytesRead == -1) { // -1 indica que s'ha tancat la conexió
                        Debugger.debug("Hey listen!");
                        break;
                    }
                    //No fa falta dormir el threat per que es queda encallat al read.
                }
                System.out.println("El client ha tancat la connexió de forma abrupta...");
                System.exit(0);
            } catch (IOException e) {
                System.out.println("El client ha tancat la connexió de forma abrupta...");
                System.exit(0);
            }
        }
    }
}






