package com.liangyuen.pi4j_rc522;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.Spi;

/**
 *
 * Basic API for handling the rc-522 RFID reader supporting
 *   - Tag detection
 *   - Collision detection
 *   - Unlocking/locking encrypted data
 *   - Reading encrypted data
 *   - Writing encrypted data.
 *
 * The card data is organized in a number of sectors, each of which divided
 * in a number of blocks.
 *
 * Blocks are handled using a simple cycle:
 *   - authCard() decrypts the data and enables further block operations.
 *   - read() and write() can be done on decrypted data after authCard()
 *   - stopCrypto() restores the encryption state to encrypted and loocked.
 *
 * The module is unsynchronized and fails badly if there is more than one
 * instance.
 *
 * Created by Liang on 2016/3/17,originated from  Python RC522
 *
 * Copyright (c) Alec Leamas, 2018
 *
 * @see https://www.elecrow.com/download/MFRC522%20Datasheet.pdf
 * @see https://github.com/ondryaso/pi-rc522
 *
 */

public class RaspRC522 {
    public static final int     DEFAULT_RST_PIN     = 22;
    public static final int     DEFAULT_SPEED       = 50000;


    public static final byte    PCD_IDLE            = 0x00;
    public static final byte    PCD_AUTHENT         = 0x0E;
    public static final byte    PCD_RECEIVE         = 0x08;
    public static final byte    PCD_TRANSMIT        = 0x04;
    public static final byte    PCD_TRANSCEIVE      = 0x0C;
    public static final byte    PCD_RESETPHASE      = 0x0F;
    public static final byte    PCD_CALCCRC         = 0x03;

    public static final byte    PICC_REQIDL         = (byte) 0x26;
    public static final byte    PICC_REQALL         = (byte) 0x52;
    public static final byte    PICC_ANTICOLL       = (byte) 0x93;
    public static final byte    PICC_SElECTTAG      = (byte) 0x93;
    public static final byte    PICC_AUTHENT1A      = (byte) 0x60;
    public static final byte    PICC_AUTHENT1B      = (byte) 0x61;
    public static final byte    PICC_READ           = (byte) 0x30;
    public static final byte    PICC_WRITE          = (byte) 0xA0;
    public static final byte    PICC_DECREMENT      = (byte) 0xC0;
    public static final byte    PICC_INCREMENT      = (byte) 0xC1;
    public static final byte    PICC_RESTORE        = (byte) 0xC2;
    public static final byte    PICC_TRANSFER       = (byte) 0xB0;
    public static final byte    PICC_HALT           = (byte) 0x50;

    public static final int     MI_OK               = 0;
    public static final int     MI_NOTAGERR         = 1;
    public static final int     MI_ERR              = 2;

    public static final byte    Reserved00          = 0x00;
    public static final byte    CommandReg          = 0x01;
    public static final byte    CommIEnReg          = 0x02;
    public static final byte    DivlEnReg           = 0x03;
    public static final byte    CommIrqReg          = 0x04;
    public static final byte    DivIrqReg           = 0x05;
    public static final byte    ErrorReg            = 0x06;
    public static final byte    Status1Reg          = 0x07;
    public static final byte    Status2Reg          = 0x08;
    public static final byte    FIFODataReg         = 0x09;
    public static final byte    FIFOLevelReg        = 0x0A;
    public static final byte    WaterLevelReg       = 0x0B;
    public static final byte    ControlReg          = 0x0C;
    public static final byte    BitFramingReg       = 0x0D;
    public static final byte    CollReg             = 0x0E;
    public static final byte    Reserved01          = 0x0F;

    public static final byte    Reserved10          = 0x10;
    public static final byte    ModeReg             = 0x11;
    public static final byte    TxModeReg           = 0x12;
    public static final byte    RxModeReg           = 0x13;
    public static final byte    TxControlReg        = 0x14;
    public static final byte    TxAutoReg           = 0x15;
    public static final byte    TxSelReg            = 0x16;
    public static final byte    RxSelReg            = 0x17;
    public static final byte    RxThresholdReg      = 0x18;
    public static final byte    DemodReg            = 0x19;
    public static final byte    Reserved11          = 0x1A;
    public static final byte    Reserved12          = 0x1B;
    public static final byte    MifareReg           = 0x1C;
    public static final byte    Reserved13          = 0x1D;
    public static final byte    Reserved14          = 0x1E;
    public static final byte    SerialSpeedReg      = 0x1F;

