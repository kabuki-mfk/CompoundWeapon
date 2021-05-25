package com.github.kabuki.compoundweapon.client.model;

import com.github.kabuki.compoundweapon.CompoundWeapon;
import com.github.kabuki.compoundweapon.client.ModelRegistryHandler;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IRegistryDelegate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SideOnly(Side.CLIENT)
public enum ModelManager implements ICustomModelLoader {
    INSTANCE;

    private final Set<String> acceptDomain = new HashSet<>();
    private final Map<ModelResourceLocation, CustomResourceLocation> variantFiles = Maps.newHashMap();
    private final Map<ModelResourceLocation, IModel> modelCache = Maps.newHashMap();
    private final Map<Pair<IRegistryDelegate<Item>, Integer>, ModelResourceLocation> customModels = Maps.newHashMap();
    private final IModel missingModel = CustomModel.NULL_Model;
    private File modelDir;
    private ModelPack modelPack;

    public void initLoadDir(File modDir) {
        modelDir = new File(modDir, "mods" + File.separator + CompoundWeapon.MOD_NAME + File.separator
                + "Custom" + File.separator + "Model");
        CustomResourceLocation.modelDir = getModelDir();
        if(modelDir.exists())
        {
            modelPack = new ModelPack(modelDir);
        }
        else
        {
            this.modelDir.mkdirs();
        }
    }

    public File getModelDir() {
        return modelDir;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        //do not thing
    }

    public void registerItemModel(Item item, VariantMapper... variantMappers)
    {
        ResourceLocation registryName = item.getRegistryName();
        if(registryName == null) return;

        for(VariantMapper variant : variantMappers) {
            CustomResourceLocation fileLocation = variant.getModelLocation();
            ModelResourceLocation mrl =  new ModelResourceLocation(registryName, StringUtils.isNullOrEmpty(variant.getVariantName()) ? "inventory" : variant.getVariantName());
            customModels.put(Pair.of(item.delegate, variant.getMeta()), mrl);
            variantFiles.put(mrl, fileLocation);
        }
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return modelLocation instanceof CustomResourceLocation &&
                (modelLocation.getPath().endsWith(".obj") || modelLocation.getPath().endsWith(".json"));
    }

    /**
     * internal do not use
     */
    public void registerItemRender()
    {
        ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        for(Map.Entry<Pair<IRegistryDelegate<Item>, Integer>, ModelResourceLocation> e : customModels.entrySet()) {
            itemModelMesher.register(e.getKey().getLeft().get(), e.getKey().getRight(), e.getValue());
        }
    }

    /**
     * internal do not use
     */
    public void onBakeModels(IRegistry<ModelResourceLocation, IBakedModel> modelRegistry)
    {
        net.minecraft.client.renderer.texture.TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
        java.util.function.Function<ResourceLocation, net.minecraft.client.renderer.texture.TextureAtlasSprite> textureGetter
                = res -> textureMap.getAtlasSprite(res.toString());

        for(Map.Entry<ModelResourceLocation, IModel> e : modelCache.entrySet())
        {
            IModel model = e.getValue();
            modelRegistry.putObject(e.getKey(), model.bake(model.getDefaultState(),
                    DefaultVertexFormats.ITEM, textureGetter));
        }
    }

    /**
     * internal do not use
     */
    public void onLoadModels() {
        net.minecraft.client.renderer.texture.TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
        for(Map.Entry<ModelResourceLocation, CustomResourceLocation> e : variantFiles.entrySet())
        {
            if(e.getValue() == null)
            {
                modelCache.put(e.getKey(), missingModel);
                CompoundWeapon.LOGGER.error("File Location of variant '{}' isEmpty", e.getKey());
                continue;
            }

            IModel model = null;

            try {
                model = loadModel(e.getValue());
            } catch (Exception exception) {
                CompoundWeapon.LOGGER.error("Exception loading Item Model, location:{} , skipping", e.getValue());
            }

            if(model == missingModel) CompoundWeapon.LOGGER.error("Loader returned missing model while loading model {}", e.getValue());
            if(model == null) model = missingModel;

            modelCache.put(e.getKey(), model);
            if(model == missingModel) continue;

            for(ResourceLocation resourceLocation : model.getTextures())
            {
                if(resourceLocation instanceof CustomResourceLocation)
                    ModelRegistryHandler.registryCustomTextureSprite(new CustomTexture((CustomResourceLocation) resourceLocation));
                else
                    textureMap.registerSprite(resourceLocation);
            }
        }
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

//            if(resourceLocation.getModelType() == ModelType.JSON)
//            {
//                TODO parser JSON
//            }
        }
        finally {
            IOUtils.closeQuietly(resource);
        }

        if (model == null) model = missingModel;
        return model;
    }

    public ModelPack getModelPack() {
        return modelPack;
    }
}
