package reciever;

import packets.Packet;
import util.Utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public class Receiver {
    private String filename;
    private int portNumber;
    private DatagramSocket datSock;
    private DatagramPacket datPac;

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
            byte[] bytArr = new byte[1024];
            initializeDatagramPacket(bytArr);
            datSock.receive(datPac);
            Packet p = Utilities.BufferToPacket(Utilities.byteArrToBuffer(datPac.getData()));
            long check = Utilities.checksum(p.getPayload());
            long receiveCheck = p.getChecksum();
            System.out.println("Received Checksum: "+receiveCheck + " Checksum: " + check);

            if (receiveCheck == check) {
                System.out.println("Received Checksum: "+receiveCheck + " Checksum: " + check);
            }
            String str = new String(p.getPayload(), StandardCharsets.UTF_8);
            System.out.println(str);
            if(filename != null) {
                fileMaker(str);
            }


        }catch(SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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

}
