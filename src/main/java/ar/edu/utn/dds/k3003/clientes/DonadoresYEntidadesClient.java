package ar.edu.utn.dds.k3003.clientes;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorStatsDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EntidadBeneficaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "donadoresYEntidades", url = "${FACHADA_DYE}")
public interface DonadoresYEntidadesClient {

    @GetMapping("/necesidades/insatisfechas")
    List<NecesidadMaterialDTO> obtenerNecesidadesInsatisfechasDe(@RequestParam String productoId);

    @PostMapping("necesidades/{necesidadID}/satisfaccion")
    NecesidadMaterialDTO satisfacerNecesidad(@PathVariable String necesidadID, @RequestParam Integer cantidad);

    @GetMapping("/necesidades/{necesidadID}")
    public ResponseEntity<NecesidadMaterialDTO> obtenerNecesidad(@PathVariable String necesidadID);

    @PostMapping("/donadores")
    public ResponseEntity<DonadorDTO> agregarDonador(@RequestBody DonadorDTO donadorDTO);

    @GetMapping("/donadores/{id}/estadisticas")
    public ResponseEntity<DonadorStatsDTO> estadisticas(@PathVariable String id);

    @GetMapping("donadores/{id}")
    public ResponseEntity<DonadorDTO> buscarDonador(@PathVariable String id);

    @GetMapping("/donadores")
    public ResponseEntity<List<DonadorDTO>> obtenerDonadores();

    @PostMapping("/entidades")
    public ResponseEntity<EntidadBeneficaDTO> agregarEntidad(@RequestBody EntidadBeneficaDTO entidadDTO);

    @PutMapping("/entidades/{id}")
    public ResponseEntity<EntidadBeneficaDTO> modificarEntidad(@PathVariable String id, @RequestBody EntidadBeneficaDTO entidadDTO);

    @GetMapping("/entidades")
    public ResponseEntity<List<EntidadBeneficaDTO>> obtenerEntidades();

    @GetMapping("/entidades/{id}")
    public ResponseEntity<EntidadBeneficaDTO> buscarEntidad(@PathVariable String id);

    @PostMapping("/necesidades")
    public ResponseEntity<NecesidadMaterialDTO> registrarNecesidad(@RequestBody NecesidadMaterialDTO necesidadDTO);

    @DeleteMapping("/necesidades/{necesidadID}")
    public ResponseEntity<String> eliminarNecesidad(@PathVariable String necesidadID);

    @PutMapping("/necesidades/{necesidadID}")
    public ResponseEntity<NecesidadMaterialDTO> modificarNecesidad(@PathVariable String necesidadID, @RequestBody NecesidadMaterialDTO necesidadDTO);

}
