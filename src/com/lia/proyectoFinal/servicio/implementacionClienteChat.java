package com.lia.proyectoFinal.servicio;

import com.lia.proyectoFinal.Menu;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class implementacionClienteChat extends UnicastRemoteObject implements chatCliente, Runnable {
    private chatServidor servidor;
    private String nombre;
    private int cantArchivos;
    private int opcion;
    private Menu menu;

    public implementacionClienteChat(String nombre, chatServidor servidor, int cantArchivos, int opcion, Menu menu) throws RemoteException {
        this.nombre = nombre;
        this.servidor = servidor;
        this.opcion = opcion;
        this.cantArchivos = cantArchivos;
        this.menu = menu;
        servidor.registro(this);
    }

    @Override
    public void mensajeCliente(String mensaje) throws RemoteException {
        System.err.println(mensaje);
        menu.setTxtAreaArchivosDescargados(mensaje);
    }

    @Override
    public void tiempo(String tiempo) throws RemoteException{
        menu.setTiempo(tiempo);
    }

    @Override
    public String getNombre() throws RemoteException {
        return this.nombre;
    }

    @Override
    public void run() {
        while (true) {
            try {
                while (!menu.isCantidadArchivosEnviada()) {
                    Thread.sleep(100);
                }

                if (menu.isCantidadArchivosEnviada()) {
                    this.cantArchivos = menu.getCantidadArchivos();
                    menu.setCantidadArchivosEnviada(false);  // Resetea la bandera después de actualizar la cantidad
                }
                this.opcion = menu.getOpcion();

                String resultado = servidor.procesarSolicitud(cantArchivos, opcion, nombre);
                menu.setOpcion(0);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
