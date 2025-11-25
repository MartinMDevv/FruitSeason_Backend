package com.example.FruitseasonBackend.model.entity;

/**
 * FruitType - Tipos de frutas y verduras disponibles
 * 
 * Lista de productos disponibles semanalmente:
 * - Verduras: Alcachofa, Espárrago, Lechuga, Tomate, Zapallo italiano, Brócoli,
 * Zapallo, Coliflor, Repollo
 * - Frutas: Frutilla, Níspero, Durazno, Melón, Sandía, Manzana, Pera, Uvas,
 * Kiwi, Mandarina, Naranja
 */
public enum FruitType {
    // Verduras
    ALCACHOFA("Alcachofa", "VERDURA"),
    ESPARRAGO("Espárrago", "VERDURA"),
    LECHUGA("Lechuga", "VERDURA"),
    TOMATE("Tomate", "VERDURA"),
    ZAPALLO_ITALIANO("Zapallo italiano", "VERDURA"),
    BROCOLI("Brócoli", "VERDURA"),
    ZAPALLO("Zapallo", "VERDURA"),
    COLIFLOR("Coliflor", "VERDURA"),
    REPOLLO("Repollo", "VERDURA"),

    // Frutas
    FRUTILLA("Frutilla", "FRUTA"),
    NISPERO("Níspero", "FRUTA"),
    DURAZNO("Durazno", "FRUTA"),
    MELON("Melón", "FRUTA"),
    SANDIA("Sandía", "FRUTA"),
    MANZANA("Manzana", "FRUTA"),
    PERA("Pera", "FRUTA"),
    UVAS("Uvas", "FRUTA"),
    KIWI("Kiwi", "FRUTA"),
    MANDARINA("Mandarina", "FRUTA"),
    NARANJA("Naranja", "FRUTA");

    private final String displayName;
    private final String category;

    FruitType(String displayName, String category) {
        this.displayName = displayName;
        this.category = category;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCategory() {
        return category;
    }
}
