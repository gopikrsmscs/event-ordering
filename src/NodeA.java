import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.Scanner;

public class NodeA extends UnicastRemoteObject implements ClientServerInterface, Runnable {

	private static final long serialVersionUID = 748396043385570557L;

	private static int PORT = 8000;

	static Random rand = new Random();
	private static int nodeACounter = rand.nextInt(50);

	protected NodeA() throws RemoteException {
		super();

	}

	@Override
	public void sendMessage(String message) throws RemoteException {
		System.out.println(nodeACounter);
		System.out.println("Node A received Message");
		System.out.println("Incrementing Node A counter");
		nodeACounter = nodeACounter + 1;
		System.out.println(nodeACounter);
	}

	public static void main(String[] args) {

		try {
			Registry registry = LocateRegistry.createRegistry(PORT);
			registry.bind("berkly", new NodeA());
			System.err.println("Node A is Started Sucessfully");
			System.out.println("The NodeA Initial counter is : " + nodeACounter);
			Thread thread = new Thread(new NodeA());
			thread.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getTheCounter() throws RemoteException {
		return nodeACounter;
	}

	@Override
	public void adjustTheCounter(int counter) throws RemoteException {
		nodeACounter = counter;
		System.out.println("The Node A counter is adjusted to : " + nodeACounter);

	}

	@Override
	public void run() {
		while (true) {
			try {
				System.out.println("Choose option : send message(1) or Synchronize(2)");
				Scanner sc = new Scanner(System.in);
				Integer input = sc.nextInt();
				if (input == 1) {
					System.out.println("Select the node to send message (B),(C)");
					String node = sc.next();
					if (node.equals("B")) {
						System.out.println("The NodeA counter before sending message");
						System.out.println(nodeACounter);

						ClientServerInterface nodeB = (ClientServerInterface) Naming
								.lookup("rmi://localhost:8001/berkly");
						nodeB.sendMessage("Message");
						System.out.println("The NodeA counter after sending message");
						nodeACounter = nodeACounter + 1;
						System.out.println(nodeACounter);
					} else {
						System.out.println("The NodeA counter before sending message");
						System.out.println(nodeACounter);

						ClientServerInterface nodeC = (ClientServerInterface) Naming
								.lookup("rmi://localhost:8002/berkly");
						nodeC.sendMessage("Message");
						System.out.println("The NodeA counter after sending message");
						nodeACounter = nodeACounter + 1;
						System.out.println(nodeACounter);
					}
				}
				if (input == 2) {
					System.out.println("Synchronizing the counters.");
					System.out.println("Getting the counters from all nodes.");
					int counterA = nodeACounter;
					ClientServerInterface nodeB = (ClientServerInterface) Naming.lookup("rmi://localhost:8001/berkly");
					int counterB = nodeB.getTheCounter();
					ClientServerInterface nodeC = (ClientServerInterface) Naming.lookup("rmi://localhost:8002/berkly");
					int counterC = nodeC.getTheCounter();
					System.out.println("The NodeA counter is: " + counterA);
					System.out.println("The NodeB counter is: " + counterB);
					System.out.println("The NodeC counter is: " + counterC);

					int average = (counterA + counterB + counterC) / 3;

					System.out.println("Adjusting the counters in all the nodes .");
					nodeACounter = average;
					nodeB.adjustTheCounter(average);
					nodeC.adjustTheCounter(average);
					System.out.println("The Node A counter is adjusted to : " + nodeACounter);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

}
