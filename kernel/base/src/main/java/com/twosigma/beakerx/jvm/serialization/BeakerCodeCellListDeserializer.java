/*
 *  Copyright 2014 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.jvm.serialization;

import com.twosigma.beakerx.CodeCell;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;

/*
 * This class is used to deserialize the above fake root object when reading the notebook code cells
 */
public class BeakerCodeCellListDeserializer extends JsonDeserializer<BeakerCodeCellList> {

  private final Provider<BeakerObjectConverter> objectSerializerProvider;

  @Inject
  public BeakerCodeCellListDeserializer(Provider<BeakerObjectConverter> osp) {
    objectSerializerProvider = osp;
  }

  @Override
  public BeakerCodeCellList deserialize(JsonParser jp, DeserializationContext ctxt) 
      throws IOException, JsonProcessingException {
    ObjectMapper mapper = (ObjectMapper)jp.getCodec();
    JsonNode node = mapper.readTree(jp);
    
    List<CodeCell> l = new ArrayList<CodeCell>();
    if (node.isArray()) {
      for (JsonNode o : node) {
        Object obj = objectSerializerProvider.get().deserialize(o, mapper);
        if (obj instanceof CodeCell)
          l.add((CodeCell) obj);
      }
    }
    
    BeakerCodeCellList r = new BeakerCodeCellList();
    r.theList = l;
    return r;
  }
}