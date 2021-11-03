package com.github.jojo2357.rendering;

import com.github.jojo2357.Main;
import com.github.jojo2357.events.EventManager;
import com.github.jojo2357.events.EventTypes;
import com.github.jojo2357.events.GameTimes;
import com.github.jojo2357.util.Dimensions;
import com.github.jojo2357.util.Point;
import com.github.jojo2357.util.Texture;
import org.lwjgl.opengl.GL;

import java.io.Serializable;

public abstract class RenderableObject implements IRecievesEvent, Serializable {
    public static final long serialVersionUID = Main.serialVersionUID;

    protected transient Texture image;
    protected transient Dimensions imageSize;
    protected double rotation = 0;
    protected String fileNameUsed;

    public abstract void reloadTextures();

    protected RenderableObject() {

    }

    protected RenderableObject(String filename) {
        loadImage(filename);
        fileNameUsed = filename;
        registerListener(EventTypes.RenderEvent);
    }

    protected void loadImage(String filename) {
        GL.createCapabilities();
        this.image = Texture.create(filename);
        this.imageSize = new Dimensions(image.getWidth(), image.getHeight());
    }

    protected void render(Point location) {
        if (EventManager.currentPhase == GameTimes.SECOND_RENDER)
            ScreenManager.renderTexture(this.image, location, 1, this.rotation, this.imageSize);
    }

    public void dispose() {
        EventManager.disposeListener(this);
    }
}
