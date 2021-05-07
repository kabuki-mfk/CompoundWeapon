package com.github.kabuki.compoundweapon.client.model;

import net.minecraft.util.ResourceLocation;

import java.io.File;

public class CustomResourceLocation extends ResourceLocation {
    public static File modelDir;
    private final ModelType modelType;
    public CustomResourceLocation(String path, ModelType type) {
        super(modelDir.getPath(), path);
        this.modelType = type;
    }

    public ModelType getModelType() {
        return modelType;
    }

    public String getLocation()
    {
        return this.namespace + ":" + this.path;
    }

    @Override
    public String toString() {
        return "CustomResourceLocation{" +
                "location=" + getLocation() +
                "modelType=" + modelType +
                '}';
    }
}
