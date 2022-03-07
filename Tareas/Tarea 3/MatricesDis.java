import java.lang.Thread;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.io.DataOutputStream;
import java.io.DataInputStream;

public class MatricesDis {
    static double checksum = 0;
    static int N = 4;
    static double[][] A = new double[N][N];
    static double[][] B = new double[N][N];
    static double[][] C = new double[N][N];

    static class Worker extends Thread {
        Socket connection;
        int nodo;

        Worker(Socket connection) {
            this.connection = connection;
        }

        public void run() {
            try {
                DataInputStream input = new DataInputStream(connection.getInputStream());
                DataOutputStream output = new DataOutputStream(connection.getOutputStream());
                double ai[][] = new double[N / 2][N];
                double bi[][] = new double[N / 2][N];

                int nodo = input.readInt(); 
                switch (nodo) {
                    case 1:
                        for (int i = 0; i < N / 2; i++) {
                            for (int j = 0; j < N; j++) {
                                ai[i][j] = A[i][j];
                                bi[i][j] = B[i][j];
                            }
                        }
                        break;
                    case 2:
                        for (int i = 0; i < N / 2; i++) {
                            for (int j = 0; j < N; j++) {
                                ai[i][j] = A[i][j];
                                bi[i][j] = B[i + N / 2][j];
                            }
                        }
                        break;
                    case 3:
                        for (int i = 0; i < N / 2; i++) {
                            for (int j = 0; j < N; j++) {
                                ai[i][j] = A[i + N / 2][j];
                                bi[i][j] = B[i][j];
                            }
                        }
                        break;
                    default:
                        break;
                }
                senderArray(ai, N / 2, N, output);
                senderArray(bi, N / 2, N, output);

                double ci[][] = receiverArray(N / 2, N / 2, input);
                switch (nodo) {
                    case 1:
                        for (int i = 0; i < N / 2; i++) {
                            for (int j = 0; j < N / 2; j++) {
                                C[i][j] = ci[i][j];
                            }
                        }
                        break;
                    case 2:
                        for (int i = 0; i < N / 2; i++) {
                            for (int j = 0; j < N / 2; j++) {
                                C[i][j + N / 2] = ci[i][j];
                            }
                        }
                        break;
                    case 3:
                        for (int i = 0; i < N / 2; i++) {
                            for (int j = 0; j < N / 2; j++) {
                                C[i + N / 2][j] = ci[i][j];
                            }
                        }
                        break;

                    default:
                        break;
                }

                for (int i = 0; i < N / 2; i++) {
                    for (int j = 0; j < N / 2; j++) {
                        C[i + N / 2][j + N / 2] = ci[i][j];
                    }
                }
                input.close();
                output.close();
                connection.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    static void printArray(double aux[][], int N) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(" "+aux[i][j]+" ");
            }
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
            for (int j = 0; j < cols; j++) {
                b.putDouble(aux[i][j]);
            }
            byte[] bytes = b.array();
            try {
                output.write(bytes);
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
                ByteBuffer bf = ByteBuffer.wrap(bytes);
                for (int j = 0; j < cols; j++)
                    aux[i][j] = bf.getDouble();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return aux;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Por favor escriba el numero del nodo");
            System.exit(0);
        }
        int nodo = Integer.valueOf(args[0]);
        if (nodo == 0) {
            ServerSocket server = new ServerSocket(50000);
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

            for (int i = 0; i < 3; i++) {
                Socket client = server.accept();
                w[i] = new Worker(client);
                w[i].start();
            }
            for (int i = 0; i < 3; i++) {
                w[i].join();
            }

            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    checksum += C[i][j];
                }
            }
            System.out.println("Matriz C");
            printArray(C, N);

            System.out.println("check sum = " + checksum);

            server.close();
        } else {
            Socket connection = null;

            for (;;)
                try {
                    connection = new Socket("localhost", 50000);
                    break;
                } catch (Exception e) {
                    Thread.sleep(100);
                }

            DataInputStream input = new DataInputStream(connection.getInputStream());
            DataOutputStream output = new DataOutputStream(connection.getOutputStream());

            output.writeInt(nodo);

            double ai[][] = receiverArray(N / 2, N, input);
            double bi[][] = receiverArray(N / 2, N, input);
            double ci[][] = new double[N / 2][N / 2];

            for (int i = 0; i < N / 2; i++)
                for (int j = 0; j < N / 2; j++)
                    for (int k = 0; k < N; k++)
                        ci[i][j] += ai[i][k] * bi[j][k];

            senderArray(ci, N / 2, N / 2, output);

            input.close();
            output.close();
            connection.close();
        }
    }
}