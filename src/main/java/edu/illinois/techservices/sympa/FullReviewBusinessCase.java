package edu.illinois.techservices.sympa;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import javax.xml.namespace.QName;

import jakarta.xml.soap.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1. Lists the members, editors and owners of the given list
 * 2. Enumerates through the full review results and print the
 * list of users by the given permission type (subscriber/owner/editor).
 */
public class FullReviewBusinessCase {

    private static final Logger logger = LoggerFactory.getLogger(FullReviewBusinessCase.class);
    private String cookie;
    private static String sympaSoapUrl = "https://lists-dev.techservices.illinois.edu/sympasoap";

    private String sympaEmail = "changeme"; // Fill in and recompile.
    private String sympaPassword = "changeme";
    private String permissionType;
    private String listName;

    public FullReviewBusinessCase(String[] args) {
        if (args[0] == null || args[0].isEmpty()) {
            throw new IllegalArgumentException("Please provide listName for the fullReview.");
        }
        if (args[1] == null || args[1].isEmpty()) {
            throw new IllegalArgumentException("Please provide type (subscriber, editor or owner) for the fullReview.");
        }

        this.listName = args[0];
        this.permissionType = args[1];
        sympaLogin();
    }

    /**
     * COnnect to Sympa server and retrieve the cookie from it.
     */
    private void sympaLogin() {
        try {
            // Create SOAPMessage from MessageFactory.
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();

            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();

            // Declare relevant XML namespaces used by Sympa SOAP Service.
            envelope.addNamespaceDeclaration("ns", "urn:sympasoap");
            envelope.addNamespaceDeclaration("soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
            envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
            envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
            envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            envelope.addNamespaceDeclaration("targetNamespace",
                    "https://lists-dev.techservices.illinois.edu/lists/wsdl");
            envelope.addNamespaceDeclaration("SOAP-ENC", "http://schemas.xmlsoap.org/soap/encoding/");
            envelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

            // Set the parameters for login call to grab the cookie.
            SOAPBody soapBody = envelope.getBody();
            SOAPElement soapElement = soapBody.addChildElement("login", "ns", "urn:sympasoap");
            soapElement.addChildElement("email")
                    .addTextNode(sympaEmail)
                    .addAttribute(new QName("xsi:type"), "xsd:string");
            soapElement.addChildElement("password")
                    .addTextNode(sympaPassword)
                    .addAttribute(new QName("xsi:type"), "xsd:string");

            // Set Mime Headers needed by HTTPTransport.
            MimeHeaders headers = soapMessage.getMimeHeaders();
            headers.addHeader("Content-Type", "text/xml");
            headers.addHeader("SOAPAction", "urn:sympasoap#login");
            headers.addHeader("RequestMethod", "POST");

            soapMessage.saveChanges();

            // Create a SOAP connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Make Sympa API call for 'login' by sending the SOAP message to the endpoint.
            SOAPMessage soapResponse = soapConnection.call(soapMessage, sympaSoapUrl);
            soapConnection.close();

            logger.debug("session cookie: {}", printSOAPMessage(soapResponse));

            // Grab Session Cookie from ResponseBody
            SOAPBody responseBody = soapResponse.getSOAPBody();
            if (responseBody.hasFault()) {
                SOAPFault fault = responseBody.getFault();
                System.err.println("SOAP Fault: " + fault.getFaultString());
            } else {
                cookie = responseBody.getTextContent();
            }
        } catch (Exception e) {
            logger.error("\n THE ERROR...\n");
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    public void run() {
        // code to build the request envelope
        // send SOAP request
        // receive response
        // print response

        try {
            // Create SOAP Message
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();

            // Declare relevant XML namespaces used by Sympa SOAP Service.
            envelope.addNamespaceDeclaration("ns", "urn:sympasoap");
            envelope.addNamespaceDeclaration("soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
            envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
            envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
            envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            envelope.addNamespaceDeclaration("targetNamespace",
                    "https://lists-dev.techservices.illinois.edu/lists/wsdl");
            envelope.addNamespaceDeclaration("SOAP-ENC", "http://schemas.xmlsoap.org/soap/encoding/");
            envelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

            MimeHeaders headers = soapMessage.getMimeHeaders();
            headers.addHeader("Content-Type", "text/xml");
            headers.addHeader("SOAPAction", "urn:sympasoap#fullReview");
            headers.addHeader("Cookie", "sympa_session=" + cookie);
            SOAPBody soapBody = envelope.getBody();

            SOAPElement soapElement = soapBody.addChildElement("fullReview", "ns", "urn:sympasoap");
            soapElement.addChildElement("list", "ns")
                    .addTextNode(listName)
                    .addAttribute(new QName("xsi:type"), "xsd:string");
            soapMessage.saveChanges();

            printSOAPMessage(soapMessage);

            // Create a SOAP connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Call Sympa API by passing SOAPMessage
            SOAPMessage fullreview = soapConnection.call(soapMessage, sympaSoapUrl);

            soapConnection.close();

            logger.debug("\n Full Review Response : {}", printSOAPMessage(fullreview));

            List<String> emailList = enumerateListByType(fullreview);

            if (emailList.size() > 0) {
                logger.debug("permission type = {}", permissionType);
                for (String email : emailList) {
                    System.out.println(email);
                }
            } else {
                System.out.println("\n \n No " + permissionType + " for the list " + listName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the permissions of
     * 
     * @param soapMessage
     */
    private List<String> enumerateListByType(SOAPMessage soapMessage) throws Exception {

        int count = 0;
        List<String> emailList = new ArrayList<>();

        SOAPBody responseBody = soapMessage.getSOAPBody();

        if (responseBody.hasFault()) {
            SOAPFault fault = responseBody.getFault();
            System.err.println("SOAP Fault: " + fault.getFaultString());

            return emailList;
        }

        NodeList returnNodes = responseBody.getElementsByTagName("return");
        if (returnNodes.getLength() == 0) {
            System.out.println("No <return> element found.");
        }

        if ("subscriber".equalsIgnoreCase(permissionType)) {
            permissionType = "isSubscriber";
        } else if ("owner".equalsIgnoreCase(permissionType)) {
            permissionType = "isOwner";
        } else {
            permissionType = "isEditor";
        }

        Node returnNode = returnNodes.item(0);
        NodeList items = returnNode.getChildNodes();
        Boolean flag = false;
        for (int i = 0; i < items.getLength(); i++) {
            Node item = items.item(i);
            String email = "";
            if (item.getNodeType() == Node.ELEMENT_NODE && "item".equals(item.getLocalName())) {

                NodeList fields = item.getChildNodes();

                for (int j = 0; j < fields.getLength(); j++) {
                    Node field = fields.item(j);
                    String fieldValue = field.getTextContent() != null ? field.getTextContent().trim() : "";
                    logger.debug("Field Name: " + field.getLocalName() + "; fieldValue: " +
                            fieldValue);

                    if (field.getNodeType() == Node.ELEMENT_NODE) {
                        if ("email".equalsIgnoreCase(field.getLocalName())) {
                            email = fieldValue;
                        }
                        if (permissionType.equals(field.getLocalName()) && "true".equalsIgnoreCase(fieldValue)) {
                            flag = true;
                        }
                        if (!"".equals(email) && email != null && flag) {
                            emailList.add(email);

                            // Reset the 'flag' and 'email' once we find the necessary type(i.e.,
                            // editor/owner/subscriber) so the values that are
                            // set are not repeated in the next iteration.
                            flag = false;
                            email = "";
                        }
                    }
                }
            }
        }
        return emailList;
    }

    /**
     * Print the contents of soap message to the console.
     * 
     * @param message
     * @throws Exception
     */
    public static String printSOAPMessage(SOAPMessage message) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        message.writeTo(out);

        return new String(out.toByteArray());
    }

    public static void main(String[] args) {
        FullReviewBusinessCase bc = new FullReviewBusinessCase(args);
        bc.run();
    }

}
