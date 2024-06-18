package com.lia.proyectoFinal;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Menu extends JFrame implements ActionListener {
    private CloudFileDownloader fileDownloader;
    private JTextArea txtAreaArchivosDescargados;
    private JTextField txtCantidadArchivos;
    private JLabel lbTiempoSecuencial;
    private JLabel lbTiemForkJoin;
    private JLabel lbTiemExceSer;
    private JButton btnSecuencia;
    private JButton btnForkJoin;
    private JButton btnExceSer;

    private int opcion;
    private int cantidadArchivos;


    private static final String ONEDRIVE_FOLDER_PATH = "C:\\Users\\DELL\\Documents\\DescargadorDeArchivo";
    String[] predefUrls = {
            "https://www.redalyc.org/pdf/6956/695676764003.pdf",
            "https://www.redalyc.org/pdf/6956/695676764004.pdf",
            "https://www.redalyc.org/pdf/6956/695676764005.pdf",
            "https://www.redalyc.org/pdf/6956/695676764008.pdf"
    };
    private JButton btnLimpiar;

    public Menu() {

        setTitle("Descarga de Archivos");
        setSize(600, 700);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        initComponents();
        fileDownloader = new CloudFileDownloader();
    }

    private void initComponents() {
        txtAreaArchivosDescargados = new JTextArea();
        txtAreaArchivosDescargados.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtAreaArchivosDescargados);
        scrollPane.setBounds(20, 220, 560, 400);
        add(scrollPane);

        JLabel lbCantidadArchivos = new JLabel("Cantidad de archivos a descargar:");
        lbCantidadArchivos.setBounds(20, 20, 200, 25);
        add(lbCantidadArchivos);

        txtCantidadArchivos = new JTextField();
        txtCantidadArchivos.setBounds(230, 20, 100, 25);
        add(txtCantidadArchivos);

        lbTiempoSecuencial = new JLabel("Tiempo Secuencial:");
        lbTiempoSecuencial.setBounds(20, 60, 300, 25);
        add(lbTiempoSecuencial);

        lbTiemForkJoin = new JLabel("Tiempo Fork/Join:");
        lbTiemForkJoin.setBounds(20, 100, 300, 25);
        add(lbTiemForkJoin);

        lbTiemExceSer = new JLabel("Tiempo Executor Service:");
        lbTiemExceSer.setBounds(20, 140, 300, 25);
        add(lbTiemExceSer);

        btnSecuencia = new JButton("Secuencial");
        btnSecuencia.setBounds(20, 170, 100, 30);
        btnSecuencia.addActionListener(this);
        add(btnSecuencia);

        btnForkJoin = new JButton("Fork/Join");
        btnForkJoin.setBounds(140, 170, 100, 30);
        btnForkJoin.addActionListener(this);
        add(btnForkJoin);

        btnExceSer = new JButton("Executor Service");
        btnExceSer.setBounds(260, 170, 150, 30);
        btnExceSer.addActionListener(this);
        add(btnExceSer);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(450, 170, 100, 30);
        btnLimpiar.addActionListener(this);
        add(btnLimpiar);

        JButton btnCantidadArchivos = new JButton("Enviar Cantidad");
        btnCantidadArchivos.setBounds(450, 120, 100, 30);
        btnCantidadArchivos.addActionListener(e -> {
            try {
                cantidadArchivos = Integer.parseInt(txtCantidadArchivos.getText());
                txtCantidadArchivos.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, ingresa un número válido para el tamaño del arreglo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(btnCantidadArchivos);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSecuencia) {
            opcion = 1;
        } else if (e.getSource() == btnForkJoin) {
            opcion = 2;
        } else if (e.getSource() == btnExceSer) {
            opcion = 3;
        }else if (e.getSource() == btnLimpiar){
            txtAreaArchivosDescargados.setText("");
            txtCantidadArchivos.setText("");
//            try {
//                implementacionChat implementacionChat = new implementacionChat();
//                implementacionChat.limpiarArregloDeArreglos();
//            } catch (RemoteException ex) {
//                throw new RuntimeException(ex);
//            }
        }
    }

    private void runSequentialDownload() {
        int cantidadArchivos = Integer.parseInt(txtCantidadArchivos.getText());
        long startTime = System.nanoTime();
        List<String> fileUrls = new ArrayList<>();
        List<String> downloadedFiles = new ArrayList<>(); // Lista para almacenar los nombres de archivo descargados



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


        for (String fileUrl : fileUrls) {
            String downloadedFile = downloadAndUploadFile(fileUrl);
            if (downloadedFile != null) {
                downloadedFiles.add(downloadedFile);
            }
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000; // Convertir a milisegundos
        displayDownloadedFiles(downloadedFiles);
        lbTiempoSecuencial.setText("Tiempo Secuencial: " + duration + " ms");
    }

    private void runForkJoinDownload() {
        int cantidadArchivos = Integer.parseInt(txtCantidadArchivos.getText());
        long startTime = System.nanoTime();

        List<String> fileUrls = new ArrayList<>();
        if (cantidadArchivos <= 0) {
            System.out.println("El número de URLs debe ser mayor que cero.");
        } else {
            System.out.println("Obteniendo URLs");

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
        lbTiemForkJoin.setText( "Tiempo ForkJoin: " + duration + " ms");

        // Crear una lista para almacenar los nombres de archivos descargados
        List<String> downloadedFiles = new ArrayList<>();

// Iterar sobre las tareas y obtener los nombres de archivos descargados
        for (DownloadTask task : tasks) {
            downloadedFiles.addAll(task.getDownloadedFiles());
        }

        displayDownloadedFiles(downloadedFiles);
    }



    static String downloadAndUploadFile(String fileUrl) {
        try {
            // Descargar el archivo desde la URL
            URL url = new URL(fileUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                String uniqueFileName = generateUniqueFileName(fileName);

                InputStream inputStream = httpConn.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(ONEDRIVE_FOLDER_PATH + File.separator + uniqueFileName);

                // Leer y escribir el contenido del archivo
                int bytesRead;
                byte[] buffer = new byte[4096];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();

                System.out.println("El archivo " + fileName + " se ha descargado y guardado en OneDrive como " + uniqueFileName);
                return uniqueFileName;
            } else {
                System.out.println("Error en la conexión. Código de respuesta: " + responseCode);
            }
            httpConn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void displayDownloadedFiles(List<String> archivosDescargados) {
        SwingUtilities.invokeLater(() -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (String fileName : archivosDescargados) {
                stringBuilder.append(fileName).append("\n");
            }
            txtAreaArchivosDescargados.append(stringBuilder.toString());
        });
    }


    private static String generateUniqueFileName(String fileName) {
        // Genera un nombre único para el archivo, por ejemplo, agregando un timestamp al nombre original
        long timestamp = System.currentTimeMillis();
        return timestamp + "_" + fileName;
    }

    public int getOpcion(){return this.opcion;}
    public void setOpcion(int opcion){this.opcion = opcion;}
    public int getCantidadArchivos(){return this.cantidadArchivos;}
    public void setTxtAreaArchivosDescargados(String archivos){txtAreaArchivosDescargados.setText(archivos);}
    public void setTiempo(String resultado) {
        String[] partes = resultado.split("\\|");

        // La segunda parte contiene el tiempo
        if (partes.length > 1) {
            String tiempo = partes[1];
            if (getOpcion() == 1) {
                lbTiempoSecuencial.setText(tiempo);
            }else if(getOpcion() == 2){
                lbTiemForkJoin.setText(tiempo);
            }else if (getOpcion() == 3){
                lbTiemExceSer.setText(tiempo);
            }
        }
    }
    public void mostrarArregloOrdenado(String resultado) {
        StringBuilder sb = new StringBuilder();
        String[] valores = resultado.split(" "); // Dividir el resultado por espacios

//        for (int i = 0; i < valores.length; i++) {
//            sb.append(valores[i]); // Agregar el valor actual
//            if (i != valores.length - 1) {
//                sb.append("\n"); // Agregar salto de línea si no es el último valor
//            }
//        }

        txtAreaArchivosDescargados.setText(resultado);
    }

}