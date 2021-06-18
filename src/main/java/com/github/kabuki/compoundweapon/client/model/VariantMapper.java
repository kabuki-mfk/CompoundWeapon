package com.github.kabuki.compoundweapon.client.model;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Objects;

public class VariantMapper {
    private final int meta;
    private final Pair<String, ModelResourceLocation> variant;

    public VariantMapper(int metadata, String variantName, String path, ModelType modelType) {
        this(metadata, Pair.of(variantName, new CustomResourceLocation(path, modelType)));
    }

    public VariantMapper(int metadata, String variantName, net.minecraft.util.ResourceLocation resourceLocation) {
        this(metadata, Pair.of(variantName, new ModelResourceLocation(resourceLocation, variantName)));
    }

    public VariantMapper(int metadata, Pair<String, ModelResourceLocation> variant)
    {
        this.meta = metadata;
        this.variant = Objects.requireNonNull(variant);
    }

    public int getMeta() {
        return meta;
    }

    public String getVariantName() {
        return variant.getLeft();
    }

    public ModelResourceLocation getModelLocation() {
        return variant.getRight();
    }
}
