package com.example.appagromanager.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Insumo implements Parcelable {
    private String id;
    private String nombre;
    private String tipo;
    private double cantidad;
    private String unidad;
    private String fincaId;

    public Insumo() {}

    public Insumo(String id, String nombre, String tipo, double cantidad, String unidad, String fincaId) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.unidad = unidad;
        this.fincaId = fincaId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public String getFincaId() { return fincaId; }
    public void setFincaId(String fincaId) { this.fincaId = fincaId; }

    protected Insumo(Parcel in) {
        id = in.readString();
        nombre = in.readString();
        tipo = in.readString();
        cantidad = in.readDouble();
        unidad = in.readString();
        fincaId = in.readString();
    }

    public static final Creator<Insumo> CREATOR = new Creator<Insumo>() {
        @Override
        public Insumo createFromParcel(Parcel in) {
            return new Insumo(in);
        }

        @Override
        public Insumo[] newArray(int size) {
            return new Insumo[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nombre);
        dest.writeString(tipo);
        dest.writeDouble(cantidad);
        dest.writeString(unidad);
        dest.writeString(fincaId);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
