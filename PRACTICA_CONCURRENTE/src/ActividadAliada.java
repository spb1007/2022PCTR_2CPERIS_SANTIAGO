public class ActividadAliada extends Thread {
    private int tipoEnemigo;
    private Juego juego;

    public ActividadAliada(int tipoEnemigo, Juego juego) {
        this.tipoEnemigo = tipoEnemigo;
        this.juego = juego;
    }

    @Override
    public void run() {
        while (true) {
            juego.eliminarEnemigo(tipoEnemigo);
        }
    }
}
