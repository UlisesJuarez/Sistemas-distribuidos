import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MatrizDist {
    static int N = 100;
    static double[][] A = new double[N][N];
    static double[][] B = new double[N][N];
    static double[][] C = new double[N][N];

    public static void server(int portS, int nodo) {
        double[][] C1 = new double[N / 2][N / 2];
        double[][] C2 = new double[N / 2][N];
        double[][] C3 = new double[N][N / 2];
        double[][] C4 = new double[N][N];
        try {
            ServerSocket server = new ServerSocket(portS);
            for (;;) {
                Socket connection = server.accept();
                DataInputStream input = new DataInputStream(connection.getInputStream());
                switch (nodo) {
                    case 1:// 10.- Recibir la matriz C1 del nodo 1
                    for (int i = 0; i < (N / 2); i++) {
                        for (int j = 0; j < (N / 2); j++) {
                            C1[i][j] = input.readDouble(); 
                            C[i][j] = C1[i][j];
                        }
                    }

                        break;
                    case 2:// 11.- Recibir la matriz C2 del nodo 2
                    for (int i = 0; i < (N / 2); i++) {
                        for (int j = (N / 2); j < N; j++) {
                            C2[i][j] = input.readDouble();
                            C[i][j] = C2[i][j];
                        }
                    }

                        break;
                    case 3:// 12.- Recibir la matriz C3 del nodo 3
                    for (int i = (N / 2); i < N; i++) {
                        for (int j = 0; j < (N / 2); j++) {
                            C3[i - (N / 2)][j] = input.readDouble();
                            C[i][j] = C3[i - (N / 2)][j];
                        }
                    }

                        break;

                    default:
                        break;
                }
                connection.close();
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    public static void client(int port, int nodo) {
        double[][] A1 = new double[N / 2][N];
        double[][] A2 = new double[N / 2][N];
        double[][] B1 = new double[N / 2][N];
        double[][] B2 = new double[N / 2][N];
        try {
            for (;;)
                try {
                    Socket connection = new Socket("localhost", port);
                    DataOutputStream output = new DataOutputStream(connection.getOutputStream());
                    switch (nodo) {
                        case 1:
                            for (int i = 0; i < (N / 2); i++) {
                                for (int j = 0; j < N; j++) {
                                    A1[i][j] = A[i][j];
                                    B1[i][j] = B[i][j];
                                    output.writeDouble(A1[i][j]); // 3.- Enviar la matriz A1 al nodo 1
                                    output.writeDouble(B1[i][j]); // 4.- Enviar la matriz B1 al nodo 1
                                }
                            }
                            break;
                        case 2:
                            for (int i = 0; i < (N / 2); i++) {
                                for (int j = 0; j < N; j++) {
                                    A1[i][j] = A[i][j];
                                    output.writeDouble(A1[i][j]); // 5.- Enviar la matriz A1 al nodo 2.
                                }
                            }
                            for (int i = (N / 2); i < N; i++) {
                                for (int j = 0; j < N; j++) {
                                    B2[i - (N / 2)][j] = B[i][j];
                                    output.writeDouble(B2[i - (N / 2)][j]); // 6.- Enviar la matriz B2 al nodo 2.
                                }
                            }
                            break;
                        case 3:
                            for (int i = (N / 2); i < N; i++) {
                                for (int j = 0; j < N; j++) {
                                    A2[i - (N / 2)][j] = A[i][j];
                                    output.writeDouble(A2[i - (N / 2)][j]); // 7.- Enviar la matriz A2 al nodo 3.
                                }
                            }
                            for (int i = 0; i < (N / 2); i++) {
                                for (int j = 0; j < N; j++) {
                                    B1[i][j] = B[i][j];
                                    output.writeDouble(B1[i][j]);// 8.- Enviar la matriz B1 al nodo 3.
                                }
                            }
                            break;
                        default:
                            break;
                    }
                    output.writeShort(nodo);
                    Thread.sleep(50);
                    connection.close();
                    break;
                } catch (Exception ex) {
                    Thread.sleep(100);
                }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    public static void initializeArray() {
        // 1.- inicializamos nuestras matrices A y B
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                A[i][j] = i + 5 * j;
                B[i][j] = 5 * i - j;
            }
        }
        // 2.- transponemos la matriz B y dejamos la transpuesta en B
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < i; j++) {
                double x = B[i][j];
                B[i][j] = B[j][i];
                B[j][i] = x;
            }
        }
        // if (N == 8)
        // printArray(A, N);
        // else
        // System.out.println("La matriz es muy grande para imprimir");

    }

    private static void printArray(double[][] mat, int N) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(mat[i][j] + " ");
            }
            System.out.println("");
        }
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                int nodo = Integer.parseInt(args[0]);
                if (nodo >= 0 && nodo <= 3) {
                    switch (nodo) {
                        case 0:
                            System.out.println("Client running");
                            initializeArray();
                            client(50001, nodo = 1);
                            client(50002, nodo = 2);
                            client(50003, nodo = 3);
                            break;
                        case 1:
                            System.out.println("Server running on port 5001");
                            server(50001, nodo);
                            break;
                        case 2:
                            System.out.println("Server running on port 5002");
                            server(50002, nodo);
                            break;
                        case 3:
                            System.out.println("Server running on port 5003");
                            server(50003, nodo);
                            break;

                        default:
                            System.exit(0);
                            break;
                    }
                } else {
                    System.out.println("Por favor ingrese un nodo entre el 0-3");
                }

            } catch (NumberFormatException e) {
                System.out.println("Por favor escriba un numero");
            }
        } else {
            System.out.println("Por favor ingrese un parametro entre 0-3");
        }
    }
}
