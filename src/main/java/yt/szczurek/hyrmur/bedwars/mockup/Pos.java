package yt.szczurek.hyrmur.bedwars.mockup;

public record Pos(float x, float y, float z) {
    static Pos ZERO = new Pos(0, 0,0);
}
