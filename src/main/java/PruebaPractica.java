import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


/**
 * Esta es la clase que ejecutara el progarma principal con todos sus hilos y procesos
 * Tiene como metodos:
 *      - ejecutarHiloHostsArriba
 *      - validarSubred
 *      - descargarArchivo
 *
 */
public class PruebaPractica {

    private static InputStream inputStream  ;
    private static InputStreamReader inputStreamReader;
    private static BufferedReader bufferedReader;
    private static Map<Integer, String> mapaPuertos = new LinkedHashMap<>();
    private static final String URL = "http://ftp.sun.ac.za/ftp/pub/documentation/security/port-numbers.txt";


    private static List<String> listaHostsArriba = Collections.synchronizedList(new ArrayList<>());


    /**
     * Este es el metodo principal de la clase, ejecuta el metodo {@link #descargarArchivo(String)} y luego pide una subred por teclado
     * Válida la subred mediante el metodo {@link #validarSubred(String)}
     * En base a esa subred ejecuta los procesos en paralelo mediante la clase {@link EjecutorPing} que nos agrupan las ips que están arriba
     * Luego ejecuta el metodo {@link #ejecutarHiloHostsArriba()}
     *
     */
    public static void main(String[] args) {


        descargarArchivo(URL);

        Scanner teclado = new Scanner(System.in);

        System.out.print("Introduce una subred /24: ");
        String subred = teclado.next();

        if (validarSubred(subred)) {
            System.out.println("Subred valida");
        }
        String ip = subred + ".";

        EjecutorPing[] listaHilos = new EjecutorPing[255];

        try {
            for (int i = 0; i < listaHilos.length; i++) {
                String ipUsable = ip+i;
                listaHilos[i] = new EjecutorPing(ipUsable, listaHostsArriba);
                listaHilos[i].start();
            }

            for (Thread t: listaHilos) {
                t.join();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ejecutarHiloHostsArriba();

    }

    /**
     * El metodo ejecutarHiloHostsArriba utiliza la lista de ips que están arriba y lanza uno por uno los hilos
     * A los hilos les pasamos la ip y el mapa con los puertos y sus nombres
     *
     */
    public static void ejecutarHiloHostsArriba() {
        try {
            HilosIP[] hilosIPS = new HilosIP[listaHostsArriba.size()];
            for(int i=0; i < listaHostsArriba.size(); i++) {
                hilosIPS[i] = new HilosIP(listaHostsArriba.get(i), mapaPuertos);
                hilosIPS[i].start();
            }

            for (int i = 0; i < hilosIPS.length; i++) {
                hilosIPS[i].join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Metodo que recibe una subred por parametro y valida si es una subred /24 válida
     * @param ip String que recibe el metodo y que debe estar en forma de subred /24 (x.x.x)
     * @return {@code true} o {@code false} dependiendo de si es una subred valida o no
     *
     */
    public static boolean validarSubred(String ip) {

        // Step 1: Separate the given string into an array of strings using the dot as delimiter
        String[] parts = ip.split("\\.");

        // Step 2: Check if there are exactly 3 parts
        if (parts.length != 3) {
            return false;
        }

        // Step 3: Check each part for valid number
        for (String part : parts) {
            try {
                // Step 4: Convert each part into a number
                int num = Integer.parseInt(part);

                // Step 5: Check whether the number lies in between 0 to 255
                if (num < 0 || num > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                // If parsing fails, it's not a valid number
                return false;
            }
        }

        // If all checks passed, return true
        return true;
    }

    /**
     * Este metodo descarga y almacena el contenido de la url pasada por parametro en {@code mapaPuertos}
     * Filtra el contenido de la página en base a su número de espacios y solo almacena aquellos que son adecuados a los filtros
     * @param urlDescargar Url que utiliza para descargar y procesar los datos
     *
     */
    public static void descargarArchivo(String urlDescargar){
        System.out.println("Descargando: " + urlDescargar + "...");

        try {
            URL url = new URL(urlDescargar);

            inputStream = url.openStream(); //Esto va a leer byte a byte
            inputStreamReader = new InputStreamReader(inputStream); //Este va a leer linea por linea
            bufferedReader = new BufferedReader(inputStreamReader);

            String linea;
            while ((linea = bufferedReader.readLine()) != null ){
                if (linea.contains("tcp")) {
                    String[] partes = linea.split("\\s+\\s+");
                    if (partes.length == 3) {
                        String[] tipoPuerto = partes[1].split("/");
                        if (tipoPuerto[1].equals("tcp") && tipoPuerto[0].matches("\\d+")) {
                            if (!partes[0].equals("#")) {
                                int numPuerto = Integer.parseInt(tipoPuerto[0]);
                                String nombrePuerto = partes[0];
                                mapaPuertos.put(numPuerto, nombrePuerto);
                            }
                        }
                    }
                }
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                bufferedReader.close();
                inputStreamReader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
