package util;

import com.sun.beans.editors.ByteEditor;
import packets.Packet;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
    public byte[] toByteArr(Packet p){
        byte[] tooPac = {(byte)p.getType(), (byte)p.getTr(), (byte)p.getWindows(), (byte)p.getSequenceNumber(), (byte)p.getLength()};
        return tooPac;
    }

    public static long checksum(byte[] payload){
       CRC32 check = new CRC32();
        check.update(payload, 0 , payload.length);

      long checkBytes = check.getValue();
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
        ByteBuffer bs = ByteBuffer.allocate(516).order(ByteOrder.BIG_ENDIAN);

        bs.put((byte) pac.getType());
        bs.put((byte) pac.getTr());
        bs.put((byte) pac.getWindows());
        bs.putInt(pac.getSequenceNumber());
        bs.putInt(pac.getLength());
        bs.putInt(pac.getTimestamp());
        bs.putLong(pac.getChecksum());
        bs.put(pac.getPayload());

        bs.flip();

        return bs;
    }

    private static byte[] payLoadToByteArr(ByteBuffer r) {
        byte[] arr = new byte[r.remaining()];
        r.get(arr);
        return arr;
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
}
