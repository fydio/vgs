package santiagoAndFerdy.vgs.resourceManager;

import com.linkedin.parseq.promise.Promise;
import santiagoAndFerdy.vgs.discovery.IAdressable;
import santiagoAndFerdy.vgs.messages.UserRequest;

import java.rmi.RemoteException;

/**
 * Created by Fydio on 3/30/16.
 */
public interface IResourceManagerGridSchedulerClient extends IAdressable {

    /**
     * Schedule a job on this cluster managed by this resource manager.
     * Must be accepted
     * @param userRequest to userRequest
     * @return a promise that will be fulliflled to signify ACKnowledgement
     * @throws RemoteException
     */
    Promise<Void> schedule(UserRequest userRequest) throws RemoteException;
}