package Serveur;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;

import Common.Download;
import Common.Upload;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;


public class HttpsFileServer {

	
	
    
    /**
     * Classe implémentant le gestionnaire de requètes vers le service file
     */
    static class FileHandler implements HttpHandler {

        private static final String UPLOAD = "upload de %s effectuée";
        private static final String DOWNLOAD = "download de %s effectuée";
        private static final String WELCOME = "<h1 align='center'>Bonjour, donnée ou prenez des fichier</h1>";
        
        private static final String TEST = "Bonjour </br> test arg   %s   ";
        
        static {
            System.setProperty("javax.net.ssl.keyStore", "kssrv.ks");
            System.setProperty("javax.net.ssl.keyStorePassword", "x4TRDf4JHY578pth");
            System.setProperty("javax.net.ssl.keyStoreType", "JCEKS");
            System.setProperty("javax.net.debug", "all");
        }

        
        /**
         * Méthode de gestion des requètes vers le service fileserver
         * @param he l'objet encapsulant la requète et la réponse
         * @throws IOException si le traitement de la requète ou de la réponse échoue
         */
        public void handle(HttpExchange he) throws IOException {
            // récupération des paramètres de requètes
            String query = he.getRequestURI().getQuery();
            
            // récupération des en-têtes de la réponse HTTP
            Headers responseHeaders = he.getResponseHeaders();
            responseHeaders.set("Content-Type", "text/html");
            // Fabrication de la réponse (ordinaire ou personnalisée)
            String response = WELCOME;
           
            //response = String.format(TEST, parseQuery(query));
            
            File filein,fileout;
           
            
            if (query != null) { 
            	if(parseQuery(query).equals("upload")){
            		response = parseUploadQuery(query);
            		Upload up = new Upload(response);
            		up.up();
            	 }
            		
                if(parseQuery(query).equals("download")){
                	response = parseDownloadQuery(query);
                	Download down = new Download(response);
                	down.down();
                }
            }
            byte[] responseBytes = response.getBytes();
            // préparation de la réponse
            he.sendResponseHeaders(200, responseBytes.length);
            // soumission de la réponse
            OutputStream out = he.getResponseBody();
            out.write(responseBytes);
            out.flush();
            out.close();
        }
        
        
        private static String parseQuery(String query){
        	String[] params = query.split("\\s=*\\s*");
        	String querry = params[0].split("\\s*=\\s*")[0];
        	return querry;
        }
        
        private static String parseFileQuery(String query){
        	String[] params = query.split("\\s=*\\s*");
        	String querry = params[1].split("\\s*=\\s*")[0];
        	return querry;
        }
        
        /**
         * Méthode d'aide pour la récupération des valeurs des paramètres du fichier à upload
         * @param query les paramètres de requète :
         * normalement une chaîne de la forme uplaod=****
         * @return le message d'accueil personnalisé
         */
        private static String parseUploadQuery(String query){
        	String[] params = query.split("\\s*\\&\\s*");
            // récupération upload
            String upload = params[0].split("\\s*=\\s*")[1];
            return upload;
            // return String.format(UPLOAD, upload);
        }
        
        /**
         * Méthode d'aide pour la récupération des valeurs des paramètres du fichier à download
         * @param query les paramètres de requète :
         * normalement une chaîne de la forme download=****
         * @return le message d'accueil personnalisé
         */
        private static String parseDownloadQuery(String query) {
            String[] params = query.split("\\s*\\&\\s*");
            // récupération download
            String download = params[0].split("\\s*=\\s*")[1];
            return download;
            // return String.format(DOWNLOAD, download);
        }
    }
    
    
    /**
     * Classe gérant l'ordonnancement des réactions aux requètes
     * Ici un nouveau thread est créé pour le traitement de chaque requète
     */
    static class ThreadPerTaskExecutor implements Executor {

        /**
         * Méthode implémentant la stratégie d'exécution d'une nouvelle requète
         * @param command le Runnable à exécuter
         */
        public void execute(Runnable command) {
            // Instancier un nouveau thread dédié à l'exécution de ce Runnable
            // et le démarrer
            new Thread(command).start();
        }
    }
    
    // le contexte du service
    private static final String CONTEXT = "/file";
    // le serveur HTTPS
    private HttpsServer server;
    
    /**
     * Création du serveur
     * @param host le nom de l'hôte hébergeant le service
     * @param port le numéro de port associé au service hello
     * @throws IOException si la création du serveur échoue
     */
    public HttpsFileServer(String host, int port) throws IOException, GeneralSecurityException {
        this.server = HttpsServer.create(new InetSocketAddress(host, port), 0);
        // Association du contexte au handler chargé de traiter les requètes
        server.createContext(CONTEXT, new FileHandler());
        // Configuration du contexte SSL
        this.server.setHttpsConfigurator(new HttpsConfigurator(SSLContext.getDefault()));
        // Configuration de l'exécuteur traitant les réponses
        this.server.setExecutor(new ThreadPerTaskExecutor());
        // Lancement du serveur
        this.server.start();
        System.out.println("En attente de connection!..");
    }
    
    public static void main(String[] args) {
  
    	try {
            new HttpsFileServer("localhost", 1337);
        } catch (IOException ex) {
            Logger.getLogger(HttpsFileServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(HttpsFileServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
}
