package negocio;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Response;

import java.sql.*;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;

@Path("ws")
public class Servicio {
  static DataSource pool = null;
  static {
    try {
      Context ctx = new InitialContext();
      pool = (DataSource) ctx.lookup("java:comp/env/jdbc/datasource_Servicio");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static Gson j = new GsonBuilder().registerTypeAdapter(byte[].class, new AdaptadorGsonBase64())
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();

  @POST
  @Path("alta_articulo")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response alta(@FormParam("articulo") Articulo articulo) throws Exception {
    Connection conexion = pool.getConnection();

    if (articulo.nombre == null || articulo.nombre.equals(""))
    return Response.status(400).entity(j.toJson(new Error("Ingresar el nombre del articulo")))
        .build();
    if (articulo.descripcion == null || articulo.descripcion.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Ingresar la descripcion del articulo")))
          .build();

    if (articulo.precio <= 0.0f)
      return Response.status(400).entity(j.toJson(new Error("Ingresar un precio adecuado"))).build();

    if (articulo.cantidad <= 0)
      return Response.status(400).entity(j.toJson(new Error("Debe haber al menos un producto"))).build();

    if (articulo.foto == null)
      return Response.status(400).entity(j.toJson(new Error("Ingrese una imagen del articulo")))
          .build();

    try {
      conexion.setAutoCommit(false);
      PreparedStatement stmt_1 = conexion.prepareStatement("INSERT INTO articulos values(0, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS);
      try {
        stmt_1.setString(1, articulo.nombre);
        stmt_1.setString(2, articulo.descripcion);
        stmt_1.setFloat(3, articulo.precio);
        stmt_1.setInt(4, articulo.cantidad);
        stmt_1.executeUpdate();
        ResultSet rs = stmt_1.getGeneratedKeys();
        try {
          if (rs.next())
            articulo.id_articulo = rs.getInt(1);
        } finally {
          rs.close();
        }
      } finally {
        stmt_1.close();
      }

      if (articulo.foto != null) {
        PreparedStatement stmt_2 = conexion.prepareStatement("INSERT INTO foto_articulos VALUES (0,?,?)");
        try {
          stmt_2.setBytes(1, articulo.foto);
          stmt_2.setInt(2, articulo.id_articulo);
          stmt_2.executeUpdate();
        } finally {
          stmt_2.close();
        }
      }

      return Response.ok().entity(j.toJson(articulo.id_articulo)).build();

    } catch (Exception e) {
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    } finally {
      conexion.setAutoCommit(true);
      conexion.close();
    }
  }

  @POST
  @Path("busca_articulo")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response consultaArticulos(@FormParam("descripcion") String descripcion) throws Exception {
    Connection conexion = pool.getConnection();
    ArrayList<Articulo> articulos_relacionados = new ArrayList<Articulo>();

    try {
      PreparedStatement stmt_1 = conexion.prepareStatement(
          "SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, a.cantidad_almacen, b.foto FROM articulos a LEFT OUTER JOIN foto_articulos b ON a.id_articulo = b.id_articulo WHERE a.descripcion LIKE ?");

      try {
        stmt_1.setString(1, "%" + descripcion + "%");
        ResultSet rs = stmt_1.executeQuery();
        try {

          while (rs.next()) {
            Articulo a = new Articulo();
            a.id_articulo = rs.getInt(1);
            a.nombre = rs.getString(2);
            a.descripcion = rs.getString(3);
            a.precio = rs.getFloat(4);
            a.cantidad = rs.getInt(5);
            a.foto = rs.getBytes(6);

            articulos_relacionados.add(a);
          }

          if (articulos_relacionados.size() > 0) {
            return Response.ok().entity(j.toJson(articulos_relacionados)).build();
          } else {
            return Response.status(400).entity(j.toJson(new Error("No se encontrarón articulos relacionados.")))
                .build();
          }
        } finally {
          rs.close();
        }
      } finally {
        stmt_1.close();
      }
    } catch (Exception e) {
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    } finally {
      conexion.close();
    }
  }

  @POST
  @Path("comprar_articulo")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response comprar(@FormParam("articulo") Articulo articulo) throws Exception {
    Connection conexion = pool.getConnection();
    int disponible = 0;
    int id_carrito_articulo;

    try{
      conexion.setAutoCommit(false);
      conexion.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
      PreparedStatement stmt_1=conexion.prepareStatement("SELECT cantidad_almacen FROM articulos WHERE id_articulo=?");
      PreparedStatement stmt_2=conexion.prepareStatement("INSERT INTO carrito_compra(id_carrito_articulo, id_articulo, cantidad) VALUES (0,?,?)");
      PreparedStatement stmt_3=conexion.prepareStatement("UPDATE articulos SET cantidad_almacen=? WHERE id_articulo=?");

      try{
        stmt_1.setInt(1,articulo.id_articulo);
        ResultSet rs=stmt_1.executeQuery();
        while(rs.next()){
          disponible=rs.getInt("cantidad_almacen");
        }
        if(articulo.id_articulo<=disponible){
          try{
            stmt_2.setInt(1, articulo.id_articulo);
            stmt_2.setInt(2, articulo.cantidad);
            stmt_2.executeUpdate();

            stmt_3.setInt(1, articulo.cantidad);
            stmt_3.setInt(2, articulo.id_articulo);
            stmt_3.executeUpdate();

            conexion.commit();
          }catch(Exception e){
            conexion.rollback();
          }
        }else{
            return Response.status(400).entity(j.toJson(new Error("Lo sentimos, solo tenemos " +Integer.toString(disponible)+" productos disponibles."))).build();
        }
        return Response.ok().entity(j.toJson(id_carrito_articulo)).build();
      }finally{
        stmt_1.close();
        stmt_2.close();
        stmt_3.close();
      }
    }catch(Exception e){
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    }finally{
      conexion.close();
    }
  }

  @POST
  @Path("ver_carrito")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response consultaCarrito() throws Exception {
    ArrayList<Articulo> articulos_carrito = new ArrayList<Articulo>();
    Connection conexion = pool.getConnection();
    try {

      PreparedStatement stmt_1 = conexion.prepareStatement(
          "SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, b.cantidad, c.foto FROM carrito_compra b LEFT OUTER JOIN articulos a ON a.id_articulo = b.id_articulo LEFT OUTER JOIN foto_articulos c ON b.id_articulo = c.id_articulo");

      try {

        ResultSet rs = stmt_1.executeQuery();
        try {
          while (rs.next()) {
            Articulo a = new Articulo();
            a.id_articulo = rs.getInt(1);
            a.nombre = rs.getString(2);
            a.descripcion = rs.getString(3);
            a.precio = rs.getFloat(4);
            a.cantidad = rs.getInt(5);
            a.foto = rs.getBytes(6);

            // Añadimos el objeto "Articulo" al ArrayList
            articulos_carrito.add(a);
          }

          if (articulos_carrito.size() > 0) {
            return Response.ok().entity(j.toJson(articulos_carrito)).build();
          } else {
            return Response.status(400).entity(j.toJson(new Error("No hay articulos que coincidan :("))).build();
          }
        } catch (Exception e) {
          return Response.status(400).entity(j.toJson(new Error("No hay articulos que coincidan :("))).build();
        } finally {
          rs.close();
        }
      } finally {
        stmt_1.close();
      }
    } catch (Exception e) {
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    } finally {
      conexion.close();
    }
  }

}
