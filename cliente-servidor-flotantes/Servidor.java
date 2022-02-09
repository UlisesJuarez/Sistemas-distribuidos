import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class Servidor{
    public static void main(String[] args) {
        try {
            ServerSocket servidor=new ServerSocket(50000);
            Socket conexion=servidor.accept();

            DataOutputStream salida=new DataOutputStream(conexion.getOutputStream());
            DataInputStream entrada=new DataInputStream(conexion.getInputStream());     
            
            int n=entrada.readInt();
            System.out.println(n);

            double x=entrada.readDouble();
            System.out.println(x);

            byte[] buffer=new byte[4];
            read(entrada,buffer,0,4);
            System.out.println(new String(buffer,"UTF-8"));

            salida.write("HOLA".getBytes());
            
        } catch (Exception e) {
            System.out.println(e);
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
}