package com.smartahc.android.core_qr_lib;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import java.util.List;

public class CameraUtils {
    public CameraUtils() {
    }

    public static Camera getCameraInstance() {
        return getCameraInstance(getDefaultCameraId());
    }

    public static int getDefaultCameraId() {
        int numberOfCameras = Camera.getNumberOfCameras();
        CameraInfo cameraInfo = new CameraInfo();
        int defaultCameraId = -1;

        for(int i = 0; i < numberOfCameras; ++i) {
            defaultCameraId = i;
            Camera.getCameraInfo(i, cameraInfo);
            if(cameraInfo.facing == 0) {
                return i;
            }
        }

        return defaultCameraId;
    }

    public static Camera getCameraInstance(int cameraId) {
        Camera c = null;

        try {
            if(cameraId == -1) {
                c = Camera.open();
            } else {
                c = Camera.open(cameraId);
            }
        } catch (Exception var3) {
            ;
        }

        return c;
    }

    public static boolean isFlashSupported(Camera camera) {
        if(camera != null) {
            Parameters parameters = camera.getParameters();
            if(parameters.getFlashMode() == null) {
                return false;
            } else {
                List supportedFlashModes = parameters.getSupportedFlashModes();
                return supportedFlashModes != null && !supportedFlashModes.isEmpty() && (supportedFlashModes.size() != 1 || !((String)supportedFlashModes.get(0)).equals("off"));
            }
        } else {
            return false;
        }
    }
}