package edu.illinois.techservices.sympa;

import javax.xml.namespace.QName;
import java.io.*;
import jakarta.xml.soap.*;
import edu.illinois.techservices.sympa.SympaClient;

public class SympaListOps {

    private static String sympaSoapUrl = "https://lists-dev.techservices.illinois.edu/sympasoap";

    public static void add(String cookie) {
        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

            MimeHeaders headers = soapMessage.getMimeHeaders();

            //headers.addHeader("Authorization", encodedAuth);
            headers.addHeader("Content-Type", "text/xml"); //application/soap+xml
            headers.addHeader("SOAPAction", "urn:sympasoap#add"); 
            headers.addHeader("Cookie", "sympa_session="+cookie);
            SOAPBody soapBody = envelope.getBody();
            
            SOAPElement soapElement = soapBody.addChildElement("add", "ns", "urn:sympasoap");

            soapElement.addChildElement("list", "ns")
                .addTextNode("pbalesamplelist")
                .addAttribute(new QName("xsi:type"), "xsd:string");

            soapElement.addChildElement("email", "ns")
                .addTextNode("rstanton@test.com")
                .addAttribute(new QName("xsi:type"), "xsd:string");

            soapElement.addChildElement("gecos", "ns")
                .addTextNode("Test user")
                .addAttribute(new QName("xsi:type"), "xsd:string");

            soapElement.addChildElement("quiet", "ns")
                .addTextNode("0")
                .addAttribute(new QName("xsi:type"), "xsd:boolean");

            soapMessage.saveChanges();

            // Send the SOAP message to the endpoint
            SOAPMessage add = SympaClient.callSympaAPI(soapMessage);
            
            System.out.println("\n add Response : ");
            SympaClient.printSOAPMessage(add);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 
     * @param cookie
     */
    public static void del(String cookie) {
        try {

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        //String myNamespaceURI = "https://lists-dev.techservices.illinois.edu/lists/wsdl";
        SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

        MimeHeaders headers = soapMessage.getMimeHeaders();

        //headers.addHeader("Authorization", encodedAuth);
        headers.addHeader("Content-Type", "text/xml"); //application/soap+xml
        headers.addHeader("SOAPAction", "urn:sympasoap#del"); 
        headers.addHeader("Cookie", "sympa_session="+cookie);
        SOAPBody soapBody = envelope.getBody();

        SOAPElement soapElement = soapBody.addChildElement("del", "ns", "urn:sympasoap");
        soapElement.addChildElement("list", "ns")
            .addTextNode("pbalesamplelist")
            .addAttribute(new QName("xsi:type"), "xsd:string");

        soapElement.addChildElement("email", "ns")
            .addTextNode("rstanton@test.com")
            .addAttribute(new QName("xsi:type"), "xsd:string");

        soapElement.addChildElement("quiet", "ns")
            .addTextNode("0")
            .addAttribute(new QName("xsi:type"), "xsd:boolean");

        soapMessage.saveChanges();

        SOAPMessage del = SympaClient.callSympaAPI(soapMessage);

        System.out.println("\n del Response : ");
        SympaClient.printSOAPMessage(del);

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 
     * @param cookie
     */
    public static void getComplexLists(String cookie) {
        try {

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        //String myNamespaceURI = "https://lists-dev.techservices.illinois.edu/lists/wsdl";
        SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

        MimeHeaders headers = soapMessage.getMimeHeaders();

        //headers.addHeader("Authorization", encodedAuth);
        headers.addHeader("Content-Type", "text/xml"); //application/soap+xml
        headers.addHeader("SOAPAction", "urn:sympasoap#complexLists"); 
        headers.addHeader("Cookie", "sympa_session="+cookie);
        SOAPBody soapBody = envelope.getBody();
        
        SOAPElement soapElement = soapBody.addChildElement("complexLists", "ns", "urn:sympasoap");

        soapMessage.saveChanges();

        System.out.println("\n  Soap Call for complexLists ");
        
        SOAPMessage lists = SympaClient.callSympaAPI(soapMessage);

        System.out.println("\n complexLists Response : ");
        SympaClient.printSOAPMessage(lists);

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 
     * @param cookie
     */
    public static void closeList(String cookie) {
        try {

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        //String myNamespaceURI = "https://lists-dev.techservices.illinois.edu/lists/wsdl";
        SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

        MimeHeaders headers = soapMessage.getMimeHeaders();

        //headers.addHeader("Authorization", encodedAuth);
        headers.addHeader("Content-Type", "text/xml"); //application/soap+xml
        headers.addHeader("SOAPAction", "urn:sympasoap#closeList"); 
        headers.addHeader("Cookie", "sympa_session="+cookie);
        SOAPBody soapBody = envelope.getBody();
        
        SOAPElement soapElement = soapBody.addChildElement("closeList", "ns", "urn:sympasoap");
        soapElement.addChildElement("list", "ns")
            .addTextNode("rstanton_samplelist_1")
            .addAttribute(new QName("xsi:type"), "xsd:string");

        soapMessage.saveChanges();

        System.out.println("\n  Soap Call for closeList ");
        
        SOAPMessage closeList = SympaClient.callSympaAPI(soapMessage);

        System.out.println("\n closeList Response : ");
        SympaClient.printSOAPMessage(closeList);

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
