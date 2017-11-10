package com.smartahc.android.core_qr_lib;

import android.hardware.Camera;

public class CameraWrapper {
    public final Camera mCamera;
    public final int mCameraId;

    private CameraWrapper(Camera camera, int cameraId) {
        if(camera == null) {
            throw new NullPointerException("Camera cannot be null");
        } else {
            this.mCamera = camera;
            this.mCameraId = cameraId;
        }
    }

    public static CameraWrapper getWrapper(Camera camera, int cameraId) {
        return camera == null?null:new CameraWrapper(camera, cameraId);
    }
}
