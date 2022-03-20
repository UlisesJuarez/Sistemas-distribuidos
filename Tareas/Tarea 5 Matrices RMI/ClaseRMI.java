import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClaseRMI extends UnicastRemoteObject implements InterfaceRMI{
    public static int N=8;

    public ClaseRMI() throws RemoteException {
        super();
    }
    public float[][] mult_matrices(float[][] A,float [][]B) throws RemoteException{
        float[][]C=new float[N/4][N/4];
        for(int i=0;i<N/4;i++)
            for (int j = 0; j < N/4; j++)
                for (int k = 0; k <N; k++)
                    C[i][j]+=A[i][k]*B[j][k];
        return C;
    }
}