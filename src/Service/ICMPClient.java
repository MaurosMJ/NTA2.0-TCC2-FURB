/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Service;

import org.shortpasta.icmp2.IcmpPingUtil;
import org.shortpasta.icmp2.IcmpPingRequest;
import org.shortpasta.icmp2.IcmpPingResponse;

/**
 *
 * @author Mauros
 */
public class ICMPClient {

    public static void main(String[] args) {
        IcmpPingRequest request = IcmpPingUtil.createIcmpPingRequest();
        request.setHost("smtp.gmail.com");

        IcmpPingResponse response = IcmpPingUtil.executePingRequest(request);
        System.out.println("Ping para: " + request.getHost());

        if (response.getSuccessFlag()) {
            System.out.println("Resposta em: " + response.getRtt() + " ms");
            System.out.println("Endereço de IP (Máquina): " + response.getHost());
            System.out.println("Tamanho pacote de resposta recebido: " + response.getSize());
            System.out.println("TTL: " + response.getTtl());
        } else {
            System.out.println("Falha no ping: " + response.getErrorMessage());
        }
    }
}
