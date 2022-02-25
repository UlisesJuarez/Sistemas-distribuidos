import java.net.Socket;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.DataInputStream;
import javax.net.ssl.SSLSocketFactory;

/**
 * build by Ulises Juárez
 */
public class ClientSSL {

    public static void main(String[] args) throws Exception {
        try {

            System.setProperty("javax.net.ssl.trustStore", "keystore_cliente.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "123456"); // PRopiedades sockets seguros CLIENTE
            SSLSocketFactory client = (SSLSocketFactory) SSLSocketFactory.getDefault();
            Socket connection = client.createSocket("localhost", 50000);
            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            DataInputStream input = new DataInputStream(connection.getInputStream());

            output.writeDouble(3.1416);

            Thread.sleep(1000);
            connection.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    static byte[] read_file(String file) throws Exception{
        FileInputStream f=new FileInputStream(file);
        byte[] buffer;
        try {
            buffer=new byte[f.available()];
            f.read(buffer);
        }finally{
            f.close();
        }
        return buffer;
    }
}