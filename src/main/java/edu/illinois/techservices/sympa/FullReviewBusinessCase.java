package edu.illinois.techservices.sympa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPConnectionFactory;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPPart;
import jakarta.xml.soap.SOAPFault;

/**
 * 1. Demonstration of how to contact a Sympa server to list the members,
 * editors
 * and owners of the given list.
 * 2. Enumerates through the full review results and print the
 * list of users by the given permission type (subscriber/owner/editor).
 * <P>
 * This demonstration uses the SAAJ API for accessing the SOAP server. These
 * system properties may be set at the command line to override default values:
 * 
 * <DL>
 * <DT><CODE>sympa.url</CODE>
 * <DD>URL for the Sympa SOAP server.
 * <DT><CODE>sympa.username</CODE>
 * <DD>Sympa SOAP server username.
 * <DT><CODE>sympa.password</CODE>
 * <DD>Sympa SOAP server password.
 * <DL>
 * 
 * <P>
 * <STRONG>NOTE:</STRONG> we acknowledge using a system property for a password
 * is very poor security practice, however this is a demonstration program and
 * should never be deployed to a production environment where security is
 * valued.
 */
public class FullReviewBusinessCase implements Runnable {

    public static final String DEFAULT_SYMPA_URL = "http://localhost:8080/sympasoap";

    public static final String DEFAULT_SYMPA_EMAIL = "sympaemail";

    public static final String DEFAULT_SYMPA_PASSWORD = "sympapassword";

    private String sympaUrl = System.getProperty("sympa.url", DEFAULT_SYMPA_URL);

    private String sympaEmail = System.getProperty("sympa.email", DEFAULT_SYMPA_EMAIL);

    private String sympaPassword = System.getProperty("sympa.password", DEFAULT_SYMPA_PASSWORD);

    private String sessionCookie = "";

    private SOAPMessage sympaResponse;

    private String permissionType;
    private String listName;

    /**
     * <p>
     * This constructor validates the inputs:
     * <ul>
     * <li>{@code listName} must not be {@code null} or blank</li>
     * <li>{@code permissionType} must not be {@code null} or blank</li>
     * </ul>
     * 
     * @param args length must be the user's name; must not be {@code null} or blank
     * @throws IllegalArgumentException if {@code listName} is null/blank or
     *                                  {@code permissionType} is negative
     */
    public FullReviewBusinessCase(String[] args) {

        if (args[0] == null || args[0].isEmpty()) {
            throw new IllegalArgumentException("Please provide listName as a first argument.");
        }
        if (args[1] == null || args[1].isEmpty()) {
            throw new IllegalArgumentException(
                    "Please provide type (subscriber, editor or owner) as  a second argument.");
        }

        this.listName = args[0];
        this.permissionType = args[1];
    }

    /**
     * Run the demo.
     * 
     * <P>
     * A demonstration of the flow for interacting with Sympa. The program will
     * verify the credentials and sympa server url have been set to some value, a
     * login will be attempted, and the call to fullReview will be executed. If
     * fullReview is successful, the SOAPMessage for this class will be set and can
     * be read from a call to {@link #getSympaResponse()}. If there's a fault,
     * calling
     * the same {@link #getSympaResponse()} will make the SOAPMessage available for
     * additional processing.
     */
    @Override
    public void run() {
        checkCredentialsSet();
        checkSympaUrlSet();
        login();

        // Make sure a SOAP fault wasn't returned. If the {@link #sympaResponse} isn't
        // {@code null}, then there was a fault and this method should return
        // immediately.
        if (Objects.nonNull(sympaResponse)) {
            return;
        }

        // Make sure the session cookie has been set, otherwise that's an error.
        if (sessionCookie.length() == 0) {
            throw new RuntimeException("Login failed, check that your credentials and url are set correctly.");
        }

        getFullReview();
    }

