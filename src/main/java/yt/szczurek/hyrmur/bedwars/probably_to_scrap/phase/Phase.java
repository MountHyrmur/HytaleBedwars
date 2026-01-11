package yt.szczurek.hyrmur.bedwars.probably_to_scrap.phase;

import org.jetbrains.annotations.Nullable;
import yt.szczurek.hyrmur.bedwars.BedwarsGame;
import yt.szczurek.hyrmur.bedwars.GameBackend;
import yt.szczurek.hyrmur.bedwars.mockup.Player;

public abstract class Phase {
    protected GameBackend backend;
    protected BedwarsGame game;

    public Phase(GameBackend backend, BedwarsGame game) {
        this.backend = backend;
        this.game = game;
    }

    abstract void init();
    abstract void deinit();

    abstract void tick();

    abstract void addPlayer(Player player);
    abstract void removePlayer(Player player);

    abstract @Nullable Phase nextPhase();
}
