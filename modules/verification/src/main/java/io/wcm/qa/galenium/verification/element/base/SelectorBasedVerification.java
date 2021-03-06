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
package io.wcm.qa.galenium.verification.element.base;

import io.wcm.qa.galenium.sampling.element.base.SelectorBasedSampler;
import io.wcm.qa.galenium.verification.base.SamplerBasedVerification;

/**
 * Abstract base class for implementations verifying samples from web elements.
 * @param <S> type of sampler
 * @param <T> type of sample
 */
public abstract class SelectorBasedVerification<S extends SelectorBasedSampler<T>, T> extends SamplerBasedVerification<S, T> {

  protected SelectorBasedVerification(String verificationName, S sampler) {
    super(verificationName, sampler);
  }

}
