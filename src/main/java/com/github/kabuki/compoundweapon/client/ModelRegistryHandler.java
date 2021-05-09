package com.github.kabuki.compoundweapon.client;

import com.github.kabuki.compoundweapon.CompoundWeapon;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.LinkedHashSet;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = CompoundWeapon.MOD_ID)
public class ModelRegistryHandler {
    private static final LinkedHashSet<TextureAtlasSprite> textureAtlasSprites = new LinkedHashSet<>();

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
