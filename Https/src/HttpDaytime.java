import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Une classe impl�mentant le service DayTime sous le protocole HTTP
 * @author Patrick Guichet
 */
public class HttpDaytime implements HttpHandler {
    // le contexte du service
    private static final String CONTEXT = "/daytime";
    // l'objet responsable du formattage de la date
    private static DateFormat df =
            DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM);
    // le serveur HTTP
    private HttpServer server;

    /**
     * Le gestionnaire impl�mentant le service
     * @param ex l'objet encapsulant la communication client-serveur
     * @throws IOException si la communication �choue
     */
    public void handle(HttpExchange ex) throws IOException {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        // le string exposant la date
        String dateString = df.format(date);
        // pr�paration du message
        String message = String.format("<h1 align='center'>%s</h1>", dateString);
        // r�cup�ration des headers de la r�ponse
        Headers respHeaders = ex.getResponseHeaders();
        byte[] messageBytes = message.getBytes();
        // Initialisation des headers n�cessaires � une
        // bonne interpr�tation de la r�ponse par le client
        respHeaders.set("Content-Type", "text/html");
        // Exp�dition du code r�ponse (ici OK)
        ex.sendResponseHeaders(200, messageBytes.length);
        // Exp�dition du message
        OutputStream out = ex.getResponseBody();
        out.write(messageBytes);
        out.flush();
        out.close();
    }

    /**
     * Cr�ation d'une instance de la classe
     * @param address l'adresse de l'h�te h�bergeant le service
     * @param port le port associ� au service
     * @throws IOException si la cr�ation du serveur �choue
     */
    public HttpDaytime(String address, int port) throws IOException {
        // Cr�ation du serveur
        server = HttpServer.create(new InetSocketAddress(address, port), 0);
        // association du contexte et du handler au serveur
        server.createContext(CONTEXT, this);
        // l'ex�cuteur associ� au serveur fait que chaque requ�te 
        // sera trait�e dans un thread s�par�
        server.setExecutor(new Executor() {
            public void execute(Runnable command) {
                new Thread(command).start();
            }
        });
        // d�marrage du serveur
        server.start();
        System.out.println("server running");
    }

    public static void main(String[] args) {
        try {
            HttpDaytime daytime = new HttpDaytime("localhost", 8000);
        } catch (IOException ex) {
            Logger.getLogger(HttpDaytime.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

