package com.mgcoders.entities;

import org.bson.*;
import org.bson.codecs.*;
import org.bson.types.ObjectId;

/**
 * Created by rsperoni on 03/05/17.
 */
public class UserCodec implements CollectibleCodec<User> {

    private Codec<Document> documentCodec;

    public UserCodec() {
        this.documentCodec = new DocumentCodec();
    }

    public UserCodec(Codec<Document> codec) {
        this.documentCodec = codec;
    }

    @Override
    public User generateIdIfAbsentFromDocument(User user) {
        return documentHasId(user) ? user.withNewObjectId() : user;
    }

    @Override
    public boolean documentHasId(User user) {
        return null == user.getId();
    }

    @Override
    public BsonValue getDocumentId(User user) {
        if (!documentHasId(user)) {
            throw new IllegalStateException("The document does not contain an _id");
        }
        return new BsonString(user.getId().toHexString());
    }

    @Override
    public User decode(BsonReader bsonReader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(bsonReader, decoderContext);
        System.out.println("document " + document);
        User user = new User();

        user.setId(document.getObjectId("_id"));

        user.setEmail(document.getString("email"));

        user.setPassword(document.getString("password"));

        return user;
    }

    @Override
    public void encode(BsonWriter bsonWriter, User user, EncoderContext encoderContext) {
        Document document = new Document();

        ObjectId id = user.getId();
        String email = user.getEmail();
        String password = user.getPassword();

        if (null != id) {
            document.put("_id", id);
        }

        if (null != email) {
            document.put("email", email);
        }

        if (null != password) {
            document.put("password", password);
        }

        documentCodec.encode(bsonWriter, document, encoderContext);
    }

    @Override
    public Class<User> getEncoderClass() {
        return User.class;
    }
}
