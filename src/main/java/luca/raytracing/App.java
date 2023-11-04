package luca.raytracing;

public class App {
    public static void main(String[] args) {
        Controller controller = new Controller();
        try {
            final long startTime = System.currentTimeMillis();
            System.out.println("Path Tracing");
            controller.startTracing();
            final long endTime = System.currentTimeMillis();
            System.out.printf("Execution Time: %ds\n", (endTime - startTime) / 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}