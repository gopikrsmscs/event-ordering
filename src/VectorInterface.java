import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

public interface VectorInterface extends Remote {
	void sendMessage(Vector<Integer> vector) throws RemoteException;
}
