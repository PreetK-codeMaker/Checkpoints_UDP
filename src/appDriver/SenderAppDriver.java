package appDriver;

import packet.Packet;
import sender.Sender;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;

public class SenderAppDriver { // The Sender. Client.
    public static void main(String[] args) throws Exception {
//        if(args.length < 1 && args.length > 5) {
//            throw new Exception("Invalid arguments");
//        }
//        if(args[0].equalsIgnoreCase("-f")) {
//            new Sender(args[1], args[2], Integer.parseInt(args[3]));
//        }else {
//            new Sender(endOfFileReader(), args[0], Integer.parseInt(args[1]));
//        }
        Packet nice = new Packet((byte)1,(byte)2,8, 8, 8, new byte[6]);
        byte[] b = nice.toBuf().array();
        //System.out.println(b.get(0));
        for(int i = 0; i < b.length; i++) {
            System.out.println(b[i]+" ---------- Index: " +i);
        }

    }

    private static String endOfFileReader() {
        String msg = "";
        String newLine = "\n";
        Scanner input = new Scanner(System.in);
        while(input.hasNext()) {
            msg += input.nextLine() + newLine;
        }
        return msg;

    }
}
