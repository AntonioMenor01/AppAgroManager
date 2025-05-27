package com.example.appagromanager.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDate;

public class Animal implements Parcelable {
    private String id;
    private String crotal;
    private double peso;
    private String grupo;
    private String fincaId;
    private String fechaNacimiento;
    private String fechaInsercion;
    private String piensoid;

    public Animal() {}

    public Animal(String id, String crotal, double peso, String grupo, String fincaId, String fechaNacimiento, String fechaInsercion,String piensoid) {
        this.id = id;
        this.crotal = crotal;
        this.peso = peso;
        this.grupo = grupo;
        this.fincaId = fincaId;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaInsercion = fechaInsercion;
        this.piensoid = piensoid;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCrotal() { return crotal; }
    public void setCrotal(String crotal) { this.crotal = crotal; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }

    public String getFincaId() { return fincaId; }
    public void setFincaId(String fincaId) { this.fincaId = fincaId; }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getFechaInsercion() { return fechaInsercion; }
    public void setFechaInsercion(String fechaInsercion) { this.fechaInsercion = fechaInsercion; }

    public String getPiensoId() {return piensoid; }
    public void setPiensoId(String piensoid) {this.piensoid = piensoid; }

    protected Animal(Parcel in) {
        crotal = in.readString();
        fechaNacimiento = in.readString();
        peso = in.readDouble();
        grupo = in.readString();
        fechaInsercion = in.readString();
        fincaId = in.readString();
        piensoid = in.readString();
    }

    public static final Creator<Animal> CREATOR = new Creator<Animal>() {
        @Override
        public Animal createFromParcel(Parcel in) {
            return new Animal(in);
        }

        @Override
        public Animal[] newArray(int size) {
            return new Animal[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(crotal);
        dest.writeString(fechaNacimiento);
        dest.writeDouble(peso);
        dest.writeString(grupo);
        dest.writeString(fechaInsercion);
        dest.writeString(fincaId);
        dest.writeString(piensoid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return this.getCrotal();
    }

}

