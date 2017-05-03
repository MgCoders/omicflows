package com.mgcoders.entities;


import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

/**
 * Created by rsperoni on 02/05/17.
 */
public class Tool implements Bson {

    private ObjectId _id;
    private String name;
    private String cwl;

    public Tool withNewObjectId() {
        setId(new ObjectId());
        return this;
    }

    public org.bson.types.ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCwl() {
        return cwl;
    }

    public void setCwl(String cwl) {
        this.cwl = cwl;
    }

    @Override
    public <TDocument> BsonDocument toBsonDocument(Class<TDocument> aClass, CodecRegistry codecRegistry) {
        return new BsonDocumentWrapper<>(this, codecRegistry.get(Tool.class));
    }

    @Override
    public String toString() {
        return "Tool{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                ", cwl='" + cwl + '\'' +
                '}';
    }
}
