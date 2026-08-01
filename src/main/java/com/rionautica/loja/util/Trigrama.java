package com.rionautica.loja.util;

import java.util.HashSet;
import java.util.Set;

public class Trigrama {

    public static Set<String> gerar(String texto) {
        Set<String> trigramas = new HashSet<>();
        String t = "  " + texto + " "; // padding ajuda em palavras curtas
        for (int i = 0; i < t.length() - 2; i++) {
            trigramas.add(t.substring(i, i + 3));
        }
        return trigramas;
    }

    // Índice de Jaccard: proporção de trigramas em comum
    public static double similaridade(String a, String b) {
        if (a.isEmpty() || b.isEmpty()) return 0;
        Set<String> ta = gerar(a);
        Set<String> tb = gerar(b);

        Set<String> intersecao = new HashSet<>(ta);
        intersecao.retainAll(tb);

        Set<String> uniao = new HashSet<>(ta);
        uniao.addAll(tb);

        return (double) intersecao.size() / uniao.size();
    }

}
