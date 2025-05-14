package com.example.appagromanager.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Finca implements Parcelable {
    private String id,nombre,ubicacion;

    public Finca() {}

    public Finca(String id, String nombre, String ubicacion) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
    }

    protected Finca(Parcel in) {
        id = in.readString();
        nombre = in.readString();
        ubicacion = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nombre);
        dest.writeString(ubicacion);
    }
}
