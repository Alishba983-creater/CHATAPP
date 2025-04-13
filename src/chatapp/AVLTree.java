
package chatapp;
// AVLTree class for managing clients
class AVLTree {
    private ClientNode root;

    public void insertClient(String name, ClientHandler handler) {
        root = insert(root, name, handler);
    }

    public void deleteClient(String name) {
        root = delete(root, name);
    }

    public void removeClient(String name) {
        if (searchClient(name) != null) { // Ensure client exists before attempting to delete
            deleteClient(name);
        } else {
            System.err.println("Client with name \"" + name + "\" does not exist.");
        }
    }

    public ClientNode searchClient(String name) {
        return search(root, name);
    }

    private ClientNode insert(ClientNode node, String name, ClientHandler handler) {
        if (node == null) return new ClientNode(name, handler);

        if (name.compareTo(node.name) < 0) {
            node.left = insert(node.left, name, handler);
        } else if (name.compareTo(node.name) > 0) {
            node.right = insert(node.right, name, handler);
        } else {
            // Duplicate names are not allowed
            System.err.println("Duplicate client name \"" + name + "\" ignored.");
            return node;
        }

        // Balance the tree
        return balance(node);
    }

    private ClientNode delete(ClientNode node, String name) {
        if (node == null) {
            return null; // Name not found, nothing to delete
        }

        if (name.compareTo(node.name) < 0) {
            node.left = delete(node.left, name);
        } else if (name.compareTo(node.name) > 0) {
            node.right = delete(node.right, name);
        } else {
            // Node to be deleted found
            if (node.left == null || node.right == null) {
                node = (node.left != null) ? node.left : node.right;
            } else {
                ClientNode temp = getMin(node.right); // Find the in-order successor
                node.name = temp.name;
                node.handler = temp.handler;
                node.right = delete(node.right, temp.name);
            }
        }

        if (node == null) {
            return null; // Return if the tree becomes empty
        }

        // Balance the tree
        return balance(node);
    }

    private ClientNode balance(ClientNode node) {
        if (node == null) {
            return null; // No balancing needed for a null node
        }

        // Update height
        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));

        // Perform balancing logic
        int balanceFactor = getHeight(node.left) - getHeight(node.right);

        if (balanceFactor > 1) {
            if (getHeight(node.left.left) >= getHeight(node.left.right)) {
                node = rightRotate(node); // Right rotation
            } else {
                node.left = leftRotate(node.left);
                node = rightRotate(node);
            }
        } else if (balanceFactor < -1) {
            if (getHeight(node.right.right) >= getHeight(node.right.left)) {
                node = leftRotate(node); // Left rotation
            } else {
                node.right = rightRotate(node.right);
                node = leftRotate(node);
            }
        }

        return node;
    }

    private ClientNode rightRotate(ClientNode y) {
        ClientNode x = y.left;
        ClientNode T = x.right;

        // Perform rotation
        x.right = y;
        y.left = T;

        // Update heights
        y.height = 1 + Math.max(getHeight(y.left), getHeight(y.right));
        x.height = 1 + Math.max(getHeight(x.left), getHeight(x.right));

        return x;
    }

    private ClientNode leftRotate(ClientNode x) {
        ClientNode y = x.right;
        ClientNode T = y.left;

        // Perform rotation
        y.left = x;
        x.right = T;

        // Update heights
        x.height = 1 + Math.max(getHeight(x.left), getHeight(x.right));
        y.height = 1 + Math.max(getHeight(y.left), getHeight(y.right));

        return y;
    }

    private int getHeight(ClientNode node) {
        return (node == null) ? 0 : node.height;
    }

    private ClientNode getMin(ClientNode node) {
        while (node.left != null) node = node.left;
        return node;
    }

    private ClientNode search(ClientNode node, String name) {
        if (node == null || name.equals(node.name)) return node;
        if (name.compareTo(node.name) < 0) return search(node.left, name);
        return search(node.right, name);
    }
}

// ClientNode class
class ClientNode {
    String name;
    ClientHandler handler;
    ClientNode left, right;
    int height;

    public ClientNode(String name, ClientHandler handler) {
        this.name = name;
        this.handler = handler;
        this.left = this.right = null;
        this.height = 1; // Initialize height to 1 for new nodes
    }
}
