package com.github.kabuki.compoundweapon.client.model;

import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import java.io.File;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;

public class ModelProperties {
    private final ModelResourceLocation modelLocation;
    private final ModelResourceLocation guiModelLocation;
    private boolean hasOtherModel;
    private final EnumMap<ItemCameraTransforms.TransformType, TRSRTransformation> transformationMap;

    public ModelProperties(ModelResourceLocation modelLocation, @Nullable ModelResourceLocation guiModelLocation, @Nullable EnumMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms) {
        this.modelLocation = modelLocation;
        this.guiModelLocation = guiModelLocation;
        this.transformationMap = transforms == null ? Maps.newEnumMap(ItemCameraTransforms.TransformType.class) : transforms;
    }

    protected Map<ItemCameraTransforms.TransformType, TRSRTransformation> getTransforms() { return transformationMap; }

    public TRSRTransformation getTRSRTransformation(ItemCameraTransforms.TransformType type) {
        return transformationMap.get(type);
    }

    public boolean hasGuiModel()
    {
        return guiModelLocation != null;
    }

    public ModelResourceLocation getGuiModelLocation() {
        return guiModelLocation;
    }

    public ModelResourceLocation getModelLocation() {
        return modelLocation;
    }

    public void setHasOtherModel(boolean hasOtherModel) {
        this.hasOtherModel = hasOtherModel;
    }

    public boolean hasOtherModel() {
        return hasOtherModel;
    }

    public static class Deserializer implements JsonDeserializer<ModelProperties>
    {
        @Override
        public ModelProperties deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            ModelResourceLocation model = parserResourceLocation(jsonObject.get("model").getAsString());
            ModelResourceLocation guiModel = jsonObject.has("guiModel") ? parserResourceLocation(jsonObject.get("guiModel").getAsString()) : null;
            boolean flag = model instanceof CustomResourceLocation && guiModel instanceof CustomResourceLocation;
            EnumMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms = Maps.newEnumMap(ItemCameraTransforms.TransformType.class);
            if(jsonObject.has("display"))
            {
                JsonObject display = jsonObject.get("display").getAsJsonObject();
                transforms.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, parserTransform("firstperson_lefthand", jsonObject));
                transforms.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, parserTransform("firstperson_righthand", jsonObject));
                transforms.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, parserTransform("thirdperson_lefthand", jsonObject));
                transforms.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, parserTransform("thirdperson_righthand", jsonObject));
                transforms.put(ItemCameraTransforms.TransformType.GUI, parserTransform("gui", jsonObject));
                transforms.put(ItemCameraTransforms.TransformType.GROUND, parserTransform("ground", jsonObject));
                transforms.put(ItemCameraTransforms.TransformType.FIXED, parserTransform("fixed", jsonObject));
                transforms.put(ItemCameraTransforms.TransformType.HEAD, parserTransform("head", jsonObject));
            }
            ModelProperties properties = new ModelProperties(model, guiModel, transforms);
            properties.setHasOtherModel(!flag);
            return properties;
        }

        private TRSRTransformation parserTransform(String key, JsonObject jsonObject) {
            if(jsonObject.has(key))
            {
                JsonObject obj = jsonObject.getAsJsonObject(key);
                Vector3f vector3f = parserVector3f("rotation", obj);
                Vector3f vector3f1 = parserVector3f("translation", obj);
                Vector3f vector3f2 = parserVector3f("scale", obj);

                if(vector3f == null || vector3f1 == null || vector3f2 == null)
                {
                    return TRSRTransformation.identity();
                }
                return new TRSRTransformation(vector3f1, TRSRTransformation.quatFromXYZDegrees(vector3f), vector3f2, null);
            }
            return TRSRTransformation.identity();
        }

        private Vector3f parserVector3f(String key, JsonObject jsonObject) {
            if(jsonObject.has(key))
            {
                JsonArray array = jsonObject.getAsJsonArray(key);
                float[] afloat = new float[3];

                for (int i = 0; i < afloat.length; ++i)
                {
                    afloat[i] = JsonUtils.getFloat(array.get(i), key + "[" + i + "]");
                }

                return new Vector3f(afloat[0], afloat[1], afloat[2]);
            }
            return null;
        }

        private ModelResourceLocation parserResourceLocation(String str) {
            if(str.contains(":")) return new ModelResourceLocation(str);

            ModelType type;
            if(str.endsWith(".obj")) type = ModelType.OBJ;
            else if(str.endsWith(".json")) type = ModelType.JSON;
            else throw new JsonParseException("Unsupported custom model type, modelPath:" + ModelManager.INSTANCE.getModelDir().getPath() + File.separator + str);

            return new CustomResourceLocation(str, type);
        }
    }
}
