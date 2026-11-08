package edu.aitu.sdp.filesystem.visitor;

import edu.aitu.sdp.filesystem.model.DirectoryNode;
import edu.aitu.sdp.filesystem.model.Element;
import edu.aitu.sdp.filesystem.model.FileNode;

public final class StatisticsVisitor implements Visitor {

    private int fileCount;
    private int directoryCount;
    private int maxDepth;
    private int currentDepth;

    @Override
    public void visit(FileNode file) {
        if (file == null) {
            throw new IllegalArgumentException("Visited FileNode must not be null.");
        }
        if (currentDepth == 0) {
            reset();
        }
        fileCount++;
        maxDepth = Math.max(maxDepth, currentDepth);
    }

    @Override
    public void visit(DirectoryNode directory) {
        if (directory == null) {
            throw new IllegalArgumentException("Visited DirectoryNode must not be null.");
        }
        if (currentDepth == 0) {
            reset();
        }
        directoryCount++;
        maxDepth = Math.max(maxDepth, currentDepth);

        currentDepth++;
        try {
            for (Element child : directory.getChildren()) {
                child.accept(this);
            }
        } finally {
            currentDepth--;
        }
    }

    public StatisticsVisitor analyze(Element root) {
        if (root == null) {
            throw new IllegalArgumentException("Root element must not be null.");
        }
        reset();
        root.accept(this);
        return this;
    }

    public int getFileCount() {
        return fileCount;
    }

    public int getDirectoryCount() {
        return directoryCount;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void reset() {
        this.fileCount = 0;
        this.directoryCount = 0;
        this.maxDepth = 0;
        this.currentDepth = 0;
    }
}