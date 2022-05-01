package negocio;

import com.google.gson.*;

public class Articulo {
    int id_articulo;
    String nombre;
    String descripcion;
    float precio;
    int cantidad;
    byte[] foto;


    public static Articulo valueOf(String s) throws Exception {
        Gson j = new GsonBuilder().registerTypeAdapter(byte[].class, new AdaptadorGsonBase64()).create();
        return (Articulo) j.fromJson(s, Articulo.class);
    }
}

