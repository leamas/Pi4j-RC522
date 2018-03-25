
package com.liangyuen.pi4j_rc522;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *  Created by Liang on 2016/3/13.
 *  Copyright (c) Alec Leamas, 2018
 */


public class WiringPiSPIExample {

    private static Logger logger =
        LogManager.getLogger(ReadRFID.class.getName());

    public static void main(String args[])
    	throws InterruptedException, RC522Exception
    {
        RaspRC522 rc522 = new RaspRC522();
        int back_len;
        Uid uid;
        int i, status;
        String strUID;
        byte sector = 15, block = 3;

        try {
             back_len = rc522.setupTranscieve(RaspRC522.PICC_REQIDL);
            logger.info("Detected card:" + back_len);
        }
        catch (RC522Exception ex) {
            logger.info("No card detected");
            return;
        }
        uid = rc522.antiColl();
        if (uid.isFailed()) {
            logger.info("anticoll error");
            return;
        }
        // logger.info(strUID);
        // logger.info("Card Read UID:" + tagid[0] + "," + tagid[1] + "," +
        // tagid[2] + "," + tagid[3]);
        logger.info("Card Read UID:" + uid.toString(","));

        Key defaultkey = new Key("ff:ff:ff:ff:ff:ff");
        // Select the scanned tag
        int size = rc522.selectTag(uid);
        logger.info("Size=" + size);

        // Authenticate

        BlockAddress blockAddress = new BlockAddress(sector, block);
        status = rc522.authCard(RaspRC522.PICC_AUTHENT1A, blockAddress, defaultkey, uid);
        if (status != RaspRC522.MI_OK) {
            logger.info("Authenticate error");
            return;
        }
        byte data[] = new byte[16];
        byte controlbytes[] = new byte[] { (byte) 0x08, (byte) 0x77, (byte) 0x8f, (byte) 0x69 };
        System.arraycopy(controlbytes, 0, data, 6, 4);
        Key keyA = new Key("03:03:00:01:02:03");
        Key keyB = new Key("ff:ff:ff:ff:ff:ff");
        System.arraycopy(keyA.toBytes(), 0, data, 0, 6);
        System.arraycopy(keyB.toBytes(), 0, data, 10, 6);
        status = rc522.write(blockAddress, data);
        if (status == RaspRC522.MI_OK) {
            logger.info("Write data finished");
        } else {
            logger.info("Write data error,status=" + status);
            return;
        }
    }
}
