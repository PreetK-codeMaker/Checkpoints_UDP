package sender;

import packets.Packet;
import util.Utilities;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.Timer;

public class Sender {
    private String fileName;
    private String ipAddress;
    private int portNumber;
    private int windowSize;
    private int limitWindow;
    private int seqNumber;
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
        limitWindow = 3;
        loadPackets();
        selectiveRepeat();
    }


    private void selectiveRepeat() throws Exception {
        numOfTime = 0;
        timer = new Timer(true);
        windowSize = 0;
        while(true) {
            if(windowSize == 0) {
                windowSize = Math.min(pacQue.size(), limitWindow);
                window =new int [windowSize];
                Arrays.fill(window, NACK);
                for (int i = 0; i < windowSize; i++) {
                    Packet p = pacQue.poll();
                    winList.add(p);
                    sendFrame(p);

                }

            } else {
                int space = adjustWindow();
                int[] moveWin  = new int[windowSize];
                int setNewWindow = 0;
                for (int i = 0; i < space; i++) {
                    winList.remove(i);
                }
                for (int i = space; i < windowSize; i++) {
                    moveWin[setNewWindow] = window[i];
                    setNewWindow++;
                }

                while(space -- !=0 && !pacQue.isEmpty()) {
                    Packet p = pacQue.poll();

                   winList.add(winList.size(), p); // this might be a problem.
                }

                window = moveWin;
                windowSize = winList.size();
            }
            if(windowSize != 0 ) {
                byte[] ackData = new byte[529];
                DatagramPacket daPa = new DatagramPacket(ackData, ackData.length);
                datSock.receive(daPa);
                recivedAck(daPa.getData());
            }
        }
    }

    private void recivedAck(byte[] dap) {
        Packet p = Utilities.BufferToPacket(Utilities.byteArrToBuffer(dap));
        int seq = p.getSequenceNumber();
        for (int i = 0; i < windowSize; i++) {
            if(p.getType() == ACK) {
                window[i] = ACK;
            }
        }
    }
    private int adjustWindow() throws Exception {
        int windowMoved = 0;
        boolean retu = true;
        for (int i = 0; i < windowSize && retu; i++) {
            if(window[i] == ACK) {
                windowMoved++;
            }else {
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
//    private void initializeDatagramSocket() throws SocketException {
//        datSock = new DatagramSocket();
//    }
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
            pacList.add(packetInitialize(added, i));
            pacQue.add(pacList.get(i));

        }
       // to initialize the Queue.
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
