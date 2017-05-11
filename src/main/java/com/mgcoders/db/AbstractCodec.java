package com.mgcoders.db;

import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DocumentCodec;

import java.lang.reflect.ParameterizedType;

/**
 * Created by rsperoni on 08/05/17.
 */
public abstract class AbstractCodec<T extends AbstractMongoEntity<T>> implements CollectibleCodec<T> {

    final Class<T> typeParameterClass = ((Class) ((ParameterizedType) getClass()
            .getGenericSuperclass()).getActualTypeArguments()[0]);
    protected Codec<Document> documentCodec;

    public AbstractCodec() {
        this.documentCodec = new DocumentCodec();
    }

    public AbstractCodec(Codec<Document> codec) {
        this.documentCodec = codec;
    }

    @Override
    public T generateIdIfAbsentFromDocument(T concreteDocument) {
        return documentHasId(concreteDocument) ? concreteDocument.withNewObjectId() : concreteDocument;
    }

    @Override
    public boolean documentHasId(T concreteDocument) {
        return null == concreteDocument.getId();
    }

    @Override
    public BsonValue getDocumentId(T concreteDocument) {
        if (!documentHasId(concreteDocument)) {
            throw new IllegalStateException("The document does not contain an _id");
        }
        return new BsonString(concreteDocument.getId().toHexString());
    }

    @Override
    public Class<T> getEncoderClass() {
        return typeParameterClass;
    }

}
