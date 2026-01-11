package yt.szczurek.hyrmur.bedwars.resource;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class TeamRes {
    public String name;
    public int color;

    private static HashMap<String, TeamRes> ASSET_MAP;

    public static HashMap<String, TeamRes> getAssetMap() {
        if (ASSET_MAP == null) ASSET_MAP = new HashMap<>();
        return ASSET_MAP;
    }

    public static @Nullable TeamRes byId(String id) {
        return ASSET_MAP.get(id);
    }
}
