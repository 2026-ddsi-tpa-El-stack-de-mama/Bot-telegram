package ar.edu.utn.dds.k3003.clientes;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "incentivos", url = "${FACHADA_I}")
public interface IncentivosClient {

    @PostMapping("/insignias")
    public ResponseEntity<?> postInsignia(@RequestBody InsigniaDTO insigniaDTO);

    @PutMapping("/insignias/{id}")
    public ResponseEntity<?> putInsignia(@PathVariable("id") String insigniaID, @RequestBody InsigniaDTO insigniaDTO);

    @DeleteMapping("/insignias/{id}")
    public ResponseEntity<?> deleteInsignia(@PathVariable("id") String insigniaID);

    @GetMapping("/insignias")
    public ResponseEntity<List<InsigniaDTO>> getInsignias();

    @GetMapping("/insignias/{id}")
    public ResponseEntity<?> getInsigniaById(@PathVariable("id") String insigniaID);

    @PostMapping("/misiones")
    public ResponseEntity<?> postMision(@RequestBody MisionDTO misionDTO);

    @PutMapping("/misiones/{id}")
    public ResponseEntity<?> putMision(@PathVariable("id") String misionID, @RequestBody MisionDTO misionDTO);

    @DeleteMapping("/misiones/{id}")
    public ResponseEntity<?> deleteMision(@PathVariable("id") String misionID);

    @GetMapping("/misiones")
    public ResponseEntity<List<MisionDTO>> getMisiones();

    @GetMapping("/misiones/{id}")
    public ResponseEntity<?> getMisionById(@PathVariable("id") String misionID);

}
