import java.beans.VetoableChangeListener;
import java.util.*;

public class ErdosNumbers {
    /**
     * String representing Paul Erdos's name to check against
     */
    public static final String ERDOS = "Paul Erdös";

    List<String> papers;
    Graph graph;

    /**
     * Initialises the class with a list of papers and authors.
     *
     * Each element in 'papers' corresponds to a String of the form:
     * 
     * [paper name]:[author1][|author2[|...]]]
     *
     * Note that for this constructor and the below methods, authors and papers
     * are unique (i.e. there can't be multiple authors or papers with the exact same name or title).
     * 
     * @param papers List of papers and their authors
     */
    public ErdosNumbers(List<String> papers) {
        // TODO: implement this
        this.papers = papers;
        graph = new Graph();
        for (String paper : papers) {
            String[] parts = paper.split(":");
            String paperName = parts[0];
            String[] authors = parts[1].split("\\|");
            if (authors.length == 1) {
                graph.addEdge(paperName, authors[0], authors[0]);
                continue;
            }
            for (int i = 0; i < authors.length; i++) {
                String primary = authors[i];
                for (int j = i + 1; j < authors.length; j++) {
                    String secondary = authors[j];
                    graph.addEdge(paperName, primary, secondary);
                }
            }
        }
        graph.weightEdges();
        Vertex src = graph.vertexList.get(ERDOS);
        graph.calculatePaths(src);
    }
    
    /**
     * Gets all the unique papers the author has written (either solely or
     * as a co-author).
     * 
     * @param author to get the papers for.
     * @return the unique set of papers this author has written.
     */
    public Set<String> getPapers(String author) {
        return graph.getIncidentEdges(author);
    }

    /**
     * Gets all the unique co-authors the author has written a paper with.
     *
     * @param author to get collaborators for
     * @return the unique co-authors the author has written with.
     */
    public Set<String> getCollaborators(String author) {
        return graph.getCollaborators(author);
    }

