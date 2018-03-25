package com.liangyuen.pi4j_rc522;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Liang on 2016/3/7.
 *
 *  Copyright (c) Alec Leamas, 2018
 */

import com.pi4j.wiringpi.Spi;

public class ReadRFID {
    static Key KEY_A = new Key("ff:ff:ff:ff:ff:ff");
    static Key KEY_B = new Key("ff:ff:ff:ff:ff:ff");
    private static Logger logger =
        LogManager.getLogger(ReadRFID.class.getName());

    public static void main(String[] args)
            throws InterruptedException, RC522Exception {
        logger.info("Initiating ReadRFID test program");
        RaspRC522 rc522 = new RaspRC522();
        int back_bits;
        ByteArray strUID;
        Uid uid;
//      int i;
        int status = 0;
//      byte blockaddress = 8;
        BlockAddress address = new BlockAddress(15, 2);
        byte sector = 15; //, block = 2;
        while (true) {
            Thread.sleep(2000);
            try {
                back_bits = rc522.setupTranscieve(RaspRC522.PICC_REQIDL);
                logger.info("Detected:"+back_bits);
            } catch (RC522Exception ex) {
                logger.debug("No card detected");
                continue;
            }
            uid = (rc522.antiColl());
            if (uid.isFailed()) {
                logger.debug("anticoll error");
                continue;
            }

            try {
                int size=rc522.selectTag(uid);
                logger.debug("Size="+size);
            }
            catch (RC522Exception ex) {
            	logger.debug("Cannot select: " + ex.getMessage());
            	continue;
            }

//          rc522.selectMirareOne(tagid);
            // System.out.println(strUID);
            logger.debug("New UID: " + uid.toString(","));

            // Authenticate
            byte data[] = new byte[16];
            try {
                status = rc522.authCard(RaspRC522.PICC_AUTHENT1A, address,
                                        KEY_A, uid);
            }
            catch (RC522Exception ex) {
            	logger.info("Cannot authenticate: %d", status);
            }
            if (status != RaspRC522.MI_OK) {
                logger.info("Authenticate A error");
                continue;
            }
            try {
                Block block = rc522.read(new BlockAddress(0, 3));
                logger.info("Authenticated, read data: " + block.toString());
            }
            catch (RC522Exception ex) {
                logger.debug("Cannot authenticate");
            }
            try {
                Block block = rc522.read(new BlockAddress(0, 3));
                logger.info("Read control block data: " + block.toString());
            }
            catch (RC522Exception ex) {
                logger.debug("Cannot read control block");
            }
            rc522.stopCrypto();
//
//      for (i = 0; i < 16; i++) {
//          data[i] = (byte) 0x00;
//      }
//      // Authenticate
//      status = rc522.authCard(RaspRC522.PICC_AUTHENT1B, sector, block, keyB, tagid);
//      if (status != RaspRC522.MI_OK) {
//          System.out.println("Authenticate B error");
//          continue;
//      }
//      status = rc522.write(sector, block, data);
//      if (status == RaspRC522.MI_OK)
//          System.out.println("Write data finished");
//      else {
//          System.out.println("Write data error,status=" + status);
//          continue;
//      }
        // byte buff[]=new byte[16];
        //
        // for (i = 0; i < 16; i++)
        // {
        // buff[i]=(byte)0;
        // }
        // status=rc522.Read(sector,block,buff);
        // if (status == RaspRC522.MI_OK)
        // System.out.println("Read Data finished");
        // else
        // {
        // System.out.println("Read data error,status="+status);
        // continue;
        // }
        //
        // System.out.print("sector"+sector+",block="+block+" :");
        // String strData= Convert.bytesToHex(buff);
        // for (i=0;i<16;i++)
        // {
        // System.out.print(strData.substring(i*2,i*2+2));
        // if (i < 15) System.out.print(",");
        // else System.out.println("");
        // }
        }  // while (true)
    }

    public static void rfidReaderLoop(int sleeptime) throws InterruptedException {
        int count = 0;
        while (count++ < 3) {

            int packetlength = 5;

            byte packet[] = new byte[packetlength];
            packet[0] = (byte) 0x80; // FIRST PACKET GETS IGNORED BUT HAS
            // TO BE SET TO READ
            packet[1] = (byte) 0x80; // ADDRESS 0 Gives data of Address 0
            packet[2] = (byte) 0x82; // ADDRESS 1 Gives data of Address 1
            packet[3] = (byte) 0x84; // ADDRESS 2 Gives data of Address 2
            packet[4] = (byte) 0x86; // ADDRESS 3 Gives data of Address 3

            logger.debug("Data to be transmitted:");
            logger.debug("[TX] " + bytesToHex(packet));
            logger.debug("[TX1] " + packet[1]);
            logger.debug("[TX2] " + packet[2]);
            logger.debug("[TX3] " + packet[3]);
            logger.debug("[TX4] " + packet[4]);
            logger.debug("Transmitting data...");

            // Send data to Reader and receive answerpacket.
            packet = readFromRFID(0, packet, packetlength);

            logger.debug("Data transmitted, packets received.");
            logger.debug("Received Packets (First packet to be ignored!)");
            logger.debug("[RX] " + bytesToHex(packet));
            logger.debug("[RX1] " + packet[1]);
            logger.debug("[RX2] " + packet[2]);
            logger.debug("[RX3] " + packet[3]);
            logger.debug("[RX4] " + packet[4]);
            logger.debug("-----------------------------------------------");

            if (packet.length == 0) {
                // Reset when no packet received
                // ResetPin.high();
                Thread.sleep(50);
                // ResetPin.low();
            }
            // Wait 1/2 second before trying to read again
            Thread.sleep(sleeptime);
        }

    }

    public static byte[] readFromRFID(int channel, byte[] packet, int length) {
        Spi.wiringPiSPIDataRW(channel, packet, length);

        return packet;
    }

    public static boolean writeToRFID(int channel, byte fullAddress, byte data) {

        byte[] packet = new byte[2];
        packet[0] = fullAddress;
        packet[1] = data;

        if (Spi.wiringPiSPIDataRW(channel, packet, 1) >= 0)
            return true;
        else
            return false;
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
