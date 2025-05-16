/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Service;

import Entities.LogOccurrenceModule;
import Enum.LogLevel;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.ArrayList;
import org.shortpasta.icmp2.IcmpPingRequest;
import org.shortpasta.icmp2.IcmpPingResponse;
import org.shortpasta.icmp2.IcmpPingUtil;

/**
 *
 * @author 320167484
 */
public class IcmpClient {

    private final StringWriter sw = new StringWriter();
    private final PrintWriter pw = new PrintWriter(sw);
    private final ArrayList<LogOccurrenceModule> LogArray = new ArrayList<>();
    private final String host;
    private final int qtd;

    public IcmpClient(String host, String qtd) {
        this.host = host;
        this.qtd = Integer.parseInt(qtd);
    }

    public ArrayList<LogOccurrenceModule> PerformServerConnection() {
        String output = "";
        IcmpPingRequest request = IcmpPingUtil.createIcmpPingRequest();
        request.setHost(host);

        IcmpPingResponse response = IcmpPingUtil.executePingRequest(request);
        output += "\nPing para: " + request.getHost() + "\n";

        if (response.getSuccessFlag()) {
            output += "Resposta em: " + response.getRtt() + " ms\n";
            output += verifyRtt(response.getRtt()) + "\n";
            output += "Endereço de IP (Máquina): " + response.getHost() + "\n";
            output += "Tamanho pacote de resposta recebido: " + response.getSize() + "\n";
            output += "TTL: " + response.getTtl() + "\n";
        } else {
            System.out.println("Falha no ping: " + response.getErrorMessage());
        }
        addToArray(output, LogLevel.FINE);
        return getLogArray();
    }

    private String verifyRtt(int input) {
        if (input <= 30) {
            return "Latência Excelente. Comum em redes locais (LAN), conexão de alta qualidade.";
        } else if (input <= 100) {
            return "Latência moderada. Pode gerar pequenos atrasos perceptíveis.";
        } else if (input <= 200) {
            return "Latência Alta. Atrasos são frequentemente perceptíveis.";
        } else if (input <= 500) {
            return "Latência Muito Alta. Lentidão e travamentos perceptíveis devido a rede.";
        } else {
            return "Latência Extrema. Quase inutilizável.";
        }
    }

    private void addToArray(String input, LogLevel level) {

        LogOccurrenceModule log = new LogOccurrenceModule(input, level);
        this.LogArray.add(log);
    }

    public ArrayList<LogOccurrenceModule> getLogArray() {
        return LogArray;
    }

}
