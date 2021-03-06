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
package org.jclouds.http.functions;

import javax.annotation.Resource;

import org.jclouds.encryption.internal.Base64;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.Logger;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;
import com.google.common.io.Closeables;

/**
 * @author Adrian Cole
 */
public class ParseContentMD5FromHeaders implements Function<HttpResponse, byte[]>,
         InvocationContext {

   public static class NoContentMD5Exception extends RuntimeException {

      private static final long serialVersionUID = 1L;
      private final GeneratedHttpRequest<?> request;
      private final HttpResponse response;

      public NoContentMD5Exception(GeneratedHttpRequest<?> request, HttpResponse response) {
         super(String.format("no MD5 returned from request: %s; response %s", request, response));
         this.request = request;
         this.response = response;
      }

      public GeneratedHttpRequest<?> getRequest() {
         return request;
      }

      public HttpResponse getResponse() {
         return response;
      }

   }

   @Resource
   protected Logger logger = Logger.NULL;
   private GeneratedHttpRequest<?> request;

   public byte[] apply(HttpResponse from) {
      Closeables.closeQuietly(from.getContent());
      String contentMD5 = from.getFirstHeaderOrNull("Content-MD5");
      if (contentMD5 != null) {
         return Base64.decode(contentMD5);
      }
      throw new NoContentMD5Exception(request, from);
   }

   public void setContext(GeneratedHttpRequest<?> request) {
      this.request = request;
   }

}