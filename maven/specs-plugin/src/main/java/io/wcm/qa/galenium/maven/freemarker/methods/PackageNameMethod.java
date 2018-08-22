/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2018 wcm.io
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
package io.wcm.qa.galenium.maven.freemarker.methods;

import io.wcm.qa.galenium.maven.freemarker.pojo.SpecPojo;
import io.wcm.qa.galenium.maven.freemarker.util.FormatUtil;

/**
 * Package name for selector paackage.
 */
public class PackageNameMethod extends AbstractTemplateMethod<SpecPojo> {

  @Override
  protected String exec(SpecPojo spec) {
    String packageRoot = System.getProperty("packageRootName", "io.wcm.qa.galenium.selectors");
    return FormatUtil.getSelectorsPackageName(packageRoot, spec);
  }

}
