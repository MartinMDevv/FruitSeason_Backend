package com.example.FruitseasonBackend.repository;

import com.example.FruitseasonBackend.model.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

}
