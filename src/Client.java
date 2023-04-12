import util.NetworkUtil;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private String name;
    private NetworkUtil networkUtil;
    private NetworkInformation networkInformation = new NetworkInformation();

    public Client(String serverAddress, int serverPort) {
        try {
            System.out.print("Enter name of the client: ");
            Scanner scanner = new Scanner(System.in);
            name = scanner.nextLine();
            networkUtil = new NetworkUtil(serverAddress, serverPort);
            networkUtil.write(name);

            new Thread(new ReadThreadClient()).start();
            new Thread(new WriteThreadClient()).start();


        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Write thread class (Anonymous inner class)

    private class WriteThreadClient implements Runnable {

        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String localMessage = "";
                String to = "";
                String messageBody = "";
                while (localMessage.isEmpty()) {

                    localMessage = scanner.nextLine();

                    String[] split = localMessage.split(","); // split the message with ,

                    /*

                    Receiver = split[0]
                    Message = split[1]

                     */

                    to = split[0];
                    messageBody = split[1];

                    if (to.toLowerCase().equals("server")) {
                        printInboxMessages();
                    }

                }
                Message message = new Message();
                message.setFrom(name);
                message.setTo(to);
                message.setText(messageBody);
                try {
                    networkUtil.write(message);
                } catch (Exception e) {
                    System.out.println(e);
                    break;
                }
            }
        }
    }


    public void printInboxMessages() {
        for (int i = 0; i < networkInformation.inbox.size(); i++) {
            System.out.println(networkInformation.inbox.get(i));
        }
    }


    // ReadThreadClient class (anonymous inner class)
    private class ReadThreadClient implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Object receivedObject = networkUtil.read();
                    if (receivedObject instanceof Message) {

                        /*
                        receivedObject instanceof Message means that the variable receivedObject
                        is being tested to see if it is an instance of the Message class or one of its subclasses
                         */
                        Message message = (Message) receivedObject;
                        networkInformation.setMessages("From: " + message.getFrom() + " Message: " + message.getText());
                        System.out.println("From: " + message.getFrom() + " Message: " + message.getText());

                    }
                } catch (Exception e) {
                    System.out.println(e);
                    break;
                }
            }
        }
    }

    public static void main(String args[]) {
        String serverAddress = "127.0.0.1";
        int serverPort = 33333;
        Client client = new Client(serverAddress, serverPort);
    }
}
