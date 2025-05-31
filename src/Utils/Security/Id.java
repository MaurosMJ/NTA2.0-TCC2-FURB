/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils.Security;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

/**
 *
 * @author Mauros
 */
public class Id {

    private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final SecureRandom random = new SecureRandom();

    public static String gerarIdentificadorUnico() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String timestampEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(
                String.valueOf(System.currentTimeMillis()).getBytes());

        StringBuilder especiais = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(SPECIAL_CHARS.length());
            especiais.append(SPECIAL_CHARS.charAt(index));
        }

        return uuid + "_" + timestampEncoded + "_" + especiais.toString();
    }
}
