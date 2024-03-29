package com.github.kabuki.compoundweapon.client.model;

import com.github.kabuki.compoundweapon.CompoundWeapon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.ResourcePackFileNotFoundException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

@SideOnly(Side.CLIENT)
public class CustomTexture extends TextureAtlasSprite {

    private boolean isMissing;

    public CustomTexture(CustomResourceLocation location) {
        this(location.getPath());
    }

    public CustomTexture(String path) {
       super(path);
    }

    @Override
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        return true;
    }

    @Override
    public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
        InputStream inputStream;
        CustomResourceLocation resourceLocation = new CustomResourceLocation(this.getIconName(), null);
        try {
            inputStream = ModelManager.INSTANCE.getModelPack().getInputStreamFrommLocation(resourceLocation);
            BufferedImage image = TextureUtil.readBufferedImage(inputStream);
            int[][] aint = new int[Minecraft.getMinecraft().gameSettings.mipmapLevels + 1][];
            aint[0] = new int[image.getHeight() * image.getWidth()];
            image.getRGB(0, 0, image.getWidth(), image.getHeight(), aint[0], 0, image.getWidth());
            clearFramesTextureData();
            framesTextureData.add(aint);
            this.width = image.getWidth();
            this.height = image.getHeight();
        } catch (ResourcePackFileNotFoundException packFileNotFoundException) {
            CompoundWeapon.LOGGER.error("TexturePNG#load: missing texture, path '{}'", resourceLocation.getLocation());
            isMissing = true;
            return true;
        } catch (IOException e) {
            CompoundWeapon.LOGGER.error("TexturePNG#load: error on load texture, path '{}'", resourceLocation.getLocation());
            isMissing = true;
            return true;
        }
        return false;
    }

    public boolean isMissingTexture() {
        return isMissing;
    }
}
