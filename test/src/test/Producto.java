/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author Katsuo
 */
public class Producto {
    private String nombre;
    private float cantidad;
    private int codigoProducto;
    
    public Producto(String nombre, float cantidad, int codigoProducto){
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.codigoProducto = codigoProducto;
    }
    
    void setNombre(String nombre){
        this.nombre = nombre;
    }
    
    String getNombre(){
        return nombre;
    }
    
    void setCantidad(float cantidad){
        this.cantidad = cantidad;
    }
    
    float getCantidad(){
        return cantidad;
    }
    
    void setCodigoProducto(int codigoProducto){
        this.codigoProducto =codigoProducto;
    }
    
    int getCodigoProducto(){
        return codigoProducto;
    }
}