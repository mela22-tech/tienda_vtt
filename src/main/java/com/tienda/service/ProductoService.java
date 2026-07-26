package com.tienda.service;

import com.tienda.domain.Producto;
import com.tienda.repository.ProductoRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    private final FirebaseStorageService firebaseStorageService;

    public ProductoService(ProductoRepository productoRepository, FirebaseStorageService firebaseStorageService) {
        this.productoRepository = productoRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Transactional(readOnly = true)
    public List<Producto> getProductos(boolean activo) {
        if (activo) { // Solo se quieren las productos activas
            return productoRepository.findByActivoTrue();
        }

        return productoRepository.findAll();
    }

    // Recupera en un registro de producto -si existe-
    @Transactional(readOnly = true)
    public Optional<Producto> getProducto(Integer idProducto) {
        return productoRepository.findById(idProducto);
    }

    // Si producto, trae un iDproducto, se actualiza el registro, sino se crea
    @Transactional
    public void save(Producto producto, MultipartFile imagenFile) {
        //se salva la producto 
        productoRepository.save(producto);
        if (!imagenFile.isEmpty()) { // nos pasan una imagen
            try {
                String ruta = firebaseStorageService.uploadImage(
                        imagenFile,
                        "producto", producto.getIdProducto());
                producto.setRutaImagen(ruta);
                productoRepository.save(producto);

            } catch (IOException e) {
            }
        }
    }
// si idproducto exite se elimina ... si no tiene productos asociados

    @Transactional
    public void delete(Integer idProducto) {
// Se valida que la producto exista
        if (!productoRepository.existsById(idProducto)) {
            // se lanza una exception para indicarle al usuario que no se elimino
            throw new IllegalArgumentException("La producto con ID " + idProducto + " no exite!");
        }
        try {
            productoRepository.deleteById(idProducto);

        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la producto, tiene productos asociados");
        }

    }

    // consulta derivada
    @Transactional(readOnly = true)
    public List<Producto> consultaDerivada(double precioInf, double precioSup) {
        return productoRepository.findByPrecioBetweenOrderByPrecioAsc(precioInf, precioSup);
    }

    // Metodo de servicio que utiliza la consulta jpql
    @Transactional(readOnly = true)
    public List<Producto> consultaJPQL(double precioInf, double precioSup) {
        return productoRepository.consultaJPQL(precioInf, precioSup);
    }

    // Metodo de servicio que utiliza la consulta sql
    @Transactional(readOnly = true)
    public List<Producto> consultaSQL(double precioInf, double precioSup) {
        return productoRepository.consultaSQL(precioInf, precioSup);
    }

    @Transactional(readOnly = true)
    public List<Producto> consultaExistencias(Integer existencias) {
        return productoRepository.findByExistenciasLessThanEqualOrderByExistenciasAsc(existencias);
    }
}
