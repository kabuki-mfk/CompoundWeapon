package com.github.kabuki.compoundweapon.client.model;

import org.apache.commons.lang3.tuple.Pair;

public class VariantMapper {
    private final int meta;
    private final Pair<String, CustomResourceLocation> variant;

    public VariantMapper(int metadata, String variantName, String path, ModelType modelType) {
        this(metadata, Pair.of(variantName, new CustomResourceLocation(path, modelType)));
    }

    public VariantMapper(int metadata, Pair<String, CustomResourceLocation> variant)
    {
        this.meta = metadata;
        this.variant = variant;
    }

    public int getMeta() {
        return meta;
    }

    public String getVariantName() {
        return variant.getLeft();
    }

    public CustomResourceLocation getModelLocation() {
        return variant.getRight();
    }
}
