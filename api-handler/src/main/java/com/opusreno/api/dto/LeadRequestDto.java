package com.opusreno.api.dto;

import com.opusreno.common.domain.LeadBudget;
import com.opusreno.common.domain.LeadScope;
import com.opusreno.common.domain.LeadSource;
import com.opusreno.common.domain.LeadTimeline;
import com.opusreno.common.domain.LeadType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class LeadRequestDto {

    @NotBlank @Size(min = 2, max = 100)
    public String name;

    @NotBlank @Pattern(regexp = "^\\+91[0-9]{10}$", message = "must match +91XXXXXXXXXX")
    public String phone;

    @Email
    public String email;

    @NotNull
    public LeadSource source;

    @NotNull
    public LeadType type;

    @NotNull
    public LeadScope scope;

    public LeadBudget budget;

    public LeadTimeline timeline;

    @Size(max = 100)
    public String area;

    @Size(max = 500)
    public String message;

    @NotBlank
    public String sessionId;

    // Anti-spam fields
    public String website;

    @NotNull
    public Long formOpenedAt;
}
