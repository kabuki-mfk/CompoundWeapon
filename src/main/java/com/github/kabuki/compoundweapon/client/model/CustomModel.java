package com.github.kabuki.compoundweapon.client.model;

import com.github.kabuki.compoundweapon.CompoundWeapon;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import javax.annotation.Nullable;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class CustomModel implements IModel {
    private final MaterialManager materialManager;
    private final CustomResourceLocation resourceLocation;
    private final ModelType modelType;

    public CustomModel(MaterialManager mtlLib, CustomResourceLocation modelLocation, ModelType type)
    {
        this.materialManager = mtlLib;
        this.resourceLocation = modelLocation;
        this.modelType = type;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return null;
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        List<ResourceLocation> list = Lists.newArrayList();
        for(Material material : materialManager.materials.values())
        {
            ResourceLocation resourceLocation = material.texture.getTexturePath();
            if(list.contains(resourceLocation) || material.isWhite()) continue;
            list.add(resourceLocation);
        }
        return list;
    }

    public static class CustomBakedModel implements IBakedModel
    {

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
            return null;
        }

        @Override
        public boolean isAmbientOcclusion() {
            return false;
        }

        @Override
        public boolean isGui3d() {
            return false;
        }

        @Override
        public boolean isBuiltInRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return null;
        }

        @Override
        public ItemOverrideList getOverrides() {
            return null;
        }
    }
    s
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

            if (!mtlPath.contains("/"))
                mtlPath = location.getNamespace() + mtlPath;
            CustomResourceLocation mtlLocation = new CustomResourceLocation(mtlPath, null);

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
                    String[] splitData = WHITE_SPACE.split(data);

                    switch (lineData[0])
                    {
                        case "newmtl":
                            hasColor = false;
                            hasTexture = false;
                            material = new Material();
                            material.setName(data);
                            break;
                        case "kd":
                        case "ka":
                        case "ks":
                            String[] rgbStr = WHITE_SPACE.split(data, 3);
                            if(!hasColor || lineData[0].equals("kd")) {
                                material.setColor(new Vector4f(Float.parseFloat(rgbStr[0]), Float.parseFloat(rgbStr[0]), Float.parseFloat(rgbStr[0]), 1.0f));
                                hasColor = true;
                            }
                            break;
                        case "map_Kd":
                        case "map_Ka":
                        case "map_Ks":
                            if(!hasTexture || lineData[0].equals("map_Kd")) {
                                Texture texture = null;
                                if(data.contains(" ")) {
                                    String[] mapStr = WHITE_SPACE.split(data);
                                    material.setTexture(new Texture(mapStr[mapStr.length - 1]));
                                }
                                else {
                                    texture = new Texture(data);
                                }
                                material.setTexture(texture);
                                hasTexture = true;
                            }
                            break;
                        case "d":
                            float alpha = Float.parseFloat(data);
                            material.getColor().setW(alpha);
                        default:
                            CompoundWeapon.LOGGER.warn("CompoundWeapon.MaterialManager: command '{}' (mtl: '{}') is not currently supported, skipping.", lineData[0], mtlLocation);
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
        private BufferedReader reader;

        private List<String> groupList = Lists.newArrayList();
        private List<Vertex> verteies = Lists.newArrayList();
        private List<Vector3f> normals = Lists.newArrayList();
        private List<TextureCoordinate> textureCoordinates = Lists.newArrayList();

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
                        this.verteies.add(new Vertex(vec4f, material));
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
                        Vertex[] va = new Vertex[4];
                        for (int i = 0; i < splitData.length; i++)
                        {
                            String[] vtn = splitData[i].split("/");
                            int vIdx = Integer.parseInt(vtn[0]);
                            vIdx = vIdx < 0 ? verteies.size() - 1 : vIdx - 1;
                            Vertex newV = new Vertex(new Vector4f(this.verteies.get(vIdx).pos), material);

                            if(!(vtn.length < 2  || StringUtils.isNullOrEmpty(vtn[1]))) {
                                int vtIdx = Integer.parseInt(vtn[1]);
                                newV.setTextureCoordinate(textureCoordinates.get(vtIdx < 0 ? textureCoordinates.size() - 1 : vtIdx - 1));
                            }

                            if(!(vtn.length < 3  || StringUtils.isNullOrEmpty(vtn[2]))) {
                                int vtIdx = Integer.parseInt(vtn[2]);
                                newV.setTextureCoordinate(textureCoordinates.get(vtIdx < 0 ? textureCoordinates.size() - 1 : vtIdx - 1));
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
            return new CustomModel(mtlManager, resourceLocation);
        }
    }

    public static class Face {
        private Vertex[] vertices;
        private Material material;

        public Face(Vertex[] va, Material material) {
            this.vertices = va;
            this.material = material;
        }

        public boolean isTriangles()
        {
            return vertices.length == 3;
        }
    }

    public static class Vertex {
        private Vector4f pos;
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
        private Vector3f data;

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
        private String texturePath;
        private Vector2f position;
        private Vector2f scale;
        private float rotation;

        public Texture(String path) {
            texturePath = path;
        }

        public CustomResourceLocation getTexturePath() {
            return new CustomResourceLocation(texturePath, null);
        }
    }
}
