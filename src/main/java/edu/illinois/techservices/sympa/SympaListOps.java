package edu.illinois.techservices.sympa;

import javax.xml.namespace.QName;
import jakarta.xml.soap.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collection of Sympa Calls performed on List(s).
 */
public class SympaListOps {
    private static final Logger logger = LoggerFactory.getLogger(SympaListOps.class);

    public static void add(String cookie, String[] args) {
        try {
            SOAPMessage soapMessage = SympaClient.createMessageFactoryInstance();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

            MimeHeaders headers = soapMessage.getMimeHeaders();

            headers.addHeader("Content-Type", "text/xml"); // application/soap+xml
            headers.addHeader("SOAPAction", "urn:sympasoap#add");
            headers.addHeader("Cookie", "sympa_session=" + cookie);
            SOAPBody soapBody = envelope.getBody();

            SOAPElement soapElement = soapBody.addChildElement("add", "ns", "urn:sympasoap");

            soapElement.addChildElement("list", "ns")
                    .addTextNode(args[1])
                    .addAttribute(new QName("xsi:type"), "xsd:string");

            soapElement.addChildElement("email", "ns")
                    .addTextNode(args[2])
                    .addAttribute(new QName("xsi:type"), "xsd:string");

            soapElement.addChildElement("gecos", "ns")
                    .addTextNode(args[3])
                    .addAttribute(new QName("xsi:type"), "xsd:string");

            soapElement.addChildElement("quiet", "ns")
                    .addTextNode(args[4])
                    .addAttribute(new QName("xsi:type"), "xsd:boolean");

            soapMessage.saveChanges();

            // Send the SOAP message to the endpoint
            SOAPMessage add = SympaClient.callSympaAPI(soapMessage);

            SympaClient.printFormattedSOAPMessage(add);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param cookie
     */
    public static void del(String cookie, String[] args) {
        try {
            SOAPMessage soapMessage = SympaClient.createMessageFactoryInstance();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

            MimeHeaders headers = soapMessage.getMimeHeaders();

            headers.addHeader("Content-Type", "text/xml"); // application/soap+xml
            headers.addHeader("SOAPAction", "urn:sympasoap#del");
            headers.addHeader("Cookie", "sympa_session=" + cookie);
            SOAPBody soapBody = envelope.getBody();

            SOAPElement soapElement = soapBody.addChildElement("del", "ns", "urn:sympasoap");
            soapElement.addChildElement("list", "ns")
                    .addTextNode(args[1])
                    .addAttribute(new QName("xsi:type"), "xsd:string");

            soapElement.addChildElement("email", "ns")
                    .addTextNode(args[2])
                    .addAttribute(new QName("xsi:type"), "xsd:string");

            soapElement.addChildElement("quiet", "ns")
                    .addTextNode(args[3])
                    .addAttribute(new QName("xsi:type"), "xsd:boolean");

            soapMessage.saveChanges();

            SOAPMessage del = SympaClient.callSympaAPI(soapMessage);

            SympaClient.printFormattedSOAPMessage(del);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param cookie
     */
    public static void getComplexLists(String cookie) {
        try {
            SOAPMessage soapMessage = SympaClient.createMessageFactoryInstance();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

            MimeHeaders headers = soapMessage.getMimeHeaders();

            headers.addHeader("Content-Type", "text/xml"); // application/soap+xml
            headers.addHeader("SOAPAction", "urn:sympasoap#complexLists");
            headers.addHeader("Cookie", "sympa_session=" + cookie);
            SOAPBody soapBody = envelope.getBody();

            SOAPElement soapElement = soapBody.addChildElement("complexLists", "ns", "urn:sympasoap");
            soapMessage.saveChanges();

            SOAPMessage lists = SympaClient.callSympaAPI(soapMessage);

            SympaClient.printFormattedSOAPMessage(lists);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param cookie
     */
    public static void closeList(String cookie, String[] args) {
        try {
            SOAPMessage soapMessage = SympaClient.createMessageFactoryInstance();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

            MimeHeaders headers = soapMessage.getMimeHeaders();

            headers.addHeader("Content-Type", "text/xml"); // application/soap+xml
            headers.addHeader("SOAPAction", "urn:sympasoap#closeList");
            headers.addHeader("Cookie", "sympa_session=" + cookie);
            SOAPBody soapBody = envelope.getBody();

            SOAPElement soapElement = soapBody.addChildElement("closeList", "ns", "urn:sympasoap");
            soapElement.addChildElement("list", "ns")
                    .addTextNode(args[1])
                    .addAttribute(new QName("xsi:type"), "xsd:string");

            soapMessage.saveChanges();

            SOAPMessage closeList = SympaClient.callSympaAPI(soapMessage);

            SympaClient.printFormattedSOAPMessage(closeList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
