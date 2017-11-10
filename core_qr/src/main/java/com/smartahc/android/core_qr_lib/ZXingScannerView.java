package com.smartahc.android.core_qr_lib;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;


public class ZXingScannerView extends BarcodeScannerView {
    private static final String TAG = "ZXingScannerView";
    public static final List<BarcodeFormat> ALL_FORMATS = new ArrayList();
    private List<BarcodeFormat> mFormats;
    private ZXingScannerView.ResultHandler mResultHandler;
    // MultiFormatReader : 支持二维码和条形码扫描，只返回一个扫描结果；
    private MultiFormatReader mMultiFormatReader;
    // QrCodeMultiReader : 仅支持二维码扫描，可扫描多个二维码，返回多个结果；
    private QRCodeMultiReader qrCodeMultiReader;
    // type
    private ZXingType zXingType;
    private EnumMap hints;

    /**
     * 初始化
     */
    private void init() {
        switch (zXingType) {
            case READER:
                this.initMultiFormatReader();
                break;
            case QR_READER:
                this.initQRMultiFormatReader();
                break;
        }
    }


    public ZXingScannerView(Context context, ZXingType zXingType) {
        super(context);
        this.zXingType = zXingType;
        this.init();
    }


    public ZXingScannerView(Context context, AttributeSet attributeSet, ZXingType zXingType) {
        super(context, attributeSet);
        this.zXingType = zXingType;
        this.init();
    }

    public void setFormats(List<BarcodeFormat> formats) {
        this.mFormats = formats;
        this.init();
    }

    public void setResultHandler(ZXingScannerView.ResultHandler resultHandler) {
        this.mResultHandler = resultHandler;
    }

    public Collection<BarcodeFormat> getFormats() {
        return this.mFormats == null ? ALL_FORMATS : this.mFormats;
    }

