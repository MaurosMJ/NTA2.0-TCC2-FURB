/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence.Configs;

import Enum.Role;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Mauros
 */
public class UsuarioPersistence {

    public List<UsuarioPersistence.SessionValues> session;
    public String workspace;

    public static class SessionValues {

        //Configurações do modulo
        public String imageDir;
        public String usuario;
        public String nomeCompleto;
        public String senha;
        public String email;
        public Role role;
        public String acesso;

    }

}
