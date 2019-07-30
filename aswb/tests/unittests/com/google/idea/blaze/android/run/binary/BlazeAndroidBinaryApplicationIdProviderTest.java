/*
 * Copyright 2019 The Bazel Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.idea.blaze.android.run.binary;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import com.android.tools.idea.run.ApkProvisionException;
import com.google.common.collect.ImmutableList;
import com.google.idea.blaze.android.manifest.ManifestParser.ParsedManifest;
import com.google.idea.blaze.android.run.deployinfo.BlazeAndroidDeployInfo;
import com.google.idea.blaze.android.run.runner.BlazeApkBuildStep;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

/**
 * Unit tests for {@link
 * com.google.idea.blaze.android.run.binary.BlazeAndroidBinaryApplicationIdProvider}
 */
@RunWith(JUnit4.class)
public class BlazeAndroidBinaryApplicationIdProviderTest {
  private BlazeApkBuildStep mockBuildStep;
  private BlazeAndroidDeployInfo mockDeployInfo;

  @Before
  public void initMocks() throws ApkProvisionException {
    mockBuildStep = Mockito.mock(BlazeApkBuildStep.class);
    mockDeployInfo = Mockito.mock(BlazeAndroidDeployInfo.class);
    when(mockBuildStep.getDeployInfo()).thenReturn(mockDeployInfo);
  }

  @Test
  public void getApplicationId() throws Exception {
    ParsedManifest manifest = new ParsedManifest("package.name", ImmutableList.of(), null);
    when(mockDeployInfo.getMergedManifest()).thenReturn(manifest);

    BlazeAndroidBinaryApplicationIdProvider provider =
        new BlazeAndroidBinaryApplicationIdProvider(mockBuildStep);
    assertThat(provider.getPackageName()).isEqualTo("package.name");
  }

  @Test
  public void getApplicationId_noPackageNameInMergedManifest() throws Exception {
    ParsedManifest manifest = new ParsedManifest(null, ImmutableList.of(), null);
    when(mockDeployInfo.getMergedManifest()).thenReturn(manifest);

    try {
      BlazeAndroidBinaryApplicationIdProvider provider =
          new BlazeAndroidBinaryApplicationIdProvider(mockBuildStep);
      provider.getPackageName();
    } catch (ApkProvisionException ex) {
      return;
    }

    // An exception should be thrown if the package name is not available because it's a
    // serious error and should not fail silently.
    fail();
  }

  @Test
  public void getApplicationId_noMergedManifest() throws Exception {
    when(mockDeployInfo.getMergedManifest()).thenReturn(null);

    try {
      BlazeAndroidBinaryApplicationIdProvider provider =
          new BlazeAndroidBinaryApplicationIdProvider(mockBuildStep);
      provider.getPackageName();
    } catch (ApkProvisionException ex) {
      return;
    }

    // An exception should be thrown if the merged manifest not available because it's a
    // serious error and should not fail silently.
    fail();
  }
}
