package ar.edu.utn.dds.k3003;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EntidadBeneficaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.config.TelegramClientProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@Lazy
public class TelegramBot extends TelegramLongPollingBot {
    private final Fachada fachada;
    private final String botUsername;
    private final String botToken;

    public TelegramBot(
            Fachada fachada,
            TelegramClientProperties properties
    ) {
        this.fachada = fachada;
        this.botUsername = properties.getBotUsername();
        this.botToken = properties.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.getMessage() == null ||
                update.getMessage().getText() == null) {
            return;
        }

        Long chatId = update.getMessage().getChatId();
        String texto = update.getMessage().getText().trim();

        String respuesta = procesarMensaje(texto);

        enviarMensaje(chatId, respuesta);
    }

    private String procesarMensaje(String texto) {

        if (texto.equalsIgnoreCase("/start")) {
            return """
                    Bienvenido al sistema de donaciones.

                    Comandos disponibles:

                    /entidad <id> - Buscar una entidad.
                    /entidades - Listar todas las entidades.
                    /necesidad <id> - Buscar una necesidad.
                    /donador <id> - Buscar un donador.
                    /donadores - Listar todos los donadores.

                    Ejemplo:
                    /entidad 1
                    """;
        }

        try {

            if (texto.startsWith("/entidad ")) {
                String id = obtenerId(texto);
                return obtenerEntidad(id);
            }

            if (texto.equalsIgnoreCase("/entidades")) {
                return obtenerEntidades();
            }

            if (texto.startsWith("/necesidad ")) {
                String id = obtenerId(texto);
                return obtenerNecesidad(id);
            }

            if (texto.startsWith("/donador ")) {
                String id = obtenerId(texto);
                return obtenerDonador(id);
            }

            if (texto.equalsIgnoreCase("/donadores")) {
                return obtenerDonadores();
            }

            return """
                    No reconocí el comando.

                    Escribí /start para consultar los comandos disponibles.
                    """;

        } catch (NumberFormatException e) {
            return "El ID indicado no es válido.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Ocurrió un error al procesar la solicitud.";
        }
    }

    private String obtenerId(String texto) {

        String[] partes = texto.split("\\s+");

        if (partes.length != 2) {
            throw new NumberFormatException();
        }

        Integer.parseInt(partes[1]);

        return partes[1];
    }

    private String obtenerEntidad(String id) {

        EntidadBeneficaDTO entidad = fachada.buscarEntidad(id);

        if (entidad == null) {
            return "No se encontró ninguna entidad con el ID " + id + ".";
        }

        return "Detalle de la entidad\n\n"
                + "ID: " + entidad.id() + "\n"
                + "Razón social: " + entidad.razonSocial() + "\n"
                + "Domicilio: " + entidad.domicilio() + "\n"
                + "Teléfono: " + entidad.telefono() + "\n"
                + "Correo: " + entidad.correo();
    }

    private String obtenerEntidades() {

        List<EntidadBeneficaDTO> entidades = fachada.obtenerEntidades();

        if (entidades == null || entidades.isEmpty()) {
            return "No hay entidades registradas.";
        }

        StringBuilder resultado =
                new StringBuilder("Entidades benéficas registradas\n\n");

        for (EntidadBeneficaDTO entidad : entidades) {

            resultado.append("ID: ")
                    .append(entidad.id())
                    .append("\n")
                    .append("Razón social: ")
                    .append(entidad.razonSocial())
                    .append("\n")
                    .append("Domicilio: ")
                    .append(entidad.domicilio())
                    .append("\n")
                    .append("Teléfono: ")
                    .append(entidad.telefono())
                    .append("\n")
                    .append("Correo: ")
                    .append(entidad.correo())
                    .append("\n\n");
        }

        return resultado.toString().trim();
    }

    private String obtenerNecesidad(String id) {

        NecesidadMaterialDTO necesidad =
                fachada.obtenerNecesidad(id);

        if (necesidad == null) {
            return "No se encontró ninguna necesidad con el ID " + id + ".";
        }

        return "Detalle de la necesidad\n\n"
                + "ID: " + necesidad.id() + "\n"
                + "Entidad: " + necesidad.entidadID() + "\n"
                + "Nivel de urgencia: " + necesidad.nivelDeUrgencia() + "\n"
                + "Descripción: " + necesidad.descripcion() + "\n"
                + "Cantidad objetivo: " + necesidad.cantidadObjetivo() + "\n"
                + "Producto solicitado: " + necesidad.productoSolicitadoID() + "\n"
                + "Tipo: " + necesidad.tipo();
    }

    private String obtenerDonador(String id) {

        DonadorDTO donador = fachada.buscarDonador(id);

        if (donador == null) {
            return "No se encontró ningún donador con el ID " + id + ".";
        }

        return "Detalle del donador\n\n"
                + "ID: " + donador.id() + "\n"
                + "Nombre: " + donador.nombre() + "\n"
                + "Apellido: " + donador.apellido();
    }

    private String obtenerDonadores() {

        List<DonadorDTO> donadores = fachada.obtenerDonadores();

        if (donadores == null || donadores.isEmpty()) {
            return "No hay donadores registrados.";
        }

        StringBuilder resultado =
                new StringBuilder("Donadores registrados\n\n");

        for (DonadorDTO donador : donadores) {

            resultado.append("ID: ")
                    .append(donador.id())
                    .append("\n")
                    .append("Nombre: ")
                    .append(donador.nombre())
                    .append(" ")
                    .append(donador.apellido())
                    .append("\n\n");
        }

        return resultado.toString().trim();
    }

    private void enviarMensaje(Long chatId, String texto) {

        SendMessage mensaje = new SendMessage();

        mensaje.setChatId(chatId.toString());
        mensaje.setText(texto);

        try {
            execute(mensaje);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}