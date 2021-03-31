package test;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import packets.Packet;
import util.Utilities;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.CRC32;


public class packetTest {
	private static ArrayList<Packet> arrPac;
	private static Utilities util;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		util = new Utilities();
		Packet pac = new Packet.PacketBuilder()
							.setType(0x02)
							.setTr(0x0)
							.setWindows(0x14)
							.setSequenceNumber(0x1)
							.setLength(0x117)
							.setTimestamp()
					 		.setCheckSum((util.fileSender("res/yo.txt").getBytes(StandardCharsets.UTF_8)))
							.setPayload((util.fileSender("res/yo.txt").getBytes(StandardCharsets.UTF_8)))
							.createPack();
		arrPac = new ArrayList<>();
		arrPac.add(pac);
		util.toByteArr(arrPac.get(0));

	}

	@Test
	public void testTypeGet() {
		assertEquals(0x02, arrPac.get(0).getType());
	}

	@Test
	public void testTypeSet() {
		arrPac.get(0).setType(0x1);
		assertEquals(0x1, arrPac.get(0).getType());

	}

	@Test
	public void testTRGet() {
		assertEquals(0x0, arrPac.get(0).getTr());
	}

	@Test
	public void testWindowsGet() {
		assertEquals(0x14, arrPac.get(0).getWindows());
	}

	@Test
	public void testWindowsSet() {
		arrPac.get(0).setWindows(0x20);
		assertEquals(0x20, arrPac.get(0).getWindows());
	}

	@Test
	public void testSequenceNumberGet() {
		assertEquals(0x1, arrPac.get(0).getSequenceNumber());
	}

	@Test
	public void testSequenceNumberSet() {
		arrPac.get(0).setSequenceNumber(0x31);
		assertEquals(0x31, arrPac.get(0).getSequenceNumber());
	}


	@Test
	public void testLengthGet() {
		assertEquals(0x117, arrPac.get(0).getLength());
	}

	@Test
	public void testLengthSet() {
		arrPac.get(0).setLength(0x30);
		assertEquals(0x30, arrPac.get(0).getLength());
	}

	@Test
	public void testTimestampGet() {
		assertEquals(System.currentTimeMillis()/1000, arrPac.get(0).getTimestamp());
	}

	@Test
	public void testTimestampSet() {
		arrPac.get(0).setTimestamp(1400);
		assertEquals(1400, arrPac.get(0).getTimestamp());
	}

	@Test
	public void testChecksumGet() {
		CRC32 verify = new CRC32 ();
		verify.update(arrPac.get(0).getPayload());
		//assertEquals(verify.getValue(), ByteBuffer.wrap(arrPac.get(0).getChecksum()).getLong());
	}

	@Test
	public void testChecksumSet() {
		CRC32 newCheck = new CRC32();
		newCheck.update(arrPac.get(0).setChecksum((util.fileSender("res/new.txt").getBytes(StandardCharsets.UTF_8))));
		//assertEquals(newCheck.getValue(), ByteBuffer.wrap(arrPac.get(0).getChecksum()).getLong());
	}

	@Test
	public void testPayloadGet() {
		String name = null;
		for(Packet arr :  arrPac) {
			name = new String(arr.getPayload(), StandardCharsets.UTF_8);
		}
		assertEquals(util.fileSender("res/yo.txt"), name);
	}

	@Test
	public void testPayloadSet() {
		String name = null;
		for(Packet arr : arrPac) {
			//name = new String(arr.setPayload("res/new.txt").getBytes(StandardCharsets.UTF_8));
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}



}
