package com.mgcoders.entities;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.util.Arrays;

/**
 * Created by rsperoni on 03/05/17.
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class MongoClientProvider {

    private static String CONNECTION_STRING = "mongo";
    private static String DATABASE_NAME = "omicflows";
    private static String COLLECTION_TOOL = "tool";
    private static String COLLECTION_USER = "user";

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

    @PostConstruct
    public void init() {

        Codec<Document> defaultDocumentCodec = MongoClient.getDefaultCodecRegistry().get(Document.class);
        ToolCodec toolCodec = new ToolCodec(defaultDocumentCodec);
        UserCodec userCodec = new UserCodec(defaultDocumentCodec);
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClient.getDefaultCodecRegistry(), CodecRegistries.fromCodecs(Arrays.asList(toolCodec, userCodec)));
        MongoClientOptions options = MongoClientOptions.builder().codecRegistry(codecRegistry)
                .build();
        try {
            mongoClient = new MongoClient(CONNECTION_STRING, options);
            System.out.println(mongoClient.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
