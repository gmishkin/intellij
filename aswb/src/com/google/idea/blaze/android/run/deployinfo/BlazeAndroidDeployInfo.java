/*
 * Copyright 2016 The Bazel Authors. All rights reserved.
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
package com.google.idea.blaze.android.run.deployinfo;

import com.google.common.collect.ImmutableList;
import com.google.idea.blaze.android.manifest.ManifestParser;
import com.google.idea.blaze.android.manifest.ParsedManifestService;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;

/** Info about the deployment phase. */
public class BlazeAndroidDeployInfo {
  private static final Logger log = Logger.getInstance(BlazeAndroidDeployInfo.class);
  private final Project project;
  private final File mergedManifestFile;
  @Nullable private final File testTargetMergedManifestFile;
  private final List<File> apksToDeploy;

  public BlazeAndroidDeployInfo(
      Project project,
      File mergedManifestFile,
      @Nullable File testTargetMergedManifestFile,
      List<File> apksToDeploy) {
    this.project = project;
    this.mergedManifestFile = mergedManifestFile;
    this.testTargetMergedManifestFile = testTargetMergedManifestFile;
    this.apksToDeploy = apksToDeploy;
  }

  /**
   * Returns parsed manifest of the main target for this deployment. During normal app deployment,
   * the main target is the android_binary that builds the app itself. During instrumentation tests
   * the main target is the android_binary/android_test target responsible for instrumenting the
   * app, while the merged manifest of the app under test can be obtained through {@link
   * BlazeAndroidDeployInfo#getTestTargetMergedManifest()}.
   */
  public ManifestParser.ParsedManifest getMergedManifest() {
    try {
      return ParsedManifestService.getInstance(project).getParsedManifest(mergedManifestFile);
    } catch (IOException e) {
      log.warn(
          "Could not read main merged manifest file "
              + mergedManifestFile
              + " due to error: "
              + e.getMessage());
      return null;
    }
  }

  /**
   * Returns parsed manifest of the app under test during an instrumentation test. This method
   * returns null in all other scenarios.
   */
  public ManifestParser.ParsedManifest getTestTargetMergedManifest() {
    if (testTargetMergedManifestFile == null) {
      return null;
    }
    try {
      return ParsedManifestService.getInstance(project)
          .getParsedManifest(testTargetMergedManifestFile);
    } catch (IOException e) {
      log.warn(
          "Could not read test target's merged manifest file: " + testTargetMergedManifestFile);
      return null;
    }
  }

  /** Returns all manifests files involved in this deployment. */
  ImmutableList<File> getManifestFiles() {
    if (testTargetMergedManifestFile == null) {
      return ImmutableList.of(mergedManifestFile);
    }
    return ImmutableList.of(mergedManifestFile, testTargetMergedManifestFile);
  }

  /** Returns the full list of apks to deploy, if any. */
  List<File> getApksToDeploy() {
    return apksToDeploy;
  }
}
