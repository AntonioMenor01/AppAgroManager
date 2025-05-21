package com.example.appagromanager.models;

import com.google.gson.annotations.SerializedName;

public class ConfiguracionConsumo {

    private String grupoAnimal;

    @SerializedName("consumoKgPorAnimalPorDia")
    private double consumoKgPorAnimalPorDia;

    public String getGrupoAnimal() {
        return grupoAnimal;
    }

    public double getConsumoKgPorAnimalPorDia() {
        return consumoKgPorAnimalPorDia;
    }
}
