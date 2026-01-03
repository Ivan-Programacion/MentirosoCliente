package programa_cliente;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Cliente {

	final static String IP = "192.168.1.46"; // CAMBIAR DEPENDIENDO DE LA RED: cmd -> ipconfig -> ipv4
	final static String PUERTO = "8080"; // PUERTO POR DEFECTO: 8080
	static int ID_PARTIDA;
	static HttpClient cliente = HttpClient.newHttpClient();
	static HttpRequest peticion;
	static HttpResponse<String> respuesta = null;
	static Scanner sc = new Scanner(System.in);
	static String nombre;
	static int id;
	static ArrayList<String> cartas;
	static ArrayList<String> listaCartas = new ArrayList<>(
			Arrays.asList("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"));

	public static void main(String[] args) {
		System.out.println("Comprobando conexión...");
		conexion();
		int opcion = 0;
		System.out.println(); // Salto línea
		System.out.println("Bienvenido al juego del mentiroso!");
		while (opcion != 3) {
			try {
				opcion = menu();
				if (opcion < 1 || opcion > 3) {
					System.out.println("Escribe un valor correcto");
					System.out.println(); // salto línea
				} else {
					opciones(opcion);
				}
			} catch (NumberFormatException e) {
				System.out.println("Escribe un valor correcto");
				System.out.println(); // salto línea
			}
		}
	}

	private static void opciones(int opcion) {
		System.out.println(); // Salto línea
		if (opcion == 1) {
			crearPartida();
		} else if (opcion == 2) {
			unirPartida();
		} else {

		}

	}

	private static int menu() throws NumberFormatException {
		System.out.println("-- Elige una de las opciones --");
		System.out.println("1. Crear partida");
		System.out.println("2. Unirse a partida");
		System.out.println("3. Salir");
		return Integer.parseInt(sc.nextLine());
	}

	private static String endPoint(String url) {
		String contenido = null;
		try {
			peticion = HttpRequest.newBuilder().uri(URI.create(url)).build();
			respuesta = cliente.send(peticion, BodyHandlers.ofString());
			contenido = respuesta.body();
//			System.out.println(contenido);
		} catch (ConnectException e) { // si hay error de conexión
			System.out.println("ERROR DE CONEXIÓN. Intenta entrar más tarde");
			System.exit(-1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return contenido;
	}

	// Método que hace una primera llamada para comprobar la conexión
	private static void conexion() {
		// Llama al método conexión del servidor
		String url = String.format("http://%s:%s/conexion", IP, PUERTO);
		System.out.println(endPoint(url));
	}

	/**
	 * Método para crear la partida que muestre el id de usuario correspondiente,
	 * las cartas recibidas por el servidor y el id de la partida
	 */
	private static void crearPartida() {
		System.out.print("Introduce el nombre de jugador: ");
		nombre = sc.nextLine();
		String url = String.format("http://%s:%s/crearPartida/%s", IP, PUERTO, nombre);
		String respuesta = endPoint(url);
		// CAMBIOS AQUÍ: se divide con : para que podamos reutilizar siempre el método
		// de repartirCartas más adelante
		// De tal forma que, todo lo que vaya después de los : van a ser cartas (aunque
		// después haya más cosas).
		// Ejemplo de respuesta justo abajo (descomentar el syso y probad a crear
		// partida)
//		System.out.println(respuesta); // PRUEBA ------------------------------------------------
		System.out.println(); // Salto línea
		String[] partes = respuesta.split(":");
		id = Integer.parseInt(partes[0]);
		String[] cartasMasId = partes[1].split(",");
		ID_PARTIDA = Integer.parseInt(cartasMasId[cartasMasId.length - 1]);
		System.out.println("El ID de la partida es: " + ID_PARTIDA);
		repartirCartas(cartasMasId);
		comprobarTurno();
	}

	private static void comprobarTurno() {
		System.out.println("Entrando en partida");
		boolean estado = true;
		while (estado) {
//			System.err.println("IMPRIMIENDO ID: " + id); // PRUEBA --------------------------------------
			String url = String.format("http://%s:%s/comprobarTurno/%d/%d", IP, PUERTO, id, ID_PARTIDA);
			String respuesta = endPoint(url);
//			System.out.println(respuesta); // PRUEBA ----------------------------------------------------
			System.out.println(); // salto línea
			System.out.println("Tus cartas son: ");
			for (String string : cartas) {
				System.out.print(string + " ");
			}
			// Doble salto de línea para que quede bien
			System.out.println();
			System.out.println();
			if (respuesta != null) {
				String[] partes = respuesta.split(":");
				int rondas = 0;
				if (partes[0].equals("0")) {
					String[] comprobacionTurno = partes[1].split(",");
					rondas = Integer.parseInt(comprobacionTurno[1]);
					if (comprobacionTurno[0].equals("1") && rondas > 1) {
						System.out.println("¡¡HAS GANADO. CACHO MENTIROSO!!");
						estado = false;
					} else {
						rondas = Integer.parseInt(comprobacionTurno[1]);
						// En este split añadimos: nombreJugadorAnterior,tipoJugada,valoresJugada
						String[] datosJugadaAnterior = partes[2].split(",");
						System.out.println("Es tu turno");
						if (!datosJugadaAnterior[0].equals(" "))
							System.out.println("Jugada de " + datosJugadaAnterior[0] + ": " + datosJugadaAnterior[1]
									+ " -> " + datosJugadaAnterior[2]);
						estado = jugar(rondas);
					}
					/*
					 * Aquí habrá que tener en cuenta lo dicho en el servidor.
					 */
				} else {
					System.out.println("Turno de " + partes[1]);
					System.out.println("Esperando turno...");
					try {
						Thread.sleep(30000); // CAMBIAR A 10 SEC --------------------------------------
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				System.out.println("Error: Jugador actual no encontrado");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Metodo que pide los valores y comprueba si existen en la baraja o no
	private static void pedirValores(ArrayList<String> valores, ArrayList<String> listaCartas, String tipo,
			int jugada) {
		String respuesta = "";
		switch (jugada) {
		case 1:
			System.out.println("Introduce el valor de tu carta alta:");
			respuesta = sc.nextLine();
			while (!listaCartas.contains(respuesta)) {
				System.out.println("Cartas inexistentes...");
				System.out.println("Introduce cartas existentes en la baraja:");
				respuesta = sc.nextLine();
			}
			valores.add(respuesta);
			break;
		case 2:
			System.out.println("Introduce el valor de tu pareja:");
			respuesta = sc.nextLine();
			while (!listaCartas.contains(respuesta)) {
				System.out.println("Cartas inexistentes...");
				System.out.println("Introduce cartas existentes en la baraja:");
				respuesta = sc.nextLine();
			}
			for (int i = 0; i < 2; i++)
				valores.add(respuesta);
			break;
		case 3:
			System.out.println("Introduce el valor de tu trío:");
			respuesta = sc.nextLine();
			while (!listaCartas.contains(respuesta)) {
				System.out.println("Cartas inexistentes...");
				System.out.println("Introduce cartas existentes en la baraja:");
				respuesta = sc.nextLine();
			}
			for (int i = 0; i < 3; i++)
				valores.add(respuesta);
			break;
		case 4:
			System.out.println("Introduce el valor de la primera pareja:");
			respuesta = sc.nextLine();
			while (!listaCartas.contains(respuesta)) {
				System.out.println("Cartas inexistentes...");
				System.out.println("Introduce cartas existentes en la baraja:");
				respuesta = sc.nextLine();
			}
			for (int i = 0; i < 2; i++)
				valores.add(respuesta);

			System.out.println("Introduce el valor de la segunda pareja:");
			respuesta = sc.nextLine();
			while (!listaCartas.contains(respuesta)) {
				System.out.println("Cartas inexistentes...");
				System.out.println("Introduce cartas existentes en la baraja:");
				respuesta = sc.nextLine();
			}
			for (int i = 0; i < 2; i++)
				valores.add(respuesta);
			break;
		case 5:
			System.out.println("Introduce el valor de la pareja:");
			respuesta = sc.nextLine();
			while (!listaCartas.contains(respuesta)) {
				System.out.println("Cartas inexistentes...");
				System.out.println("Introduce cartas existentes en la baraja:");
				respuesta = sc.nextLine();
			}
			for (int i = 0; i < 2; i++)
				valores.add(respuesta);

			System.out.println("Introduce el valor de trío:");
			respuesta = sc.nextLine();
			while (!listaCartas.contains(respuesta)) {
				System.out.println("Cartas inexistentes...");
				System.out.println("Introduce cartas existentes en la baraja:");
				respuesta = sc.nextLine();
			}
			for (int i = 0; i < 3; i++)
				valores.add(respuesta);
			break;
		case 6:
			System.out.println("Introduce el valor de la pareja:");
			respuesta = sc.nextLine();
			while (!listaCartas.contains(respuesta)) {
				System.out.println("Cartas inexistentes...");
				System.out.println("Introduce cartas existentes en la baraja:");
				respuesta = sc.nextLine();
			}
			for (int i = 0; i < 4; i++)
				valores.add(respuesta);
			break;

		default:
			System.out.println("Se ha producido un error al comprobar las cartas");
			break;
		}
	}

	/**
	 * Método que almacena el tipo de jugada, los valores de las cartas. En caso de
	 * que decida jugar con sus cartas se mandarán los valores en un String separado
	 * por comas que se gestionará en el servidor y en caso contrario se mandará una
	 * declaración de si es mentiroso o no el anterior, que se gestionará tambien en
	 * el servidor. Pendiente de optimización y de comprobación de inputs
	 * 
	 * @param rondas
	 */
	private static boolean jugar(int rondas) {

		ArrayList<String> valores = new ArrayList<>();
		boolean mentiroso = false;
		System.out.println("");
		System.out.println("Selecciona el tipo de jugada que quieres tirar: " + "\n1. Carta alta " + "\n2. Pareja "
				+ "\n3. Doble pareja " + "\n4. Trío " + "\n5. Full House " + "\n6. Póker");
		// Si no es la primera ronda y es el primer jugador, no se le da la opción de
		// Declarar mentiroso
		// IMPORTANTE ------------------- UTILIZAR ESTA VARIABLE PARA LAS EXCEPCIONES
		boolean comprobacion = id != 1 || rondas > 1;
		if (comprobacion) {
			System.out.println("7. Declarar mentiroso");
		}
		String seleccionJugada = sc.nextLine();
		String tipo = "";
		try {
			int seleccionJugadaNum = Integer.parseInt(seleccionJugada);

			if (seleccionJugadaNum < 1 || seleccionJugadaNum > 7) {
				System.out.println("Introduce una opcion correcta");
			}
			// Añadimos un número de jugada que le indicará al método qué jugada es
			// 1 --> carta alta
			// 2 --> pareja
			// 4 --> doble pareja
			// 3 --> trio
			// 5 --> full
			// 6 --> póker
			int jugada = 0;
			// Cambio hecho para usar el metodo
			switch (seleccionJugadaNum) {
			case 1:
				jugada = 1;
				tipo = "Carta_alta";
				pedirValores(valores, listaCartas, "carta alta", jugada);
				break;
			case 2:
				jugada = 2;
				tipo = "Pareja";
				pedirValores(valores, listaCartas, "pareja", jugada);
				break;
			case 3:
				jugada = 4;
				tipo = "Doble_pareja";
				pedirValores(valores, listaCartas, "primera pareja", jugada);
				break;
			case 4:
				jugada = 3;
				tipo = "Trío";
				pedirValores(valores, listaCartas, "trio", jugada);
				break;
			case 5:
				jugada = 5;
				tipo = "Full_House";
				pedirValores(valores, listaCartas, "pareja", jugada);
				break;
			case 6:
				jugada = 6;
				tipo = "Póker";
				pedirValores(valores, listaCartas, "póker", jugada);
				break;

			case 7:
				tipo = "Declarar_mentiroso";
				System.out.println("Comprobando si es mentiroso...");
				mentiroso = true;
				break;
			}
		} catch (NumberFormatException e) {
			System.out.println("Introduce un numero correcto");
		}
		if (!mentiroso) {
			String valoresComas = "";
			if (valores.size() != 1) {
				for (int i = 0; i < valores.size(); i++) {

					if (i != valores.size() - 1) {
						valoresComas += valores.get(i) + ",";
					} else {
						valoresComas += valores.get(i);
					}
				}
			} else {
				valoresComas = valores.get(0);
			}
			System.out.println(valoresComas); // PRUEBA --------------------------------------
			String url = String.format("http://%s:%s/jugar/%d/%d/%s/%s", IP, PUERTO, ID_PARTIDA, id, tipo,
					valoresComas);
			System.out.println(endPoint(url));
			return true; // Se devuelve true para que siga jugando
		} else {
			String url = String.format("http://%s:%s/mentiroso/%d/%d", IP, PUERTO, ID_PARTIDA, id);
			return comprobarMentiroso(endPoint(url)); // Se devolverá un valor según si ha acertado o no
		}
	}

	private static boolean comprobarMentiroso(String respuesta) {
		boolean comprobacion = true;
		if (respuesta.equals("-1")) {
			System.out.println("Error en la partida");
			System.exit(-1);
		} else if (respuesta.equals("-2")) {
			System.out.println("No ha jugado nadie anteriormente");
		} else if (respuesta.equals("t")) {
			System.out.println("¡La jugada es VERDADERA!");
			System.out.println("Has sido eliminado del juego");
			// Si has fallado, se retorará false para que seas eliminado (salir del bucle de
			// comprobarTurno)
			comprobacion = false;
		} else {
			System.out.println("¡La jugada es MENTIRA!");
			System.out.println("Se ha eliminado al jugador");
		}
		System.out.println(); // salto línea
		return comprobacion;
	}

	private static void repartirCartas(String[] partes) {
		cartas = new ArrayList<String>();

		cartas.add(partes[0]);
		cartas.add(partes[1]);
		cartas.add(partes[2]);
		cartas.add(partes[3]);
		cartas.add(partes[4]);
		cartas.sort(null); // Ordenamos el ArrayList por defecto
		System.out.println(); // salto línea
	}

	/**
	 * Método para unirse a una partida con el nombre del jugador y el id de la
	 * partida. Recibe los nombres y las cartas del jugador
	 */
	private static void unirPartida() {
		System.out.println("Introduce el nombre de jugador: ");
		nombre = sc.nextLine();
		System.out.println("Introduce el id de la partida: ");
		// EXCEPCIÓN DE ENTRADA
		boolean entradaCorrecta = false;
		while (!entradaCorrecta) {
			try {
				ID_PARTIDA = Integer.parseInt(sc.nextLine());
				entradaCorrecta = true;
			} catch (NumberFormatException e) {
				System.out.println("Introduce un número por favor");
			}
		}

		System.out.println(); // Salto línea
		System.out.println("Buscando partida...");
		String url = String.format("http://%s:%s/unir/%s/%s", IP, PUERTO, nombre, ID_PARTIDA);
		String respuesta = endPoint(url);
//		System.out.println(respuesta); //PRUEBA --------------------------------------------------------------
		if (respuesta.equals("-1"))
			System.out.println("Partida no encontrada. Verifique el ID");
		else if (respuesta.equals("-2"))
			System.out.println("Ronda 1 de partida superada. No puedes acceder a la partida");
		else if (respuesta.equals("-3"))
			System.out.println("No puedes unirte a la partida. Partida completa de jugadores");
		else {
			// El servidor devolvera una cadena con los nombres de los jugadores actuales y
			// las cartas del jugador, siendo divididos por un ":"
			String[] partes = respuesta.split(":");
			System.out.println(); // Salto línea
//			System.out.println(Arrays.toString(partes)); // PRUEBAA ---------------------------------------------------------
			String[] nombres = partes[0].split(",");
			System.out.println("Número de jugadores: " + nombres.length);
			String[] cartas = partes[1].split(",");
			id = Integer.parseInt(partes[2]); // Añadimos el ID al jugador
			for (int i = 0; i < nombres.length; i++) {
				System.out.println((i + 1) + ". " + nombres[i]);
			}
			repartirCartas(cartas);
			comprobarTurno();
		}
	}
}