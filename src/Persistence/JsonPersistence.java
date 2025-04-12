/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import Persistence.SmtpPersistence.SmtpConfig;
import Persistence.SocketPersistence.SocketConfig;
import Utils.HostConfig;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mauros
 */
public class JsonPersistence {

    public static void salvarJsonEmAppData(String nomeArquivo, String conteudoJson) {
        String os = HostConfig.obterSistemaOperacional();
        String pastaBase;

        switch (os) {
            case "Windows":
                pastaBase = System.getenv("APPDATA"); // C:\Users\USER\AppData\Roaming
                break;
            case "Mac":
                pastaBase = System.getProperty("user.home") + "/Library/Application Support";
                break;
            case "Linux":
                pastaBase = System.getProperty("user.home") + "/.config";
                break;
            default:
                System.err.println("Sistema operacional não suportado.");
                return;
        }

        File pastaNTA = new File(pastaBase, "NTA");
        if (!pastaNTA.exists()) {
            boolean criada = pastaNTA.mkdirs();
            if (!criada) {
                System.err.println("Não foi possível criar a pasta NTA.");
                return;
            }
        }

        File arquivoJson = new File(pastaNTA, nomeArquivo);
        try (FileWriter writer = new FileWriter(arquivoJson)) {
            writer.write(conteudoJson);
            System.out.println("JSON salvo com sucesso em: " + arquivoJson.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T carregarJsonAppdata(String nomeArquivo, Class<T> clazz) {
        String conteudoJson = lerJsonDeAppData(nomeArquivo);

        if (conteudoJson == null || conteudoJson.isEmpty()) {
            System.out.println("Arquivo de configuração não encontrado: " + nomeArquivo);
            return null;
        }

        Gson gson = new Gson();
        try {
            return gson.fromJson(conteudoJson, clazz);
        } catch (Exception e) {
            System.err.println("Erro ao desserializar JSON para " + clazz.getSimpleName() + ": " + e.getMessage());
            return null;
        }
    }

    private static String lerJsonDeAppData(String nomeArquivo) {
        try {
            String appDataPath = System.getenv("APPDATA");
            Path path = Paths.get(appDataPath, "NTA", nomeArquivo);
            if (Files.exists(path)) {
                return new String(Files.readAllBytes(path));
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