    /**
     * 初始化 qr format reader
     */
    private void initQRMultiFormatReader() {
        hints = new EnumMap(DecodeHintType.class);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, this.getFormats());
        this.qrCodeMultiReader = new QRCodeMultiReader();
    }

    /**
     * 初始化 multiformatReader
     */
    private void initMultiFormatReader() {
        hints = new EnumMap(DecodeHintType.class);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, this.getFormats());
        this.mMultiFormatReader = new MultiFormatReader();
        this.mMultiFormatReader.setHints(hints);
    }

    /**
     * 解析数据
     *
     * @param data   data
     * @param camera 相机
     */
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (this.mResultHandler != null) {
            switch (zXingType) {
                case READER:
                    parseMultiFormatReader(data, camera);
                    break;
                case QR_READER:
                    parseQrMultiFormatReader(data, camera);
                    break;
            }
        }
    }

    /**
     * 解析多个结果
     */
    private void parseQrMultiFormatReader(byte[] data, Camera camera) {

        try {
            Parameters e = camera.getParameters();
            Size size = e.getPreviewSize();
            int width = size.width;
            int height = size.height;
            if (DisplayUtils.getScreenOrientation(this.getContext()) == 1) {
                int rawResult = this.getRotationCount();
                if (rawResult == 1 || rawResult == 3) {
                    int source = width;
                    width = height;
                    height = source;
                }

                data = this.getRotatedData(data, camera);
            }

            Result[] rawResult1 = null;
            PlanarYUVLuminanceSource source1 = this.buildLuminanceSource(data, width, height);
            if (source1 != null) {
                BinaryBitmap finalRawResult = new BinaryBitmap(new HybridBinarizer(source1));
                try {
                    rawResult1 = this.qrCodeMultiReader.decodeMultiple(finalRawResult, hints);
                } catch (ReaderException | NullPointerException | ArrayIndexOutOfBoundsException ignored) {
                    ;
                } finally {
                    this.qrCodeMultiReader.reset();
                }

                if (rawResult1 == null) {
                    LuminanceSource handler = source1.invert();
                    finalRawResult = new BinaryBitmap(new HybridBinarizer(handler));

                    try {
                        rawResult1 = this.qrCodeMultiReader.decodeMultiple(finalRawResult, hints);
                    } catch (NotFoundException ignored) {
                        ;
                    } finally {
                        this.qrCodeMultiReader.reset();
                    }
                }
            }

            if (rawResult1 != null && rawResult1.length > 0) {
                Handler handler1 = new Handler(Looper.getMainLooper());
                final Result[] finalRawResult1 = rawResult1;
                handler1.post(new Runnable() {
                    public void run() {
                        ResultHandler tmpResultHandler = ZXingScannerView.this.mResultHandler;
                        ZXingScannerView.this.mResultHandler = null;
                        ZXingScannerView.this.stopCameraPreview();
                        if (tmpResultHandler != null) {
                            tmpResultHandler.handleResult(finalRawResult1);
                        }

                    }
                });
            } else {
                camera.setOneShotPreviewCallback(this);
            }
        } catch (RuntimeException var33) {
            Log.e("ZXingScannerView", var33.toString(), var33);
        }
    }

    /**
     * multiformatReader
     */
    private void parseMultiFormatReader(byte[] data, Camera camera) {
        try {
            Parameters e = camera.getParameters();
            Size size = e.getPreviewSize();
            int width = size.width;
            int height = size.height;
            if (DisplayUtils.getScreenOrientation(this.getContext()) == 1) {
                int rawResult = this.getRotationCount();
                if (rawResult == 1 || rawResult == 3) {
                    int source = width;
                    width = height;
                    height = source;
                }

                data = this.getRotatedData(data, camera);
            }


            Result rawResult1 = null;
            PlanarYUVLuminanceSource source1 = this.buildLuminanceSource(data, width, height);
            if (source1 != null) {
                BinaryBitmap finalRawResult = new BinaryBitmap(new HybridBinarizer(source1));

                try {
                    rawResult1 = this.mMultiFormatReader.decodeWithState(finalRawResult);
                } catch (ReaderException var29) {
                    ;
                } catch (NullPointerException var30) {
                    ;
                } catch (ArrayIndexOutOfBoundsException var31) {
                    ;
                } finally {
                    this.mMultiFormatReader.reset();
                }

                if (rawResult1 == null) {
                    LuminanceSource handler = source1.invert();
                    finalRawResult = new BinaryBitmap(new HybridBinarizer(handler));

                    try {
                        rawResult1 = this.mMultiFormatReader.decodeWithState(finalRawResult);
                    } catch (NotFoundException var27) {
                        ;
                    } finally {
                        this.mMultiFormatReader.reset();
                    }
                }
            }

            if (rawResult1 != null) {
                Handler handler1 = new Handler(Looper.getMainLooper());
                final Result finalRawResult1 = rawResult1;
                handler1.post(new Runnable() {
                    public void run() {
                        ResultHandler tmpResultHandler = ZXingScannerView.this.mResultHandler;
                        ZXingScannerView.this.mResultHandler = null;
                        ZXingScannerView.this.stopCameraPreview();
                        if (tmpResultHandler != null) {
                            tmpResultHandler.handleResult(finalRawResult1);
                        }

                    }
                });
            } else {
                camera.setOneShotPreviewCallback(this);
            }
        } catch (RuntimeException var33) {
            Log.e("ZXingScannerView", var33.toString(), var33);
        }
    }

    /**
     * 重新扫描
     */
    public void resumeCameraPreview(ZXingScannerView.ResultHandler resultHandler) {
        this.mResultHandler = resultHandler;
        super.resumeCameraPreview();
    }

    /**
     * PlanarYUVLuminanceSource
     */
    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = this.getFramingRectInPreview(width, height);
        if (rect == null) {
            return null;
        } else {
            PlanarYUVLuminanceSource source = null;

            try {
                source = new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height(), false);
            } catch (Exception var7) {
                ;
            }

            return source;
        }
    }

    static {
        ALL_FORMATS.add(BarcodeFormat.AZTEC);
        ALL_FORMATS.add(BarcodeFormat.CODABAR);
        ALL_FORMATS.add(BarcodeFormat.CODE_39);
        ALL_FORMATS.add(BarcodeFormat.CODE_93);
        ALL_FORMATS.add(BarcodeFormat.CODE_128);
        ALL_FORMATS.add(BarcodeFormat.DATA_MATRIX);
        ALL_FORMATS.add(BarcodeFormat.EAN_8);
        ALL_FORMATS.add(BarcodeFormat.EAN_13);
        ALL_FORMATS.add(BarcodeFormat.ITF);
        ALL_FORMATS.add(BarcodeFormat.MAXICODE);
        ALL_FORMATS.add(BarcodeFormat.PDF_417);
        ALL_FORMATS.add(BarcodeFormat.QR_CODE);
        ALL_FORMATS.add(BarcodeFormat.RSS_14);
        ALL_FORMATS.add(BarcodeFormat.RSS_EXPANDED);
        ALL_FORMATS.add(BarcodeFormat.UPC_A);
        ALL_FORMATS.add(BarcodeFormat.UPC_E);
        ALL_FORMATS.add(BarcodeFormat.UPC_EAN_EXTENSION);
    }

    public interface ResultHandler {

        void handleResult(Result... results);

    }
}