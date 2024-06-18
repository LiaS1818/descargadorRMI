package com.lia.proyectoFinal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CloudFileDownloader {
    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors(); // Número de hilos disponibles

    private static  String downloadedFile;
    private static  List<String> downloadedFiles;
    private ExecutorService executorService;
    private String tiempo;
    private String listaArchivos;
    private String resultado;
    String[] PREDEF_URLS = {
            "https://www.redalyc.org/pdf/6956/695676764003.pdf",
            "https://www.redalyc.org/pdf/6956/695676764004.pdf",
            "https://www.redalyc.org/pdf/6956/695676764005.pdf",
            "https://www.redalyc.org/pdf/6956/695676764008.pdf"
    };

    public CloudFileDownloader() {
        this.executorService = Executors.newFixedThreadPool(NUM_THREADS);
    }

    public String runExecutorServiceDownload(int cantidadArchivos) {
        if (executorService.isTerminated()){
            executorService = Executors.newFixedThreadPool(NUM_THREADS);
        }
        List<String> fileUrls = new ArrayList<>();
        downloadedFiles = new ArrayList<>(); // Lista para almacenar los nombres de archivo descargados


            System.out.println("Obteniendo URLs");

            // Llenar el arreglo con URLs predefinidas

            int predefIndex = 0;

            for (int i = 1; i <= cantidadArchivos; i++) {
                if (predefIndex < this.PREDEF_URLS.length) {
                    // Agregar URLs predefinidas hasta que se alcance el tamaño deseado
                    fileUrls.add(this.PREDEF_URLS[predefIndex]);
                    predefIndex++;
                } else {
                    // Si se agotan las URLs predefinidas, repetirlas automáticamente
                    fileUrls.add(this.PREDEF_URLS[predefIndex % this.PREDEF_URLS.length]);
                    predefIndex++;
                }
            }

        // Enviar tareas al ExecutorService
        long startTime = System.nanoTime();
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
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000; // Convertir a milisegundos
        downloadedFiles = getDownloadedFile();

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < downloadedFiles.size(); i++) {
            String fileName = downloadedFiles.get(i);
            stringBuilder.append(i + 1).append(". ").append(fileName).append("\n");
        }
        this.listaArchivos = stringBuilder.toString();
        this.tiempo = "Tiempo ForkJoin: " + duration + " ms";

        this.resultado = listaArchivos + "|"+ tiempo;
        return this.resultado;

    }

    public static List<String> getDownloadedFile() {
        return downloadedFiles;
    }

}

