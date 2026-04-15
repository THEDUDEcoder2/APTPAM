package com.example.trabajos;

public class SesionManager {
    private static SesionManager instancia;
    private Usuario usuarioActual;

    private SesionManager() {}

    public static SesionManager getInstancia() {
        if (instancia == null) {
            instancia = new SesionManager();
        }
        return instancia;
    }

    public void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean haySesionActiva() {
        return usuarioActual != null;
    }

    public boolean esEmpresa() {
        return usuarioActual != null && usuarioActual.isEsEmpresa();
    }

    public boolean esTrabajador() {
        return usuarioActual != null && !usuarioActual.isEsEmpresa();
    }
}