package sender;

import packets.Packet;
import util.Utilities;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Sender {
    private String fileName;
    private String ipAddress;
    private int portNumber;
    private int windowSize = 5;
    private int limitWindow;
    private int seqNumber;
    private int baseWindow;
    private int receiverPortNumber;
    private static InetAddress receiverIP;
    private static final int ACK = 2;
    private static final int NACK = 3;
    private static final int PAYLOAD_SIZE = 512;
    private static final int HEADER_SIZE = 14;
    private static int numOfTime;
    private Timer timer;
    private int timeOut;
    private int window[];
    private DatagramSocket datSock;
    private DatagramPacket datPac;
    private Queue<Packet> pacQue;
    private ArrayList<Packet> winList;
    private ArrayList<Packet> pacList;

    public Sender(String fileName, String ipAddress, int portNumber) throws Exception {
        this.fileName = fileName;
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        pacList = new ArrayList<Packet>();
        pacQue = new LinkedList<>();
        winList = new ArrayList<>();
        initializeDatagramSocket();
        limitWindow = 3;
        loadPackets();
        initilizePacket();
    }

    private void initilizePacket() throws Exception {
        byte[] reciveeData = new byte[530];
        DatagramPacket recPAcket = new DatagramPacket(reciveeData, reciveeData.length);
        baseWindow = 0;
        window = new int[windowSize];
        Arrays.fill(window, NACK);
        for (int i = 0; i < windowSize; i++) {
            if ((i < pacList.size())) {
               sendFrame(pacList.get(i));
            }
        }
        while(true) {
            byte []ackData = new byte[530];
            DatagramPacket getAck = new DatagramPacket(ackData, ackData.length);
            datSock.receive(getAck);
//            receiverPortNumber = getAck.getPort();
//            receiverIP = getAck.getAddress();
            recivedAck(getAck.getData());
            int windowMove = adjustWindow();
            for (int i = windowMove; i > 0 ; i--) {
                sendFrame(pacList.get(baseWindow+windowSize -i));
            }
            if(allPackAck()) {
                Packet p = packetToStop(1);
                byte[] arr= Utilities.packetToBuffer(p).array();
                DatagramPacket pac = new DatagramPacket(arr, arr.length);
                datSock.send(pac);
                break;
            }

        }

    }

    private boolean allPackAck() {
        boolean allGood = true;
        for(int i= 0; i <window.length & allGood; i++) {
            if(window[i] == NACK) {
                allGood = false;
            }
        }
        return allGood;
    }

//
//    private void selectiveRepeat() throws Exception {
//        numOfTime = 0;
//        timer = new Timer(true);
//        windowSize = 0;
//        while(true) {
//            if(pacQue.isEmpty()) {
//                break;
//            }
//            if(windowSize == 0) {
//                windowSize = Math.min(pacQue.size(), limitWindow);
//                window =new int [windowSize];
//                Arrays.fill(window, NACK);
//                for (int i = 0; i < windowSize; i++) {
//                    Packet p = pacQue.poll();
//                    winList.add(p);
//                    sendFrame(p);
//
//                }
//
//            } else {
//                int space = adjustWindow();
//                int[] moveWin  = new int[windowSize];
//                int setNewWindow = 0;
//                for (int i = 0; i < space; i++) {
//                    winList.remove(i);
//                }
//                for (int i = space; i < windowSize; i++) {
//                    moveWin[setNewWindow] = window[i];
//                    setNewWindow++;
//                }
//
//                while(space -- !=0 && !pacQue.isEmpty()) {
//                    Packet p = pacQue.poll();
//
//                   winList.add(winList.size(), p); // this might be a problem.
//                }
//
//                window = moveWin;
//                windowSize = winList.size();
//            }
//            if(windowSize != 0 ) {
//                byte[] ackData = new byte[529];
//                DatagramPacket daPa = new DatagramPacket(ackData, ackData.length);
//                datSock.receive(daPa);
//                System.out.println("Helo");
//                recivedAck(daPa.getData());
//            }
//        }
//    }

    private void recivedAck(byte[] dap) {
        Packet p = Utilities.BufferToPacket(Utilities.byteArrToBuffer(dap));
        int seq = p.getSequenceNumber();
        for (int i = 0; i < seq + windowSize; i++) {
            if(p.getType() == ACK) {
                window[i] = ACK;
            }
        }
    }
    private int adjustWindow() throws Exception {
        int windowMoved = 0;
        boolean retu = true;
        while(retu) {
            if(window[baseWindow] == ACK) {
                if(baseWindow + windowSize < window.length) {
                    baseWindow++;
                    windowMoved++;
                }else {
                    retu = !retu;
                }
            } else {
                retu = false;
            }
        }
        return windowMoved;

    }

//    private void runSender () {
//        try{
//            initializeDatagramSocket();
//            InetAddress addr = InetAddress.getByName(ipAddress);
//            for (int i = 0; i < pacList.size(); i++) {
////                initializeDatagramPacket(Utilities.packetToBuffer(pacList.get(2)).array(),addr);
//                datSock.send(datPac);
//            }
//
//        }catch(SocketException | UnknownHostException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    private void initializeDatagramSocket() throws SocketException {
        datSock = new DatagramSocket(12346);
    }
//    private void initializeDatagramPacket(byte[] arr, InetAddress add) {
//        datPac = new DatagramPacket(arr, arr.length,add,portNumber);
//    }
//
//
    private void sendFrame(Packet p) throws IOException {
     byte[] arr = Utilities.packetToBuffer(p).array();
    DatagramPacket pac = new DatagramPacket(arr, arr.length, InetAddress.getByName(ipAddress), portNumber);
    datSock.send(pac);
    }
    private void loadPackets() {
        byte [][] dividedPayload = Utilities.payloadDivider(Utilities.fileSender(fileName));
        for (int i = 0; i < dividedPayload.length; i++) {
            byte [] added = new byte[dividedPayload[i].length];
            for (int j = 0; j < dividedPayload[i].length; j++) {
                added[j] = dividedPayload[i][j];
            }
            pacList.add(packetInitialize(added, i, dividedPayload.length));
            pacQue.add(pacList.get(i));

        }
    }

    private Packet packetInitialize(byte[] payload, int i, int length) {
        Packet pac = new Packet.PacketBuilder()
                .setType(2) //1
                .setTr(0)   //2
                .setWindows(31) //3
                .setSequenceNumber((i %256))
                .setLength(length)
                .setTimestamp()
                .setCheckSum(0)
                .setPayload(payload)
                .createPack();
        pac.setChecksum(Utilities.toByteArr(pac));
        return pac;
    }

    private Packet packetToStop(int toStop) {
        Packet pac = new Packet.PacketBuilder()
                .setType(1) //1
                .setTr(0)   //2
                .setWindows(0) //3
                .setSequenceNumber((255))
                .setLength(0)
                .setTimestamp()
                .setCheckSum(0)
                .setPayload(null)
                .createPack();
        pac.setChecksum(Utilities.toByteArr(pac));
        return pac;
    }

}
