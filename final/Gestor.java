import java.sql.*;
import java.util.ArrayList;

public class Gestor {
   /// Objeto de conexión
   private static Connection con = null;
   /// Savepoint al que volver si falla algo
   private static Savepoint sp = null;
   /// Savepoint especial para realizar un pedido
   private static Savepoint pedido = null;

   /**
    * Se conecta a la base de datos de la ugr
    * @throws SQLException
    */
   public static void conectar() throws SQLException {
      // Parámetros de conexión
      String url;
      String usr;
      String pwd;

      url = "jdbc:oracle:thin:@oracle0.ugr.es:1521/practbd.oracle0.ugr.es";
      usr = "x5943758";
      pwd = "x5943758";

      // Establece la conexión
      con = DriverManager.getConnection(url, usr, pwd);

      // Desactiva el autocommit
      con.setAutoCommit(false);
   }

   /**
    * Se desconecta de la base de datos de la ugr
    * @throws SQLException
    */
   public static void desconectar() throws SQLException {
      // Guarda los cambios
      con.commit();

      // Se desconecta
      con.close();
   }

   /**
    * Restablece la BD a los valores por defecto
    * @throws SQLException
    */
   public static void reset() throws SQLException {
      // Crea un objeto statement dado por la conexión
      Statement sta = con.createStatement();

      // Crea un savepoint antes de hacer el reset
      sp = con.setSavepoint("reset");

      // Borra las tablas
      sta.execute(
         "DROP TABLE stock CASCADE CONSTRAINTS"
      );
      sta.execute(
         "DROP TABLE pedido CASCADE CONSTRAINTS"
      );
      sta.execute(
         "DROP TABLE detalle_pedido"
      );

      // Crea las tablas
      sta.execute(
         "CREATE TABLE stock (" +
            "cproducto INT GENERATED AS IDENTITY," +
            "cantidad  INT," +
            "PRIMARY KEY (cproducto)" +
         ")"
      );
      sta.execute(
         "CREATE TABLE pedido (" +
            "cpedido      INT GENERATED AS IDENTITY," +
            "ccliente     INT," +
            "fecha_pedido DATE DEFAULT SYSDATE," +
            "PRIMARY KEY (cpedido)" +
         ")"
      );
      sta.execute(
         "CREATE TABLE detalle_pedido (" +
            "cpedido   INT REFERENCES pedido(cpedido)," +
            "cproducto INT REFERENCES stock (cproducto)," +
            "cantidad  INT," +
            "PRIMARY KEY (cpedido, cproducto)" +
         ")"
      );

      // Prepara la sentencia para insertar en stock
      // Hay que hacerlo así por el "GENERATED AS IDENTITY" de SQL
      String instr = "INSERT INTO stock (cantidad) VALUES (10)";
      String[] autogen = {"cproducto"};
      PreparedStatement ps = con.prepareStatement(instr, autogen);
      // Inserta valores en stock
      for (int i = 0; i < 10; i++) {
         ps.execute();
      }

      // Guarda los cambios
      con.commit();
      sta.close();
      ps.close();
   }
   
   /**
    * Añade un pedido a la base de datos
    * @param ccliente Código del cliente que realiza el pedido
    * @return código del pedido
    * @throws SQLException
    */
   public static int crearPedido(int ccliente) throws SQLException {
      // Crea un objeto statement dado por la conexión
      Statement sta = con.createStatement();

      // Crea un savepoint antes de crear el pedido
      pedido = con.setSavepoint("crearPedido");

      // Prepara la sentencia de insertar en pedido
      String instr = "INSERT INTO pedido (ccliente) VALUES (" + ccliente + ")";
      String[] autogen = {"cpedido"};
      PreparedStatement ps = con.prepareStatement(instr, autogen);
   
      // Realiza la insercion
      ps.executeUpdate();
      
      // Recoge la clave autogenerada
      ResultSet rs = ps.getGeneratedKeys();
      rs.next();
      int cpedido = rs.getInt(1);

      // Crea un savepoint con el pedido ya hecho
      sp = con.setSavepoint("pedidoCreado");

      // Cierra
      sta.close();
      ps.close();
      rs.close();

      return cpedido;
   }

