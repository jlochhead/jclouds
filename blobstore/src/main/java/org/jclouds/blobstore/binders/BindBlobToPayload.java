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
package org.jclouds.blobstore.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

/**
 * 
 * @author Adrian Cole
 */
public class BindBlobToPayload implements Binder {
   private final EncryptionService encryptionService;

   @Inject
   public BindBlobToPayload(EncryptionService encryptionService) {
      this.encryptionService = encryptionService;
   }

   public void bindToRequest(HttpRequest request, Object payload) {
      Blob object = (Blob) payload;
      request.setPayload(checkNotNull(object.getPayload(), "object.getPayload()"));
      request.getHeaders()
               .put(
                        HttpHeaders.CONTENT_TYPE,
                        checkNotNull(object.getMetadata().getContentType(),
                                 "object.metadata.contentType()"));

      request.getHeaders().put(HttpHeaders.CONTENT_LENGTH, object.getContentLength() + "");

      if (object.getMetadata().getContentMD5() != null) {
         request.getHeaders().put("Content-MD5",
                  encryptionService.toBase64String(object.getMetadata().getContentMD5()));
      }
   }
}
