import java.rmi.Naming;

public class ClienteRMI {
    private static int N = 8;

    static void junta_matriz(float[][] resultante, float[][] parte, int ren, int col) {
        for (int i = 0; i < N / 4; i++)
            for (int j = 0; j < N / 4; j++)
                resultante[i + ren][j + col] = parte[i][j];
    }

    static float[][] separar_matriz(float[][] auxiliar, int inicio) {
        float[][] separada = new float[N / 4][N];
        for (int i = 0; i < N / 4; i++)
            for (int j = 0; j < N; j++)
                separada[i][j] = auxiliar[i + inicio][j];
        return separada;
    }

    static void imprimir(float matriz[][], int tam) {
        for (int i = 0; i < tam; i++) {
            for (int j = 0; j < tam; j++) {
                System.out.printf("%8.1f", matriz[i][j]);
            }
            System.out.println("");
        }
    }

    static void inicializar_trasponer(float[][] A, float[][] B, float[][] C) {
        // Inicializar
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                A[i][j] = i + 2 * j;
                B[i][j] = 3 * i - j;
                C[i][j] = 0;
            }
        }

        // Trasponer
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < i; j++) {
                float x = B[i][j];
                B[i][j] = B[j][i];
                B[j][i] = x;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        float[][] A = new float[N][N];
        float[][] B = new float[N][N];
        float[][] C = new float[N][N];
        float checksum = 0;

        // inicializamos y hacemos la traspuesta
        inicializar_trasponer(A, B, C);

        // separamos las matrices
        float[][] A1 = separar_matriz(A, 0);
        float[][] A2 = separar_matriz(A, N / 4);
        float[][] A3 = separar_matriz(A, 2 * N / 4);
        float[][] A4 = separar_matriz(A, 3 * N / 4);
        float[][] B1 = separar_matriz(B, 0);
        float[][] B2 = separar_matriz(B, N / 4);
        float[][] B3 = separar_matriz(B, 2 * N / 4);
        float[][] B4 = separar_matriz(B, 3 * N / 4);

        // creamos los ServidoresRMI
        InterfaceRMI n1 = (InterfaceRMI) Naming.lookup("rmi://localhost/multiplica");
        InterfaceRMI n2 = (InterfaceRMI) Naming.lookup("rmi://localhost/multiplica");
        InterfaceRMI n3 = (InterfaceRMI) Naming.lookup("rmi://localhost/multiplica");
        InterfaceRMI n4 = (InterfaceRMI) Naming.lookup("rmi://localhost/multiplica");

        // multiplicamos las matrices por nodo
        float[][] C1 = n1.mult_matrices(A1, B1);
        float[][] C2 = n1.mult_matrices(A1, B2);
        float[][] C3 = n1.mult_matrices(A1, B3);
        float[][] C4 = n1.mult_matrices(A1, B4);
        float[][] C5 = n2.mult_matrices(A2, B1);
        float[][] C6 = n2.mult_matrices(A2, B2);
        float[][] C7 = n2.mult_matrices(A2, B3);
        float[][] C8 = n2.mult_matrices(A2, B4);
        float[][] C9 = n3.mult_matrices(A3, B1);
        float[][] C10 = n3.mult_matrices(A3, B2);
        float[][] C11 = n3.mult_matrices(A3, B3);
        float[][] C12 = n3.mult_matrices(A3, B4);
        float[][] C13 = n4.mult_matrices(A4, B1);
        float[][] C14 = n4.mult_matrices(A4, B2);
        float[][] C15 = n4.mult_matrices(A4, B3);
        float[][] C16 = n4.mult_matrices(A4, B4);

        // juntamos y acomodamos las matrices
        junta_matriz(C, C1, 0, 0);
        junta_matriz(C, C2, 0, N / 4);
        junta_matriz(C, C3, 0, 2 * N / 4);
        junta_matriz(C, C4, 0, 3 * N / 4);
        junta_matriz(C, C5, N / 4, 0);
        junta_matriz(C, C6, N / 4, N / 4);
        junta_matriz(C, C7, N / 4, 2 * N / 4);
        junta_matriz(C, C8, N / 4, 3 * N / 4);
        junta_matriz(C, C9, 2 * N / 4, 0);
        junta_matriz(C, C10, 2 * N / 4, N / 4);
        junta_matriz(C, C11, 2 * N / 4, 2 * N / 4);
        junta_matriz(C, C12, 2 * N / 4, 3 * N / 4);
        junta_matriz(C, C13, 3 * N / 4, 0);
        junta_matriz(C, C14, 3 * N / 4, N / 4);
        junta_matriz(C, C15, 3 * N / 4, 2 * N / 4);
        junta_matriz(C, C16, 3 * N / 4, 3 * N / 4);

        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                checksum += C[i][j];

        if (N == 8) {
            System.out.println("Matriz A");
            imprimir(A, N);
            System.out.println("Matriz B traspuesta");
            imprimir(B, N);
            System.out.println("Matriz C");
            imprimir(C, N);
            System.out.printf("Checksum: %.2f", checksum);
        }
    }
}
