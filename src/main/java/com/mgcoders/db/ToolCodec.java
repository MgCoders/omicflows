package com.mgcoders.db;

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
public class ToolCodec extends AbstractCodec<Tool> {

    public ToolCodec(Codec<Document> codec) {
        super(codec);
    }

    @Override
    public Tool decode(BsonReader bsonReader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(bsonReader, decoderContext);
        System.out.println("document " + document);
        Tool tool = new Tool(document.getString("name"), document.getString("cwl"));
        tool.setId(document.getObjectId("_id"));
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


}
