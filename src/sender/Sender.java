package sender;

import packets.Packet;
import util.Utilities;

import javax.rmi.CORBA.Util;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.CRC32;

public class Sender {
    private String fileName;
    private String ipAddress;
    private int portNumber;
    private static final int PAYLOAD_SIZE = 512;
    private static final int HEADER_SIZE = 14;
    private DatagramSocket datSock;
    private DatagramPacket datPac;
    private ArrayList<Packet> pacList;

    public Sender(String fileName, String ipAddress, int portNumber){
        this.fileName = fileName;
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
//        Utilities.payloadDivider(Utilities.fileSender(fileName));
        initializePackets();
        runSender();
    }

    private void initializePackets() {

    }

    private void runSender () {
        try{
            initializeDatagramSocket();
            byte[] bytArr = Utilities.packetToBuffer(packetInitialize()).array();
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

    private Packet packetInitialize() {
        Packet pac = new Packet.PacketBuilder()
                .setType(1) //1
                .setTr(1)   //2
                .setWindows(31) //3
                .setSequenceNumber(255)
                .setLength(512)
                .setTimestamp()
                .setCheckSum(0)
                .setPayload(Utilities.fileSender(fileName).getBytes(StandardCharsets.UTF_8))
                .createPack();
        pac.setChecksum(Utilities.toByteArr(pac));
        return pac;
    }

}
