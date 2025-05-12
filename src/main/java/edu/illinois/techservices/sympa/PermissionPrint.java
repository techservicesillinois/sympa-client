package edu.illinois.techservices.sympa;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jakarta.xml.soap.*;

public class PermissionPrint {

    /**
     * Prints the permissions of
     * 
     * @param soapMessage
     */
    public static String permissionPrint(SOAPMessage soapMessage, String type) throws Exception {

        int count = 0;
        List<String> emailList = new ArrayList<>();

        SOAPBody responseBody = soapMessage.getSOAPBody();

        if (responseBody.hasFault()) {
            SOAPFault fault = responseBody.getFault();
            System.err.println("SOAP Fault: " + fault.getFaultString());

            return " ";
        }

        NodeList returnNodes = responseBody.getElementsByTagName("return");
        if (returnNodes.getLength() == 0) {
            System.out.println("No <return> element found.");
        }

        if ("subscriber".equalsIgnoreCase(type)) {
            type = "isSubscriber";
        } else if ("owner".equalsIgnoreCase(type)) {
            type = "isOwner";
        } else {
            type = "isEditor";
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
                    System.out.println("Field Name: " + field.getLocalName() + "; fieldValue: " + fieldValue);

                    if (field.getNodeType() == Node.ELEMENT_NODE) {
                        if ("email".equalsIgnoreCase(field.getLocalName())) {
                            email = fieldValue;
                        }
                        if (type.equals(field.getLocalName()) && "true".equalsIgnoreCase(fieldValue)) {
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
            System.out.println("emailList: " + emailList);
        }
        System.out.println("emailList: " + emailList);
        return "";
    }
}
