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
     * Classe impl�mentant le gestionnaire de requ�tes vers le service file
     */
    static class FileHandler implements HttpHandler {

        private static final String UPLOAD = "upload de %s effectu�e";
        private static final String DOWNLOAD = "download de %s effectu�e";
        private static final String WELCOME = "<h1 align='center'>Bonjour, donn�e ou prenez des fichier</h1>";
        
        private static final String TEST = "Bonjour </br> test arg   %s   ";
        
        static {
            System.setProperty("javax.net.ssl.keyStore", "kssrv.ks");
            System.setProperty("javax.net.ssl.keyStorePassword", "x4TRDf4JHY578pth");
            System.setProperty("javax.net.ssl.keyStoreType", "JCEKS");
            System.setProperty("javax.net.debug", "all");
        }

        
        /**
         * M�thode de gestion des requ�tes vers le service fileserver
         * @param he l'objet encapsulant la requ�te et la r�ponse
         * @throws IOException si le traitement de la requ�te ou de la r�ponse �choue
         */
        public void handle(HttpExchange he) throws IOException {
            // r�cup�ration des param�tres de requ�tes
            String query = he.getRequestURI().getQuery();
            
            // r�cup�ration des en-t�tes de la r�ponse HTTP
            Headers responseHeaders = he.getResponseHeaders();
            responseHeaders.set("Content-Type", "text/html");
            // Fabrication de la r�ponse (ordinaire ou personnalis�e)
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
            // pr�paration de la r�ponse
            he.sendResponseHeaders(200, responseBytes.length);
            // soumission de la r�ponse
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
         * M�thode d'aide pour la r�cup�ration des valeurs des param�tres du fichier � upload
         * @param query les param�tres de requ�te :
         * normalement une cha�ne de la forme uplaod=****
         * @return le message d'accueil personnalis�
         */
        private static String parseUploadQuery(String query){
        	String[] params = query.split("\\s*\\&\\s*");
            // r�cup�ration upload
            String upload = params[0].split("\\s*=\\s*")[1];
            return upload;
            // return String.format(UPLOAD, upload);
        }
        
        /**
         * M�thode d'aide pour la r�cup�ration des valeurs des param�tres du fichier � download
         * @param query les param�tres de requ�te :
         * normalement une cha�ne de la forme download=****
         * @return le message d'accueil personnalis�
         */
        private static String parseDownloadQuery(String query) {
            String[] params = query.split("\\s*\\&\\s*");
            // r�cup�ration download
            String download = params[0].split("\\s*=\\s*")[1];
            return download;
            // return String.format(DOWNLOAD, download);
        }
    }
    
    
    /**
     * Classe g�rant l'ordonnancement des r�actions aux requ�tes
     * Ici un nouveau thread est cr�� pour le traitement de chaque requ�te
     */
    static class ThreadPerTaskExecutor implements Executor {

        /**
         * M�thode impl�mentant la strat�gie d'ex�cution d'une nouvelle requ�te
         * @param command le Runnable � ex�cuter
         */
        public void execute(Runnable command) {
            // Instancier un nouveau thread d�di� � l'ex�cution de ce Runnable
            // et le d�marrer
            new Thread(command).start();
        }
    }
    
    // le contexte du service
    private static final String CONTEXT = "/file";
    // le serveur HTTPS
    private HttpsServer server;
    
    /**
     * Cr�ation du serveur
     * @param host le nom de l'h�te h�bergeant le service
     * @param port le num�ro de port associ� au service hello
     * @throws IOException si la cr�ation du serveur �choue
     */
    public HttpsFileServer(String host, int port) throws IOException, GeneralSecurityException {
        this.server = HttpsServer.create(new InetSocketAddress(host, port), 0);
        // Association du contexte au handler charg� de traiter les requ�tes
        server.createContext(CONTEXT, new FileHandler());
        // Configuration du contexte SSL
        this.server.setHttpsConfigurator(new HttpsConfigurator(SSLContext.getDefault()));
        // Configuration de l'ex�cuteur traitant les r�ponses
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
