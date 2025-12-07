import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Esta es la clase que se encargara de confirmar si las ips que recibe como parametro tienen sus puertos abiertos o no
 *
 */
public class HilosIP extends Thread {

    private String ip;
    private Map<Integer, String> mapaPuertos;
    private final int NUM_MAX_PUERTOS = 500;

    /**
     * Constructor que se encarga de recibir los parametros que le pasa {@link PruebaPractica}
     * @param ip Ip que se pasa desde la clase antes mencionda
     * @param mapaPuertos Mapa que contiene el número de puerto y su nombre
     *
     */
    public HilosIP(String ip, Map<Integer, String> mapaPuertos){
        this.ip = ip;
        this.mapaPuertos = mapaPuertos;
    }

    /**
     * Metodo que se encarga de recorrer todos los puertos hasta {@code NUM_MAX_PUERTOS}
     * @param puerto Número del puerto que queremos comprobar
     * @return {@code true} si no salta ningun error al conectarse al codigo {@code false} cuando al intentar conectarse
     * salta alguna excepcion, indicador de que no se pudo conectar
     */
    public boolean puertosTCPAbiertos(int puerto){
        try (Socket socket = new Socket()){
            socket.connect(new InetSocketAddress(ip, puerto), 50);
            //Si no salta nada, el puerto esta abierto y nos podemos conectar
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Metodo que ejecuta el hilo cada vez que es llamado
     * Crea un {@code StringBuilder} para almacenar la información de cada puerto
     * Y utiliza un {@code boolean} para devolver la información si tiene puertos abiertos o si no los tiene
     *
     */
    @Override
    public void run(){

        boolean puertosAbiertos = false;

        StringBuilder sb = new StringBuilder();

        sb.append("IP: ").append(ip).append(" ACTIVA\n");

        for (int i = 1; i < NUM_MAX_PUERTOS; i++) {
            if (puertosTCPAbiertos(i)){
                puertosAbiertos = true;

                sb.append("\tPUERTO: ")
                        .append(i)
                        .append(" ABIERTO: ")
                        .append(mapaPuertos.get(i))
                        .append("\n");

            }
        }

        if (puertosAbiertos){
            synchronized (System.out) {
                System.out.print(sb.toString());
            }
        } else {
            sb = null;
        }
    }
}