    public static final byte    Reserved20          = 0x20;
    public static final byte    CRCResultRegM       = 0x21;
    public static final byte    CRCResultRegL       = 0x22;
    public static final byte    Reserved21          = 0x23;
    public static final byte    ModWidthReg         = 0x24;
    public static final byte    Reserved22          = 0x25;
    public static final byte    RFCfgReg            = 0x26;
    public static final byte    GsNReg              = 0x27;
    public static final byte    CWGsPReg            = 0x28;
    public static final byte    ModGsPReg           = 0x29;
    public static final byte    TModeReg            = 0x2A;
    public static final byte    TPrescalerReg       = 0x2B;
    public static final byte    TReloadRegH         = 0x2C;
    public static final byte    TReloadRegL         = 0x2D;
    public static final byte    TCounterValueRegH   = 0x2E;
    public static final byte    TCounterValueRegL   = 0x2F;

    public static final byte    Reserved30          = 0x30;
    public static final byte    TestSel1Reg         = 0x31;
    public static final byte    TestSel2Reg         = 0x32;
    public static final byte    TestPinEnReg        = 0x33;
    public static final byte    TestPinValueReg     = 0x34;
    public static final byte    TestBusReg          = 0x35;
    public static final byte    AutoTestReg         = 0x36;
    public static final byte    VersionReg          = 0x37;
    public static final byte    AnalogTestReg       = 0x38;
    public static final byte    TestDAC1Reg         = 0x39;
    public static final byte    TestDAC2Reg         = 0x3A;
    public static final byte    TestADCReg          = 0x3B;
    public static final byte    Reserved31          = 0x3C;
    public static final byte    Reserved32          = 0x3D;
    public static final byte    Reserved33          = 0x3E;
    public static final byte    Reserved34          = 0x3F;

    private int rstPinNumber = 22; // RST Pin number
    private int speed = 500000;
    private int spiChannel = 0;
    private final int MAX_LEN = 16;

    /**
     * Create a RaspRC532 using speed = DEFAULT_SPEED and DEFAULT_RST_PIN reset pin
     * number.
     */
    public RaspRC522() {
        this(DEFAULT_SPEED, DEFAULT_RST_PIN);
    }

    /**
     * Create a RaspRC532 using DEFAULT_RST_PIN as reset pin number.
     *
     * @param speed
     *            Transfer speed as defined by com.pi4j.io.spi.imp, in
     *            range 500kHz - 32MHz.
     */
    public RaspRC522(int speed) {
        this(speed, DEFAULT_RST_PIN);
    }

    /**
     * Create a RaspRC522
     *
     * @param speed
     *            transfer speed as defined by com.pi4j.io.spi.imp, in
     *            range 500kHz - 32MHz.
     * @param resetPinNumber
     *            The GPIO pin driven low on interrupt conditions in the
     *            driver. If -1, the default value DEFAULT_RST_PIN_ is used.
     */

    public RaspRC522(int speed, int resetPinNumber) {
        this.rstPinNumber =
            resetPinNumber == -1 ? DEFAULT_RST_PIN : resetPinNumber;
        if (speed < 500000 || speed > 32000000)
            throw new IllegalArgumentException("Speed out of range");
        this.speed = speed;

        Gpio.wiringPiSetup(); // Enable wiringPi pin schema
        int fd = Spi.wiringPiSPISetup(spiChannel, speed);
        if (fd <= -1) {
            System.out.println(" --> Failed to set up  SPI communication");
            // Stop code when error happened
            return;
        } else {
            // System.out.println(" --> Successfully loaded SPI communication");
        }

        Gpio.pinMode(rstPinNumber, Gpio.OUTPUT);
        Gpio.digitalWrite(rstPinNumber, Gpio.HIGH);
        reset();
        writeRC522(TModeReg, (byte) 0x8D);
        writeRC522(TPrescalerReg, (byte) 0x3E);
        writeRC522(TReloadRegL, (byte) 30);
        writeRC522(TReloadRegH, (byte) 0);
        writeRC522(TxAutoReg, (byte) 0x40);
        writeRC522(ModeReg, (byte) 0x3D);
        antennaOn();
    }

    private void reset() {
        writeRC522(CommandReg, PCD_RESETPHASE);
    }

    private void writeRC522(byte address, byte value) {
        byte data[] = new byte[2];
        data[0] = (byte) ((address << 1) & 0x7E);
        data[1] = value;
        int result = Spi.wiringPiSPIDataRW(spiChannel, data);
        if (result == -1)
            System.out.println("Device write  error,address="
                               + address + ",value=" + value);
    }

