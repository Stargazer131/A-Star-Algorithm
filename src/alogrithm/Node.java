package alogrithm;

public class Node implements Comparable<Node> {
    Point point;
    Node parent;
    int g;
    int f;

    public Node(Point point, Node parent, int g, int f) {
        this.point = point;
        this.parent = parent;
        this.g = g;
        this.f = f;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return point.equals(node.point);
    }

    @Override
    public int hashCode() {
        return point.hashCode();
    }

    @Override
    public int compareTo(Node o) {
        return Integer.compare(f, o.f);
    }

    @Override
    public String toString() {
        return point.toString();
    }
}
