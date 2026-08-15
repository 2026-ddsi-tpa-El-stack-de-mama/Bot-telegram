package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorStatsDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EntidadBeneficaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.TipoNecesidadMaterialEnum;
import ar.edu.utn.dds.k3003.clientes.DonadoresYEntidadesClient;


import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Fachada {

    private final DonadoresYEntidadesClient donadoresYEntidadesClient;

    public Fachada(DonadoresYEntidadesClient donadoresYEntidadesClient) {
        this.donadoresYEntidadesClient = donadoresYEntidadesClient;
    }

}