# Assignment 06 — Visitor Pattern (Advanced File System)

**Group:** Se-2522 

## Project Overview

This project implements an in-memory hierarchical file system in Java 17 using the **Visitor** design pattern and double dispatch.

### Packages and Classes

- **edu.aitu.sdp.filesystem.model**
  - **Element** — common interface for file system nodes with the **accept(Visitor)** method.
  - **FileNode** — concrete element representing a file with a name and non-negative byte size.
  - **DirectoryNode** — concrete element representing a directory that stores child elements and prevents negative sizes, duplicate sibling names, multiple parent ownership, and cycles.

- **edu.aitu.sdp.filesystem.visitor**
  - **Visitor** — interface declaring overloaded **visit(FileNode)** and **visit(DirectoryNode)** methods.
  - **TotalSizeVisitor** — calculates the total byte size of all files in a visited subtree.
  - **SearchVisitor** — filters files by filename criterion and minimum byte size, returning sorted root-relative paths.
  - **StatisticsVisitor** — collects total file count, directory count (including the root directory), and maximum tree depth (root depth = 0).

- **edu.aitu.sdp.filesystem**
  - **Main** — executable class demonstrating all three visitors on a sample file system tree.
  - **VisitorPatternTest** — JUnit 5 test suite covering verification checks **T1–T8**.

## How to Run

1. **Run automated JUnit 5 tests:**
   mvn test

2. **Run the application (Main):**
   mvn compile exec:java
