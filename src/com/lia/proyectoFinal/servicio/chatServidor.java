package com.lia.proyectoFinal.servicio;

import java.rmi.Remote;
import java.rmi.RemoteException;



public interface chatServidor extends Remote {
    void registro(chatCliente cliente) throws RemoteException;
    void mensaje(String mensaje) throws RemoteException;
    String procesarSolicitud(Integer cantidadArchivos, int opcion, String nombre) throws RemoteException;
    public void enviarTiempoCliente(String nombreCliente, String mensaje) throws RemoteException;
}

