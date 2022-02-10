import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

class Cliente{
    public static void main(String[] args) {
        try {
            Socket connection=new Socket("localhost",50000);
            DataOutputStream output=new DataOutputStream(connection.getOutputStream());
            DataInputStream input=new DataInputStream(connection.getInputStream());

            output.writeInt(123);

            output.writeDouble(1234567890.1234567890);

            output.write("hola".getBytes());

            byte[] buffer =new byte[4];
            read(input,buffer,0,4);
            System.out.println(new String(buffer,"UTF-8"));

            ByteBuffer b=ByteBuffer.allocate(5*8);
            b.putDouble(1.1);
            b.putDouble(1.2);
            b.putDouble(1,3);
            b.putDouble(1.4);
            b.putDouble(1.5);

            byte[] a=b.array();
            output.write(a);

            Thread.sleep(1000);
            
            connection.close();
            
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