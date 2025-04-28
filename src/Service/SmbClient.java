/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Service;

import Entities.LogOccurrence;
import Enum.LogLevel;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

/**
 *
 * @author 320167484
 */
public class SmbClient {

    private final StringWriter sw = new StringWriter();
    private final PrintWriter pw = new PrintWriter(sw);
    private final ArrayList<LogOccurrence> LogArray = new ArrayList<>();
    private String usr;
    private String dmn;
    private String shost;
    private String spwd;
    private String fileName;
    private String fileContent;
    private NtlmPasswordAuthentication auth = null;

    public SmbClient(String usr, String dmn, String shost, String spwd, String fileName, String fileContent) {

        if (!shost.contains("\\\\")) {
            shost = "\\\\" + shost;
        }

        this.usr = usr;
        this.dmn = dmn;
        this.shost = shost;
        this.spwd = spwd;
        if (fileName.length() > 0) {
            this.fileName = fileName;
        } else {
            this.fileName = "Nta";
        }
        if (fileContent.length() > 0) {
            this.fileContent = fileContent;
        } else {
            this.fileContent = "Arquivo encaminhado ao servidor!";
        }
    }

    public ArrayList<LogOccurrence> smbAuth() {
        System.out.println("Iniciando autenticação com o host destino.");

        shost = shost.replace("\\", "/");
        //       shost = "smb:" + shost + "/";
        NtlmPasswordAuthentication auth = null;
        try {
            auth = new NtlmPasswordAuthentication(dmn, usr, spwd);
            this.auth = auth;
            addToArray("Autenticado no servidor.", LogLevel.INFO);
        } catch (Exception e) {
            addToArray("Ocorreu um erro ao autenticar.", LogLevel.ERROR);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            System.out.println(sw.toString());
            System.out.println(pw.toString());
        }

        /*
         shost = shost.replace("\\", "/");
         shost = "smb:" + shost;
         String timestamp = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ssss").format(new Date());
         String fileName = "/smbRW-" + timestamp.replaceAll("[: ]", "") + ".txt";
         shost += fileName + "/";
         System.out.println(shost);

         try {
         NtlmPasswordAuthentication authentication = new NtlmPasswordAuthentication(dmn, usr, spwd);
         writeToFile(shost, authentication);
         } catch (Exception e) {
         e.printStackTrace();
         }
         */
        return this.getLogArray();
    }

    public ArrayList<LogOccurrence> writeToFile() {
        this.smbAuth();

        String smbPath = shost.replace("\\", "/");
        smbPath = "smb:" + smbPath;

        String timestamp = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ssss").format(new Date());
        String fileName = "/" + this.fileName + "-" + timestamp.replaceAll("[: ]", "") + ".txt";
        smbPath += fileName;

        System.out.println("Caminho SMB final: " + smbPath);

        SmbFile remoteFile;
        try {
            remoteFile = new SmbFile(smbPath, this.auth);
        } catch (MalformedURLException e) {
            logException("URL do arquivo SMB é inválida.", e);
            return this.getLogArray();
        }

        byte[] content = this.fileContent.getBytes();

        try (SmbFileOutputStream outputStream = new SmbFileOutputStream(remoteFile)) {
            outputStream.write(content);
            addToArray("[WRITE = OK] Arquivo enviado com sucesso ao servidor!", LogLevel.INFO);
        } catch (IOException e) {
            logException("Erro ao escrever ou fechar o arquivo SMB.", e);
        }

        return this.getLogArray();
    }

    private void logException(String message, Exception e) {
        Logger.getLogger(SmbClient.class.getName()).log(Level.SEVERE, message, e);
        this.addToArray(message, LogLevel.ERROR);
        this.addToArray(getStackTraceAsString(e), LogLevel.DEBUG);
    }

    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public void readFromFile(String fileName) {
        SmbFile remoteFile = null;

        this.smbAuth();

        String smbPath = shost.replace("\\", "/");
        smbPath = "smb:" + smbPath;

        String timestamp = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ssss").format(new Date());
//        String fileName = "/" + this.fileName + "-" + timestamp.replaceAll("[: ]", "") + ".txt";
        smbPath += fileName;

        System.out.println("Caminho SMB final: " + smbPath);

        try {
            remoteFile = new SmbFile(smbPath, auth);
        } catch (MalformedURLException ex) {
            Logger.getLogger(SmbClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        SmbFileInputStream inputStream = null;
        try {
            inputStream = new SmbFileInputStream(remoteFile);
        } catch (SmbException ex) {
            Logger.getLogger(SmbClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(SmbClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(SmbClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] buffer = new byte[1024];
        int bytesRead;
        StringBuilder fileContent = new StringBuilder();
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileContent.append(new String(buffer, 0, bytesRead));
            }
        } catch (IOException ex) {
            Logger.getLogger(SmbClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(SmbClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        addToArray("[READ = OK] File content read from the server: \n", LogLevel.INFO);
    }

    /**
     * Initiates authentication with the target host and lists folders and files
     * in the directory.
     *
     * @param user The username for authentication.
     * @param passW The password for authentication.
     * @param host The host address for the SMB server.
     */
    public ArrayList<LogOccurrence> smbListDir() {
        System.out.println("Initiating authentication with the target host.");

        shost = shost.replace("\\", "/");
        shost = "smb:" + shost + "/";

        try {
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", usr, spwd);
            listFilesInDirectory(shost, auth);
        } catch (Exception e) {
            handleAuthenticationError(e);
            this.addToArray(sw.toString(), LogLevel.DEBUG);
        }
        return getLogArray();
    }

    private void listFilesInDirectory(String host, NtlmPasswordAuthentication authentication) {
        try {
            SmbFile directory = new SmbFile(host, authentication);

            if (directory.exists() && directory.isDirectory()) {
                addToArray("INITIATING SEARCH:", LogLevel.INFO);
                addToArray("Folders and files in the directory:", LogLevel.INFO);

                SmbFile[] files = directory.listFiles();
                for (SmbFile file : files) {
                    addToArray(" File Found: " + file.getName(), LogLevel.INFO);
                }
            }
        } catch (Exception e) {
            // Log the exception and continue execution
            addToArray(" Error while listing files in directory:  " + e.getMessage(), LogLevel.ERROR);

            e.printStackTrace(pw);
            this.addToArray(sw.toString(), LogLevel.DEBUG);
        }
    }

    private void handleAuthenticationError(Exception e) {
        // Log the authentication error and continue execution
        addToArray(" Error while listing files in directory:  " + e.getMessage(), LogLevel.ERROR);
        addToArray("Authentication error: " + e.getMessage(), LogLevel.ERROR);
        e.printStackTrace(pw);
        this.addToArray(sw.toString(), LogLevel.DEBUG);
    }

    private void addToArray(String input, LogLevel level) {

        LogOccurrence log = new LogOccurrence(input, level);
        this.LogArray.add(log);
    }

    public ArrayList<LogOccurrence> getLogArray() {
        return LogArray;
    }

}
