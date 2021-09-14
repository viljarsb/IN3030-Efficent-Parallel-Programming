


//This class is just to store the points found in the thread recurison.
//The threads cant just add them to the final list in a random order.
//The points would be the same, but the order would be wrong.
public class recTree {

    private Node rootNode;

    public recTree()
    {
        rootNode = new Node();
    }

    public Node getRoot()
    {
        return rootNode;
    }


    public IntList makeEnvelope()
    {
        IntList envelope = new IntList();
        Node rootNode = getRoot();
        helper2(envelope, rootNode);
        return envelope;
    }

    private void helper2(IntList envelope, Node n)
    {
        if(n != null)
        {
            if(n.point != -1)
            {
                helper2(envelope, n.right);
                envelope.add(n.point);
                helper2(envelope, n.left);
            }
        }
    }

    public void printTree()
    {
        Node rootNode = getRoot();
        helper(rootNode);
    }

    private void helper(Node n)
    {
        if(n != null && n.point != -1)
        {
            helper(n.right);
            System.out.println(n.point);
            helper(n.left);
        }
    }
}


