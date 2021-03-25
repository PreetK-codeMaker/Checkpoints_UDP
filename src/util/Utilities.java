package util;

import com.sun.beans.editors.ByteEditor;
import packets.Packet;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class Utilities {
    public String fileSender(String fileLocation) {
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

    public static byte[] checksum(byte[] payload){
       CRC32 check = new CRC32();
        check.update(payload, 0 , payload.length);
        byte[] checkBytes = ByteBuffer.allocate(8).putLong(check.getValue()).array();
        // long fromBytes = ByteBuffer.wrap(checkBytes).getLong();
        // System.out.println(fromBytes+"--------");

        return checkBytes;
    }
}
