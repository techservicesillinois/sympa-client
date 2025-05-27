package edu.illinois.techservices.sympa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

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

import javax.xml.namespace.QName;

/**
 * 1. Demonstration of how to contact a Sympa server to create a new list.
 * 
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
public class CreateList implements Runnable {

    public static final String DEFAULT_SYMPA_URL = "http://localhost:8080/sympasoap";

    public static final String DEFAULT_SYMPA_EMAIL = "sympaemail";

    public static final String DEFAULT_SYMPA_PASSWORD = "sympapassword";

    private String sympaUrl = System.getProperty("sympa.url", DEFAULT_SYMPA_URL);

    private String sympaEmail = System.getProperty("sympa.email", DEFAULT_SYMPA_EMAIL);

    private String sympaPassword = System.getProperty("sympa.password", DEFAULT_SYMPA_PASSWORD);

    private String sessionCookie = "";

    private SOAPMessage sympaResponse;

    private String listName;
    private String subject;
    private String description;
    private String topic;
    private String template;

    /**
     * <p>
     * This constructor validates the inputs since all the parameters are mandatory.
     * <ul>
     * <li>{@code listName} must not be {@code null} or blank</li>
     * <li>{@code permissionType} must not be {@code null} or blank</li>
     * </ul>
     * 
     * @param args
     * @throws IllegalArgumentException if {@code listName} is null/blank or
     *                                  {@code subject} or is null/blank
     *                                  {@code template} or is null/blank
     *                                  {@code description} or is null/blank
     *                                  {@code topic} or is null/blank
     */
    public CreateList(String[] args) {

        if (args[0] == null || args[0].isEmpty()) {
            throw new IllegalArgumentException("Please provide listName as a first argument.");
        }
        if (args[1] == null || args[1].isEmpty()) {
            throw new IllegalArgumentException(
                    "Please provide subject as a second argument.");
        }
        if (args[2] == null || args[2].isEmpty()) {
            throw new IllegalArgumentException("Please provide template as a third argument.");
        }
        if (args[3] == null || args[3].isEmpty()) {
            throw new IllegalArgumentException(
                    "Please provide description as a fourth argument.");
        }
        if (args[4] == null || args[4].isEmpty()) {
            throw new IllegalArgumentException(
                    "Please provide topic as  a fifth argument.");
        }

        this.listName = args[0];
        this.subject = args[1];
        this.template = args[2];
        this.description = args[3];
        this.topic = args[4];
    }

    /**
     * Run the demo.
     * 
     * <P>
     * A demonstration of the flow for interacting with Sympa. The program will
     * verify the credentials and sympa server url have been set to some value, a
     * login will be attempted, and the call to createList will be executed. If
     * createList is successful, the SOAPMessage for this class will be set and can
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

        createList();
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
     * Demonstrates a call to the Sympa API to create a new list.
     * 
     * <P>
     * This method should be called <STRONG>AFTER</STRONG> {@link #login()}. The
     * method {@link #getSympaResponse()} can be called to get the SOAP message that
     * was returned for additional processing after this method completes.
     */
    private void createList() {
        try {
            // Create SOAP Message
            SOAPMessage smCreateList = MessageFactory.newInstance().createMessage();
            SOAPPart spCreateList = smCreateList.getSOAPPart();

            // Declare SOAP and sympa namespaces
            SOAPEnvelope senvCreateList = spCreateList.getEnvelope();
            addSympaNamespaceDeclarations(senvCreateList);

            // Create SOAP request body.
            SOAPBody sbCreateList = senvCreateList.getBody();
            @SuppressWarnings("seCreateList")
            SOAPElement seCreateList = sbCreateList.addChildElement("createList", "ns", "urn:sympasoap");

            seCreateList.addChildElement("list", "ns")
                    .addTextNode(listName)
                    .addAttribute(new QName("xsi:type"), "xsd:string");

            seCreateList.addChildElement("subject", "ns")
                    .addTextNode(subject)
                    .addAttribute(new QName("xsi:type"), "xsd:string");

            seCreateList.addChildElement("template", "ns")
                    .addTextNode(template)
                    .addAttribute(new QName("xsi:type"), "xsd:string");

            seCreateList.addChildElement("description", "ns")
                    .addTextNode(description)
                    .addAttribute(new QName("xsi:type"), "xsd:string");

            seCreateList.addChildElement("topic", "ns")
                    .addTextNode(topic)
                    .addAttribute(new QName("xsi:type"), "xsd:string");

            MimeHeaders headers = smCreateList.getMimeHeaders();
            headers.addHeader("Content-Type", "text/xml");
            headers.addHeader("SOAPAction", "urn:sympasoap#createList");
            headers.addHeader("Cookie", "sympa_session=" + sessionCookie);

            smCreateList.saveChanges();

            // Initialize SOAP connection
            SOAPConnectionFactory scfCreateList = SOAPConnectionFactory.newInstance();
            SOAPConnection scCreateList = scfCreateList.createConnection();

            // Send requst and expect a response. This could be a SOAP fault if the server
            // returns one. Most of the time, expect the payload to be the mailing lists.
            sympaResponse = scCreateList.call(smCreateList, sympaUrl);

            // Create a SOAP connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Call Sympa API by passing SOAPMessage
            SOAPMessage createList = soapConnection.call(smCreateList, sympaUrl);

            soapConnection.close();

        } catch (Exception e) {
            e.printStackTrace();
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
     * @param args This program takes command line arguments.
     *             1. name of the new list (mandatory)
     *             2. subject of the list (mandatort)
     *             3. template to use (mandatory) (the name of a template found in
     *             the create_list_templates directory for this Sympa service
     *             4. description of the list (mandatory)
     *             5. topic of the list (mandatory)
     */
    public static void main(String[] args) {

        if (args.length != 5) {
            System.err.println(
                    "Usage: mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.CreateList -Dexec:args=<listName> <subject> <template> <description> <topic>");
        }

        CreateList app = new CreateList(args);
        app.run();

        // Print the Sympa response to System.out.
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            app.getSympaResponse().writeTo(out);
            System.out.println(new String(out.toByteArray()));
        } catch (SOAPException | IOException e) {
            e.printStackTrace();
        }
    }
}
