class Matriz {


    public void calcula_checksum(int N) {
        int[][] A = new int[N][N];
        int[][] B = new int[N][N];
        int[][] C = new int[N][N];
        long t1 = System.currentTimeMillis();

        // inicializa las matrices A y B
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                A[i][j] = 2 * i - j;
                B[i][j] = i + 2 * j;
                C[i][j] = 0;
            }
        }

        // transponiendo la matriz B, la traspuesta queda en B

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int x = B[i][j];
                B[i][j] = B[i][j];
                B[j][i] = x;
            }
        }

        // multiplica la matriz A y la matriz B, el resultado se queda en la matriz C
        //los indices de la matriz B se intercambiaron con respecto al primer programa
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < N; k++) {
                    C[i][j] += A[i][k] * B[j][k];
                }
            }
        }

        long t2=System.currentTimeMillis();
        System.out.println("Tiempo: "+(t2-t1)+"ms");

    }
}
