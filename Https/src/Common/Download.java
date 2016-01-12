package Common;


import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.swing.AbstractAction;

import Client.IntHumMach;
import Client.KeyStoreClient;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.SSLContext;

import org.omg.CORBA.portable.InputStream;

public class Download extends AbstractAction {
	String namefile = null;
	public Download(String texte){
		super(texte);
		namefile=texte;
	}
	
	public void actionPerformed(ActionEvent e) { 
		
		IntHumMach.affichetext();
		namefile=IntHumMach.text;
		
		//try {
		//	System.out.println("enter send get");
			//sendGet();
		//} catch (Exception e1) {
			// TODO Auto-generated catch block
		//	e1.printStackTrace();
		//}
		
		/*
		try {
		    URL myURL = new URL("https://localhost:1337/file");
		    
		    KeyStore keyStore = KeyStore.getInstance("JCEKS");
			
		    String algorithm = KeyManagerFactory.getDefaultAlgorithm();
		    KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
		    kmf.init(keyStore);
			
		    SSLContext context = SSLContext.getInstance("TLS");
		    context.init(kmf.getKeyManagers(), null, null);
		
		    
		    HttpsURLConnection myURLConnection = (HttpsURLConnection) myURL.openConnection();
		    System.out.println("co");
		    //myURLConnection.connect();
		    myURLConnection.setSSLSocketFactory(context.getSocketFactory());
		    System.out.println("gij");
		    
		} 
		catch (MalformedURLException p) { 
		    // new URL() failed
		    p.printStackTrace();
		} 
		catch (IOException p) {   
		    // openConnection() failed
		    p.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		
		/*
		URL url =new URL("https://localhost:1337/file");
		HttpsURLConnection httpsurlcon = new HttpsURLConnection(url);
		*/
		
		/*
		System.out.println("le nom du fichier est : " + namefile);
		File fi = new File(namefile);
		File fout= new File("client/"+fi.getName());
		try {
			fout.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ishere(fi);
		copyFile(fi,fout);
		*/
		File fi = new File(namefile);
		try {
			copyFile(fi);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	} 
	
	
	// HTTP GET request
		private void sendGet() throws Exception {

			final String url = "https://localhost:1337/file?download="+namefile;
			//final String url = "http://www.google.com/search?q=mkyong";
			
			URL obj = new URL(url);
			final HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			
			System.out.println("connection effectué");
			
			// optional default is GET
			con.setRequestMethod("GET");

			System.out.println("type de request get");
			
			//add request header
			//con.setRequestProperty("User-Agent", USER_AGENT);

			final int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
			System.out.println(response.toString());

		}
	
	
	public void down() throws MalformedURLException, IOException{
		System.out.println("le nom du fichier est : " + namefile);
		File fi = new File(namefile);
		File fout= new File("client/"+fi.getName());
		try {
			fout.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ishere(fi);
		copyFile(fi);
	}
	
	public String getLink(){
		return namefile;
	}
	
	
	public boolean ishere(File file){
		File f =new File("server/" + file.getName());
		if(f.exists()){
			System.out.println("exist");
			return true;
		}else {
			System.out.println("doesn't exist");
			return false;
		}
	}

			public  void copyFile(File source1) throws MalformedURLException, IOException{
				
					/*File source =new File("server/" + source1.getName());
					File dest =new File("client/" + dest1.getName());
				try{
					// Declaration et ouverture des flux
					java.io.FileInputStream sourceFile = new java.io.FileInputStream(source);
			 
					try{
						java.io.FileOutputStream destinationFile = null;
			 
						try{
							destinationFile = new FileOutputStream(dest);
			 
							// Lecture par segment de 0.5Mo 
							byte buffer[] = new byte[512 * 1024];
							int nbLecture;
			 
							while ((nbLecture = sourceFile.read(buffer)) != -1){
								destinationFile.write(buffer, 0, nbLecture);
							}
						} finally {
							destinationFile.close();
						}
					} finally {
						sourceFile.close();
					}
				} catch (IOException e){
					e.printStackTrace();
				}

			}*/
				System.out.println("fdv");
				URL urlimage = new URL(namefile);
				ReadableByteChannel rbc = Channels.newChannel(urlimage.openStream());
				FileOutputStream fos = new FileOutputStream("client/image.jpg");
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				}
	
}
