import java.rmi.Naming;

/**
 * ServidorRMI
 */
public class ServidorRMI {

    public static void main(String[] args) throws Exception{
        
        String url="rmi://localhost/prueba";
        Runtime.getRuntime().exec("rmiregistry");
        ClaseRMI obj=new ClaseRMI();

        Naming.rebind(url, obj);
    }
}