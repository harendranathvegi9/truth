/*
 * Copyright (c) 2011 Google, Inc.
 *
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
 */
package com.google.common.truth;

import java.util.Arrays;

public abstract class FailureStrategy {
  public void fail(String message) {
    fail(message, null);
  }

  public void fail(String message, Throwable cause) {
    AssertionError up = new AssertionError(message);
    if (cause == null) {
      cause = new AssertionError(message);
    }
    try {
      up.initCause(cause);
    } catch (IllegalStateException alreadyInitializedBecauseOfHarmonyBug) {
      // https://code.google.com/p/android/issues/detail?id=29378
      // No message, but it's the best we can do without awful hacks.
      throw new AssertionError(cause);
    }
    stripTruthStackFrames(up);
    throw up;
  }

  public void failComparing(String message, CharSequence expected, CharSequence actual) {
    fail(StringUtil.messageFor(message, expected, actual));
  }

  /**
   * Strips stack frames from the throwable that have a class starting with com.google.common.truth.
   */
  private static void stripTruthStackFrames(Throwable throwable) {
    StackTraceElement[] stackTrace = throwable.getStackTrace();

    int i = 0;
    while (i < stackTrace.length
        && stackTrace[i].getClassName().startsWith("com.google.common.truth")) {
      i++;
    }
    throwable.setStackTrace(Arrays.copyOfRange(stackTrace, i, stackTrace.length));
  }
}
