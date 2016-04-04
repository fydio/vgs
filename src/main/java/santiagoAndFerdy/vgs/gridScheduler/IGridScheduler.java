package santiagoAndFerdy.vgs.gridScheduler;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import santiagoAndFerdy.vgs.discovery.IAddressable;
import santiagoAndFerdy.vgs.messages.BackUpRequest;
import santiagoAndFerdy.vgs.messages.MonitoringRequest;
import santiagoAndFerdy.vgs.model.Job;

/**
 * Created by Fydio on 3/19/16.
 */
public interface IGridScheduler extends IAddressable {
    /**
     * Request this grid scheduler to monitor the life-cycle for this job
     * 
     * @param monitorRequest
     *            the request to watch
     * @throws RemoteException
     */
    void monitorPrimary(MonitoringRequest monitorRequest) throws RemoteException;

    /**
     * Request this grid scheduler to also watch a job in case the primary grid scheduler crashes
     * 
     * @param backUpRequest
     *            the userRequest to watch
     * @throws RemoteException
     */
    void monitorBackUp(BackUpRequest backUpRequest) throws RemoteException;

    /**
     * For a resource manager to userRequest a job to be scheduled elsewhere
     * 
     * @param job
     *            to schedule somewhere else
     */
    void offload(Job job) throws RemoteException;

    /**
     * A job has finished. All the reserved resources can be released.
     * 
     * @param requestId
     */
    void releaseResources(int requestId) throws RemoteException;
}
