package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorStatsDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EntidadBeneficaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.PaqueteDTO;
import ar.edu.utn.dds.k3003.clientes.DonacionesClient;
import ar.edu.utn.dds.k3003.clientes.DonadoresYEntidadesClient;

import ar.edu.utn.dds.k3003.clientes.IncentivosClient;
import ar.edu.utn.dds.k3003.clientes.LogisticaClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Fachada {

    private final DonadoresYEntidadesClient donadoresYEntidadesClient;
    private final DonacionesClient donacionesClient;
    private final IncentivosClient incentivosClient;
    private final LogisticaClient logisticaClient;

    public Fachada(DonadoresYEntidadesClient donadoresYEntidadesClient, DonacionesClient donacionesClient, IncentivosClient incentivosClient, LogisticaClient logisticaClient) {
        this.donadoresYEntidadesClient = donadoresYEntidadesClient;
        this.donacionesClient = donacionesClient;
        this.incentivosClient = incentivosClient;
        this.logisticaClient = logisticaClient;
    }

    public DonadorDTO registrarDonador(DonadorDTO donadorDTO) {
        return donadoresYEntidadesClient.agregarDonador(donadorDTO).getBody();
    }

    public String obtenerEstadisticasDonador(String id) {
        try {

            DonadorStatsDTO estadisticas = donadoresYEntidadesClient.estadisticas(id).getBody();

            if (estadisticas == null) {
                return "No se encontraron estadísticas para el donador con ID " + id;
            }

            String insignias = estadisticas.insigniasID() != null
                    && !estadisticas.insigniasID().isEmpty()
                    ? String.join(", ", estadisticas.insigniasID())
                    : "Sin insignias";

            String mision = estadisticas.misionActualID() != null
                    ? estadisticas.misionActualID()
                    : "Sin misión activa";

            return "Estadísticas del donador\n\n"
                    + "ID: " + estadisticas.id() + "\n"
                    + "Nombre: " + estadisticas.nombre() + " "
                    + estadisticas.apellido() + "\n"
                    + "Edad: " + estadisticas.edad() + " años\n"
                    + "Estado: " + estadisticas.estado() + "\n"
                    + "Categoría: " + estadisticas.categoria() + "\n"
                    + "Misión actual: " + mision + "\n"
                    + "Insignias: " + insignias;

        } catch (NumberFormatException e) {
            return "El ID del donador no es válido.";
        }
    }

    public DonadorDTO buscarDonador(String id) {
        try {
            return donadoresYEntidadesClient.buscarDonador(id).getBody();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public List<DonadorDTO> obtenerDonadores() {
        return donadoresYEntidadesClient.obtenerDonadores().getBody();
    }

    public EntidadBeneficaDTO registrarEntidad(
            EntidadBeneficaDTO entidadDTO
    ) {
        return donadoresYEntidadesClient.agregarEntidad(entidadDTO).getBody();
    }

    public EntidadBeneficaDTO modificarEntidad(
            String id,
            EntidadBeneficaDTO entidadDTO
    ) {
        try {
            return donadoresYEntidadesClient.modificarEntidad(
                    id,
                    entidadDTO
            ).getBody();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public List<EntidadBeneficaDTO> obtenerEntidades() {
        return donadoresYEntidadesClient.obtenerEntidades().getBody();
    }

    public EntidadBeneficaDTO buscarEntidad(String id) {
        try {
            return donadoresYEntidadesClient.buscarEntidad(id).getBody();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public NecesidadMaterialDTO obtenerNecesidad(String id) {
        try {
            return donadoresYEntidadesClient.obtenerNecesidad(id).getBody();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public NecesidadMaterialDTO registrarNecesidad(
            NecesidadMaterialDTO necesidadDTO
    ) {
        return donadoresYEntidadesClient.registrarNecesidad(necesidadDTO).getBody();
    }

    public String eliminarNecesidad(String id) {
        try {
            donadoresYEntidadesClient.eliminarNecesidad(id);

            return "La necesidad con ID " + id + " fue eliminada correctamente.";

        } catch (NumberFormatException e) {
            return "El ID de la necesidad no es válido.";

        } catch (Exception e) {
            return "No se pudo eliminar la necesidad con ID " + id + ".";
        }
    }

    public NecesidadMaterialDTO modificarNecesidad(
            String id,
            NecesidadMaterialDTO necesidadDTO
    ) {
        try {
            return donadoresYEntidadesClient.modificarNecesidad(
                    id,
                    necesidadDTO
            ).getBody();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public ProductoDTO registrarProducto(ProductoDTO productoDTO) {
        return donacionesClient.postProducto(productoDTO).getBody();
    }

    public String eliminarProducto(String id){
        try {
            donacionesClient.deleteProducto(id);
            return "El producto con ID " + id + " fue eliminada correctamente.";

        } catch (NumberFormatException e) {
            return "El ID del producto no es válido.";

        } catch (Exception e) {
            return "No se pudo eliminar el producto con ID " + id + ".";
        }
    }

    public ProductoDTO modificarProducto(ProductoDTO productoDTO){
        try {
            return donacionesClient.putProducto(productoDTO.id(), productoDTO).getBody();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public ResponseEntity<?> buscarProducto(String id){
        try {
            return donacionesClient.getProductoByID(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public ResponseEntity<List<ProductoDTO>> obtenerProductos(){
        try {
            return donacionesClient.getProductos();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public InsigniaDTO registrarInsignia(InsigniaDTO insignia) {
        return (InsigniaDTO) incentivosClient.postInsignia(insignia).getBody();
    }

    public String eliminarInsignia(String id){
        try {
            incentivosClient.deleteInsignia(id);
            return "La insignia con ID " + id + " fue eliminada correctamente.";

        } catch (NumberFormatException e) {
            return "El ID de la insignia no es válido.";

        } catch (Exception e) {
            return "No se pudo eliminar la insignia con ID " + id + ".";
        }
    }

    public InsigniaDTO modificarInsignia(InsigniaDTO insignia){
        try {
            return (InsigniaDTO) incentivosClient.putInsignia(insignia.id(), insignia).getBody();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public ResponseEntity<?> buscarInsignia(String id){
        try {
            return incentivosClient.getInsigniaById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public ResponseEntity<List<InsigniaDTO>> obtenerInsignias(){
        try {
            return incentivosClient.getInsignias();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Optional<PaqueteDTO> buscarPaquete(String id){
        try {
            return logisticaClient.buscarPaquete(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public List<PaqueteDTO> obtenerPaquetes(){
        try {
            return logisticaClient.getPaquetes();
        } catch (NumberFormatException e) {
            return null;
        }
    }
}