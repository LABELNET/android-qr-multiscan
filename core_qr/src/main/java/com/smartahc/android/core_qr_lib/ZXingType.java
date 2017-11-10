package com.smartahc.android.core_qr_lib;

/**
 * reader : 支持二维码和条形码，但只返回一个结果；
 * qr_reader : 仅支持二维码，但可以返回多个二维码值；
 */
public enum ZXingType {
    READER, QR_READER
}