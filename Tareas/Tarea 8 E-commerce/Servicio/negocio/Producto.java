/*
  Carlos Pineda Guerrero, marzo 2022.
*/

package negocio;

import com.google.gson.*;

public class Producto
{
  String descripcion;
  String precio;
  String cantidad;
  byte[] foto;

  // @FormParam necesita un metodo que convierta una String al objeto de tipo Usuario
  public static Producto valueOf(String s) throws Exception
  {
    Gson j = new GsonBuilder().registerTypeAdapter(byte[].class,new AdaptadorGsonBase64()).create();
    return (Producto)j.fromJson(s,Producto.class);
  }
}
