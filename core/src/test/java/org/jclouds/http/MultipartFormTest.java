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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.MultipartForm.Part;
import org.jclouds.http.MultipartForm.Part.PartOptions;
import org.jclouds.http.payloads.FilePayload;
import org.jclouds.http.payloads.StringPayload;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

/**
 * Tests parsing of a request
 * 
 * @author Adrian Cole
 */
@Test(testName = "http.MultipartFormTest")
public class MultipartFormTest {
   String boundary = "------------------------------c88555ffd14e";

   public void testSinglePart() throws IOException {

      StringBuilder builder = new StringBuilder();
      addData(boundary, "hello", builder);
      builder.append("--").append(boundary).append("--").append("\r\n");
      String expects = builder.toString();
      assertEquals(expects.length(), 199);

      MultipartForm multipartForm = new MultipartForm(boundary, newPart("hello"));

      assertEquals(Utils.toStringAndClose(multipartForm.getInput()), expects);
      assertEquals(multipartForm.calculateSize(), new Long(199));
   }

   public static class MockFilePayload extends FilePayload {

      private final StringPayload realPayload;

      public MockFilePayload(String content) {
         super(createMockFile(content));
         this.realPayload = Payloads.newStringPayload(content);
      }

      private static File createMockFile(String content) {
         File file = createMock(File.class);
         expect(file.exists()).andReturn(true);
         expect(file.getName()).andReturn("testfile.txt");
         replay(file);
         return file;
      }

      @Override
      public Long calculateSize() {
         return realPayload.calculateSize();
      }

      @Override
      public InputStream getInput() {
         return realPayload.getInput();
      }

      @Override
      public boolean isRepeatable() {
         return realPayload.isRepeatable();
      }

      @Override
      public void writeTo(OutputStream outstream) throws IOException {
         realPayload.writeTo(outstream);
      }

   }

   private Part newPart(String data) {
      return Part.create("file", new MockFilePayload(data), new PartOptions()
               .contentType(MediaType.TEXT_PLAIN));
   }

   private void addData(String boundary, String data, StringBuilder builder) {
      builder.append("--").append(boundary).append("\r\n");
      builder.append("Content-Disposition").append(": ").append(
               "form-data; name=\"file\"; filename=\"testfile.txt\"").append("\r\n");
      builder.append("Content-Type").append(": ").append("text/plain").append("\r\n");
      builder.append("\r\n");
      builder.append(data).append("\r\n");
   }

   public void testMultipleParts() throws IOException {

      StringBuilder builder = new StringBuilder();
      addData(boundary, "hello", builder);
      addData(boundary, "goodbye", builder);

      builder.append("--").append(boundary).append("--").append("\r\n");
      String expects = builder.toString();

      assertEquals(expects.length(), 352);

      MultipartForm multipartForm = new MultipartForm(boundary, newPart("hello"),
               newPart("goodbye"));

      assertEquals(Utils.toStringAndClose(multipartForm.getInput()), expects);

      // test repeatable
      assert multipartForm.isRepeatable();
      assertEquals(Utils.toStringAndClose(multipartForm.getInput()), expects);
      assertEquals(multipartForm.calculateSize(), new Long(352));
   }

}
