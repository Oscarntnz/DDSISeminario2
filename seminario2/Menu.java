package pedidos;

public class Menu{
    protected void MainMenu(){
        System.out.print("Gestor de pedidos:\n");
        showInstructions();
    }

    protected void showInstructions(){
        System.out.print("\t1 - Añadir detalle de producto\n");
        System.out.print("\t2 - Eliminar todos los detalles de producto\n");
        System.out.print("\t3 - Cancelar pedido\n");
        System.out.print("\t4 - Finalizar pedido\n");
    }
}