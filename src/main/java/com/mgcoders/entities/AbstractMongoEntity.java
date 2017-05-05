package com.mgcoders.entities;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.lang.reflect.ParameterizedType;

/**
 * Created by rsperoni on 05/05/17.
 */
public abstract class AbstractMongoEntity<T> implements Bson {

    final Class<T> typeParameterClass;
    protected ObjectId _id;

    public AbstractMongoEntity() {
        this.typeParameterClass = ((Class) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    public T withNewObjectId() {
        setId(new ObjectId());
        return (T) this;
    }

    public org.bson.types.ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId _id) {
        this._id = _id;
    }

    @Override
    public <TDocument> BsonDocument toBsonDocument(Class<TDocument> aClass, CodecRegistry codecRegistry) {
        return new BsonDocumentWrapper<>((T) this, codecRegistry.get((Class) typeParameterClass));
    }
}
