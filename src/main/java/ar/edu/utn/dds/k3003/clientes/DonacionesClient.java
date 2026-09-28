package ar.edu.utn.dds.k3003.clientes;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "donaciones", url = "${FACHADA_D}")
public interface DonacionesClient {

    @PostMapping("/productos")
    public ResponseEntity<ProductoDTO> postProducto(@RequestBody ProductoDTO productoDTO);

    @GetMapping("/productos/{id}")
    public ResponseEntity<?> getProductoByID(@PathVariable("id") String productoID);

    @GetMapping("/productos")
    public ResponseEntity<List<ProductoDTO>> getProductos();

    @PutMapping("/productos/{id}")
    public ResponseEntity<ProductoDTO> putProducto(@PathVariable("id") String productoID, @RequestParam ProductoDTO nuevoProductoDTO);

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<ProductoDTO> deleteProducto(@PathVariable("id") String productoID);
}
