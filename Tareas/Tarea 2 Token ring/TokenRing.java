import java.io.*;
import java.net.*;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

public class TokenRing {

    public static void enviar(int puerto, short num) {

        try {
            for (;;) {
                try {

                    // Socket conexion = new Socket("localhost",puerto);
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
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

    }

    public static void main(String args[]) {

        System.setProperty("javax.net.ssl.keyStore", "keystore_servidor.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "1029384");
        System.setProperty("javax.net.ssl.trustStore", "keystore_cliente.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "102938");

        if (args.length == 1) {
            try {

                int numNodo = Integer.parseInt(args[0]);

                if (numNodo >= 0) {

                    if (numNodo <= 5) {
                        int puertoCliente = 0;
                        int puertoServidor = 0;
                        switch (numNodo) {
                            case 0:
                                puertoServidor = 50000;
                                puertoCliente = 50001;
                                short nInicial = 0;

                                enviar(puertoCliente, nInicial);

                                try {
                                    // ServerSocket servidor=new ServerSocket(puertoServidor);
                                    SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory
                                            .getDefault();
                                    ServerSocket servidor = socket_factory.createServerSocket(puertoServidor);
                                    for (;;) {
                                        Socket conexion = servidor.accept();
                                        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                                        short resultado = entrada.readShort();
                                        conexion.close();

                                        if (resultado >= 500) {
                                            break;
                                        } else {
                                            resultado++;
                                            System.out.println(resultado);
                                            enviar(puertoCliente, resultado);
                                        }
                                    }
                                    servidor.close();
                                    System.exit(0);

                                } catch (Exception ex) {
                                    System.out.println(ex.toString());
                                }

                                break;
                            case 1:
                                puertoServidor = 50001;
                                puertoCliente = 50002;
                                try {

                                    // ServerSocket servidor=new ServerSocket(puertoServidor);
                                    SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory
                                            .getDefault();
                                    ServerSocket servidor = socket_factory.createServerSocket(puertoServidor);

                                    for (;;) {
                                        Socket conexion = servidor.accept();
                                        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                                        short resultado = entrada.readShort();
                                        conexion.close();
                                        resultado++;
                                        System.out.println(resultado);
                                        enviar(puertoCliente, resultado);

                                    }

                                } catch (Exception ex) {
                                    System.out.println(ex.toString());
                                }

                                break;
                            case 2:
                                puertoServidor = 50002;
                                puertoCliente = 50003;
                                try {
                                    // ServerSocket servidor=new ServerSocket(puertoServidor);
                                    SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory
                                            .getDefault();
                                    ServerSocket servidor = socket_factory.createServerSocket(puertoServidor);

                                    for (;;) {
                                        Socket conexion = servidor.accept();
                                        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                                        short resultado = entrada.readShort();
                                        conexion.close();
                                        resultado++;
                                        System.out.println(resultado);
                                        enviar(puertoCliente, resultado);

                                    }

                                } catch (Exception ex) {
                                    System.out.println(ex.toString());
                                }
                                break;
                            case 3:
                                puertoServidor = 50003;
                                puertoCliente = 50004;
                                try {
                                    // ServerSocket servidor=new ServerSocket(puertoServidor);
                                    SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory
                                            .getDefault();
                                    ServerSocket servidor = socket_factory.createServerSocket(puertoServidor);
                                    for (;;) {
                                        Socket conexion = servidor.accept();
                                        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                                        short resultado = entrada.readShort();
                                        conexion.close();
                                        resultado++;
                                        System.out.println(resultado);
                                        enviar(puertoCliente, resultado);

                                    }

                                } catch (Exception ex) {
                                    System.out.println(ex.toString());
                                }
                                break;
                            case 4:
                                puertoServidor = 50004;
                                puertoCliente = 50005;
                                try {
                                    // ServerSocket servidor=new ServerSocket(puertoServidor);
                                    SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory
                                            .getDefault();

                                    ServerSocket servidor = socket_factory.createServerSocket(puertoServidor);
                                    for (;;) {
                                        Socket conexion = servidor.accept();
                                        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                                        short resultado = entrada.readShort();
                                        conexion.close();
                                        resultado++;
                                        System.out.println(resultado);
                                        enviar(puertoCliente, resultado);

                                    }

                                } catch (Exception ex) {
                                    System.out.println(ex.toString());
                                }
                                break;
                            case 5:
                                puertoServidor = 50005;
                                puertoCliente = 50000;
                                try {
                                    // ServerSocket servidor=new ServerSocket(puertoServidor);
                                    SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory
                                            .getDefault();

                                    ServerSocket servidor = socket_factory.createServerSocket(puertoServidor);
                                    for (;;) {
                                        Socket conexion = servidor.accept();
                                        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                                        short resultado = entrada.readShort();
                                        conexion.close();
                                        resultado++;
                                        System.out.println(resultado);
                                        enviar(puertoCliente, resultado);

                                    }

                                } catch (Exception ex) {
                                    System.out.println(ex.toString());
                                }
                                break;
                            default:
                                System.out.println("Escriba un numero menor a 6");
                                System.exit(0);
                                break;
                        }

                    } else {

                        System.out.println("Escriba un numero menor a 6");

                    }

                } else {

                    System.out.println("Escriba un numero positivo");

                }

            } catch (NumberFormatException e) {

                System.out.println("Escriba un numero");

            }

        } else {
            System.out.println("Escriba solo un parametro");
        }

    }
}
