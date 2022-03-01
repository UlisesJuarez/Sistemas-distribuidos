import java.io.*;
import java.net.*;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

public class Tarea2 {

    public static void main(String args[]) {

        System.setProperty("javax.net.ssl.keyStore", "keystore_servidor.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "1029384");
        System.setProperty("javax.net.ssl.trustStore", "keystore_cliente.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");

        if (args.length == 1)
            try {
                int numNodo = Integer.parseInt(args[0]);
                if (numNodo >= 0 && numNodo <= 5)
                    switch (numNodo) {
                        case 0:
                            short nInicial;
                            enviar(50001, nInicial = 0); // puertoCliente | valor inicial
                            recibir(50001, 50000); // puertoCliente | puertoServidor
                            break;
                        case 1:
                            recibir(50002, 50001);
                            break;
                        case 2:
                            recibir(50003, 50002);
                            break;
                        case 3:
                            recibir(50004, 50003);
                            break;
                        case 4:
                            recibir(50005, 50004);
                            break;
                        case 5:
                            recibir(50000, 50005);
                            break;
                        default:
                            System.exit(0);
                            break;
                    }
                else
                    System.out.println("Numero de nodo no valido");
            } catch (NumberFormatException e) {
                System.out.println("Escriba un numero");
            }
    }

    public static void recibir(int puertoC, int puertoS) {
            try {
                SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
                ServerSocket servidor = socket_factory.createServerSocket(puertoS);
                for (;;){
                    Socket conexion = servidor.accept();
                    DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                    short resultado = entrada.readShort();
                    conexion.close();
                    if (resultado >= 500 && puertoS == 50000) // puertoS = 50000 se refiere al servidor nodo 0
                        break;
                    else {
                        resultado++;
                        System.out.println(resultado);
                        enviar(puertoC, resultado);
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
    }

    public static void enviar(int puerto, short num) {
        try {
            for (;;)
                try {
                    SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
                    Socket conexion = cliente.createSocket("localhost", puerto);
                    DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                    salida.writeShort(num);
                    Thread.sleep(50);
                    conexion.close();
                    break;
                } catch (Exception ex) {
                    Thread.sleep(100);
                }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
}
