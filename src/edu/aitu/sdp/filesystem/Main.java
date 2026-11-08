package edu.aitu.sdp.filesystem;

import edu.aitu.sdp.filesystem.model.DirectoryNode;
import edu.aitu.sdp.filesystem.model.FileNode;
import edu.aitu.sdp.filesystem.visitor.SearchVisitor;
import edu.aitu.sdp.filesystem.visitor.StatisticsVisitor;
import edu.aitu.sdp.filesystem.visitor.TotalSizeVisitor;
import edu.aitu.sdp.filesystem.visitor.Visitor;

import java.util.List;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        DirectoryNode root = buildSampleWorkspace();

        System.out.println("==================================================================");
        System.out.println(" ASTANA IT UNIVERSITY | SE 2522 | ASSIGNMENT 06: VISITOR PATTERN ");
        System.out.println("==================================================================");
        System.out.println("Visited Root Directory: " + root.getName());
        System.out.println();

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);
        long rootTotalBytes = sizeVisitor.getTotalSize();

        DirectoryNode servicesSubtree = findDirectSubdirectory(root, "services");
        servicesSubtree.accept(sizeVisitor);
        long servicesTotalBytes = sizeVisitor.getTotalSize();

        System.out.println("[1] TotalSizeVisitor Results:");
        System.out.println("    - Entire '" + root.getName() + "' tree total size : " + rootTotalBytes + " bytes");
        System.out.println("    - Subtree 'services' total size                : " + servicesTotalBytes + " bytes");
        System.out.println();

        SearchVisitor javaMin5KBSearch = SearchVisitor.bySuffix(".java", 5_000L);
        root.accept(javaMin5KBSearch);
        List<String> javaMatches = javaMin5KBSearch.getMatchingPaths();

        SearchVisitor allFilesAtLeast4KB = new SearchVisitor(name -> true, 4_000L);
        root.accept(allFilesAtLeast4KB);
        List<String> largeFileMatches = allFilesAtLeast4KB.getMatchingPaths();

        System.out.println("[2] SearchVisitor Results (Deterministic Root-Relative Paths):");
        System.out.println("    - Criterion A (name ends with '.java' AND size >= 5,000 B):");
        for (String path : javaMatches) {
            System.out.println("        * " + path);
        }
        System.out.println("    - Criterion B (any filename AND size >= 4,000 B):");
        for (String path : largeFileMatches) {
            System.out.println("        * " + path);
        }
        System.out.println();

        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        root.accept(statsVisitor);
        System.out.println("[3] StatisticsVisitor Results (Root = '" + root.getName() + "', root depth = 0):");
        System.out.println("    - Total File Count      : " + statsVisitor.getFileCount());
        System.out.println("    - Total Directory Count : " + statsVisitor.getDirectoryCount());
        System.out.println("    - Maximum Tree Depth    : " + statsVisitor.getMaxDepth());

        servicesSubtree.accept(statsVisitor);
        System.out.println("    - Subtree 'services'    : files=" + statsVisitor.getFileCount()
                + ", directories=" + statsVisitor.getDirectoryCount()
                + ", maxDepth=" + statsVisitor.getMaxDepth());
        System.out.println("==================================================================");
    }

    private static DirectoryNode buildSampleWorkspace() {
        DirectoryNode workspace = new DirectoryNode("aitu-cloud-platform");
        DirectoryNode config = new DirectoryNode("config");
        DirectoryNode services = new DirectoryNode("services");
        DirectoryNode auth = new DirectoryNode("auth");
        DirectoryNode billing = new DirectoryNode("billing");
        DirectoryNode docs = new DirectoryNode("docs");
        DirectoryNode archive = new DirectoryNode("archive");

        config.addChild(new FileNode("application.yml", 3_800L));

        auth.addChild(new FileNode("AuthService.java", 8_400L))
                .addChild(new FileNode("HealthCheck.java", 1_500L));

        billing.addChild(new FileNode("OrderEngine.java", 5_000L))
                .addChild(new FileNode("schema.sql", 6_200L));

        services.addChild(auth)
                .addChild(billing);

        docs.addChild(new FileNode("architecture.pdf", 10_800L));

        workspace.addChild(new FileNode("README.md", 1_250L))
                .addChild(config)
                .addChild(services)
                .addChild(docs)
                .addChild(archive);

        return workspace;
    }

    private static DirectoryNode findDirectSubdirectory(DirectoryNode root, String name) {
        var subdirHolder = new DirectoryNode[1];
        for (var child : root.getChildren()) {
            if (child.getName().equals(name)) {
                child.accept(new Visitor() {
                    @Override
                    public void visit(FileNode file) {
                    }

                    @Override
                    public void visit(DirectoryNode directory) {
                        subdirHolder[0] = directory;
                    }
                });
            }
        }
        return subdirHolder[0];
    }
}