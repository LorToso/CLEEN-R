/*
 * Copyright (c) 2010 Jacek Fedorynski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This file is derived from:
 * 
 * http://developer.android.com/resources/samples/BluetoothChat/src/com/example/android/BluetoothChat/BluetoothChatService.html
 * 
 * Copyright (c) 2009 The Android Open Source Project
 */

package com.cleenr.cleen_r.nxt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class NxtTalker {
    public static final String TAG = NxtTalker.class.getSimpleName();

    public static final int STATE_NONE = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    public static final byte MOTOR_PORT_A = 0x00;
    public static final byte MOTOR_PORT_B = 0x01;
    public static final byte MOTOR_PORT_C = 0x02;
    public static final byte MOTOR_PORT_ALL = (byte) 0xFF;

    public static final byte MOTOR_REG_MODE_NONE = 0x00;
    public static final byte MOTOR_REG_MODE_SPEED = 0x01;
    public static final byte MOTOR_REG_MODE_SYNC = 0x02;

    public static final byte SENSOR_TYPE_NONE = 0x00;
    public static final byte SENSOR_TYPE_SWITCH = 0x01;
    public static final byte SENSOR_TYPE_TEMPERATURE = 0x02;
    public static final byte SENSOR_TYPE_REFLECTION = 0x03;
    public static final byte SENSOR_TYPE_ANGLE = 0x04;
    public static final byte SENSOR_TYPE_LIGHT_ACTIVE = 0x05;
    public static final byte SENSOR_TYPE_LIGHT_INACTIVE = 0x06;
    public static final byte SENSOR_TYPE_SOUND_DB = 0x07;
    public static final byte SENSOR_TYPE_SOUND_DBA = 0x08;
    public static final byte SENSOR_TYPE_CUSTOM = 0x09;
    public static final byte SENSOR_TYPE_LOWSPEED = 0x0A;
    public static final byte SENSOR_TYPE_LOWSPEED_9V = 0x0B;
    public static final byte SENSOR_TYPE_UNKNOWN = 0x0C;

    public static final byte SENSOR_MODE_RAW = 0x00;
    public static final byte SENSOR_MODE_BOOLEAN = 0x20;
    public static final byte SENSOR_MODE_TRANSITIONCNT = 0x40;
    public static final byte SENSOR_MODE_PERIODCOUNTER = 0x60;
    public static final byte SENSOR_MODE_PCTFULLSCALE = (byte) 0x80;
    public static final byte SENSOR_MODE_CELSIUSMODE = (byte) 0xA0;
    public static final byte SENSOR_MODE_FAHRENHEIT = (byte) 0xC0;
    public static final byte SENSOR_MODE_ANGLESTEPS = (byte) 0xE0;
//    public static final byte SENSOR_MODE_SLOPEMASK = 0x1F;
//    public static final byte SENSOR_MODE_MODEMASK = (byte) 0xE0;

    private int mState;
    private BluetoothAdapter mAdapter;
    private BluetoothSocket mBtSocket;
    private InputStream mBtInputStream;
    private OutputStream mBtOutputStream;
    private ConnectThread mConnectThread;

    public NxtTalker() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        setState(STATE_NONE);
    }

    public synchronized int getState() {
        return mState;
    }

    private synchronized void setState(int state) {
        mState = state;
        //TODO: insert external state handler calls here
    }

    public synchronized void connect(BluetoothDevice device) {
        Log.i(TAG, "Connecting to device '" + device.getName() + "'");

        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        closeBtSocket();

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        closeBtSocket();
        mBtSocket = socket;
        try {
            mBtInputStream = socket.getInputStream();
            mBtOutputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            connectionFailed();
            return;
        }

        Log.i(TAG, "Connected to '" + device.getName() + "'");

        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        closeBtSocket();
        setState(STATE_NONE);
    }

    private synchronized void closeBtSocket() {
        if (mBtSocket == null)
            return;

        try {
            mBtSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mBtSocket = null;
        mBtInputStream = null;
        mBtOutputStream = null;
    }

    private void connectionFailed() {
        setState(STATE_NONE);
        Log.e(TAG, "Connection failed");
    }

    private void connectionLost() {
        setState(STATE_NONE);
        Log.e(TAG, "Connection lost");
    }

    public void setMotorSpeed(byte port, byte power) {
        setMotorSpeed(port, power, MOTOR_REG_MODE_NONE);
    }

    public void setMotorSpeed(byte port, byte power, byte regulation) {
        if ((port < 0 || port > 2) && port != (byte) 0xFF)
            throw new IllegalArgumentException("port");
        if (power < -100 || power > 100)
            throw new IllegalArgumentException("power");
        if (regulation < 0 || regulation > 2)
            throw new IllegalArgumentException("regulation");

        byte[] data = {
                (byte) 0x80,           // command type
                0x04,                  // command
                port,                  // motor output port
                power,                 // motor power set point
                0x07,                  // motor mode
                regulation,            // regulation mode
                0x00,                  // turn ratio
                0x20,                  // run state
                0x00, 0x00, 0x00, 0x00 // tacho limit (LSB first)
        };

        Log.v(TAG, String.format("Setting motor %d to speed %d, regulation: %d",
                port, power, regulation));
        sendPacket(data);
    }

    public void setSensorType(byte port, byte type, byte mode) {
        if (port < 0 || port > 3)
            throw new IllegalArgumentException("port");
        if (type < 0x00 || type > 0x0C)
            throw new IllegalArgumentException("type");
        if ((mode & 0x1F) != 0)
            throw new IllegalArgumentException("mode");

        byte[] data = {
                (byte) 0x80, // command type
                0x05,        // command
                port,        // sensor port
                type,        // sensor type
                mode         // sensor mode
        };

        Log.v(TAG, String.format("Setting sensor %d to type %#X, mode: %#X",
                port, type, mode));
        sendPacket(data);
    }

    public NxtSensorReturnPackage readSensor(byte port) {
        if (port < 0 || port > 3)
            throw new IllegalArgumentException("port");

        byte[] data = {
                (byte) 0x80, // command type
                0x07,        // command
                port         // sensor port
        };

        Log.v(TAG, String.format("Requesting sensor value on port %d",
                port));

        sendPacket(data);

        byte buffer[] = new byte[16];
        try {
            mBtInputStream.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            connectionLost();
            return null;
        }

        if (buffer[0] != 0x02 || buffer[1] != 0x07)
            return null;

        return new NxtSensorReturnPackage(buffer);
    }

    private void sendPacket(byte[] out) {
        if (mState != STATE_CONNECTED) {
            return;
        }
        int len = out.length;
        try {
            mBtOutputStream.write(len & 0xFF);          // message length LSB
            mBtOutputStream.write((len & 0xFF00) >> 8); // message length MSB
            mBtOutputStream.write(out);
        } catch (IOException e) {
            e.printStackTrace();
            connectionLost();
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothDevice mmDevice;
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
        }

        public void run() {
            setName("ConnectThread");
            mAdapter.cancelDiscovery();

            try {
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                mmSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    // This is a workaround that reportedly helps on some older devices like HTC Desire, where using
                    // the standard createRfcommSocketToServiceRecord() method always causes connect() to fail.
                    Method method = mmDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                    mmSocket = (BluetoothSocket) method.invoke(mmDevice, Integer.valueOf(1));
                    mmSocket.connect();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    connectionFailed();
                    try {
                        mmSocket.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    return;
                }
            }

            synchronized (NxtTalker.this) {
                mConnectThread = null;
            }

            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                if (mmSocket != null) {
                    mmSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
