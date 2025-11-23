package com.example.FruitseasonBackend.controller;

import com.example.FruitseasonBackend.model.entity.Producto;
import com.example.FruitseasonBackend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository repo;

    @GetMapping("/test")
    public String test() {
        Producto p = new Producto();
        p.setNombre("Prueba");
        repo.save(p);

        return "BD funcionando";
    }
}
