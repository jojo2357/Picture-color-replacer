package com.github.jojo2357.util.fileutilis;

import com.github.jojo2357.util.PixelData;
import com.github.jojo2357.util.Point;
import com.github.jojo2357.util.Texture;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ImageObject extends Texture {
    private final File location;
    private final byte[] initialData;
    public ByteBuffer otherText;
    public ByteBuffer startingData;
    private byte[] tempOther;

    public HashMap<PixelData, PixelData> transformations = new HashMap<>();

    public static HashMap<PixelData, PixelData> generateMapping(ImageObject selectedForMapping, ImageObject image) {
        if (selectedForMapping.initialData.length != image.initialData.length)
            return null;
        final HashMap<PixelData, PixelData> out = new HashMap<>();
        for (int i = 0; i < selectedForMapping.initialData.length; i += 4) {
            PixelData temp = new PixelData(selectedForMapping.initialData[i], selectedForMapping.initialData[i + 1], selectedForMapping.initialData[i + 2], selectedForMapping.initialData[i + 3], new Point(i / 4 % selectedForMapping.width, i / 4 / selectedForMapping.width));
            PixelData tempOther = new PixelData(image.initialData[i], image.initialData[i + 1], image.initialData[i + 2], image.initialData[i + 3], new Point(i / 4 % image.width, i / 4 / image.width));
            PixelData holder;
            if ((holder = out.put(temp.copy(), tempOther.copy())) != null && !holder.equals(tempOther))
                return null;
        }
        return out;
    }

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
        return ((tempOther[(int) (4 * (pixelFromScreenCoord.getX() + pixelFromScreenCoord.getY() * this.width))] & 0xFF) << 24) |
                ((tempOther[1 + (int) (4 * (pixelFromScreenCoord.getX() + pixelFromScreenCoord.getY() * this.width))] & 0xFF) << 16) |
                ((tempOther[2 + (int) (4 * (pixelFromScreenCoord.getX() + pixelFromScreenCoord.getY() * this.width))] & 0xFF) << 8) |
                ((tempOther[3 + (int) (4 * (pixelFromScreenCoord.getX() + pixelFromScreenCoord.getY() * this.width))] & 0xFF));
    }

    public void resetChanges() {
        transformations.clear();
        tempOther = Arrays.copyOf(initialData, initialData.length);
        otherText = BufferUtils.createByteBuffer(tempOther.length);
        otherText.put(tempOther);
        otherText.flip();
    }

    public void updateReplacement() {
        for (Map.Entry<PixelData, PixelData> entry : transformations.entrySet()) {
            updateReplacement(entry.getKey(), entry.getValue());
        }
    }

    public void updateReplacement(PixelData startingData, PixelData finalData) {
        updateCopy(startingData, finalData);
        if (transformations.containsKey(startingData) && transformations.get(startingData).equals(finalData))
            return;
        if (!startingData.equals(finalData))
            transformations.put(startingData, finalData);
        else transformations.remove(startingData);
    }

    public String getName() {
        return location.getName();
    }

    public String getName(String find, String replace) {
        String out = location.getName();
        if (find.equals(""))
            return out;
        if (!replace.contains(find))
            while (out.contains(find))
                out = out.replace(find, replace);
        else out = out.replace(find, replace);
        return out;
    }

    public boolean saveToFile(String startingFind, String endingFind) {
        try {
            BufferedImage image = new BufferedImage(width, super.heigth, BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < super.heigth; y++) {
                    int i = (x + (width * y)) * 4;
                    byte r = tempOther[y * width * 4 + x * 4];//buffer.get(i) & 0xFF;
                    byte g = tempOther[y * width * 4 + x * 4 + 1];//buffer.get(i + 1) & 0xFF;
                    byte b = tempOther[y * width * 4 + x * 4 + 2];//buffer.get(i + 2) & 0xFF;
                    byte a = tempOther[y * width * 4 + x * 4 + 3];//buffer.get(i + 2) & 0xFF;
                    image.setRGB(x, y, ((a << 24) & 0xFF000000) | ((r << 16) & 0xFF0000) | ((g << 8) & 0xFF00) | (b & 0xFF));
                }
            }

            return ImageIO.write(image, "png", Paths.get(location.getParent(), getName(startingFind, endingFind)).toFile());
        } catch (Exception e) {
            return false;
        }
    }

    /*@Override
    public int hashCode() {
        return Arrays.hashCode(initialData) ^ Arrays.hashCode(tempOther);
    }*/
}
