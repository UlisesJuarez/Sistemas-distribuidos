import java.net.Socket;
import java.net.ServerSocket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import javax.net.ssl.SSLServerSocketFactory;

/**
 * build by Ulises Juárez
 */
public class ServerSSL {

    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.ssl.keyStore", "keystore_servidor.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "1234567");         //propiedades sockets seguros Servidor
        SSLServerSocketFactory socket_factory=(SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
        ServerSocket socket_server=socket_factory.createServerSocket(50000);
        Socket connection= socket_server.accept();
        
        DataOutputStream output=new DataOutputStream(connection.getOutputStream());
        DataInputStream input=new DataInputStream(connection.getInputStream());

        double x=input.readDouble();
        System.out.println(x);

        connection.close();
    }
}