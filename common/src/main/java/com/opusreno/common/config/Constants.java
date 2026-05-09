package com.opusreno.common.config;

public final class Constants {

    private Constants() {}

    // DynamoDB index names
    public static final String GSI1_INDEX_NAME = "GSI1";
    public static final String GSI2_INDEX_NAME = "GSI2";

    // Rate limiting
    public static final int DEDUP_MAX_LEADS_PER_PHONE = 3;
    public static final int DEDUP_WINDOW_HOURS = 24;

    // Anti-spam
    public static final int MIN_FORM_SUBMISSION_SECONDS = 3;
}
