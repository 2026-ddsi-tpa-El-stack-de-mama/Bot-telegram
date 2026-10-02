package ar.edu.utn.dds.k3003;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.*;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.PaqueteDTO;
import ar.edu.utn.dds.k3003.config.TelegramClientProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

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

                ¿Qué quiere consultar?

                /1_donadores - Ver opciones de donadores.
                /2_entidades - Ver opciones de entidades.
                /3_necesidades - Ver opciones de necesidades.
                /4_productos - Ver opciones de productos.
                /5_insignias - Ver opciones de insignias.
                /6_paquetes - Ver opciones de paquetes.
                /7_donaciones - Ver opciones de donaciones.
                """;
        }

        if (texto.equalsIgnoreCase("/1_donadores ")) {
            return """
                Opciones para donadores:

                /registrar_donador <nombre> <apellido> <edad> <email> <nroDocumento> <domicilio> <estado> <categoria> - Registrarse como donador.
                /estadisticas <id> - Consultar tus estadísticas.
                /donador <id> - Buscar un donador por ID.
                /donadores - Listar todos los donadores.
                """;
        }

        if (texto.equalsIgnoreCase("/2_entidades ")) {
            return """
                Opciones de entidades:

                /registrar_entidad <razonSocial> <domicilio> <telefono> <correo> - Crear una entidad.
                /modificar_entidad <id> <razonSocial> <domicilio> <telefono> <correo> - Editar una entidad.
                /entidad <id> - Buscar una entidad.
                /entidades - Listar todas las entidades.
                """;
        }
        if (texto.equalsIgnoreCase("/3_necesidades ")) {
            return """
                Opciones de necesidades:

                /registrar_necesidad <entidadId> <urgencia> <descripcion> <cantidadObjetivo> <cantidadActual> <productoId> <tipo> - Alta de necesidad.
                /eliminar_necesidad <id> - Borrar una necesidad.
                /modificar_necesidad <id> <entidadId> <urgencia> <descripcion> <cantidad> <productoId> <tipo> - Modificar una necesidad.
                /necesidad <id> - Consultar una necesidad.
                """;
        }

        if (texto.equalsIgnoreCase("/4_productos ")) {
            return """
                Opciones de productos:

                /registrar_producto <nombre> <descripcion> <categoriaID> <identificadorID> - Alta de producto.
                /eliminar_producto <id> - Borrar un producto.
                /modificar_producto <id> <nombre> <descripcion> <categoriaID> <identificadorID> - Modificar un producto.
                /producto <id> - Consultar un producto.
                /productos - Listar todos los productos.
                """;
        }

        if (texto.equalsIgnoreCase("/5_insignias ")) {
            return """
                Opciones de insignias:

                /registrar_insignia <nombre> <descripcion> - Alta de insignia.
                /eliminar_insignia <id> - Borrar una insignia.
                /modificar_insignia <id> <nombre> <descripcion> - Modificar una insignia.
                /insignia <id> - Consultar una insignia.
                /insignias - Listar todas las insignias.
                """;
        }

        if (texto.equalsIgnoreCase("/6_paquetes ")) {
            return """
                Opciones de paquetes:

                /paquete <id> - Consultar un paquete.
                /paquetes - Listar todos los paquetes.
                """;
        }

        if (texto.equalsIgnoreCase("/7_donaciones ")) {
            return """
                Opciones de donaciones:

                /registrar_donacion <donadorID> <depositoID> <descripcion> <productoID> <cantidad> - Alta de donación.
                /donacion <id> - Consultar una donación.
                /donaciones - Listar todas las donaciones.
                /eliminar_donacion <id> - Borrar una donación.
                """;
        }

        try {
            //ENTIDADES ------------------------------------------------------
            if (texto.startsWith("/entidad ")) {
                String id = obtenerId(texto);
                return obtenerEntidad(id);
            }

            if (texto.equalsIgnoreCase("/entidades ")) {
                return obtenerEntidades();
            }

            if (texto.startsWith("/registrar_entidad ")) {
                return registrarEntidad(texto);
            }

            if (texto.startsWith("/modificar_entidad ")) {
                return modificarEntidad(texto);
            }

            //NECESIDADES -----------------------------------------------------
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

            //DONADORES ----------------------------------------
            if (texto.startsWith("/registrar_donador ")) {
                return registrarDonador(texto);
            }

            if (texto.startsWith("/donador ")) {
                String id = obtenerId(texto);
                return obtenerDonador(id);
            }

            if (texto.equalsIgnoreCase("/donadores ")) {
                return obtenerDonadores();
            }

            if (texto.startsWith("/estadisticas ")) {
                String id = obtenerId(texto);
                return fachada.obtenerEstadisticasDonador(id);
            }

            //PRODUCTOS ---------------------------------------------
            if(texto.startsWith("/registrar_producto ")){
                return registrarProducto(texto);
            }

            if(texto.startsWith("/eliminar_producto ")){
                String id = obtenerId(texto);
                return fachada.eliminarProducto(id);
            }

            if(texto.startsWith("/modificar_producto ")){
                return modificarProducto(texto);
            }

            if(texto.startsWith("/producto ")){
                String id = obtenerId(texto);
                return obtenerProducto(id);
            }

            if(texto.startsWith("/productos ")){
                return obtenerProductos();
            }

            //INSIGNIAS -----------------------------------------------
            if(texto.startsWith("/registrar_insignia ")){
                return registrarInsignia(texto);
            }

            if(texto.startsWith("/eliminar_insignia ")){
                String id = obtenerId(texto);
                return fachada.eliminarInsignia(id);
            }

            if(texto.startsWith("/modificar_insignia ")){
                return modificarInsignia(texto);
            }

            if(texto.startsWith("/insignia ")){
                String id = obtenerId(texto);
                return obtenerInsignia(id);
            }

            if(texto.startsWith("/insignias ")){
                return obtenerInsignias();
            }

            //PAQUETES ------------------------------------
            if(texto.startsWith("/paquete ")){
                String id = obtenerId(texto);
                return obtenerPaquete(id);
            }
            if(texto.startsWith("/paquetes ")){
                return obtenerPaquetes();
            }

            //DONACIONES -----------------------------------
            if(texto.startsWith("/registrar_donacion ")){
                return registrarDonacion(texto);
            }

            if(texto.startsWith("/donacion ")){
                String id = obtenerId(texto);
                return obtenerDonacion(id);
            }

            if(texto.startsWith("/donaciones ")){
                return obtenerDonaciones();
            }

            if(texto.startsWith("/eliminar_donacion ")){
                String id = obtenerId(texto);
                return fachada.eliminarDonacion(id);
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
                /registrar_donador Juan Perez 25 juan@mail.com 30123456 CalleFalsa123 ACTIVO or
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

    private String registrarProducto(String texto){
        String[] args = obtenerArgumentos(texto);

        if (args.length != 4) {
            return """
                    Uso incorrecto.

                    Formato:
                    /registrar_producto <nombre> <descripcion> <categoriaID> <identificadorID>

                    Ejemplo:
                    /registrar_producto fideos paquete 1 5
                    """;
        }

        ProductoDTO producto = new ProductoDTO(
                        null,
                        args[0],
                        args[1],
                        args[2],
                        args[3]
                );

        ProductoDTO registrado = fachada.registrarProducto(producto);

        if (registrado == null) {
            return "No se pudo registrar la entidad.";
        }

        return "Producto registrado correctamente.\n\n"
                + "ID: " + producto.id() + "\n";
    }

    private String modificarProducto(String texto){
        String[] args = obtenerArgumentos(texto);

        if (args.length != 5) {
            return """
                    Uso incorrecto.

                    Formato:
                    /registrar_producto <id> <nombre> <descripcion> <categoriaID> <identificadorID>

                    Ejemplo:
                    /registrar_producto 943 fideos paquete 1 5
                    """;
        }

        ProductoDTO producto = new ProductoDTO(
                args[0],
                args[1],
                args[2],
                args[3],
                args[4]
        );

        ProductoDTO modificado = fachada.modificarProducto(producto);

        if (modificado == null) {
            return "No se pudo registrar la entidad.";
        }

        return "Producto modificado correctamente.\n\n"
                + "ID: " + producto.id() + "\n";
    }

    public String obtenerProducto(String id){
        ResponseEntity<?> productoDTO = fachada.buscarProducto(id);

        if (productoDTO == null || productoDTO.getBody() == null) {
            return "No se encontró ningún producto con el ID " + id + ".";
        }

        ProductoDTO producto = (ProductoDTO) productoDTO.getBody();

        return "Detalle del producto\n\n"
                + "ID: " + id + "\n"
                + "Nombre: " + producto.nombre() + "\n"
                + "Descripción: " + producto.descripcion() + "\n"
                + "Categoria ID: " + producto.categoriaID() + "\n"
                + "Identificador ID: " + producto.identificadorID();
    }

    public String obtenerProductos(){
        List<ProductoDTO> productos = fachada.obtenerProductos().getBody();

        if (productos == null || productos.isEmpty()) {
            return "No hay productos registrados.";
        }

        StringBuilder resultado =
                new StringBuilder("Productos registrados\n\n");

        for (ProductoDTO producto : productos) {
            resultado.append("ID: ")
                    .append(producto.id())
                    .append("\n")
                    .append("Nombre: ")
                    .append(producto.nombre())
                    .append("\n")
                    .append("Descripcion: ")
                    .append(producto.descripcion())
                    .append("\n")
                    .append("Categoria ID: ")
                    .append(producto.categoriaID())
                    .append("\n")
                    .append("Identificador ID")
                    .append(producto.identificadorID())
                    .append("\n\n");
        }

        return resultado.toString().trim();
    }

    private String registrarInsignia(String texto) {

        String[] args = obtenerArgumentos(texto);

        if (args.length != 2) {
            return """
                    Uso incorrecto.

                    Formato:
                    /registrar_insignia <nombre> <descripcion>

                    Ejemplo:
                    /registrar_insignia especial premium
                    """;
        }

        InsigniaDTO insigniaDTO = new InsigniaDTO(
                        null,
                        args[0],
                        args[1]
                );

        InsigniaDTO registrada = fachada.registrarInsignia(insigniaDTO);

        if (registrada == null) {
            return "No se pudo registrar la insignia.";
        }

        return "Insignia registrada correctamente.\n\n"
                + "ID: " + registrada.id();
    }

    private String modificarInsignia(String texto){
        String[] args = obtenerArgumentos(texto);

        if (args.length != 3) {
            return """
                    Uso incorrecto.

                    Formato:
                    /registrar_insignia <id> <nombre> <descripcion> 

                    Ejemplo:
                    /registrar_insignia 747 especial premium
                    """;
        }

        InsigniaDTO insignia = new InsigniaDTO(
                args[0],
                args[1],
                args[2]
        );

        InsigniaDTO modificado = fachada.modificarInsignia(insignia);

        if (modificado == null) {
            return "No se pudo registrar la insignia.";
        }

        return "Insignia modificada correctamente.\n\n"
                + "ID: " + insignia.id() + "\n";
    }

    public String obtenerInsignia(String id){
        ResponseEntity<?> insignia = fachada.buscarInsignia(id);

        if (insignia == null || insignia.getBody() == null) {
            return "No se encontró ninguna insignia con el ID " + id + ".";
        }

        InsigniaDTO insigniaDTO = (InsigniaDTO) insignia.getBody();

        return "Detalle de la insignia\n\n"
                + "ID: " + id + "\n"
                + "Nombre: " + insigniaDTO.nombre() + "\n"
                + "Descripción: " + insigniaDTO.descripcion();
    }

    public String obtenerInsignias(){
        List<InsigniaDTO> insignias = fachada.obtenerInsignias().getBody();

        if (insignias == null || insignias.isEmpty()) {
            return "No hay insignias registradas.";
        }

        StringBuilder resultado = new StringBuilder("Insignias registradas\n\n");

        for (InsigniaDTO insignia : insignias) {
            resultado.append("ID: ")
                    .append(insignia.id())
                    .append("\n")
                    .append("Nombre: ")
                    .append(insignia.nombre())
                    .append("\n")
                    .append("Descripcion: ")
                    .append(insignia.descripcion());
        }

        return resultado.toString().trim();
    }

    public String obtenerPaquete(String id){
        Optional<PaqueteDTO> paqueteDTO = fachada.buscarPaquete(id);

        if (paqueteDTO.isEmpty()) {
            return "No se encontró ningun paquete con el ID " + id + ".";
        }

        return "Detalle del paquete: \n\n"
                + "ID: " + id + "\n"
                + "Donación ID: " + paqueteDTO.get().donacionID() + "\n"
                + "Producto: " + paqueteDTO.get().producto() + "\n"
                + "Cantidad: " + paqueteDTO.get().cantidad().toString();
    }

    public String obtenerPaquetes(){
        List<PaqueteDTO> paquetes = fachada.obtenerPaquetes();

        if (paquetes == null || paquetes.isEmpty()) {
            return "No hay paquetes registrados.";
        }

        StringBuilder resultado = new StringBuilder("Paquetes registrados: \n\n");

        for (PaqueteDTO paquete : paquetes) {
            resultado.append("ID: ")
                    .append(paquete.id())
                    .append("\n")
                    .append("Donación ID: ")
                    .append(paquete.donacionID())
                    .append("\n")
                    .append("Producto: ")
                    .append(paquete.producto())
                    .append("\n")
                    .append("Cantidad: ")
                    .append(paquete.cantidad());
        }

        return resultado.toString().trim();
    }

    private String registrarDonacion(String texto) {

        String[] args = obtenerArgumentos(texto);

        if (args.length != 5) {
            return """
                    Uso incorrecto.

                    Formato:
                    /registrar_donacion <donadorID> <depositoID> <descripcion> <productoID> <cantidad>

                    Ejemplo:
                    /registrar_donacion 254 334 fideos 245 5
                    """;
        }

        DonacionDTO donacionDTO = new DonacionDTO(
                null,
                args[0],
                args[1],
                args[2],
                args[3],
                Integer.valueOf(args[4]),
                EstadoDonacionEnum.INGRESADA
        );

        DonacionDTO registrada = fachada.registrarDonacion(donacionDTO);

        if (registrada == null) {
            return "No se pudo registrar la donación.";
        }

        return "Donacion registrada correctamente.\n\n"
                + "ID: " + registrada.id();
    }

    public String obtenerDonacion(String id){
        ResponseEntity<?> donacionDTO = fachada.buscarDonacion(id);

        if (donacionDTO == null) {
            return "No se encontró ninguna donación con el ID " + id + ".";
        }

        DonacionDTO donacion = (DonacionDTO) donacionDTO.getBody();

        return "Detalle de la donación\n\n"
                + "ID: " + id + "\n"
                + "Donador ID: " + donacion.donadorID() + "\n"
                + "Deposito ID: " + donacion.depositoID() + "\n"
                + "Descripción: " + donacion.descripcion() + "\n"
                + "Producto ID: " + donacion.productoID() + "\n"
                + "Cantidad: " + donacion.cantidad().toString() + "\n"
                + "Estado: " + donacion.estado().toString();
    }

    public String obtenerDonaciones(){
        ResponseEntity<?> donaciones = fachada.obtenerDonaciones();

        if (donaciones == null || donaciones.getBody() == null) {
            return "No hay donaciones registrados.";
        }
        List<DonacionDTO> donacionesList = (List<DonacionDTO>) donaciones.getBody();
        StringBuilder resultado = new StringBuilder("Donaciones registrados: \n\n");

        for (DonacionDTO donacion : donacionesList) {
            resultado.append("ID: ")
                    .append(donacion.id())
                    .append("\n")
                    .append("Donador ID: ")
                    .append(donacion.donadorID())
                    .append("\n")
                    .append("Depósito ID: ")
                    .append(donacion.depositoID())
                    .append("\n")
                    .append("Descripción: ")
                    .append(donacion.descripcion())
                    .append("\n")
                    .append("Producto ID: ")
                    .append(donacion.productoID())
                    .append("\n")
                    .append("Cantidad: ")
                    .append(donacion.cantidad())
                    .append("\n")
                    .append("Estado: ")
                    .append(donacion.estado());
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