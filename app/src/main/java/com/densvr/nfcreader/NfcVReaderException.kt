package com.densvr.nfcreader

import com.densvr.nfcreader.NfcResponseCode.*
/**
 * Created by i-sergeev on 10.04.2020.
 */
internal class NfcVReaderException(
    val responseCode: NfcResponseCode
) : Exception(responseCode.errorMessage)

internal val NfcResponseCode.errorMessage: String
    get() = when (this) {
        NoStatusInformation -> "No status Information"
        CommandWasSuccessful -> "Command was successful"
        CommandNotSupported -> "Command not supported"
        CommandNotRecognised -> "Command not recognised (e.g. Format error)"
        OptionNotSupported -> "Option not supported"
        UnknownError -> "Unknown error"
        OutOfRange -> "Block not available (out of range)"
        BlockAlreadyLocked -> "Block already locked (can’t be locked again)"
        ContentsCanNotBeChange -> "Block already locked – contents can’t be changed"
        ProgrammingWasUnsuccessful -> "Programming was unsuccessful"
        LockingOrKillWasUnsuccessful -> "Locking/Kill was unsuccessful"
        StartBlockMustBeEven -> "Start block must be even"
        OneOrBothBlocksAlreadyLocked -> "One or both blocks already locked"
        ReadAccessDenied -> "Read Access denied"
        UnknownResponse -> "Unknown response code"
    }