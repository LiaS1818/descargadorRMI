package com.lia.proyectoFinal.servicio;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface chatServidor extends Remote {
    void registro(chatCliente cliente) throws RemoteException;
    void mensaje(String mensaje) throws RemoteException;
    String procesarSolicitud(Integer cantidadArchivos, int opcion, String nombre) throws RemoteException;
    //String mostrarArregloOrginal(int[] arreglo, String nombre) throws RemoteException;
    int combinarResultadosClientes() throws RemoteException;
    //ArrayList<int[]> getResultados() throws RemoteException;
}