    private byte readRC522(byte address) {
        byte data[] = new byte[2];
        data[0] = (byte) (((address << 1) & 0x7E) | 0x80);
        data[1] = 0;
        int result = Spi.wiringPiSPIDataRW(spiChannel, data);
        if (result == -1)
            System.out.println("Device read  error,address=" + address);
        return data[1];
    }

    private void setBitMask(byte address, byte mask) {
        byte value = readRC522(address);
        writeRC522(address, (byte) (value | mask));
    }

    private void clearBitMask(byte address, byte mask) {
        byte value = readRC522(address);
        writeRC522(address, (byte) (value & (~mask)));
    }

    private void antennaOn() {
        byte value = readRC522(TxControlReg);
        // if((value & 0x03) != 0x03)
        setBitMask(TxControlReg, (byte) 0x03);
    }

    private void antennaOff() {
        clearBitMask(TxControlReg, (byte) 0x03);
    }

    private int writeCard(byte command, byte[] data, int dataLen,
                           byte[] back_data, int[] back_bits, int[] backLen) {
        int status = MI_ERR;
        byte irq = 0, irq_wait = 0, lastBits = 0;
        int n = 0, i = 0;

        backLen[0] = 0;
        if (command == PCD_AUTHENT) {
            irq = 0x12;
            irq_wait = 0x10;
        } else if (command == PCD_TRANSCEIVE) {
            irq = 0x77;
            irq_wait = 0x30;
        }

        writeRC522(CommIEnReg, (byte) (irq | 0x80));
        clearBitMask(CommIrqReg, (byte) 0x80);
        setBitMask(FIFOLevelReg, (byte) 0x80);

        writeRC522(CommandReg, PCD_IDLE);

        for (i = 0; i < dataLen; i++)
            writeRC522(FIFODataReg, data[i]);

        writeRC522(CommandReg, command);
        if (command == PCD_TRANSCEIVE)
            setBitMask(BitFramingReg, (byte) 0x80);

        i = 2000;
        while (true) {
            n = readRC522(CommIrqReg);
            i--;
            if ((i == 0) || (n & 0x01) > 0 || (n & irq_wait) > 0) {
                // System.out.println("Write_Card i="+i+",n="+n);
                break;
            }
        }
        clearBitMask(BitFramingReg, (byte) 0x80);

        if (i != 0) {
            if ((readRC522(ErrorReg) & 0x1B) == 0x00) {
                status = MI_OK;
                if ((n & irq & 0x01) > 0)
                    status = MI_NOTAGERR;
                if (command == PCD_TRANSCEIVE) {
                    n = readRC522(FIFOLevelReg);
                    lastBits = (byte) (readRC522(ControlReg) & 0x07);
                    if (lastBits != 0)
                        back_bits[0] = (n - 1) * 8 + lastBits;
                    else
                        back_bits[0] = n * 8;

                    if (n == 0)
                        n = 1;
                    if (n > this.MAX_LEN)
                        n = this.MAX_LEN;
                    backLen[0] = n;
                    for (i = 0; i < n; i++)
                        back_data[i] = readRC522(FIFODataReg);
                }
            } else
                status = MI_ERR;
        }
        return status;
    }

    private void calculateCRC(byte[] data) {
        int i, n;
        clearBitMask(DivIrqReg, (byte) 0x04);
        setBitMask(FIFOLevelReg, (byte) 0x80);

        for (i = 0; i < data.length - 2; i++)
            writeRC522(FIFODataReg, data[i]);
        writeRC522(CommandReg, PCD_CALCCRC);
        i = 255;
        while (true) {
            n = readRC522(DivIrqReg);
            i--;
            if ((i == 0) || ((n & 0x04) > 0))
                break;
        }
        data[data.length - 2] = readRC522(CRCResultRegL);
        data[data.length - 1] = readRC522(CRCResultRegM);
    }

    /**
     * Convert sector  to blockaddress.
     *
     * @param sector 0-15
     * @param block 0-3
     * @return block address
     */
    private byte sector2BlockAddress(byte sector, byte block) {
        if (sector < 0 || sector > 15 || block < 0 || block > 3)
            return (byte) (-1);
        return (byte) (sector * 4 + block);
    }

