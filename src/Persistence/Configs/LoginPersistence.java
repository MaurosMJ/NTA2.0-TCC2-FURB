/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence.Configs;

/**
 *
 * @author Mauros
 */
public class LoginPersistence {

    public SessionValues session;
    public String workspace;

    public static class SessionValues {

        public String usuario;
        public String senha;
        public boolean autoComplete;

    }

}
