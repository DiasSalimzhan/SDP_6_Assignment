package edu.aitu.sdp.filesystem.visitor;

import edu.aitu.sdp.filesystem.model.DirectoryNode;
import edu.aitu.sdp.filesystem.model.Element;
import edu.aitu.sdp.filesystem.model.FileNode;

public final class TotalSizeVisitor implements Visitor {

    private long totalSize;
    private int activeTraversalDepth;

    @Override
    public void visit(FileNode file) {
        if (file == null) {
            throw new IllegalArgumentException("Visited FileNode must not be null.");
        }
        if (activeTraversalDepth == 0) {
            reset();
        }
        totalSize += file.getSize();
    }

    @Override
    public void visit(DirectoryNode directory) {
        if (directory == null) {
            throw new IllegalArgumentException("Visited DirectoryNode must not be null.");
        }
        if (activeTraversalDepth == 0) {
            reset();
        }
        activeTraversalDepth++;
        try {
            for (Element child : directory.getChildren()) {
                child.accept(this);
            }
        } finally {
            activeTraversalDepth--;
        }
    }

    public long computeTotalSize(Element root) {
        if (root == null) {
            throw new IllegalArgumentException("Root element must not be null.");
        }
        reset();
        root.accept(this);
        return getTotalSize();
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void reset() {
        this.totalSize = 0L;
        this.activeTraversalDepth = 0;
    }
}