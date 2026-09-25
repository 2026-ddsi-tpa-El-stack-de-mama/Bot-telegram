package ar.edu.utn.dds.k3003;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.*;
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

                ¿Qué tipo de usuario sos?

                /donador - Ver opciones para donadores.
                /admin - Ver opciones de administración.
                """;
        }

        if (texto.equalsIgnoreCase("/donador")) {
            return """
                Opciones para donadores:

                /registrar_donador <nombre> <apellido> <edad> ... - Registrarse como donador.
                /estadisticas <id> - Consultar tus estadísticas.
                /donador <id> - Buscar un donador por ID.
                /donadores - Listar todos los donadores.
                """;
        }

        if (texto.equalsIgnoreCase("/admin")) {
            return """
                Opciones de administración:

                /registrar_entidad <razonSocial> <domicilio> <telefono> <correo> - Crear una entidad.
                /modificar_entidad <id> <razonSocial> <domicilio> <telefono> <correo> - Editar una entidad.
                /entidad <id> - Buscar una entidad.
                /entidades - Listar todas las entidades.
                /registrar_necesidad <entidadId> <urgencia> <descripcion> <cantidadObjetivo> <cantidadActual> <productoId> <tipo> - Alta de necesidad.
                /eliminar_necesidad <id> - Borrar una necesidad.
                /modificar_necesidad <id> <entidadId> <urgencia> <descripcion> <cantidad> <productoId> <tipo> - Modificar una necesidad.
                /necesidad <id> - Consultar una necesidad.
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

            if (texto.startsWith("/eliminar_necesidad ")) {
                String id = obtenerId(texto);
                return fachada.eliminarNecesidad(id);
            }

            if (texto.startsWith("/registrar_necesidad ")) {
                return registrarNecesidad(texto);
            }

            if (texto.startsWith("/modificar_necesidad ")) {
                return modificarNecesidad(texto);
            }

            if (texto.startsWith("/registrar_donador ")) {
                return registrarDonador(texto);
            }

            if (texto.startsWith("/registrar_entidad ")) {
                return registrarEntidad(texto);
            }

            if (texto.startsWith("/donador ")) {
                String id = obtenerId(texto);
                return obtenerDonador(id);
            }

            if (texto.startsWith("/modificar_entidad ")) {
                return modificarEntidad(texto);
            }

            if (texto.equalsIgnoreCase("/donadores")) {
                return obtenerDonadores();
            }

            if (texto.startsWith("/estadisticas ")) {
                String id = obtenerId(texto);
                return fachada.obtenerEstadisticasDonador(id);
            }

            return """
                No reconocí el comando.

                Escribí /start para ver las opciones disponibles.
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

        return partes[1];
    }

    private String[] obtenerArgumentos(String texto) {

        String[] partes = texto.split("\\s+");

        if (partes.length < 2) {
            throw new IllegalArgumentException(
                    "Faltan argumentos para el comando."
            );
        }

        String[] argumentos = new String[partes.length - 1];

        System.arraycopy(
                partes,
                1,
                argumentos,
                0,
                argumentos.length
        );

        return argumentos;
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

    private String registrarNecesidad(String texto) {

        String[] args = obtenerArgumentos(texto);

        if (args.length != 7) {
            return """
                    Uso incorrecto.

                    Formato:
                    /registrar_necesidad <entidadId> <urgencia> <descripcion> <cantidadObjetivo> <cantidadActual> <productoId> <tipo>

                    Ejemplo:
                    /registrar_necesidad 1 7 alimentos 10 25 3 EXTRAORDINARIA
                    """;
        }

        NecesidadMaterialDTO necesidad = new NecesidadMaterialDTO(
                null,
                args[0],
                Integer.valueOf(args[1]),
                args[2],
                Integer.valueOf(args[3]),
                Integer.valueOf(args[4]),
                args[5],
                TipoNecesidadMaterialEnum.valueOf(args[6])
        );

        NecesidadMaterialDTO creada =
                fachada.registrarNecesidad(necesidad);

        if (creada == null) {
            return "No se pudo registrar la necesidad.";
        }

        return "Necesidad registrada correctamente.\n\n"
                + "ID: " + creada.id();
    }

    private String modificarNecesidad(String texto) {

        String[] args = obtenerArgumentos(texto);

        if (args.length != 8) {
            return """
                    Uso incorrecto.

                    Formato:
                    /modificar_necesidad <id> <entidadId> <urgencia> <descripcion> <cantidadObjetivo> <cantidadActual> <productoId> <tipo>
                    """;
        }

        String id = args[0];

        NecesidadMaterialDTO necesidad = new NecesidadMaterialDTO(
                null,
                args[0],
                Integer.valueOf(args[1]),
                args[2],
                Integer.valueOf(args[3]),
                Integer.valueOf(args[4]),
                args[5],
                TipoNecesidadMaterialEnum.valueOf(args[6])
        );

        NecesidadMaterialDTO modificada =
                fachada.modificarNecesidad(id, necesidad);

        if (modificada == null) {
            return "No se pudo modificar la necesidad con ID " + id + ".";
        }

        return "Necesidad modificada correctamente.\n\n"
                + "ID: " + modificada.id();
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

    private String registrarDonador(String texto) {

        String[] args = obtenerArgumentos(texto);

        if (args.length != 8) {
            return """
                Uso incorrecto.

                Formato:
                /registrar_donador <nombre> <apellido> <edad> <email> <nroDocumento> <domicilio> <estado> <categoria>

                Ejemplo:
                /registrar_donador Juan Perez 25 juan@mail.com 30123456 CalleFalsa123 VERIFICADO ORO
                """;
        }

        DonadorDTO donador;
        try {
            donador = new DonadorDTO(
                null,
                args[0],
                args[1],
                Integer.valueOf(args[2]),
                args[3],
                args[4],
                args[5],
                EstadoDonadorEnum.valueOf(args[6]),
                args[7]
            );
        } catch (IllegalArgumentException e) {
            return "El estado indicado no es válido. Estados posibles: " +
                java.util.Arrays.toString(EstadoDonadorEnum.values());
        }

        DonadorDTO registrado = fachada.registrarDonador(donador);

        if (registrado == null) {
            return "No se pudo registrar el donador.";
        }

        return "Donador registrado correctamente.\n\n"
            + "ID: " + registrado.id() + "\n"
            + "Nombre: " + registrado.nombre() + " "
            + registrado.apellido();
    }

    private String registrarEntidad(String texto) {

        String[] args = obtenerArgumentos(texto);

        if (args.length != 4) {
            return """
                    Uso incorrecto.

                    Formato:
                    /registrar_entidad <razonSocial> <domicilio> <telefono> <correo>

                    Ejemplo:
                    /registrar_entidad ComedorSolidario Calle123 1112345678 correo@mail.com
                    """;
        }

        EntidadBeneficaDTO entidad =
                new EntidadBeneficaDTO(
                        null,
                        args[0],
                        args[1],
                        args[2],
                        args[3]
                );

        EntidadBeneficaDTO registrada =
                fachada.registrarEntidad(entidad);

        if (registrada == null) {
            return "No se pudo registrar la entidad.";
        }

        return "Entidad registrada correctamente.\n\n"
                + "ID: " + registrada.id() + "\n"
                + "Razón social: " + registrada.razonSocial();
    }

    private String modificarEntidad(String texto) {

        String[] args = obtenerArgumentos(texto);

        if (args.length != 5) {
            return """
                    Uso incorrecto.

                    Formato:
                    /modificar_entidad <id> <razonSocial> <domicilio> <telefono> <correo>

                    Ejemplo:
                    /modificar_entidad 1 ComedorNuevo Calle456 1112345678 nuevo@mail.com
                    """;
        }

        String id = args[0];

        EntidadBeneficaDTO entidad =
                new EntidadBeneficaDTO(
                        null,
                        args[1],
                        args[2],
                        args[3],
                        args[4]
                );

        EntidadBeneficaDTO modificada =
                fachada.modificarEntidad(id, entidad);

        if (modificada == null) {
            return "No se pudo modificar la entidad con ID " + id + ".";
        }

        return "Entidad modificada correctamente.\n\n"
                + "ID: " + modificada.id() + "\n"
                + "Razón social: " + modificada.razonSocial();
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