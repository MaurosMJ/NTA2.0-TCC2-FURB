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
public class httpPersistence {

    // Classe exemplo que representa sua configuração
    public static class HttpConfig {

        
        
        public String workspace;
        public SessionValues session;

        public static class SessionValues {
            //Configurações do modulo
            public String endpoint;
            public int operacao;
            public int protocolo;
            public String parametros;
            public String url;
            
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

}}
