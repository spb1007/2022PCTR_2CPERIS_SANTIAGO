public class SistemaLanzador {

    public static void main(String[] args) {
        int numTiposEnemigos = 4; // Número de tipos de enemigos
        int maxEnemigosSimultaneos = 5; // Máximo número de enemigos simultáneos
        int[] numEnemigosPorTipo = {4, 3, 2, 1}; // Número de enemigos de cada tipo

        Juego juego = new Juego(numTiposEnemigos, maxEnemigosSimultaneos, numEnemigosPorTipo);
        juego.iniciarJuego();
    }
}
