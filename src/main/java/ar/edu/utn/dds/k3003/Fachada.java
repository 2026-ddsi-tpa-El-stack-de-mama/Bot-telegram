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

    public DonadorDTO registrarDonador(DonadorDTO donadorDTO) {
        return donadoresYEntidadesClient.guardarDonador(donadorDTO);
    }

    public DonadorDTO buscarDonadorPorId(Integer id) {
        return donadoresYEntidadesClient.buscarDonadorPorId(id);
    }

    public List<DonadorDTO> buscarTodosLosDonadores() {
        return donadoresYEntidadesClient.buscarTodosLosDonadores();
    }

    public String obtenerEstadisticasDonador(Integer id) {
        DonadorStatsDTO estadisticas = donadoresYEntidadesClient.obtenerEstadisticas(id);

        if (estadisticas == null) {
            return "No se encontraron estadísticas para el donador con ID " + id + ".";
        }

        String insignias = estadisticas.insigniasID() != null
                && !estadisticas.insigniasID().isEmpty()
                ? String.join(", ", estadisticas.insigniasID())
                : "Sin insignias";

        String misionActual = estadisticas.misionActualID() != null
                ? estadisticas.misionActualID()
                : "Sin misión activa";

        return "Estadísticas del donador\n\n"
                + "ID: " + estadisticas.id() + "\n"
                + "Nombre: " + estadisticas.nombre() + " " + estadisticas.apellido() + "\n"
                + "Edad: " + estadisticas.edad() + " años\n"
                + "Estado: " + estadisticas.estado() + "\n"
                + "Categoría: " + estadisticas.categoria() + "\n"
                + "Misión actual: " + misionActual + "\n"
                + "Insignias: " + insignias;
    }

    public String crearEntidad(
            String razonSocial,
            String domicilio,
            String telefono,
            String correo
    ) {
        EntidadBeneficaDTO entidad = new EntidadBeneficaDTO(
                null,
                razonSocial,
                domicilio,
                telefono,
                correo
        );

        EntidadBeneficaDTO creada = donadoresYEntidadesClient.guardarEntidad(entidad);

        if (creada == null) {
            return "No se pudo crear la entidad.";
        }

        return "Entidad creada correctamente.\n\n"
                + formatearEntidad(creada);
    }

    public String editarEntidad(
            Integer id,
            String razonSocial,
            String domicilio,
            String telefono,
            String correo
    ) {
        try {
            EntidadBeneficaDTO entidad = new EntidadBeneficaDTO(
                    id,
                    razonSocial,
                    domicilio,
                    telefono,
                    correo
            );

            EntidadBeneficaDTO actualizada =
                    donadoresYEntidadesClient.editarEntidad(id, entidad);

            if (actualizada == null) {
                return "No existe ninguna entidad con el ID " + id + ".";
            }

            return "Entidad actualizada correctamente.\n\n"
                    + formatearEntidad(actualizada);

        } catch (Exception e) {
            return "No se pudo actualizar la entidad con ID " + id + ".";
        }
    }

    public String buscarEntidadPorId(Integer id) {
        EntidadBeneficaDTO entidad =
                donadoresYEntidadesClient.buscarEntidadPorId(id);

        if (entidad == null) {
            return "No se encontró ninguna entidad con el ID " + id + ".";
        }

        return "Detalle de la entidad\n\n"
                + formatearEntidad(entidad);
    }

    public String buscarTodasLasEntidades() {
        List<EntidadBeneficaDTO> entidades =
                donadoresYEntidadesClient.buscarTodasLasEntidades();

        if ((entidades == null) || entidades.isEmpty()) {
            return "No hay entidades registradas en el sistema.";
        }

        StringBuilder resultado = new StringBuilder();

        resultado.append("Entidades benéficas (")
                .append(entidades.size())
                .append(")\n\n");

        for (EntidadBeneficaDTO entidad : entidades) {
            resultado.append(formatearEntidad(entidad))
                    .append("\n")
                    .append("--------------------------------\n");
        }

        return resultado.toString().trim();
    }

    private String formatearEntidad(EntidadBeneficaDTO entidad) {
        return "ID: " + entidad.id() + "\n"
                + "Razón social: " + entidad.razonSocial() + "\n"
                + "Domicilio: " + entidad.domicilio() + "\n"
                + "Teléfono: " + entidad.telefono() + "\n"
                + "Correo: " + entidad.correo();
    }

    public String crearNecesidad(
            String entidadId,
            Integer nivelDeUrgencia,
            String descripcion,
            Integer cantidadObjetivo,
            String productoSolicitadoId,
            TipoNecesidadMaterialEnum tipo
    ) {
        NecesidadMaterialDTO necesidad = new NecesidadMaterialDTO(
                null,
                entidadId,
                nivelDeUrgencia,
                descripcion,
                cantidadObjetivo,
                0,
                productoSolicitadoId,
                tipo
        );

        NecesidadMaterialDTO creada =
                donadoresYEntidadesClient.guardarNecesidad(necesidad);

        if (creada == null) {
            return "No se pudo registrar la necesidad.";
        }

        Integer cantidadRecibida = creada.cantidadRecibida() != null
                ? creada.cantidadRecibida()
                : 0;

        return "Necesidad registrada correctamente.\n\n"
                + "ID: " + creada.id() + "\n"
                + "Entidad: " + creada.entidadID() + "\n"
                + "Nivel de urgencia: " + creada.nivelDeUrgencia() + "\n"
                + "Descripción: " + creada.descripcion() + "\n"
                + "Cantidad objetivo: " + creada.cantidadObjetivo() + "\n"
                + "Cantidad recibida: " + cantidadRecibida + "\n"
                + "Producto solicitado: " + creada.productoSolicitadoID() + "\n"
                + "Tipo: " + creada.tipo();
    }

    public String editarNecesidad(
            Integer id,
            Integer nivelDeUrgencia,
            String descripcion
    ) {
        try {
            NecesidadMaterialDTO necesidad = new NecesidadMaterialDTO(
                    id,
                    null,
                    nivelDeUrgencia,
                    descripcion,
                    null,
                    null,
                    null,
                    null
            );

            NecesidadMaterialDTO actualizada =
                    donadoresYEntidadesClient.editarNecesidad(id, necesidad);

            if (actualizada == null) {
                return "No existe ninguna necesidad con el ID " + id + ".";
            }

            return "Necesidad actualizada correctamente.\n\n"
                    + "ID: " + actualizada.id() + "\n"
                    + "Nivel de urgencia: " + actualizada.nivelDeUrgencia() + "\n"
                    + "Descripción: " + actualizada.descripcion();

        } catch (Exception e) {
            return "No se pudo actualizar la necesidad con ID " + id + ".";
        }
    }

    public String borrarNecesidadPorId(Integer id) {
        try {
            donadoresYEntidadesClient.borrarNecesidad(id);
            return "La necesidad con ID " + id + " fue eliminada correctamente.";

        } catch (Exception e) {
            return "No se pudo eliminar la necesidad con ID " + id + ".";
        }
    }

    public String buscarNecesidadPorId(Integer id) {
        NecesidadMaterialDTO necesidad =
                donadoresYEntidadesClient.buscarNecesidadPorId(id);

        if (necesidad == null) {
            return "No se encontró ninguna necesidad con el ID " + id + ".";
        }

        return "Detalle de la necesidad\n\n"
                + "ID: " + necesidad.id() + "\n"
                + "Entidad: " + necesidad.entidadID() + "\n"
                + "Nivel de urgencia: " + necesidad.nivelDeUrgencia() + "\n"
                + "Descripción: " + necesidad.descripcion() + "\n"
                + "Cantidad objetivo: " + necesidad.cantidadObjetivo() + "\n"
                + "Cantidad recibida: " + necesidad.cantidadRecibida() + "\n"
                + "Producto solicitado: " + necesidad.productoSolicitadoID() + "\n"
                + "Tipo: " + necesidad.tipo();
    }

}