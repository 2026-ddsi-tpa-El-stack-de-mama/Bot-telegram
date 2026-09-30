package ar.edu.utn.dds.k3003.clientes;


import ar.edu.utn.dds.k3003.catedra.dtos.logistica.PaqueteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "logistica", url = "${FACHADA_L}")
public interface LogisticaClient {
    @GetMapping("/paquetes/{id}")
    public Optional<PaqueteDTO> buscarPaquete(@PathVariable("id") String id);
}
