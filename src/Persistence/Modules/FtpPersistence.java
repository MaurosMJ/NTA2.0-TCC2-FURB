/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence.Modules;

/**
 *
 * @author Mauros
 */
public class FtpPersistence {

    // Classe exemplo que representa sua configuração
    public static class FtpConfig {

        
        
        public String workspace;
        public SessionValues session;

        public static class SessionValues {
            //Configurações do modulo
            public String servidor;
            public String porta;
            public int operacao;
            public String password;
            public int protocolo;
            public String usuario;
            public String diretorioLocal;
            public String diretorioRemoto;
            
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
