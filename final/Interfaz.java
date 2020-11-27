import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class Interfaz {
   /**
    * Muestra el menú principal
    */
   private static void mostrarMenu() {
      System.out.println(
         "\nMenú principal\n" + 
         "1. Resetear las tablas\n" +
         "2. Dar de alta un nuevo pedido\n" +
         "3. Borrar un pedido\n" + 
         "4. Salir del programa"
      );
   }

   /**
    * Muestra el menú de la opción: 2. Dar de alta un nuevo pedido
    */
   private static void mostrarMenuPedido() {
      System.out.println(
         "\nDar de alta pedido\n" +
         "1. Añadir detalle de pedido\n" +
         "2. Eliminar todos los detalles de producto\n" +
         "3. Cancelar pedido\n" +
         "4. Finalizar pedido"
      );
   }

   /**
    * Se conecta a la base de datos
    */
   private static void conectar() {
      try {
         System.out.println("Conectando a la base de datos...");
         Gestor.conectar();
         System.out.println("Conexión establecida correctamente");
      } catch (SQLException e) {
         System.err.println("Algo ha ido mal estableciendo la conexión");
				 System.exit(0);
      }
   }

   /**
    * Se desconecta de la base de datos
    */
   private static void desconectar() {
      try {
         System.out.println("Desconectando de la base de datos...");
         Gestor.desconectar();
         System.out.println("Desconexión realizada con éxito");
      } catch (SQLException e) {
         System.err.println("Algo ha ido mal cerrando la conexión");
      }
   }
   
   /**
    * Restablece la BD a los valores por defecto
    */
   private static void reset() {
      try {
         System.out.println("Reseteando la base de datos...");
         Gestor.reset();
         System.out.println("Las tablas se han reseteado con éxito");
      } catch (SQLException e) {
         System.err.println("Algo ha ido mal al resetear las tablas");
      }
   }

   
   /**
    * Crea un nuevo pedido
    * @param in BufferedReader que maneja la entrada del programa
    */
   private static void nuevoPedido(BufferedReader in) {
      try {
         System.out.print("Introduce el código del cliente: ");

         // Guarda la respuesta del usuario y convierte a int
         String respuesta = in.readLine();
         int ccliente = Integer.parseInt(respuesta);

         // Añade el pedido a la base de datos
         System.out.println("Creando pedido...");
         int cpedido = Gestor.crearPedido(ccliente);
         System.out.println("Pedido creado con éxito");
         System.out.println("Código de pedido = " + cpedido);

         // Loop para añadir productos al pedido, cancelar, etc
         boolean salir = false;
         while (!salir) {
            mostrarMenuPedido();
            System.out.println("Elección");

            // Recoge la respuesta y convierte a int
            respuesta = in.readLine();
            int r = Integer.parseInt(respuesta);

            // Gestiona la respuesta
            switch (r) {
               case 1:
                  // 1. Añadir detalle de producto
                  nuevoDetallePedido(cpedido, in);
               break;
               case 2:
                  // 2. Eliminar todos los detalles de producto
                  eliminarDetallesProducto();
               break;
               case 3:
                  // 3. Cancelar pedido
                  cancelarPedido();
                  salir = true;
               break;
               case 4:
                  // 4. Finalizar pedido
                  finalizarPedido();
                  salir = true;
               break;
               default:
                  System.out.println(r + " no es una opción válida");
               break;
            }
         }
      } catch (SQLException e) {
         System.err.println("Algo ha fallado al añadir el producto");
      } catch (NumberFormatException e) {
         System.err.println("No se ha introducido un código de cliente válido");
      } catch (IOException e) {
         System.err.println("Se ha producido un error en la lectura");
      }
   }

   /**
    * Añade un nuevo producto al pedido
    * @param cpedido código del pedido
    * @param in BufferedReader que maneja la entrada del programa
    */
   private static void nuevoDetallePedido(int cpedido, BufferedReader in) {
      try {
         System.out.println("Introduce el código del producto: ");

         // Almacena la respuesta del usuario y convierte a int
         String respuesta = in.readLine();
         int cproducto = Integer.parseInt(respuesta);

         // Comprueba si el producto tiene stock
         int stock = Gestor.comprobarStock(cproducto);
         
         // Pregunta cuanto quiere pedir
         System.out.println("La cantidad del producto es " + stock);
         System.out.println("¿Cuántos quieres pedir? ");

         // Recibe la respuesta del usuario y la convierte a int
         respuesta = in.readLine();
         int c = Integer.parseInt(respuesta);

         // Se asegura que no pide más del stock que hay
         while (c > stock) {
            System.out.println("Solo puedes pedir hasta " + stock);
            System.out.print("¿Cuántos quieres pedir? ");

            // Recibe la respuesta del usuario y convierte a int
            respuesta = in.readLine();
            c = Integer.parseInt(respuesta);
         }

         // Realiza el pedido
         Gestor.crearDetallePedido(cpedido, cproducto, c);
         System.out.println("Se ha añadido el producto con éxito");
      } catch (IOException e) {
         System.err.println("Se ha producido un error en la lectura");
      } catch (SQLException e) {
         System.err.println("El código de producto indicado no es válido");
      }
   }

   /**
    * Elimina todos los productos de un pedido (no borra el pedido en sí)
    */
   private static void eliminarDetallesProducto() {
      try {
         System.out.println("Eliminando detalles del pedido...");
         // Vuelve a justo después de crear el pedido (borra todos los detalles)
         Gestor.rollback();
         System.out.println("Detalles eliminados con éxito");
      } catch (SQLException e) {
         System.err.println("Error al hacer rollback");
      }
   }

   /**
    * Cancela el pedido (borra el pedido)
    */
   private static void cancelarPedido() {
      try {
         System.out.println("Cancelando pedido...");
         // Vuelve al estado anterior a crear el pedido (borra el pedido)
         Gestor.rollbackPedido();
         System.out.println("Pedido cancelado con éxito");
      } catch (SQLException e) {
         System.err.println("Error al hacer rollback al pedido");
      }
   }

   /**
    * Finaliza el pedido y lo guarda en la BD
    */
   private static void finalizarPedido() {
      try {
         System.out.println("Finalizando pedido...");
         // Guarda los cambios en el pedido
         Gestor.commit();
         System.out.println("Pedido finalizado con éxito");
      } catch (SQLException e) {
         System.err.println("Error al guardar los cambios");
      }
   }

   /**
    * Borra un pedido
    * @param in BufferedReader que se encarga de la entrada del programa
    */
   private static void borrarPedido(BufferedReader in) {
      try {
         System.out.print("Introduce el código del pedido a borrar: ");

         // Almacena la respuesta del usuario y convierte a int
         String respuesta = in.readLine();
         int cpedido = Integer.parseInt(respuesta);

         // Borra el pedido
         System.out.println("Borrando pedido...");
         Gestor.borrarPedido(cpedido);
         System.out.println("Se ha borrado el pedido con éxito");

      } catch (IOException e) {
         System.err.println("Se ha producido un error de lectura");
      } catch (SQLException e) {
         System.err.println("El pedido no se ha borrado correctamente");
      }
   }

   public static void main(String[] args) {
      // Variable para leer la entrada
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      
      // Se conecta a la base de datos
      conectar();
      
      String respuesta = "";
      boolean salir = false;
      while (!salir) {
         // Muestra el menú
         mostrarMenu();
         System.out.print("Elección: ");

         try {
            // Lee la respueta del usuario
            respuesta = in.readLine();
            int r = Integer.parseInt(respuesta);

            // Elige la función correcta
            switch (r) {
               case 1:
                  reset();
               break;
               case 2:
                  nuevoPedido(in);
               break;
               case 3:
                  borrarPedido(in);
               break;
               case 4:
                  salir = true;
               break;
               default:
                  System.err.println(r + " no es una opción válida");
               break;
            }
         } catch (IOException e) {
            System.err.println("Se ha producido un error en la lectura");
         } catch (NumberFormatException e) {
            System.err.println(respuesta + " no es una opción válida");
         }
      }

      // Se desconecta de la base de datos
      desconectar();
   }
}
