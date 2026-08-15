package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorStatsDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EntidadBeneficaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.TipoNecesidadMaterialEnum;
import ar.edu.utn.dds.k3003.clientes.DonadoresYEntidadesClient;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Fachada {

    private final DonadoresYEntidadesClient donadoresYEntidadesClient;

    public Fachada(DonadoresYEntidadesClient donadoresYEntidadesClient) {
        this.donadoresYEntidadesClient = donadoresYEntidadesClient;
    }

    public DonadorDTO registrarDonador(DonadorDTO donadorDTO) {
        return donadoresYEntidadesClient.agregarDonador(donadorDTO).getBody();
    }

    public String obtenerEstadisticasDonador(String id) {
        try {
            Integer donadorId = Integer.valueOf(id);

            DonadorStatsDTO estadisticas = donadoresYEntidadesClient.estadisticas(String.valueOf(donadorId)).getBody();

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
            return donadoresYEntidadesClient.buscarDonador(String.valueOf(Integer.valueOf(id))).getBody();
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
                    String.valueOf(Integer.valueOf(id)),
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
            return donadoresYEntidadesClient.buscarEntidad(String.valueOf(Integer.valueOf(id))).getBody();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public ResponseEntity<EntidadBeneficaDTO> obtenerNecesidad(String id) {
        try {
            return donadoresYEntidadesClient.buscarEntidad(String.valueOf(Integer.valueOf(id)));
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
            donadoresYEntidadesClient.eliminarNecesidad(String.valueOf(Integer.valueOf(id)));

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
                    String.valueOf(Integer.valueOf(id)),
                    necesidadDTO
            ).getBody();
        } catch (NumberFormatException e) {
            return null;
        }
    }

}