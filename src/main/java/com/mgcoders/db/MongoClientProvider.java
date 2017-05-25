package com.mgcoders.db;

import com.mgcoders.db.entities.Tool;
import com.mgcoders.db.entities.User;
import com.mgcoders.db.entities.Workflow;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
            //logger.info(mongoClient.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
