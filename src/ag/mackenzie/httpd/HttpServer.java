package ag.mackenzie.httpd;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

 
public class HttpServer {
	private int maxThreads = 10;
	private int initialThreads = 2;
	private long threadKeepalive = 60;
	public int port = 8900;
	public String webroot = System.getProperty("user.dir") + "/htdocs";
	private Logger logger = Logger.getLogger("ag.mackenzie.httpd");	
    private ThreadPoolExecutor pool = new ThreadPoolExecutor(
    				initialThreads, maxThreads,threadKeepalive, 
    				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	private ServerSocket serverSocket = null;
	
	
	public HttpServer() {
		serve();
	}
	
	public HttpServer(int port, String webroot, int initialThreads, int maxThreads, int threadKeepalive) {
		this.port = port;
		this.webroot = webroot;
		this.initialThreads = initialThreads;
		this.maxThreads = maxThreads;
		this.threadKeepalive = threadKeepalive;
		
		serve();
	}
	
    public void serve()  {
        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
        	logger.log(Level.SEVERE, "Could not bind to port " + this.port);
        	pool.shutdownNow();
            System.exit(-1);
        }
        logger.info("Now accepting connections on port " + this.port + " and serving files from " + webroot + ".");
 
        
        
        while (true) {
        	try {
				pool.execute(new HttpConnectionHandler(serverSocket.accept(), webroot, logger));
			} catch (IOException e) {
				logger.severe("Error when accepting client socket: " + e.getMessage());
			}
        }
 
        
    }
    
    public static void main(String[] argv) {
    	new HttpServer();
    }
}
