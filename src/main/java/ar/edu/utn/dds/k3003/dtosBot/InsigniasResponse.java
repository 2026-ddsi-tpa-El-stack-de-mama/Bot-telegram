package ar.edu.utn.dds.k3003.dtosBot;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;

import java.util.List;

public record InsigniasResponse(
        List<InsigniaDTO> data,
        String request_id
) {}