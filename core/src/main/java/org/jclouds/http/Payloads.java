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
package org.jclouds.http;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.InputStream;

import org.jclouds.http.payloads.ByteArrayPayload;
import org.jclouds.http.payloads.FilePayload;
import org.jclouds.http.payloads.InputStreamPayload;
import org.jclouds.http.payloads.StringPayload;

/**
 * 
 * @author Adrian Cole
 */
public class Payloads {

   public static Payload newPayload(Object data) {
      checkNotNull(data, "data");
      if (data instanceof Payload) {
         return (Payload) data;
      } else if (data instanceof InputStream) {
         return newInputStreamPayload((InputStream) data);
      } else if (data instanceof byte[]) {
         return newByteArrayPayload((byte[]) data);
      } else if (data instanceof String) {
         return newStringPayload((String) data);
      } else if (data instanceof File) {
         return newFilePayload((File) data);
      } else {
         throw new UnsupportedOperationException("unsupported payload type: " + data.getClass());
      }
   }

   public static InputStreamPayload newInputStreamPayload(InputStream data) {
      return new InputStreamPayload(checkNotNull(data, "data"));
   }

   public static ByteArrayPayload newByteArrayPayload(byte[] data) {
      return new ByteArrayPayload(checkNotNull(data, "data"));
   }

   public static StringPayload newStringPayload(String data) {
      return new StringPayload(checkNotNull(data, "data"));
   }

   public static FilePayload newFilePayload(File data) {
      return new FilePayload(checkNotNull(data, "data"));
   }

}
