package util;

import com.sun.beans.editors.ByteEditor;
import packets.Packet;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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
    public static int recieverChecksum(byte[] payload){
        ArrayList<Byte> lol = new ArrayList<Byte>();
        for (int i = 0; i < payload.length; i++) {
            if(payload[i] != 0) {
                lol.add(payload[i]);
            }
        }
        CRC32 check = new CRC32();
        byte [] arr = new byte[lol.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = lol.get(i);
        }
        check.update(arr);

        int checkBytes = (int) check.getValue();
        return checkBytes;
    }
    public static int checksum(byte[] payload) {
       CRC32 check = new CRC32();
        check.update(payload);

        int checkBytes = (int) check.getValue();
        return checkBytes;
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
//        byte[] br = {buff.get(), buff.get(), buff.get()};
        ByteBuffer bs = ByteBuffer.allocate(516).order(ByteOrder.BIG_ENDIAN);

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

    public static byte[][] payloadDivider(String payload) {
        int spotNeeded = (int) Math.ceil((double) payload.length()/512 );
        byte[][] payDiv = new byte[spotNeeded][];

        if(spotNeeded == 1) {
            payDiv[0] = payload.getBytes(StandardCharsets.UTF_8);
        } else {
            String[] dividedPayload = equalStringSplit(payload, spotNeeded);
            for (int i = 0; i < spotNeeded; i++) {
                payDiv[i] = dividedPayload[i].getBytes(StandardCharsets.UTF_8);
            }
        }
        return payDiv;
    }

    public static byte[] intToByte (final int i) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            dos.writeInt(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    private static byte[] payLoadToByteArr(ByteBuffer r) {
        byte[] arr = new byte[r.remaining()];
        r.get(arr);
        return arr;
    }

    private static String[] equalStringSplit(String payload, int subSize) {
        int position = 0;
        int offset = (payload.length() / subSize);
        String [] subString = new String[subSize];
        for (int i = 0; i < offset * subSize; i = i + offset) {
            subString[position ++] = payload.substring(i, Math.min(i + offset, payload.length()));
//            subString[position ++] = payload.substring(i,  i+offset);
        }
        return  subString;
    }
}
