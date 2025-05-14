package com.example.appagromanager.models;

public enum GrupoAnimal {
    VACUNO,
    PORCINO,
    OVINO;

    public static GrupoAnimal fromString(String value) {
        try {
            return GrupoAnimal.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return VACUNO;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case VACUNO: return "Vacuno";
            case PORCINO: return "Porcino";
            case OVINO: return "Ovino";
            default: return super.toString();
        }
    }
}
