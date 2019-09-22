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

import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.rules.android.deployinfo.AndroidDeployInfoOuterClass;
import com.google.devtools.build.lib.rules.android.deployinfo.AndroidDeployInfoOuterClass.AndroidDeployInfo;
import com.google.idea.blaze.base.command.buildresult.BlazeArtifact;
import com.google.idea.blaze.base.command.buildresult.BuildResultHelper;
import com.google.idea.blaze.base.command.buildresult.BuildResultHelper.GetArtifactsException;
import com.intellij.openapi.diagnostic.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;
import javax.annotation.Nullable;

/** Reads the deploy info from a build step. */
public class BlazeApkDeployInfoProtoHelper {
  private static final Logger LOG = Logger.getInstance(BlazeApkDeployInfoProtoHelper.class);

  @Nullable
  public static AndroidDeployInfo readDeployInfoProto(
      BuildResultHelper buildResultHelper, Predicate<String> pathFilter)
      throws GetArtifactsException {
    File deployInfoFile =
        Iterables.getOnlyElement(
            BlazeArtifact.getLocalFiles(buildResultHelper.getAllOutputArtifacts(pathFilter)), null);
    if (deployInfoFile == null) {
      return null;
    }
    AndroidDeployInfo deployInfo;
    try (InputStream inputStream = new FileInputStream(deployInfoFile)) {
      deployInfo = AndroidDeployInfoOuterClass.AndroidDeployInfo.parseFrom(inputStream);
    } catch (IOException e) {
      LOG.error(e);
      return null;
    }
    return deployInfo;
  }
}
