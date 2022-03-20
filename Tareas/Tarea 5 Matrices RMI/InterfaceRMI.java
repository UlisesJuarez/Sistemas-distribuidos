import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceRMI extends Remote {

    public float [][] mult_matrices(float[][] A,float [][]B) throws RemoteException;
    
}
