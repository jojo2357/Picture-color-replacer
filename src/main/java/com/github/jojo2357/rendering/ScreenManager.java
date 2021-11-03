package com.github.jojo2357.rendering;

import com.github.jojo2357.Main;
import com.github.jojo2357.events.EventManager;
import com.github.jojo2357.events.GameTimes;
import com.github.jojo2357.events.events.KeyInputEvent;
import com.github.jojo2357.events.events.MouseInputEvent;
import com.github.jojo2357.rendering.typeface.Colors;
import com.github.jojo2357.rendering.typeface.TextRenderer;
import com.github.jojo2357.util.Dimensions;
import com.github.jojo2357.util.Point;
import com.github.jojo2357.util.Texture;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.newByteChannel;
import static java.nio.file.Paths.get;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;

public class ScreenManager {
    private static final double[] x = new double[1];
    private static final double[] y = new double[1];
    public static Dimensions windowSize = new Dimensions(1000, 700);
    public static long window;
    public static MouseInputEvent lastPostedMouseEvent = new MouseInputEvent(), lastMouseEvent = new MouseInputEvent();
    public static KeyInputEvent lastPostedKeyEvent = new KeyInputEvent((char) 0, 0), lastKeyEvent = new KeyInputEvent((char) 0, 0);
    private static final List<Integer> activeModifiers = new ArrayList<>();
    private static final double rot = 0;
    private static final float zoom = 1;
    private static double scrolls = 0;

    public static void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        //System.out.println("Hello LWJGL " + getVersion() + "!");

