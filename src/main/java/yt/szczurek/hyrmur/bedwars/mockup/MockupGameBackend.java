package yt.szczurek.hyrmur.bedwars.mockup;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yt.szczurek.hyrmur.bedwars.GameBackend;

import java.util.HashMap;

public class MockupGameBackend implements GameBackend {
    private HashMap<Player, Pos> playerPositions = new HashMap<>();

    @Override
    public void teleportPlayer(@NotNull Player player, @NotNull Pos pos) {
        playerPositions.put(player, pos);
    }

    public @Nullable Pos getPlayerPos(@NotNull Player player) {
        return playerPositions.get(player);
    }

    @Override
    public void placeStructure(String id, Pos pos) {

    }
}
