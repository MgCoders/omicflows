package com.mgcoders.entities;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

/**
 * Created by rsperoni on 03/05/17.
 */
public class UserCodec extends AbstractCodec<User> {

    public UserCodec(Codec<Document> codec) {
        super(codec);
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


}
