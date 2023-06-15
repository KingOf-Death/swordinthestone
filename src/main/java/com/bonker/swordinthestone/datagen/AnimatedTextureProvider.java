package com.bonker.swordinthestone.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class AnimatedTextureProvider implements DataProvider {
    private final PackOutput packOutput;
    private final String modid;
    private final List<AnimatedTextureBuilder> data = new ArrayList<>();

    public AnimatedTextureProvider(PackOutput packOutput, String modid) {
        this.packOutput = packOutput;
        this.modid = modid;
    }

    protected abstract void addFiles();

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        addFiles();
        return generateAll(pOutput);
    }

    protected CompletableFuture<?> generateAll(CachedOutput cache) {
        CompletableFuture<?>[] futures = new CompletableFuture<?>[data.size()];
        int i = 0;
        for (AnimatedTextureBuilder builder : data) {
            Path target = packOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(modid).resolve("textures").resolve(builder.path + ".png.mcmeta");
            futures[i++] = DataProvider.saveStable(cache, builder.toJson(), target);
        }
        return CompletableFuture.allOf(futures);
    }

    @Override
    public String getName() {
        return "Animated Textures:" + modid;
    }

    public AnimatedTextureBuilder create(String path) {
        AnimatedTextureBuilder builder = new AnimatedTextureBuilder(path);
        data.add(builder);
        return builder;
    }

    public static class AnimatedTextureBuilder {
        private final String path;
        private int frametime;
        private JsonArray frames;

        public AnimatedTextureBuilder(String path) { // ex. block/texture or item/texture
            this.path = path;
        }

        public JsonObject toJson() {
            JsonObject root = new JsonObject();
            JsonObject animation = new JsonObject();
            root.add("animation", animation);
            animation.add("frametime", new JsonPrimitive(frametime));
            if (frames != null) {
                animation.add("frames", frames);
            }
            return root;
        }

        public AnimatedTextureBuilder frametime(int frametime) {
            this.frametime = frametime;
            return this;
        }

        public AnimatedTextureBuilder frames(@Nullable int[] framesArr) {
            if (framesArr == null) return this;
            frames = new JsonArray();
            for (int i : framesArr) {
                frames.add(new JsonPrimitive(i));
            }
            return this;
        }
    }
}
