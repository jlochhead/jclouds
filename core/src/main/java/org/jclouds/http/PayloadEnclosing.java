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

import java.io.File;
import java.io.InputStream;

/**
 * 
 * @author Adrian Cole
 */
public interface PayloadEnclosing {

   /**
    * Sets payload for the request or the content from the response. If size isn't set, this will
    * attempt to discover it.
    * 
    * @param data
    *           typically InputStream for downloads, or File, byte [], String, or InputStream for
    *           uploads.
    */
   void setPayload(Payload data);

   void setPayload(File data);

   void setPayload(byte[] data);

   void setPayload(InputStream data);

   void setPayload(String data);

   Payload getPayload();

   /**
    * @return InputStream, if downloading, or whatever was set during {@link #setPayload(Payload)}
    */
   InputStream getContent();

   void setContentLength(long contentLength);

   /**
    * Returns the total size of the downloaded object, or the chunk that's available.
    * <p/>
    * Chunking is only used when org.jclouds.http.GetOptions is called with options like tail,
    * range, or startAt.
    * 
    * @return the length in bytes that can be be obtained from {@link #getContent()}
    * @see javax.ws.rs.core.HttpHeaders#CONTENT_LENGTH
    * @see org.jclouds.http.options.GetOptions
    */
   Long getContentLength();

   /**
    * generate an MD5 Hash for the current data.
    * <p/>
    * <h2>Note</h2>
    * <p/>
    * If this is an InputStream, it will be converted to a byte array first.
    */
   void generateMD5();

}