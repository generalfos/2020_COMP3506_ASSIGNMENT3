import java.util.*;

public class FactChecker {

    /**
     * Checks if a list of facts is internally consistent. 
     * That is, can they all hold true at the same time?
     * Or are two (or potentially more) facts logically incompatible?
     * 
     * @param facts list of facts to check consistency of
     * @return true if all the facts are internally consistent, otherwise false.
     */
    public static boolean areFactsConsistent(List<Fact> facts) {
        Diagraph graph = constructGraph(facts);

        return !graph.containsCycle();
    }

    private static Diagraph constructGraph(List<Fact> facts) {
        Diagraph graph = new Diagraph();

        for (Fact fact : facts) {
            String personA = fact.getPersonA();
            String personB = fact.getPersonB();
            if (fact.getType().equals(Fact.FactType.TYPE_ONE)) {
                graph.addEdge(personA, personB, 0);
            } else {
                graph.addEdge(personA, personB, 1);
            }
        }
        return graph;
    }

    private static class Diagraph {
        private HashMap<String, LinkedList<Edge>> adjMap;

        private Diagraph() {
            adjMap = new HashMap<>();
        }

        private void addEdge(String src, String dest, int weight) {
            LinkedList<Edge> tmp;

            Edge edge = new Edge(src, dest, weight);

            tmp = updateAdjMap(src, edge);
            if (tmp != null)  {
                adjMap.put(src, tmp);
            }
            if (weight != 0) {
                tmp = updateAdjMap(dest, edge);
                if (tmp != null) {
                    adjMap.put(dest, tmp);
                }
            }
        }

        private LinkedList<Edge> updateAdjMap(String vertex, Edge edge) {
            LinkedList<Edge> tmp;
            if (adjMap.containsKey(vertex)) {
                tmp = adjMap.get(vertex);
                if (tmp.contains(edge)) {
                    return null;
                } else {
                    tmp.add(edge);
                }
            } else {
                tmp = new LinkedList<>();
                tmp.add(edge);
            }
            Collections.sort(tmp, (Edge e1, Edge e2) -> e1.weight - e2.weight);
            return tmp;
        }

        private boolean containsCycle() {
            // Perform BFS
            SearchNode curr;
            SearchNode next;
            String destination;
            HashMap<String, SearchNode> visited = new HashMap<>();
            LinkedList<Edge> explored = new LinkedList<>();
            LinkedList<SearchNode> queue = new LinkedList<>();
            int count = 0;

            for (String vertex : adjMap.keySet()) {
                if (visited.containsKey(vertex)) {
                    continue;
                }
                SearchNode src = new SearchNode(vertex, 0, count);
                queue.add(src);
                visited.put(src.person, src);
                while (queue.size() != 0) {
                    curr = queue.pollFirst();
                    if (adjMap.get(curr.person) == null) {
                        continue;
                    }
                    // Iterate through adjacent vertices
                    for (Edge edge : adjMap.get(curr.person)) {
                        if (explored.contains(edge)) {
                            continue;
                        }
                        explored.add(edge);
                        if (edge.src.equals(curr.person)) {
                            destination = edge.dest;
                        } else {
                            destination = edge.src;
                        }
                        if (visited.containsKey(destination)) {
                            // Handle previously visited vertices
                            if (visited.get(destination).path == count) {
                                return true;
                            }
                        } else {
                            visited.put(destination, curr);
                            next = new SearchNode(destination, edge.weight, count);
                            if (edge.weight == 0) {
                                queue.addFirst(next);
                            } else {
                                queue.addLast(next);
                            }
                        }
                    }
            }
                count++;
            }
            return false;
        }

        private static class Edge {
            private String src;
            private String dest;
            private int weight;

            private Edge(String personA, String personB, int weight) {
                this.weight = weight;
                src = personA;
                dest = personB;
            }

            @Override
            public boolean equals(Object o) {
                if (o instanceof Edge) {
                    Edge edge = (Edge) o;
                    return ((edge.dest.equals(src) &&
                            edge.src.equals(dest) ) ||
                           (edge.src.equals(src) &&
                            edge.dest.equals(dest)) &&
                            edge.weight == weight);
                }
                return false;
            }
        }

        private static class SearchNode {
            private String person;
            private int weight;
            private int path;

            private SearchNode(String person, int weight, int path) {
                this.person = person;
                this.weight = weight;
                this.path = path;
            }
        }
    }
}
