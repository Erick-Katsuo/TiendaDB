/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Katsuo
 */
public class DBconexion {

    private ArrayList<Producto> listProductos;
    private static final String USUARIO = "root";
    private static final String CLAVE = "";
    private static final String URL = "jdbc:mariadb://localhost/Almacen";
    private static final int OPCION_DEF = -1;
    private static final int OPCION_SALIR = 0;

    private static final int OPC_AGREGAR = 1;
    private static final int OPC_BUSCAR = 2;
    private static final int OPC_ELIMINAR = 3;

    private static final int OPC_MOSTRAR_DB = 4;

    private Scanner opcionEscaner = null;

    private int opcion = OPCION_DEF;

    private static final String SQL_INSERT = "INSERT INTO productos(nombre, cantidad, codigoProducto) value(?, ?, ?);";
    private static final String SQL_SELECT = "SELECT * FROM productos";
    private static final String SQL_SEARCH = "SELECT * FROM productos WHERE nombre=?;";
    private static final String SQL_DELETE = "DELETE FROM productos WHERE nombre=?;";

    private Connection coneccion = null;

    private static final int COLUMN_NOMBRE = 1;
    private static final int COLUMN_CANTIDAD = 2;
    private static final int COLUMN_CODIGO = 3;

    private static final boolean LISTA_USADA = true;
    private static final boolean LISTA_NO_USADA = false;

    private boolean listaUsada = LISTA_NO_USADA;

    public DBconexion(ArrayList<Producto> listProductos) {
        this.listProductos = listProductos;
    }

    public void setListProductos(ArrayList<Producto> listProductos) {
        this.listProductos = listProductos;
    }

    public boolean conectarDb() {
        try {
            coneccion = DriverManager.getConnection(URL, USUARIO, CLAVE);
            menuDb();
        } catch (SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaUsada;
    }

    private void agregarProductos() {
        try {
            /*Statement st = coneccion.createStatement(); 
            st.executeUpdate("INSERT INTO productos (nombre, cantidad)" + 
                "VALUES ('test2', 0.5)"); // al crear la tabla tener en cuenta que el rango es de 5 en TOTAL y 5 para decimales
            //entonces solo toma valores de 0,x 
            coneccion.close(); 
             */
            PreparedStatement pstm = coneccion.prepareStatement(SQL_INSERT);
            System.out.println("\n----Agregando Productos al Almacen (DB)---\n");
            if (!listProductos.isEmpty() && !listaUsada) {
                listaUsada = LISTA_USADA;
                System.out.println("\t###usando lista de productos proporcionada por la tienda\n");
                for (int i = 0; i < listProductos.size(); i++) {
                    pstm.setString(COLUMN_NOMBRE, listProductos.get(i).getNombre());
                    pstm.setFloat(COLUMN_CANTIDAD, listProductos.get(i).getCantidad());
                    pstm.setInt(COLUMN_CODIGO, listProductos.get(i).getCodigoProducto());
                    pstm.executeUpdate();
                    System.out.println("# " + listProductos.get(i).getCodigoProducto() + " # \n" + listProductos.get(i).getNombre() + " [" + listProductos.get(i).getCantidad() + "] - AGREGADO\n");
                }
            } else if (listaUsada) {
                System.out.println("No es posible agregar [Lista usada]");
            } else {
                System.out.println("No hay productos por agregar [Lista vacia]");
            }
            System.out.println("------------------------------------------\n\n");

            /*PreparedStatement pstm = coneccion.prepareStatement("SELECT * FROM productos");
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("nombre"));
                System.out.println(rs.getFloat("cantidad") );
            }*/
        } catch (SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void mostrarProductosDb() {
        try {
            PreparedStatement pstm = coneccion.prepareStatement(SQL_SELECT);
            ResultSet rs = pstm.executeQuery();
            System.out.println("\n---Mostrando productos de DB---\n");
            while (rs.next()) {
                System.out.println("[" + rs.getInt("codigoProducto") + "]  \n" + rs.getString("nombre") + "\n\tCantidad: " + rs.getFloat("cantidad"));
            }
            System.out.println("\n-------------------------------");
        } catch (SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void menuDb() {
        opcionEscaner = new Scanner(System.in);
        do{
            mostrarMenuDb();
            opcion = opcionEscaner.nextInt();
            switch (opcion) {
                case OPC_AGREGAR:
                    agregarProductos();
                    //mayb to-do limitar productos y contemplar duplicados
                    break;
                case OPC_BUSCAR:
                    buscarProducto();
                    break;
                case OPC_ELIMINAR:
                    eliminarProducto();
                    break;
                case OPC_MOSTRAR_DB:
                    mostrarProductosDb();
                    break;
                case OPCION_SALIR:
                    System.out.println("Saliendo del Almacen..");
                    break;
                default:
                    System.out.println("Def");
            }
        } while (opcion != OPCION_SALIR);
    }

    private void buscarProducto() {
        System.out.print("Ingrese nombre del producto a buscar: ");
        opcionEscaner.nextLine();
        String productoBuscado = opcionEscaner.next();
        try {
            PreparedStatement pstm = coneccion.prepareStatement(SQL_SEARCH);
            pstm.setString(1, productoBuscado);
            ResultSet rs = pstm.executeQuery();
            if (rs.first()) {
                rs.beforeFirst();
                System.out.println("\n---Buscando producto en DB---\n");
                while (rs.next()) {
                    System.out.println("[" + rs.getInt("codigoProducto") + "]  \n" + rs.getString("nombre") + "\n\tCantidad: " + rs.getFloat("cantidad"));
                }
                System.out.println("\n-------------------------------");
            } else {
                System.out.println("\n\n###No se encontraron productos con el nombre requerido\n");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void eliminarProducto() {
        System.out.print("Ingrese nombre del producto a eliminar: ");
        opcionEscaner.nextLine();
        String productoEliminar = opcionEscaner.next();
        try {
            PreparedStatement pstm = coneccion.prepareStatement(SQL_DELETE);
            pstm.setString(1, productoEliminar);
            pstm.executeQuery();
            System.out.println("\n###Producto "+productoEliminar+" eliminado\n");
            /*ResultSet rs = pstm.executeQuery();
            if (rs.first()) {
                rs.beforeFirst();
                System.out.println("\n---Eliminando producto en DB---\n");
                //mayb to-do delimitar la eliminacion a uno solo, debido a que el nombre no se repite
                while (rs.next()) {
                    System.out.println("[" + rs.getInt("codigoProducto") + "]  " + rs.getString("nombre") + "\n\tCantidad: " + rs.getFloat("cantidad"));
                }
                System.out.println("\n-------------------------------");
            } else {
                System.out.println("\n\n###No se encontraron productos con el nombre requerido\n");
            }*/
        } catch (SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void mostrarMenuDb() {
        System.out.println("------------Menu Almacen------------");
        System.out.println("\t • Abastecer almacen  [1]");
        System.out.println("\t • Buscar producto    [2]");
        System.out.println("\t • Eliminar producto  [3]");
        System.out.println("\t • Mostrar Tabla DB   [4]");
        System.out.println("\n\t • Salir             [0]");
        System.out.println("-------------------------------------");
    }
}
