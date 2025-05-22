package edu.illinois.techservices.sympa;

import edu.illinois.techservices.sympa.SympaListOps;
import edu.illinois.techservices.sympa.SympaLoginClient;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPMessage;
import edu.illinois.techservices.sympa.Review;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SympaMain {
  private static final Logger logger = LoggerFactory.getLogger(SympaMain.class);

  public static void main(String[] args) {
    logger.info("Application started. Starting Sympa Main with arguments:");

    String sessionCookie = null;
    String input = null;
    try {
      input = args[0];
      logger.info("Sympa service to be called: {}", input);
    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println("[ERROR] Please provide an argument for function call.");
      System.out.println("  Example args: getList, createList, getInfo");
      System.out.println("  Example Usage: -Dexec.args=\"getList\"");
      throw new IllegalArgumentException("[ERROR] Please provide an argument for function call. See logs for details.");
    }

    logger.debug("Grab session cookie... ");
    // TODO: Validate call before logging in (use enum or something similar?)
    sessionCookie = SympaClient.loginSympa();
    logger.debug("sessionCookie = {}", sessionCookie);
    if (sessionCookie != null) {
      switch (input) {
        case "getList": {
          SympaClient.getLists(sessionCookie);
          break;
        }
        case "createList": {
          if (args.length > 5) {
            SympaClient.createList(sessionCookie, args);
            break;
          } else {
            System.out
                .println("Please provide all required parameters to perform createList");
            System.exit(0);
          }

        }
        case "info": {
          SympaClient.getInfo(sessionCookie, args[1]);
          break;
        }
        case "authenticateAndRun": {

          /*
           * AuthenticateAndRun api = new AuthenticateAndRun();
           * api.Run();
           * break;
           */

          /*
           * String service = null;
           * List<String> serviceParameters = new ArrayList<>();
           * 
           * if (args.length == 3) {
           * String parameters = null;
           * service = args[1];
           * parameters = args[2];
           * serviceParameters.addAll(Arrays.asList(parameters.split(",")));
           * logger.debug("service = {}", service);
           * logger.debug("serviceParameters: ", serviceParameters.size());
           * logger.debug("serviceParameters: ", serviceParameters);
           * 
           * SympaLoginClient.authenticateAndRun(sessionCookie, service,
           * serviceParameters);
           * break;
           * } else {
           * System.out
           * .println("Please Provide service(for example: add/del) and parameters required to perform add/del"
           * );
           * System.exit(0);
           * }
           */
        }
        case "authenticateRemoteAppAndRun": {
          if (args.length > 5) {
            SympaLoginClient.authenticateRemoteAppAndRun(args);
            break;
          } else {
            System.out
                .println("Please Provide all parameters required to perform authenticateRemoteAppAndRun");
            System.exit(0);
          }
        }
        case "getUserEmailByCookie": {
          SympaLoginClient.getUserEmailByCookie(sessionCookie);
          break;
        }
        case "review": {
          String listName = null;
          if (args.length > 0) {
            listName = args[1];
          }
          Review.review(sessionCookie, listName);
          break;
        }
        case "subscribe": {
          String listName = null;
          if (args.length > 0) {
            listName = args[1];
          }
          Subscribe.subscribe(sessionCookie, listName);
          break;
        }
        case "fullreview": {
          String listName = null;
          String type = null;
          if (args.length > 0) {
            listName = args[1];
            type = args[2];

          }
          FullReview.fullreview(sessionCookie, listName, type);
          break;
        }
        case "add": {
          if (args.length >= 5) {
            SympaListOps.add(sessionCookie, args);
            break;
          } else {
            System.out
                .println("Please Provide all parameters required to perform add");
            System.exit(0);
          }
        }
        case "del": {
          if (args.length >= 4) {
            SympaListOps.del(sessionCookie, args);
            break;
          } else {
            System.out
                .println("Please Provide all parameters required to perform del");
            System.exit(0);
          }
        }
        case "getComplexLists": {
          SympaListOps.getComplexLists(sessionCookie);
          break;
        }
        case "closeList": {
          if (args.length >= 1) {
            SympaListOps.closeList(sessionCookie, args);
            break;
          } else {
            System.out
                .println("Please Provide all parameters required to perform closeList");
            System.exit(0);
          }
        }
        default:
          System.out.println("Invalid API call. Please provide a valid function call.");
          System.out.println("Example args: getList, createList, getInfo");
          System.out.println("Example Usage: -Dexec.args=\"getList\"");
      }
    }
  }
}
