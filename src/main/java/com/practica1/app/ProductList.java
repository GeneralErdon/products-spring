package com.practica1.app;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductList implements Serializable {
    private final ArrayList<Producto> data;
    private int count;
    ProductList(){
        this.data = new ArrayList<>();
        this.count = 0;
    }
    public ArrayList<Producto> getProductos(){
        return this.data;
    }
    public ArrayList<Producto> getProductos(String nameParam) {
        return data.stream().filter(
                (e) -> e.name.contains(nameParam)
        ).collect(Collectors.toCollection(ArrayList::new));
    }
    public Optional<Producto> retrieveProducto(int id){
        // Esto es una bùsqueda lineal (la puedo mejorar por Binary Search)

        for(Producto obj : data){
            if(obj.id == id) return Optional.of(obj);
        }
        return Optional.empty();
    }
    public void updateProducto(Producto producto, String newName) throws ObjetoExistenteException{
        // Se supone que estoy mutando en la misma direccion de memoria xd
        if(itExists(newName)){
            throw new ObjetoExistenteException("Este nombre de producto ya existe");
        }
        producto.setName(newName);
    }
    public boolean itExists(Producto producto){
        return this.data.stream().anyMatch((e) -> e.name.equals(producto.name));
    }
    public boolean itExists(String name){
        return this.data.stream().anyMatch((e) -> e.name.equals(name));
    }
    public int getCount(){
        return this.count;
    }
    public void addProducto(Producto obj) throws ObjetoExistenteException{
        if(this.data.isEmpty()){
            this.count++;
            obj.setId(this.count);
            this.data.add(obj);
            return;
        }
        if(this.itExists(obj)){
            throw new ObjetoExistenteException();
        }else{
            this.count++;
            obj.setId(this.count);
            this.data.add(obj);
        }
    }
    public void deleteProducto(int id){
        
        Optional<Producto> obj = this.data.stream().filter((e) -> e.id == id).findFirst();
        if(obj.isEmpty()){
            throw new IllegalArgumentException("No se encontro esa id");
        }
        data.remove(obj.get());
        count--;
    }

    public void deleteProducto(Producto obj){
        this.data.remove(obj);
        count--;
    }

    public static class Producto implements Serializable{
        private int id;
        private String name;
        public Producto(String name){
            this.name = name;
        }
        public Producto(){}
        public String getName(){
            return this.name;
        }
        public void setName(String name){
            this.name = name.toUpperCase();
        }
        public void setId(int id){
            this.id = id;
        }
        public int getId(){
            return this.id;
        }
        @Override
        public String toString(){
            return this.name;
        }

    }
    public static class ObjetoExistenteException extends Exception{
        ObjetoExistenteException(){
            super("Este objeto ya existe en la data");
        }
        ObjetoExistenteException(String msg){
            super(msg);
        }
    }
}
