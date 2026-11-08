package edu.aitu.sdp.filesystem.visitor;

import edu.aitu.sdp.filesystem.model.DirectoryNode;
import edu.aitu.sdp.filesystem.model.FileNode;

public interface Visitor {

    void visit(FileNode file);

    void visit(DirectoryNode directory);
}