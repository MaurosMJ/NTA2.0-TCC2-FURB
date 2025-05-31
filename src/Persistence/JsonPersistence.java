/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import Enum.Role;
import Persistence.Configs.UsuarioPersistence;
import Persistence.Logs.LogPersistence;
import Persistence.Worker1.Worker1Persistence;
import Utils.HostConfig;
import Utils.Security.Id;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 *
 * @author Mauros
 */
public class JsonPersistence {

    public static void salvarJsonEmAppData(String nomeArquivo, String conteudoJson, String opc) {
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

        File pastaLog = new File(pastaBase, "NTA" + opc);
        if (!pastaLog.exists()) {
            boolean criada = pastaLog.mkdirs();
            if (!criada) {
                System.err.println("Não foi possível criar a pasta NTA.");
                return;
            }
        }

        File arquivoJson = new File(pastaLog, nomeArquivo);
        try (FileWriter writer = new FileWriter(arquivoJson)) {
            writer.write(conteudoJson);
            System.out.println("JSON atualizado com sucesso em: " + arquivoJson.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T carregarJsonAppdata(String nomeArquivo, Class<T> clazz, String opc) {
        String conteudoJson = lerJsonDeAppData(nomeArquivo, opc);

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

    private static String lerJsonDeAppData(String nomeArquivo, String opc) {
        try {
            String appDataPath = System.getenv("APPDATA");
            Path path = Paths.get(appDataPath, "NTA" + opc, nomeArquivo);
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

    public static List<LogPersistence> carregarJsonAppdataLog(String nomeArquivo) {
        String conteudoJson = lerJsonDeAppData(nomeArquivo, "/Log");

        if (conteudoJson == null || conteudoJson.isEmpty()) {
            System.out.println("Arquivo de configuração não encontrado: " + nomeArquivo);
            return null;
        }

        Gson gson = new Gson();
        try {
            Type listType = new TypeToken<List<LogPersistence>>() {
            }.getType();
            return gson.fromJson(conteudoJson, listType);
        } catch (Exception e) {
            System.err.println("Erro ao desserializar JSON para LogPersistence: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static List<UsuarioPersistence> carregarJsonAppdataUsuario(String nomeArquivo) {
        String conteudoJson = lerJsonDeAppData(nomeArquivo, "/Configs");

        if (conteudoJson == null || conteudoJson.isEmpty()) {
            System.out.println("Arquivo de configuração não encontrado: " + nomeArquivo);
            return null;
        }

        Gson gson = new Gson();
        try {
            Type listType = new TypeToken<List<UsuarioPersistence>>() {
            }.getType();
            return gson.fromJson(conteudoJson, listType);
        } catch (Exception e) {
            System.err.println("Erro ao desserializar JSON para LogPersistence: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public static List<Worker1Persistence> carregarJsonAppdataMonitoramento(String nomeArquivo) {
        String conteudoJson = lerJsonDeAppData(nomeArquivo, "/Monitoramento");

        if (conteudoJson == null || conteudoJson.isEmpty()) {
            System.out.println("Arquivo de configuração não encontrado: " + nomeArquivo);
            return null;
        }

        Gson gson = new Gson();
        try {
            Type listType = new TypeToken<List<Worker1Persistence>>() {
            }.getType();
            return gson.fromJson(conteudoJson, listType);
        } catch (Exception e) {
            System.err.println("Erro ao desserializar JSON para LogPersistence: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void adicionarUsuarioAoJson(String nomeArquivo, String workspace, UsuarioPersistence.SessionValues novoUsuario) {
        // Carrega todos os usuários
        List<UsuarioPersistence> todosUsuarios = carregarJsonAppdataUsuario(nomeArquivo);
        if (todosUsuarios == null) {
            todosUsuarios = new ArrayList<>();
        }

        // Procura o workspace existente
        UsuarioPersistence workspaceExistente = null;
        for (UsuarioPersistence up : todosUsuarios) {
            if (up.workspace.equalsIgnoreCase(workspace)) {
                workspaceExistente = up;
                break;
            }
        }

        // Se não existir, cria novo
        if (workspaceExistente == null) {
            workspaceExistente = new UsuarioPersistence();
            workspaceExistente.workspace = workspace;
            workspaceExistente.session = new ArrayList<>();
            todosUsuarios.add(workspaceExistente);
        }

        // Adiciona o novo usuário
        workspaceExistente.session.add(novoUsuario);

        // Salva no disco
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonAtualizado = gson.toJson(todosUsuarios);
        salvarJsonEmAppData(nomeArquivo, jsonAtualizado, "/Configs");
    }

    public static void alterarUsuarioNoJson(
            String nomeArquivo,
            String nomeUsuarioAntigo,
            UsuarioPersistence.SessionValues novoUsuario) {

        // Carrega os dados existentes
        List<UsuarioPersistence> todosUsuarios = carregarJsonAppdataUsuario(nomeArquivo);
        if (todosUsuarios == null) {
            System.out.println("Nenhum dado encontrado no JSON.");
            return;
        }

        boolean usuarioEncontrado = false;

        for (UsuarioPersistence workspace : todosUsuarios) {
            if (workspace.session != null) {
                for (UsuarioPersistence.SessionValues usuarioSession : workspace.session) {
                    if (usuarioSession.usuario.equalsIgnoreCase(nomeUsuarioAntigo)) {
                        usuarioSession.imageDir = novoUsuario.imageDir;
                        usuarioSession.usuario = novoUsuario.usuario;
                        usuarioSession.nomeCompleto = novoUsuario.nomeCompleto;
                        usuarioSession.senha = novoUsuario.senha;
                        usuarioSession.email = novoUsuario.email;
                        usuarioSession.role = novoUsuario.role;
                        usuarioSession.acesso = novoUsuario.acesso;

                        usuarioEncontrado = true;
                        System.out.println("Usuário '" + nomeUsuarioAntigo + "' alterado com sucesso.");
                        break;
                    }
                }
            }
        }

        if (!usuarioEncontrado) {
            System.out.println("Usuário '" + nomeUsuarioAntigo + "' não encontrado.");
            return;
        }

        // Salva o JSON atualizado
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonAtualizado = gson.toJson(todosUsuarios);
        salvarJsonEmAppData(nomeArquivo, jsonAtualizado, "/Configs");
    }

    public static void removerUsuarioDoJson(String nomeArquivo, String nomeUsuario) {
        // Carrega todos os usuários
        List<UsuarioPersistence> todosUsuarios = carregarJsonAppdataUsuario(nomeArquivo);
        if (todosUsuarios == null) {
            System.out.println("Nenhum dado encontrado no JSON.");
            return;
        }

        boolean usuarioRemovido = false;

        // Percorre todos os workspaces
        for (UsuarioPersistence workspace : todosUsuarios) {
            if (workspace.session != null) {
                // Remove se o nome de usuário bater
                Iterator<UsuarioPersistence.SessionValues> iterator = workspace.session.iterator();
                while (iterator.hasNext()) {
                    UsuarioPersistence.SessionValues usuario = iterator.next();
                    if (usuario.usuario.equalsIgnoreCase(nomeUsuario)) {
                        iterator.remove();
                        usuarioRemovido = true;
                        System.out.println("Usuário '" + nomeUsuario + "' removido com sucesso.");
                        break; // Remove apenas o primeiro encontrado. Remova esse break se quiser excluir todos com esse nome.
                    }
                }
            }
        }

        if (!usuarioRemovido) {
            System.out.println("Usuário '" + nomeUsuario + "' não encontrado.");
            return;
        }

        // Remove workspaces vazios (sem sessões)
        todosUsuarios.removeIf(w -> w.session == null || w.session.isEmpty());

        // Atualiza e salva o JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonAtualizado = gson.toJson(todosUsuarios);
        salvarJsonEmAppData(nomeArquivo, jsonAtualizado, "/Configs");
    }
    
    public static void removerMonitoramentoDoJson(String nomeArquivo, String uuid) {
        // Carrega todos os usuários
        List<Worker1Persistence> todosMonitoramentos = carregarJsonAppdataMonitoramento(nomeArquivo);
        if (todosMonitoramentos == null) {
            System.out.println("Nenhum dado encontrado no JSON.");
            return;
        }

        boolean usuarioRemovido = false;

        // Percorre todos os workspaces
        for (Worker1Persistence workspace : todosMonitoramentos) {
            if (workspace.session != null) {
                // Remove se o nome de usuário bater
                Iterator<Worker1Persistence.SessionValues> iterator = workspace.session.iterator();
                while (iterator.hasNext()) {
                    Worker1Persistence.SessionValues monitoramento = iterator.next();
                    if (monitoramento.UUID.equalsIgnoreCase(uuid)) {
                        iterator.remove();
                        usuarioRemovido = true;
                        System.out.println("Monitoramento '" + uuid + "' removido com sucesso.");
                        break; // Remove apenas o primeiro encontrado. Remova esse break se quiser excluir todos com esse nome.
                    }
                }
            }
        }

        if (!usuarioRemovido) {
            System.out.println("Monitoramento '" + uuid + "' não encontrado.");
            return;
        }

        // Remove workspaces vazios (sem sessões)
        todosMonitoramentos.removeIf(w -> w.session == null || w.session.isEmpty());

        // Atualiza e salva o JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonAtualizado = gson.toJson(todosMonitoramentos);
        salvarJsonEmAppData(nomeArquivo, jsonAtualizado, "/Monitoramento");
    }

    public static void adicionarEntradaAoLog(String nomeArquivo, String modulo, LogPersistence.SessionValues novaEntrada) {
        // Carrega todos os logs
        List<LogPersistence> todosLogs = carregarJsonAppdataLog(nomeArquivo);
        if (todosLogs == null) {
            todosLogs = new ArrayList<>();
        }

        // Procura pelo módulo existente
        LogPersistence moduloExistente = null;
        for (LogPersistence lp : todosLogs) {
            if (lp.module.equalsIgnoreCase(modulo)) {
                moduloExistente = lp;
                break;
            }
        }

        // Se não existir, cria um novo
        if (moduloExistente == null) {
            moduloExistente = new LogPersistence();
            moduloExistente.module = modulo;
            moduloExistente.session = new ArrayList<>();
            todosLogs.add(moduloExistente);
        }

        // Adiciona a nova entrada ao módulo correspondente
        moduloExistente.session.add(novaEntrada);

        // Salva no disco
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonAtualizado = gson.toJson(todosLogs);
        salvarJsonEmAppData(nomeArquivo, jsonAtualizado, "/Log");
    }
    
    public static void adicionarMonitoramentoAoJson(String nomeArquivo, String workspace, Worker1Persistence.SessionValues novoMonitoramento) {

        List<Worker1Persistence> todoMonitoramento = carregarJsonAppdataMonitoramento(nomeArquivo);
        if (todoMonitoramento == null) {
            todoMonitoramento = new ArrayList<>();
        }

        Worker1Persistence workspaceExistente = null;
        for (Worker1Persistence up : todoMonitoramento) {
            if (up.workspace.equalsIgnoreCase(workspace)) {
                workspaceExistente = up;
                break;
            }
        }

        if (workspaceExistente == null) {
            workspaceExistente = new Worker1Persistence();
            workspaceExistente.workspace = workspace;
            workspaceExistente.session = new ArrayList<>();
            todoMonitoramento.add(workspaceExistente);
        }

        workspaceExistente.session.add(novoMonitoramento);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonAtualizado = gson.toJson(todoMonitoramento);
        salvarJsonEmAppData(nomeArquivo, jsonAtualizado, "/Monitoramento");
    }

}
