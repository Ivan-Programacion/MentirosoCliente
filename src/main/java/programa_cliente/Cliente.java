package programa_cliente;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class Cliente {

	public static void main(String[] args) {
		String url = "192.168.1.46:8080/prueba";
		String contenido = "";
		HttpClient cliente = HttpClient.newHttpClient();
		HttpRequest peticion;
		HttpResponse<String> respuesta = null;
		try {
			peticion = HttpRequest.newBuilder().uri(URI.create(url)).build();
			respuesta = cliente.send(peticion, BodyHandlers.ofString());
			contenido = respuesta.body();
			System.out.println(contenido);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
