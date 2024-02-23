package com.practica1.app;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.practica1.app.ProductList.Producto;

@RestController
public class HelloController {
    String FILE_PATH = Path.of(
            "Datos.ser").toString();
    public void saveFile(ProductList obj){
        try {
            FileOutputStream fileOut = new FileOutputStream(this.FILE_PATH);
            ObjectOutputStream output = new ObjectOutputStream(fileOut);
            output.writeObject(obj);
            output.close();
            fileOut.close();
            System.out.println("File saved");

        }catch (IOException e) {
            e.printStackTrace(); // callate webon
        }

    }
    public ProductList readFile() throws IOException, ClassNotFoundException{
        ProductList obj;
        FileInputStream fileIn = new FileInputStream(this.FILE_PATH);
        ObjectInputStream input = new ObjectInputStream(fileIn);
        obj = (ProductList) input.readObject();
        input.close();
        fileIn.close();
        return obj;
    }
    public ProductList getDataInstance() {
        try{
            return readFile();
        } catch (Exception e){
            ProductList obj = new ProductList();
            saveFile(obj);
            return obj;
        }
    }




    @GetMapping("{path:^.*$}")
    public ResponseEntity<Map<String, String>> test(String path){
        HashMap<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("message", "Esa ruta no existe bobo, coloca la ruta: /products");
        return new ResponseEntity<>(jsonResponse, HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = {"/products", "/products/"})
    public ResponseEntity<Map<String, Object>> index(@RequestParam(required = false) String name){
        ProductList data = getDataInstance();
        HashMap<String, Object> jsonResponse = new HashMap<>();
        ArrayList<Producto> results;
        if (name == null){
            results = data.getProductos();
            jsonResponse.put("count", data.getCount());
            jsonResponse.put("data", results);
        } else {
            results = data.getProductos(name);
            jsonResponse.put("count", results.size());
            jsonResponse.put("data", results);
        }
        return new ResponseEntity<>(
                jsonResponse,
                results.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK
            );
    }


    @GetMapping(value = {"/products/{id}", "/products/{id}/"})
    public ResponseEntity<Object> retrieve(@PathVariable Integer id){
        HashMap<String, Object> json = new HashMap<>();
        ProductList data = getDataInstance();
        Optional<Producto> result = data.retrieveProducto(id);
        if(result.isPresent()){
            return new ResponseEntity<>(result.get(), HttpStatus.OK);
        }

        json.put("message", "Objeto no encontrado");
        return new ResponseEntity<>(json, HttpStatus.NOT_FOUND);
    }
    @PostMapping(value = {"/products", "/products/"})
    public ResponseEntity<Map<String, Object>> addProduct(@RequestBody Producto producto){
        ProductList data = getDataInstance();
        HashMap<String, Object> jsonResponse = new HashMap<>();

        try {
            data.addProducto(producto);
            saveFile(data);
            jsonResponse.put("message", "Objeto creado exitosamente");
        } catch (ProductList.ObjetoExistenteException e) {
            jsonResponse.put("message", "El objeto ya existe");
        }
        return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
    }

    @PatchMapping(value = {"/products/{id}", "/products/{id}/"})
    public ResponseEntity<Map<String, String>> partialUpdate(
            @PathVariable Integer id, @RequestBody Producto newProducto
            ){
        HashMap<String, String> jsonResponse = new HashMap<>();
        ProductList data = getDataInstance();
        Optional<Producto> obj = data.retrieveProducto(id);
        if(obj.isEmpty()){
            jsonResponse.put("message", "No se encontró un registro con esa ID");
            return new ResponseEntity<>(jsonResponse, HttpStatus.NOT_FOUND);
        }
        try {
            data.updateProducto(obj.get(), newProducto.getName());
        } catch (ProductList.ObjetoExistenteException e){
            jsonResponse.put("message", "Error, ya existe un item con ese nombre");
            return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
        }
        jsonResponse.put("message", "Objeto actualizado exitosamente");
        System.out.println(obj.get().getName());
        saveFile(data);
        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }
    @DeleteMapping(value = {"/products/{id}", "/products/{id}/"})
    public ResponseEntity<Map<String, String>> delete(@PathVariable Integer id){
        HashMap<String, String> jsonResponse = new HashMap<>();
        ProductList data = getDataInstance();
        Optional<Producto> obj = data.retrieveProducto(id);
        if(obj.isEmpty()){
            jsonResponse.put("message", "No se encontró un registro con esa ID");
            return new ResponseEntity<>(jsonResponse, HttpStatus.NOT_FOUND);
        }
        data.deleteProducto(obj.get());
        saveFile(data);
        jsonResponse.put("message", "Objeto eliminado con éxito");
        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }
}
