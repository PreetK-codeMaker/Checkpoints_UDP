package packet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.zip.CRC32;

public class Packet {
    private static final int MIN_LENGTH = 5;
    private static final int MAX_LENGTH = 512 + 5;

    private byte type;
    private byte tr;
    private int window;
    private int sequenceNumber;
    private int length;
    private byte[] payload;
//


    public Packet(byte type, byte tr, int window, int sequenceNumber, int length, byte[] payload) {
        this.type = type;
        this.tr = tr;
        this.window = window;
        this.sequenceNumber = sequenceNumber;
        this.length = length;
        this.payload = payload;
    }
//
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

    public int getWindow() {
        return window;
    }

    public void setWindow(int window) {
        this.window = window;
    }
//
    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public static byte[] checksum(byte[] payload) {
        CRC32 checksum = new CRC32();
        checksum.update(payload);
        byte[] checkBytes = ByteBuffer.allocate(8).putLong(checksum.getValue()).array();
        return checkBytes;
    }

    private void write(ByteBuffer buffer) {
        Date date= new Date();
        buffer.put(type);
        buffer.put(tr);
        buffer.putInt(window);
        buffer.putInt(sequenceNumber);
        buffer.putInt(length);
        buffer.putLong(date.getTime());
        buffer.put(checksum(payload));
        buffer.put(payload);
    }

    public ByteBuffer toBuf() {
        ByteBuffer buffer = ByteBuffer.allocate(69).order(ByteOrder.BIG_ENDIAN);
        write(buffer);
        return buffer;

    }

}
