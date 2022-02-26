import java.nio.ByteBuffer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
public class Server {

    public static void main(String[] args) throws Exception{
        try {
            System.setProperty("java.net.preferIPv4Stack","true");

            envia_mensaje("hola".getBytes(),"230.0.0.0", 50000);

            ByteBuffer b=ByteBuffer.allocate(5*8);

            b.putDouble(1.1);
            b.putDouble(1.2);
            b.putDouble(1.3);
            b.putDouble(1.4);
            b.putDouble(1.5);

            envia_mensaje(b.array(),"230.0.0.0",50000);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
    }
        
    static void envia_mensaje(byte[] buffer, String ip,int puerto)throws IOException
    {
        DatagramSocket socket = new DatagramSocket();
        InetAddress grupo= InetAddress.getByName(ip);
        DatagramPacket paquete=new DatagramPacket(buffer,buffer.length,grupo,puerto);
        socket.send(paquete);
        socket.close();
    }
}
