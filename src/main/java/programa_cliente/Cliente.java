package programa_cliente;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Scanner;

public class Cliente {

	final static String IP = "10.1.192.154"; // CAMBIAR DEPENDIENDO DE LA RED: cmd -> ipconfig -> ipv4
	final static String PUERTO = "8080"; // PUERTO POR DEFECTO: 8080
	static HttpClient cliente = HttpClient.newHttpClient();
	static HttpRequest peticion;
	static HttpResponse<String> respuesta = null;
	static Scanner sc = new Scanner(System.in);
	static String nombre;
	static int id;
	static ArrayList<String> cartas;

	public static void main(String[] args) {
		System.out.println("Comprobando conexión...");
		conexion();
		int opcion = 0;
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
//		String url = String.format("http://%s:%s/prueba", IP, PUERTO);
//		endPoint(url);

	}

	private static void opciones(int opcion) {
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
	 * Método para crear la partida que muestre el id de la partida y las cartas,
	 * recibidas por el servidor
	 */
	private static void crearPartida() {
		System.out.println("Introduce el nombre de jugador: ");
		nombre = sc.nextLine();
		String url = String.format("http://%s:%s/crear/%s", IP, PUERTO, nombre);
		// LLega el return del servidor, que es una cadena
		// con el id y las cartas, separados por comas que dividimos
		// a un array para guardarlo en variables que se utilizarán después
		String respuesta = endPoint(url);
		String[] partes = respuesta.split(",");

		id = Integer.parseInt(partes[0]);
		System.out.println("El ID de la partida es: " + id);
		repartirCartas(partes);
	}

	private static void repartirCartas(String[] partes) {
		cartas.add(partes[1]);
		cartas.add(partes[2]);
		cartas.add(partes[3]);
		cartas.add(partes[4]);
		cartas.add(partes[5]);

		System.out.println("");
		System.out.println("Tus cartas son: ");
		for (String string : cartas) {
			System.out.print(string + " ");

		}
	}

	/**
	 * Método para unirse a una partida con el nombre del jugador y el id de la
	 * partida. Recibe los nombres y las cartas del jugador
	 */
	private static void unirPartida() {
		System.out.println("Introduce el nombre de jugador: ");
		nombre = sc.nextLine();
		System.out.println("Introduce el id de la partida: ");
		id = sc.nextInt();
		String url = String.format("http://%s:%s/unir/%s/%s", IP, PUERTO, nombre, String.valueOf(id));

		String respuesta = endPoint(url);

		if (respuesta.equals("No existe el id")) {
			// También habrá un caso de verificar rondas
			System.out.println("No se ha encontrado ninguna partida con ese id");
		} else {
			// El servidor devolvera una cadena con los nombres de los jugadores actuales y
			// las cartas del jugador, siendo divididos por un ":"
			String[] partes = respuesta.split(":");
			String[] nombres = partes[0].split(",");
			String[] cartas = partes[1].split(",");
			System.out.println("Jugadores actuales: ");
			for (int i = 0; i < nombres.length; i++) {
				System.out.print(nombres[i]);
			}
			repartirCartas(cartas);
		}
	}
}
