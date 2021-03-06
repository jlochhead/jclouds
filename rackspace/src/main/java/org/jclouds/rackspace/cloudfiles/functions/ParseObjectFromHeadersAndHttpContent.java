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
package org.jclouds.rackspace.cloudfiles.functions;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.functions.ParseSystemAndUserMetadataFromHeaders;
import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;

/**
 * Parses response headers and creates a new CFObject from them and the HTTP content.
 * 
 * @see ParseMetadataFromHeaders
 * @author Adrian Cole
 */
public class ParseObjectFromHeadersAndHttpContent implements Function<HttpResponse, CFObject>,
         InvocationContext {

   private final ParseObjectInfoFromHeaders infoParser;
   private final CFObject.Factory objectProvider;

   @Inject
   public ParseObjectFromHeadersAndHttpContent(ParseObjectInfoFromHeaders infoParser,
            CFObject.Factory objectProvider) {
      this.infoParser = infoParser;
      this.objectProvider = objectProvider;
   }

   /**
    * First, calls {@link ParseSystemAndUserMetadataFromHeaders}.
    * 
    * Then, sets the object size based on the Content-Length header and adds the content to the
    * {@link CFObject} result.
    * 
    * @throws org.jclouds.http.HttpException
    */
   public CFObject apply(HttpResponse from) {
      CFObject object = objectProvider.create(infoParser.apply(from));
      object.getAllHeaders().putAll(from.getHeaders());
      String contentLength = from.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH);
      String contentRange = from.getFirstHeaderOrNull("Content-Range");

      if (contentLength != null) {
         object.setContentLength(Long.parseLong(contentLength));
      }
      if (from.getContent() != null) {
         object.setPayload(from.getContent());
      } else if (object.getContentLength() != null && object.getContentLength() == 0) {
         object.setPayload(new byte[0]);
      } else {
         assert false : "no content in " + from;
      }
      if (contentRange == null && contentLength != null) {
         object.getInfo().setBytes(object.getContentLength());
      } else if (contentRange != null) {
         object.getInfo().setBytes(
                  Long.parseLong(contentRange.substring(contentRange.lastIndexOf('/') + 1)));
      }
      return object;
   }

   public void setContext(GeneratedHttpRequest<?> request) {
      infoParser.setContext(request);
   }

}