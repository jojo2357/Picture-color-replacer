package com.github.jojo2357.util.fileutilis;

import com.github.jojo2357.util.PixelData;
import com.github.jojo2357.util.Point;
import com.github.jojo2357.util.Texture;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;

public class ImageObject extends Texture {
    private final File location;
    private final byte[] initialData;
    public ByteBuffer otherText;
    public ByteBuffer startingData;
    private byte[] tempOther;

    public ImageObject(File file) {
        super(file.getAbsolutePath());
        location = file;
        initialData = super.getRawArray();
        otherText = super.getRawBuffer();

        startingData = BufferUtils.createByteBuffer(initialData.length);
        startingData.put(initialData);
        startingData.flip();
        tempOther = Arrays.copyOf(initialData, initialData.length);
    }

    public void updateCopy(PixelData find, PixelData replace) {
        for (int i = 0; i < initialData.length; i += 4) {
            if ((initialData[i] & 0xFF) == find.getR() && (initialData[i + 1] & 0xFF) == find.getG()
            && (initialData[i + 2] & 0xFF) == find.getB()) {
                tempOther[i] = (byte) replace.getR();
                tempOther[i + 1] = (byte) replace.getG();
                tempOther[i + 2] = (byte) replace.getB();
            }
        }
        otherText = BufferUtils.createByteBuffer(tempOther.length);
        otherText.put(tempOther);
        otherText.flip();
    }

    public int getFinalPixel(Point pixelFromScreenCoord) {
        return ((tempOther[(int)(4 * (pixelFromScreenCoord.getX() + pixelFromScreenCoord.getY() * this.width))] & 0xFF) << 24) |
                ((tempOther[1 + (int)(4 * (pixelFromScreenCoord.getX() + pixelFromScreenCoord.getY() * this.width))] & 0xFF) << 16) |
                ((tempOther[2 + (int)(4 * (pixelFromScreenCoord.getX() + pixelFromScreenCoord.getY() * this.width))] & 0xFF) << 8) |
                ((tempOther[3 + (int)(4 * (pixelFromScreenCoord.getX() + pixelFromScreenCoord.getY() * this.width))] & 0xFF));
    }
}
