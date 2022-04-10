import java.net.URL;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

class Usuario {
    int id_usuario;
    String email;
    String nombre;
    String apellido_paterno;
    String apellido_materno;
    String fecha_nacimiento;
    String telefono;
    String genero;
    byte[] foto;
}

public class Cliente {
    static String bd_email, bd_nombre, bd_paterno, bd_materno, bd_nacimiento, bd_tel, bd_genero;

    public static void main(String[] args) throws Exception {

        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("\n\n+------------------------------------------------+");
            System.out.println("\t\tMENU");
            System.out.println(" a. Alta usuario");
            System.out.println(" b. Consulta usuario");
            System.out.println(" c. Borra usuario");
            System.out.println(" d. Salir");
            System.out.print("Opcion: ");

            char sel = br.readLine().charAt(0);
            Usuario usuario = new Usuario();
            switch (sel) {
                case 'a':
                    System.out.println("\n\n+------------------------------------------------+\n");
                    System.out.println("\tAlta usuario");

                    System.out.print("Email: ");
                    usuario.email = br.readLine();
                    System.out.print("Nombre: ");
                    usuario.nombre = br.readLine();
                    System.out.print("Apellido paterno: ");
                    usuario.apellido_paterno = br.readLine();
                    System.out.print("Apellido materno: ");
                    usuario.apellido_materno = br.readLine();
                    System.out.print("Fecha de nacimiento (YYYY-MM-DD): ");
                    usuario.fecha_nacimiento = br.readLine();
                    System.out.print("Telefono: ");
                    usuario.telefono = br.readLine();
                    System.out.print("Genero ('M' o 'F'): ");
                    usuario.genero = br.readLine();
                    altaUsuario(usuario);
                    break;
                case 'b':
                    System.out.println("\n\n+------------------------------------------------+\n");
                    System.out.println("\tConsulta usuario");
                    System.out.print("ID del usuario: ");
                    String id = br.readLine();
                    consultaUsuario(Integer.parseInt(id));
                    System.out.println("Desea actualiza los datos (s/n): ");
                    char sel2 = br.readLine().charAt(0);
                    if (sel2 == 's') {
                        usuario.id_usuario = Integer.parseInt(id);
                        System.out.print("Email: ");
                        usuario.email = br.readLine();
                        if (usuario.email == null || usuario.email.equals("")) {
                            usuario.email = bd_email;
                        }
                        System.out.print("Nombre: ");
                        usuario.nombre = br.readLine();
                        if (usuario.nombre == null || usuario.nombre.equals("")) {
                            usuario.nombre = bd_nombre;
                        }
                        System.out.print("Apellido paterno: ");
                        usuario.apellido_paterno = br.readLine();
                        if (usuario.apellido_paterno == null || usuario.apellido_paterno.equals("")) {
                            usuario.apellido_paterno = bd_paterno;
                        }
                        System.out.print("Apellido materno: ");
                        usuario.apellido_materno = br.readLine();
                        if (usuario.apellido_materno == null || usuario.apellido_materno.equals("")) {
                            usuario.apellido_materno = bd_materno;
                        }
                        System.out.print("Fecha de nacimiento (YYYY-MM-DD): ");
                        usuario.fecha_nacimiento = br.readLine();
                        if (usuario.fecha_nacimiento == null || usuario.fecha_nacimiento.equals("")) {
                            usuario.fecha_nacimiento = bd_nacimiento;
                        }
                        System.out.print("Telefono: ");
                        usuario.telefono = br.readLine();
                        if (usuario.telefono == null || usuario.telefono.equals("")) {
                            usuario.telefono = bd_tel;
                        }
                        System.out.print("Genero ('M' o 'F'): ");
                        usuario.genero = br.readLine();
                        if (usuario.genero == null || usuario.genero.equals("")) {
                            usuario.genero = bd_genero;
                        }
                        actualizaUsuario(usuario);
                    } else if (sel == 'n') {
                        System.out.println("Fin de la consulta de usuario. ");
                    } else {
                        System.out.println("No ha seleccionado una opción valida, volviendo al menú. ");
                    }
                    break;
                case 'c':
                    System.out.println("\n\n+------------------------------------------------+\n");
                    System.out.println("\tBorrar usuario");
                    System.out.print("ID del usuario: ");
                    borraUsuario(Integer.parseInt(br.readLine()));
                    break;
                case 'd':
                    br.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Por favor ingrese un inciso válido, ");
                    break;
            }
        }
    }

    public static void altaUsuario(Usuario usuario) throws IOException {
        URL url = new URL("http://20.127.16.163:8080/Servicio/rest/ws/altaUsuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);

        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();

        Gson gson = builder.create();
        String body = gson.toJson(usuario);

        String parametros = "usuario=" + URLEncoder.encode(body, "UTF-8");

        OutputStream os = conexion.getOutputStream();
        os.write(parametros.getBytes());
        os.flush();

        if (conexion.getResponseCode() == 200) { // codigo de exito
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));
            String respuesta;
            while ((respuesta = br.readLine()) != null)
                System.out.println("Usuario con ID: " + respuesta + " agregado");
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
            String respuesta;
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);
            throw new RuntimeException("Código de error HTTP: " + conexion.getResponseCode());
        }
        conexion.disconnect();
    }

    public static void consultaUsuario(int id_usuario) throws IOException {

        URL url = new URL("http://20.127.16.163:8080/Servicio/rest/ws/consultaUsuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);
        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String parametros = "id_usuario=" + URLEncoder.encode(String.valueOf(id_usuario), "UTF-8");

        OutputStream os = conexion.getOutputStream();
        os.write(parametros.getBytes());
        os.flush();

        if (conexion.getResponseCode() == 200) { // código de exito
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));
            String respuesta;
            Gson j = new GsonBuilder().setDateFormat("yyyy-mm-dd").create();
            while ((respuesta = br.readLine()) != null) {
                Usuario usuario = (Usuario) j.fromJson(respuesta, Usuario.class);

                // si no se ingresan datos se conservarán los de la base de datos
                bd_email = usuario.email;
                bd_nombre = usuario.nombre;
                bd_paterno = usuario.apellido_paterno;
                bd_materno = usuario.apellido_materno;
                bd_nacimiento = usuario.fecha_nacimiento;
                bd_tel = usuario.telefono;
                bd_genero = usuario.genero;

                System.out.println("Nombre: " + usuario.nombre);
                System.out.println("Apellido paterno: " + usuario.apellido_paterno);
                System.out.println("Apellido materno: " + usuario.apellido_materno);
                System.out.println("Fecha: " + usuario.fecha_nacimiento);
                System.out.println("Telefono: " + usuario.telefono);
                System.out.println("Genero: " + usuario.genero);
            }
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
            String respuesta;
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);
            throw new RuntimeException("Código de error HTTP: " + conexion.getResponseCode());
        }
        conexion.disconnect();
    }

    public static void borraUsuario(int id_usuario) throws IOException {

        URL url = new URL("http://20.127.16.163:8080/Servicio/rest/ws/borraUsuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);
        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String parametros = "id_usuario=" + URLEncoder.encode(String.valueOf(id_usuario), "UTF-8");
        OutputStream os = conexion.getOutputStream();
        os.write(parametros.getBytes());
        os.flush();

        if (conexion.getResponseCode() == 200) {// Código de exito
            System.out.println("El usuario ha sido borrado");
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
            String respuesta;
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);
            throw new RuntimeException("Código de error HTTP: " + conexion.getResponseCode());
        }
        conexion.disconnect();
    }

    public static void actualizaUsuario(Usuario usuario) throws IOException {

        URL url = new URL("http://20.127.16.163:8080/Servicio/rest/ws/actualizaUsuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);
        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        String body = gson.toJson(usuario);

        String parametros = "usuario=" + URLEncoder.encode(body, "UTF-8");

        OutputStream os = conexion.getOutputStream();
        os.write(parametros.getBytes());
        os.flush();

        if (conexion.getResponseCode() == 200) { // Código de exito
            System.out.println("Datos actualizados correctamente");
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
            String respuesta;
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);
            throw new RuntimeException("Código de error HTTP: " + conexion.getResponseCode());
        }
        conexion.disconnect();
    }
}
