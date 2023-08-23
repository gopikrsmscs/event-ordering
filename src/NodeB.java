import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.Scanner;

public class NodeB extends UnicastRemoteObject implements ClientServerInterface, Runnable {

	private static final long serialVersionUID = -5561653634319447799L;

	private static int PORT = 8001;

	static Random rand = new Random();
	private static int nodeBCounter = rand.nextInt(40);

	protected NodeB() throws RemoteException {
		super();

	}

	@Override
	public void sendMessage(String message) throws RemoteException {
		System.out.println(nodeBCounter);
		System.out.println("Node B received Message");
		System.out.println("Incrementing Node B counter");
		nodeBCounter = nodeBCounter + 1;
		System.out.println(nodeBCounter);

	}

	public static void main(String[] args) {

		try {
			Registry registry = LocateRegistry.createRegistry(PORT);
			registry.bind("berkly", new NodeB());
			System.err.println("Node B is Started Sucessfully");
			System.out.println("The NodeB Initial counter is : " + nodeBCounter);
			Thread thread = new Thread(new NodeB());
			thread.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getTheCounter() throws RemoteException {
		return nodeBCounter;
	}

	@Override
	public void adjustTheCounter(int counter) throws RemoteException {
		nodeBCounter = counter;
		System.out.println("The Node B counter is adjusted to : " + nodeBCounter);

	}

	@Override
	public void run() {
		while (true) {
			try {
				System.out.println("Choose option : send message(1) or Synchronize(2)");
				Scanner sc = new Scanner(System.in);
				Integer input = sc.nextInt();
				if (input == 1) {
					System.out.println("Select the node to send message (A),(C)");
					String node = sc.next();
					if (node.equals("A")) {
						System.out.println("The NodeB counter before sending message");
						System.out.println(nodeBCounter);

						ClientServerInterface nodeA = (ClientServerInterface) Naming
								.lookup("rmi://localhost:8000/berkly");
						nodeA.sendMessage("Message");
						System.out.println("The NodeB counter after sending message");
						nodeBCounter = nodeBCounter + 1;
						System.out.println(nodeBCounter);
					} else {
						System.out.println("The NodeB counter before sending message");
						System.out.println(nodeBCounter);

						ClientServerInterface nodeC = (ClientServerInterface) Naming
								.lookup("rmi://localhost:8002/berkly");
						nodeC.sendMessage("Message");
						System.out.println("The NodeB counter after sending message");
						nodeBCounter = nodeBCounter + 1;
						System.out.println(nodeBCounter);
					}

				}
				if (input == 2) {
					System.out.println("Synchronizing the counters.");
					System.out.println("Getting the counters from all nodes.");
					ClientServerInterface nodeA = (ClientServerInterface) Naming.lookup("rmi://localhost:8000/berkly");
					int counterA = nodeA.getTheCounter();
					int counterB = nodeBCounter;
					ClientServerInterface nodeC = (ClientServerInterface) Naming.lookup("rmi://localhost:8002/berkly");
					int counterC = nodeC.getTheCounter();
					System.out.println("The NodeA counter is: " + counterA);
					System.out.println("The NodeB counter is: " + counterB);
					System.out.println("The NodeC counter is: " + counterC);

					int average = (counterA + counterB + counterC) / 3;

					System.out.println("Adjusting the counters in all the nodes .");
					nodeBCounter = average;
					nodeA.adjustTheCounter(average);
					nodeC.adjustTheCounter(average);
					System.out.println("The Node B counter is adjusted to : " + nodeBCounter);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

}
