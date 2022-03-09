
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Matrices {

    static double checksum = 0;
    static int N = 8;
    static double[][] A = new double[N][N];
    static double[][] B = new double[N][N];
    static double[][] C = new double[N][N];

    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            int nodo = Integer.valueOf(args[0]);
            switch (nodo) {
                case 0:
                    cliente();
                    break;
                case 1:
                    servidor(nodo);
                    break;
                case 2:
                    servidor(nodo);
                    break;
                case 3:
                    servidor(nodo);
                    break;
            }
        }
    }

    static void cliente() throws IOException, InterruptedException {
        // Nuevo
        Socket nodo1 = new Socket();
        Socket nodo2 = new Socket();
        Socket nodo3 = new Socket();

        for (;;)
                try {
            nodo1 = new Socket("localhost", 50000);
            nodo2 = new Socket("localhost", 50001);
            nodo3 = new Socket("localhost", 50002);
            break;
        } catch (Exception e) {
            Thread.sleep(100);
        }
        // fin nuevo

        Worker w[] = new Worker[3];

        // inicializamos las matrices A y B
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                A[i][j] = i + 5 * j;
                B[i][j] = 5 * i - j;
                C[i][j] = 0;
            }
        }

        System.out.println("Matriz A:");
        printArray(A, N);

        System.out.println("Matriz B normal:");
        printArray(B, N);
        // transponemos la matriz B, la matriz traspuesta queda en B
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < i; j++) {
                double x = B[i][j];
                B[i][j] = B[j][i];
                B[j][i] = x;
            }
        }
        System.out.println("Matriz B transpuesta:");
        printArray(B, N);

        // Se mandan a servidor
        w[0] = new Worker(nodo1);
        w[0].start();
        w[1] = new Worker(nodo2);
        w[1].start();
        w[2] = new Worker(nodo3);
        w[2].start();

        for (int i = 0; i < 3; i++) {
            w[i].join();
        }

        // Parte del nodo 0
        double ai[][] = new double[N / 2][N];
        double bi[][] = new double[N / 2][N];
        double ci[][] = new double[N / 2][N];

        ai = dividirMatriz(A, false);
        bi = dividirMatriz(B, false);
        ci = productoMatrices(ai, bi);

        for (int i = 0; i < N / 2; i++) {
            for (int j = 0; j < N / 2; j++) {
                C[i + N / 2][j + N / 2] = ci[i][j];
            }
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                checksum += C[i][j];
            }
        }

        System.out.println("Matriz C");
        printArray(C, N);

        System.out.println("check sum = " + checksum);

        nodo1.close();
        nodo2.close();
        nodo3.close();
    }

    static void servidor(int nodo) throws Exception {
        ServerSocket servidor = null;
        // Esto se hizo para probarlo de manera local
        
        switch(nodo){
            case 1:
                servidor = new ServerSocket(50000);
                break;
            case 2:
                servidor = new ServerSocket(50001);
                break;
            case 3:
                servidor = new ServerSocket(50002);
                break;
        }
        

        Socket connection = servidor.accept();

        DataInputStream input = new DataInputStream(connection.getInputStream());
        DataOutputStream output = new DataOutputStream(connection.getOutputStream());

        output.writeInt(nodo);

        double ai[][] = receiverArray(N / 2, N, input);
        double bi[][] = receiverArray(N / 2, N, input);
        double ci[][] = new double[N / 2][N / 2];

        ci = productoMatrices(ai, bi);

        senderArray(ci, N / 2, N / 2, output);

        connection.close();
    }

    static double[][] productoMatrices(double[][] A, double[][] B) {
        double ci[][] = new double[N / 2][N / 2];
        for (int i = 0; i < N / 2; i++)
            for (int j = 0; j < N / 2; j++)
                for (int k = 0; k < N; k++)
                    ci[i][j] += A[i][k] * B[j][k];
        return ci;
    }

    static class Worker extends Thread {

        Socket connection;
        int nodo;

        Worker(Socket connection) {
            this.connection = connection;
        }

        public void run() {
            try {
                DataInputStream input = new DataInputStream(connection.getInputStream()); // Entrada
                DataOutputStream output = new DataOutputStream(connection.getOutputStream()); // Salida
                double ai[][] = new double[N / 2][N];
                double bi[][] = new double[N / 2][N];

                int nodo = input.readInt();

                switch (nodo) {
                    case 1:
                        ai = dividirMatriz(A, true);
                        bi = dividirMatriz(B, true);
                        break;
                    case 2:
                        ai = dividirMatriz(A, true);
                        bi = dividirMatriz(B, false);
                        break;
                    case 3:
                        ai = dividirMatriz(A, false);
                        bi = dividirMatriz(B, true);
                        break;
                }

                //Se envia la matriz a los server
                senderArray(ai, N / 2, N, output);
                senderArray(bi, N / 2, N, output);

                //Se recibe el producto de la matriz
                double ci[][] = receiverArray(N / 2, N / 2, input);

                // Se van llenando los valores de la matriz C
                switch (nodo) {
                    case 1:
                        for (int i = 0; i < N / 2; i++)
                            for (int j = 0; j < N / 2; j++) 
                                C[i][j] = ci[i][j];
                        break;
                    case 2:
                        for (int i = 0; i < N / 2; i++)
                            for (int j = 0; j < N / 2; j++)
                                C[i][j + N / 2] = ci[i][j];
                        break;
                    case 3:
                        for (int i = 0; i < N / 2; i++)
                            for (int j = 0; j < N / 2; j++)
                                C[i + N / 2][j] = ci[i][j];
                        break;
                    default:
                        break;
                }
                connection.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    static double[][] dividirMatriz(double[][] matriz, boolean superior) {
        double[][] aux = new double[N / 2][N];
        if (superior) 
            for (int i = 0; i < N / 2; i++)
                for (int j = 0; j < N; j++) 
                    aux[i][j] = matriz[i][j];
        else
            for (int i = 0; i < N / 2; i++) 
                for (int j = 0; j < N; j++) 
                    aux[i][j] = matriz[i + N / 2][j];
        return aux;
    }

    static void printArray(double aux[][], int N) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++)
                System.out.print(" " + aux[i][j] + " ");
            System.out.println("");
        }
    }

    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception {
        while (longitud > 0) {
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }

    static void senderArray(double aux[][], int rows, int cols, DataOutputStream output) {
        for (int i = 0; i < rows; i++) {
            ByteBuffer b = ByteBuffer.allocate(cols * 8);
            for (int j = 0; j < cols; j++)
                b.putDouble(aux[i][j]);
            byte[] bytes = b.array();
            try {
                output.write(bytes); // Se envian los datos
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    static double[][] receiverArray(int rows, int cols, DataInputStream input) {
        double aux[][] = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            byte[] bytes = new byte[cols * 8];
            try {
                read(input, bytes, 0, cols * 8);
                ByteBuffer b = ByteBuffer.wrap(bytes);
                for (int j = 0; j < cols; j++)
                    aux[i][j] = b.getDouble();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return aux;
    }
}