        System.out.print("Starting glfw init...        \r");
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE);

        System.out.print("Creating window...        \r");

        window = glfwCreateWindow(windowSize.getWidth(), windowSize.getHeight(), "IRT", NULL, NULL);
        if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(window,
                (window, key, scancode, action, mods) -> {
                    KeyInputEvent.updateModifications(mods);
                    if (action == GLFW_PRESS || action == GLFW_REPEAT)
                        lastKeyEvent = new KeyInputEvent((char) key, mods);
                }
        );

        System.out.print("Placing window...        \r");
        try (MemoryStack stack = stackPush()) {
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window, (vidmode.width() - windowSize.getWidth()) / 2, (vidmode.height() - windowSize.getHeight()) / 2);
        } // the stack frame is popped automatically

        glfwSetScrollCallback(window, (long window, double xoffset, double yoffset) -> doMouseWheel(yoffset));

        System.out.print("Showing window...          \r");
        glfwShowWindow(window);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        //glfwSwapInterval(1);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        //glClearColor(32 / 255f, 32 / 255f, 32 / 255f, 0);
        glClearColor(1, 1, 1, 0);
        System.out.print("Loading static textures...        \r");

        //setIcon("BoatImages/RightFacingBoat");

        glfwSetWindowSizeCallback(window, (window, width, height) -> {
                    windowSize = new Dimensions(width, height);
                    glViewport(0, 0, width, height);
                }
        );
    }

    private static void doMouseWheel(double vel) {
        scrolls += vel;
    }

    public static void setIcon(String path) throws RuntimeException {
        IntBuffer w = memAllocInt(1);
        IntBuffer h = memAllocInt(1);
        IntBuffer comp = memAllocInt(1);
        // Icons
        {
            ByteBuffer icon16;
            ByteBuffer icon32;
            try {
                icon16 = ioResourceToByteBuffer(path, 2048);
                icon32 = ioResourceToByteBuffer(path, 4096);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            try (GLFWImage.Buffer icons = GLFWImage.malloc(2)) {
                ByteBuffer pixels16 = STBImage.stbi_load_from_memory(icon16, w, h, comp, 4);
                icons.position(0).width(w.get(0)).height(h.get(0)).pixels(pixels16);

                ByteBuffer pixels32 = STBImage.stbi_load_from_memory(icon32, w, h, comp, 4);
                icons.position(1).width(w.get(0)).height(h.get(0)).pixels(pixels32);

                icons.position(0);
                glfwSetWindowIcon(window, icons);
                STBImage.stbi_image_free(pixels32);
                STBImage.stbi_image_free(pixels16);
            }
        }
        memFree(comp);
        memFree(h);
        memFree(w);
    }

    /**
     * Reads the specified resource and returns the raw data as a ByteBuffer.
     *
     * @param resource   the resource to read
     * @param bufferSize the initial buffer size
     * @return the resource data
     * @throws IOException if an IO error occurs
     */
    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = get(resource);
        if (isReadable(path)) {
            try (SeekableByteChannel fc = newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
                while (fc.read(buffer) != -1) ;
            }
        } else {
            try (
                    InputStream source = Objects.requireNonNull(Thread.currentThread()
                            .getContextClassLoader()
                            .getResourceAsStream("images/" + resource + ".png"));
                    ReadableByteChannel rbc = Channels.newChannel(source)
            ) {
                buffer = BufferUtils.createByteBuffer(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1)
                        break;
                    if (buffer.remaining() == 0)
                        buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                }
            }
        }
        buffer.flip();
        return buffer;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    public static boolean tick() {
        if (glfwGetWindowAttrib(window, GLFW_FOCUSED) == GLFW_FALSE)
            glfwWaitEventsTimeout(0.1);
        else
            glfwWaitEventsTimeout(Main.frameLength / 1000);

        if (lastKeyEvent != lastPostedKeyEvent) {
            EventManager.notify(lastKeyEvent);
            lastPostedKeyEvent = lastKeyEvent;
        }

        glfwGetCursorPos(window, x, y);
        Point currentMouse = new Point(x[0], y[0]);

        byte mouseButtonActions = (byte) 0;// release is index 0, press 1. left mouse button is actions & 1, right is & 2, middle is & 4

        for (int mouseChecker = 0; mouseChecker <= 2; mouseChecker++) {
            mouseButtonActions |= glfwGetMouseButton(window, mouseChecker) == 1 ? (byte) Math.pow(2, mouseChecker) : (byte) 0;
        }

        lastMouseEvent = (MouseInputEvent) lastPostedMouseEvent.copy();
        MouseInputEvent event = new MouseInputEvent(currentMouse, mouseButtonActions, (int) Math.round(scrolls));
        scrolls = 0;
        if (!event.equals(lastMouseEvent))
            EventManager.notify(event);
        lastPostedMouseEvent = ((MouseInputEvent) event.copy());

        glClear(GL_COLOR_BUFFER_BIT);// clear the framebuffer// swap the color buffers
        return glfwWindowShouldClose(window);
    }

    public static void renderTexture(Texture text, Point point, float sizeFactor, Dimensions dimensions) {
        switch (EventManager.currentPhase) {
            case FIRST_RENDER:
            case SECOND_RENDER:
            case THIRD_RENDER:
                renderTexture(text, point, sizeFactor, 0, dimensions);
                break;
            default:
                throw new IllegalStateException("attempted to render outside of render phase!");
        }
    }

    public static void drawBox(Point topLeft, Point bottomRight, Colors color) {
        drawBox(topLeft, bottomRight, color.R, color.G, color.B);
    }

    public static void drawBox(Point topLeft, Point bottomRight, int r, int g, int b) {
        glColor4f(r / 255f, g / 255f, b / 255f, 255);
        drawStuff(topLeft, bottomRight, GL_LINES);
        glColor4f(1f, 1f, 1f, 1f);
    }

    private static void drawStuff(Point topLeft, Point bottomRight, int glLines) {
        Point[] points = {new Point(convertToScreenCoord(topLeft.getX(), ScreenManager.windowSize.getWidth()), -convertToScreenCoord(topLeft.getY(), ScreenManager.windowSize.getHeight())), new Point(convertToScreenCoord(topLeft.getX(), ScreenManager.windowSize.getWidth()), -convertToScreenCoord(bottomRight.getY(), ScreenManager.windowSize.getHeight())), new Point(convertToScreenCoord(bottomRight.getX(), ScreenManager.windowSize.getWidth()), -convertToScreenCoord(bottomRight.getY(), ScreenManager.windowSize.getHeight())), new Point(convertToScreenCoord(bottomRight.getX(), ScreenManager.windowSize.getWidth()), -convertToScreenCoord(topLeft.getY(), ScreenManager.windowSize.getHeight())), new Point(convertToScreenCoord(topLeft.getX(), ScreenManager.windowSize.getWidth()), -convertToScreenCoord(topLeft.getY(), ScreenManager.windowSize.getHeight()))};
        glBegin(glLines);
        for (int i = 0; i < points.length - 1; i++) {
            glVertex2f(points[i].getX(), points[i].getY());
            glVertex2f(points[i + 1].getX(), points[i + 1].getY());
        }
        glEnd();
    }

    private static float convertToScreenCoord(float pointIn, float dimension) {
        return (2 * pointIn - dimension) / dimension;
    }

    public static void drawBoxFilled(Point topLeft, Point bottomRight, Colors color) {
        drawBoxFilled(topLeft, bottomRight, color.R, color.G, color.B);
    }

    public static void drawBoxFilled(Point topLeft, Point bottomRight, int r, int g, int b) {
        drawBoxFilled(topLeft, bottomRight, r, g, b, 255);
    }

    public static void drawBoxFilled(Point topLeft, Point bottomRight, int r, int g, int b, int a) {
        glColor4f(r / 255f, g / 255f, b / 255f, a / 255f);
        drawStuff(topLeft, bottomRight, GL_TRIANGLE_FAN);
        glColor4f(1f, 1f, 1f, 1f);
    }

    public static void finishRender() {
        glfwSwapBuffers(window);
    }

    public static void drawRedX() {
        renderTexture(Texture.redX, new Point(0, windowSize.getHeight()).step(Texture.redX.width / 2, -Texture.redX.heigth / 2));
    }

    public static void renderTexture(Texture text, Point point) {
        renderTexture(text, point, 1);
    }

    public static void renderTexture(Texture text, Point point, float sizeFactor) {
        renderTexture(text, point, sizeFactor, 0, new Dimensions(text.getWidth(), text.getHeight()));
    }

    public static void renderByteArray(ByteBuffer data, ByteBuffer resetData, int width, int height, Point point, float sizeFactor, double rotation, Dimensions specialDimensions) {
        double offset = Math.toDegrees(Math.atan(specialDimensions.getHeight() / (double) specialDimensions.getWidth())) - 45;

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);

        glEnable(GL_TEXTURE_2D);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(zoom * convertToScreenCoord(myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.sin((Math.PI / 180.0) * (-45 + rotation + offset)) + point.getX()), windowSize.getWidth()), zoom * -(float) convertToScreenCoord(myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.cos((Math.PI / 180.0) * (-45 + rotation + offset)) + point.getY()), windowSize.getHeight()));
        glTexCoord2f(1f, 0);
        glVertex2f(zoom * convertToScreenCoord(myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.sin((Math.PI / 180.0) * (45 + rotation - offset)) + point.getX()), windowSize.getWidth()), zoom * -(float) convertToScreenCoord(myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.cos((Math.PI / 180.0) * (45 + rotation - offset)) + point.getY()), windowSize.getHeight()));
        glTexCoord2f(1f, -1f);
        glVertex2f(zoom * convertToScreenCoord(myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.sin((Math.PI / 180.0) * (135 + rotation + offset)) + point.getX()), windowSize.getWidth()), zoom * -(float) convertToScreenCoord(myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.cos((Math.PI / 180.0) * (135 + rotation + offset)) + point.getY()), windowSize.getHeight()));
        glTexCoord2f(0, -1f);
        glVertex2f(zoom * convertToScreenCoord(myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.sin((Math.PI / 180.0) * (225 + rotation - offset)) + point.getX()), windowSize.getWidth()), zoom * -(float) convertToScreenCoord(myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.cos((Math.PI / 180.0) * (225 + rotation - offset)) + point.getY()), windowSize.getHeight()));
        glEnd();
        glDisable(GL_TEXTURE_2D);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, resetData);
    }

    public static void renderTexture(Texture text, Point point, float sizeFactor, double rotation, Dimensions specialDimensions) {
        if (EventManager.currentPhase != GameTimes.FIRST_RENDER && EventManager.currentPhase != GameTimes.SECOND_RENDER && EventManager.currentPhase != GameTimes.THIRD_RENDER) {
            throw new IllegalStateException("attempted to render outside of render phase!");
        }
        text.bind();
        //point = point.add(new Point(text.dimensions.getWidth()/2, text.dimensions.getHeight()/2));
        double offset = Math.toDegrees(Math.atan(specialDimensions.getHeight() / (double) specialDimensions.getWidth())) - 45;

        glEnable(GL_TEXTURE_2D);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(zoom * convertToScreenCoord(myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.sin((Math.PI / 180.0) * (-45 + rotation + offset)) + point.getX()), windowSize.getWidth()), zoom * -(float) convertToScreenCoord(myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.cos((Math.PI / 180.0) * (-45 + rotation + offset)) + point.getY()), windowSize.getHeight()));
        glTexCoord2f(1f, 0);
        glVertex2f(zoom * convertToScreenCoord(myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.sin((Math.PI / 180.0) * (45 + rotation - offset)) + point.getX()), windowSize.getWidth()), zoom * -(float) convertToScreenCoord(myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.cos((Math.PI / 180.0) * (45 + rotation - offset)) + point.getY()), windowSize.getHeight()));
        glTexCoord2f(1f, -1f);
        glVertex2f(zoom * convertToScreenCoord(myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.sin((Math.PI / 180.0) * (135 + rotation + offset)) + point.getX()), windowSize.getWidth()), zoom * -(float) convertToScreenCoord(myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.cos((Math.PI / 180.0) * (135 + rotation + offset)) + point.getY()), windowSize.getHeight()));
        glTexCoord2f(0, -1f);
        glVertex2f(zoom * convertToScreenCoord(myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.sin((Math.PI / 180.0) * (225 + rotation - offset)) + point.getX()), windowSize.getWidth()), zoom * -(float) convertToScreenCoord(myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.cos((Math.PI / 180.0) * (225 + rotation - offset)) + point.getY()), windowSize.getHeight()));
        glEnd();
        glDisable(GL_TEXTURE_2D);
        /*Point a = new Point((float) myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.sin((Math.PI / 180.0) * (-45 + rotation + offset)) + point.getX() - ScreenManager.windowSize.getWidth()) / ScreenManager.windowSize.getWidth(), -(float) myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.cos((Math.PI / 180.0) * (-45 + rotation + offset)) + point.getY()  - ScreenManager.windowSize.getHeight()) / ScreenManager.windowSize.getHeight());
        Point b = new Point((float) myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.sin((Math.PI / 180.0) * (45 + rotation - offset)) + point.getX()   - ScreenManager.windowSize.getWidth()) / ScreenManager.windowSize.getWidth(), -(float) myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.cos((Math.PI / 180.0) * (45 + rotation - offset)) + point.getY() - ScreenManager.windowSize.getHeight()) / ScreenManager.windowSize.getHeight());
        Point c = new Point((float) myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.sin((Math.PI / 180.0) * (135 + rotation + offset)) + point.getX()  - ScreenManager.windowSize.getWidth()) / ScreenManager.windowSize.getWidth(), -(float) myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.cos((Math.PI / 180.0) * (135 + rotation + offset)) + point.getY()- ScreenManager.windowSize.getHeight()) / ScreenManager.windowSize.getHeight());
        Point d = new Point((float) myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.sin((Math.PI / 180.0) * (225 + rotation - offset)) + point.getX()  - ScreenManager.windowSize.getWidth()) / ScreenManager.windowSize.getWidth(), -(float) myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.cos((Math.PI / 180.0) * (225 + rotation - offset)) + point.getY() - ScreenManager.windowSize.getHeight()) / ScreenManager.windowSize.getHeight());
        System.out.println(a + " " + b + " " + c + " " + d);*/
    }

    private static float myRounder(double in) {
        if (Math.abs(in) % 1 < 0.01) return (float) Math.floor(in);
        if (Math.abs(in) % 1 > 0.99) return (float) Math.ceil(in);
        return (float) in;
    }

    public static void drawCircle(Point origin, float radius, int refinement, int r, int g, int b, int a) {
        glColor4f(r / 255f, g / 255f, b / 255f, a / 255f);
        drawCircle(origin, radius, refinement);
        glColor4f(255, 255, 255, 255);
    }

    public static void drawCircle(Point origin, float radius, int refinement) {
        refinement *= radius;
        double twoPiOnRefinement = Math.PI * 2 / refinement;
        glBegin(GL_POINTS);
        for (int i = 0; i <= refinement; i++) { //NUM_PIZZA_SLICES decides how round the circle looks.
            double angle = twoPiOnRefinement * i;
            glVertex2f(convertToScreenCoord(origin.getX() + (float) Math.cos(angle) * radius, windowSize.getWidth()), -convertToScreenCoord(origin.getY() + (float) Math.sin(angle) * radius, windowSize.getHeight()));
        }
        glEnd();
    }

    public static void drawLine(Point origin, Point destination, int r, int g, int b) {
        drawLine(origin, destination, r, g, b, 255);
    }

    public static void drawLine(Point origin, Point destination, int r, int g, int b, int a) {
        glColor4f(r, g, b, a);
        drawLine(origin, destination);
        glColor4f(255, 255, 255, 255);
    }

    public static void drawLine(Point origin, Point destination) {
        glBegin(GL_LINES);
        glVertex2f(convertToScreenCoord(origin.getX(), windowSize.getWidth()), -convertToScreenCoord(origin.getY(), windowSize.getHeight()));
        glVertex2f(convertToScreenCoord(destination.getX(), windowSize.getWidth()), -convertToScreenCoord(destination.getY(), windowSize.getHeight()));
        glEnd();
    }

    public static void drawGreenBox() {
        ScreenManager.drawBoxFilled(new Point(ScreenManager.windowSize.getWidth() * 2 - 200, ScreenManager.windowSize.getHeight() * 2 - 200), new Point(ScreenManager.windowSize.getWidth() * 2, ScreenManager.windowSize.getHeight() * 2), 0, 255, 0);
    }

    public static void renderTextureWithOverlay(Texture image, Point currentSpot, float size, Colors color) {
        glColor4f(color.R / 255f, color.G / 255f, color.B / 255f, 255);
        renderTexture(image, currentSpot, size);
        glColor4f(255, 255, 255, 255);
    }

    public static void drawBackground(int r, int g, int b) {
        drawBoxFilled(new Point(), new Point(windowSize), r, g, b);
    }

    public static void drawFadedBorder(int pixelSize, Colors color) {
        drawFadedBorder(pixelSize, color.R, color.G, color.B);
    }

    public static void drawFadedBorder(int pixelSize, int hex) {
        drawFadedBorder(pixelSize, (hex >> 16) & 0xFF, (hex >> 8) & 0xFF, (hex >> 0) & 0xFF);
    }

    public static void drawFadedBorder(int pixelSize, int r, int g, int b) {
        glColor4f(r / 255f, g / 255f, b / 255f, 255);
        drawFadedBorder(pixelSize);
        glColor4f(255, 255, 255, 255);
    }

    public static void drawFadedBorder(int pixelSize) {
        ScreenManager.renderTexture(Texture.border, new Point(ScreenManager.windowSize.getWidth() / 2, ScreenManager.windowSize.getHeight() - pixelSize / 2), 1, new Dimensions(ScreenManager.windowSize.getWidth(), pixelSize));
        ScreenManager.renderTexture(Texture.border, new Point(ScreenManager.windowSize.getWidth() / 2, pixelSize / 2), 1, 180, new Dimensions(ScreenManager.windowSize.getWidth(), pixelSize));
        ScreenManager.renderTexture(Texture.border, new Point(pixelSize / 2, ScreenManager.windowSize.getHeight() / 2), 1, 270, new Dimensions(ScreenManager.windowSize.getHeight(), pixelSize));
        ScreenManager.renderTexture(Texture.border, new Point(ScreenManager.windowSize.getWidth() - pixelSize / 2, ScreenManager.windowSize.getHeight() / 2), 1, 90, new Dimensions(ScreenManager.windowSize.getHeight(), pixelSize));
    }

    public static void drawBox(Point topLeft, Point bottomRight) {
        drawBox(topLeft, bottomRight, 255, 255, 255);
    }

    public static void drawRightBottomBox(String text, Colors... colors) {
        drawBoxFilled(new Point(windowSize).step(-100), new Point(windowSize), 40, 40, 40);
        drawBoxFilled(new Point(windowSize).step(-95), new Point(windowSize).step(-5), 48, 48, 48);
        TextRenderer.renderJustified(text, new Point(windowSize).step(-50), 1000, colors);
    }

    public static void renderTextureLimited(Texture text, Point point, Point topLeft, Dimensions size) {
        renderTextureLimited(text, point, 1, topLeft, size);
    }

    public static void renderTextureLimited(Texture text, Point point, float sizeFactor, Point topLeft, Dimensions size) {
        renderTextureLimited(text, point, sizeFactor, 0, new Dimensions(text.getWidth(), text.getHeight()), topLeft, size);
    }

    public static void renderTextureLimited(Texture text, Point point, float sizeFactor, double rotation, Dimensions specialDimensions, Point topLeft, Dimensions size) {
        //glViewport(0, 0, size.getWidth() / 2, size.getHeight() / 2);
        //glViewport((int)topLeft.getX(), (int)topLeft.getY(), size.getWidth(), size.getHeight());
        if (EventManager.currentPhase != GameTimes.FIRST_RENDER && EventManager.currentPhase != GameTimes.SECOND_RENDER && EventManager.currentPhase != GameTimes.THIRD_RENDER) {
            throw new IllegalStateException("attempted to render outside of render phase!");
        }
        text.bind();
        //point = point.add(new Point(text.dimensions.getWidth()/2, text.dimensions.getHeight()/2));
        double offset = Math.toDegrees(Math.atan(specialDimensions.getHeight() / (double) specialDimensions.getWidth())) - 45;

        float rightCoord = Math.max(0, Math.min(1f, 1 - ((point.getX() + sizeFactor * specialDimensions.getWidth() / 2) - (size.getWidth() + topLeft.getX())) / (sizeFactor * specialDimensions.getWidth()))),
                leftCoord = Math.max(0, Math.min(1f, (-(point.getX() - sizeFactor * specialDimensions.getWidth() / 2) + (topLeft.getX())) / (sizeFactor * specialDimensions.getWidth())));

        float rightCoordLimit = ((size.getWidth() + topLeft.getX())), leftCoordLimit = topLeft.getX();

        float upCoord = Math.max(0, Math.min(1f, 1 - ((point.getY() + sizeFactor * specialDimensions.getHeight() / 2) - (size.getHeight() + topLeft.getY())) / (sizeFactor * specialDimensions.getHeight()))),
                downCoord = Math.max(0, Math.min(1f, (-(point.getY() - sizeFactor * specialDimensions.getHeight() / 2) + (topLeft.getY())) / (sizeFactor * specialDimensions.getHeight())));

        float downCoordLimit = ((size.getHeight() + topLeft.getY())), upCoordLimit = topLeft.getY();

        glEnable(GL_TEXTURE_2D);
        glBegin(GL_QUADS);
        glTexCoord2f(leftCoord, upCoord);
        glVertex2f(zoom * convertToScreenCoord(getMiddleValue(leftCoordLimit, rightCoordLimit, myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.sin((Math.PI / 180.0) * (-45 + rotation + offset)) + point.getX())), windowSize.getWidth()), zoom * -(float) convertToScreenCoord(getMiddleValue(upCoordLimit, downCoordLimit, myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.cos((Math.PI / 180.0) * (-45 + rotation + offset)) + point.getY())), windowSize.getHeight()));
        glTexCoord2f(rightCoord, upCoord);
        glVertex2f(zoom * convertToScreenCoord(getMiddleValue(leftCoordLimit, rightCoordLimit, myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.sin((Math.PI / 180.0) * (45 + rotation - offset)) + point.getX())), windowSize.getWidth()), zoom * -(float) convertToScreenCoord(getMiddleValue(upCoordLimit, downCoordLimit, myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.cos((Math.PI / 180.0) * (45 + rotation - offset)) + point.getY())), windowSize.getHeight()));
        glTexCoord2f(rightCoord, downCoord);
        glVertex2f(zoom * convertToScreenCoord(getMiddleValue(leftCoordLimit, rightCoordLimit, myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.sin((Math.PI / 180.0) * (135 + rotation + offset)) + point.getX())), windowSize.getWidth()), zoom * -(float) convertToScreenCoord(getMiddleValue(upCoordLimit, downCoordLimit, myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.cos((Math.PI / 180.0) * (135 + rotation + offset)) + point.getY())), windowSize.getHeight()));
        glTexCoord2f(leftCoord, downCoord);
        glVertex2f(zoom * convertToScreenCoord(getMiddleValue(leftCoordLimit, rightCoordLimit, myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.sin((Math.PI / 180.0) * (225 + rotation - offset)) + point.getX())), windowSize.getWidth()), zoom * -(float) convertToScreenCoord(getMiddleValue(upCoordLimit, downCoordLimit, myRounder(sizeFactor * specialDimensions.getDiagonal() * Math.cos((Math.PI / 180.0) * (225 + rotation - offset)) + point.getY())), windowSize.getHeight()));
        glEnd();
        glDisable(GL_TEXTURE_2D);
        //glViewport(0, 0, windowSize.getWidth(), windowSize.getHeight());
    }

    private static float getMiddleValue(float a, float b, float c) {
        if (Math.max(a, c) >= b && b >= Math.min(a, c))
            return b;
        if (Math.max(b, c) >= a && a >= Math.min(b, c))
            return a;
        if (Math.max(a, b) >= c && c >= Math.min(a, b))
            return c;
        throw new IllegalStateException("This should never happen");
    }

    public static void renderTextureLimited(Texture text, Point point, float sizeFactor, Dimensions dimensions, Point topLeft, Dimensions size) {
        switch (EventManager.currentPhase) {
            case FIRST_RENDER:
            case SECOND_RENDER:
            case THIRD_RENDER:
                renderTexture(text, point, sizeFactor, 0, dimensions);
                break;
            default:
                throw new IllegalStateException("attempted to render outside of render phase!");
        }
    }
}
