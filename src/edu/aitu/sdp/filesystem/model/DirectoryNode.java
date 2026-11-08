package edu.aitu.sdp.filesystem.model;

import edu.aitu.sdp.filesystem.visitor.Visitor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DirectoryNode implements Element {

    private final String name;
    private final Map<String, Element> childrenByName;
    private DirectoryNode parent;

    public DirectoryNode(String name) {
        this.name = FileNode.validateNodeName(name);
        this.childrenByName = new LinkedHashMap<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DirectoryNode getParent() {
        return parent;
    }

    public DirectoryNode addChild(Element child) {
        if (child == null) {
            throw new IllegalArgumentException("Child element must not be null.");
        }
        ensureNotCreatingCycle(child);
        ensureSingleParentOwnership(child);
        ensureUniqueSiblingName(child.getName());

        childrenByName.put(child.getName(), child);
        child.attachToParent(this);
        return this;
    }

    public List<Element> getChildren() {
        return List.copyOf(childrenByName.values());
    }

    boolean containsDirectChildInstance(Element candidate) {
        if (candidate == null) {
            return false;
        }
        return childrenByName.get(candidate.getName()) == candidate;
    }

    @Override
    public void attachToParent(DirectoryNode candidateParent) {
        if (candidateParent == null) {
            throw new IllegalArgumentException("Parent directory must not be null.");
        }
        if (this.parent != null) {
            throw new IllegalArgumentException(
                    "Multiple parent ownership is prohibited: directory '" + name
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

    private void ensureNotCreatingCycle(Element candidateChild) {
        for (DirectoryNode cursor = this; cursor != null; cursor = cursor.getParent()) {
            if (cursor == candidateChild) {
                throw new IllegalArgumentException(
                        "Cycle detected: cannot add directory '" + candidateChild.getName()
                                + "' into itself or its descendant '" + this.name + "'."
                );
            }
        }
    }

    private void ensureSingleParentOwnership(Element candidateChild) {
        if (candidateChild.getParent() != null) {
            throw new IllegalArgumentException(
                    "Multiple parent ownership is prohibited: element '" + candidateChild.getName()
                            + "' already belongs to directory '" + candidateChild.getParent().getName() + "'."
            );
        }
    }

    private void ensureUniqueSiblingName(String childName) {
        if (childrenByName.containsKey(childName)) {
            throw new IllegalArgumentException(
                    "Duplicate sibling name is prohibited: an element named '" + childName
                            + "' already exists in directory '" + this.name + "'."
            );
        }
    }
}