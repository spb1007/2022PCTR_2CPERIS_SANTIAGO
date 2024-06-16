import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ActividadEnemiga extends Thread {
    private int tipoEnemigo;
    private Juego juego;

    public ActividadEnemiga(int tipoEnemigo, Juego juego) {
        this.tipoEnemigo = tipoEnemigo;
        this.juego = juego;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Generar un enemigo de tipo tipoEnemigo
                TimeUnit.MILLISECONDS.sleep(new Random().nextInt(5000) + 1000); // Entre 1 y 5 segundos
                juego.generarEnemigo(tipoEnemigo);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
