package com.github.kabuki.compoundweapon.client.model;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@SideOnly(Side.CLIENT)
public enum ModelManager implements ICustomModelLoader {
    INSTANCE;

    private final Set<String> acceptDomain = new HashSet<>();
    private File modelDir;
    private ModelPack modelPack;

    public void initLoadDir(File modDir) {
        modelDir = new File(modDir, "mods/CompoundWeapon/Custom/Model");
        if(modDir.exists())
        {
            modelPack = new ModelPack(modelDir);
        }
        else
        {
            modDir.mkdirs();
        }
    }

    public File getModelDir() {
        return modelDir;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        //do not thing
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return modelLocation instanceof CustomResourceLocation &&
                (modelLocation.getPath().endsWith(".obj") || modelLocation.getPath().endsWith(".json"));
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        CustomResourceLocation resourceLocation = (CustomResourceLocation) modelLocation;
        ModelPack.ModelResource resource = null;
        IModel model = null;

        try
        {
            resource = modelPack.getModelResource(resourceLocation);
            if(resourceLocation.getModelType() == ModelType.OBJ)
            {
                CustomModel.OBJParser parser = new CustomModel.OBJParser(resource, modelPack);
                model = parser.parser();
            }
        }
        finally {
            IOUtils.closeQuietly(resource);
        }

        if (model == null) model = ModelLoaderRegistry.getMissingModel();
        return model;
    }

    public ModelPack getModelPack() {
        return modelPack;
    }
}
