package RouteMapMaker.models.enums;

import java.util.List;

public enum FileType {
    ERM("路線図メーカーテキスト形式（*.erm）", "*.erm"),
    RMM("路線図メーカー形式（*.rmm）", "*.rmm"),
    TXT("テキストファイル（*.txt）", "*.txt"),
    PNG("PNG形式（*.png）", "*.png"),
    JPG("JPEG形式（*.jpg）", "*.jpg"),
    BMP("Bitmap形式（*.bmp）", "*.bmp"),
    IMAGE("Image Files(jpg,png,gif,bmp)", 
        "*.png", "*.jpg", "*.jpeg", "*.gif","*.bmp", "*.PNG", "*.JPG", "*.JPEG", "*.GIF","*.BMP"),
    ALL("All Files (*.*)", "*.*"),
    ;

    private final String description;
    private final List<String> extensions;

    private FileType(String description, String ...extensions) {
        this.description = description;
        this.extensions = List.of(extensions);
    }

    public String getDescription() {
        return description;
    }

    public List<String> getExtensions() {
        return extensions;
    }
}
