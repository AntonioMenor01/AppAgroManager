package com.example.appagromanager.models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.OffsetDateTime;
import java.util.Date;

public class Finca implements Parcelable {
    private String id;
    @Expose
    private String nombre;
    @Expose
    private String ubicacion;
    @Expose
    private String descripcion;
    @Expose
    private int capacidad;
    @Expose(serialize = true, deserialize = true)
    @SerializedName("created_at")
    private String createdAt;

    @Expose(serialize = false)
    private int animalesActuales;


    public Finca() {}

    public Finca(String nombre, String ubicacion, String descripcion, int capacidad, String createdAt) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
        this.capacidad = capacidad;
        this.createdAt = createdAt;
    }

    protected Finca(Parcel in) {
        id = in.readString();
        nombre = in.readString();
        ubicacion = in.readString();
        descripcion = in.readString();
        capacidad = in.readInt();
        createdAt = in.readString();
    }

    public static final Creator<Finca> CREATOR = new Creator<Finca>() {
        @Override
        public Finca createFromParcel(Parcel in) {
            return new Finca(in);
        }

        @Override
        public Finca[] newArray(int size) {
            return new Finca[size];
        }
    };

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
    public String getUbicacion() {
        return ubicacion;
    }
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public int getCapacidad() {
        return capacidad;
    }
    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getAnimalesActuales() {
        return animalesActuales;
    }

    public void setAnimalesActuales(int animalesActuales) {
        this.animalesActuales = animalesActuales;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nombre);
        dest.writeString(ubicacion);
        dest.writeString(descripcion);
        dest.writeInt(capacidad);
        dest.writeString(createdAt);
    }

    @Override
    public String toString() {
        return nombre ;
    }
}
