package appDriver;

import packets.Packet;
import sender.Sender;
import util.Utilities;

import java.io.BufferedReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;
import java.util.zip.CRC32;

public class SenderAppDriver { // The Sender. Client.
    public static void main(String[] args) throws Exception {
        if(args.length < 1 && args.length > 5) {
            throw new Exception("Invalid arguments");
        }
        if(args[0].equalsIgnoreCase("-f")) {
            new Sender(args[1], args[2], Integer.parseInt(args[3]));
        }else {
            new Sender(endOfFileReader(), args[0], Integer.parseInt(args[1]));
        }
//
//
//
////
//        Packet pa = Utilities.BufferToPacket(bs);

//        System.out.println(pa.getType()+ "   "+ pa.getTr()+"  "+pa.getWindows()+" "+pa.getSequenceNumber()+ " "+pa.getLength()+ " "
//        +" "+ pa.getTimestamp()+ " "+ByteBuffer.wrap(pac.getChecksum()).getInt()+" "+new String(pa.getPayload(), StandardCharsets.UTF_8));
////        System.out.println(pac.getChecksum()+ "Checking for check sum");
//        System.out.println(bs.arrayOffset() +"Before shit coming out ------" );
//        System.out.println(bs.getInt()) ;//8
//        System.out.println(bs.getInt() );//12
//        System.out.println(bs.remaining()+ "-----------Remaining");
//        System.out.println(bs.getInt());// 16
//        System.out.println(bs.getInt() );// 20
//        System.out.println(bs.getInt() );// 24
//        System.out.println(bs.getInt() );// 24
////        int test  = (int) ByteBuffer.wrap(Utilities.intToByte(bs.getInt())).getLong();
//        byte[] daya = ByteBuffer.allocate(4).putInt(bs.getInt()).array();
//        System.out.println( ByteBuffer.wrap(pac.getPayload()).getInt() +"    "+ByteBuffer.wrap(daya).getInt());
//
//        byte [] a = new byte[bs.remaining()];
//        bs.get(a);
//        String ss = new String(bs.get(a),StandardCharsets.UTF_8);
//        System.out.println(ss);




    }

    private static String endOfFileReader() {
        String msg = "";
        Scanner input = new Scanner(System.in);
        while(input.hasNext()) {
            msg += input.nextLine();
        }
        return msg;

    }
}
