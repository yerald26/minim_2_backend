package Manager;

import BDD.orm.Session;
import BDD.orm.dao.IItemDAO;
import BDD.orm.dao.IUsuarioDAO;
import BDD.orm.dao.ItemDAOImpl;
import BDD.orm.dao.UsuarioDAOImpl;
import Model.Evento;
import Model.InscripcionRequest;
import Model.Item;
import Model.User;
import org.apache.log4j.Logger;
import java.util.List;
import java.util.ArrayList;
import java.util.*;


public class JuegoManagerImpl implements JuegoManager {

    private static JuegoManager instance;
    private static Session conn;
    final static Logger log = Logger.getLogger(JuegoManagerImpl.class.getName());

    // Estructuras de datos
    private IUsuarioDAO usuarioDAO;
    private IItemDAO itemDAO;
    // Constructor privado
    private JuegoManagerImpl() {
        this.usuarioDAO = new UsuarioDAOImpl();
        this.itemDAO = new ItemDAOImpl();
    }

    public static JuegoManager getInstance() {
        if (instance == null) {
            instance = new JuegoManagerImpl();
            log.info("Nueva instancia creada.");
        }
        return instance;
    }

    @Override
    public boolean registrarUsuario(User u) {
        log.info("INICIO registrarUsuario: nombre=" + u.getNombre());

        if (usuarioDAO.getUsuario(u.getNombre()) != null) {
            log.warn("FIN registrarUsuario: ERROR - El usuario ya existe.");
            return false;
        }

        usuarioDAO.addUsuario(u.getNombre(), u.getPassword(), u.getMail());
        log.info("FIN registrarUsuario: Usuario registrado correctamente.");
        return true;
    }

    @Override
    public User consultarUsuario(String nombre) {
        log.info("Consulta usuario: " + nombre);
        return usuarioDAO.getUsuario(nombre);    }

    @Override
    public User procesarLogin(String nombre, String password) {
        log.info("INICIO procesarLogin: nombre=" + nombre);
        User u = usuarioDAO.getUsuario(nombre);
        if (u != null && u.getPassword().equals(password)) {
            log.info("FIN procesarLogin: Login exitoso.");

            return u;
        }
        return null;
    }
    @Override
    public boolean comprarObjeto(String nombreJugador, String nombreObjeto) {
        User jugador = usuarioDAO.getUsuario(nombreJugador);

        // ¡OJO! Tu ItemDAOImpl no tiene getItemPorNombre, usa el metodo genérico getItem
        Item item = itemDAO.getItem("nombre", nombreObjeto);

        if (jugador != null && item != null) {
            if (jugador.getMonedas() >= item.getPrecio()) {

                // Casteamos a (int) porque tu item.getPrecio() devuelve double
                jugador.setMonedas(jugador.getMonedas() - (int)item.getPrecio());

                // Guardamos en Base de Datos
                usuarioDAO.updateUsuario(jugador);
                usuarioDAO.añadirItemAInventario(jugador.getId(), item.getId());

                return true;
            }
        }
        return false;
    }
    @Override
    public List<String> obtenerInventarioUsuario(String nombreUsuario) {
        User u = usuarioDAO.getUsuario(nombreUsuario);
        if(u != null) {
            return usuarioDAO.getNombresItemsUsuario(u.getId());
        }
        return new ArrayList<>(); // Devuelve vacío si no existe
    }

    @Override
    public int obtenerNumeroUsuarios() {
        List<User> lista = usuarioDAO.getUsuarios();
        return (lista != null) ? lista.size() : 0;
    }
    @Override
    public List<User> obtenerUsuarios(){
        List<User> lista = usuarioDAO.getUsuarios();
        return lista;
    }

    @Override
    public void clear() {
    }

    @Override
    public List<Item> obtenerItemsTienda() {
        System.out.println(this.itemDAO.getItems());
        return this.itemDAO.getItems();
    }

    @Override
    public java.util.List<Model.Evento> obtenerListaEventos(){
        java.util.List<Model.Evento> listaDummies = new java.util.ArrayList<>();

        Model.Evento evento1 = new Model.Evento (
                "EV-01",
                "Evento de Verano",
                "Juega al TempleRun ya para conseguir monedas en este nuevo evento de verano",
                "2026-06-02 17:00",
                "2026-09-30 17:00",
                "https://png.pngtree.com/background/20230319/original/pngtree-beautiful-beach-hot-sunny-scenery-picture-image_2150869.jpg"
        );
        Model.Evento evento2 = new Model.Evento (

                "EV-02",
                "Evento de Halloween",
                "Juega al TempleRun ya para conseguir monedas en este nuevo evento de halloween ",
                "2026-10-15 17:00",
                "2026-11-15 17:00",
                "https://cdn.pixabay.com/photo/2017/10/10/16/55/halloween-2837936_1280.png"
        );

        listaDummies.add(evento1);
        listaDummies.add(evento2);

        return listaDummies;
    }

    @Override
    public boolean registrarInscripcion (InscripcionRequest request){
        return true;
    }
}






