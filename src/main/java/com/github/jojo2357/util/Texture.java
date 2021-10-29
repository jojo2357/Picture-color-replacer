package com.github.jojo2357.util;

import org.lwjgl.BufferUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class Texture {
    public static final Texture load;
    public static final Texture upgradeButton;
    public static final Texture delete;
    public static final Texture trashCan;
    private static final HashMap<String, Texture> loadedAssets = new HashMap<>();

    static {
        load = create("MiscPics/Load");
        upgradeButton = create("MiscPics/Upgrade");
        delete = create("MiscPics/delete");
        trashCan = create("MiscPics/trash");
    }

    public final int id;
    public final int width;
    public final int heigth;
    public final Dimensions dimensions;

    protected Texture(String filename) {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer heigth = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);
        ByteBuffer data;
        Path textureFile;
        boolean loadedFromJar;
        if (loadedFromJar = !new File(filename).exists()) textureFile = Paths.get(System.getProperty("java.io.tmpdir"), "desktopboats").resolve(filename + ".png");
        else textureFile = Paths.get(filename);
        if (Paths.get(System.getProperty("java.io.tmpdir"), "desktopboats").resolve(filename + ".png").toFile().exists()) {
            //System.out.println("Found " + filename + " in temp dir");
        } else {
            //System.out.println("FNF " + filename + " in temp dir");
        }
        if (loadedFromJar)
            peelFromJar(filename, textureFile);

        data = stbi_load(textureFile.toString(), width, heigth, comp, 4);

        this.width = width.get();
        this.heigth = heigth.get();
        this.id = glGenTextures(); // generate texture name

        glBindTexture(GL_TEXTURE_2D, this.id);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.heigth, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);

        if (data == null) {
            Logger.doWarning("Something happened loading " + filename);
            Logger.doError(Thread.currentThread().getStackTrace());
            throw new NullPointerException("Failed to load " + filename);
        }
        stbi_image_free(data);

        if (loadedFromJar && Files.exists(textureFile)) {
            textureFile.toFile().delete();
        }

        dimensions = new Dimensions(this.width, this.heigth);
    }

    private void peelFromJar(String filename, Path textureFile) {
        try {
            if (Files.exists(textureFile))
                textureFile.toFile().delete();
            if (Files.notExists(textureFile)) {
                final Path forrestGameFolder = textureFile.getParent();

                Files.createDirectories(forrestGameFolder);
                Paths.get(System.getProperty("java.io.tmpdir"), "desktopboats").resolve(filename + ".png").toFile().createNewFile();

                try (final InputStream inputStream = Objects.requireNonNull(Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream("images/" + filename + ".png"));
                     final OutputStream outputStream = new FileOutputStream(Paths.get(System.getProperty("java.io.tmpdir"), "desktopboats").resolve(filename + ".png").toFile())) {
                    byte[] dater = new byte[1024];
                    int length;
                    while ((length = inputStream.read(dater)) > 0) {
                        outputStream.write(dater, 0, length);
                    }
                    Logger.doPrint("Creating texture file " + textureFile.toString());
                } catch (Exception e) {
                    StringBuilder sb = new StringBuilder();
                    for (StackTraceElement ste : Thread.currentThread().getStackTrace())
                        sb.append(ste.toString()).append('\n');
                    Logger.doError(sb.toString());
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Texture create(String assetName) {
        if (!loadedAssets.containsKey(assetName))
            loadedAssets.put(assetName, new Texture(assetName));
        return loadedAssets.get(assetName);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.heigth;
    }

    public int getId() {
        return this.id;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, this.id);
    }

    public int getPixel(Point point) {
        bind();
        byte[] pixels = new byte[width * heigth * 4];
        ByteBuffer buffer = ByteBuffer.allocateDirect(pixels.length);
        glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        buffer.get(pixels);

        return ((pixels[(int) (4 * ((point.getY() * width) + point.getX()))] & 0xFF) << 24) |
                ((pixels[(int) (4 * ((point.getY() * width) + point.getX())) + 1] & 0xFF) << 16) |
                ((pixels[(int) (4 * ((point.getY() * width) + point.getX())) + 2] & 0xFF) << 8) |
                (pixels[(int) (4 * ((point.getY() * width) + point.getX())) + 3] & 0xFF);
    }
}