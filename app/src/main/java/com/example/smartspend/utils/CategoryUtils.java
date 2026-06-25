// app/src/main/java/com/example/smartspend/utils/CategoryUtils.java
package com.example.smartspend.utils;

public class CategoryUtils {

    // Retourne l'emoji correspondant à la catégorie
    public static String getIcon(String category) {
        if (category == null) return "💰";
        switch (category) {
            case "Transport":  return "🚗";
            case "Nourriture": return "🍽️";
            case "Loyer":      return "🏠";
            case "Loisirs":    return "🎮";
            case "Santé":      return "💊";
            case "Salaire":    return "💼";
            case "Freelance":  return "💻";
            default:           return "💰";
        }
    }
}