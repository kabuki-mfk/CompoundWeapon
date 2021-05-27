package com.github.kabuki.compoundweapon.client.model;

import com.github.kabuki.compoundweapon.CompoundWeapon;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class CustomModel implements IModel {
    public static final IModel NULL_Model = new IModel() {
        @Override
        public Collection<ResourceLocation> getTextures() {
            return ModelLoaderRegistry.getMissingModel().getTextures();
        }

        @Override
        public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
            return ModelLoaderRegistry.getMissingModel().bake(state, format, bakedTextureGetter);
        }

        @Override
        public IModel process(ImmutableMap<String, String> customData) {
            return ModelLoaderRegistry.getMissingModel().process(customData);
        }

        @Override
        public IModel retexture(ImmutableMap<String, String> textures) {
            return ModelLoaderRegistry.getMissingModel().retexture(textures);
        }
    };
    private final MaterialManager materialManager;
    private final CustomResourceLocation resourceLocation;
    private final ModelType modelType;
    private final CustomData customData;

    public CustomModel(MaterialManager mtlLib, CustomResourceLocation modelLocation, ModelType type)
    {
        this(mtlLib, modelLocation, type, new CustomData());
    }

    public CustomModel(MaterialManager mtlLib, CustomResourceLocation modelLocation, ModelType type, CustomData customData)
    {
        this.materialManager = mtlLib;
        this.resourceLocation = modelLocation;
        this.modelType = type;
        this.customData = customData;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        ImmutableMap.Builder<String, TextureAtlasSprite> builder = ImmutableMap.builder();
        builder.put(ModelLoader.White.LOCATION.toString(), ModelLoader.White.INSTANCE);
        TextureAtlasSprite missing = bakedTextureGetter.apply(TextureMap.LOCATION_MISSING_TEXTURE);
        TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
        for(Map.Entry<String, Material> e : materialManager.materials.entrySet())
        {
            TextureAtlasSprite sprite = textureMap.getAtlasSprite(e.getValue().texture.texturePath);
            if(sprite instanceof CustomTexture && ((CustomTexture) sprite).isMissingTexture())
            {
                builder.put(e.getKey(), missing);
            }
            else
            {
                builder.put( e.getKey(), sprite);
            }
        }
        return new CustomBakedModel(this, state, format, builder.build());
    }

    @Override
    public IModel process(ImmutableMap<String, String> data) {
        return new CustomModel(materialManager, resourceLocation, modelType, new CustomData(customData, data));
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        List<CustomResourceLocation> textures = Lists.newArrayList();
        for (Material material : materialManager.materials.values())
        {
            if(material.isWhite()) continue;
            textures.add(new CustomResourceLocation(material.texture.texturePath, null));
        }
        return Lists.newArrayList(textures);
    }

    @Override
    public IModelState getDefaultState() {
        return new CustomModelState();
    }

    public static class CustomBakedModel implements IBakedModel {

        private final CustomModel customModel;
        private final IModelState modelState;
        private final VertexFormat vertexFormat;
        private final ImmutableMap<String, TextureAtlasSprite> textures;
        private List<BakedQuad> quads;
        private TextureAtlasSprite sprite = ModelLoader.White.INSTANCE;

        public CustomBakedModel(CustomModel model, IModelState modelState, VertexFormat format, ImmutableMap<String, TextureAtlasSprite> textures)
        {
            this.customModel = model;
            this.modelState = modelState;
            this.vertexFormat = format;
            this.textures = textures;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
            if(side != null || state != null) return ImmutableList.of();
            if(quads == null) quads = createQuads(modelState);
            return quads;
        }

        private List<BakedQuad> createQuads(IModelState state) {
            List<BakedQuad> bakedQuads = Lists.newArrayList();

            Optional<TRSRTransformation> transformation = Optional.empty();

            if(state instanceof CustomModelState)
            {
                CustomModelState customModelState = (CustomModelState) state;
                if(customModelState.parent != null)
                {
                    transformation = customModelState.parent.apply(Optional.empty());
                }
            }
            else
            {
                transformation = state.apply(Optional.empty());
            }

            Set<Face> faces = new LinkedHashSet<>();
            TRSRTransformation trsrf = transformation.orElse(TRSRTransformation.identity());
            for(LinkedHashSet<Face> group : customModel.materialManager.groups.values())
            {
                LinkedHashSet<Face> faceSet = new LinkedHashSet<>();
                for(Face f : group) {
                    faceSet.add(f.bake(trsrf));
                }
                faces.addAll(faceSet);
            }

            for (Face f : faces)
            {
                sprite = f.material.isWhite() ? ModelLoader.White.INSTANCE : textures.get(f.material.mtlName);
                Vector3f normal = f.getNormal();
                UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(vertexFormat);
                EnumFacing enumFacing = EnumFacing.getFacingFromVector(normal.x, normal.y, normal.z);

                builder.setContractUVs(true);
                builder.setQuadOrientation(enumFacing);
                builder.setTexture(sprite);
                putVertexData(builder, f.vertices[0], normal, TextureCoordinate.DEFAULT_COORDINATES[0], sprite, enumFacing);
                putVertexData(builder, f.vertices[1], normal, TextureCoordinate.DEFAULT_COORDINATES[1], sprite, enumFacing);
                putVertexData(builder, f.vertices[2], normal, TextureCoordinate.DEFAULT_COORDINATES[2], sprite, enumFacing);
                putVertexData(builder, f.vertices[f.isTriangles() ? 2 : 3], normal, TextureCoordinate.DEFAULT_COORDINATES[3], sprite, enumFacing);
                bakedQuads.add(builder.build());
            }
            return bakedQuads;
        }

        private void putVertexData(UnpackedBakedQuad.Builder builder, Vertex v, Vector3f faceNormal, TextureCoordinate defUV, TextureAtlasSprite sprite, EnumFacing side) {
            final float eps = 1e-2f;

            Vector3f uv = v.getTextureCoordinate() == null ? defUV.getUVW() : v.getTextureCoordinate().getUVW();
            float x0 = uv.x;
            float y0 = uv.y;
            float dx = side.getDirectionVec().getX() * eps / sprite.getIconWidth();
            float dy = side.getDirectionVec().getY() * eps / sprite.getIconHeight();

            for(int e = 0; e < vertexFormat.getElementCount(); e++)
            {
                switch (vertexFormat.getElement(e).getUsage())
                {
                    case POSITION:
                        builder.put(e, v.pos.x, v.pos.y, v.pos.z, v.pos.w);
                        break;
                    case COLOR:
                        if(v.getMaterial() != null) {
                            Vector4f color = v.getMaterial().getColor();
                               builder.put(e, color.x, color.y, color.z, color.w);
                        }
                        else builder.put(e, 1, 1, 1, 1);
                        break;
                    case UV:
                            builder.put(e,
                                    sprite.getInterpolatedU((x0 - dx) * 16),
                                    sprite.getInterpolatedV((y0 - dy) * 16),
                                    1, 0);
                        break;
                    case NORMAL:
                        if(v.getNormal() == null)
                            builder.put(e, faceNormal.x, faceNormal.y, faceNormal.z, 0);
                        else
                            builder.put(e, v.getNormal().x, v.getNormal().y, v.getNormal().z, 0);
                        break;
                    default:
                        builder.put(e);
                        break;
                }

            }
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
            return PerspectiveMapWrapper.handlePerspective(this, PerspectiveMapWrapper.getTransforms(modelState), cameraTransformType);
        }

        @Override
        public boolean isAmbientOcclusion() {
            return customModel == null || customModel.customData.isAmbient;
        }

        @Override
        public boolean isGui3d() {
            return customModel == null || customModel.customData.isGui3d;
        }

        @Override
        public boolean isBuiltInRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return sprite;
        }

        @Override
        public ItemOverrideList getOverrides() {
            return ItemOverrideList.NONE;
        }
    }

    public static class CustomModelState implements IModelState {

        public IModelState parent;

        public CustomModelState()
        {
            this(TRSRTransformation.identity());
        }

        public CustomModelState(IModelState parent)
        {
            this.parent = parent;
        }

        @Override
        public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part) {
            if(parent != null) parent.apply(part);
            return Optional.empty();
        }

    }

    public static class CustomData {
        public boolean isAmbient;
        public boolean isGui3d;
        public boolean flipV;

        public CustomData() {

        }

        public CustomData(CustomData parent, Map<String, String> data) {
            this.isAmbient = parent.isAmbient;
            this.isGui3d = parent.isGui3d;
            this.flipV = parent.flipV;
            process(data);
        }

        public void process(Map<String, String> customData)
        {
            for (Map.Entry<String, String> e : customData.entrySet())
            {
                switch (e.getKey()) {
                    case "ambient":
                        this.isAmbient = Boolean.parseBoolean(e.getValue());
                        break;
                    case "gui3d":
                        this.isGui3d = Boolean.parseBoolean(e.getValue());
                        break;
                    case "flip-v":
                        this.flipV = Boolean.parseBoolean(e.getValue());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public static class MaterialManager
    {
        private static final Pattern WHITE_SPACE = Pattern.compile("\\s+");
        public static final String DEFAULT_GROUP_NAME = "CustomModel.Default.Group.Name";

        private final Map<String, Material> materials = Maps.newHashMap();
        private final Map<String, LinkedHashSet<Face>> groups = Maps.newHashMap();

        public MaterialManager()
        {
            addGroup(DEFAULT_GROUP_NAME, new LinkedHashSet<>());
            materials.put(Material.DEFAULT_NAME, new Material());
        }

        public void addGroup(String groupName, LinkedHashSet<Face> faces) {
            groups.put(groupName, faces);
        }

        public boolean hasGroup(String groupName) {
            return groups.containsKey(groupName);
        }

        public LinkedHashSet<Face> getGroup(String groupName) {
            return groups.get(groupName);
        }

        public void parserMtl(ModelPack modelPack, String mtlPath, CustomResourceLocation location) throws IOException {

            this.materials.clear();

            String path = mtlPath;
            if(path.contains(File.separator))
                path = mtlPath.substring(mtlPath.lastIndexOf(File.separator));
            CustomResourceLocation mtlLocation = new CustomResourceLocation(path, null);

            try(ModelPack.ModelResource resource = modelPack.getModelResource(mtlLocation))
            {
                BufferedReader mtlReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

                Material material = new Material();
                materials.put(Material.DEFAULT_NAME, material);

                boolean hasColor = false;
                boolean hasTexture = false;

                for(;;)
                {
                    String line = mtlReader.readLine();
                    if (line == null) break; //end of the stream
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue; //skip empty line and comment

                    String[] lineData = WHITE_SPACE.split(line, 2);
                    String data = lineData[1];

                     switch (lineData[0])
                    {
                        case "newmtl":
                            hasColor = false;
                            hasTexture = false;
                            material = new Material();
                            material.setName(data);
                            materials.put(data, material);
                            break;
                        case "Kd":
                        case "Ka":
                        case "Ks":
                            String[] rgbStr = WHITE_SPACE.split(data, 3);
                            if(!hasColor || lineData[0].equals("kd")) {
                                material.setColor(new Vector4f(Float.parseFloat(rgbStr[0]), Float.parseFloat(rgbStr[1]), Float.parseFloat(rgbStr[2]), 1.0f));
                                hasColor = true;
                            }
                            break;
                        case "map_Kd":
                        case "map_Ka":
                        case "map_Ks":
                            if(!hasTexture || lineData[0].equals("map_Kd")) {
                                Texture texture;
                                String texturePath = data;

                                if(data.contains(" ")) {
                                    String[] mapStr = WHITE_SPACE.split(data);
                                    texturePath = mapStr[mapStr.length - 1];
                                }

                                if(texturePath.startsWith("../"))
                                    texturePath = texturePath.substring(texturePath.indexOf("/") + 1);

                                texture = new Texture(texturePath);
                                material.setTexture(texture);
                                hasTexture = true;
                            }
                            break;
                        case "d":
                            float alpha = Float.parseFloat(data);
                            material.getColor().setW(alpha);
                            break;
                        default:
                            CompoundWeapon.LOGGER.warn("CompoundWeapon.MaterialManager: command '{}' (mtl: '{}') is not currently supported, skipping.", lineData[0], mtlLocation);
                            break;
                    }
                }
            }
        }
    }

    public static class OBJParser {
        private static final Pattern WHITE_SPACE = Pattern.compile("\\s+");

        private final MaterialManager mtlManager = new MaterialManager();
        private final CustomResourceLocation resourceLocation;
        private final ModelPack modelPack;
        private final BufferedReader reader;

        private final List<String> groupList = Lists.newArrayList();
        private final List<Vertex> vertices = Lists.newArrayList();
        private final List<Vector3f> normals = Lists.newArrayList();
        private final List<TextureCoordinate> textureCoordinates = Lists.newArrayList();

        public OBJParser(ModelPack.ModelResource source, ModelPack modelPack) {
            this.resourceLocation = source.getModelResourceLocation();
            this.reader = new BufferedReader(new InputStreamReader(source.getInputStream(), StandardCharsets.UTF_8));
            this.modelPack = modelPack;
        }

        private static float[] parseFloats(String[] data)
        {
            float[] arr = new float[data.length];
            for(int i = 0; i < data.length; i++)
            {
                arr[i] = Float.parseFloat(data[i]);
            }
            return arr;
        }

        public CustomModel parser() throws IOException {
            int lineNum = 0;

            Material material = new Material();

            for(;;)
            {
                lineNum++;
                String line = reader.readLine();
                if(line == null) break; //end of the stream
                line = line.trim();
                if(line.isEmpty() || line.startsWith("#")) continue; //skip empty line and comment

                String[] lineData = WHITE_SPACE.split(line, 2);
                String data = lineData[1];
                String[] splitData = WHITE_SPACE.split(data);

                switch (lineData[0])
                {
                    case "mtllib":
                        mtlManager.parserMtl(this.modelPack, data, resourceLocation);
                        break;
                    case "usemtl":
                        if(mtlManager.materials.containsKey(data)) {
                            material = mtlManager.materials.get(data);
                        }
                        else {
                            CompoundWeapon.LOGGER.error("CompoundWeapon.OBJParser: command '{}' (model: '{}') material '{}' referenced but was not found",  lineData[0], resourceLocation, data);
                        }
                        break;
                    case "v":
                        float[] v = parseFloats(splitData);
                        Vector4f vec4f = new Vector4f(v[0], v[1], v[2], v.length == 4 ? v[3] : 1.0f);
                        this.vertices.add(new Vertex(vec4f, material));
                        break;
                    case "vn":
                        float[] vn = parseFloats(splitData);
                        this.normals.add(new Vector3f(vn[0], vn[1], vn[2]));
                        break;
                    case "vt":
                        float[] vt = parseFloats(splitData);
                        textureCoordinates.add(new TextureCoordinate(vt[0],
                                vt.length >= 2 ? vt[1] : 1.0f,
                                vt.length >= 3 ? vt[2] : 1.0f));
                        break;
                    case "f":
                        Vertex[] va = new Vertex[splitData.length];
                        for (int i = 0; i < splitData.length; i++)
                        {
                            String[] vtn = splitData[i].split("/");
                            int vIdx = Integer.parseInt(vtn[0]);
                            vIdx = vIdx < 0 ? vertices.size() - 1 : vIdx - 1;
                            Vertex newV = new Vertex(new Vector4f(this.vertices.get(vIdx).pos), material);

                            if(!(vtn.length < 2  || StringUtils.isNullOrEmpty(vtn[1]))) {
                                int vtIdx = Integer.parseInt(vtn[1]);
                                newV.setTextureCoordinate(textureCoordinates.get(vtIdx < 0 ? textureCoordinates.size() - 1 : vtIdx - 1));
                            }

                            if(!(vtn.length < 3  || StringUtils.isNullOrEmpty(vtn[2]))) {
                                int vtIdx = Integer.parseInt(vtn[2]);
                                newV.setNormal(new Vector3f(normals.get(vtIdx < 0 ? normals.size() - 1 : vtIdx - 1)));
                            }
                            va[i] = newV;
                        }

                        Face face = new Face(va, material);

                        if(groupList.isEmpty())
                        {
                            if(mtlManager.hasGroup(MaterialManager.DEFAULT_GROUP_NAME))
                            {
                                mtlManager.getGroup(MaterialManager.DEFAULT_GROUP_NAME).add(face);
                            }
                            else
                            {
                                LinkedHashSet<Face> group = new LinkedHashSet<>();
                                group.add(face);
                                mtlManager.addGroup(MaterialManager.DEFAULT_GROUP_NAME, group);
                            }
                        }
                        else
                        {
                            for (String s : groupList)
                            {
                                if(mtlManager.hasGroup(s))
                                {
                                    mtlManager.getGroup(s).add(face);
                                }
                                else
                                {
                                    LinkedHashSet<Face> group = new LinkedHashSet<>();
                                    group.add(face);
                                    mtlManager.addGroup(s, group);
                                }
                            }
                        }
                        break;
                    case "g":
                        groupList.clear();
                        groupList.addAll(Arrays.asList(WHITE_SPACE.split(data)));
                        break;
                    case "o":
                        groupList.clear();
                        groupList.add(data);
                        break;
                    default: //unknown command
                        CompoundWeapon.LOGGER.warn("CompoundWeapon.OBJParser: command '{}' (model: '{}') is not currently supported, skipping. Line: {} '{}'",
                                lineData[0], resourceLocation, lineNum, line);
                }
            }
            return new CustomModel(mtlManager, resourceLocation, ModelType.OBJ);
        }
    }

    public static class Face {
        private final Vertex[] vertices;
        private final Material material;

        public Face(Vertex[] va, Material material) {
            this.vertices = va;
            this.material = material;
        }

        public boolean isTriangles()
        {
            return vertices.length == 3;
        }

        public Face bake(TRSRTransformation transformation)
        {
            Vertex[] ves = new Vertex[vertices.length];

            for(int i = 0; i < vertices.length; i++)
            {
                Vertex v = vertices[i];
                Vector4f pos = new Vector4f(v.pos);
                transformation.transformPosition(pos);
                Vertex v1 = new Vertex(pos, v.getMaterial());

                if(v.getTextureCoordinate() != null) {
                    v1.setTextureCoordinate(v.getTextureCoordinate());
                }

                if(v.getNormal() != null) {
                    Vector3f normal = new Vector3f(v.normal.x, v.normal.y, v.normal.z);
                    transformation.transformNormal(normal);
                    v1.setNormal(normal);
                }
                ves[i] = v1;
            }
            return new Face(ves, this.material);
        }

        public Vector3f getNormal()
        {
            Vector3f a;
            Vector3f b;
            if(isTriangles())
            {
                a = vertices[1].getPos3();
                b = vertices[2].getPos3();
                a.sub(vertices[0].getPos3());
                b.sub(vertices[0].getPos3());
            }
            else
            {
                a = vertices[2].getPos3();
                b = vertices[3].getPos3();
                a.sub(vertices[0].getPos3());
                b.sub(vertices[1].getPos3());
            }
            a.cross(a, b);
            a.normalize();
            return a;
        }
    }

    public static class Vertex {
        private final Vector4f pos;
        private Material material;
        private Vector3f normal;
        private TextureCoordinate textureCoordinate;

        public Vertex(Vector4f pos, Material material) {
            this.pos = pos;
            this.material = material;
        }

        public void setTextureCoordinate(TextureCoordinate textureCoordinate) {
            this.textureCoordinate = textureCoordinate;
        }

        public void setMaterial(Material material) {
            this.material = material;
        }

        public void setNormal(Vector3f normal) {
            this.normal = normal;
        }

        public Vector4f getPos() {
            return pos;
        }

        public Vector3f getPos3()
        {
            return new Vector3f(pos.x, pos.y, pos.z);
        }

        public Material getMaterial() {
            return material;
        }

        public Vector3f getNormal() {
            return normal;
        }

        public TextureCoordinate getTextureCoordinate() {
            return textureCoordinate;
        }
    }

    public static class Material {
        public static final String DEFAULT_NAME = "CustomModel.Default.Material.Name";
        private Vector4f color;
        private Texture texture;
        private String mtlName;

        public Material() {
            this(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
        }

        public Material(Vector4f color)
        {
            this(color, Texture.WHITE, DEFAULT_NAME);
        }

        public Material(Vector4f color, Texture texture, String name)
        {
            this.color = color;
            this.texture = texture;
            this.mtlName = name;
        }

        public void setName(String name) {
            mtlName = name;
        }

        public void setColor(Vector4f color) {
            this.color = color;
        }

        public void setTexture(Texture texture) {
            this.texture = texture;
        }

        public Vector4f getColor() {
            return color;
        }

        public Texture getTexture() {
            return texture;
        }

        public String getMtlName() {
            return mtlName;
        }

        public boolean isWhite() {
            return texture == Texture.WHITE;
        }
    }

    public static class TextureCoordinate {
        private static final TextureCoordinate[] DEFAULT_COORDINATES = {
                new TextureCoordinate(0.0f, 0.0f, 1.0f),
                new TextureCoordinate(1.0f, 0.0f, 1.0f),
                new TextureCoordinate(1.0f, 1.0f, 1.0f),
                new TextureCoordinate(0.0f, 1.0f, 1.0f)
        };

        private final Vector3f data;

        public TextureCoordinate(float u, float v, float w)
        {
            this.data = new Vector3f(u, v, w);
        }

        public Vector3f getUVW()
        {
            return data;
        }
    }

    public static class Texture {
        public final static Texture WHITE = new Texture("CustomModel.Default.Texture.Name");
        private final String texturePath;

        public Texture(String path) {
            texturePath = path;
        }

        public CustomResourceLocation getTexturePath() {
            return new CustomResourceLocation(texturePath, null);
        }
    }
}
