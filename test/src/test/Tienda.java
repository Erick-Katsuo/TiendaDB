/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Katsuo
 */
public class Tienda {

    private static final float CANTIDAD_DEF = -1;
    private static final String NOMBRE_DEF = "def";
    private static final int MAX_PRODUCTOS = -1;
    private static final int CODIGO_DEF = -1;

    private static ArrayList<Producto> listProductos = null;

    private static int maxProductos = MAX_PRODUCTOS;
    private static String nombreProducto = NOMBRE_DEF;
    private static float cantidad = CANTIDAD_DEF;
    private static int codigoProducto = CODIGO_DEF;

    private Scanner entradaEscaner = null;
    private Producto productoNuevo = null;

    private static final int OPCION_A_DEF = -1;
    private static final int OPCION_A_SALIR = 0;

    private static final int OPC_A_AUMENTAR = 1;
    private static final int OPC_A_BUSCAR = 2;
    private static final int OPC_A_ELIMINAR = 3;

    private static final int OPC_A_MOSTRAR = 4;
    private static final int OPC_BASEDATO = 5;

    private Scanner opcionAEscaner = null;

    private int opcionAlmacen = OPCION_A_DEF;
    private static final String PROD_ELIM_DEF = "prodDef";
    private String productoEliminar = PROD_ELIM_DEF;

    private static final int ELIMINAR_UNO = 1;
    private static final int ELIMINAR_VARIOS = 2;
    private static final int ELIMINAR_TODO = 3;
    
    private static final int OPCION_INVALIDA = -1;
    
    private static final boolean NO_VERIFICADO = false;
    private static final boolean VERIFICADO = true;    
    
    private static final int SUMAR_CANTIDADES = 1;
    private static final int DESCARTAR_PRODUCTO = 2;
    
    private boolean verificacionDupl = NO_VERIFICADO;
    private int opcionDuplicado = OPCION_A_DEF;

    private static final boolean LISTA_USADA = true;
    private static final boolean LISTA_NO_USADA = false;
    
    private boolean listaUsada = LISTA_NO_USADA;
    
    public Tienda() {
        listProductos = new ArrayList<>();
    }

    public ArrayList<Producto> getListaProductos() {
        return listProductos;
    }

    /*Muestra el menu de la tienda
     *pre: opcionAlmacen correctamente definido
     *post: dependiendo del valor opcionAlmacen se realiza algun caso
     */
    public void abrirTienda() {
        //pedirDatos();//agrega datos desde listProductos
        opcionAEscaner = new Scanner(System.in);
        do {
            mostrarMenuAlmacen();
            opcionAlmacen = opcionAEscaner.nextInt();
            switch (opcionAlmacen) {
                case OPC_A_AUMENTAR:
                    pedirDatos();
                    // agrega todos los actuales en la lista
                    break;
                case OPC_A_MOSTRAR:
                    mostrarProductos();
                    break;
                case OPC_A_ELIMINAR:
                    verificarListaVacia();
                    break;
                case OPC_BASEDATO:
                    System.out.println("\n###Ingresando a la Base de datos..\n");
                    conexionDb();
                    borrarListaUsada();
                    break;
                case OPCION_A_SALIR:
                    System.out.println("Saliendo de la tienda..");
                    break;
                default:
                    System.out.println("###Ingreso invalido");
            }
        } while (opcionAlmacen != OPCION_A_SALIR);
    }

    private void conexionDb() {
        DBconexion conexionDb = new DBconexion(listProductos);
        listaUsada = conexionDb.conectarDb();
    }
    
    private void borrarListaUsada(){
        if(listaUsada){
            System.out.println("\n\n1La lista fue utilizada en el Almacen\n\t###Borrando lista utilizada");
            listProductos.clear();
        }
    }

    private void pedirDatos() {
        entradaEscaner = new Scanner(System.in);
        System.out.print("Ingrese cantidad de productos maximos para agregar: ");
        maxProductos = entradaEscaner.nextInt();
        agregarProductos();
    }

