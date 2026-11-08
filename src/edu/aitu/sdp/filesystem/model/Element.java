package edu.aitu.sdp.filesystem.model;

import edu.aitu.sdp.filesystem.visitor.Visitor;

public interface Element {

    String getName();

    DirectoryNode getParent();

    void attachToParent(DirectoryNode candidateParent);

    void accept(Visitor visitor);
}