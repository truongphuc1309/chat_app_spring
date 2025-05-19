package com.truongphuc.util.impl;

import com.truongphuc.util.TimeUtil;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TimeUtilImpl implements TimeUtil {
    @Override
    public long parseDurationToMillis(String duration) throws IllegalArgumentException {
        Pattern pattern = Pattern.compile("^(\\d+)([smhdy]|mo)$");
        Matcher matcher = pattern.matcher(duration.trim());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid duration format. Expected format: <number><unit> (e.g., '1d', '2s', '3m', '1h', '1y', '2mo')");
        }

        long value = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);

        return switch (unit) {
            case "s" ->
                    value * 1000;
            case "m" ->
                    value * 60 * 1000;
            case "h" ->
                    value * 60 * 60 * 1000;
            case "d" ->
                    value * 24 * 60 * 60 * 1000;
            case "y" ->
                    value * 365 * 24 * 60 * 60 * 1000;
            case "mo" ->
                    value * 30 * 24 * 60 * 60 * 1000;
            default -> throw new IllegalArgumentException("Unsupported time unit: " + unit);
        };
    }
}
