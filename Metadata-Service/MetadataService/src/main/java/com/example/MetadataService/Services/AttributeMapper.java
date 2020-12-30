package com.example.MetadataService.Services;

import java.util.HashMap;

public class AttributeMapper {

    private static HashMap<String, String> mapping = new HashMap<String,String>() {{
        put("event_id","id");
        put("name","name");
        put("dev_id","deviceIdentifier");
        put("created","timestamp");
        put("timestamp","timestamp");
        put("longitude","longitude");
        put("latitude","latitude");
        put("frame_num","frameNum");
        put("place_ident","placeIdent");
        put("event_frames","eventFrames");
        put("updated","updated");
    }};

    /**
     * Maps the attribute names used in the json rest endpoints to the internal attribute names.
     * @param attributeNameJson the attribute name of the json format.
     * @return returns a string with the correct attribute name.
     */
    public static String mapJsonAttributeToInternalAttributes(String attributeNameJson) {
        return mapping.get(attributeNameJson);
    }
}
