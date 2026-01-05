package programa_cliente;

import java.util.ArrayList;
import java.util.Arrays;

public class Test {

	public static void main(String[] args) {
		String[] valores = { "7", "7", "7" };
		String[] mano = { "7", "7", "A", "J", "Q" };
		ArrayList<String> valoresJugada = new ArrayList<String>(Arrays.asList(valores));
		ArrayList<String> manoJugador = new ArrayList<String>(Arrays.asList(mano));
		// Cogemos el size de los valores de la jugada
		int cartasJugada = valoresJugada.size();
		// Añadimos un contador para comparar después
		int contadorCoincidencia = 0;
		// Comprobamos, 1 a 1, si contiene los valores de la jugada en la mano del
		// jugador
		for (String cartaJugada : valoresJugada) {
			for (int i = 0; i < manoJugador.size(); i++) {
				if (cartaJugada.equals(manoJugador.get(i))) {
					manoJugador.set(i, "0");
					contadorCoincidencia++;
					break; // Se hace break para que no siga buscando
				}
			}
		}
		// Comparamos contador de coincidencias con el número de cartas que ha jugado
		// (valores jugada)
		System.out.println(contadorCoincidencia == cartasJugada ? "Son iguales" : "NO son iguales");
	}

}
