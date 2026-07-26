
package com.tienda.repository;


import com.tienda.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // Se crea una cosnulta derivada para recuperar solas las categoprias activas
    public List<Producto> findByActivoTrue();

    //Consulta Derivada que extrae los productos de un rango de precios ordenados por precio
    public List<Producto> findByPrecioBetweenOrderByPrecioAsc(double precioInf, double precioSup);

    
    //Consulta JPQL que extrae los productos de un rango de precios ordenados por precio
    @Query(value = "SELECT p FROM Producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
    public List<Producto> consultaJPQL(double precioInf, double precioSup);

    
    //Consulta JPQL que extrae los productos de un rango de precios ordenados por precio
    @Query(nativeQuery = true,
            value = "SELECT * FROM producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
    public List<Producto> consultaSQL(double precioInf, double precioSup);

    // Practica 2 para consultar por descripcion
    public List<Producto> findByExistenciasLessThanEqualOrderByExistenciasAsc(Integer existencias);
}

