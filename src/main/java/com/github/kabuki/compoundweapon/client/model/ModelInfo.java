package com.github.kabuki.compoundweapon.client.model;

import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.util.JsonUtils;

import javax.annotation.Nullable;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.EnumMap;

public class ModelInfo {
    private static final Gson GSON = (new GsonBuilder().registerTypeAdapter(ModelInfo.class, new ModelInfo.Deserializer()).registerTypeAdapter(ModelProperties.class, new ModelProperties.Deserializer()).create());
    private final EnumMap<ItemCameraTransforms.TransformType, ModelProperties> properties = Maps.newEnumMap(ItemCameraTransforms.TransformType.class);
    private ModelProperties normal;

    public void setModelProperties(ItemCameraTransforms.TransformType transformType, ModelProperties modelProperties) {
        properties.put(transformType, modelProperties);
    }

    @Nullable
    public ModelProperties getModelProperties(ItemCameraTransforms.TransformType transformType) {
        return properties.get(transformType);
    }

    public static ModelInfo deserialize(Reader reader)
    {
        return JsonUtils.gsonDeserialize(GSON, reader, ModelInfo.class, false);
    }

    public void setNormalProperties(ModelProperties normal) {
        this.normal = normal;
    }

    public ModelProperties getNormalProperties() {
        return normal;
    }

    public boolean onlyNormalProperties() { return properties.size() == 0; }

    public static class Deserializer implements JsonDeserializer<ModelInfo> {

        @Override
        public ModelInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            ModelInfo info = new ModelInfo();
            info.setNormalProperties(GSON.fromJson(jsonObject.get("normal"), ModelProperties.class));
            info.setModelProperties(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, deserializeProperties("firstperson_lefthand", jsonObject));
            info.setModelProperties(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, deserializeProperties("firstperson_righthand", jsonObject));
            info.setModelProperties(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, deserializeProperties("thirdperson_lefthand", jsonObject));
            info.setModelProperties(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, deserializeProperties("thirdperson_righthand", jsonObject));
            info.setModelProperties(ItemCameraTransforms.TransformType.GUI, deserializeProperties("gui", jsonObject));
            info.setModelProperties(ItemCameraTransforms.TransformType.GROUND, deserializeProperties("ground", jsonObject));
            info.setModelProperties(ItemCameraTransforms.TransformType.FIXED, deserializeProperties("fixed", jsonObject));
            info.setModelProperties(ItemCameraTransforms.TransformType.HEAD, deserializeProperties("head", jsonObject));
            return info;
        }

        private ModelProperties deserializeProperties(String key, JsonObject jsonObject) {
            return jsonObject.has(key) ? GSON.fromJson(jsonObject.get(key), ModelProperties.class) : null;
        }
    }
}
