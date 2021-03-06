package org.jclouds.aws.s3.predicates.validators;

import static com.google.common.base.CharMatcher.is;

import javax.inject.Inject;

import org.jclouds.predicates.validators.DnsNameValidator;

import com.google.common.base.CharMatcher;
import com.google.inject.Singleton;

/**
 * Validates name for S3 buckets. The complete requirements are listed at:
 * http://docs.amazonwebservices.com/AmazonS3/latest/index.html?BucketRestrictions.html
 * 
 * @see org.jclouds.rest.InputParamValidator
 * @see org.jclouds.predicates.Validator
 * 
 * @author Adrian Cole
 */
@Singleton
public class BucketNameValidator extends DnsNameValidator {

   @Inject
   BucketNameValidator() {
      super(3, 63);
   }

   public void validate(String containerName) {
      super.validate(containerName);
      if (containerName.indexOf("..") != -1)
         throw exception(containerName, "Bucket names cannot contain two, adjacent periods");
      if (containerName.endsWith("-"))
         throw exception(containerName, "Bucket names should not end with a dash");

      if (containerName.indexOf("-.") != -1 || containerName.indexOf(".-") != -1)
         throw exception(
                  containerName,
                  "Bucket names cannot contain dashes next to periods (e.g., \"my-.bucket.com\" and \"my.-bucket\" are invalid)");
   }

   @Override
   protected IllegalArgumentException exception(String containerName, String reason) {
      return new IllegalArgumentException(
               String
                        .format(
                                 "Object '%s' doesn't match S3 bucket virtual host naming convention. "
                                          + "Reason: %s. For more info, please refer to http://docs.amazonwebservices.com/AmazonS3/latest/index.html?BucketRestrictions.html.",
                                 containerName, reason));
   }

   /**
    * Amazon also permits periods in the dns name
    */
   @Override
   protected CharMatcher getAcceptableRange() {
      return super.getAcceptableRange().or(is('.'));
   }
}