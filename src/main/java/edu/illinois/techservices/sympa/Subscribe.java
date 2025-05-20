package edu.illinois.techservices.sympa;

import javax.xml.namespace.QName;
import java.io.*;

import jakarta.xml.soap.*;
import edu.illinois.techservices.sympa.SympaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Subscribe {
    private static final Logger logger = LoggerFactory.getLogger(Subscribe.class);

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
            logger.debug(SympaClient.printSOAPMessage(soapMessage));

            SOAPMessage subscribe = SympaClient.callSympaAPI(soapMessage);

            SympaClient.printFormattedSOAPMessage(subscribe);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}