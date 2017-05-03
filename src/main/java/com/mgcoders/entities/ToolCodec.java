package com.mgcoders.entities;

import org.bson.*;
import org.bson.codecs.*;
import org.bson.types.ObjectId;

/**
 * Created by rsperoni on 03/05/17.
 */
public class ToolCodec implements CollectibleCodec<Tool> {

    private Codec<Document> documentCodec;

    public ToolCodec() {
        this.documentCodec = new DocumentCodec();
    }

    public ToolCodec(Codec<Document> codec) {
        this.documentCodec = codec;
    }

    @Override
    public Tool generateIdIfAbsentFromDocument(Tool tool) {
        return documentHasId(tool) ? tool.withNewObjectId() : tool;
    }

    @Override
    public boolean documentHasId(Tool tool) {
        return null == tool.getId();
    }

    @Override
    public BsonValue getDocumentId(Tool tool) {
        if (!documentHasId(tool)) {
            throw new IllegalStateException("The document does not contain an _id");
        }
        return new BsonString(tool.getId().toHexString());
    }

    @Override
    public Tool decode(BsonReader bsonReader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(bsonReader, decoderContext);
        System.out.println("document " + document);
        Tool tool = new Tool();

        tool.setId(document.getObjectId("_id"));

        tool.setName(document.getString("name"));

        tool.setCwl(document.getString("cwl"));

        return tool;
    }

    @Override
    public void encode(BsonWriter bsonWriter, Tool tool, EncoderContext encoderContext) {
        Document document = new Document();

        ObjectId id = tool.getId();
        String name = tool.getName();
        String cwl = tool.getCwl();

        if (null != id) {
            document.put("_id", id);
        }

        if (null != name) {
            document.put("name", name);
        }

        if (null != cwl) {
            document.put("cwl", cwl);
        }

        documentCodec.encode(bsonWriter, document, encoderContext);
    }

    @Override
    public Class<Tool> getEncoderClass() {
        return Tool.class;
    }
}
