package sandbox.shader;

public class Attribute {
    private final int attribLocation;
    private final AttribType type;

    public Attribute(int attribLocation, AttribType type) {
        this.attribLocation = attribLocation;
        this.type = type;
    }

    public int getLocation() {
        return attribLocation;
    }

    public AttribType getType() {
        return type;
    }

    public enum AttribType {
        POSITION(3, "position", DataType.FLOAT),
        NORMAL(3, "normal", DataType.BYTE),
        COLOR(3, "color", DataType.UNSIGNED_BYTE),
        TEXTURE_COORDINATE(2, "texture_coordinate", DataType.SHORT);

        private final int size;
        private final String name;
        private final DataType type;

        AttribType(int size, String name, DataType type) {
            this.size = size;
            this.name = name;
            this.type = type;
        }

        public int getSize() {
            return size;
        }

        public String getName() {
            return name;
        }

        public DataType getType() {
            return type;
        }

        public int getTotalSize() {
            return size * type.getSize();
        }

        public static AttribType fromName(String name) {
            for (AttribType type : values()) {
                if (type.getName().equals(name)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid attribute type: " + name);
        }
    }
}
