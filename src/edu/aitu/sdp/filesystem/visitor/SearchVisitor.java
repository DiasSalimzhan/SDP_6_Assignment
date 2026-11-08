package edu.aitu.sdp.filesystem.visitor;

import edu.aitu.sdp.filesystem.model.DirectoryNode;
import edu.aitu.sdp.filesystem.model.Element;
import edu.aitu.sdp.filesystem.model.FileNode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;

public final class SearchVisitor implements Visitor {

    private final Predicate<String> fileNameCriterion;
    private final long minSizeBytes;
    private final Deque<String> relativeDirectorySegments;
    private final List<String> matchedPaths;
    private int activeTraversalDepth;

    public SearchVisitor(Predicate<String> fileNameCriterion, long minSizeBytes) {
        if (fileNameCriterion == null) {
            throw new IllegalArgumentException("Filename criterion predicate must not be null.");
        }
        if (minSizeBytes < 0) {
            throw new IllegalArgumentException(
                    "Minimum size threshold must be non-negative, but received: " + minSizeBytes
            );
        }
        this.fileNameCriterion = fileNameCriterion;
        this.minSizeBytes = minSizeBytes;
        this.relativeDirectorySegments = new ArrayDeque<>();
        this.matchedPaths = new ArrayList<>();
        this.activeTraversalDepth = 0;
    }

    public static SearchVisitor bySuffix(String suffix, long minSizeBytes) {
        if (suffix == null || suffix.isBlank()) {
            throw new IllegalArgumentException("Filename suffix criterion must not be null or blank.");
        }
        return new SearchVisitor(fileName -> fileName.endsWith(suffix), minSizeBytes);
    }

    public static SearchVisitor bySubstring(String substring, long minSizeBytes) {
        if (substring == null || substring.isEmpty()) {
            throw new IllegalArgumentException("Filename substring criterion must not be null or empty.");
        }
        return new SearchVisitor(fileName -> fileName.contains(substring), minSizeBytes);
    }

    @Override
    public void visit(FileNode file) {
        if (file == null) {
            throw new IllegalArgumentException("Visited FileNode must not be null.");
        }
        if (activeTraversalDepth == 0) {
            reset();
        }
        if (matchesBothCriteria(file)) {
            matchedPaths.add(buildRootRelativePath(file.getName()));
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
        if (directory == null) {
            throw new IllegalArgumentException("Visited DirectoryNode must not be null.");
        }
        boolean isRootDirectory = (activeTraversalDepth == 0);
        if (isRootDirectory) {
            reset();
        } else {
            relativeDirectorySegments.addLast(directory.getName());
        }

        activeTraversalDepth++;
        try {
            for (Element child : directory.getChildren()) {
                child.accept(this);
            }
        } finally {
            activeTraversalDepth--;
            if (!isRootDirectory) {
                relativeDirectorySegments.removeLast();
            }
        }
    }

    public List<String> search(Element root) {
        if (root == null) {
            throw new IllegalArgumentException("Root element must not be null.");
        }
        reset();
        root.accept(this);
        return getMatchingPaths();
    }

    public List<String> getMatchingPaths() {
        return matchedPaths.stream()
                .sorted()
                .toList();
    }

    public void reset() {
        this.matchedPaths.clear();
        this.relativeDirectorySegments.clear();
        this.activeTraversalDepth = 0;
    }

    private boolean matchesBothCriteria(FileNode file) {
        return fileNameCriterion.test(file.getName()) && file.getSize() >= minSizeBytes;
    }

    private String buildRootRelativePath(String fileName) {
        if (relativeDirectorySegments.isEmpty()) {
            return fileName;
        }
        return String.join("/", relativeDirectorySegments) + "/" + fileName;
    }
}