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
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.http.options.HttpRequestOptions;

/**
 * Sample test for the behaviour of our Integration Test jetty server.
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 40, timeUnit = TimeUnit.SECONDS)
public interface IntegrationTestClient {

   boolean exists(String path);

   String synch(String id);

   String download(String id);

   InputStream downloadStream(String id);

   String downloadException(String id, HttpRequestOptions options);

   String synchException(String id, String header);

   String upload(String id, String toPut);

   String post(String id, String toPut);

   String postAsInputStream(String id, String toPut);

   String postWithMd5(String id, String base64MD5, File file);

   String postJson(String id, String toPut);

   String action(String id, String action, Map<String, String> options);

   String downloadFilter(String id, String header);

   String download(String id, String header);

   String downloadAndParse(String id);

   StringBuffer newStringBuffer();
}
