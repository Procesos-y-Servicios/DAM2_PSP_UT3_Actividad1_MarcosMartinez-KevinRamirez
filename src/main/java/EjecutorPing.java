import javax.net.ssl.HostnameVerifier;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EjecutorPing extends Thread{

    private String host;

    private List<String> listaHostsArriba;

    public EjecutorPing(String host, List<String> lista) {
        this.host = host;
        this.listaHostsArriba = lista;
    }

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
                System.out.println("Host arriba " + host);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


//    public static ArrayList<String> addHostArriba() {
//        listaHostsArriba.add(host);
//        return listaHostsArriba;
//    }


}
