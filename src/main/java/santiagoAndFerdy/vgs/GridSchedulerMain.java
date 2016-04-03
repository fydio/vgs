package santiagoAndFerdy.vgs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import santiagoAndFerdy.vgs.discovery.IRepository;
import santiagoAndFerdy.vgs.discovery.Repository;
import santiagoAndFerdy.vgs.gridScheduler.GridScheduler;
import santiagoAndFerdy.vgs.gridScheduler.IGridScheduler;
import santiagoAndFerdy.vgs.resourceManager.IResourceManager;
import santiagoAndFerdy.vgs.rmi.RmiServer;

/**
 * Created by Fydio on 3/18/16.
 */
public class GridSchedulerMain {
    private static AWSCredentials credentials;
    private static AmazonS3       s3Client;

    public static void main(String[] args) throws InterruptedException, NotBoundException, URISyntaxException, IOException {
        if (args.length < 5) {
            System.err.println("Please enter the URL of this GridScheduler, the bucket name, the RM file name and the GS file name");
            return;
        }

        int id = Integer.parseInt(args[0]);
        String url = args[1];
        String bucketName = args[2];
        String resourceManagerListingFileName = args[3];
        String gridSchedulerListingFileName = args[4];

        credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(e.getMessage());
        }

        s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
        S3Object resourceManagerListing = s3Client.getObject(new GetObjectRequest(bucketName, resourceManagerListingFileName));
        S3Object gridSchedulerListing = s3Client.getObject(new GetObjectRequest(bucketName, gridSchedulerListingFileName));

        IRepository<IResourceManager> resourceManagerClientRepository = Repository.fromS3(resourceManagerListing.getObjectContent());
        IRepository<IGridScheduler> gridSchedulerClientRepository = Repository.fromS3(gridSchedulerListing.getObjectContent());
        RmiServer server = new RmiServer(1099);

        GridScheduler gs = new GridScheduler(server, resourceManagerClientRepository, gridSchedulerClientRepository, url, id);
        server.register(url, gs);
        while(true){ //just print connections of GS number 0
            if(id == 0){
                Thread.sleep(2000);
                gs.checkConnections();
                System.out.println("");
            }
        }
    }
}
