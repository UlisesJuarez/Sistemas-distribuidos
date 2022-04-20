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
import com.google.gson.*;


@Path("ws")
public class Servicio
{
  static DataSource pool = null;
  static
  {		
    try
    {
      Context ctx = new InitialContext();
      pool = (DataSource)ctx.lookup("java:comp/env/jdbc/datasource_Servicio");
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  static Gson j = new GsonBuilder()
		.registerTypeAdapter(byte[].class,new AdaptadorGsonBase64())
		.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
		.create();

  @POST
  @Path("alta_producto")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response alta(@FormParam("producto") Producto producto) throws Exception
  {
    Connection conexion = pool.getConnection();

    if (producto.descripcion == null || producto.descripcion.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Ingresa la descripción del producto"))).build();

    if (producto.precio == null || producto.precio.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Ingresa el precio del producto"))).build();

    if (producto.cantidad == null || producto.cantidad.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Ingresa la cantidad en almacen"))).build();

    try
    {
      PreparedStatement stmt_1 = conexion.prepareStatement("SELECT id_articulo FROM articulos WHERE descripcion=?");
      try
      {
        stmt_1.setString(1,producto.descripcion);

        ResultSet rs = stmt_1.executeQuery();
        try
        {
          if (rs.next())
            return Response.status(400).entity(j.toJson(new Error("Este producto ya existe"))).build();
        }
        finally
        {
          rs.close();
        }
      }
      finally
      {
        stmt_1.close();
      }

      PreparedStatement stmt_2 = conexion.prepareStatement("INSERT INTO articulos VALUES (0,?,?,?)");
      try
      {
        stmt_2.setString(1,producto.descripcion);
        stmt_2.setString(2,producto.precio);
        stmt_2.setString(3,producto.cantidad);
        stmt_2.executeUpdate();
      }
      finally
      {
        stmt_2.close();
      }

      if (producto.foto != null)
      {
        PreparedStatement stmt_3 = conexion.prepareStatement("INSERT INTO fotos_articulos VALUES (0,?,(SELECT id_articulo FROM articulos WHERE descripcion=?))");
        try
        {
          stmt_3.setBytes(1,producto.foto);
          stmt_3.setString(2,producto.descripcion);
          stmt_3.executeUpdate();
        }
        finally
        {
          stmt_3.close();
        }
      }
    }
    catch (Exception e)
    {
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    }
    finally
    {
      conexion.close();
    }
    return Response.ok().build();
  }

  @POST
  @Path("consulta_producto")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response consulta(@FormParam("descripcion") String descripcion) throws Exception
  {
    Connection conexion= pool.getConnection();

    try
    {
      PreparedStatement stmt_1 = conexion.prepareStatement("SELECT a.descripcion,a.precio,a.cantidad,b.foto FROM articulos a LEFT OUTER JOIN fotos_articulos b ON a.id_articulo=b.id_articulo WHERE descripcion=?");
      try
      {
        stmt_1.setString(1,descripcion);

        ResultSet rs = stmt_1.executeQuery();
        try
        {
          if (rs.next())
          {
            Producto r = new Producto();
            r.descripcion = rs.getString(1);
            r.precio = rs.getString(2);
            r.cantidad = rs.getString(3);
	          r.foto = rs.getBytes(4);
            return Response.ok().entity(j.toJson(r)).build();
          }
          return Response.status(400).entity(j.toJson(new Error("No contamos con productos para esa descripción"))).build();
        }
        finally
        {
          rs.close();
        }
      }
      finally
      {
        stmt_1.close();
      }
    }
    catch (Exception e)
    {
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    }
    finally
    {
      conexion.close();
    }
  }

  // @POST
  // @Path("modifica_usuario")
  // @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  // @Produces(MediaType.APPLICATION_JSON)
  // public Response modifica(@FormParam("usuario") Producto usuario) throws Exception
  // {
  //   Connection conexion= pool.getConnection();

  //   if (usuario.email == null || usuario.email.equals(""))
  //     return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el email"))).build();

  //   if (usuario.nombre == null || usuario.nombre.equals(""))
  //     return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el nombre"))).build();

  //   if (usuario.apellido_paterno == null || usuario.apellido_paterno.equals(""))
  //     return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el apellido paterno"))).build();

  //   if (usuario.fecha_nacimiento == null || usuario.fecha_nacimiento.equals(""))
  //     return Response.status(400).entity(j.toJson(new Error("Se debe ingresar la fecha de nacimiento"))).build();

  //   try
  //   {
  //     PreparedStatement stmt_1 = conexion.prepareStatement("UPDATE usuarios SET nombre=?,apellido_paterno=?,apellido_materno=?,fecha_nacimiento=?,telefono=?,genero=? WHERE email=?");
  //     try
  //     {
  //       stmt_1.setString(1,usuario.nombre);
  //       stmt_1.setString(2,usuario.apellido_paterno);
  //       stmt_1.setString(3,usuario.apellido_materno);
  //       stmt_1.setString(4,usuario.fecha_nacimiento);
  //       stmt_1.setString(5,usuario.telefono);
  //       stmt_1.setString(6,usuario.genero);
  //       stmt_1.setString(7,usuario.email);
  //       stmt_1.executeUpdate();
  //     }
  //     finally
  //     {
  //       stmt_1.close();
  //     }

  //     if (usuario.foto != null)
  //     {
  //       PreparedStatement stmt_2 = conexion.prepareStatement("DELETE FROM fotos_usuarios WHERE id_usuario=(SELECT id_usuario FROM usuarios WHERE email=?)");
  //       try
  //       {
  //         stmt_2.setString(1,usuario.email);
  //         stmt_2.executeUpdate();
  //       }
  //       finally
  //       {
  //         stmt_2.close();
  //       }

  //       PreparedStatement stmt_3 = conexion.prepareStatement("INSERT INTO fotos_usuarios VALUES (0,?,(SELECT id_usuario FROM usuarios WHERE email=?))");
  //       try
  //       {
  //         stmt_3.setBytes(1,usuario.foto);
  //         stmt_3.setString(2,usuario.email);
  //         stmt_3.executeUpdate();
  //       }
  //       finally
  //       {
  //         stmt_3.close();
  //       }
  //     }
  //   }
  //   catch (Exception e)
  //   {
  //     return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
  //   }
  //   finally
  //   {
  //     conexion.close();
  //   }
  //   return Response.ok().build();
  // }

//   @POST
//   @Path("borra_usuario")
//   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//   @Produces(MediaType.APPLICATION_JSON)
//   public Response borra(@FormParam("email") String email) throws Exception
//   {
//     Connection conexion= pool.getConnection();

//     try
//     {
//       PreparedStatement stmt_1 = conexion.prepareStatement("SELECT 1 FROM usuarios WHERE email=?");
//       try
//       {
//         stmt_1.setString(1,email);

//         ResultSet rs = stmt_1.executeQuery();
//         try
//         {
//           if (!rs.next())
// 		return Response.status(400).entity(j.toJson(new Error("El email no existe"))).build();
//         }
//         finally
//         {
//           rs.close();
//         }
//       }
//       finally
//       {
//         stmt_1.close();
//       }
//       PreparedStatement stmt_2 = conexion.prepareStatement("DELETE FROM fotos_usuarios WHERE id_usuario=(SELECT id_usuario FROM usuarios WHERE email=?)");
//       try
//       {
//         stmt_2.setString(1,email);
// 	stmt_2.executeUpdate();
//       }
//       finally
//       {
//         stmt_2.close();
//       }

//       PreparedStatement stmt_3 = conexion.prepareStatement("DELETE FROM usuarios WHERE email=?");
//       try
//       {
//         stmt_3.setString(1,email);
// 	stmt_3.executeUpdate();
//       }
//       finally
//       {
//         stmt_3.close();
//       }
//     }
//     catch (Exception e)
//     {
//       return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
//     }
//     finally
//     {
//       conexion.close();
//     }
//     return Response.ok().build();
//   }
}
