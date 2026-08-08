
package com.tienda.repository;


import com.tienda.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer>{
    
    // Se crea una cosnulta derivada para recuperar solas las categoprias activas
    public List<Categoria> findByActivoTrue();
    
}
