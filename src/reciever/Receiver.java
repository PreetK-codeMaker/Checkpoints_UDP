package reciever;

import packets.Packet;
import util.Utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Receiver {
    private String filename;
    private int portNumber;
    private DatagramSocket datSock;
    private DatagramSocket sendSocket;
    private DatagramPacket datPac;
    private int windowStart;
    public boolean blocked;
    private int windowList[];
    private final int ACK = 1;
    private final int NAK = 0;
    private int maxWindow;

    public Receiver(int portNumber) throws IOException {
        this.portNumber = portNumber;
        //slidingWindow();
//        runReceiver();
    }

    public Receiver(String fileName, int portNumber) throws IOException {
        this.filename = fileName;
        this.portNumber = portNumber;
        initializeDatagramSocket();
        slidingWindow();
//        runReceiver();
    }

    public Receiver(String fileName, int maxWindow, int portNumber) throws IOException {
        this.filename = fileName;
        byte[] bytArr = new byte[512];
        initializeDatagramPacket(bytArr);
        Packet p = Utilities.BufferToPacket(Utilities.byteArrToBuffer(datPac.getData()));
        this.maxWindow = p.getWindows();
        this.portNumber = portNumber;
        //slidingWindow();
    }

    public void runReceiver() {
        try {
            initializeDatagramSocket();
            byte[] bytArr = new byte[512];
            initializeDatagramPacket(bytArr);
            datSock.receive(datPac);
            Packet p = Utilities.BufferToPacket(Utilities.byteArrToBuffer(datPac.getData()));
            //String str = new String(p.getPayload(), StandardCharsets.UTF_8);
            slidingWindow();


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void slidingWindow() throws IOException {
        windowList = new int[31];
        Arrays.fill(windowList, NAK);
        byte[] received = new byte[512];
        DatagramPacket rp = new DatagramPacket(received, received.length);
        while (true) {
            blocked = false;
            datSock.receive(rp);
            System.out.println(rp.getPort());
            Packet p = Utilities.BufferToPacket(Utilities.byteArrToBuffer(rp.getData()));
            blocked = true;
            int seqNum = p.getSequenceNumber();
            boolean corrupted = isCorrupted(rp);
            if (!corrupted) {
                continue;
            } else {
                ackPacket(seqNum);
                sendAck(rp);
                windowAdjust(seqNum);
            }
            blocked = false;
        }
    }

    private void initializeDatagramSocket() throws SocketException {
        datSock = new DatagramSocket(portNumber);
        sendSocket = new DatagramSocket();
    }

    private void initializeDatagramPacket(byte[] arr) {
        datPac = new DatagramPacket(arr, arr.length);
    }

    private void fileMaker(String content) {
        try (FileWriter fw = new FileWriter(filename);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(content);
            bw.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static boolean isCorrupted(DatagramPacket dp) {
        Packet p = Utilities.BufferToPacket(Utilities.byteArrToBuffer(dp.getData()));
        int check = Utilities.checksum(Utilities.toByteArr(p));
        int receiveCheck = p.getChecksum();

        if (receiveCheck == check) {
            p.setType(2);
            return false;
        } else {
            p.setType(3);
            return true;
        }
    }

    private void ackPacket(int sequenceNum) {
        if (windowStart <= sequenceNum) {
            if (sequenceNum - windowStart < maxWindow) {
                windowList[sequenceNum - windowStart] = ACK;
            }
        }
    }

    private String sendAck(DatagramPacket dp) throws IOException {
        Packet p = Utilities.BufferToPacket(Utilities.byteArrToBuffer(dp.getData()));
        String ackMessage = ("Sequence Number: " + p.getSequenceNumber());
        byte[] ackData = new byte[ackMessage.length()];
        ackData = ackMessage.getBytes();
        DatagramPacket ap = new DatagramPacket(ackData, ackData.length, InetAddress.getByName("localhost"), portNumber+2);
        datSock = null;
        datSock = new DatagramSocket(portNumber+2);
        System.out.println(ap.getPort());
        datSock.send(ap);
        return ackMessage;
    }

    private void windowAdjust(int sequenceNum) {
        while (true) {
            if(windowList[0] == ACK) {
                for(int i = 0; i < maxWindow; i++) {
                    windowList[i] = windowList[i + 1];
                }
                windowList[maxWindow - 1] = NAK;
                windowStart++;
            }
            else {
                break;
            }
        }
    }

}