    /**
     * Demonstrates a Sympa login.
     * 
     * <P>
     * Logins must be run before execution of requested operation. This method will
     * send the credentials and will take the session cookie from the response if
     * successful.
     * 
     * <P>
     * If the Sympa server sends a SOAP fault, the message is captured and can be
     * accessed via calls to {@link #getSympaResponse()}. No exception will be
     * thrown because of a SOAP fault.
     */
    private void login() {
        try {
            SOAPMessage smLogin = MessageFactory.newInstance().createMessage();
            SOAPPart spLogin = smLogin.getSOAPPart();

            // Declare SOAP and sympa namespaces
            SOAPEnvelope senvLogin = spLogin.getEnvelope();
            addSympaNamespaceDeclarations(senvLogin);

            // Create SOAP request body
            SOAPBody sbLogin = senvLogin.getBody();
            SOAPElement seltLogin = sbLogin.addChildElement("login", "ns", "urn:sympasoap");
            seltLogin.addChildElement("email").addTextNode(sympaEmail).addAttribute(new QName("xsi:type"),
                    "xsd:string");
            seltLogin.addChildElement("password").addTextNode(sympaPassword).addAttribute(new QName("xsi:type"),
                    "xsd:string");

            // Set MIME headers
            MimeHeaders mhLogin = smLogin.getMimeHeaders();
            mhLogin.addHeader("Content-Type", "text/xml");
            mhLogin.addHeader("SOAPAction", "urn:sympasoap#login");
            mhLogin.addHeader("RequestMethod", "POST");

            smLogin.saveChanges();

            // Initialize SOAP connection
            SOAPConnectionFactory scfLogin = SOAPConnectionFactory.newInstance();
            SOAPConnection scLogin = scfLogin.createConnection();

            // Send requst and expect a response.
            SOAPMessage smLoginResponse = scLogin.call(smLogin, sympaUrl);

            // Get the session cookie that verifies authentication on subsequent functional
            // calls or capture a SOAP fault.
            SOAPBody sbLoginResponse = smLoginResponse.getSOAPBody();
            if (sbLoginResponse.hasFault()) {
                sympaResponse = smLoginResponse;
            } else {
                sessionCookie = sbLoginResponse.getTextContent();
            }

        } catch (SOAPException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Demonstrates a call to the Sympa API to get members, owners and scubscribers
     * of a list.
     * 
     * <P>
     * This method should be called <STRONG>AFTER</STRONG> {@link #login()}. The
     * method {@link #getSympaResponse()} can be called to get the SOAP message that
     * was returned for additional processing after this method completes.
     */
    private void getFullReview() {
        try {
            // Create SOAP Message
            SOAPMessage smGetFullReview = MessageFactory.newInstance().createMessage();
            SOAPPart spGetFulleReview = smGetFullReview.getSOAPPart();

            // Declare SOAP and sympa namespaces
            SOAPEnvelope senvGetFullReview = spGetFulleReview.getEnvelope();
            addSympaNamespaceDeclarations(senvGetFullReview);

            // Create SOAP request body.
            SOAPBody sbGetFullReview = senvGetFullReview.getBody();
            @SuppressWarnings("seGetFullReview")
            SOAPElement seGetFullReview = sbGetFullReview.addChildElement("fullReview", "ns", "urn:sympasoap");
            seGetFullReview.addChildElement("list", "ns")
                    .addTextNode(listName)
                    .addAttribute(new QName("xsi:type"), "xsd:string");

            MimeHeaders headers = smGetFullReview.getMimeHeaders();
            headers.addHeader("Content-Type", "text/xml");
            headers.addHeader("SOAPAction", "urn:sympasoap#fullReview");
            headers.addHeader("Cookie", "sympa_session=" + sessionCookie);

            smGetFullReview.saveChanges();

            // Initialize SOAP connection
            SOAPConnectionFactory scfGetFullReview = SOAPConnectionFactory.newInstance();
            SOAPConnection scGetFullReview = scfGetFullReview.createConnection();

            // Send requst and expect a response. This could be a SOAP fault if the server
            // returns one. Most of the time, expect the payload to be the mailing lists.
            sympaResponse = scGetFullReview.call(smGetFullReview, sympaUrl);

            // Create a SOAP connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Call Sympa API by passing SOAPMessage
            SOAPMessage fullreview = soapConnection.call(smGetFullReview, sympaUrl);

            soapConnection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculates the list of users based on the permission type picked.(subscriber,
     * editor or owner)
     * 
     * <P>
     * This method body is specific to only fullReview. This is generated by
     * considering the xml output is in certain format.
     */
    private void enumerateListByType() throws Exception {

        int count = 0;
        List<String> emailList = new ArrayList<>();

        SOAPBody responseBody = getSympaResponse().getSOAPBody();

        if (responseBody.hasFault()) {
            SOAPFault fault = responseBody.getFault();
            System.err.println("SOAP Fault: " + fault.getFaultString());
            // TODO: throw an exception
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

                    if (field.getNodeType() == Node.ELEMENT_NODE) {
                        if ("email".equalsIgnoreCase(field.getLocalName())) {
                            email = fieldValue;
                        }
                        if (permissionType.equals(field.getLocalName()) && "true".equalsIgnoreCase(fieldValue)) {
                            flag = true;
                        }
                        if (!"".equals(email) && email != null && flag) {
                            System.out.println(email);

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
    }

    /**
     * Adds Sympa and SOAP specific namespace declarations to the SOAP envelope.
     * 
     * @param senv the SOAPEnvelope.
     * @throws SOAPException if a SOAPException is thrown.
     */
    private void addSympaNamespaceDeclarations(SOAPEnvelope senv) throws SOAPException {
        senv.addNamespaceDeclaration("ns", "urn:sympasoap");
        senv.addNamespaceDeclaration("soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        senv.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
        senv.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        senv.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        senv.addNamespaceDeclaration("targetNamespace",
                "https://lists-dev.techservices.illinois.edu/lists/wsdl");
        senv.addNamespaceDeclaration("SOAP-ENC", "http://schemas.xmlsoap.org/soap/encoding/");
        senv.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");
    }

    /**
     * Checks if username and password is non null and non empty. If empty, throw an
     * IllegalStateException with a helpful message.
     * 
     * @throws IllegalStateException if credentials are not set.
     */
    private void checkCredentialsSet() {
        if (Objects.isNull(sympaEmail) || sympaEmail.isEmpty()) {
            throw new IllegalStateException("Sympa email not set! Use -Dsympa.email system property.");
        }
        if (Objects.isNull(sympaPassword) || sympaPassword.isEmpty()) {
            throw new IllegalStateException("Sympa password not set! Use -Dsympa.password system property.");
        }
    }

    /**
     * Checks if the sympa url is non null and non empty. If empty, throw an
     * IllegalStateException with a helpful message.
     * 
     * @throws IllegalStateException if Sympa server url not set.
     */
    private void checkSympaUrlSet() {
        if (Objects.isNull(sympaUrl) || sympaUrl.isEmpty()) {
            throw new IllegalStateException("Sympa url not set! Use -Dsympa.url system property.");
        }
    }

    /**
     * 
     * @return SOAPMessage from Sympa or {@code null} if not set.
     */
    public SOAPMessage getSympaResponse() {
        return sympaResponse;
    }

    /**
     * The main entry point.
     * 
     * <P>
     * Instantiate and run this demo program. Print the Sympa response if
     * successful, stack trace and error message otherwise.
     * 
     * @param args This program takes two command line arguments
     *             1. listName - name of the list
     *             2. permissionType - subscriber, editor or owner
     */
    public static void main(String[] args) {
        if (args.length !=2) {
            System.err.println(
                    "Usage: mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.FullReviewBusinessCase -Dexec:args=<listName> <permissionType>");
            System.exit(0);
        }
        FullReviewBusinessCase fullReview = new FullReviewBusinessCase(args);
        fullReview.run();

        try {
            // Enumerate through sympaResponse object to print list of
            // subscribers, owners or editors.
            fullReview.enumerateListByType();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
