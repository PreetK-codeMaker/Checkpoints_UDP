package sender;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Sender {
    private String fileName;
    private String ipAddress;
    private int portNumber;
    private DatagramSocket datSock;
    private DatagramPacket datPac;

    public Sender(String fileName, String ipAddress, int portNumber){
        this.fileName = fileName;
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        runSender();
    }

    private void runSender () {
        try{
            initializeDatagramSocket();
            byte[] bytArr = String.valueOf(fileSender(fileName)).getBytes(StandardCharsets.UTF_8);
            InetAddress addr = InetAddress.getByName(ipAddress);
            initializeDatagramPacket(bytArr,addr);
            datSock.send(datPac);
        }catch(SocketException | UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void initializeDatagramSocket() throws SocketException {
        datSock = new DatagramSocket();
    }
    private void initializeDatagramPacket(byte[] arr, InetAddress add) {
        datPac = new DatagramPacket(arr, arr.length,add,portNumber);
    }

    private String fileSender (String fileLocation) {
        File file = new File(fileLocation);
        String toBeReturned = "";
        if(fileLocation.endsWith(".txt")) {
            try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);

                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                    toBeReturned += line;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            toBeReturned = fileLocation;
        }
        return toBeReturned;
    }
}
