/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence.Configs;

import Enum.LogLevel;

/**
 *
 * @author Mauros
 */
public class Worker2Persistence {

    // Classe exemplo que representa sua configuração
    public static class Worker2Config {

        public SessionValues session;
        public String workspace;

        public static class SessionValues {

            //Configurações do modulo
            public String servidor;
            public String porta;
            public String senha;
            public boolean tls;
            public String protocolo;
            public String remetente;
            public String destinatario;
            public String tituloMail;
            public String corpoMail;
            public LogLevel nivel;

        }

    }
}
