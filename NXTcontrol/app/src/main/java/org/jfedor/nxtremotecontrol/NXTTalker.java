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

package org.jfedor.nxtremotecontrol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cleenr.com.nxtcontrol.MainActivity;

public class NXTTalker {
    public static final String TAG = NXTTalker.class.getSimpleName();

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
    public static final byte SENSOR_MODE_SLOPEMASK = 0x1F;
    public static final byte SENSOR_MODE_MODEMASK = (byte) 0xE0;

    private int mState;
    private Handler mHandler;
    private BluetoothAdapter mAdapter;

    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    public NXTTalker(Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;
        setState(STATE_NONE);
    }

    public synchronized int getState() {
        return mState;
    }

    private synchronized void setState(int state) {
        mState = state;
        if (mHandler != null) {
            mHandler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
        }
    }

    public synchronized void setHandler(Handler handler) {
        mHandler = handler;
    }

    public synchronized void connect(BluetoothDevice device) {
        Log.i(TAG, "Connecting to device '" + device.getName() + "'");

        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        Log.i(TAG, "Connected to '" + device.getName() + "'");

        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        setState(STATE_NONE);
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
                0x0C, 0x00,            // command length (LSB first)
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

        Log.v(TAG, "Setting motor " + Byte.toString(port) +
                " to speed " + Byte.toString(power) +
                ", regulation: " + Byte.toString(regulation));
        write(data);
    }

    public void setSensorType(byte port, byte mode, byte type)
    {
        write(new byte[] {
                0x00,
                0x05,

        });
    }

    private void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (mState != STATE_CONNECTED) {
                return;
            }
            r = mConnectedThread;
        }
        r.write(out);
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

            synchronized (NXTTalker.this) {
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

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    Log.v(TAG, "Read " + Integer.toString(bytes) + " bytes from device");
                } catch (IOException e) {
                    e.printStackTrace();
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