    private void agregarProductos() {
        int productosAgregados = 0;
        for (int i = 0; i < maxProductos; i++) {
            System.out.println("\nProducto nº"+(i+1));
            entradaEscaner.nextLine();//evita que tome informacion erronea
            System.out.print("\tNombre del producto nuevo: ");
            nombreProducto = entradaEscaner.next();
            entradaEscaner.nextLine();
            System.out.print("\tCantidad del producto nuevo: ");
            cantidad = entradaEscaner.nextFloat();            
            entradaEscaner.nextLine();
            System.out.print("\tCodigo del producto nuevo: ");
            //tener en cuenta que si pasa el rango de 0-255 se produce un error
            codigoProducto = entradaEscaner.nextInt();
            productoNuevo = new Producto(nombreProducto, cantidad, codigoProducto);
            verificarDuplicado();
            if(verificacionDupl){
                listProductos.add(productoNuevo);
                productosAgregados++;
            }
        }
        System.out.println("\n###Lista aumentada###");
        System.out.println("\n###Productos totales agregados: "+productosAgregados+"\n");
    }
    
    private void verificarDuplicado(){        
        if(listProductos.isEmpty()){
            verificacionDupl = VERIFICADO;
        }else{
            buscarDuplicado();
        }
    }
    
    private void buscarDuplicado(){
        for(int i = 0; i< listProductos.size(); i++){
            if(listProductos.get(i).getNombre().equals(productoNuevo.getNombre())){
                System.out.println("\n\n###Encontrado producto duplicado\n\t***"+productoNuevo.getNombre()+"***\n\n");
                confirmarSumaCantidad(i);
                verificacionDupl = NO_VERIFICADO;
                i = listProductos.size();
            }else if(i == (listProductos.size()-1)){
                verificacionDupl = VERIFICADO;                
            }
        }
    }
    
    private void confirmarSumaCantidad(int i){
        do{
            menuDuplicado();
            opcionDuplicado = opcionAEscaner.nextInt();
            switch(opcionDuplicado){
                case SUMAR_CANTIDADES:
                    System.out.println("###Sumando cantidades###");
                    sumarCantidades(i);
                    opcionDuplicado = OPCION_A_SALIR;
                    break;
                case DESCARTAR_PRODUCTO:
                    System.out.println("###Descartando actualizacion de producto###");
                    opcionDuplicado = OPCION_A_SALIR;
                    break;
                default:
                    System.out.println("###Ingreso invalido###");
                    opcionDuplicado = OPCION_A_DEF;
                    break;                    
            }
        }while(opcionDuplicado != OPCION_A_SALIR);
    }
    
    private void sumarCantidades(int i){
        float canitdadTotal = listProductos.get(i).getCantidad() + productoNuevo.getCantidad();
        listProductos.get(i).setCantidad(canitdadTotal);
        System.out.println("Cantidades sumadas");
        System.out.println("Producto "+listProductos.get(i).getNombre()+" actualizado");
        System.out.println("Cantidad actual: "+listProductos.get(i).getCantidad()+"\n\n");
    }
    
    private void mostrarProductos() {
        if (!listProductos.isEmpty()) {
            System.out.println("\n------Productos actuales por agregar------\n");
            for (int i = 0; i < listProductos.size(); i++) {
                System.out.println("• ["+listProductos.get(i).getCodigoProducto()+"]" + listProductos.get(i).getNombre() + "\n\t- Cantidad: " + listProductos.get(i).getCantidad());
            }
            System.out.println("\n------------------------------------------\n");
        } else {
            System.out.println("\n###Lista de productos por agregar vacia###\n");
        }
    }
    
    private void verificarListaVacia(){
        if(!listProductos.isEmpty()){
            eliminar();
        }else{
            System.out.println("\nImposible proceder\n\t###Lista Vacia\n");
        }
    }
    
