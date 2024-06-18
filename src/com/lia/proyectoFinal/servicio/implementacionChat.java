package com.lia.proyectoFinal.servicio;



import com.lia.proyectoFinal.CloudFileDownloader;
import com.lia.proyectoFinal.DescargadorArchivos;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;

public class implementacionChat extends UnicastRemoteObject implements chatServidor {
    private ArrayList<chatCliente> clientes;
    private ArrayList<Integer> resultadosClientes;
    private CloudFileDownloader fileDownloader;
    private final int LIMITE_SOLICITUDES = 2; // Establece el límite de solicitudes necesarias para combinar y ordenar

    protected implementacionChat() throws RemoteException {
        clientes = new ArrayList<>();
        resultadosClientes = new ArrayList<>();
    }

    @Override
    public void registro(chatCliente cliente) throws RemoteException {
        this.clientes.add(cliente);
    }

    @Override
    public void mensaje(String mensaje) throws RemoteException {
        for (chatCliente cliente : clientes) {
            cliente.mensajeCliente(mensaje);
        }
    }

    @Override
    public synchronized String procesarSolicitud(Integer cantidadArchivos, int opcion, String nombre) throws RemoteException {

        resultadosClientes.add(cantidadArchivos);

        String resultado = "";

        if (resultadosClientes.size() >= LIMITE_SOLICITUDES) {
            int totalDeArchivosParaDescargar = combinarResultadosClientes();
            resultado = descargarArchivos(totalDeArchivosParaDescargar, opcion);

            mensaje(nombre + "su resultado combinado y descargado es: " + resultado);
        }
        return "Solicitud recibida de " + nombre + ". Esperando resulado final... \n" + resultado;
    }

    private String descargarArchivos(int cantidad, int opcion) throws RemoteException {
        mensaje(String.valueOf(opcion));
        DescargadorArchivos descargadorArchivos = new DescargadorArchivos();
        fileDownloader = new CloudFileDownloader();
        String resultado = null;
        switch (opcion) {
            case 1:
                resultado = descargadorArchivos.runSequentialDownload(cantidad);
                break;
            case 2:

                resultado = descargadorArchivos.runForkJoinDownload(cantidad);
                break;
            case 3:

                resultado = fileDownloader.runExecutorServiceDownload(cantidad);
                break;
            default:
                System.out.println( "Opción no válida");
        }
        // Enviar el resultado a todos los clientes
        return resultado;
    }



    @Override
    public int combinarResultadosClientes() throws RemoteException {
        int total = resultadosClientes.stream().mapToInt(Integer::intValue).sum();
        return total;
    }


    public void limpiarArregloDeArreglos(){
        resultadosClientes.clear(); // Limpiar la lista para futuras solicitudes

    }
}