   /**
    * Comprueba el stock del producto en cuestión
    * @param cproducto código del producto a comprobar
    * @return cantidad disponible del pr
    * @throws SQLException
    */
   public static int comprobarStock(int cproducto) throws SQLException {
      // Crea un objeto statement dado por la conexión
      Statement sta = con.createStatement();

      // Realiza la consulta
      String q;
      q = "SELECT cantidad FROM stock WHERE (cproducto = " + cproducto + ")";
      ResultSet rs = sta.executeQuery(q);

      // Recoge el resultado y lo devuelve
      rs.next();
      return rs.getInt(1);
   }

   /**
    * Añade producto a detalle_pedido y resta al stock del producto
    * @param cpedido código del pedido
    * @param cproducto código del producto
    * @param cantidad cantidad a pedir
    * @throws SQLException
    */
   public static void crearDetallePedido(
      int cpedido,
      int cproducto,
      int cantidad
   ) throws SQLException {
      // Crea un objeto statement dado por la conexión
      Statement sta = con.createStatement();

      // Añade una tupla a detalle pedido
      String u;
      u = "INSERT INTO detalle_pedido (cpedido, cproducto, cantidad) VALUES (" +
          cpedido + ", " + cproducto + ", " + cantidad + ")";
      sta.executeUpdate(u);

      // Resta el stock al producto añadido
      u = "UPDATE stock SET cantidad = (cantidad - " + cantidad + 
          ") WHERE (cproducto = " + cproducto + ")";
      sta.executeUpdate(u);

      // Cierra
      sta.close();
   }

   /**
    * Hace rollback al savepoint Gestor.sp
    * @throws SQLException
    */
   public static void rollback() throws SQLException {
      con.rollback(sp);
   }

   /**
    * Hace rollback al savepoint Gestor.pedido
    * @throws SQLException
    */
   public static void rollbackPedido() throws SQLException {
      con.rollback(pedido);
   }

   /**
    * Guarda los cambios en la base de datos
    * @throws SQLException
    */
   public static void commit() throws SQLException {
      con.commit();
   }

   /**
    * Elimina el pedido y las tablas relacionadas de detalle_pedido
    * @param cpedido código del pedido a eliminar
    * @throws SQLException
    */
   public static void borrarPedido(int cpedido) throws SQLException {
      // Crea un objeto statement dado por la conexión
      Statement sta = con.createStatement();
      
      // Crea un savepoint antes de hacer las operaciones
      sp = con.setSavepoint("crearDetallePedido");
      
      // Devuelve el stock
      sta.execute("SELECT * FROM detalle_pedido WHERE cpedido = " + cpedido);
      ResultSet rs = sta.getResultSet();
      
      // Almacena los datos a actualizar
      ArrayList<Integer> productos = new ArrayList<>();
      ArrayList<Integer> cantidades = new ArrayList<>();
      while (rs.next()) {
         productos.add(rs.getInt(2));
         cantidades.add(rs.getInt(3));
      }

      // Actualiza el stock
      String s = "";
      for (int i = 0; i < productos.size(); i++) {
         s = "UPDATE stock SET cantidad = (cantidad + " +
             cantidades.get(i).toString() + ") WHERE (cproducto = " +
             productos.get(i).toString() + ")";
         sta.executeUpdate(s);
      }

      // Borra los detalles del pedido
      s = "DELETE FROM detalle_pedido WHERE cpedido = " + cpedido;
      sta.executeUpdate(s);

      // Borra el pedido
      s = "DELETE FROM pedido WHERE cpedido = " + cpedido;
      sta.executeUpdate(s);

      // Guarda los cambios
      con.commit();
      sta.close();
   }
}