    /**
     * Setup up transcieve operation mode.
     *
     * @param req_mode a PICC_ mode request
     * @param back_bits out, on return backbits[0] is number of bits in fifo.
     * @return MI_OK if successful, else a MI_ error code.
     */
    public int setupTranscieve(byte req_mode, int[] back_bits) {
        int status;
        byte tagType[] = new byte[1];
        byte data_back[] = new byte[16];
        int backLen[] = new int[1];

        writeRC522(BitFramingReg, (byte) 0x07);

        tagType[0] = req_mode;
        back_bits[0] = 0;
        status = writeCard(PCD_TRANSCEIVE, tagType, 1,
                            data_back, back_bits, backLen);
        if (status != MI_OK || back_bits[0] != 0x10) {
            // System.out.println("status="+status+",back_bits[0]="+back_bits[0]);
            status = MI_ERR;
        }

        return status;
    }

    /**
     * Check if there is a valid tag to communicate with out there.
     *
     * @param back_data On successful return, contains the located tag id
     *                  as five bytes.
     * @return MI_OK if successful, else an MI_ error code.
     */
    public int antiColl(byte[] back_data) {
        int status;
        byte[] serial_number = new byte[2];
        int serial_number_check = 0;
        int backLen[] = new int[1];
        int back_bits[] = new int[1];
        int i;

        writeRC522(BitFramingReg, (byte) 0x00);
        serial_number[0] = PICC_ANTICOLL;
        serial_number[1] = 0x20;
        status = writeCard(PCD_TRANSCEIVE, serial_number, 2,
                            back_data, back_bits, backLen);
        if (status == MI_OK) {
            if (backLen[0] == 5) {
                for (i = 0; i < 4; i++)
                    serial_number_check ^= back_data[i];
                if (serial_number_check != back_data[4]) {
                    status = MI_ERR;
                    System.out.println("check error");
                }
            } else {
                status = MI_OK;
                System.out.println("backLen[0]=" + backLen[0]);
            }
        }
        return status;
    }

    /**
     * Select a uid for further unlock/lock operations.
     *
     * @param uid UID to select, five bytes.
     * @return Read data from analog fifo if available, else 0.
     */
    public int selectTag(byte[] uid) {
        int status;
        byte data[] = new byte[9];
        byte back_data[] = new byte[this.MAX_LEN];
        int back_bits[] = new int[1];
        int backLen[] = new int[1];
        int i, j;

        data[0] = PICC_SElECTTAG;
        data[1] = 0x70;
        for (i = 0, j = 2; i < 5; i++, j++)
            data[j] = uid[i];
        calculateCRC(data);

        status = writeCard(PCD_TRANSCEIVE, data, 9,
                            back_data, back_bits, backLen);
        if (status == MI_OK && back_bits[0] == 0x18)
            return back_data[0];
        else
            return 0;
    }

    /**
     * Authenticates to use specified block in sector 0. Tag must be selected
     * using select_tag(uid) before auth.
     *
     * @param auth_mode RFID.auth_a or RFID.auth_b
     * @param block_address the block to unlock
     * @param key  six bytes key.
     * @param uid uid (4 bytes) for user to connect to.
     * @return MI_OK if successful, else an MI_ error code.
     */
    public int authCard(byte auth_mode, byte block_address,
                         byte[] key, byte[] uid) {
        int status;
        byte data[] = new byte[12];
        byte back_data[] = new byte[this.MAX_LEN];
        int back_bits[] = new int[1];
        int backLen[] = new int[1];
        int i, j;

        data[0] = auth_mode;
        data[1] = block_address;
        for (i = 0, j = 2; i < 6; i++, j++)
            data[j] = key[i];
        for (i = 0, j = 8; i < 4; i++, j++)
            data[j] = uid[i];

        status = writeCard(PCD_AUTHENT, data, 12,
                            back_data, back_bits, backLen);
        if ((readRC522(Status2Reg) & 0x08) == 0)
            status = MI_ERR;
        return status;
    }

    /**
     * Authenticates to use specified block in given sector. Tag must
     * be selected using select_tag(uid) before auth.
     *
     * @param auth_mode RFID.auth_a or RFID.auth_b
     * @param sector Sector containing block to unlock.
     * @param block_Address Block to unlock.
     * @param key  Six bytes encryption key.
     * @param uid Uid (4 bytes) for user to connect to.
     * @return MI_OK if successful, else an MI_ error code.
     */
    public int authCard(byte auth_mode, byte sector, byte block,
                         byte[] key, byte[] uid)
    {
        return
            authCard(auth_mode, sector2BlockAddress(sector, block), key, uid);
    }

