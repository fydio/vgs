package santiagoAndFerdy.vgs.resourceManager;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.EngineBuilder;
import com.linkedin.parseq.promise.Promise;
import com.sun.istack.internal.NotNull;
import santiagoAndFerdy.vgs.gridScheduler.IGridSchedulerDriver;
import santiagoAndFerdy.vgs.messages.UserRequest;
import santiagoAndFerdy.vgs.rmi.RmiServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class ResourceManagerGridSchedulerClient extends UnicastRemoteObject implements IResourceManagerGridSchedulerClient {

    private static final long serialVersionUID = 3823217677238862954L;
    private RmiServer rmiServer;
    private int id;
    private String                        url;
    private String                        driverUrl;
    private IGridSchedulerDriver        driver;
    private Engine                        engine;
    private ScheduledExecutorService    timerScheduler;
    

    public ResourceManagerGridSchedulerClient(@NotNull RmiServer rmiServer, int id, @NotNull String url, @NotNull String driverUrl)
            throws RemoteException, MalformedURLException {
        this.rmiServer = rmiServer;
        this.id = id;
        this.url = url;
        this.driverUrl = driverUrl;
        timerScheduler = Executors.newSingleThreadScheduledExecutor();
        int numCores = Runtime.getRuntime().availableProcessors();
        ExecutorService taskScheduler = Executors.newFixedThreadPool(numCores + 1);
        engine = new EngineBuilder().setTaskExecutor(taskScheduler).setTimerScheduler(timerScheduler).build();
        register();
        connect();
        

    }

    public void register() throws MalformedURLException, RemoteException {
        rmiServer.register(url, this);
    }

    public boolean connect() throws MalformedURLException {
        if (driver == null) {
            try {
                driver = (IGridSchedulerDriver) Naming.lookup(driverUrl);
                //keepAlive();
                return true;
            } catch (NotBoundException e) {
                return false;
            } catch (RemoteException e) {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public Promise<Void> schedule(UserRequest userRequest) throws RemoteException {
        //TODO:

        return null;
    }

    @Override
    public int getId() throws RemoteException {
        return id;
    }

    @Override
    public String getUrl() throws RemoteException {
        return url;
    }
}