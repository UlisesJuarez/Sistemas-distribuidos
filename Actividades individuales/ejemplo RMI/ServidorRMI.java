import java.rmi.Naming;

/**
 * ServidorRMI
 */
public class ServidorRMI {

    public static void main(String[] args) throws Exception{
        
        Naming.rebind("rmi://localhost/prueba",new ClaseRMI());
    }
}