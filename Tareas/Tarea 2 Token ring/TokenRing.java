public class TokenRing {
    static class Worker extends Thread{
        public void run(){
            try {
                System.setProperty("javax.net.ssl.keyStore", "keystore_servidor.jks");
                System.setProperty("javax.net.ssl.keyStorePassword", "1029384");   
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.trustStore", "keystore_cliente.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "102938"); 

    }
}
