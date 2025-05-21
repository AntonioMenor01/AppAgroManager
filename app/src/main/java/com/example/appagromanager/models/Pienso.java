package com.example.appagromanager.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Pienso implements Parcelable {

    private String id;
    private String nombre;
    private String tipo;
    private float cantidadActualKg;
    private String fincaId;

    public Pienso() {}
    public Pienso(String id, String nombre, String tipo, int cantidadActualKg, String fincaId) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.cantidadActualKg = cantidadActualKg;
        this.fincaId = fincaId;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public float getCantidadActualKg() {
        return cantidadActualKg;
    }
    public void setCantidadActualKg(float cantidadActualKg) {
        this.cantidadActualKg = cantidadActualKg;
    }
    public String getFincaId() {
        return fincaId;
    }
    public void setFincaId(String fincaId) {
        this.fincaId = fincaId;
    }

    protected Pienso(Parcel in) {
        id = in.readString();
        nombre = in.readString();
        tipo = in.readString();
        cantidadActualKg = in.readFloat();
        fincaId = in.readString();
    }
    public static final Creator<Pienso> CREATOR = new Creator<Pienso>() {
        @Override
        public Pienso createFromParcel(Parcel in) { return new Pienso(in); }

        @Override
        public Pienso[] newArray(int size) { return new Pienso[size]; }
    };
    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nombre);
        dest.writeString(tipo);
        dest.writeFloat(cantidadActualKg);
        dest.writeString(fincaId);
    }

    @Override
    public String toString() {
        return "Pienso: Nombre=" + nombre + '\'' +" Tipo=" + tipo + "}";
    }
}
