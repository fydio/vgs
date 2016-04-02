package santiagoAndFerdy.vgs.gridScheduler;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.EngineBuilder;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

import santiagoAndFerdy.vgs.discovery.HeartbeatHandler;
import santiagoAndFerdy.vgs.discovery.IRepository;
import santiagoAndFerdy.vgs.messages.BackUpRequest;
import santiagoAndFerdy.vgs.messages.Heartbeat;
import santiagoAndFerdy.vgs.messages.UserRequest;
import santiagoAndFerdy.vgs.model.Job;
import santiagoAndFerdy.vgs.rmi.RmiServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class GridSchedulerGridSchedulerClient extends UnicastRemoteObject implements IGridSchedulerGridSchedulerClient {

    private static final long serialVersionUID = -3646696281731009836L;
    private RmiServer                       rmiServer;
    private int                             id;
    private String                          url;
    private String                          driverUrl;
    private HeartbeatHandler                hHandler;
    private Map<Job, SettablePromise<Void>> pendingMonitoringRequests;
    IRepository<IGridSchedulerGridSchedulerClient> gsRepository;
    private Engine                          engine;

    public GridSchedulerGridSchedulerClient(RmiServer rmiServer, int id, String url, String driverUrl, IRepository<IGridSchedulerGridSchedulerClient> gsRepository) throws RemoteException, MalformedURLException {

        this.rmiServer = rmiServer;
        this.id = id;
        this.url = url;
        this.driverUrl = driverUrl;
        this.gsRepository = gsRepository;

        pendingMonitoringRequests = new HashMap<>();

        // setup async machinery
        ScheduledExecutorService timerScheduler = Executors.newSingleThreadScheduledExecutor();
        int numCores = Runtime.getRuntime().availableProcessors();
        ExecutorService taskScheduler = Executors.newFixedThreadPool(numCores + 1);
        engine = new EngineBuilder().setTaskExecutor(taskScheduler).setTimerScheduler(timerScheduler).build();
        hHandler = new HeartbeatHandler(gsRepository, id, true);
    }

    @Override
    public Promise<Void> monitorBackUp(Job jobToBackUp) throws RemoteException, MalformedURLException, NotBoundException {
        SettablePromise<Void> monitoringPromise = Promises.settable();
        pendingMonitoringRequests.put(jobToBackUp, monitoringPromise);
        BackUpRequest monitoringRequest = new BackUpRequest(url, jobToBackUp);

        IGridScheduler driver = (IGridScheduler) Naming.lookup(driverUrl);
        driver.monitorBackUp(monitoringRequest);

        return monitoringPromise;
    }

    @Override
    public void monitorBackUpAccepted(Job job) throws RemoteException {
        Optional.ofNullable(pendingMonitoringRequests.remove(job)).ifPresent(p -> p.done(null));
    }

    @Override
    public void promoteToPrimary(UserRequest userRequest) throws RemoteException {

    }

    @Override
    public Promise<Void> releaseResources(int requestId) throws RemoteException {
        return null;
    }

    @Override
    public void iAmAlive(Heartbeat h) throws MalformedURLException, RemoteException, NotBoundException {

    }

    @Override
    public int getId() throws RemoteException {
        return 0;
    }

    @Override
    public String getUrl() throws RemoteException {
        return null;
    }
    public void checkConnections() {
        hHandler.getStatus();
    }
}
