import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static final AtomicInteger inventario = new AtomicInteger(100);
    private static final int NUM_OPERACIONES = 100;
    private static final int NUM_PRODUCTORES = 3;
    private static final int NUM_CONSUMIDORES = 5;

    private static final AtomicInteger totalEntregas = new AtomicInteger(0);
    private static final AtomicInteger totalVentas = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        int inventarioInicial = inventario.get();

        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < NUM_PRODUCTORES; i++) {
            executor.submit(() -> {
                for (int j = 0; j < NUM_OPERACIONES; j++) {
                    inventario.incrementAndGet();
                    totalEntregas.incrementAndGet();
                    System.out.println(Thread.currentThread().getName() + " - Entrega. Inventario actual: " + inventario.get());
                }
            });
        }

        for (int i = 0; i < NUM_CONSUMIDORES; i++) {
            executor.submit(() -> {
                for (int j = 0; j < NUM_OPERACIONES; j++) {
                    if (inventario.get() > 0) {
                        inventario.decrementAndGet();
                        totalVentas.incrementAndGet();
                        System.out.println(Thread.currentThread().getName() + " - Venta. Inventario actual: " + inventario.get());
                    } else {
                        // Si no hay stock, se ignora la venta
                        System.out.println(Thread.currentThread().getName() + " - No hay stock para la venta. Inventario actual: " + inventario.get());
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("\n--- Resumen Final ---");
        System.out.println("Inventario inicial: " + inventarioInicial);
        System.out.println("Total de entregas realizadas: " + totalEntregas.get());
        System.out.println("Total de ventas válidas realizadas: " + totalVentas.get());
        int inventarioFinalEsperado = inventarioInicial + totalEntregas.get() - totalVentas.get();
        System.out.println("Inventario final esperado: " + inventarioFinalEsperado);
        System.out.println("Inventario final real: " + inventario.get());
    }
}