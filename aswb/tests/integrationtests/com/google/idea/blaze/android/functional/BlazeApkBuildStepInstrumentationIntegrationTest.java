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
package com.google.idea.blaze.android.functional;

import static com.google.common.truth.Truth.assertThat;
import static com.google.idea.blaze.android.targetmapbuilder.NbAndroidInstrumentationTestTarget.android_instrumentation_test;
import static com.google.idea.blaze.android.targetmapbuilder.NbAndroidTarget.android_binary;

import com.google.common.collect.ImmutableList;
import com.google.idea.blaze.android.BlazeAndroidIntegrationTestCase;
import com.google.idea.blaze.android.run.runner.BlazeApkBuildStepInstrumentation;
import com.google.idea.blaze.android.run.runner.BlazeApkBuildStepInstrumentation.InstrumentorToTarget;
import com.google.idea.blaze.base.model.primitives.Label;
import com.google.idea.blaze.base.model.primitives.WorkspacePath;
import com.google.idea.blaze.base.scope.BlazeContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Integration tests for {@link
 * com.google.idea.blaze.android.run.runner.BlazeApkBuildStepInstrumentation}
 */
@RunWith(JUnit4.class)
public class BlazeApkBuildStepInstrumentationIntegrationTest
    extends BlazeAndroidIntegrationTestCase {

  @Before
  public void setup() {
    setProjectView(
        "directories:",
        "  java/com/foo/app",
        "targets:",
        "  //java/com/foo/app:instrumentation_test",
        "android_sdk_platform: android-27");
    mockSdk("android-27", "Android 27 SDK");

    workspace.createFile(
        new WorkspacePath("java/com/foo/app/MainActivity.java"),
        "package com.foo.app",
        "import android.app.Activity;",
        "public class MainActivity extends Activity {}");

    workspace.createFile(
        new WorkspacePath("java/com/foo/app/Test.java"),
        "package com.foo.app",
        "public class Test {}");

    setTargetMap(
        android_binary("//java/com/foo/app:app").src("MainActivity.java"),
        android_binary("//java/com/foo/app:test_app")
            .src("Test.java")
            .instruments("//java/com/foo/app:app"),
        android_instrumentation_test("//java/com/foo/app:instrumentation_test")
            .test_app("//java/com/foo/app:test_app"));
    runFullBlazeSync();
  }

  @Test
  public void findInstrumentorAndTestTargets() {
    BlazeApkBuildStepInstrumentation buildStep =
        new BlazeApkBuildStepInstrumentation(
            getProject(),
            Label.create("//java/com/foo/app:instrumentation_test"),
            ImmutableList.of());

    InstrumentorToTarget pair = buildStep.getInstrumentorToTargetPair(new BlazeContext());
    assertThat(pair.instrumentor).isEqualTo(Label.create("//java/com/foo/app:test_app"));
    assertThat(pair.target).isEqualTo(Label.create("//java/com/foo/app:app"));
  }
}