    /*agregar eliminar lista o 1 producto en particular*/
    private void eliminar() {
        int opcionEliminar = OPCION_INVALIDA;
        do {
            menuEliminar();
            opcionEliminar = opcionAEscaner.nextInt();
            switch (opcionEliminar) {
                case ELIMINAR_UNO:
                    eliminarUnProduct();
                    break;
                case ELIMINAR_VARIOS:
                    eliminarProductos();
                    break;
                case ELIMINAR_TODO:
                    eliminarTodo();
                    break;
                case OPCION_A_SALIR:
                    System.out.println("###Eliminacion descartada");
                    break;
                default:
                    System.out.println("---Ingreso invalido---");
                    opcionEliminar = OPCION_INVALIDA;
            }
        }while(opcionEliminar == OPCION_INVALIDA);    
    }

    private void eliminarUnProduct() {
        opcionAEscaner.nextLine();
        System.out.print("\n- Ingrese nombre del producto a eliminar: ");
        productoEliminar = opcionAEscaner.next();
        realizarEliminacion();
    }
    
    private void realizarEliminacion(){
        System.out.println("\n###Buscando producto " + productoEliminar + "..");
        for (int i = 0; i < listProductos.size(); i++) {
            //solo funcina con equals, no con ==
            if (listProductos.get(i).getNombre().equals(productoEliminar)) {
                System.out.println("Encontrado producto *** " + listProductos.get(i).getNombre() + " ***");
                listProductos.remove(i);
                System.out.println("###Eliminado\n");
                i = listProductos.size();
            }else if(i == (listProductos.size()-1) ){
                System.out.println(" *** No se encontro el producto *** \n\n");
            }
        }
    }

    private void eliminarProductos() {
        System.out.print("\n-Ingrese la cantidad de productos a Eliminar:");
        int cantidadEliminar = opcionAEscaner.nextInt(); 
        String[] listEliminar = new String[cantidadEliminar];
        for(int i = 0 ; i < cantidadEliminar ; i++){
            opcionAEscaner.nextLine();
            System.out.print("Ingrese nombre de producto nº"+(i+1)+" a eliminar:");
            productoEliminar = opcionAEscaner.next();
            listEliminar[i] = productoEliminar;
        }
        for(int j = 0 ; j<cantidadEliminar ;j++){
            productoEliminar = listEliminar[j];
            realizarEliminacion();
        }
    }

    private void eliminarTodo() {
        System.out.println("\n-Eliminando todo..");
    }

    private void mostrarMenuAlmacen() {
        System.out.println("------------Menu Tienda-------------");
        System.out.println("       [Productos sin agregar]       ");
        System.out.println("\t• Aumentar          [1]");
        System.out.println("\t• Eliminar de lista [3]");
        System.out.println("\t• Mostrar lista     [4]");
        System.out.println("\t• Menu Database     [5]");
        System.out.println("\n\t• Salir             [0]");
        System.out.println("-------------------------------------");
        System.out.println("\n\n\n\n\n\n\n");
    }

    private void menuEliminar() {
        System.out.println("-----------SubMenu Lista-------------\n");
        System.out.println("\t• Eliminar un Producto        [1]");
        System.out.println("\t• Eliminar varios Productos   [2]");
        System.out.println("\t• Eliminar Todo               [3]");
        System.out.println("\n\t• Salir                     [0]");
        System.out.println("-------------------------------------");
        System.out.println("\n\n\n\n\n\n\n");
    }
    
    private void menuDuplicado(){
        System.out.println("-----------SubMenu Duplicado-------------");
        System.out.println("\t• Sumar cantidades           [1]");
        System.out.println("\t• Descartar producto nuevo   [2]");
        System.out.println("\n*** Por defecto el codigo no se actualizara ***");
        System.out.println("-----------------------------------------");
        System.out.println("\n\n\n\n\n\n\n");
    }
}
