package edu.illinois.techservices.sympa;

import javax.xml.namespace.QName;
import java.io.*;

import javax.xml.soap.*;
import edu.illinois.techservices.sympa.SympaClient;

public class Subscribe {

    public static void subscribe(String cookie, String listName) {
        try {
            SOAPMessage soapMessage = SympaClient.createMessageFactoryInstance();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

            MimeHeaders headers = soapMessage.getMimeHeaders();
            headers.addHeader("Content-Type", "text/xml");
            headers.addHeader("SOAPAction", "urn:sympasoap#subscribe");
            headers.addHeader("Cookie", "sympa_session=" + cookie);
            SOAPBody soapBody = envelope.getBody();

            SOAPElement soapElement = soapBody.addChildElement("subscribe", "ns", "urn:sympasoap");

            soapElement.addChildElement("list", "ns")
                    .addTextNode(listName)
                    .addAttribute(new QName("xsi:type"), "xsd:string");

            soapMessage.saveChanges();
            SympaClient.printSOAPMessage(soapMessage);

            SOAPMessage subscribe = SympaClient.callSympaAPI(soapMessage);

            System.out.println("\n Subscribe Response : ");
            SympaClient.printSOAPMessage(subscribe);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}