    /**
     * Checks if Erdos is connected to all other author's given as input to
     * the class constructor.
     * 
     * In other words, does every author in the dataset have an Erdos number?
     * 
     * @return the connectivity of Erdos to all other authors.
     */
    public boolean isErdosConnectedToAll() {
        // TODO: implement this
        for (String author : graph.vertexList.keySet()) {
            Vertex vertex = graph.vertexList.get(author);
            calculateErdosNumber(vertex.author);
            if (vertex.erdosNo == Integer.MAX_VALUE) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Calculate the Erdos number of an author. 
     * 
     * This is defined as the length of the shortest path on a graph of paper 
     * collaborations (as explained in the assignment specification).
     * 
     * If the author isn't connected to Erdos (and in other words, doesn't have
     * a defined Erdos number), returns Integer.MAX_VALUE.
     * 
     * Note: Erdos himself has an Erdos number of 0.
     * 
     * @param author to calculate the Erdos number of
     * @return authors' Erdos number or otherwise Integer.MAX_VALUE
     */
    public int calculateErdosNumber(String author) {
        Vertex dest = graph.vertexList.get(author);
        
        return dest.erdosNo;
    }

    /**
     * Gets the average Erdos number of all the authors on a paper.
     * If a paper has just a single author, this is just the author's Erdos number.
     *
     * Note: Erdos himself has an Erdos number of 0.
     *
     * @param paper to calculate it for
     * @return average Erdos number of paper's authors
     */
    public double averageErdosNumber(String paper) {
        int erdosTotal = 0;
        int authorCount = 0;
        Set<Vertex> authors = new HashSet<>();
        LinkedList<Vertex> vertices = graph.getVertices(paper);
        for (Vertex vertex : vertices) {
            erdosTotal += vertex.erdosNo;
            authorCount++;
        }
        if (authorCount == 0) {
            authorCount = 1;
        }

        return ((double) erdosTotal)/authorCount;
    }

    /**
     * Calculates the "weighted Erdos number" of an author.
     * 
     * If the author isn't connected to Erdos (and in other words, doesn't have
     * an Erdos number), returns Double.MAX_VALUE.
     *
     * Note: Erdos himself has a weighted Erdos number of 0.
     * 
     * @param author to calculate it for
     * @return author's weighted Erdos number
     */
    public double calculateWeightedErdosNumber(String author) {
        Vertex dest = graph.vertexList.get(author);
        return graph.vertexList.get(author).weightedErdosNo;
    }

    private class Graph {
        private HashMap<Vertex, LinkedList<Edge>> adjMap;
        private HashMap<String, LinkedList<Vertex>> edgeList;
        private HashMap<String, Vertex> vertexList;

        private Graph() {
            adjMap = new HashMap<>();
            edgeList = new HashMap<>();
            vertexList = new HashMap<>();
        }

        private void weightEdges() {
            LinkedList<Edge> visited = new LinkedList<>();
            for (Vertex src : adjMap.keySet()) {
                LinkedList<Edge> edges = adjMap.get(src);
                for (Edge edge : edges) {
                    if (visited.contains(edge)) {
                        continue;
                    } else {
                        edge.weight = 0;
                    }
                    edge.addWeight(1);
                    for (Edge edge1 : edges) {
                        if (!edge.equals(edge1)) {
                            if ((edge.primary.equals(edge1.primary) &&
                                edge.secondary.equals(edge1.secondary)) ||
                                (edge.secondary.equals(edge1.primary) &&
                                edge.primary.equals(edge1.secondary))) {
                                edge.addWeight(1);
                            }
                            visited.add(edge);
                        }
                    }
                    edge.invertWeight();
                }
            }
        }

        private LinkedList<Vertex> getVertices(String paper) {
            return edgeList.get(paper);
        }

        private void addEdge(String paper, String src, String dest) {
            LinkedList<Edge> srcTmp;
            LinkedList<Edge> destTmp;
            LinkedList<Vertex> edgeTmp;
            Vertex primary;
            Vertex secondary;

            // Handle existing authors
            if (vertexList.containsKey(src)) {
                primary = vertexList.get(src);
            } else {
                // Initialise vertex
                primary = new Vertex(src);
                vertexList.put(src, primary);
            }
            if (vertexList.containsKey(dest)) {
                secondary = vertexList.get(dest);
            } else {
                secondary = new Vertex(dest);
                vertexList.put(dest, secondary);
            }
            // Update adjacency list
            Edge edge = new Edge(paper, primary, secondary);
            srcTmp = updateAdjList(primary, edge);
            destTmp = updateAdjList(secondary, edge);
            // Update edge list if the edge is not a loop (single author)
            if (!src.equals(dest)) {
                edgeTmp = updateEdgeList(paper, primary);
                if (edgeTmp != null) {
                    edgeList.put(paper, edgeTmp);
                }
                edgeTmp = updateEdgeList(paper, secondary);
                if (edgeTmp != null) {
                    edgeList.put(paper, edgeTmp);
                }
            }
            if (srcTmp != null) {
                adjMap.put(primary, srcTmp);
            }
            if (destTmp != null) {
                adjMap.put(secondary, destTmp);
            }
        }

        private LinkedList<Edge> updateAdjList(Vertex entry, Edge edge) {
            LinkedList<Edge> tmp;

            if (adjMap.containsKey(entry)) {
                tmp = adjMap.get(entry);
                if (tmp.contains(edge)) {
                    return null;
                } else {
                    tmp.add(edge);
                }
            } else {
                tmp = new LinkedList<>();
                tmp.add(edge);
            }

            return tmp;
        }

        private LinkedList<Vertex> updateEdgeList(String paper, Vertex vertex) {
            LinkedList<Vertex> tmp;

            if (edgeList.containsKey(paper)) {
                tmp = edgeList.get(paper);
                if (tmp.contains(vertex)) {
                    return null;
                } else {
                    tmp.add(vertex);
                }
            } else {
                tmp = new LinkedList<>();
                tmp.add(vertex);
            }

            return tmp;
        }

        private Set<String> getIncidentEdges(String author) {
            Set<String> result = new HashSet<>();
            Vertex vertex = new Vertex(author);
            LinkedList<Edge> edges = adjMap.get(vertex);

            if (edges == null) {
                return result;
            }
            for (Edge edge : edges) {
                result.add(edge.label);
            }
            return result;
        }

        private Set<String> getCollaborators(String author) {
            Set<String> result = new HashSet<>();
            Vertex primary = new Vertex(author);
            LinkedList<Edge> edges = adjMap.get(primary);
            int count = 0;

            if (edges == null) {
                return result;
            }
            for (Edge edge : edges) {
                if (edge.primary.author.equals(author)) {
                    result.add(edge.secondary.author);
                } else {
                    result.add(edge.primary.author);
                }
            }
            return result;
        }

        private void calculatePaths(Vertex src) {
            // Perform BFS
            SearchNode curr;
            SearchNode next;
            int newDepth;
            double newWeightedDepth;
            LinkedList<Vertex> visited = new LinkedList<>();
            LinkedList<SearchNode> queue = new LinkedList<>();
            SearchNode node = new SearchNode(src, 0, 0);
            queue.add(node);
            visited.add(src);

            if (src.author.equals(ERDOS)) {
                src.weightedErdosNo = 0;
                src.erdosNo = 0;
            }
            while (queue.size() != 0) {
                curr = queue.poll();
                if (adjMap.get(curr.vertex) == null) {
                    break;
                }

                for (Edge connectedVertex : adjMap.get(curr.vertex)) {
                    newDepth = curr.depth + 1;
                    newWeightedDepth = curr.weightedDepth + connectedVertex.weight;
                    // Determine which node is the unvisited node
                    if (curr.vertex.equals(connectedVertex.primary)) {
                        // Determine if next node has been visited
                        if (!visited.contains(connectedVertex.secondary)) {
                            // Calculate fields and add to queue
                            visited.add(connectedVertex.secondary);
                            next = new SearchNode(connectedVertex.secondary,
                                    newDepth, newWeightedDepth);
                            connectedVertex.secondary.erdosNo = newDepth;
                            connectedVertex.secondary.weightedErdosNo =
                                        newWeightedDepth;
                            queue.add(next);
                        } else {
                            // Handle a indirect path having a lighter path
                            if (newWeightedDepth < connectedVertex.secondary.
                                    weightedErdosNo) {
                                connectedVertex.secondary.
                                        weightedErdosNo = newWeightedDepth;
                                next = new SearchNode(connectedVertex.secondary,
                                        newDepth, newWeightedDepth);
                                queue.add(next);
                            }
                        }
                    } else {
                        if (!visited.contains(connectedVertex.primary)) {
                            visited.add(connectedVertex.primary);
                            next = new SearchNode(connectedVertex.primary,
                                    newDepth, newWeightedDepth);
                            connectedVertex.primary.erdosNo = newDepth;
                            connectedVertex.primary.weightedErdosNo =
                                        newWeightedDepth;
                            queue.add(next);
                        } else {
                            if (newWeightedDepth < connectedVertex.primary.
                                    weightedErdosNo) {
                                connectedVertex.primary.
                                        weightedErdosNo = newWeightedDepth;
                                next = new SearchNode(connectedVertex.primary,
                                        newDepth, newWeightedDepth);
                                queue.add(next);
                            }
                        }
                    }
                }
            }
        }

        private class SearchNode {
            Vertex vertex;
            int depth;
            double weightedDepth;

            private SearchNode(Vertex vertex, int depth, double weightedDepth) {
                this.vertex = vertex;
                this.depth = depth;
                this.weightedDepth = weightedDepth;
            }
        }
    }

    private class Edge {
        String label;
        Vertex primary;
        Vertex secondary;
        double weight;

        private Edge(String paper, Vertex src, Vertex dest) {
            this.label = paper;
            this.primary = src;
            this.secondary = dest;
        }

        private String getPaper() {
            return label;
        }

        private Set<String> getVertices() {
            return Set.of(primary.author, secondary.author);
        }

        private void addWeight(int i) {
            weight += i;
        }

        private void invertWeight() {
            if (weight != 0) {
                weight = 1/weight;
            }
        }

    }

    private class Vertex {
        String author;
        int erdosNo;
        double weightedErdosNo;

        private Vertex(String author) {
            this.author = author;
            erdosNo = Integer.MAX_VALUE;
            weightedErdosNo = Double.MAX_VALUE;
        }

        private void setErdosNo(int erdosNo) {
            this.erdosNo = erdosNo;
        }

        @Override
        public int hashCode() {
            return author.hashCode();
        }

        @Override
        public boolean equals(Object vertex) {
            if (vertex instanceof Vertex) {
                return author.equals(((Vertex) vertex).author);
            }
            return false;
        }
    }
}
