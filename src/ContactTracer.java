import java.util.*;

public class ContactTracer {
    private Graph graph;

    /**
     * Initialises an empty ContactTracer with no populated contact traces.
     */
    public ContactTracer() {
        graph = new Graph();
    }

    /**
     * Initialises the ContactTracer and populates the internal data structures
     * with the given list of contract traces.
     * 
     * @param traces to populate with
     * @require traces != null
     */
    public ContactTracer(List<Trace> traces) {
        this();
        for (Trace trace : traces) {
            addTrace(trace);
        }
    }

    /**
     * Adds a new contact trace to 
     * 
     * If a contact trace involving the same two people at the exact same time is
     * already stored, do nothing.
     * 
     * @param trace to add
     * @require trace != null
     */
    public void addTrace(Trace trace) {
        String personA = trace.getPerson1();
        String personB = trace.getPerson2();
        int time = trace.getTime();
        graph.addEdge(personA, personB, time);
    }

    /**
     * Gets a list of times that person1 and person2 have come into direct 
     * contact (as per the tracing data).
     *
     * If the two people haven't come into contact before, an empty list is returned.
     * 
     * Otherwise the list should be sorted in ascending order.
     * 
     * @param person1 
     * @param person2
     * @return a list of contact times, in ascending order.
     * @require person1 != null && person2 != null
     */
    public List<Integer> getContactTimes(String person1, String person2) {
        List<Integer> result = new ArrayList<>();
        Iterator<Graph.Edge> iter = graph.getVertexConnections(person1);
        if (iter == null) {
            return result;
        }
        while (iter.hasNext()) {
            Graph.Edge edge = iter.next();
            if (edge.dest.equals(person2) || edge.src.equals(person2)) {
                result.add(edge.time);
            }
        }
        Collections.sort(result);
        return result;
    }

    /**
     * Gets all the people that the given person has been in direct contact with
     * over the entire history of the tracing dataset.
     * 
     * @param person to list direct contacts of
     * @return set of the person's direct contacts
     */
    public Set<String> getContacts(String person) {
        return getContactsAfter(person, -1);
    }

    /**
     * Gets all the people that the given person has been in direct contact with
     * at OR after the given timestamp (i.e. inclusive).
     * 
     * @param person to list direct contacts of
     * @param timestamp to filter contacts being at or after
     * @return set of the person's direct contacts at or after the timestamp
     */
    public Set<String> getContactsAfter(String person, int timestamp) {
        Set<String> result = new HashSet<>();
        Iterator<Graph.Edge> iter = graph.getVertexConnections(person);
        if (iter == null) {
            return result;
        }
        while (iter.hasNext()) {
            Graph.Edge edge = iter.next();
            if (!(timestamp == -1)) {
                if (edge.time < timestamp) {
                    continue;
                }
            }
            if (edge.src.equals(person)) {
                result.add(edge.dest);
            } else {
                result.add(edge.src);
            }
        }
        return result;
    }

    /**
     * Initiates a contact trace starting with the given person, who
     * became contagious at timeOfContagion.
     * 
     * Note that the return set shouldn't include the original person the trace started from.
     * 
     * @param person to start contact tracing from
     * @param timeOfContagion the exact time person became contagious
     * @return set of people who may have contracted the disease, originating from person
     */
    public Set<String> contactTrace(String person, int timeOfContagion) {
        HashSet<String> result = new HashSet<>();
        HashMap<String, Integer> visited = new HashMap<>();
        result.add(person);
        contractTraceRecursive(person, result, visited, timeOfContagion);
        result.remove(person);
        return result;
    }

    private void contractTraceRecursive(String nextPerson,
                                        HashSet<String> result,
                                        HashMap<String, Integer> visited,
                                        int infectPeriod) {
        result.add(nextPerson);
        visited.put(nextPerson, infectPeriod);
        Iterator<Graph.Edge> iter = graph.adjMap.get(nextPerson).iterator();

        if (iter == null) {
            return;
        }
        // Perform DFS
        while (iter.hasNext()) {
            Graph.Edge edge = iter.next();
            // The person was not contagious
            if (infectPeriod > edge.time) {
                continue;
            }
            if (edge.dest.equals(nextPerson)) {
                if (visited.containsKey(edge.src)) {
                    if ((visited.get(edge.src) > edge.time + 60) ||
                        visited.get(edge.src) == -1) {
                        visited.put(edge.src, edge.time + 60);
                    }
                } else {
                    contractTraceRecursive(edge.src, result, visited, edge.time + 60);
                }
                result.add(edge.src);
            } else {
                if (visited.containsKey(edge.dest)) {
                    if ((visited.get(edge.dest) > edge.time + 60) ||
                        visited.get(edge.dest) == -1) {
                        visited.put(edge.dest, edge.time + 60);
                    }
                } else {
                    contractTraceRecursive(edge.dest, result, visited, edge.time + 60);
                }
                result.add(edge.dest);
            }
        }
    }

    private class Graph {
        private HashMap<String, LinkedList<Edge>> adjMap;

        private Graph() {
            adjMap = new HashMap<>();
        }

        private void addEdge(String src, String dest, int time) {
            LinkedList<Edge> srcTmp;
            LinkedList<Edge> destTmp;
            Edge edge = new Edge(src, dest, time);

            srcTmp = updateCurrentList(src, edge);
            destTmp = updateCurrentList(dest, edge);
            if (srcTmp != null) {
                adjMap.put(src, srcTmp);
            }
            if (destTmp != null) {
                adjMap.put(dest, destTmp);
            }
        }

        private LinkedList<Edge> updateCurrentList(String entry, Edge edge) {
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

            sortTimeAscending(tmp);
            return tmp;
        }

        private void sortTimeAscending(LinkedList<Edge> list) {
            list.sort(Comparator.comparingInt(k -> k.time));
        }

        private void sortTimeDescending(LinkedList<Edge> list) {
            list.sort((a, b) -> b.time - a.time);
        }

        private Iterator<Edge> getVertexConnections(String person) {
            if (adjMap.containsKey(person)) {
                LinkedList<Edge> edges = adjMap.get(person);
                return edges.iterator();
            }
            return null;
        }

        private class Edge {
            private String src;
            private String dest;
            private int time;

            private Edge(String personA, String personB, int time) {
                this.time = time;
                src = personA;
                dest = personB;
            }
        }
    }
}
