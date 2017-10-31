package coop.magnesium.db;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import coop.magnesium.db.entities.Role;
import coop.magnesium.db.entities.Tool;
import coop.magnesium.db.entities.User;
import coop.magnesium.db.entities.Workflow;
import coop.magnesium.utils.PasswordUtils;
import eu.dozd.mongo.MongoMapper;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Created by rsperoni on 03/05/17.
 */
@ApplicationScoped
public class MongoClientProvider {

    private static String CONNECTION_STRING = "mongo";
    private static String DATABASE_NAME = "omicflows";
    private static String COLLECTION_TOOL = "tool";
    private static String COLLECTION_USER = "user";
    private static String COLLECTION_WORKFLOW = "workflow";
    @Inject
    Logger logger;
    private MongoClient mongoClient = null;

    @Lock(LockType.READ)
    public MongoClient getMongoClient() {
        return mongoClient;
    }

    @Lock(LockType.READ)
    public MongoDatabase getDatabase() {
        return getMongoClient().getDatabase(DATABASE_NAME);
    }

    @Lock(LockType.READ)
    public MongoCollection<Tool> getToolCollection() {
        return getDatabase().getCollection(COLLECTION_TOOL, Tool.class);
    }

    @Lock(LockType.READ)
    public MongoCollection<User> getUserCollection() {
        return getDatabase().getCollection(COLLECTION_USER, User.class);
    }

    @Lock(LockType.READ)
    public MongoCollection<Workflow> getWorkflowCollection() {
        return getDatabase().getCollection(COLLECTION_WORKFLOW, Workflow.class);
    }

    @PostConstruct
    public void init() {


        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(MongoMapper.getProviders()));
        MongoClientOptions options = MongoClientOptions.builder().codecRegistry(codecRegistry)
                .build();


        try {
            mongoClient = new MongoClient(CONNECTION_STRING, options);
            //getToolCollection().drop();
            //getWorkflowCollection().drop();
            getUserCollection().drop();
            User user = new User();
            user.setEmail("r");
            user.setPassword(PasswordUtils.digestPassword("r"));
            user.setRole(Role.ADMIN.name());
            getUserCollection().insertOne(user);

            User user2 = new User();
            user2.setEmail("a");
            user2.setPassword(PasswordUtils.digestPassword("a"));
            user2.setRole(Role.USER.name());
            getUserCollection().insertOne(user2);
            //logger.info(mongoClient.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
