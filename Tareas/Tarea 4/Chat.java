import java.net.MulticastSocket;
import java.net.*;
import java.nio.charset.*;
import java.io.*;
import java.util.Scanner; 

class Chat{

    static void envia_mensaje_multicast(byte[] buffer, String ip, int puerto) throws IOException{

        DatagramSocket socket = new DatagramSocket ();
        socket.send(new DatagramPacket(buffer,buffer.length,InetAddress.getByName(ip),puerto));
        socket.close();
    }


    static byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException{

        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        
        return paquete.getData();

    }

     static class Worker extends Thread{

          public void run(){
                
             for(;;){
                 try{
                    
                MulticastSocket socket =new MulticastSocket(50000);
                InetAddress grupo= InetAddress.getByName("230.0.0.0");
                socket.joinGroup(grupo);
                int tamPaquete= 65535;
                byte [] arregloBytesMensaje=recibe_mensaje_multicast(socket, tamPaquete);
                String mensajeReci = new String(arregloBytesMensaje,"UTF-8").trim();
                socket.close();
               System.out.println(mensajeReci);

               }catch(Exception ex){
                   System.out.println(ex.toString());
               }
             }

          }

     }

     public static void main( String [] args )  throws Exception{
        System.setProperty("java.net.preferIPv4Stack", "true");
       new Worker().start();

       String nombre = args[0];

       try{
        //  Scanner scan= new Scanner(System.in,"UTF-8");
        Scanner myObj = new Scanner(System.in,"CP850");
        for(;;){
               System.out.println("Ingrese el mensaje a enviar");
             // String mensajeEnviar="¿alguien sabe donde será el concierto?";
              //String mensajeEnviar=nombre + " dice "+ scan.nextLine();
             // System.out.println("mensaje teclado:"+mensajeEnviar);
             String men = new String(myObj.nextLine().getBytes("CP850"),"CP850");
             String mensajeEnviar=nombre + " dice "+ men;
              envia_mensaje_multicast(mensajeEnviar.getBytes("UTF-8"), "230.0.0.0",50000);
              Thread.sleep(500);
        }

       }catch(Exception ex){

         System.out.println(ex.toString());

       }

       



     }


}