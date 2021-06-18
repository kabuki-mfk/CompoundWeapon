package com.github.kabuki.compoundweapon.client.model;

import com.github.kabuki.compoundweapon.CompoundWeapon;
import com.github.kabuki.compoundweapon.client.ModelRegistryHandler;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IRegistryDelegate;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

@SideOnly(Side.CLIENT)
public enum ModelManager implements ICustomModelLoader {
    INSTANCE;

    private final Map<ModelResourceLocation, IModel> modelCache = Maps.newHashMap();
    private final Set<ModelEntry> modelEntries = Sets.newLinkedHashSet();

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
            ModelResourceLocation modelLocation = variant.getModelLocation();
            if(modelLocation instanceof CustomResourceLocation) {
                ModelResourceLocation modelRegistryLocation = new ModelResourceLocation(registryName, StringUtils.isNullOrEmpty(variant.getVariantName()) ? "inventory" : variant.getVariantName());
                ModelEntry entry;
                if(((CustomResourceLocation)modelLocation).getModelType() == ModelType.INFO) {
                    CustomResourceLocation fileLocation = (CustomResourceLocation) modelLocation;
                    ModelInfo info = loadModelInfo(fileLocation);
                    if (info == null) {
                        CompoundWeapon.LOGGER.warn("missing modelInfo:{} skipping", fileLocation.getLocation());
                        continue;
                    }
                    entry = new ModelEntry(modelRegistryLocation, info, item.delegate, variant.getMeta());
                }
                else {
                    entry = new ModelEntry(modelRegistryLocation, variant.getModelLocation(), item.delegate, variant.getMeta());
                }
                modelEntries.add(entry);
            }
            else ModelLoader.setCustomModelResourceLocation(item, variant.getMeta(), modelLocation);
        }
    }

    private ModelInfo loadModelInfo(CustomResourceLocation location) {
        try(FileReader reader = new FileReader(getModelPack().getFileFrommLocation(location))) {
            return ModelInfo.deserialize(reader);
        }
        catch (FileNotFoundException e) {
            CompoundWeapon.LOGGER.error(e);
        }
        catch (IOException e) {
            CompoundWeapon.LOGGER.error("error on load modelInfo:" + location.getLocation(), e);
        }
        return null;
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
        for(ModelEntry modelEntry : modelEntries)
        {
            itemModelMesher.register(modelEntry.itemDelegate.get(), modelEntry.meta, modelEntry.getRegistryModelLocation());
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

        for(ModelEntry entry : modelEntries)
        {
            IModel model= entry.getLoadedModel();
            if(entry.hasReloadModel()) {
                model = entry.reload();
                entry.setModelState(model instanceof TransformModels ?
                        TransformModels.ModelState.fromModelInfo(entry.getModelInfo()) : model.getDefaultState());
            }

            modelRegistry.putObject(entry.getRegistryModelLocation(), model.bake(entry.getModelState(),
                    DefaultVertexFormats.ITEM, textureGetter));
        }
    }

    /**
     * internal do not use
     */
    public void onLoadModels() {
        net.minecraft.client.renderer.texture.TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
        modelCache.clear();
        for(ModelEntry entry : modelEntries)
        {
            IModel model = null;
            ModelInfo modelInfo = entry.getModelInfo();
            boolean hasModelInfo = modelInfo != null;
            boolean hasModelFile = entry.getModelFileLocation() != null;

            if(!hasModelFile && !hasModelInfo) {
                modelCache.put(entry.getRegistryModelLocation(), missingModel);
                CompoundWeapon.LOGGER.error("ModelInfo of variant '{}' is Null, skipping", entry.getRegistryModelLocation());
                continue;
            }

            ReloadModel reloadModel;
            if(hasModelInfo) {
                reloadModel = validateModelInfo(modelInfo, entry.getRegistryModelLocation());
            }
            else {
                reloadModel = validateModelFile(entry.getModelFileLocation());
            }

            if(reloadModel != null) {
                entry.setReloadModel(reloadModel);
                continue;
            }

            try {
                model = hasModelFile ? loadModel(entry.getModelFileLocation()) : loadModels(modelInfo);
            } catch (Throwable throwable) {
                CompoundWeapon.LOGGER.error("Exception on loading Item Model from ModelInfo, model variant:" + entry.getRegistryModelLocation() + " skipping", throwable);
            }

            if(model == missingModel) CompoundWeapon.LOGGER.error("Loader returned missing model while loading model {}", entry.getRegistryModelLocation());
            if(model == null) model = missingModel;
            modelCache.put(entry.getRegistryModelLocation(), model);

            if(model == missingModel) continue;

            entry.setLoadedModel(model);
            entry.setModelState(model instanceof TransformModels ?
                    TransformModels.ModelState.fromModelInfo(modelInfo) : model.getDefaultState());
            for(ResourceLocation resourceLocation : model.getTextures())
            {
                if(resourceLocation instanceof CustomResourceLocation)
                    ModelRegistryHandler.registryCustomTextureSprite(new CustomTexture((CustomResourceLocation) resourceLocation));
                else
                    textureMap.registerSprite(resourceLocation);
            }
        }
    }

    private IModel loadModels(ModelInfo info) throws Exception {
        ModelProperties properties = info.getNormalProperties();
        IModel normalModel = loadModel(properties.getModelLocation());
        if(info.onlyNormalProperties()) {
            IModel guiModel = null;
            if(properties.hasGuiModel()) guiModel = loadModel(properties.getGuiModelLocation());
            if(guiModel == missingModel) guiModel = normalModel;
            return new CustomModelWrapper(normalModel, guiModel);
        }

        EnumMap<ItemCameraTransforms.TransformType, IModel> modelMap = new EnumMap<>(ItemCameraTransforms.TransformType.class);

        for(ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values())
        {
            ModelProperties modelProperties = info.getModelProperties(type);
            if(modelProperties == null) continue;
            IModel model = loadModel(modelProperties.getModelLocation());
            modelMap.put(type, model);
        }

        return new TransformModels(normalModel, modelMap);
    }

    public IModel getModel(ModelResourceLocation mdl) {
        return modelCache.get(mdl);
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        if(!(modelLocation instanceof CustomResourceLocation)) return ModelLoaderRegistry.getModel(modelLocation);

        CustomResourceLocation resourceLocation = (CustomResourceLocation) modelLocation;
        ModelPack.ModelResource resource = null;
        IModel model = getModel((ModelResourceLocation) modelLocation);

        if(model != null && model != missingModel) return model;

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

    private ReloadModel validateModelFile(ModelResourceLocation modelResourceLocation) {
        return modelResourceLocation instanceof CustomResourceLocation ?
                null : () -> ModelLoaderRegistry.getModel(modelResourceLocation);
    }

    private ReloadModel validateModelInfo(ModelInfo info, ModelResourceLocation registryLocation) {
        boolean flag;
        ModelProperties modelProperties = info.getNormalProperties();
        flag = validateProperties(modelProperties, registryLocation);

        for(ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values())
        {
            modelProperties = info.getModelProperties(type);
            if(modelProperties == null) continue;
            validateProperties(modelProperties, registryLocation);
        }

        return !flag ? null : () -> loadModels(info);
    }

    private boolean validateProperties(ModelProperties modelProperties, ModelResourceLocation registryLocation) {
        if(modelProperties.hasOtherModel()) {
            filterAndLoadModel(modelProperties.getModelLocation(), registryLocation);
            if(modelProperties.hasGuiModel())
                filterAndLoadModel(modelProperties.getGuiModelLocation(), registryLocation);
            return true;
        }
        return false;
    }

    private void filterAndLoadModel(ModelResourceLocation location, ModelResourceLocation registryLocation) {
        if(location instanceof CustomResourceLocation) {
            IModel model = null;
            try {
                model = loadModel(location);
            }
            catch (Throwable throwable) {
                CompoundWeapon.LOGGER.error("Exception on loading Item Model from ModelInfo, model file:" + ((CustomResourceLocation) location).getLocation() + " skipping", throwable);
            }
            if(model == missingModel) CompoundWeapon.LOGGER.error("Loader returned missing model while loading model {}", registryLocation);
            if(model == null) model = missingModel;
            modelCache.put(registryLocation, model);

            if(model == missingModel) return;

            net.minecraft.client.renderer.texture.TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
            for(ResourceLocation resourceLocation : model.getTextures())
            {
                if(resourceLocation instanceof CustomResourceLocation)
                    ModelRegistryHandler.registryCustomTextureSprite(new CustomTexture((CustomResourceLocation) resourceLocation));
                else
                    textureMap.registerSprite(resourceLocation);
            }
        }
    }

    interface ReloadModel {
         IModel reload() throws Throwable;
    }

    private static class ModelEntry {
        private final ModelResourceLocation registryModelLocation;
        private final ModelResourceLocation modelFileLocation;
        private IModel loadedModel = ModelManager.INSTANCE.missingModel;
        private IModelState modelState;
        private final ModelInfo info;
        @Nullable
        private ReloadModel reloadModel;
        public final IRegistryDelegate<Item> itemDelegate;
        public final int meta;

        public ModelEntry(ModelResourceLocation registryModelLocation, ModelResourceLocation fileLocation, IRegistryDelegate<Item> delegate, int meta) {
            this.registryModelLocation = registryModelLocation;
            this.modelFileLocation = fileLocation;
            this.itemDelegate = delegate;
            this.meta = meta;
            this.info = null;
        }

        public ModelEntry(ModelResourceLocation registryModelLocation, ModelInfo modelInfo, IRegistryDelegate<Item> delegate, int meta) {
            this.registryModelLocation = registryModelLocation;
            this.modelFileLocation = null;
            this.info = modelInfo;
            this.itemDelegate = delegate;
            this.meta = meta;
        }

        @Nullable
        public ModelResourceLocation getModelFileLocation() {
            return modelFileLocation;
        }

        public ModelResourceLocation getRegistryModelLocation() {
            return registryModelLocation;
        }

        @Nullable
        public ModelInfo getModelInfo() {
            return info;
        }

        public IModelState getModelState() {
            return modelState;
        }

        public void setModelState(IModelState modelState) {
            this.modelState = modelState;
        }

        public IModel getLoadedModel() {
            return loadedModel;
        }

        public void setLoadedModel(IModel loadedModel) {
            this.loadedModel = loadedModel;
        }

        public void setReloadModel(ReloadModel reloadModel) {
            this.reloadModel = reloadModel;
        }

        public boolean hasReloadModel() {
            return reloadModel != null;
        }

        public IModel reload() {
            IModel model = ModelManager.INSTANCE.missingModel;
            if(reloadModel == null) return model;

            try {
                model = reloadModel.reload();
            }
            catch (Throwable throwable) {
                CompoundWeapon.LOGGER.error("error on reloadModel, model variant:" + this.registryModelLocation, throwable);
            }
            return model;
        }
    }
}
