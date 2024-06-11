import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CloudFileDownloader {
    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors(); // Número de hilos disponibles

    private static  String downloadedFile;
    private static  List<String> downloadedFiles;
    private ExecutorService executorService;

    public CloudFileDownloader() {
        this.executorService = Executors.newFixedThreadPool(NUM_THREADS);
    }

    public void runExecutorServiceDownload(int cantidadArchivos, String[] predefUrls) {
        if (executorService.isTerminated()){
            executorService = Executors.newFixedThreadPool(NUM_THREADS);
        }
        List<String> fileUrls = new ArrayList<>();
         downloadedFiles = new ArrayList<>(); // Lista para almacenar los nombres de archivo descargados

        if (cantidadArchivos <= 0) {
            System.out.println("El número de URLs debe ser mayor que cero.");
            return;
        } else {
            System.out.println("Obteniendo URLs");

            // Llenar el arreglo con URLs predefinidas

            int predefIndex = 0;

            for (int i = 1; i <= cantidadArchivos; i++) {
                if (predefIndex < predefUrls.length) {
                    // Agregar URLs predefinidas hasta que se alcance el tamaño deseado
                    fileUrls.add(predefUrls[predefIndex]);
                    predefIndex++;
                } else {
                    // Si se agotan las URLs predefinidas, repetirlas automáticamente
                    fileUrls.add(predefUrls[predefIndex % predefUrls.length]);
                    predefIndex++;
                }
            }
        }

        // Enviar tareas al ExecutorService
        List<Future<String>> futures = new ArrayList<>();
        for (String fileUrl : fileUrls) {
            Future<String> future = executorService.submit(() -> Menu.downloadAndUploadFile(fileUrl));
            futures.add(future);
        }

        // Obtener resultados y manejar excepciones
        for (Future<String> future : futures) {
            try {
                String downloadedFile = future.get();
                if (downloadedFile != null) {
                    downloadedFiles.add(downloadedFile);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Apagar el ExecutorService
        executorService.shutdown();

        // Esperar a que todas las tareas se completen o transcurra un tiempo límite
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static List<String> getDownloadedFile() {
        return downloadedFiles;
    }

}
