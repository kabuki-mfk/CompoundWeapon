package com.github.kabuki.compoundweapon.client;

import com.github.kabuki.compoundweapon.CompoundWeapon;

import com.github.kabuki.compoundweapon.client.model.ModelManager;
import com.github.kabuki.compoundweapon.client.model.ModelType;
import com.github.kabuki.compoundweapon.client.model.VariantMapper;
import com.github.kabuki.compoundweapon.common.registries.RegistryEventHandler;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.LinkedHashSet;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = CompoundWeapon.MOD_ID)
public class ModelRegistryHandler {
    private static final LinkedHashSet<TextureAtlasSprite> textureAtlasSprites = new LinkedHashSet<>();

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event)
    {
        ModelManager.INSTANCE.registerItemModel(RegistryEventHandler.TEST, new VariantMapper(0, null, "cubes.obj", ModelType.OBJ));
        ModelManager.INSTANCE.onLoadModels();
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event)
    {
        ModelManager.INSTANCE.onBakeModels(event.getModelRegistry());
    }

    @SubscribeEvent
    public static void onTextureLoad(TextureStitchEvent.Pre event)
    {
        for(TextureAtlasSprite sprite : textureAtlasSprites)
        {
            event.getMap().setTextureEntry(sprite);
        }
    }

    public static boolean registryCustomTextureSprite(TextureAtlasSprite textureAtlasSprite) {
        return textureAtlasSprites.add(textureAtlasSprite);
    }

    public static LinkedHashSet<TextureAtlasSprite> getTextureAtlasSprites() {
        return textureAtlasSprites;
    }
}
