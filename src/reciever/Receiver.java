package reciever;

import packets.Packet;
import util.Utilities;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Receiver {
    private  int report;
    private String filename;
    private int portNumber;
    private static DatagramSocket datSock;
    private static DatagramPacket datPac;
    private int windowStart;
    public boolean blocked;
    private int windowSize = 5;
    private int baseWindow;
    private int window[];
    private final int ACK = 2;
    private final int NAK = 3;
    private int totalFrameNUmber ;
    ArrayList<Packet> toReceive = new ArrayList<>();
//
    public Receiver(int portNumber) throws IOException {
        this.portNumber = portNumber;
        slidingWindow();
//        runReceiver();
    }

    public Receiver(String fileName, int portNumber) throws IOException {
        this.filename = fileName;
        this.portNumber = portNumber;
        System.out.println(portNumber);
        initializeDatagramSocket();
        slidingWindow();
    }

    public void slidingWindow() throws IOException {
        byte[] received = new byte[530];
        DatagramPacket rp = new DatagramPacket(received, received.length);

        baseWindow = 0;
        window = new int[windowSize];
        Arrays.fill(window, NAK);
        boolean run = true;
        int timesToRUn =0;
        int compareto = 1;
        while (timesToRUn < compareto) {
            datSock.receive(rp);
            report = rp.getPort();
            Packet p = Utilities.BufferToPacket(Utilities.byteArrToBuffer(rp.getData()));

            if ((p.getLength() != compareto)) {
                compareto = p.getLength();
            }
            if(p.getType() == 1) {
                run = false;
            }
           totalFrameNUmber += p.getSequenceNumber();
            int seqNum = p.getSequenceNumber();
            boolean corrupted = isCorrupted(p);
            if (!corrupted) {
                continue;
            } else {
                ackPacket(seqNum);
                sendAck(rp);
                windowAdjust(seqNum);
                toReceive.add(p);
            }
            timesToRUn++;
        }
        printPayload();
    }

    private void printPayload() throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        for (int i = 0; i < toReceive.size(); i++) {
            byteStream.write(toReceive.get(i).getPayload());
        }
        byte[] concatenatedString = byteStream.toByteArray();
        String outPut = new String(concatenatedString, StandardCharsets.UTF_8);
        System.out.println("Packets That were send and Ack:+---------------------");
        System.out.println("Packets That were send and Ack:+"+ "The Below is the output of the file.");
        System.out.println(outPut);
        System.out.println();
    }

    private void initializeDatagramSocket() throws SocketException {
        datSock = new DatagramSocket(12345);
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

    private static boolean isCorrupted(Packet p) {
        int check = Utilities.checksum(Utilities.toByteArr(p));
        int receiveCheck = p.getChecksum();

        if (receiveCheck == check) {
            p.setType(2);
            return true;
        } else {
            p.setType(3);
            return false;
        }
    }

    private void ackPacket(int sequenceNum) {
        if (windowStart <= sequenceNum) {
            if (sequenceNum - windowStart < windowSize) {
                window[sequenceNum - baseWindow] = ACK;
            }
        }
    }

    private String sendAck(DatagramPacket dp) throws IOException {
        Packet p = Utilities.BufferToPacket(Utilities.byteArrToBuffer(dp.getData()));
        String ackMessage = ("Sequence Number: " + p.getSequenceNumber());
        byte[] ackData = new byte[ackMessage.length()];
        ackData = ackMessage.getBytes();
        DatagramPacket ap = new DatagramPacket(ackData, ackData.length, InetAddress.getByName("localhost"),report);
        System.out.println("Packet Acknowledged: "+ p.getSequenceNumber());
        datSock.send(ap);
        return ackMessage;
    }

    private void windowAdjust(int sequenceNum) {
        while (true) {
            if(window[0] == ACK) {
                for(int i = 0; i < windowSize-1; i++) {
                    window[i] = window[i + 1];
                }
                window[windowSize - 1] = NAK;
                baseWindow++;
            }
            else {
                break;
            }
        }
    }

}


