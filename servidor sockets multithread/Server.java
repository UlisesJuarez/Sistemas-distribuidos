import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

class Server{
    
    static class Worker extends Thread{
        Socket connection;
        Worker(Socket connection){
            this.connection=connection;
        }
        public void run(){
            try {
                DataOutputStream output=new DataOutputStream(connection.getOutputStream());
                DataInputStream input=new DataInputStream(connection.getInputStream());

                int n=input.readInt();
                System.out.println(n);

                double x=input.readDouble();
                System.out.println(x);

                byte[] buffer=new byte[4];
                read(input, buffer,0,4);
                System.out.println(new String(buffer,"UTF-8"));

                output.write("HOLA".getBytes());

                byte[] a=new byte[5*8];
                read(input,a,0,5*8);

                ByteBuffer b=ByteBuffer.wrap(a);
                for (int i = 0; i < 5; i++) System.out.println(b.getDouble());

                connection.close();

                
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    static void read(DataInputStream f,byte[] b,int posicion, int longitud) throws Exception
    {
        while(longitud>0){
            try {
                int n=f.read(b,posicion,longitud);
                posicion +=n;
                longitud -=n;
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
    public static void main(String[] args) {
        try {
            ServerSocket server=new ServerSocket(50000);
            for(;;)
            {
                Socket connection=server.accept();
                Worker w=new Worker(connection);
                w.start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}