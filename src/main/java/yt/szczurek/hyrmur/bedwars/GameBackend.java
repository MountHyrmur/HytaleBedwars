package yt.szczurek.hyrmur.bedwars;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yt.szczurek.hyrmur.bedwars.mockup.Player;
import yt.szczurek.hyrmur.bedwars.mockup.Pos;

public interface GameBackend {
    void teleportPlayer(Player player, Pos pos);

    @Nullable Pos getPlayerPos(@NotNull Player player);

    void placeStructure(String id, Pos pos);
}
