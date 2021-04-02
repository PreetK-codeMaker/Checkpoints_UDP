package packets;

import util.Utilities;

import java.io.Serializable;

public class Packet implements Serializable {
    private int type;
    private int tr;
    private int windows;
    private int sequenceNumber;
    private int length;
    private int timestamp;
    private long checksum;
    private byte[] payload;
    private final int HEADER = 40;
    private final int PAYLOAD_AND_HEADER =  512 + HEADER;

    public Packet() {
    }

    public Packet(int type, int tr, int windows, int sequenceNumber, int length, int timestamp, long checksum, byte[] payload) {
        this.type =  type;
        this.tr = tr;
        this.windows =  windows;
        this.sequenceNumber = sequenceNumber;
        this.length =  length;
        this.timestamp = timestamp;
        this.checksum = checksum;
        this.payload = payload;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) { this.type = type; }

    public int getTr() {
        return tr;
    }

    public int getWindows() {
        return windows;
    }

    public void setWindows(int windows) { this.windows = windows; }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) { this.sequenceNumber = sequenceNumber; }

    public int getLength() {
        return length;
    }

    public void setLength(int length) { this.length = length; }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) { this.timestamp = timestamp; }

    public long getChecksum() {
        return checksum;
    }

    public long setChecksum(byte[] checksum) { this.checksum = Utilities.checksum(checksum);
        return this.checksum;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) { this.payload = payload; }

    public static class PacketBuilder {
        private int type;
        private int tr;
        private int windows;
        private int sequenceNumber;
        private int length;
        private int timestamp;
        private long checkSumValue;
        private byte[] payload;
        private final int HEADER = 32;
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
            this.sequenceNumber = (int) Integer.toUnsignedLong(sequenceNumber);
            return this;
        }

        public PacketBuilder setLength(int length) {
            this.length = (int) Integer.toUnsignedLong(length);
            return this;
        }

        public PacketBuilder setTimestamp() {
            this.timestamp = (int) Integer.toUnsignedLong((int) (System.currentTimeMillis()/1000));
            return this;
        }

        public PacketBuilder setTimestamp(int timestamp) {
            this.timestamp = (int) Integer.toUnsignedLong(timestamp);
            return this;
        }

        public PacketBuilder setCheckSum(byte[] payload) {
            this.checkSumValue =Utilities.checksum(payload);
            return this;
        }

        public PacketBuilder setCheckSum(long checkSum) {
            this.checkSumValue = checkSum;
            return this;
        }

        public PacketBuilder setPayload(byte[] payload) {
            this.payload = payload;
            return this;
        }

        public Packet createPack() {
            return new Packet(type, tr, windows, sequenceNumber, length, timestamp, checkSumValue, payload);
        }

    }
}