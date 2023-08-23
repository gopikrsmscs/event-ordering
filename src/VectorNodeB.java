import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.Vector;

public class VectorNodeB extends UnicastRemoteObject implements VectorInterface, Runnable {

	private static final long serialVersionUID = 2597576699482300257L;

	private static int PORT = 8004;

	static Vector<Integer> vectorClock = new Vector<Integer>(3);

	protected VectorNodeB() throws RemoteException {
		super();

	}

	@Override
	public void sendMessage(Vector<Integer> currentVector) throws RemoteException {
		System.out.println("NodeB vector clock before Receving Message.");
		System.out.println(vectorClock);
		for (int i = 0; i < currentVector.size(); i++) {
			int maxTimer = Math.max(currentVector.get(i), vectorClock.get(i));
			vectorClock.set(i, maxTimer);
		}
		vectorClock.set(1, vectorClock.get(1) + 1); // Incrementing NodeB Timer
		System.out.println("NodeB vector clock after Receving Message.");
		System.out.println(vectorClock);
	}

	public static void main(String[] args) {

		try {
			Registry registry = LocateRegistry.createRegistry(PORT);
			registry.bind("vector", new VectorNodeB());
			System.err.println("Node B is Started Sucessfully");
			synchronizeTheVector();
			Thread thread = new Thread(new VectorNodeB());
			thread.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Synchronizing the Timers from the Berkly

	private static void synchronizeTheVector() {
		try {
			ClientServerInterface nodeA = (ClientServerInterface) Naming.lookup("rmi://localhost:8000/berkly");
			ClientServerInterface nodeB = (ClientServerInterface) Naming.lookup("rmi://localhost:8001/berkly");
			ClientServerInterface nodeC = (ClientServerInterface) Naming.lookup("rmi://localhost:8002/berkly");
			int timerA = nodeA.getTheCounter();
			int timerB = nodeB.getTheCounter();
			int timerC = nodeC.getTheCounter();
			vectorClock.add(timerA);
			vectorClock.add(timerB);
			vectorClock.add(timerC);
			System.out.println("Synchronized Vector");
			System.out.println(vectorClock);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				System.out.println("Choose option : send message to Node(A) or (B) or (C)");
				Scanner sc = new Scanner(System.in);
				String node = sc.next();
				if (node.equals("B")) {
					// If Internal Event occurs
					sendMessage(vectorClock);
				} else if (node.equals("A")) { // Send Message to Node A
					System.out.println("NodeB vector clock before Sending Message.");
					System.out.println(vectorClock);
					vectorClock.set(1, vectorClock.get(1) + 1); // Incrementing NodeB Timer
					VectorInterface nodeA = (VectorInterface) Naming.lookup("rmi://localhost:8003/vector");
					nodeA.sendMessage(vectorClock);
					System.out.println("NodeB vector clock after Sending Message.");
					System.out.println(vectorClock);

				} else { // Send Message to NodeC
					System.out.println("NodeB vector clock before Sending Message.");
					System.out.println(vectorClock);
					vectorClock.set(1, vectorClock.get(1) + 1); // Incrementing NodeA Timer
					VectorInterface nodeC = (VectorInterface) Naming.lookup("rmi://localhost:8005/vector");
					nodeC.sendMessage(vectorClock);
					System.out.println("NodeB vector clock after Sending Message.");
					System.out.println(vectorClock);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

}
