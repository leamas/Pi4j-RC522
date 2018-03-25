
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

        byte[] defaultkey = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
        // Select the scanned tag
        int size = rc522.selectTag(uid);
        logger.info("Size=" + size);

        // Authenticate

        BlockAddress blockAddress = new BlockAddress(sector, block);
        status = rc522.authCard(RaspRC522.PICC_AUTHENT1A, blockAddress, defaultkey, uid.toBytes());
        if (status != RaspRC522.MI_OK) {
            logger.info("Authenticate error");
            return;
        }
        byte data[] = new byte[16];
        byte controlbytes[] = new byte[] { (byte) 0x08, (byte) 0x77, (byte) 0x8f, (byte) 0x69 };
        System.arraycopy(controlbytes, 0, data, 6, 4);
        byte[] keyA = new byte[] { (byte) 0x03, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03 };
        byte[] keyB = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
        System.arraycopy(keyA, 0, data, 0, 6);
        System.arraycopy(keyB, 0, data, 10, 6);
        status = rc522.write(blockAddress, data);
        if (status == RaspRC522.MI_OK) {
            logger.info("Write data finished");
        } else {
            logger.info("Write data error,status=" + status);
            return;
        }
    }
}
