package packets;

import com.sun.istack.internal.Nullable;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.zip.CRC32;

public class Packet {
    private int type;
    private int tr;
    private int windows;
    private int sequenceNumber;
    private int length;
    private byte[] payload;
    private final int HEADER = 40;
    private final int PAYLOAD_AND_HEADER =  512 + HEADER;

    public Packet() {
    }

    public Packet(int type, int tr, int windows, int sequenceNumber, int length, byte[] payload) {
        this.type =  type;
        this.tr = tr;
        this.windows =  windows;
        this.sequenceNumber = sequenceNumber;
        this.length =  length;
        this.payload = payload;
    }

    public int getType() {
        return type;
    }

    public int getTr() {
        return tr;
    }

    public int getWindows() {
        return windows;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int getLength() {
        return length;
    }

    public byte[] getPayload() {
        return payload;
    }

    public static byte[] checksum(byte[] payload){
        CRC32 check = new CRC32();
        check.update(payload);
        String crcNumber = check.getValue()+"";
        byte[] checkBytes = ByteBuffer.allocate(4).putLong(check.getValue()).array();
//        byte [] checkBytes = crcNumber.getBytes(StandardCharsets.UTF_8);
        return checkBytes;
    }

    public static class PacketBuilder {
        private int type;
        private int tr;
        private int windows;
        private int sequenceNumber;
        private int length;
        private byte[] payload;
        private final int HEADER = 40;
        private final int PAYLOAD_AND_HEADER =  512 + HEADER;


        public PacketBuilder setType(int type) {
            this.type = (int) Integer.toUnsignedLong(type);
            return this;
        }

        public PacketBuilder setTr(int tr) {
            this.tr = (byte) Integer.toUnsignedLong(tr);
            return this;
        }

        public PacketBuilder setWindows(int windows) {
            this.windows = (byte) Integer.toUnsignedLong(windows);
            return this;
        }


        public PacketBuilder setSequenceNumber(int sequenceNumber) {
            this.sequenceNumber = (byte) Integer.toUnsignedLong(sequenceNumber);
            return this;
        }

        public PacketBuilder setLength(int length) {
            this.length = (byte)Integer.toUnsignedLong(length);
            return this;
        }


        public PacketBuilder setPayload(byte[] payload) {
            this.payload = payload;
            return this;
        }

        public Packet createPack() {
            return new Packet(type, tr, windows, sequenceNumber, length, payload);
        }

    }

    public static void main(String[] args) {
        Packet pa = new Packet.PacketBuilder().setType(0x2002).setTr(0).setWindows(12).setSequenceNumber(1).setLength(22)
                .setPayload(null).createPack();
        ArrayList<Packet> na = new ArrayList<>();
        na.add(pa);
        for (int i = 0; i < na.size(); i++) {
            System.out.println( na.get(i).getType() );

        }
    }
}