package com.github.kabuki.compoundweapon.client;

import com.github.kabuki.compoundweapon.CompoundWeapon;

import com.github.kabuki.compoundweapon.api.weapon.IWeapon;
import com.github.kabuki.compoundweapon.client.model.ModelManager;
import com.github.kabuki.compoundweapon.client.model.VariantMapper;
import com.github.kabuki.compoundweapon.common.registries.WeaponRegistry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.LinkedHashSet;
import java.util.Map;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = CompoundWeapon.MOD_ID)
public class ModelRegistryHandler {
    private static final LinkedHashSet<TextureAtlasSprite> textureAtlasSprites = new LinkedHashSet<>();

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event)
    {
        for(Map.Entry<String, IWeapon> entry : WeaponRegistry.getInstance().getRegistry().entrySet())
        {
            for(VariantMapper variantMapper :entry.getValue().getResource())
            {
                ModelManager.INSTANCE.registerItemModel((Item) entry.getValue(), variantMapper);
            }
        }
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
