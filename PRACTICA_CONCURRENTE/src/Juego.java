import java.util.HashMap;
import java.util.Map;

public class Juego {
    private int[] enemigosTotales;
    private int[] enemigosActuales;
    private int[] enemigosEliminados;
    private int numTiposEnemigos;
    private int maxEnemigosSimultaneos;
    private boolean juegoTerminado;

    public Juego(int numTiposEnemigos, int maxEnemigosSimultaneos, int[] numEnemigosPorTipo) {
        this.numTiposEnemigos = numTiposEnemigos;
        this.maxEnemigosSimultaneos = maxEnemigosSimultaneos;
        this.juegoTerminado = false;

        enemigosTotales = new int[numTiposEnemigos];
        enemigosActuales = new int[numTiposEnemigos];
        enemigosEliminados = new int[numTiposEnemigos];

        for (int i = 0; i < numTiposEnemigos; i++) {
            enemigosTotales[i] = numEnemigosPorTipo[i];
        }
    }

    public synchronized void generarEnemigo(int tipoEnemigo) throws InterruptedException {
        while (enemigosActuales[tipoEnemigo] >= maxEnemigosSimultaneos ||
                (tipoEnemigo > 0 && enemigosEliminados[tipoEnemigo - 1] < enemigosTotales[tipoEnemigo - 1])) {
            wait();
        }

        enemigosActuales[tipoEnemigo]++;
        mostrarEstado();
        notifyAll();
    }

    public synchronized void eliminarEnemigo(int tipoEnemigo) {
        while (enemigosActuales[tipoEnemigo] <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (enemigosEliminados[tipoEnemigo] >= 4) {
            notifyAll();
            return; // No hacer más eliminaciones si ya hay 4 eliminados
        }

        enemigosActuales[tipoEnemigo]--;
        enemigosEliminados[tipoEnemigo]++;
        mostrarEstado();
        if (esJuegoTerminado()) {
            juegoTerminado = true;
            notifyAll();  // Despertar a todos los hilos para que puedan terminar
        }
        notifyAll();
    }

    private void mostrarEstado() {
        System.out.println("--> Enemigos totales: " + totalEnemigosActuales());
        for (int i = numTiposEnemigos - 1; i >= 0; i--) {
            System.out.println("----> Enemigos tipo " + i + ": " + enemigosActuales[i] +
                    " ------ [Eliminados: " + Math.min(enemigosEliminados[i], 4) + "]");
        }
    }

    private int totalEnemigosActuales() {
        int total = 0;
        for (int i = 0; i < numTiposEnemigos; i++) {
            total += enemigosActuales[i];
        }
        return total;
    }

    private boolean esJuegoTerminado() {
        for (int i = 0; i < numTiposEnemigos; i++) {
            if (enemigosEliminados[i] != enemigosTotales[i]) {
                return false;
            }
        }
        return true;
    }

    public void iniciarJuego() {
        Map<Integer, Thread[]> threads = new HashMap<>();

        // Inicializa threads de actividad enemiga y aliada
        for (int i = numTiposEnemigos - 1; i >= 0; i--) {
            Thread[] enemigos = new Thread[enemigosTotales[i]];
            Thread[] aliados = new Thread[enemigosTotales[i]];

            for (int j = 0; j < enemigosTotales[i]; j++) {
                enemigos[j] = new ActividadEnemiga(i, this);
                aliados[j] = new ActividadAliada(i, this);
                enemigos[j].start();
                aliados[j].start();
            }

            threads.put(i, enemigos);
            threads.put(-i, aliados);
        }

        // Espera a que todos los threads terminen
        for (int i = numTiposEnemigos - 1; i >= 0; i--) {
            try {
                for (int j = 0; j < enemigosTotales[i]; j++) {
                    threads.get(i)[j].join();
                    threads.get(-i)[j].join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (isJuegoTerminado()) {
            System.out.println("¡Juego terminado! Todos los enemigos han sido eliminados.");
        }
    }

    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }
}
