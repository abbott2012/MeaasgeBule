package com.bql.utils;

import java.io.UnsupportedEncodingException;

/**
 * Created by Cyarie on 2016/1/4.
 */
public class BaiduBase64 {

    private static final byte[] MAP = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74,
            75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97,
            98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111,
            112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50,
            51, 52, 53, 54, 55, 56, 57, 43, 47};

    public static byte[] decode(byte[] paramArrayOfByte) {
        return decode(paramArrayOfByte, paramArrayOfByte.length);
    }

    public static byte[] decode(byte[] paramArrayOfByte, int paramInt) {
        int i = paramInt / 4 * 3;
        if (i == 0)
            return new byte[0];
        byte[] arrayOfByte1 = new byte[i];
        int j = 0;
        int k;
        while (true) {
            k = paramArrayOfByte[(paramInt - 1)];
            if ((k != 10) && (k != 13) && (k != 32) && (k != 9)) {
                if (k != 61)
                    break;
                j++;
            }
            paramInt--;
        }
        int m = 0;
        int n = 0;
        int i1 = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < paramInt; i3++) {
            k = paramArrayOfByte[i3];
            if ((k != 10) && (k != 13) && (k != 32) && (k != 9)) {
                if ((k >= 65) && (k <= 90))
                    i1 = k - 65;
                else if ((k >= 97) && (k <= 122))
                    i1 = k - 71;
                else if ((k >= 48) && (k <= 57))
                    i1 = k + 4;
                else if (k == 43)
                    i1 = 62;
                else if (k == 47)
                    i1 = 63;
                else
                    return null;
                i2 = i2 << 6 | (byte) i1;
                if (n % 4 == 3) {
                    arrayOfByte1[(m++)] = ((byte) ((i2 & 0xFF0000) >> 16));
                    arrayOfByte1[(m++)] = ((byte) ((i2 & 0xFF00) >> 8));
                    arrayOfByte1[(m++)] = ((byte) (i2 & 0xFF));
                }
                n++;
            }
        }
        if (j > 0) {
            i2 <<= 6 * j;
            arrayOfByte1[(m++)] = ((byte) ((i2 & 0xFF0000) >> 16));
            if (j == 1)
                arrayOfByte1[(m++)] = ((byte) ((i2 & 0xFF00) >> 8));
        }
        byte[] arrayOfByte2 = new byte[m];
        System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, m);
        return arrayOfByte2;
    }

    public static String encode(byte[] paramArrayOfByte, String paramString)
            throws UnsupportedEncodingException {
        int i = paramArrayOfByte.length * 4 / 3;
        i += i / 76 + 3;
        byte[] arrayOfByte = new byte[i];
        int j = 0;
        int m = 0;
        int n = paramArrayOfByte.length - paramArrayOfByte.length % 3;
        for (int k = 0; k < n; k += 3) {
            arrayOfByte[(j++)] = MAP[((paramArrayOfByte[k] & 0xFF) >> 2)];
            arrayOfByte[(j++)] = MAP[((paramArrayOfByte[k] & 0x3) << 4 | (paramArrayOfByte[(k + 1)] & 0xFF) >> 4)];
            arrayOfByte[(j++)] = MAP[((paramArrayOfByte[(k + 1)] & 0xF) << 2 | (paramArrayOfByte[(k + 2)] & 0xFF) >> 6)];
            arrayOfByte[(j++)] = MAP[(paramArrayOfByte[(k + 2)] & 0x3F)];
            if (((j - m) % 76 == 0) && (j != 0)) {
                arrayOfByte[(j++)] = 10;
                m++;
            }
        }
        switch (paramArrayOfByte.length % 3) {
            case 1:
                arrayOfByte[(j++)] = MAP[((paramArrayOfByte[n] & 0xFF) >> 2)];
                arrayOfByte[(j++)] = MAP[((paramArrayOfByte[n] & 0x3) << 4)];
                arrayOfByte[(j++)] = 61;
                arrayOfByte[(j++)] = 61;
                break;
            case 2:
                arrayOfByte[(j++)] = MAP[((paramArrayOfByte[n] & 0xFF) >> 2)];
                arrayOfByte[(j++)] = MAP[((paramArrayOfByte[n] & 0x3) << 4 | (paramArrayOfByte[(n + 1)] & 0xFF) >> 4)];
                arrayOfByte[(j++)] = MAP[((paramArrayOfByte[(n + 1)] & 0xF) << 2)];
                arrayOfByte[(j++)] = 61;
        }
        return new String(arrayOfByte, 0, j, paramString);
    }
}
