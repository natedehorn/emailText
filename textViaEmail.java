package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.Console;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class textViaEmail {

    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter phone number to text: ");
        String answer = input.nextLine();
        String carrier = findCarrier(answer);
        String carrierEmail = getCarrierEmail(carrier);
        String fullAddress = answer + carrierEmail;

        System.out.print("Enter your email: ");
        String email = input.nextLine();

        Console console = System.console();
        String pass = new String(console.readPassword("Please enter your password: "));

        System.out.print("Enter the message you wish to send: ");
        String body = input.nextLine();

        System.out.println("Sending message. . .");
        sendMessage(email, pass, fullAddress, body);
    }

    //Method to get the carrier, given a phone number
    public static String findCarrier(String phoneNumber) throws IOException {
        //Get number by area code, prefix, and postfix
        String areaCode = phoneNumber.substring(0,3);
        String firstDigits = phoneNumber.substring(3,6);
        String finalDigits = phoneNumber.substring(6,10);

        //Build url to get more info
        String url = "http://www.fonefinder.net/findome.php?npa=" + areaCode +"&nxx=" + firstDigits + "&thoublock=" + finalDigits + "&usaquerytype=Search+by+Number&cityname=";

        //Extract Carrier
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select("a[href]"); //All 'a' elements that contain a link
        Element tableElement = elements.get(5);
        String link = tableElement.attr("abs:href"); //Extract the link
        int linkLength = link.length();
        link = link.substring(22,linkLength-4); //Gets just the last part that we want
        return link;
    }

    //Method to get the carrier messaging email, given the carrier
    public static String getCarrierEmail(String carrier){
        String carrierEmail = null;
        if(carrier.equals("att"))
        {
            carrierEmail = "@txt.att.net";
        }
        else if(carrier.equals("verizon"))
        {
            carrierEmail = "@vtext.com";
        }
        else if(carrier.equals("sprint"))
        {
            carrierEmail = "@messaging.sprintpcs.com";

        }
        return carrierEmail;
    }

    //Method to send a text message via email, given the email address to send the message to
    public static void sendMessage(String senderAddress, String senderPassword, String sendTo, String bodyText){
        final String user = senderAddress;
        final String password = senderPassword;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sendTo));
            message.setText(bodyText);

            Transport.send(message);

            System.out.println("Message Sent!");

        }
        catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}