import java.rmi.Naming;

public class ServidorRMI {
    public static void main(String[] args) throws Exception{
        Naming.rebind("rmi://localhost/multiplica", new ClaseRMI());
    }
}
