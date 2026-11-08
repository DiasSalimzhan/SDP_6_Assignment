package edu.aitu.sdp.filesystem.model;

import edu.aitu.sdp.filesystem.visitor.Visitor;

public final class FileNode implements Element {

    private final String name;
    private final long size;
    private DirectoryNode parent;

    public FileNode(String name, long size) {
        this.name = validateNodeName(name);
        this.size = validateNonNegativeSize(size);
    }

    @Override
    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    @Override
    public DirectoryNode getParent() {
        return parent;
    }

    @Override
    public void attachToParent(DirectoryNode candidateParent) {
        if (candidateParent == null) {
            throw new IllegalArgumentException("Parent directory must not be null.");
        }
        if (this.parent != null) {
            throw new IllegalArgumentException(
                    "Multiple parent ownership is prohibited: file '" + name
                            + "' already belongs to directory '" + this.parent.getName() + "'."
            );
        }
        if (!candidateParent.containsDirectChildInstance(this)) {
            throw new IllegalArgumentException(
                    "Parent attachment must be performed through DirectoryNode.addChild(Element)."
            );
        }
        this.parent = candidateParent;
    }

    @Override
    public void accept(Visitor visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException("Visitor must not be null.");
        }
        visitor.visit(this);
    }

    static String validateNodeName(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            throw new IllegalArgumentException("Element name must not be null or blank.");
        }
        String trimmed = rawName.trim();
        if (trimmed.contains("/") || trimmed.contains("\\")) {
            throw new IllegalArgumentException(
                    "Element name must not contain path separators ('/' or '\\'): " + rawName
            );
        }
        return trimmed;
    }

    private static long validateNonNegativeSize(long candidateSize) {
        if (candidateSize < 0) {
            throw new IllegalArgumentException(
                    "File size must be non-negative, but received: " + candidateSize
            );
        }
        return candidateSize;
    }
}