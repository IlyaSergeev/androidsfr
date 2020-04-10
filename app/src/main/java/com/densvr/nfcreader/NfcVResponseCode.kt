package com.densvr.nfcreader

//value from ISO15693
enum class NfcVResponseCode(internal val bytes: ByteArray) {

    //[]
    NoStatusInformation(byteArrayOf()),

    //[0000]
    CommandWasSuccessful(byteArrayOf(0x00, 0x00)),

    //[0101]
    CommandNotSupported(byteArrayOf(0x01, 0x01)),

    //[0102]
    CommandNotRecognised(byteArrayOf(0x01, 0x02)),

    //[0103]
    OptionNotSupported(byteArrayOf(0x01, 0x03)),

    //[010F]
    UnknownError(byteArrayOf(0x01, 0x0F)),

    //[0110]
    OutOfRange(byteArrayOf(0x01, 0x10)),

    //[0111]
    BlockAlreadyLocked(byteArrayOf(0x01, 0x11)),

    //[0112]
    ContentsCanNotBeChange(byteArrayOf(0x01, 0x12)),

    //[0113]
    ProgrammingWasUnsuccessful(byteArrayOf(0x01, 0x13)),

    //[0114]
    LockingOrKillWasUnsuccessful(byteArrayOf(0x01, 0x14)),

    //[01A1]
    StartBlockMustBeEven(byteArrayOf(0x01, 0xA1.toByte())),

    //[01A2]
    OneOrBothBlocksAlreadyLocked(byteArrayOf(0x01, 0xA2.toByte())),

    //[01B0]
    ReadAccessDenied(byteArrayOf(0x01, 0xB0.toByte()));

    val hasError
        get() = bytes.size > 1 && bytes[0] != ZERO_BYTE

    val isSuccessful
        get() = this == CommandWasSuccessful

    val length = bytes.size
}