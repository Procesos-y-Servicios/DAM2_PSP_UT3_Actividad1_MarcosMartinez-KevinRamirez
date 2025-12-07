import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class HilosIP extends Thread {

    private String ip;
    private final int NUM_MAX_PUERTOS = 1000;


    public HilosIP(String ip){
        this.ip = ip;
    }

    public boolean puertosTCPAbiertos(int puerto){
        try (Socket socket = new Socket()){
            socket.connect(new InetSocketAddress(ip, puerto), 50);
            //Si no salta nada, el puerto esta abierto y nos podemos conectar
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void run(){
        boolean contieneTCP = false;

        StringBuilder sb = new StringBuilder();

        sb.append("IP: ").append(ip).append(" ACTIVA\n");

        for (int i = 1; i < NUM_MAX_PUERTOS; i++) {
            if (puertosTCPAbiertos(i)){
                contieneTCP = true;

                sb.append("\tPUERTO: ")
                        .append(i)
                        .append(" ABIERTO: Aqui deberia de salir algo :P --> IP usada ")
                        .append(ip)
                        .append("\n");
//                synchronized(System.out) {
//                    System.out.println("\tPUERTO: " + i + " ABIERTO: Aqui deberia de salir algo :P --> IP usada " + ip);
//                }

            }
        }

        if (!contieneTCP){
            sb.append("\tNo tiene puertos abiertos\n");
        }

        synchronized (System.out) {
            System.out.print(sb.toString());
        }

    }
}
