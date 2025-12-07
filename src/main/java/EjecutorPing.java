import javax.net.ssl.HostnameVerifier;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Esta clase se encarga de ejecutar el ping a las ips que se le pasan como parametros y almacenarlas en {@code listaHostsArriba}
 *
 */
public class EjecutorPing extends Thread{

    private String host;

    private List<String> listaHostsArriba;

    /**
     * Este metod es el constructor que asigna el valor de los parametros pasados desde {@link PruebaPractica}
     * @param host Ip pasada por la clase antes mencionada
     * @param lista Lista de hosts abiertos
     */
    public EjecutorPing(String host, List<String> lista) {
        this.host = host;
        this.listaHostsArriba = lista;
    }

    /**
     * Metodo que ejecuta el hilo
     * Lanza un proceso de manera paralela que se encarga de hacer un ping y que distingue el tipo de sistema operativo
     * Lee el valor de salida y dependiendo de este ultimo, lo añade o no a {@code listaHostsArriba}
     */
    @Override
    public void run() {
        ProcessBuilder pb;
        if (System.getProperty("os.name").toLowerCase().contains("win")){
            pb = new ProcessBuilder("ping","-n", "1" , host);
        } else {
            pb = new ProcessBuilder("ping","-c", "1" , host);
        }

        try {
            Process process = pb.start();
            process.waitFor();
            if (process.exitValue() == 0){
                listaHostsArriba.add(host);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
