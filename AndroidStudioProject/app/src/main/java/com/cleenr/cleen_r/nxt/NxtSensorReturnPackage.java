package com.cleenr.cleen_r.nxt;

/**
 * Created by hudini on 26.11.2014.
 */
public class NxtSensorReturnPackage {
    private final byte mStatus;
    private final byte mPort;
    private final boolean mValid;
    private final boolean mCalibrated;
    private final byte mSensorType;
    private final byte mSensorMode;
    private final short mRawValue;
    private final short mNormalizedValue;
    private final short mScaledValue;
    private final short mCalibratedValue;

    public NxtSensorReturnPackage(
            byte status, byte port, boolean valid,
            boolean calibrated, byte sensorType,
            byte sensorMode, short rawValue, short normalizedValue,
            short scaledValue, short calibratedValue) {
        mStatus = status;
        mPort = port;
        mValid = valid;
        mCalibrated = calibrated;
        mSensorType = sensorType;
        mSensorMode = sensorMode;
        mRawValue = rawValue;
        mNormalizedValue = normalizedValue;
        mScaledValue = scaledValue;
        mCalibratedValue = calibratedValue;
    }

    public NxtSensorReturnPackage(byte[] packet) {
        this(
                packet[2], packet[3],
                packet[4] != 0, packet[5] != 0,
                packet[6], packet[7],
                (short) (packet[8] << 8 | packet[9]),
                (short) (packet[10] << 8 | packet[11]),
                (short) (packet[12] << 8 | packet[13]),
                (short) (packet[14] << 8 | packet[15]));
    }


    public byte getStatus() {
        return mStatus;
    }

    public byte getPort() {
        return mPort;
    }

    public boolean isValid() {
        return mValid;
    }

    public boolean isCalibrated() {
        return mCalibrated;
    }

    public byte getSensorType() {
        return mSensorType;
    }

    public byte getSensorMode() {
        return mSensorMode;
    }

    public short getRawValue() {
        return mRawValue;
    }

    public short getNormalizedValue() {
        return mNormalizedValue;
    }

    public short getScaledValue() {
        return mScaledValue;
    }

    public short getCalibratedValue() {
        return mCalibratedValue;
    }
}
