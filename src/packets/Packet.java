package packets;

import com.sun.istack.internal.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.zip.CRC32;

public class Packet {
    private byte type;
    private byte tr;
    private byte windows;
    private byte sequenceNumber;
    private byte length;
    private byte[] payload;
    private final byte HEADER = (byte)36;
    private final byte PAYLOAD_AND_HEADER = (byte) 512 + HEADER;

    public Packet() {
    }

    public Packet(int type, int tr, int windows, int sequenceNumber, int length, byte[] payload) {
        this.type = (byte) type;
        this.tr = (byte) tr;
        this.windows = (byte) windows;
        this.sequenceNumber = (byte) sequenceNumber;
        this.length = (byte) length;
        this.payload = payload;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getTr() {
        return tr;
    }

    public void setTr(byte tr) {
        this.tr = tr;
    }

    public byte getWindows() {
        return windows;
    }

    public void setWindows(byte windows) {
        this.windows = windows;
    }

    public byte getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(byte sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public byte getLength() {
        return length;
    }

    public void setLength(byte length) {
        this.length = length;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public static byte[] checksum(byte[] payload){
        CRC32 check = new CRC32();
        check.update(payload);
        String crcNumber = check.getValue()+"";
        byte[] checkBytes = crcNumber.getBytes(StandardCharsets.UTF_8);
        return checkBytes;
    }
    public ByteBuffer tBu() {
        ByteBuffer buffer = ByteBuffer.allocate(PAYLOAD_AND_HEADER).order(ByteOrder.BIG_ENDIAN);
        write(buffer);
        buffer.flip();
        return buffer;
    }

    private void write (ByteBuffer buf) {
        Date date = new Date();
        buf.put(type);
        buf.put(tr);
        buf.put(windows);
        buf.put(sequenceNumber);
        buf.put(length);
        buf.putLong(date.getTime());
        buf.put(checksum(payload))   ;
        buf.put(payload);
    }
    public static void main(String[] args) {
        byte bu[] = {0,1,0,1};
        Packet pa = new Packet(1,0,16,1,56,bu);
     ByteBuffer ra = pa.tBu();
        for (int i = 0; i < ra.limit() ; i++) {

            System.out.println(ra.get(i) + "----------------"+ i);
        }
    }
}