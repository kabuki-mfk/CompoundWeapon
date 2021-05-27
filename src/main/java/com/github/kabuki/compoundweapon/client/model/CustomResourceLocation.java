package com.github.kabuki.compoundweapon.client.model;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import java.io.File;
import java.util.Objects;

public class CustomResourceLocation extends ModelResourceLocation {
    public static File modelDir;
    private final ModelType modelType;
    private final String filePath;

    public CustomResourceLocation(String path, ModelType type) {
        super(modelDir.getPath(), path);
        this.modelType = type;
        this.filePath = path;
    }

    public ModelType getModelType() {
        return modelType;
    }

    public String getLocation()
    {
        return modelDir.getPath() + File.separator + this.filePath;
    }

    @Override
    public String toString() {
        return getLocation();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CustomResourceLocation)) return false;
        if (this == o) return true;
        CustomResourceLocation that = (CustomResourceLocation) o;
        return modelType == that.modelType && that.filePath.equals(filePath);
    }

    @Override
    public String getPath() {
        return filePath;
    }

    @Override
    public String getNamespace() {
        return modelDir.getPath();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), modelType, filePath);
    }
}
