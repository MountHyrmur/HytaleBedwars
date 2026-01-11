package yt.szczurek.hyrmur.bedwars.resource;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class LevelRes {
    public String id;
    public String prefab;
    public List<ProtectedArea> protectedAreas;
    public List<TeamRes> teams;
    public int teamSize;

    private static HashMap<String, LevelRes> ASSET_MAP;

    public static HashMap<String, LevelRes> getAssetMap() {
        if (ASSET_MAP == null) ASSET_MAP = new HashMap<>();
        return ASSET_MAP;
    }

    public static @Nullable LevelRes byId(String id) {
        return ASSET_MAP.get(id);
    }


}
