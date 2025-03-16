import java.io.*;
import java.net.Socket;

public class Client {

    public static volatile Boolean fi = false; // Volatile para asegurar visibilidad entre hilos

    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port1 = 12345;
        int port2 = 54321;
        Socket s1 = new Socket(host, port1);
        Socket s2 = new Socket(host, port2);
        BufferedReader reader = new BufferedReader(new InputStreamReader(s2.getInputStream()));
        String mensajeInicial = reader.readLine();
        System.out.println("Servidor: " + mensajeInicial);

        try{
            Thread readerThread = new Thread(new ReadInput(s2));
            Thread writerThread = new Thread(new WriteOutput(s1));
            readerThread.start();
            writerThread.start();

            readerThread.join();  // Esperar a que terminen los hilos
            writerThread.join();
            System.out.println("Tancant connexió...");

        } catch (Exception e) {
            System.out.println("El servidor no està disponible.");
        }
    }

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
                while (!fi && (entrada = reader.readLine()) != null) {
                    if (!entrada.trim().isEmpty()) {
                        writer.write(entrada + "\n");
                        writer.flush();
                    }
                    if (entrada.equalsIgnoreCase("FI")) {
                        System.out.println("Has tancat la connexió.");
                        fi = true;
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error en la escritura del cliente.");
                System.exit(0);
            } finally {
                try {
                    writer.close();
                    reader.close();
                    mySocket.close();
                    System.exit(0);
                } catch (IOException ignored) {
                }
            }
        }
    }

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
                    entrada = entrada.replaceAll("[^\\x20-\\x7E]", ""); // Elimina caracteres no imprimibles
                    if (!entrada.trim().isEmpty()) {
                        System.out.println("Servidor: " + entrada);
                    }
                    if (entrada.equalsIgnoreCase("FI")) {
                        System.out.println("El servidor ha tancat la connexió.");
                        fi = true;
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error en la lectura del servidor. El servidor s'ha desconectat.");
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
}
