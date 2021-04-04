package util;

import com.sun.beans.editors.ByteEditor;
import packets.Packet;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;

public  class Utilities {
    public static String fileSender(String fileLocation) {
        File file = new File(fileLocation);
        String toBeReturned = "";
        if(fileLocation.endsWith(".txt")) {
            try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);

                String line;
                while ((line = br.readLine()) != null) {
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
    public static byte [] toByteArr(Packet p){
        ByteBuffer buff = ByteBuffer.allocate(14).order(ByteOrder.BIG_ENDIAN);
        buff.put((byte) p.getType());
        buff.put((byte) p.getTr());
        buff.put((byte) p.getWindows());
        buff.putInt( p.getSequenceNumber());
        buff.putInt( p.getLength());
        buff.flip();

        return buff.array();
    }

    public static int checksum(byte[] payload) {
       CRC32 check = new CRC32();
        check.update(payload);

        int checkBytes = (int) check.getValue();
        return checkBytes &  0x7FFFFFFF;
    }

    public static ByteBuffer byteArrToBuffer (byte[] arr) {
        return ByteBuffer.wrap(arr);
    }

    public static Packet BufferToPacket(ByteBuffer buff) {

        Packet pac = new Packet.PacketBuilder().
                                setType(buff.get()) //4
                                .setTr(buff.get()) //8
                                .setWindows(buff.get()) //12
                                .setSequenceNumber(buff.getInt()) // 20
                                .setLength(buff.getInt()) // 16
                                .setTimestamp(buff.getInt()) // 24
                                .setCheckSum(buff.getInt())
                                .setPayload(payLoadToByteArr(buff))
                                .createPack();// 28


        return pac;
    }

    public static ByteBuffer packetToBuffer (Packet pac) {

        ByteBuffer bs = ByteBuffer.allocate(560).order(ByteOrder.BIG_ENDIAN);

        bs.put((byte) pac.getType());
        bs.put((byte) pac.getTr());
        bs.put((byte) pac.getWindows());
        bs.putInt(pac.getSequenceNumber());
        bs.putInt(pac.getLength());
        bs.putInt(pac.getTimestamp());
        bs.putInt(pac.getChecksum());
        bs.put(pac.getPayload());
        bs.flip();

        return bs;
    }
    private static byte[] payLoadToByteArr(ByteBuffer r) {
        byte[] arr = new byte[r.remaining()];
        r.get(arr);
        return arr;
    }

    public static byte[][] payloadDivider(String payload) {
        System.out.println(payload.length());
        int spotNeeded = (int) Math.ceil((double) payload.length()/512 );
        byte[][] payDiv = new byte[spotNeeded][];

        if(spotNeeded == 1) {
            payDiv[0] = payload.getBytes(StandardCharsets.UTF_8);
        } else {
            String[] dividedPayload = equalStringSplit(payload, 512);
            for (int i = 0; i < spotNeeded; i++) {
                payDiv[i] = dividedPayload[i].getBytes(StandardCharsets.UTF_8);
            }
        }
        return payDiv;
    }


//    private static String[] equalStringSplit(String payload, int subSize) {
//        int position = 0;
//        int offset = (payload.length() / subSize);
//        String [] subString = new String[subSize];
//        for (int i = 0; i < offset * subSize; i = i + offset) {
//            subString[position ++] = payload.substring(i, Math.min(i + offset, payload.length()));
//        }
//        return  subString;
//    }
    private static String[] equalStringSplit(String text, int size) {
        List<String> parts = new ArrayList<>();

        int length = text.length();
        for (int i = 0; i < length; i += size) {
            parts.add(text.substring(i, Math.min(length, i + size)));
        }
        return parts.toArray(new String[0]);
    }
}

