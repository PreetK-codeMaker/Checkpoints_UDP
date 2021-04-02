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