    /** End operation initiated by authCard(). */
    public void stopCrypto() {
        clearBitMask(Status2Reg, (byte) 0x08);
    }

    /**
     * Reads data from block in sector 0. Block must be authenticated
     * using authCard() before calling read().
     *
     * @param block_address Block number to read from
     * @param back_data On successful return, holds data.
     * @return MI_OK if successful, else an MI_ error code.
     */
    public int read(byte block_address, byte[] back_data) {
        int status;
        byte data[] = new byte[4];
        int back_bits[] = new int[1];
        int backLen[] = new int[1];
        int i, j;

        data[0] = PICC_READ;
        data[1] = block_address;
        calculateCRC(data);
        status = writeCard(PCD_TRANSCEIVE, data, data.length,
                            back_data, back_bits, backLen);
        if (backLen[0] == 16)
            status = MI_OK;
        return status;
    }

    /**
     * Read data from block in given sector. Block must be authenticated
     * using authCard() before calling read().
     *
     * @param sector Sector containing block.
     * @param block_address Block number to read from
     * @param back_data On successful return, holds data.
     * @return MI_OK if successful, else an MI_ error code.
     */
    public int read(byte sector, byte block, byte[] back_data) {
        return read(sector2BlockAddress(sector, block), back_data);
    }

    /**
     * Write data to block in sector 0. Block must be authenticated
     * using authCard() before calling write().
     *
     * @param block_address Block to read
     * @param data On successful return, read data (16 bytes)
     * @return MI_OK if successful, else an MI_ error code.
     */
    public int write(byte block_address, byte[] data) {
        int status;
        byte buff[] = new byte[4];
        byte buff_write[] = new byte[data.length + 2];
        byte back_data[] = new byte[this.MAX_LEN];
        int back_bits[] = new int[1];
        int backLen[] = new int[1];
        int i;

        buff[0] = PICC_WRITE;
        buff[1] = block_address;
        calculateCRC(buff);
        status = writeCard(PCD_TRANSCEIVE, buff, buff.length,
                            back_data, back_bits, backLen);
        // System.out.println("write_card status="+status);
        // System.out.println("back_bits[0]="+back_bits[0]+",(back_data[0] &
        // 0x0F)="+(back_data[0] &
        // 0x0F));
        if (status != MI_OK || back_bits[0] != 4
            || (back_data[0] & 0x0F) != 0x0A) {
            status = MI_ERR;
        }
        if (status == MI_OK) {
            for (i = 0; i < data.length; i++)
                buff_write[i] = data[i];
            calculateCRC(buff_write);
            status = writeCard(PCD_TRANSCEIVE,
                                buff_write, buff_write.length,
                                back_data, back_bits, backLen);
            // System.out.println("write_card data status="+status);
            // System.out.println("back_bits[0]="+back_bits[0]+",(back_data[0] &
            // 0x0F)="+(back_data[0] &
            // 0x0F));
            if (status != MI_OK || back_bits[0] != 4
                || (back_data[0] & 0x0F) != 0x0A) {
                    status = MI_ERR;
            }
        }
        return status;
    }

    /**
     * Write data to block in given sector. Block must be authenticated
     * using authCard() before calling write().
     *
     * @param block_address Block number to read from
     * @param sector Sector containing block.
     * @param data On successful return, holds data read.
     * @return MI_OK if successful, else an MI_ error code.
     */
    public int write(byte sector, byte block, byte[] data) {
        return write(sector2BlockAddress(sector, block), data);
    }

    public byte[] dumpClassic1K(byte[] key, byte[] uid) {
        int i, status;
        byte[] data = new byte[1024];
        byte[] buff = new byte[16];

        for (i = 0; i < 64; i++) {
            status = authCard(PICC_AUTHENT1A, (byte) i, key, uid);
            if (status == MI_OK) {
                status = read((byte) i, buff);
                if (status == MI_OK)
                    System.arraycopy(buff, 0, data, i * 64, 16);
            }
        }
        return data;
    }

    // uid-5 bytes
    public int selectMirareOne(byte[] uid) {
        int back_bits[] = new int[1];
        byte tagid[] = new byte[5];
        int status;

        status = setupTranscieve(RaspRC522.PICC_REQIDL, back_bits);
        if (status != MI_OK)
            return status;
        status = antiColl(tagid);
        if (status != MI_OK)
            return status;
        selectTag(tagid);
        System.arraycopy(tagid, 0, uid, 0, 5);

        return status;
    }
}
