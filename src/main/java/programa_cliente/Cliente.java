package programa_cliente;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Scanner;

public class Cliente {

	final static String IP = "192.168.1.46"; // CAMBIAR DEPENDIENDO DE LA RED: cmd -> ipconfig -> ipv4
	final static String PUERTO = "8080"; // PUERTO POR DEFECTO: 8080
	static HttpClient cliente = HttpClient.newHttpClient();
	static HttpRequest peticion;
	static HttpResponse<String> respuesta = null;
	static Scanner sc = new Scanner(System.in);

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
			
		} else if (opcion == 2) {

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
//			 System.out.println(contenido);
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
		endPoint(url);
	}

}
