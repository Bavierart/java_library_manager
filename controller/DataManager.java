package controller;

import java.io.*;
import java.util.ArrayList;

public final class DataManager {
    private DataManager() {}

    public static void save(Object data, String filename) {
        try {
            File file = new File(filename);
            File parent = file.getParentFile();
            if (parent != null) parent.mkdirs();
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(data);
            }
        } catch (IOException e) {
            System.err.println("[ERRO] Falha ao salvar dados em " + filename + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T load(String filename) {
        File file = new File(filename);
        try {
            // 🔹 Se o arquivo não existir, cria um novo e retorna lista vazia
            if (!file.exists()) {
                file.createNewFile();

                // Tentamos retornar o tipo correto: se for lista, devolve vazia
                if (filename.endsWith(".dat")) {
                    return (T) new ArrayList<>();
                }
                return null;
            }

            // 🔹 Se existir, tenta ler
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                return (T) in.readObject();
            }
        } catch (EOFException e) {
            // Arquivo existe, mas está vazio
            return (T) new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[ERRO] Falha ao carregar " + filename + ": " + e.getMessage());
            return (T) new ArrayList<>();
        }
    }
}
