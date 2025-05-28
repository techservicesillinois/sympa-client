package edu.illinois.techservices.sympa;

import java.util.Objects;

import javax.xml.namespace.QName;

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
 * 1. Demonstration of how to contact a Sympa server to delete a member from
 *    a given list.
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
public class Delete implements Runnable {

    public static final String DEFAULT_SYMPA_URL = "http://localhost:8080/sympasoap";

    public static final String DEFAULT_SYMPA_EMAIL = "sympaemail";

    public static final String DEFAULT_SYMPA_PASSWORD = "sympapassword";

    private String sympaUrl = System.getProperty("sympa.url", DEFAULT_SYMPA_URL);

    private String sympaEmail = System.getProperty("sympa.email", DEFAULT_SYMPA_EMAIL);

    private String sympaPassword = System.getProperty("sympa.password", DEFAULT_SYMPA_PASSWORD);

    private String sessionCookie = "";

    private SOAPMessage sympaResponse;

    private String listName;
    private String userEmail;
    private String userDescription;
    private Boolean quiet = false;
    
    /**
     * <p>
     * This constructor validates the inputs:
     * <ul>
     * <li>{@code listName} must not be {@code null} or blank</li>
     * <li>{@code email} must not be {@code null} or blank</li>
     * <li>{@code quiet} is optional</li>
     * </ul>
     * 
     * @param args length must be the user's name; must not be {@code null} or blank
     * @throws IllegalArgumentException if {@code listName} or {@code email} or
     *                                     {@code quiet} is not an accepted Boolean value
     */
    public Delete(String[] args) {

        if (args[0] == null || args[0].isEmpty()) {
            throw new IllegalArgumentException("Please provide a 'listName' to delete a user email from as the first argument.");
        }
        if (args[1] == null || args[1].isEmpty()) {
            throw new IllegalArgumentException(
                    "Please provide the email to remove from the list as the second argument.");
        }
        
        this.listName = args[0];
        this.userEmail = args[1];
        
        /* verify that if the user passes in a value to override the Boolean default of false,
           that it is either 'true', '1', 'false', or '0' before we convert it to a Boolean value */
        if (args[2] != null && !(args[3].isEmpty())) {
            if (args[2].equalsIgnoreCase("true") || 
                args[2].equalsIgnoreCase("1") || 
                args[2].equalsIgnoreCase("false") || 
                args[2].equalsIgnoreCase("0")) {
                    this.quiet = Boolean.valueOf(args[2]);
            }
            else {
                throw new IllegalArgumentException(
                    "Please provide a valid Boolean value for the Quiet option.");
            }
        }
    }

    /**
     * Run the demo.
     * 
     * <P>
     * A demonstration of the flow for interacting with Sympa. The program will
     * verify the credentials and sympa server url have been set to some value, a
     * login will be attempted, and the call to deleteMember will be executed. If
     * deleteMember is successful, the SOAPMessage for this class will be set and can
     * be read from a call to {@link #getSympaResponse()}. If there's a fault,
     * calling the same {@link #getSympaResponse()} will make the SOAPMessage 
     * available for additional processing.
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

        deleteMember();
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
     * Demonstrates a call to the Sympa API to remove a member from a list.
     * 
     * <P>
     * This method should be called <STRONG>AFTER</STRONG> {@link #login()}. The
     * method {@link #getSympaResponse()} can be called to get the SOAP message that
     * was returned for additional processing after this method completes.
     */
    private void deleteMember() {
        try {
            // Create SOAP Message
            SOAPMessage smDeleteMember = MessageFactory.newInstance().createMessage();
            SOAPPart spDeleteMember = smDeleteMember.getSOAPPart();

            // Declare SOAP and sympa namespaces
            SOAPEnvelope senvDeleteMember = spDeleteMember.getEnvelope();
            addSympaNamespaceDeclarations(senvDeleteMember);

            // Create SOAP request body.
            SOAPBody sbDeleteMember = senvDeleteMember.getBody();
            SOAPElement seDeleteMember = sbDeleteMember.addChildElement("del", "ns", "urn:sympasoap");
            seDeleteMember.addChildElement("list", "ns")
                .addTextNode(listName)
                .addAttribute(new QName("xsi:type"), "xsd:string");

            seDeleteMember.addChildElement("email", "ns")
                .addTextNode(userEmail)
                .addAttribute(new QName("xsi:type"), "xsd:string");

            seDeleteMember.addChildElement("quiet", "ns")
                .addTextNode(quiet.toString())
                .addAttribute(new QName("xsi:type"), "xsd:boolean");

            MimeHeaders headers = smDeleteMember.getMimeHeaders();
            headers.addHeader("Content-Type", "text/xml");
            headers.addHeader("SOAPAction", "urn:sympasoap#del");
            headers.addHeader("Cookie", "sympa_session=" + sessionCookie);

            smDeleteMember.saveChanges();

            // Initialize SOAP connection
            SOAPConnectionFactory scfDeleteMember = SOAPConnectionFactory.newInstance();
            SOAPConnection scDeleteMember = scfDeleteMember.createConnection();

            // Send requst and expect a response. This could be a SOAP fault if the server
            // returns one. Most of the time, expect the payload to be the mailing lists.
            sympaResponse = scDeleteMember.call(smDeleteMember, sympaUrl);

            // Create a SOAP connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Call Sympa API by passing SOAPMessage
            SOAPMessage deletemember = soapConnection.call(smDeleteMember, sympaUrl);

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
     * @param args This program doesn't take any command line arguments.
     */
    public static void main(String[] args) {
        Delete deleteUser = new Delete(args);
        deleteUser.run();

        try {
            // print Sympa response
            deleteUser.getSympaResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}