import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MatrizDist {

    public static void server(int portS) {
        try {
            ServerSocket server = new ServerSocket(portS);
            for (;;) {
                Socket connection = server.accept();
                DataInputStream input = new DataInputStream(connection.getInputStream());
                short result = input.readShort();
                System.out.println(result);
                
                connection.close();
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    public static void client(int port, short num) {
        try {
            for (;;)
                try {
                    Socket connection = new Socket("localhost", port);
                    DataOutputStream output = new DataOutputStream(connection.getOutputStream());
                    output.writeShort(num);
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

    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                int nodo = Integer.parseInt(args[0]);
                if (nodo >= 0 && nodo <= 3) {
                    switch (nodo) {
                        case 0:
                            System.out.println("Client running");
                            short nInicial=34;
                            client(50000, nInicial);
                            client(50001,nInicial);
                            client(50002,nInicial);
                            
                            break;
                        case 1:
                            System.out.println("Server running on port 5000");
                            server(50000);
                            break;
                        case 2:
                            System.out.println("Server running on port 5001");
                            server(50001);
                            break;
                        case 3:
                            System.out.println("Server running on port 5002");
                            server(50002);
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
        }else{
            System.out.println("Por favor ingrese un parametro entre 0-3");
        }
    }
}
