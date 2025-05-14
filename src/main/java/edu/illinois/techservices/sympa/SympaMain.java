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

public class SympaMain {

  public static void main(String[] args) {
    String sessionCookie = null;
    String input = null;
    try {
      input = args[0];
      System.out.println("input: " + input);
    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println("[ERROR] Please provide an argument for function call.");
      System.out.println("  Example args: getList, createList, getInfo");
      System.out.println("  Example Usage: -Dexec.args=\"getList\"");
      throw new IllegalArgumentException("[ERROR] Please provide an argument for function call. See logs for details.");
    }
    // TODO: Validate call before logging in (use enum or something similar?)
    sessionCookie = SympaClient.loginSympa();

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
                .println("Please Provide all parameters required to perform createList");
            System.exit(0);
          }

        }
        case "getInfo": {
          SympaClient.getInfo(sessionCookie);
          break;
        }
        case "authenticateAndRun": {
          String service = null;
          List<String> serviceParameters = new ArrayList<>();

          if (args.length == 3) {
            String parameters = null;
            service = args[1];
            parameters = args[2];
            serviceParameters.addAll(Arrays.asList(parameters.split(",")));
            System.out.println("service: " + service);
            System.out.println("serviceParameters: " + serviceParameters.size());
            System.out.println("serviceParameters: " + serviceParameters);

            SympaLoginClient.authenticateAndRun(sessionCookie, service, serviceParameters);
            break;
          } else {
            System.out
                .println("Please Provide service(for example: add/del) and parameters required to perform add/del");
            System.exit(0);
          }
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
        case "add":
        {
          System.out.println("in add case");
          if (args.length >= 5) {
            SympaListOps.add(sessionCookie, args);
            break;
          } else {
            System.out
                .println("Please Provide all parameters required to perform add");
            System.exit(0);
          }
        }
        case "del":
        {
          if (args.length >= 4){
            SympaListOps.del(sessionCookie, args);
            break;
          }
          else {
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
          if (args.length >= 1){
            SympaListOps.closeList(sessionCookie, args);
            break;
          }
          else {
            System.out
                .println("Please Provide all parameters required to perform closeList");
            System.exit(0);
          }
        }
        case "which": {
          if (args.length != 2) {
            System.out.println("Please provide a list name to check");
            System.exit(0);
          }
          String listName = args[1];
          Which.which(listName);
          break;
        }
        case "complexWhich": {
          if (args.length != 2) {
            System.out.println("Please provide a list name to check");
            System.exit(0);
          }
          String listName = args[1];
          Which.complexWhich(listName);
          break;
        }
        default:
          System.out.println("Invalid API call. Please provide a valid function call.");
          System.out.println("Example args: getList, createList, getInfo");
          System.out.println("Example Usage: -Dexec.args=\"getList\"");
          System.out.println("Use space-separated arguments for calls with parameters.");
          System.out.println("Received args: " + Arrays.toString(args));
          System.exit(0);
      }
    }
  }
}
