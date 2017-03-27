/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.qa.galenium.sampling.images;

import static io.wcm.qa.galenium.reporting.GaleniumReportUtil.getLogger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;
import com.galenframework.specs.Spec;
import com.galenframework.validation.CombinedValidationListener;
import com.galenframework.validation.ImageComparison;
import com.galenframework.validation.PageValidation;
import com.galenframework.validation.ValidationError;
import com.galenframework.validation.ValidationResult;

import io.wcm.qa.galenium.util.GaleniumConfiguration;


/**
 * {@link CombinedValidationListener} to handle storing of sampled image files in ZIP file.
 */
public class ImageComparisonValidationListener extends CombinedValidationListener {

  private static final BufferedImage DUMMY_IMAGE = new BufferedImage(20, 20, BufferedImage.TYPE_3BYTE_BGR);

  // Logger
  private static final Logger log = LoggerFactory.getLogger(ImageComparisonValidationListener.class);

  private static final String REGEX_IMAGE_FILENAME = ".*image file ([^,]*\\.png).*";

  @Override
  public void onSpecError(PageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
    super.onSpecError(pageValidation, objectName, spec, result);
    getLogger().trace("spec error triggered: " + objectName);
    if (GaleniumConfiguration.isSaveSampledImages()) {
      getLogger().trace("saving sample: " + objectName);
      logSpec(spec);
      String text = spec.toText();
      Matcher matcher = getImagePathExtractionRegEx().matcher(text);
      if (matcher.matches()) {
        if (matcher.groupCount() >= 1) {
          String imagePath = matcher.group(1);
          BufferedImage actualImage = getActualImage(result);
          if (actualImage == DUMMY_IMAGE) {
            getLogger().trace("actual image sample could not be retrieved: " + objectName);
            BufferedImage pageElementImage = getPageElementScreenshot(pageValidation, objectName);
            if (pageElementImage != null) {
              getLogger().trace("made secondary image sample: " + objectName);
              actualImage = pageElementImage;
            }
            else {
              getLogger().trace("failed to make secondary image sample: " + objectName);
            }
          }
          getLogger().debug("image: " + imagePath + " (" + actualImage.getWidth() + "x" + actualImage.getHeight() + ")");
          try {
            File imageFile = getImageFile(imagePath);
            ImageIO.write(actualImage, "png", imageFile);
          }
          catch (IOException ex) {
            String msg = "could not write image: " + imagePath;
            log.error(msg, ex);
            getLogger().error(msg, ex);
          }
        }
        else {
          String msg = "could not extract image name from: " + text;
          log.warn(msg);
          getLogger().warn(msg);
        }
      }
    }
    else {
      getLogger().trace("not saving sample. " + objectName);
    }
  }

  private BufferedImage getPageElementScreenshot(PageValidation pageValidation, String objectName) {
    BufferedImage wholePageImage = pageValidation.getPage().getScreenshotImage();
    PageElement element = pageValidation.findPageElement(objectName);
    if (element != null) {
      Rect area = element.getArea();
      BufferedImage elementImage = wholePageImage.getSubimage(area.getLeft(), area.getTop(), area.getWidth(), area.getHeight());
      return elementImage;
    }
    return null;
  }

  protected BufferedImage getActualImage(ValidationResult result) {

    ValidationError error = result.getError();
    if (error != null) {
      ImageComparison imageComparison = error.getImageComparison();
      if (imageComparison != null) {
        BufferedImage actualImage = imageComparison.getOriginalFilteredImage();
        if (actualImage != null) {
          return actualImage;
        }
      }
    }

    return DUMMY_IMAGE;
  }

  protected File getDirectoryToRelativizeImageSavePathTo() {
    return new File(GaleniumConfiguration.getGalenSpecPath());
  }

  protected File getImageFile(String imagePath) throws IOException {
    String imageComparisonDirectory = GaleniumConfiguration.getImageComparisonDirectory();
    String path;
    if (StringUtils.isNotBlank(imageComparisonDirectory)) {
      String canonical1 = getDirectoryToRelativizeImageSavePathTo().getCanonicalPath();
      String canonical2 = new File(imagePath).getCanonicalPath();
      String difference = StringUtils.difference(canonical1, canonical2);
      getLogger().trace("image path construction image dir: " + canonical1);
      getLogger().trace("image path construction image path: " + canonical2);
      getLogger().trace("image path construction difference: " + difference);
      path = imageComparisonDirectory + File.separator + difference;
    }
    else {
      path = imagePath;
    }
    File imageFile = new File(path);
    File parentFile = imageFile.getParentFile();
    if (!parentFile.isDirectory()) {
      getLogger().debug("creating directory: " + parentFile.getPath());
      FileUtils.forceMkdir(parentFile);
    }
    return imageFile;
  }

  protected Pattern getImagePathExtractionRegEx() {
    return Pattern.compile(REGEX_IMAGE_FILENAME);
  }

  protected void logSpec(Spec spec) {
    getLogger().debug("checking failed spec for image file: " + spec.toText() + " (with regex: " + REGEX_IMAGE_FILENAME + ")");
  }

}
