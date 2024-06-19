package com.lia.proyectoFinal.servicio;

import com.lia.proyectoFinal.CloudFileDownloader;
import com.lia.proyectoFinal.DescargadorArchivos;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class implementacionChat extends UnicastRemoteObject implements chatServidor {
    private List<chatCliente> clientes;
    private int totalArchivos; // Variable para almacenar la cantidad total de archivos
    private int contSoli;
    private List<SolicitudCliente> solicitudes; // Lista para almacenar las solicitudes de los clientes
    private CloudFileDownloader fileDownloader;
    private final int LIMITE_SOLICITUDES = 2; // Establece el límite de solicitudes necesarias para combinar y ordenar

    protected implementacionChat() throws RemoteException {
        clientes = new ArrayList<>();
        solicitudes = new ArrayList<>();
    }

    @Override
    public synchronized void registro(chatCliente cliente) throws RemoteException {
        clientes.add(cliente);
        cliente.mensajeCliente("Conexión establecida con el servidor.");
    }

    @Override
    public synchronized void mensaje(String mensaje) throws RemoteException {
        for (chatCliente cliente : clientes) {
            cliente.mensajeCliente(mensaje);
        }
    }

    @Override
    public synchronized String procesarSolicitud(Integer cantidadArchivos, int opcion, String nombre) throws RemoteException {
        if (cantidadArchivos >= 0) {
            totalArchivos += cantidadArchivos; // Sumar la cantidad a la variable total
            solicitudes.add(new SolicitudCliente(nombre, cantidadArchivos, opcion)); // Agregar la solicitud a la lista
            contSoli++;
        }

        String resultado = null;

        if (solicitudes.size() >= LIMITE_SOLICITUDES) {
            for (SolicitudCliente solicitud : solicitudes) {
                resultado = descargarArchivos(totalArchivos, solicitud.getOpcion());
                enviarMensajeCliente(solicitud.getNombre(), "Resultado de descarga con opción " + solicitud.getOpcion() + ": " + resultado);
                enviarTiempoCliente(solicitud.getNombre(), resultado);
            }

            solicitudes.clear();
        }

        return resultado; // Devolver el último resultado procesado (puede ser null si no se procesaron solicitudes)
    }

    private String descargarArchivos(int cantidad, int opcion) throws RemoteException {
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
                resultado = "Opción no válida";
                break;
        }

        return resultado;
    }

    private void enviarMensajeCliente(String nombreCliente, String mensaje) throws RemoteException {
        for (chatCliente cliente : clientes) {
            if (cliente.getNombre().equals(nombreCliente)) {
                cliente.mensajeCliente(mensaje);
            }
        }
    }

    public void enviarTiempoCliente(String nombreCliente, String mensaje) throws RemoteException {
        for (chatCliente cliente : clientes) {
            if (cliente.getNombre().equals(nombreCliente)) {
                cliente.tiempo(mensaje);
            }
        }
    }

}

class SolicitudCliente {
    private String nombre;
    private int cantidadArchivos;
    private int opcion;

    public SolicitudCliente(String nombre, int cantidadArchivos, int opcion) {
        this.nombre = nombre;
        this.cantidadArchivos = cantidadArchivos;
        this.opcion = opcion;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidadArchivos() {
        return cantidadArchivos;
    }

    public int getOpcion() {
        return opcion;
    }
}
