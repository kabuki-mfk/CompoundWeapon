package com.github.kabuki.compoundweapon.client.model;

import com.google.common.base.CharMatcher;

import net.minecraft.client.resources.ResourcePackFileNotFoundException;
import net.minecraft.util.Util;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.*;

@SideOnly(Side.CLIENT)
public class ModelPack {

    private static final boolean ON_WINDOWS = Util.getOSType() == Util.EnumOS.WINDOWS;
    private static final CharMatcher BACKSLASH_MATCHER = CharMatcher.is('\\');

    protected final File dir;

    public ModelPack(File modelDir) {
        this.dir = modelDir;
    }

    private boolean validatePath(File file, String path) throws IOException {
        String s = ON_WINDOWS ? BACKSLASH_MATCHER.replaceFrom(file.getCanonicalPath(), "/") : file.getCanonicalPath();
        return s.endsWith(path);
    }

    public ModelResource getModelResource(CustomResourceLocation resLocation) throws IOException {
        InputStream inputStream = getInputStreamFrommLocation(resLocation);
        return new ModelResource(resLocation, inputStream);
    }

    public File getFileFrommLocation(CustomResourceLocation resLocation) {
        try
        {
            File file = new File(dir, resLocation.getPath());

            if (file.isFile() && validatePath(file, resLocation.getPath()))
            {
                return file;
            }
        }
        catch (IOException ignored)
        {

        }

        return null;
    }

    public InputStream getInputStreamFrommLocation(CustomResourceLocation resourceLocation) throws IOException {
        File file = getFileFrommLocation(resourceLocation);
        if(file == null)
        {
            throw new ResourcePackFileNotFoundException(dir, resourceLocation.toString());
        }

        return new FileInputStream(file);
    }

    public static class ModelResource implements Closeable {
        private final CustomResourceLocation modelResource;
        private final InputStream inputStream;

        public ModelResource(CustomResourceLocation resourceLocation, InputStream inputStream)
        {
            this.modelResource = resourceLocation;
            this.inputStream = inputStream;
        }

        public InputStream getInputStream()
        {
            return inputStream;
        }

        public CustomResourceLocation getModelResourceLocation()
        {
            return modelResource;
        }

        @Override
        public void close() throws IOException {
            inputStream.close();
        }
    }
}
