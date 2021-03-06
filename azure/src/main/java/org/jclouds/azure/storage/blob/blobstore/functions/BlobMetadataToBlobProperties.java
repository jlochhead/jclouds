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
package org.jclouds.azure.storage.blob.blobstore.functions;

import java.util.Map.Entry;

import javax.inject.Singleton;

import org.jclouds.azure.storage.blob.domain.MutableBlobProperties;
import org.jclouds.azure.storage.blob.domain.internal.MutableBlobPropertiesImpl;
import org.jclouds.blobstore.domain.BlobMetadata;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobMetadataToBlobProperties implements Function<BlobMetadata, MutableBlobProperties> {
   public MutableBlobProperties apply(BlobMetadata from) {
      if (from == null)
         return null;
      MutableBlobProperties to = new MutableBlobPropertiesImpl();
      to.setContentType(from.getContentType());
      to.setETag(from.getETag());
      to.setContentMD5(from.getContentMD5());
      to.setName(from.getName());
      to.setLastModified(from.getLastModified());
      if (from.getSize() != null)
         to.setContentLength(from.getSize());
      if (from.getUserMetadata() != null) {
         for (Entry<String, String> entry : from.getUserMetadata().entrySet())
            to.getMetadata().put(entry.getKey().toLowerCase(), entry.getValue());
      }
      return to;
   }

}