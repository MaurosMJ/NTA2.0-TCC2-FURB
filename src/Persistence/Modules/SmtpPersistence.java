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
public class SmtpPersistence {

    // Classe exemplo que representa sua configuração
    public static class SmtpConfig {

        
        
        public String workspace;
        public SessionValues session;

        public static class SessionValues {
            //Configurações do modulo
            public String servidor;
            public String porta;
            public String password;
            public int protocolo;
            public String remetente;
            public String destinatario;
            public String titulo;
            public String corpoEmail;
            public boolean isStartTls;
            public boolean isRelay;
            public boolean isAutenticacao;
            
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
