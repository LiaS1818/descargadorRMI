package com.lia.proyectoFinal;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.lia.proyectoFinal.Menu.downloadAndUploadFile;

public class DescargadorArchivos {
    String[] PREDEF_URLS = {
            "https://www.redalyc.org/pdf/6956/695676764003.pdf",
            "https://www.redalyc.org/pdf/6956/695676764004.pdf",
            "https://www.redalyc.org/pdf/6956/695676764005.pdf",
            "https://www.redalyc.org/pdf/6956/695676764008.pdf"
    };
    private String tiempo;
    private String listaArchivos;
    private String resultado;

    public String runSequentialDownload(int cantidadArchivos) {
        long startTime = System.nanoTime();
        List<String> fileUrls = new ArrayList<>();
        List<String> downloadedFiles = new ArrayList<>(); // Lista para almacenar los nombres de archivo descargados
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

        for (String fileUrl : fileUrls) {
            String downloadedFile = downloadAndUploadFile(fileUrl);
            if (downloadedFile != null) {
                downloadedFiles.add(downloadedFile);
            }
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000; // Convertir a milisegundos

        // Construir el string con la lista de archivos
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < downloadedFiles.size(); i++) {
            String fileName = downloadedFiles.get(i);
            stringBuilder.append(i + 1).append(". ").append(fileName).append("\n");
        }
        this.listaArchivos = stringBuilder.toString();
        this.tiempo = "Tiempo Secuencial: " + duration + " ms";

        this.resultado = listaArchivos + "|"+ tiempo;
        return this.resultado;
    }

    public String runForkJoinDownload(int cantidadArchivos) {
        long startTime = System.nanoTime();

        List<String> fileUrls = new ArrayList<>();
        if (cantidadArchivos <= 0) {
            System.out.println("El número de URLs debe ser mayor que cero.");
        } else {
            System.out.println("Obteniendo URLs");

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
        }

        // Crear un ForkJoinPool con el número de hilos disponibles
        ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

        // Lanzar las tareas para descargar y cargar archivos
        // Lanzar las tareas para descargar y cargar archivos
        List<DownloadTask> tasks = new ArrayList<>();
        for (String url : fileUrls) {
            tasks.add(new DownloadTask(url));

        }

        forkJoinPool.invokeAll(tasks);
        // Cerrar el scanner y el ForkJoinPool
        forkJoinPool.shutdown();

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000; // Convertir a milisegundos

        // Crear una lista para almacenar los nombres de archivos descargados
        List<String> downloadedFiles = new ArrayList<>();

// Iterar sobre las tareas y obtener los nombres de archivos descargados
        for (DownloadTask task : tasks) {
            downloadedFiles.addAll(task.getDownloadedFiles());
        }

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


}

