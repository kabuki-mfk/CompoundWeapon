package com.github.kabuki.compoundweapon.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class CustomModelWrapper implements IModel {
    private final IModel model;
    private final IModel guiModel;

    public CustomModelWrapper(IModel model, @Nullable IModel guiModel) {
        this.model = model;
        this.guiModel = guiModel == null ? model : guiModel;
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        Collection<ResourceLocation> textures = Sets.newLinkedHashSet();
        textures.addAll(model.getTextures());
        if(guiModel != model) textures.addAll(guiModel.getTextures());
        return textures;
    }

    @Override
    public IModel retexture(ImmutableMap<String, String> textures) {
        return new CustomModelWrapper(model.retexture(textures), guiModel.retexture(textures));
    }

    @Override
    public IModel process(ImmutableMap<String, String> customData) {
        return new CustomModelWrapper(model.process(customData), guiModel.process(customData));
    }

    @Override
    public IModel uvlock(boolean value) {
        return new CustomModelWrapper(model.uvlock(value), guiModel.uvlock(value));
    }

    @Override
    public IModel smoothLighting(boolean value) {
        return new CustomModelWrapper(model.smoothLighting(value), guiModel.smoothLighting(value));
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new BakedModelWrapper(model.bake(state, format, bakedTextureGetter), guiModel.bake(state, format, bakedTextureGetter));
    }

    @Override
    public IModelState getDefaultState() {
        return model.getDefaultState();
    }

    public static class BakedModelWrapper implements IBakedModel {
        private final IBakedModel model;
        private final IBakedModel guiModel;

        public BakedModelWrapper(IBakedModel model, IBakedModel guiModel) {
            this.model = model;
            this.guiModel = guiModel;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
            return model.getQuads(state, side, rand);
        }

        @Override
        public boolean isAmbientOcclusion() {
            return model.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return guiModel != null ? guiModel.isGui3d() : model.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer() {
            return model.isBuiltInRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return model.getParticleTexture();
        }

        @Override
        public ItemOverrideList getOverrides() {
            return model.getOverrides();
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
            if(cameraTransformType == ItemCameraTransforms.TransformType.GUI) return guiModel.handlePerspective(cameraTransformType);
            return model.handlePerspective(cameraTransformType);
        }
    }
}
