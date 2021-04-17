package com.example.essentialstracker.sample.util;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.essentialstracker.exceptions.BleScanException;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Helper class to show BleScanException error messages as toasts.
 */
public class ScanExceptionHandler {

    private ScanExceptionHandler() {
        // Utility class
    }

    /**
     * Mapping of exception reasons to error string resource ids.
     */
    @SuppressLint("UseSparseArrays")
    private static final Map<Integer, Integer> ERROR_MESSAGES = new HashMap<>();

    /*
      Add new mappings here.
     */
    static {
        ERROR_MESSAGES.put(BleScanException.BLUETOOTH_NOT_AVAILABLE, 0);
        ERROR_MESSAGES.put(BleScanException.BLUETOOTH_DISABLED, 0);
        ERROR_MESSAGES.put(BleScanException.LOCATION_PERMISSION_MISSING, 0);
        ERROR_MESSAGES.put(BleScanException.LOCATION_SERVICES_DISABLED, 0);
        ERROR_MESSAGES.put(BleScanException.SCAN_FAILED_ALREADY_STARTED, 0);
        ERROR_MESSAGES.put(
                BleScanException.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED,
                0
        );
        ERROR_MESSAGES.put(
                BleScanException.SCAN_FAILED_FEATURE_UNSUPPORTED,
                0
        );
        ERROR_MESSAGES.put(BleScanException.SCAN_FAILED_INTERNAL_ERROR, 0);
        ERROR_MESSAGES.put(
                BleScanException.SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES,
                0
        );
        ERROR_MESSAGES.put(BleScanException.BLUETOOTH_CANNOT_START, 0);
        ERROR_MESSAGES.put(BleScanException.UNKNOWN_ERROR_CODE, 0);
    }

    /**
     * Show toast with error message appropriate to exception reason.
     *
     * @param context   current Activity context
     * @param exception BleScanException to show error message for
     */
    public static void handleException(final Activity context, final BleScanException exception) {
        final String text;
        final int reason = exception.getReason();

        // Special case, as there might or might not be a retry date suggestion
        if (reason == BleScanException.UNDOCUMENTED_SCAN_THROTTLE) {
            text = getUndocumentedScanThrottleErrorMessage(context, exception.getRetryDateSuggestion());
        } else {
            // Handle all other possible errors
            final Integer resId = ERROR_MESSAGES.get(reason);
            if (resId != null) {
                text = context.getString(resId);
            } else {
                // unknown error - return default message
                Log.w("Scanning", String.format("No message found for reason=%d. Consider adding one.", reason));
                text = "Unkown error";
            }
        }

        Log.w("Scanning", text, exception);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private static String getUndocumentedScanThrottleErrorMessage(final Activity context, final Date retryDate) {
        final StringBuilder stringBuilder =
                new StringBuilder("Android 7+ does not allow more scans");

        if (retryDate != null) {
            final String retryText = String.format(
                    Locale.getDefault(),
                   "Try in %d seconds",
                    secondsTill(retryDate)
            );
            stringBuilder.append(retryText);
        }

        return stringBuilder.toString();
    }

    private static long secondsTill(@NonNull final Date retryDateSuggestion) {
        return TimeUnit.MILLISECONDS.toSeconds(retryDateSuggestion.getTime() - System.currentTimeMillis());
    }
}
