package reciever;

import packets.Packet;
import util.Utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Receiver {
    private String filename;
    private int portNumber;
    private static DatagramSocket datSock;
    private static DatagramPacket datPac;

    public Receiver(int portNumber) {
        this.portNumber = portNumber;
        runReceiver();
    }

    public Receiver(String fileName, int portNumber)  {
        this.filename = fileName;
        this.portNumber = portNumber;
        runReceiver();
    }
    public void runReceiver()  {
        try {
            initializeDatagramSocket();
            byte[] bytArr = new byte[512];
            initializeDatagramPacket(bytArr);
            datSock.receive(datPac);
            Packet p = Utilities.BufferToPacket(Utilities.byteArrToBuffer(datPac.getData()));
            String str = new String(p.getPayload(), StandardCharsets.UTF_8);
//            long check = Utilities.checksum(Utilities.toByteArr(p));
//            long receiveCheck = p.getChecksum();
//            System.out.println("Received Checksum: "+receiveCheck + " Checksum: " + check);
//
//            if (receiveCheck == check) {
//                System.out.println("Received Checksum: "+receiveCheck + " Checksum: " + check);
//            }
            if(filename != null) {
                fileMaker(str);
            }


        }catch(SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void slidingWindow(InetAddress addr) throws IOException {
        int totalPackets = 0;
        int corruptedPackets = 0;

        //initializeDatagramPacket(Utilities.packetToBuffer(pacList.get(0)).array(),addr);
        DatagramPacket receiveData = new DatagramPacket(datPac.getData(), datPac.getLength());

        while(true) {
            // datSock.receive(receiveData);
            totalPackets++;

            if(receiveData == null) {
                break;
            }

            int corrupted = isCorrupted(receiveData);

            if(corrupted == 2) {
                //System.out.println();

//                DatagramPacket ack = new DatagramPacket(datPac.getData(), datPac.getLength(), addr, portNumber);
//                datSock.send(ack);
                System.out.println("I worked!");
            }
            else if(corrupted == 3) {
                System.out.println("Corrupted packet found");
                corruptedPackets++;
            }
        }
    }

    private void initializeDatagramSocket() throws SocketException {
        datSock = new DatagramSocket(portNumber);
    }
    private void initializeDatagramPacket(byte[] arr) {
        datPac = new DatagramPacket(arr, arr.length);
    }
    private void fileMaker(String content) {
        try(FileWriter fw = new FileWriter(filename);
            BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(content);
            bw.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static int isCorrupted(DatagramPacket dp) {
        Packet p = Utilities.BufferToPacket(Utilities.byteArrToBuffer(dp.getData()));
        int check = Utilities.checksum(Utilities.toByteArr(p));
        int receiveCheck = p.getChecksum();

        if(receiveCheck == check) {
            p.setType(2);
            return p.getType();
        }
        else {
            p.setType(3);
            return p.getType();
        }
    }

}
