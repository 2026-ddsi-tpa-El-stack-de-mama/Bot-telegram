package ar.edu.utn.dds.k3003;
import ar.edu.utn.dds.k3003.config.TelegramClientProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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

                    También podés utilizar los comandos para consultar información del sistema.
                    """;
        }

        try {

            if (texto.startsWith("/entidad ")) {
                Integer id = obtenerId(texto);
                return fachada.buscarEntidadPorId(id);
            }

            if (texto.equalsIgnoreCase("/entidades")) {
                return fachada.buscarTodasLasEntidades();
            }

            if (texto.startsWith("/necesidad ")) {
                Integer id = obtenerId(texto);
                return fachada.buscarNecesidadPorId(id);
            }

            if (texto.startsWith("/donador ")) {
                Integer id = obtenerId(texto);
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

    private Integer obtenerId(String texto) {
        String[] partes = texto.split("\\s+");

        if (partes.length != 2) {
            throw new NumberFormatException();
        }

        return Integer.parseInt(partes[1]);
    }

    private String obtenerDonador(Integer id) {

        var donador = fachada.buscarDonadorPorId(id);

        if (donador == null) {
            return "No se encontró ningún donador con el ID " + id + ".";
        }

        return "Detalle del donador\n\n"
                + "ID: " + donador.id() + "\n"
                + "Nombre: " + donador.nombre() + "\n"
                + "Apellido: " + donador.apellido();
    }

    private String obtenerDonadores() {

        var donadores = fachada.buscarTodosLosDonadores();

        if (donadores == null || donadores.isEmpty()) {
            return "No hay donadores registrados.";
        }

        StringBuilder resultado =
                new StringBuilder("Donadores registrados\n\n");

        for (var donador : donadores) {
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