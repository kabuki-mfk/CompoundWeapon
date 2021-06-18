package com.github.kabuki.compoundweapon.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
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
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.*;
import java.util.function.Function;

public class TransformModels implements IModel {

    private final Map<ItemCameraTransforms.TransformType, IModel> transformModelMap;
    private final IModel defaultModel;

    public TransformModels(IModel defaultModel, Map<ItemCameraTransforms.TransformType, IModel> transformModelMap) {
        this.transformModelMap = ImmutableMap.copyOf(transformModelMap);
        this.defaultModel = defaultModel;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        ImmutableMap.Builder<ItemCameraTransforms.TransformType, IBakedModel> builder = new ImmutableMap.Builder<>();
        boolean flag = state instanceof TransformModels.ModelState;

        for(Map.Entry<ItemCameraTransforms.TransformType, IModel> entry : transformModelMap.entrySet())
        {
            builder.put(entry.getKey(), entry.getValue().bake(
                    flag ? ((ModelState)state).getStateFormType(entry.getKey()) : state,
                    format, bakedTextureGetter));
        }
        return new BakedTransformModels(defaultModel.bake(flag ? ((ModelState)state).def : state,
                format, bakedTextureGetter), builder.build());
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        Collection<ResourceLocation> textures = Lists.newLinkedList();
        for(IModel model : transformModelMap.values())
        {
            textures.addAll(model.getTextures());
        }
        textures.addAll(defaultModel.getTextures());
        return textures;
    }

    @Override
    public IModel retexture(ImmutableMap<String, String> textures) {
        ImmutableMap.Builder<ItemCameraTransforms.TransformType, IModel> builder = new ImmutableMap.Builder<>();
        for(Map.Entry<ItemCameraTransforms.TransformType, IModel> entry : transformModelMap.entrySet())
        {
            builder.put(entry.getKey(), entry.getValue().retexture(textures));
        }
        return new TransformModels(defaultModel.retexture(textures), builder.build());
    }

    @Override
    public IModel process(ImmutableMap<String, String> customData) {
        ImmutableMap.Builder<ItemCameraTransforms.TransformType, IModel> builder = new ImmutableMap.Builder<>();
        for(Map.Entry<ItemCameraTransforms.TransformType, IModel> entry : transformModelMap.entrySet())
        {
            builder.put(entry.getKey(), entry.getValue().process(customData));
        }
        return new TransformModels(defaultModel.process(customData), builder.build());
    }

    @Override
    public IModel gui3d(boolean value) {
        ImmutableMap.Builder<ItemCameraTransforms.TransformType, IModel> builder = new ImmutableMap.Builder<>();
        for(Map.Entry<ItemCameraTransforms.TransformType, IModel> entry : transformModelMap.entrySet())
        {
            builder.put(entry.getKey(), entry.getValue().gui3d(value));
        }
        return new TransformModels(defaultModel.gui3d(value), builder.build());
    }

    @Override
    public IModel smoothLighting(boolean value) {
        ImmutableMap.Builder<ItemCameraTransforms.TransformType, IModel> builder = new ImmutableMap.Builder<>();
        for(Map.Entry<ItemCameraTransforms.TransformType, IModel> entry : transformModelMap.entrySet())
        {
            builder.put(entry.getKey(), entry.getValue().smoothLighting(value));
        }
        return new TransformModels(defaultModel.smoothLighting(value), builder.build());
    }

    @Override
    public IModel uvlock(boolean value) {
        ImmutableMap.Builder<ItemCameraTransforms.TransformType, IModel> builder = new ImmutableMap.Builder<>();
        for(Map.Entry<ItemCameraTransforms.TransformType, IModel> entry : transformModelMap.entrySet())
        {
            builder.put(entry.getKey(), entry.getValue().uvlock(value));
        }
        return new TransformModels(defaultModel.uvlock(value), builder.build());
    }

    @Override
    public IModelState getDefaultState() {
        return new CustomModel.CustomModelState();
    }

    public static class BakedTransformModels implements IBakedModel {
        private final IBakedModel defaultModel;
        private final Map<ItemCameraTransforms.TransformType, IBakedModel> transformModelMap;

        public BakedTransformModels(IBakedModel defaultModel, Map<ItemCameraTransforms.TransformType, IBakedModel> transformModelMap) {
            this.defaultModel = defaultModel;
            this.transformModelMap = transformModelMap;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
            return defaultModel.getQuads(state, side, rand);
        }

        @Override
        public boolean isAmbientOcclusion() {
            return defaultModel.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return defaultModel.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer() {
            return defaultModel.isBuiltInRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return defaultModel.getParticleTexture();
        }

        @Override
        public ItemOverrideList getOverrides() {
            return defaultModel.getOverrides();
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
            IBakedModel bakedModel = transformModelMap.get(cameraTransformType);
            if(bakedModel == null) return defaultModel.handlePerspective(cameraTransformType);
            return bakedModel.handlePerspective(cameraTransformType);
        }
    }

    public static class ModelState implements IModelState {
        private final IModelState def;
        private final Map<ItemCameraTransforms.TransformType, IModelState> modelStateMap;

        public ModelState(IModelState defaultState, Map<ItemCameraTransforms.TransformType, IModelState> map) {
            def = defaultState;
            modelStateMap = ImmutableMap.copyOf(map);
        }

        public static ModelState fromModelInfo(ModelInfo info) {
            EnumMap<ItemCameraTransforms.TransformType, IModelState> map = new EnumMap<>(ItemCameraTransforms.TransformType.class);
            for(ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values())
            {
                ModelProperties properties = info.getModelProperties(type);
                if(properties == null) continue;
                map.put(type, new CustomModel.CustomModelState(properties.getTransforms()));
            }
            return new ModelState(new CustomModel.CustomModelState(info.getNormalProperties().getTransforms()), map);
        }

        public IModelState getStateFormType(ItemCameraTransforms.TransformType type) { return modelStateMap.get(type); }

        @Override
        public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part) {

            if(part.isPresent() && part.get() instanceof ItemCameraTransforms.TransformType) {
                return modelStateMap
                        .getOrDefault((ItemCameraTransforms.TransformType) part.get(), TRSRTransformation.identity())
                        .apply(part);
            }
            return Optional.empty();
        }
    }
}
