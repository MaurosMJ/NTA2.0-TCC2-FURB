/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import org.xbill.DNS.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mauros
 */
public class NewMain {

    public static void main(String[] args) {
        String domain = "google.com";

        // Cria uma instância de SimpleResolver com um servidor DNS explícito
        Resolver resolver = null;
        try {
            resolver = new SimpleResolver("8.8.8.8"); // Google DNS
        } catch (UnknownHostException ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        // Cria a consulta DNS
        Name name = null;
        try {
            name = Name.fromString(domain, Name.root);
        } catch (TextParseException ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        Record record = Record.newRecord(name, Type.A, DClass.IN);
        Message query = Message.newQuery(record);

        // Executa a consulta com tempo medido
        Message response = null;
        long startTime = System.nanoTime(); // Início da medição

        try {
            response = resolver.send(query);
        } catch (IOException ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        long endTime = System.nanoTime(); // Fim da medição
        double elapsedMillis = (endTime - startTime) / 1_000_000.0;

        // Mostra as respostas
        Record[] answers = response.getSectionArray(Section.ANSWER);
        if (answers.length == 0) {
            System.out.println("Nenhuma resposta recebida.");
        } else {
            for (Record ans : answers) {
                System.out.println("Resposta: " + ans.rdataToString());
            }
            System.out.printf("Tempo de resolução DNS: %.3f ms%n", elapsedMillis);
        }
    }
}
