package com.densvr.nfcreader

/**
 * Created by i-sergeev on 10.04.2020.
 */
class NfcVReaderException(
    val responseCode: NfcVResponseCode
) : Exception(responseCode.message)