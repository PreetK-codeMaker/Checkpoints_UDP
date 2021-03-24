package test;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import packets.Packet;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class packetTest {
	private static ArrayList<Packet> arrPac;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Packet pac = new Packet.PacketBuilder()
							.setType(0x02)
							.setTr(0x0)
							.setWindows(0x14)
							.setSequenceNumber(0x4E)
							.setLength(0x118)
							.setPayload((fileSender("res/yo.txt").getBytes(StandardCharsets.UTF_8)))
							.createPack();
		arrPac = new ArrayList<>();
		arrPac.add(pac);

	}
	private static String fileSender(String fileLocation) {
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
	@Test
	public void testPayload() {
		String name = null;
		for(Packet arr :  arrPac) {
			name = new String(arr.getPayload(), StandardCharsets.UTF_8);
		}
		assertEquals(fileSender("res/yo.txt"), name);
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}



}
