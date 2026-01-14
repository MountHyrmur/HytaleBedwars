package yt.szczurek.hyrmur.bedwars.probably_to_scrap.phase;

import yt.szczurek.hyrmur.bedwars.BedwarsGame;
import yt.szczurek.hyrmur.bedwars.GameBackend;
import yt.szczurek.hyrmur.bedwars.mockup.Player;

public class SetupPhase extends Phase {


    public SetupPhase(GameBackend backend, BedwarsGame game) {
        super(backend, game);
    }

    @Override
    void init() {
//        for (var level: game.level.islands) {}
    }

    @Override
    void deinit() {

    }

    @Override
    void tick() {

    }

    @Override
    public void addPlayer(Player player) {

    }

    @Override
    public void removePlayer(Player player) {

    }

    @Override
    Phase nextPhase() {
        return new RunningPhase(backend, game);
    }
}
