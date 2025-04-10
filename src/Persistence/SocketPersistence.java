/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author Mauros
 */
public class SocketPersistence {

    // Classe exemplo que representa sua configuração
    public static class Config {

        
        
        public String workspace;
        public SessionValues session;

        public static class SessionValues {
            //Configurações do modulo
            public String servidor;
            public String porta;
            
            //Configurações padrões
            public boolean executar;
            public boolean monitorar;
            public boolean lixeira;
            public boolean home;
            public boolean considerarData;
            public int level;
            public String dataInicial; 
            public String dataFinal;
            public boolean filtrar;
            public boolean toggleEditor;
            

        }

    // Gera o conteúdo JSON de exemplo
    public static String gerarConteudoJson() {
        Config config = new Config();
//        config.usuario = "thalles";
//        config.tema = "dark";
//        config.tamanhoFonte = 14;

//        config.autenticacao = new Configuracoes.Autenticacao();
//        config.autenticacao.token = "abc123xyz";
//        config.autenticacao.expiraEm = "2025-12-31T23:59:59";

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(config);
    }
}}
