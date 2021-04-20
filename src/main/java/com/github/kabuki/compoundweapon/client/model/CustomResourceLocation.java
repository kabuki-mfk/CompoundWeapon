package com.github.kabuki.compoundweapon.client.model;

import net.minecraft.util.ResourceLocation;

public class CustomResourceLocation extends ResourceLocation {

    private final ModelType modelType;
    public CustomResourceLocation(String path, ModelType type) {
        super("./mods/CompoundWeapon/Custom/Model", path);
        this.modelType = type;
    }

    public ModelType getModelType() {
        return modelType;
    }
}
