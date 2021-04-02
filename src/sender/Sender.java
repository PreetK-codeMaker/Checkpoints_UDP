package sender;

import packets.Packet;
import util.Utilities;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

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
        pacList = new ArrayList<Packet>();
        loadPackets();
        runSender();
    }

    private void runSender () {
        try{
            initializeDatagramSocket();
            InetAddress addr = InetAddress.getByName(ipAddress);
            for (int i = 0; i < pacList.size(); i++) {
                initializeDatagramPacket(Utilities.packetToBuffer(pacList.get(0)).array(),addr);
                datSock.send(datPac);
            }

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


    private void loadPackets() {
        byte [][] dividedPayload = Utilities.payloadDivider(Utilities.fileSender(fileName));
        for (int i = 0; i < dividedPayload.length; i++) {
            byte [] added = new byte[dividedPayload[i].length];
            for (int j = 0; j < dividedPayload[i].length; j++) {
                added[j] = dividedPayload[i][j];
            }
            pacList.add(packetInitialize(added, i));
        }

    }

    private Packet packetInitialize(byte[] payload, int i) {
        Packet pac = new Packet.PacketBuilder()
                .setType(1) //1
                .setTr(0)   //2
                .setWindows(31) //3
                .setSequenceNumber((i %256))
                .setLength(payload.length)
                .setTimestamp()
                .setCheckSum(0)
                .setPayload(payload)
                .createPack();
        pac.setChecksum(Utilities.toByteArr(pac));
        return pac;
    }

}
