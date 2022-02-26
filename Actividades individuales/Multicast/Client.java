import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Client {

    public static void main(String[] args) throws Exception {
        try {

            

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    static byte[] recibe_mensaje(MulticastSocket socket, int longitud_mensaje) throws IOException{
        byte[] buffer=new byte[longitud_mensaje];
        DatagramPacket paquete=new DatagramPacket(buffer,buffer.length);
        socket.receive(paquete);
        
        return paquete.getData();
    }

}
