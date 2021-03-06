/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.rest.binders;

import static com.google.common.base.Preconditions.checkState;

import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;

import com.google.gson.Gson;

/**
 * Binds the object to the request as a json object.
 * 
 * @author Adrian Cole
 * @since 4.0
 */
public class BindToJsonPayload implements MapBinder {

   @Inject
   protected Gson gson;

   public void bindToRequest(HttpRequest request, Map<String, String> postParams) {
      bindToRequest(request, (Object) postParams);
   }

   public void bindToRequest(HttpRequest request, Object toBind) {
      checkState(gson != null, "Program error: gson should have been injected at this point");
      String json = gson.toJson(toBind);
      request.setPayload(json);
      request.getHeaders().replaceValues(HttpHeaders.CONTENT_LENGTH,
               Collections.singletonList(json.getBytes().length + ""));
      request.getHeaders().replaceValues(HttpHeaders.CONTENT_TYPE,
               Collections.singletonList(MediaType.APPLICATION_JSON));
   }

}