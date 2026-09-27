# Graph Algorithms Visualizer

This is a Java Swing program where you can draw a graph with your mouse and run graph algorithms on it. It was given to us as a starter project for an ICE task, and my job was to improve it and add new features.

Kruskal's Algorithm
<img width="800" height="600" alt="4ed4b145-328e-4759-9c12-4cea55d96cbb" src="https://github.com/user-attachments/assets/9dee2b9b-8c3b-4937-a0c8-6c739f20e5a8" />


## What the program does

You can add vertices and weighted edges by clicking on the screen. Then you can pick an algorithm from the menu and click a starting vertex to see the result. The program supports:

- Breadth-First Search (BFS)
- Depth-First Search (DFS)
- Dijkstra's Algorithm (shortest distances)
- Prim's Algorithm (minimum spanning tree)
- Kruskal's Algorithm (minimum spanning tree) – I added this one

## What I added / changed

**1. Kruskal's Algorithm**
I added a new class called `KruskalsAlgorithm`. It sorts all edges from smallest to largest weight and adds them one by one. If an edge would make a cycle, it skips it. To check for cycles I used a Union-Find structure. At the end it shows the chosen edges and the total weight.

**2. Colouring the result step by step**
Before, the program only showed the result as text at the bottom. Now the vertices change colour one by one in the order the algorithm visits them (green is the start vertex, orange is visited). The edges that are part of the result become thick and orange. I used a Swing `Timer` for this.

**3. Error messages**
Before, if you typed something wrong nothing happened. Now the program shows a message, for example when:
- the edge weight is negative (Dijkstra doesn't work correctly with negative weights)
- the weight is not a number
- you try to connect a vertex to itself or add the same edge twice
- the vertex ID already exists or is longer than 1 character

Also, when adding an edge, the first vertex you click turns blue so you can see what you selected.

**4. Fixing problems**
- The project didn't run at first. The `pom.xml` was set to Java 25 and had the wrong main class, so I changed it to Java 21 and `visualizer.GraphVisualizer`.
- If you pressed Cancel when entering an edge weight, you couldn't add that edge again later. I fixed this.
- Dijkstra crashed when there was only one vertex. I fixed this too.

## How to run it

You need Java 21 (JDK) and an IDE like NetBeans or IntelliJ.

1. Open the `GraphVisualizer` folder as a project (the one with `pom.xml` inside).
2. Clean and Build.
3. Run the project.

Note: on Windows, put the project in a folder without Turkish characters in the path (for example `C:\JavaProjects`). I had a `ClassNotFoundException` because of this.

## How to use it

1. **Mode → Add a Vertex**, click on the screen and type a letter.
2. **Mode → Add an Edge**, click two vertices and type the weight.
3. Choose an algorithm from the **Algorithms** menu and click the start vertex.
4. **File → New** clears everything.

You can also remove vertices and edges from the Mode menu.

## Screenshots

All examples use the same graph and start from vertex A.

**Breadth-First Search** – result: `BFS : A -> C -> B -> D`

BFS
<img width="800" height="600" alt="da238916-8f45-40ce-994c-1e0f9af42e15" src="https://github.com/user-attachments/assets/51df2b50-50fb-487a-bfba-18971e7676ca" />


**Depth-First Search** – result: `DFS : A - C - D - B`

DFS 
<img width="800" height="600" alt="52cc7130-b0b5-4714-a24c-aa5eac33cb77" src="https://github.com/user-attachments/assets/1fa43fdf-94cb-40e4-8c77-f55ba5fbb26a" />
 `B=A, C=A, D=B`

**Dijkstra's Algorithm** – shortest distances from A:

Dijkstra
<img width="800" height="600" alt="4a6d473d-292e-493e-9f0e-aa8d240e31b4" src="https://github.com/user-attachments/assets/4ab75986-cfd0-4408-90b8-ff55aac5252a" />


**Prim's Algorithm** – parent of each vertex in the tree:

Prim
<img width="800" height="600" alt="aa65ac69-bdab-4116-b0a1-c71a8a528647" src="https://github.com/user-attachments/assets/0bd4a89c-3329-4a5a-9192-fe857ec7f4c6" />


**Kruskal's Algorithm** – the edge C-D (5) is skipped because it would make a cycle



**Error message** – when a negative weight is entered

Error message
<img width="800" height="600" alt="c5c8feb8-a651-49fc-ac0c-02932b44e4bf" src="https://github.com/user-attachments/assets/c0b7816e-f25d-4991-bc18-ab2060c27a6d" />


## Skills I used

- Java Swing (windows, menus, dialogs, drawing shapes)
- Event handling (mouse clicks, menu clicks, timer)
- Data structures (maps, lists, queues, sets, union-find)
- Graph algorithms
- Git and GitHub

## Technologies

- Java 21
- Java Swing
- Maven
- NetBeans
- GitHub

## Why this project is good for my portfolio

This project shows that I can work on code that someone else wrote, understand it, fix problems in it and add new features without breaking it. It also shows both GUI programming and algorithms in one project.

What I learned:
- the difference between BFS, DFS, Dijkstra, Prim and Kruskal, and how they choose which vertex or edge comes next
- how to use a Timer in Swing to make simple animations
- why checking user input is important
- how to fix build problems and upload a project to GitHub

## Author
Ayse Kevser ERDOGAN
