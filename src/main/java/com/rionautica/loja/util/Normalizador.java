package com.rionautica.loja.util;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Normalizador {
    private static final Pattern ACENTOS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final Pattern NAO_ALFANUMERICO = Pattern.compile("[^a-z0-9\\s]");

    public static String normalizar(String texto) {
        if (texto == null) return "";
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD);
        semAcento = ACENTOS.matcher(semAcento).replaceAll("");
        String minusculo = semAcento.toLowerCase().trim();
        return NAO_ALFANUMERICO.matcher(minusculo).replaceAll(" ").replaceAll("\\s+", " ").trim();
    }

    public static List<String> tokenizar(String texto) {
        String normalizado = normalizar(texto);
        if (normalizado.isEmpty()) return List.of();
        return Arrays.asList(normalizado.split(" "));
    }
}
