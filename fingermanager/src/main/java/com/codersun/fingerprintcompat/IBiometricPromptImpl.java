package com.codersun.fingerprintcompat;

import android.os.CancellationSignal;
import android.support.annotation.NonNull;

public interface IBiometricPromptImpl {
    void authenticate(@NonNull CancellationSignal cancel);
}
