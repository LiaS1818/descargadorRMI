package com.lia.proyectoFinal.servicio;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface chatCliente extends Remote {
    void mensajeCliente(String mensaje) throws RemoteException;


     String getNombre() throws RemoteException;
     void tiempo(String tiempo) throws RemoteException;
}
