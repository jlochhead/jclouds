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
package org.jclouds.rimuhosting.miro.functions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.functions.ParseJson;
import org.jclouds.rimuhosting.miro.domain.ServerInfo;
import org.jclouds.rimuhosting.miro.domain.internal.RimuHostingResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author Ivan Meredith
 */
@Singleton
public class ParseInstanceInfoFromJsonResponse extends ParseJson<ServerInfo> {
   @Inject
   public ParseInstanceInfoFromJsonResponse(Gson gson) {
      super(gson);
   }

   private static class OrderResponse extends RimuHostingResponse {
      private ServerInfo running_vps_info;

      public ServerInfo getInstanceInfo() {
         return running_vps_info;
      }

      @SuppressWarnings("unused")
      public void setInstanceInfo(ServerInfo running_vps_info) {
         this.running_vps_info = running_vps_info;
      }
   }

   @Override
   protected ServerInfo apply(InputStream stream) {
      Type setType = new TypeToken<Map<String, OrderResponse>>() {
      }.getType();
      try {
         Map<String, OrderResponse> responseMap = gson.fromJson(new InputStreamReader(stream,
                  "UTF-8"), setType);
         return responseMap.values().iterator().next().getInstanceInfo();
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}