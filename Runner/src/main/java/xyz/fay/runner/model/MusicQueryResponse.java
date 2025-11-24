package xyz.fay.runner.model;

public class MusicQueryResponse {
    private final String display;
    private final String ext;
    private final String name;
    private final String type;

    public MusicQueryResponse(String display, String ext, String name, String type) {
        this.display = display;
        this.ext = ext;
        this.name = name;
        this.type = type;
    }

    public String getDisplay() {
        return display;
    }

    public String getExt() {
        return ext;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
